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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spl.fm.Product;
import spl.fm.TSet;
import spl.techniques.SimilarityTechnique;
import spl.utils.FileUtils;


public class SolutionDistributionAnalyzer {

	public int exisitingT = 2;
	public int nbProds = 100;
	public int t = 6;
	public long timeAllowed; 
	public long evaluations; 
 
	public String outputDir;
	public int runs;
	public String algName;
	private Set<TSet> validTSets;
	 
	public SolutionDistributionAnalyzer() {
		
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
	
	public void computeDistribution(String fmFile) throws Exception{
		 String sNbProds = "" + nbProds;
         String fmFileName = new File(fmFile).getName();
        
         double avgCoverage = 0.0;
                
         String path = outputDir + algName  + "/" + fmFileName + "/Samples/" + sNbProds + "prods/";
                  
         //FileUtils.CheckDir(path); 
         
         String loadProductsPath = outputDir + algName  + "/"  + fmFileName + "/Samples/" + sNbProds + "prods/";            
  	 
         if (MAP_test.getInstance().validTSets == null) {       
        	 
         	String validTsetFolder= fmFile + ".valid" + t + "-Sets/"  ;  
         	 
     	    for (int j = 0; j < 1; j++) { 
     	    	MAP_test.getInstance().validTSets = MAP_test.getInstance().loadValidTSet(validTsetFolder+"tSet."+j);  
       	  	}
     	    
          }
         
         double [] distribution =  tsetsDistribution(MAP_test.getInstance().validTSets);
         
         generateDistributionMFiles("./all_FM/testingfm/",distribution,fmFileName);
         
//         for (int i = 0; i < runs; i++) {
//             System.out.println("ComputeDistribution, run " + (i + 1));
//         	
//             List<Product> products = null;    
//             double sumCoverageValues = 0.0;        
//             
//             // Load products                            
//             products = SPL_sampler.getInstance().loadProductsFromFile(loadProductsPath + "Products." + i);
//// 	         shuffle(products); // 洗牌
// 	                      
//            // Compute distribution diff
//                                       
//             double [] prodDistribution = productsDistribution(products);
//             
//             // distance 
//             double diff = 0.0;
//             
//             for(int j = 0; j < prodDistribution.length;j++) {
//            	 diff = diff +  (prodDistribution [j] - distribution[j]) * (prodDistribution [j] - distribution[j]) ;
//             }
//   
//             diff = Math.sqrt(diff);
//             
//             // KL Divergence
//            		 
//             // distance 
////             double KLD = 0.0;
////             
////             for(int j = 0; j < prodDistribution.length;j++) {
////            	 KLD = KLD +  (distribution[j]  * Math.log(distribution[j]/(prodDistribution [j] + 2.2204e-016)));        
////             }
////             
////             
////             double diff = KLD;                       
//             
//             System.out.println(" t = " + t + " distributionDiff = " + diff);     
//             
//             SPL_sampler.getInstance().writeDataToFile(path + t + "wiseDistDiff." + i, diff);// write coverage
//
//         } // for runs   
         
	}
	
	public void computeSolutionDistribution(String fmFile) throws Exception{
		 String sNbProds = "" + nbProds;
        String fmFileName = new File(fmFile).getName();
       
        double avgCoverage = 0.0;
               
        String path = outputDir + algName  + "/" + fmFileName + "/Samples/" + sNbProds + "prods/";
                 
        //FileUtils.CheckDir(path); 
        
        String loadProductsPath = outputDir + algName  + "/"  + fmFileName + "/Samples/" + sNbProds + "prods/";            
 	 
        if (MAP_test.getInstance().validTSets == null) {       
       	 
        	String validTsetFolder= fmFile + ".valid" + t + "-Sets/"  ;  
        	 
    	    for (int j = 0; j < 1; j++) { 
    	    	MAP_test.getInstance().validTSets = MAP_test.getInstance().loadValidTSet(validTsetFolder+"tSet."+j);  
      	  	}
    	    
         }
        
        double [] distribution =  tsetsDistribution(MAP_test.getInstance().validTSets);
                       
        for (int i = 0; i < runs; i++) {
            System.out.println("ComputeDistribution, run " + (i + 1));
        	
            List<Product> products = null;    
            double sumCoverageValues = 0.0;        
            
            // Load products                            
            products = MAP_test.getInstance().loadProductsFromFile(loadProductsPath + "Products." + i);
//	         shuffle(products); // 洗牌
	                      
           // Compute distribution diff                                      
            double [] prodDistribution = productsDistribution(products);
//            
//            // distance 
            double diff = 0.0;
            
            for(int j = 0; j < prodDistribution.length;j++) {
           	 diff = diff +  (prodDistribution [j] - distribution[j]) * (prodDistribution [j] - distribution[j]) ;
            }
  
            diff = Math.sqrt(diff);            
                           
            System.out.println(" t = " + t + " distributionDiff = " + diff);     
            
            MAP_test.getInstance().writeDataToFile(path + t + "wiseDistDiff." + i, diff);// write coverage

            generateSolutionDistributionMFiles(path,i,prodDistribution,fmFileName);
            
        } // for runs   
        
	}
	
	public void generateDistributionMFiles(String path,  double [] distribution, String fmFileName) throws Exception {
        
		fmFileName = fmFileName.replace(".", "_");
	 	
	 	String mPath = path + fmFileName + "_" + t + "wiseTsetDistribution.m";

	 	
        FileUtils.resetFile(mPath);     
        
        // Write header
         FileOutputStream fos   = new FileOutputStream(mPath,false)     ;
	      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
	      BufferedWriter bw      = new BufferedWriter(osw)        ;
	    
	            	       	
	   	  bw.write("%% Plots for convergence");bw.newLine();	
	   	  bw.write("figure ");
	   	  bw.newLine();
	      bw.write("clear ");
	      bw.newLine();
	      bw.write("clc ");
	      bw.newLine();
	      
	      bw.write("set(gcf,'Position',[500 200 500 400])"); // ����ͼ����ʾλ��
         bw.newLine();
       
         bw.write("if get(gca,'Position') <= [inf inf 400 400]");
         bw.newLine();
         bw.write("    Size = [3 5 .8 18];");
         bw.newLine();
         bw.write("else");
         bw.newLine();
         bw.write("    Size = [6 3 2 18];");
         bw.newLine();
         bw.write("end");
         bw.newLine();
       
         bw.write("set(gca,'NextPlot','add','Box','on','Fontname','Times New Roman','FontSize',Size(4),'LineWidth',1.3);");
         bw.newLine();
         	              
         bw.write(" distribution = [");
	       
         for (int i = 0; i < distribution.length - 1;i++) {
       	  bw.write(distribution[i] + ",");
         }
         
         bw.write(distribution[distribution.length - 1] + "];" );
         bw.newLine();	
         
         bw.write("distribution(find (distribution == -1.0)) =[]; ");
         bw.newLine();	
       //  bw.write("y = y + 0.001;"); bw.newLine();	
       		  
       bw.write(" b=bar(distribution,'linewidth',2,'edgecolor','[0.5 0 0]', 'facecolor', '[0.5 0 0]');");
	    bw.newLine();	
         
       bw.write(" tit = title('" + fmFileName.substring(0,fmFileName.length() - 7) + "');");
       bw.newLine();	
       bw.write("set(tit,'fontsize',20)");
       bw.newLine();
        
//       bw.newLine();
//       bw.write("xl = xlabel('Time (s)');");
//       bw.newLine();
//       bw.write("set(xl,'fontsize',20)");
//       bw.newLine();
       	         

       bw.write("yl = ylabel('Ratio of 1''s');");
       bw.newLine();
       bw.write("set(yl,'fontsize',20)");
       bw.newLine();
	     bw.write("set(gca,'YLim',[0,1]);");
	     bw.newLine();
     
	     bw.write("set(gca,'XLim',[0,size(distribution,2)+1]);");
	     bw.newLine();
//       bw.write("axis([1 102 -0.2 max(y)+0.2]);");
//       bw.newLine();
       
//       bw.write("set(gca,'xminortick','on') ;");
//       bw.newLine();
//       
//       bw.write("set(gca,'yminortick','on') ;");
//       bw.newLine();
	     
       bw.write("set(gca,'ytick',[0 0.5 1.0]) ;");
       bw.newLine();
       bw.write("set(gca,'yticklabel',{'0' '0.5' '1.0'}) ;");
       bw.newLine();
     	bw.write("line([0,size(distribution,2)+1],[0.5 0.5],'linewidth',2,'linestyle','--','color','green');");
     	bw.newLine();
       bw.close();         

   }
	
public void generateSolutionDistributionMFiles(String path, int r, double [] distribution, String fmFileName) throws Exception {
        
		fmFileName = fmFileName.replace(".", "_");
	 	
	 	String mPath = path  + "SolutionDistribution"+r+".m";

	 	
        FileUtils.resetFile(mPath);     
        
        // Write header
         FileOutputStream fos   = new FileOutputStream(mPath,false)     ;
	      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
	      BufferedWriter bw      = new BufferedWriter(osw)        ;
	    
	            	       	
	   	  bw.write("%% Plots for convergence");bw.newLine();	
	   	  bw.write("figure ");
	   	  bw.newLine();
	      bw.write("clear ");
	      bw.newLine();
	      bw.write("clc ");
	      bw.newLine();
	      
	      bw.write("set(gcf,'Position',[500 200 500 400])"); // ����ͼ����ʾλ��
         bw.newLine();
       
         bw.write("if get(gca,'Position') <= [inf inf 400 400]");
         bw.newLine();
         bw.write("    Size = [3 5 .8 18];");
         bw.newLine();
         bw.write("else");
         bw.newLine();
         bw.write("    Size = [6 3 2 18];");
         bw.newLine();
         bw.write("end");
         bw.newLine();
       
         bw.write("set(gca,'NextPlot','add','Box','on','Fontname','Times New Roman','FontSize',Size(4),'LineWidth',1.3);");
         bw.newLine();
         	              
         bw.write(" distribution = [");
	       
         for (int i = 0; i < distribution.length - 1;i++) {
       	  bw.write(distribution[i] + ",");
         }
         
         bw.write(distribution[distribution.length - 1] + "];" );
         bw.newLine();	
         
         bw.write("distribution(find (distribution == -1.0)) =[]; ");
         bw.newLine();	
       //  bw.write("y = y + 0.001;"); bw.newLine();	
       		  
//       bw.write(" b=bar(distribution,'linewidth',2,'edgecolor','[0.5 0 0]', 'facecolor', '[0.5 0 0]');");
//	    bw.newLine();	
         
	    bw.write(" b=plot(1:size(distribution,2),distribution,'r.');");
	    bw.newLine();	
	    
       bw.write(" tit = title('" + fmFileName.substring(0,fmFileName.length() - 7) + "');");
       bw.newLine();	
       bw.write("set(tit,'fontsize',20)");
       bw.newLine();
        
//       bw.newLine();
//       bw.write("xl = xlabel('Time (s)');");
//       bw.newLine();
//       bw.write("set(xl,'fontsize',20)");
//       bw.newLine();
       	         

       bw.write("yl = ylabel('Ratio of 1''s');");
       bw.newLine();
       bw.write("set(yl,'fontsize',20)");
       bw.newLine();
	     bw.write("set(gca,'YLim',[0,1]);");
	     bw.newLine();
     
	     bw.write("set(gca,'XLim',[0,size(distribution,2)+1]);");
	     bw.newLine();
//       bw.write("axis([1 102 -0.2 max(y)+0.2]);");
//       bw.newLine();
       
//       bw.write("set(gca,'xminortick','on') ;");
//       bw.newLine();
//       
//       bw.write("set(gca,'yminortick','on') ;");
//       bw.newLine();
	     
       bw.write("set(gca,'ytick',[0 0.5 1.0]) ;");
       bw.newLine();
       bw.write("set(gca,'yticklabel',{'0' '0.5' '1.0'}) ;");
       bw.newLine();
     	
//       bw.write("line([0,size(distribution,2)+1],[0.5 0.5],'linewidth',2,'linestyle','--','color','green');");     
//       bw.newLine();
       bw.close();         

   }
	public double [] tsetsDistribution(Set<TSet> validTests) {
		double [] distribution = new double [MAP_test.getInstance().numFeatures];
		int [] counter  = new int [MAP_test.getInstance().numFeatures];
		
	      for (TSet t : validTests) { // for each t-set
	    	  
	    	  for (Integer i : t.getVals()) {
	    		  if(MAP_test.getInstance().featureIndicesAllowedFlip.contains(Math.abs(i) - 1)) {// feature allowing flips
		    		  counter[Math.abs(i) - 1]++;
		    		  
		              if (i > 0) {
		            	  distribution[i-1]++;
		              }
	    		  }
	              
	          }
	      }
	        
	      for (int i = 0; i < distribution.length;i++) {
	    	  if (counter[i] == 0) { // means mandatory or dead features 
	    		  distribution[i] = -1;
	    	  }  else {
	    		  distribution[i] = distribution[i]/counter[i];	   
	    	  }
	    	  
	      }
	      
		return distribution;
	}
	
	
	public double [] productsDistribution(List<Product> products) {
		double [] distribution = new double [MAP_test.getInstance().numFeatures];
		int [] counter  = new int [MAP_test.getInstance().numFeatures];
			        
	      for (Product prod : products) { // for each product
	    	  
	    	  for (Integer i : prod) {
	    		  if(MAP_test.getInstance().featureIndicesAllowedFlip.contains(Math.abs(i) - 1)) {// feature allowing flips
		    		  counter[Math.abs(i) - 1]++;
		    		  
		              if (i > 0) {
		            	  distribution[i-1]++;
		              }
	    		  }
	              
	          }
	      }
	        
	      for (int i = 0; i < distribution.length;i++) {
	    	  
	    	  if (counter[i] == 0) { // means mandatory or dead features 
	    		  distribution[i] = -1;
	    	  }  else {
	    		  distribution[i] = distribution[i]/counter[i];	   
	    	  }
	    	  
	      }
	      
		return distribution;
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
        String path =  outputDir + algName  + "/" + fmFileName +"/" + t + "wise/" + sNbProds + "prods/"+ timeAllowed + "ms/";
        FileUtils.CheckDir(path); 
        
        // Load path for products 
        String loadProductsPath = outputDir + algName  + "/" + fmFileName +"/Samples/"  + sNbProds + "prods/"+ timeAllowed + "ms/";
       		 
        // Load path for faults
        String faultsDir = "./all_FM/TSE/" + fmFileName + ".faults/";      
        
        int repeatTimes = 100;        
        
        for (int i = 0; i < runs; i++) {
//          System.out.println("run " + (i + 1));
        	
            List<Product> products = null; 
            // Load products                            
            products = MAP_test.getInstance().loadProductsFromFile(loadProductsPath + "Products." + i);
	                  
            double sumFaultsValues = 0.0;               
            double [] faults = new double[repeatTimes];
            
            
            for (int k = 0; k < repeatTimes;k++) {
            	
            	 String faultsFile = faultsDir + "faults." + k;    	
            	 
                 if (faultsFile != null && (new File(faultsFile).exists())) {
                 	           
                 	validTSets = MAP_test.getInstance().loadValidTSet(faultsFile);   
//                 	System.out.println("Loading faults. Number of faults " + validTSets.size());
                 	
                 	MAP_test.getInstance().computeProductsPartialFaults(products, validTSets);  
                 	
                 	double faultsRate = 0.0;
                    for (int j = 0; j < nbProds; j++) {
                        double coverFaults = products.get(j).getCoverage();
                        faultsRate += coverFaults; //累积错误数       
                    }
                    
                    faults[k] = faultsRate/validTSets.size() * 100;                      
                    sumFaultsValues = sumFaultsValues + faultsRate/validTSets.size() * 100;                    
                 } // IF    
                 
             }  // for k               
                                                     
            sumFaultsValues = sumFaultsValues/repeatTimes;     

            MAP_test.getInstance().writeDataToFile(path + "MeanFaultRate." + i, sumFaultsValues);// write mean faults rate  
            MAP_test.getInstance().writeDataToFile(path + "FaultArray." + i, faults);// write all fault rates
        } // for runs         
       
	}

  /**
   * Main method
   * @param args
 * @throws Exception 
   */
  public static void main(String[] args) throws Exception {
    SolutionDistributionAnalyzer gfr = new SolutionDistributionAnalyzer();
    
    String [] fms = {
    		/** ***************** Small-scale*****************  */
    		"CounterStrikeSimpleFeatureModel", //24
			"HiPAcc",//31
			"SPLSSimuelESPnP",//32
			"JavaGC",//39
			"Polly", //40
			"DSSample", //41    
			"VP9",//42
			"WebPortal",//43
			"JHipster", //45
			"Drupal", //48
			"SmartHomev2.2",//60
			"VideoPlayer",//71
			"Amazon",//79
			"ModelTransformation", //88
			"CocheEcologico", //94
			"Printers",//172
			"fiasco_17_10",//234
			"uClibc-ng_1_0_29",//269
			"E-shop",//290
			"toybox",//544
			"axTLS", // 684
			"financial",//771 
			"busybox_1_28_0", // 998  		
//  			/***************Median-scale******************** */
//			"mpc50", //1213
//			"ref4955",//1218  		
//			"linux", //1232
//			"csb281", //1233
//			"ecos-icse11", //1244
//			"ebsa285", //1245
//			"vrc4373", // 1247
//			"pati", // 1248
//			"dreamcast", //1252
//			"pc_i82544", //1259
//			"XSEngine",  //1260
//			"refidt334", //1263
//			"ocelot", //1266
//			"integrator_arm9", //1267
//			"olpcl2294", //1273
//			"olpce2294", //1274
//			"phycore", // 1274
//			"hs7729pci", //1298
//			"freebsd-icse11",//1396
//			"fiasco",//1638
//			"uClinux",//1850
//			"Automotive01", //2513 
//			"SPLOT-3CNF-FM-5000-1000-0,30-SAT-1",// 5000  			
//  			/*******************  Large-scale ***************** */
//			 "busybox-1.18.0",//6796
//			 "2.6.28.6-icse11", //6888
//			 "uClinux-config", //11254
//			 "buildroot", // 14910   		
	};

	gfr.t           = 2; // To generate results for t= X, t=0 means faults rate
	gfr.nbProds     = 4;
	gfr.outputDir   = "G:/javaWorkSpace/SATSolvingBasedTesting/output/";//"./output/";
	gfr.runs        = 30; 
	gfr.algName     = "TwoArchEDANSp=0.1";	
//	gfr.algName     = "TwoArchNormalEDANSp=0.1";	
	
	
	String fmFile = null;	
	for (int i = 0;i < fms.length;i++) {			

		fmFile = "./all_FM/testingfm/" + fms[i] + ".dimacs"; 
		System.out.println(fmFile);
		MAP_test.getInstance().initializeModelSolversCalIndicators(fmFile,  gfr.t);
		
//		gfr.computeDistribution(fmFile);	// t-set 的分布
		gfr.computeSolutionDistribution(fmFile); // solutions's distribution

	} // main
  }
} 


