/* GenerateFromExistingResultsMain.java
 * 
 * For different t, we can generate t-wise coverage from sampled results.
 * 
 * Author:  Yi Xiang <xiangyi@scut.edu.cn> or <gzhuxiang_yi@163.com>
 *  
 * Reference: 
 *  
 * Yi Xiang, Han Huang, Miqing Li, Sizhe Li, and Xiaowei Yang, 
 * Looking For Novelty in Search-based Software Product Line Testing, TSE, 2021
 * 
 * Data: 25/01/2021
 * Copyright (c) 2021 Yi Xiang
 * 
 * Note: This is a free software developed based on the open 
 * source projects PLEDGE <https://github.com/christopherhenard/pledge> 
 * and jMetal<http://jmetal.sourceforge.net>. The copy right of PLEDGE 
 * belongs to  its original author, Christopher Henard, and the copy 
 * right of jMetal belongs to Antonio J. Nebro and Juan J. Durillo. 
 * Nevertheless, this current version can be redistributed and/or 
 * modified under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU  General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package spl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jmetal.qualityIndicator.util.MetricsUtil;
import spl.fm.Product;
import spl.fm.TSet;
import spl.techniques.DistancesUtil;
import spl.techniques.SimilarityTechnique;
import spl.utils.FileUtils;


public class GeneratePerformanceMetrics {

	public int exisitingT = 2;
	public int min_products = 1;
	public int max_products = 100;
	public int nbProds = 100;
	public int t = 6;
	public long timeAllowed; 
	public long evaluations; 
 
	public String outputDir;
	public int runs;
	public String algName;
	private Set<TSet> validTSets;
	 
	public GeneratePerformanceMetrics() {
		
	}	

	public void loadTSets(String fmFile) throws Exception {
		 /**
         * 读取T-set
         */        
        String validTsetFile = "./all_FM/"  + fmFile + ".valid" + t + "-Set_" + nbProds + "products";    	    
    
        if (validTsetFile != null && (new File(validTsetFile).exists())) {       	

        	validTSets = MAP_test.getInstance().loadValidTSet(validTsetFile);    
//        	System.out.println("Number of validTSets " + validTSets.size());
        	
        }

	}	
	
	public void computeTwiseCoverage(String fmFile) throws Exception{
		 String fmFileName = new File(fmFile).getName();
        
         double avgCoverage = 0.0;
                
//         String path = outputDir + algName  + "/" + fmFileName +"/Samples/Min" + min_products +"Max"+ max_products +"prods/";
                 
         String path = outputDir + algName  + "/" + fmFileName +"/";
         
         //FileUtils.CheckDir(path);          
         String loadProductsPath = outputDir + algName  + "/"  + fmFileName +"/";            
  	                  
         List<Set<TSet> > allValidTSets = new ArrayList< Set<TSet>>();
         
         int times = 10;
         
         if (t>3) {
        	 times = 10;
         }
         
         if (MAP_test.getInstance().validTSets == null) {       
        	 
        	String validTsetFolder= fmFile + ".valid" + t + "-Sets/"  ;  
        	 
    	    for (int j = 0; j < times; j++) { 
    	    	allValidTSets.add( MAP_test.getInstance().loadValidTSet(validTsetFolder+"tSet."+j));  
      	  	}
    	    
         }
         
         for (int i = 0; i < runs; i++) {
             System.out.println("ComputeTwiseCoverage, run " + (i + 1));
             
         	 MetricsUtil utils = new MetricsUtil();                             	;
             double[][] data =  utils.readFront(loadProductsPath + "Minnproducts." + i);
             min_products = (int)data[0][0];
             data =  utils.readFront(loadProductsPath + "Maxnproducts." + i);
             max_products = (int)data[0][0];
             
             double [] coverageList = new double[max_products - min_products+1];
         	 double sumCoverage = 0.0;         	
         	 double bestCoverage = -1.0;
         	          	 
         	 for (int n=0;n < max_products - min_products+1;n++) { // for each TS in a MAP
         		 
         		 List<Product> products = null;    
                 double sumCoverageValues = 0.0;   
                 // Load products                            
                 products = MAP_test.getInstance().loadProductsFromFile(loadProductsPath + "TS." + i + "_" + n);
     	                      
                // Compute coverage
                 if (MAP_test.getInstance().validTSets != null) {
                	 
                	 MAP_test.getInstance().computeProductsPartialCoverage(products, MAP_test.getInstance().validTSets);      
                     for (int j = 0; j < products.size(); j++) {
                         double cov = products.get(j).getCoverage();
                         sumCoverageValues += cov; //累积覆盖率                
                     }  
                     
                     sumCoverageValues = sumCoverageValues/ MAP_test.getInstance().validTSets.size() * 100.0;
                
                 } else {            	 
                	             	             	  
                      for (int j = 0; j < times; j++) { 
                    	  System.out.println("Compute with the j = " + j + "validset: NO=" +  allValidTSets.get(j).size());
                    	  
                    	  MAP_test.getInstance().computeProductsPartialCoverage(products, allValidTSets.get(j));      
                          
                    	  for (int k = 0; k < products.size(); k ++) {
                              double cov = products.get(k).getCoverage();
                              sumCoverageValues += cov; //累积覆盖率                
                          } 
                    	  
                	  }
                      
                      sumCoverageValues = sumCoverageValues/times;
                      sumCoverageValues = sumCoverageValues/allValidTSets.get(0).size() * 100.0;
                      
                 }// if
         		
                 coverageList[n] = sumCoverageValues;  
                 
                 if (sumCoverageValues > bestCoverage)
                	 bestCoverage = sumCoverageValues;
                 
                 sumCoverage = sumCoverage + coverageList[n];
         
         	 }// for n
             
           	MAP_test.getInstance().writeDataToFile(path + t + "wiseCoverageAll." + i, coverageList);// write all coverage
        	MAP_test.getInstance().writeDataToFile(path + t + "wiseCoverage." + i, bestCoverage);// write best coverage
           	MAP_test.getInstance().writeDataToFile(path + "QDscore" + t + "wiseCoverage." + i, sumCoverage);// write QDscore
         } // for runs   
         
	}
	
	/**
	 * 
	 * @param fmFile
	 * @throws Exception
	 */
	public void computeQDDominanceFitness(String fmFile) throws Exception{
		 String fmFileName = new File(fmFile).getName();
       
        double avgCoverage = 0.0;
               
//        String path = outputDir + algName  + "/" + fmFileName +"/Samples/Min" + min_products +"Max"+ max_products +"prods/";
                
        String path = outputDir + algName  + "/" + fmFileName +"/";
        
        //FileUtils.CheckDir(path);          
        String loadProductsPath = outputDir + algName  + "/"  + fmFileName +"/";            

        for (int i = 0; i < runs; i++) {
            System.out.println("computeQDDominanceFitness, run " + (i + 1));
            
        	MetricsUtil utils = new MetricsUtil();                             	;
            double[][] data =  utils.readFront(loadProductsPath + "Minnproducts." + i);
            min_products = (int)data[0][0];
            data =  utils.readFront(loadProductsPath + "Maxnproducts." + i);
            max_products = (int)data[0][0];
            
            System.out.println("min_products = " + min_products + ";max_products = " + max_products);
            int number = max_products - min_products + 1;
            
            // Read fitness vectors from the file 
            double[][] fitness =  utils.readFront(loadProductsPath + "Fitness." + i);
            
            double [][] fun = new double[number][2];
            
            for (int j = 0; j < number;j++) {
            	fun[j][0] = min_products + j;
            	fun[j][1] = fitness [j][0];
            }
           
            // Print fun
            for (int k = 0; k < fun.length;k++)
            	System.out.println("the 0-th " + fun[k][0] + ";the 1-th " + fun[k][1]);

            //Compute score considering dominance
            double sumCoverage = fitness [0][0];
            int failedNo = 0;
                       
            for (int k = 1; k < fitness.length;k++) {
            	
            	if (fitness [k][0] <= fitness [k-1][0] && fitness [k][0] != 100.0) {
        			failedNo++;        			
//        			fitness [k][0] = fitness [k-1][0];//Reset
            	}
            		
        		sumCoverage += fitness [k][0];
    
            }                                    
                     
            System.out.println("QDscoreDominanceFitness = " + sumCoverage + "; Failed no = " + failedNo);
        	MAP_test.getInstance().writeDataToFile(path + "QDscoreDominanceFitness." + i, sumCoverage);// write QDscore
        	MAP_test.getInstance().writeDataToFile(path + "FailNo." + i, failedNo);// write QDscore
        } // for runs   
        
	}
	
	
	public void computeImprovementRate(String fmFile) throws Exception{
		String fmFileName = new File(fmFile).getName();
              
        String path = outputDir + algName  + "/" + fmFileName +"/";
        
        //FileUtils.CheckDir(path);          
        String loadProductsPath = outputDir + algName  + "/"  + fmFileName +"/";            

        int count = 0;
        
        for (int i = 0; i < runs; i++) {
            
        	MetricsUtil utils = new MetricsUtil();                             	;
            double[][] data =  utils.readFront(loadProductsPath + "Maxnproducts." + i);
            int max_products = (int)data[0][0];
            
            data =  utils.readFront(loadProductsPath + "First100." + i);
            
            int First100 = (int)data[0][0];

            if (First100 < max_products)
            	count++;

        } // for runs   
        
        System.out.println("Improvement rate is " + count/(double)runs * 100);
      	MAP_test.getInstance().writeDataToFile(path + "ImprovementRate", count/(double)runs * 100);// write QDscore

	}
	
	
	public void computeDiversity4NonMAPMethods(String fmFile) throws Exception{
		String fmFileName = new File(fmFile).getName();
              
        String path = outputDir + algName  + "/" + fmFileName +"/";
        
        //FileUtils.CheckDir(path);          
        String loadProductsPath = outputDir + algName  + "/"  + fmFileName +"/";            

        List<Product> products = null;  
        
        for (int r = 0; r < runs; r++) {
        	 
        	System.out.println("ComputeDiversity, run " + (r + 1));
        	 
        	products = MAP_test.getInstance().loadProductsFromFile(loadProductsPath + "TS." + r);
        	
		  	double [][] distancesMatrix = new double[products.size()][products.size()];
        	// Computation of the distances
            for (int i = 0; i < products.size(); i++) {
            	distancesMatrix[i][i] = 0.0;
            	
                for (int j = i + 1; j < products.size(); j++) {   

                	double dist = DistancesUtil.getAntiDiceDistance(products.get(i), products.get(j));             	
                    distancesMatrix[i][j] = dist;       
                    distancesMatrix[j][i] = dist;  
                } // for j
            } // for i
                                
            int k = Math.max(products.size()/2,2);   

        	int size = products.size();
        	double noveltyScoreSum = 0.0;            	
        	
        	double [] dist = new double [size]; 
        	int []    idx =  new int [size]; 
            			
        	for (int i = 0; i < size;i++) {
        		
        		for (int j = 0; j < size; j++) {
        			dist[j] = distancesMatrix[i][j];
        			idx[j] = j;
        		}
        		
        		/* Find the k-nearest neighbors*/
        		DistancesUtil.minFastSort(dist, idx, size, k);
        		
        		double noveltyScore  = 0.0;    		
        		for (int q = 0; q < k; q++) {
        			noveltyScore = noveltyScore + dist[q];
        		}
        		
        		noveltyScore = noveltyScore/k; // the average value
        		noveltyScoreSum = noveltyScoreSum + noveltyScore;           	
          
        	 }   
            	
            MAP_test.getInstance().writeDataToFile(path + "Diversity." + r, noveltyScoreSum);// write all coverage
          
        } // for runs   

	}
	
	public void computeDiversity(String fmFile) throws Exception{
		String fmFileName = new File(fmFile).getName();
              
        String path = outputDir + algName  + "/" + fmFileName +"/";
        
        //FileUtils.CheckDir(path);          
        String loadProductsPath = outputDir + algName  + "/"  + fmFileName +"/";            

        List<Product> products = null;    
        
        
        for (int r = 0; r < runs; r++) {
        	 
        	System.out.println("ComputeDiversity, run " + (r + 1));
            
        	 MetricsUtil utils = new MetricsUtil();                             	;
             double[][] data =  utils.readFront(loadProductsPath + "Minnproducts." + r);
             min_products = (int)data[0][0];
             data =  utils.readFront(loadProductsPath + "Maxnproducts." + r);
             max_products = (int)data[0][0];
            
             double [] diversityList = new double[max_products - min_products+1];
        	 double sumDiversity = 0.0;         	
        	 double maxDiversity = -1.0;
        	 
        	 for (int n=0;n<max_products - min_products+1;n++) { // for each TS in a MAP
        		 
        		products = MAP_test.getInstance().loadProductsFromFile(loadProductsPath + "TS." + r + "_" + n);
    		  	double [][] distancesMatrix = new double[products.size()][products.size()];
            	// Computation of the distances
                for (int i = 0; i < products.size(); i++) {
                	distancesMatrix[i][i] = 0.0;
                	
                    for (int j = i + 1; j < products.size(); j++) {   

                    	double dist = DistancesUtil.getAntiDiceDistance(products.get(i), products.get(j));             	
                        distancesMatrix[i][j] = dist;       
                        distancesMatrix[j][i] = dist;  
                    } // for j
                } // for i
                                
                int k = Math.max(products.size()/2,2);   

            	int size = products.size();
            	double noveltyScoreSum = 0.0;            	
            	
            	double [] dist = new double [size]; 
            	int []    idx =  new int [size]; 
            			
            	for (int i = 0; i < size;i++) {
            		
            		for (int j = 0; j < size; j++) {
            			dist[j] = distancesMatrix[i][j];
            			idx[j] = j;
            		}
            		
            		/* Find the k-nearest neighbors*/
            		DistancesUtil.minFastSort(dist, idx, size, k);
            		
            		double noveltyScore  = 0.0;    		
            		for (int q = 0; q < k; q++) {
            			noveltyScore = noveltyScore + dist[q];
            		}
            		
            		noveltyScore = noveltyScore/k; // the average value
            		noveltyScoreSum = noveltyScoreSum + noveltyScore;
            		
            	} // for i
                
            	diversityList[n] = noveltyScoreSum;
            	sumDiversity = sumDiversity + noveltyScoreSum;    
            	
            	if (diversityList[n] > maxDiversity)
            		maxDiversity = diversityList[n];
        	 }        	 
        	       
        	maxDiversity =diversityList[(max_products - min_products)/2] ;// the middle position        	
            MAP_test.getInstance().writeDataToFile(path + "DiversityAll." + r, diversityList);// write all coverage
            MAP_test.getInstance().writeDataToFile(path + "Diversity." + r, maxDiversity);// write all coverage
            MAP_test.getInstance().writeDataToFile(path + "QDscore" + "Diversity." + r, sumDiversity);// write QDscore
        } // for runs   

	}
	
	public void computeTwiseCoverage4NonMAPMethods(String fmFile) throws Exception{
		 String fmFileName = new File(fmFile).getName();
       
        double avgCoverage = 0.0;
               
        String path = outputDir + algName  + "/" + fmFileName +"/";
                        
        String loadProductsPath = path;            
 	          
//        String writeResultPath = outputDir + algName  + "/"  + fmFileName +"/Samples/Min0" +"Max0" +"prods/";            
        String writeResultPath = outputDir + algName  + "/"  + fmFileName +"/";            
        FileUtils.CheckDir(writeResultPath);          
        
        List<Set<TSet> > allValidTSets = new ArrayList< Set<TSet>>();
        
        int times = 10;
        
//        if (t>3) {
//       	 times = 1;
//        }
        
        if (MAP_test.getInstance().validTSets == null) {       
       	 
	       	String validTsetFolder= fmFile + ".valid" + t + "-Sets/"  ;  
	       	 
	   	    for (int j = 0; j < times; j++) { 
	   	    	allValidTSets.add( MAP_test.getInstance().loadValidTSet(validTsetFolder+"tSet."+j));  
	     	}
	   	    
	     }
        
        for (int i = 0; i < runs; i++) {
            System.out.println("ComputeTwiseCoverage, run " + (i + 1));
        	          
        	 double sumCoverage = 0.0;
        		 
        	 List<Product> products = null;    
             double sumCoverageValues = 0.0;   
                // Load products                            
             products = MAP_test.getInstance().loadProductsFromFile(loadProductsPath + "TS." + i,";");
    	                      
               // Compute coverage
                if (MAP_test.getInstance().validTSets != null) {
               	 
               	 MAP_test.getInstance().computeProductsPartialCoverage(products, MAP_test.getInstance().validTSets);      
                    for (int j = 0; j < products.size(); j++) {
                        double cov = products.get(j).getCoverage();
                        sumCoverageValues += cov; //累积覆盖率                
                    } 
                    
                    sumCoverageValues = sumCoverageValues/ MAP_test.getInstance().validTSets.size() * 100.0;
                    
                } else {            	 
               	             	             	  
                     for (int j = 0; j < times; j++) { 
	                   	  System.out.println("Compute with the j = " + j + "validset: NO=" +  allValidTSets.get(j).size());
	                   	  
	                   	  MAP_test.getInstance().computeProductsPartialCoverage(products, allValidTSets.get(j));      
	                         
	                   	  for (int k = 0; k  < products.size(); k ++) {
                             double cov = products.get(k).getCoverage();
                             sumCoverageValues += cov; //累积覆盖 的个数     
                          }  
                     }
                     
                     sumCoverageValues = sumCoverageValues/times;                     
                     sumCoverageValues = sumCoverageValues/ allValidTSets.get(0).size() * 100.0;
                }// if               
                                		
                System.out.println("First100 =" + products.size());
                MAP_test.getInstance().writeDataToFile(writeResultPath + "First100." + i, products.size());// write the first 
                System.out.println("Coverage =" + sumCoverageValues);
                MAP_test.getInstance().writeDataToFile(writeResultPath +  t + "wiseCoverage." + i, sumCoverageValues);// write QDscore

        } // for runs   
        
	}
	public void computeFitness(String fmFile) throws Exception{
		 String sNbProds = "" + nbProds;
        String fmFileName = new File(fmFile).getName();

       
        double avgCoverage = 0.0;
        //double avgFitness = 0.0;
        SimilarityTechnique st = new SimilarityTechnique(SimilarityTechnique.Anti_Dice_Distance, SimilarityTechnique.NEAR_OPTIMAL_SEARCH);
                        
        String path = outputDir + algName  + "/" + fmFileName + "/Samples/" + sNbProds + "prods/"+ timeAllowed + "ms/";
        FileUtils.CheckDir(path); 
        
        String loadProductsPath = outputDir + algName  + "/"  + fmFileName + "/Samples/" + sNbProds + "prods/"+ timeAllowed + "ms/";            
        
        for (int i = 0; i < runs; i++) {
            System.out.println("computeFitness, run " + (i + 1));
        	
            List<Product> products = null; //
            
            // Load products                            
            products = MAP_test.getInstance().loadProductsFromFile(loadProductsPath + "Products." + i);
//	         shuffle(products); // 洗牌
	                      
            /*
             * 计算适应度值
             */
//          products = st.prioritize(products);
//          double fitness = st.getLastFitnessComputed();
//            double fitness = st.PD(products);
            double fitness = st.noveltyScoreSum(products, nbProds/2);                                                     

            MAP_test.getInstance().writeDataToFile(path + "Fitness." + i, fitness);// write Novelty score        

        } // for runs   
        
	}
	
	public void computeFaultRate(String fmFile) throws Exception{
		String sNbProds = "" + nbProds;
        String fmFileName = new File(fmFile).getName();
                  
        // Write path for faults
        String path =  outputDir + algName  + "/" + fmFileName +"/";
        FileUtils.CheckDir(path); 
        
        // Load path for products 
        String loadProductsPath = outputDir + algName  + "/" + fmFileName +"/";
       		 
        // Load path for faults
        String faultsDir = "./all_FM/testingfm/" + fmFileName + ".faults/";      
        
        int repeatTimes = 100;        
        
        for (int i = 0; i < runs; i++) {
//          System.out.println("run " + (i + 1));
        	
        	MetricsUtil utils = new MetricsUtil();                             	;
            double[][] data =  utils.readFront(loadProductsPath + "Minnproducts." + i);
            min_products = (int)data[0][0];
            data =  utils.readFront(loadProductsPath + "Maxnproducts." + i);
            max_products = (int)data[0][0];
             
        	double [] faultRateList = new double[max_products - min_products+1];
        	double sumFaultRate = 0.0;
        	double bestFaultRate = -1.0;
        	
        	for (int n=0;n<max_products - min_products+1;n++) { // for each TS in a MAP
        		
        		   List<Product> products = null; 
                   // Load products                            
                   products = MAP_test.getInstance().loadProductsFromFile(loadProductsPath + "TS." + i+"_"+n);
       	                             
                   // Average fault rate over <repeatTimes> times
                   double avgFaultsValues = 0.0;               
                                     
                   for (int k = 0; k < repeatTimes;k++) {
                   	
                   	 	String faultsFile = faultsDir + "faults." + k;    	
                   	 
                        if (faultsFile != null && (new File(faultsFile).exists())) {
                        	           
                        	validTSets = MAP_test.getInstance().loadValidTSet(faultsFile);   
//                        	System.out.println("Loading faults. Number of faults " + validTSets.size() + ",K = " + k);
                      
                        	MAP_test.getInstance().computeProductsPartialFaults(products, validTSets);  
                        	
                           double faultsRate = 0.0;
                           for (int j = 0; j < products.size(); j++) {
                               double coverFaults = products.get(j).getCoverage();
                               faultsRate += coverFaults; //累积错误数       
                           }
                                      
                           avgFaultsValues = avgFaultsValues + faultsRate/validTSets.size() * 100.0;                    
                        } // IF    
                        
                    }  // for k 
                   
                   avgFaultsValues = avgFaultsValues/repeatTimes; 
                   
                   faultRateList[n] = avgFaultsValues;
                   sumFaultRate = sumFaultRate + faultRateList[n];   
                   
                   if (avgFaultsValues > bestFaultRate) 
                	   bestFaultRate = avgFaultsValues;
        	} // for n
        	
        	bestFaultRate =faultRateList[(max_products - min_products)/2] ;// the middle position        	
            MAP_test.getInstance().writeDataToFile(path + "FaultRateAll." + i, faultRateList);// write all fault rates
            MAP_test.getInstance().writeDataToFile(path + "FaultRate." + i, bestFaultRate);// write all fault rates
            MAP_test.getInstance().writeDataToFile(path + "QDscoreFaultRate." + i, sumFaultRate);// write mean faults rate  
        
        }// for each run   
       
	} //computeFaultRate

	public void computeFaultRate4NonMAPMethods(String fmFile) throws Exception{
		String sNbProds = "" + nbProds;
        String fmFileName = new File(fmFile).getName();
                  
        // Write path for faults
        String path = outputDir + algName  + "/"  + fmFileName +"/";            
        FileUtils.CheckDir(path); 
        
        // Load path for products 
        String loadProductsPath = outputDir + algName  + "/" + fmFileName +"/";;
       		 
        // Load path for faults
        String faultsDir = "./all_FM/testingfm/" + fmFileName + ".faults/";      
        
        int repeatTimes = 100;        
        
        for (int i = 0; i < runs; i++) {
//          System.out.println("run " + (i + 1));
        	
		   List<Product> products = null; 
           // Load products                            
//           products = MAP_test.getInstance().loadProductsFromFile(loadProductsPath + "TS." + i," ");
		   products = MAP_test.getInstance().loadProductsFromFile(loadProductsPath + "TS." + i);
		   
           // Average fault rate over <repeatTimes> times
           double avgFaultsValues = 0.0;               
                             
           for (int k = 0; k < repeatTimes;k++) {
           	
           	 	String faultsFile = faultsDir + "faults." + k;    	
           	 
                if (faultsFile != null && (new File(faultsFile).exists())) {
                	           
                	validTSets = MAP_test.getInstance().loadValidTSet(faultsFile);   
//                	System.out.println("Loading faults. Number of faults " + validTSets.size() + ",K = " + k);
                	
                	MAP_test.getInstance().computeProductsPartialFaults(products, validTSets);  
                	
                   double faultsRate = 0.0;
                   for (int j = 0; j < products.size(); j++) {
                       double coverFaults = products.get(j).getCoverage();
                       faultsRate += coverFaults; //累积错误数       
                   }
                                        
                   avgFaultsValues = avgFaultsValues + faultsRate/validTSets.size() * 100;                    
                } // IF    
                
            }  // for k 
           
           avgFaultsValues = avgFaultsValues/repeatTimes;                   
     
           System.out.println("FaultRate=" + avgFaultsValues);
           MAP_test.getInstance().writeDataToFile(path + "FaultRate." + i, avgFaultsValues);// write mean faults rate  
        
        }// for each run   
       
	} //computeFaultRate

  /**
   * Main method
   * @param args
 * @throws Exception 
   */
  public static void main(String[] args) throws Exception {
    GeneratePerformanceMetrics gfr = new GeneratePerformanceMetrics();
    
    String [] fms = {
  			/** ***************** Small-scale N<=100*****************  */
			"ZipMe",//8
			"BerkeleyDBFootprint",//9
			"Apache",//10
			"argo-uml-spl",//11
			"LLVM",//12
			"PKJab",//12
			"Curl",//14
			"Wget",//17
			"x264",//17
			"BerkeleyDBC",//18
			"gpl",//18
			"BerkeleyDBMemory",//19
			"fame_dbms_fm",//21
			"DesktopSearcher",//22  			
			"CounterStrikeSimpleFeatureModel", //24 OK(TS=12,improved)
			"BerkeleyDBPerformance",//27
			"LinkedList",//27
			"SensorNetwork",//27			
			"HiPAcc",//31 OK (33,improved)
			"SPLSSimuelESPnP",//32 OK(11,improved)
			"TankWar",//37	
			"JavaGC",//39 OK(46,improved)
			"Polly", //40 OK (35,improved)
			"DSSample", //41  OK (96,not improved)
			"VP9",//42 OK(33,improved)
			"WebPortal",//43 OK (20,improved)
			"JHipster", //45 OK (38, improved)
			"Drupal", //48  (14,not improved)
			"SmartHomev2.2",//60 (17,not improved)
			"VideoPlayer",//71 (14,not improved)			
			"Amazon",//79 (266,improved)
			"ModelTransformation", //88 (29,not improved)
			"CocheEcologico", //94 (90,not improved) 	
			"n30Model1",
			"n30Model2",
			"n30Model3",
			"n30Model4",
			"n30Model5",
			"n30Model6",
			"n30Model7",
			"n30Model8",
			"n30Model9",
			"n30Model10",
			"n50Model1",
			"n50Model2",
			"n50Model3",
			"n50Model4",
			"n50Model5",
			"n50Model6",
			"n50Model7",
			"n50Model8",
			"n50Model9",
			"n50Model10",	
			
//			"n100Model1",
//			"n100Model2",
//			"n100Model3",
//			"n100Model4",
//			"n100Model5",
//			"n100Model6",
//			"n100Model7",
//			"n100Model8",
//			"n100Model9",
//			"n100Model10",				
//			/***************Median-scale******************** */
//			"Printers",//172
//			"fiasco_17_10",//234
//			"uClibc-ng_1_0_29",//269
//			"E-shop",//290
//			"toybox",//544
//			"axTLS", // 684
//			"busybox_1_28_0", // 998  	
//    		"SPLOT-3CNF-FM-1000-200-0,50-SAT-1",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-2",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-3",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-4",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-5",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-6",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-7",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-8",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-9",			
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-10",	
//			"freebsd-icse11",//1396,OK
//			"fiasco",//1638， OK
//			"uClinux",//1850, OK
//			"Automotive01", //2513, OK, 
//			"SPLOT-3CNF-FM-5000-1000-0,30-SAT-1",// 5000 , OK	
//	  		/*******************  Large-scale ***************** */
//			 "busybox-1.18.0",//6796
//			 "2.6.28.6-icse11", //6888
//			 "uClinux-config", //11254
//			 "buildroot", // 14910  		
			
//			"mpc50", //1213  M = 0(强制特征为0)
//			"ref4955",//1218  M = 0		
//			"linux", //1232  M = 0	
//			"csb281", //1233 M = 0
//			"ecos-icse11", //1244 M=0
//			"ebsa285", //1245 M=0
//			"vrc4373", // 1247  M=0, dead 是乱码？
//			"pati", // 1248  M=0, dead 是乱码？
//			"dreamcast", //1252 M=0, dead 是乱码？
//			"pc_i82544", //1259  M=0,
//			"XSEngine",  //1260  M=0,
//			"refidt334", //1263  M=0,
//			"ocelot", //1266   M=0, dead 是乱码？
//			"integrator_arm9", //1267 M=0,
//			"olpcl2294", //1273 M=0, dead 是乱码？
//			"olpce2294", //1274 M=0,
//			"phycore", // 1274，M=0, dead 是乱码？
//			"hs7729pci", //1298，M=0, dead 是乱码？	  	
	 
	};

	gfr.t           = 2; // To generate results for t= X, t=0 means faults rate
	gfr.nbProds     = 100;
	gfr.min_products= 5;
	gfr.max_products= 12;
	gfr.outputDir   =  "G:/javaWorkSpace/SPLTestingMAP/output/";  // output dir
//	gfr.outputDir   =  "./output/";  // output dir
	gfr.runs        = 30; 
//	gfr.algName     = "MAPelites-twoSAT";
//	gfr.algName     = "MAPelites-SAT";
//	gfr.algName     = "MAPelites-rSAT";
//	gfr.algName     = "MAPelites-DiverseProbSAT";
//	gfr.algName     = "SamplingCA";
//	gfr.algName     = "SamplingCASimplification";
	
	/**
	 * RQ1: mutated.mutate() + getOneRandomProductSAT4J();
	 */
//	gfr.algName = "MPNoSeedingAccuracy"; //RQ1: 
//	gfr.algName = "MPNoSeedingAccuracySingleTask";//RQ1	
//	gfr.algName = "MPNoSeedingDiversity";// RQ1:Noseeding + diversity fitness
//	gfr.algName = "MPNoSeedingDiversitySingleTask";//RQ1: Noseeding + diversity fitness
//	gfr.algName  = "MPSeedingDiversity";// Seeding + diversity fitness	
//	gfr.algName  = "MPSeedingDiversitySingleTask";// Seeding + diversity fitness
	
	/**
	 * RQ2: mutated.mutate() + getOneRandomProductSAT4J();
	 */
//	gfr.algName = "MPSeedingAccuracy";// Seeding + accuracy fitness+ SAT4J  	
//	gfr.algName = "MPSeedingAccuracySingleTask";// Seeding + accuracy fitness+ SAT4J +SingleTask 
	
	
	/**
	 * RQ3: mutated.mutate() + getOneRandomProductSAT4J();
	 */
//	gfr.algName = "MPNoSeedingDiversityForLargeMutateWorst";// Noseeding + diversity fitness + for larger FMs
//	gfr.algName = "MPNoSeedingDiversityForLargeMutateWorstSingleTask";
//	gfr.algName = "MPNoSeedingDiversityForLargeFinal";
//	gfr.algName = "MPNoSeedingDiversityForLargeFinal1";
//	gfr.algName = "TSENSE2000";	
//	gfr.algName = "TSENSE6000";	
	
	/**
	 * RQ4
	 */

//	gfr.algName = "MPNoSeedingAccuracy";
	gfr.algName = "NSGA2-NRRNoSeedingAccuracy";
//	gfr.algName = "NSGA2NoSeedingAccuracy";	
	
//	gfr.algName = "MPSeedingAccuracy";
//	gfr.algName = "NSGA2-NRRSeedingAccuracy";
//	gfr.algName = "NSGA2SeedingAccuracy";	
	

	//------------------------------------------------	
	


	
	
//	gfr.algName = "MPNoSeedingAccuracyFinal";// RQ1:Noseeding + diversity fitness
//	gfr.algName = "MPNoSeedingDiversityFinal";// RQ1:Noseeding + diversity fitness()	 	
//	gfr.algName = "MPSeedingAccuracyInitial";
//	gfr.algName = "MPNoSeedingDiversityForLarge";// Noseeding + diversity fitness + for larger FMs
//	gfr.algName = "MPNoSeedingDiversityForLargeEqual";// Noseeding + diversity fitness + for larger FMs
//	gfr.algName = "MPNoSeedingDiversityForLargeSingleTask";// Noseeding + diversity fitness + for larger FMs
//	gfr.algName = "MPNoSeedingDiversityTwoSAT";
//	gfr.algName = "MPNoSeedingDiversityTwoSATSingleTask";	

//	gfr.algName = "MPNoSeedingDiversityTwoSATNew";// Noseeding + diversity fitness, 无EDA，fix the error with prob
//	gfr.algName = "MPNoSeedingDiversityTwoSATNewEDA";// Noseeding + diversity fitness, 有EDA，fix the error with prob


	String fmFile = null;	
	for (int i = 0;i < fms.length;i++) {				

		fmFile = "./all_FM/testingfm/" + fms[i] + ".dimacs"; 
		
		System.out.println(fmFile);
		MAP_test.getInstance().initializeModelSolversCalIndicators(fmFile,  gfr.t);
		
//		gfr.computeTwiseCoverage(fmFile);	
//		gfr.computeFaultRate(fmFile);
//		gfr.computeDiversity(fmFile);
		
//		gfr.computeTwiseCoverage4NonMAPMethods(fmFile);
//		gfr.computeFaultRate4NonMAPMethods(fmFile);
//		gfr.computeDiversity4NonMAPMethods(fmFile);
		
//		gfr.computeImprovementRate(fmFile);
		
		
		gfr.computeQDDominanceFitness(fmFile); // For RQ4
	} // main
  }
} 


