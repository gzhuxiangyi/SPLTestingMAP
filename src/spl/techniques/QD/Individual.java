/*
 * Author : Christopher Henard (christopher.henard@uni.lu)
 * Date : 21/05/2012
 * Copyright 2012 University of Luxembourg – Interdisciplinary Centre for Security Reliability and Trust (SnT)
 * All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package spl.techniques.QD;

import java.util.ArrayList;
import java.util.List;

import jmetal.encodings.variable.Binary;
import spl.MAP_test;
import spl.fm.Product;
import spl.techniques.DistancesUtil;
import spl.techniques.SimilarityTechnique;
import spl.utils.PseudoRandom;

/**
 *
 * @author Christopher Henard
 */
public class Individual implements Comparable<Individual> {

    public static final int MUTATE_WORST = 0;
    public static final int MUTATE_BEST = 1;
    public static final int MUTATE_RANDOM = 2;
    
    public static final int EXACT_COPY = 0;
    public static final int ADD_COPY = 1;
    public static final int DEL_COPY = 2;

    public static final int FITNESS_TYPE_ACCURATE = 0;
    public static final int FITNESS_TYPE_APPORIMATE = 1;
    public static final int FITNESS_TYPE_DIVERSITY = 2;
    
    
    private double fitness;
    private int worstFitnessID = -1;
    
    private List<Product> products;
    private static SimilarityTechnique st = new SimilarityTechnique(SimilarityTechnique.JACCARD_DISTANCE, SimilarityTechnique.NEAR_OPTIMAL_SEARCH);
    private int minProductsNo;
    private int maxProductsNo;
    private int [] noOfTrue;
    private double [] prob;
    private double[][] distancesMatrix;
    public static boolean useDistanceAsFitness = false;
    public static int counter = 0;
    
    public Individual(Individual other) {
        this.products = new ArrayList(other.products);
        this.fitness = other.fitness;
        this.worstFitnessID = other.worstFitnessID;
        this.minProductsNo = other.minProductsNo;
        this.maxProductsNo = other.maxProductsNo;
        
        this.noOfTrue   = new int[other.noOfTrue.length] ;   
        this.prob = new double[other.prob.length];
        
        for (int i=0;i<noOfTrue.length;i++) {
        	noOfTrue[i] = other.noOfTrue[i];
        	prob[i] = other.prob[i];
        }   
                
        if (useDistanceAsFitness == true) 
        	distancesMatrix = matrixCopy(other.distancesMatrix, -1, 0); // exact copy
        
    }

    public boolean isUseDistanceAsFitness() {
		return useDistanceAsFitness;
	}

	public  void setUseDistanceAsFitness(boolean useDistanceAsFitness) {
		this.useDistanceAsFitness = useDistanceAsFitness;
	}

	public Individual(List<Product> products) {
        this.products = products;
    }

    public int getMaxProductsNo() {
		return maxProductsNo;
	}

	public void setMaxProductsNo(int maxProductsNo) {
		this.maxProductsNo = maxProductsNo;
	}
	
	

	public int getMinProductsNo() {
		return minProductsNo;
	}

	public void setMinProductsNo(int minProductsNo) {
		this.minProductsNo = minProductsNo;
	}

	public Individual(List<Product> products, int minProductsNo, int maxProductsNo) {
		
        this.products = products;
        this.minProductsNo = minProductsNo; 
        this.maxProductsNo = maxProductsNo;
                
        initializeSelectedNumber();
        
        prob = new double [noOfTrue.length];
        
        if (useDistanceAsFitness == true) {    
//        	System.out.println("Initialize disttance");
        	initializeMatric();
        }
    }
    
	/**
	 *  flag = 0 (exact copy), =1 (add one row/col); =2 (del one row/col specified by <skipped>)
	 * @param matrix
	 * @param skipped
	 * @param flag
	 * @return
	 */
	private double [][] matrixCopy(double [][] matrix,int skipped, int flag) { 
		double [][] copiedMatrix = null;
		
		if (flag == EXACT_COPY) {
			 copiedMatrix = new double [matrix.length][matrix[0].length];
		}
		
		if (flag == ADD_COPY) {
			copiedMatrix = new double [matrix.length+1][matrix[0].length+1];
		}
		
		if (flag == DEL_COPY) {
			copiedMatrix = new double [matrix.length-1][matrix[0].length-1];
		}
		
		
		if (flag == EXACT_COPY || flag == ADD_COPY) {
			
			for (int i = 0; i < matrix.length;i++) {
				System.arraycopy(matrix[i], 0, copiedMatrix[i], 0, matrix[0].length);
			}// for
			
		} else if (flag == DEL_COPY){
			
			for (int i = 0; i < skipped;i++) {
				System.arraycopy(matrix[i], 0, copiedMatrix[i], 0, skipped);
				System.arraycopy(matrix[i], skipped + 1, copiedMatrix[i], skipped, matrix[0].length - (skipped+1));
			}// for
			
			for (int i = (skipped +1); i < matrix.length ;i++) {
				System.arraycopy(matrix[i], 0, copiedMatrix[i-1], 0, skipped);
				System.arraycopy(matrix[i], skipped + 1, copiedMatrix[i-1], skipped, matrix[0].length - (skipped+1));
			}// for
			
		}
		
		return copiedMatrix;
		
	}

	/**
               * 初始化距离矩阵
     * @param product
     */
    public void initializeMatric() {
    	distancesMatrix = new double[products.size()][products.size()];
    	// Computation of the distances
        for (int i = 0; i < products.size(); i++) {
        	distancesMatrix[i][i] = 0.0;
        	
            for (int j = i + 1; j < products.size(); j++) {   
//                double dist = DistancesUtil.getJaccardDistance(archive.get(i), archive.get(j)); 
//            	 double dist = DistancesUtil.getHammingDistance(products.get(i), products.get(j));            	 
//            	double dist = DistancesUtil.getDiceDistance(archive.get(i), archive.get(j)); 
            	double dist = DistancesUtil.getAntiDiceDistance(products.get(i), products.get(j));             	
                distancesMatrix[i][j] = dist;       
                distancesMatrix[j][i] = dist;  
            } // for j
        } // for i
        
    } // initializeMatric
    
	/**
	*  
	* @param product
	*/
	public void initializeSelectedNumber() {
		noOfTrue = new int[products.get(0).size()];		
			
		for (Product prod:products)  {
			
			for (Integer i:prod) {	        	
	        	if (i > 0){
	        		noOfTrue[i-1] ++ ;
	        	} 	        		
	        } // for i
			
		} // prod
	
	} // initializeSelectedNumber

    /**
              * 将Product转换成Binary
     * @param vector
     * @return
     */
    public Binary Product2Bin(Product prod) {

    	Binary bin = new Binary(prod.size());    	    
        
        for (Integer i:prod) {
        	
        	if (i > 0){
        		bin.setIth(i-1, true);
        	} else {
        		bin.setIth(Math.abs(i)-1, false);
        	}
        		
        } // for i
        return bin;
    }
    public void fitnessAndOrdering() {
        products = st.prioritize(products);
        fitness = st.getLastFitnessComputed();
    }

    public void fitness() {
        fitness = SimilarityTechnique.getJaccardFitnessSum(products);
    }

    public void fitness2() {
        fitness = SimilarityTechnique.getBinaryDistance(products);
    }

    public void fitness(int type) {
       if (type == FITNESS_TYPE_ACCURATE)
    	   fitnessCoverageAccurate();
//       else if (type == FITNESS_TYPE_APPORIMATE)
//    	   fitnessCoverageApproximate();
       else if (type == FITNESS_TYPE_DIVERSITY)
    	   fitnessDiversity();
       else {
    	   System.out.println("Undefined fitness type");
       }
    }
    
    
    /**
     * Coverage-based fitness
     */
    public void fitnessCoverageAccurate() {
    	double sumCoverageValues  = 0.0;
    	
    	// Compute coverage (for small FMs)
        if (MAP_test.getInstance().validTSets != null) {
       	 
	       	 MAP_test.getInstance().computeProductsPartialCoverage(products, MAP_test.getInstance().validTSets);      
	       
	       	 for (int j = 0; j < products.size(); j++) {
	            double cov = products.get(j).getCoverage();
	            sumCoverageValues += cov;                
	       	 }  
	       	 
	       	sumCoverageValues = sumCoverageValues/MAP_test.getInstance().validTSets.size() * 100.0;
        }       
        
       fitness = sumCoverageValues;  
    }
    
    
    /**
     * Coverage-based fitness
     */
    public void fitnessCoverageApproximate() {
//    	double sumCoverageValues  = 0.0;    	
        // Compute coverage (for large FMs)
//       if  (MAP_test.getInstance().allValidTSets != null) {    
//    	   
//    	   int times = MAP_test.getInstance().allValidTSets.size();   
//    	   
//             for (int j = 0; j < times; j++) { 
//           	            	  
//           	  MAP_test.getInstance().computeProductsPartialCoverage(products,  MAP_test.getInstance().allValidTSets.get(j));      
//                 
//           	  for (int k = 0; k  < products.size(); k ++) {
//                     double cov = products.get(k).getCoverage();
//                     sumCoverageValues += cov; //累积覆盖率                
//                 } 
//           	  
//           	  sumCoverageValues = sumCoverageValues/MAP_test.getInstance().allValidTSets.get(j).size() * 100.0;
//       	     } // for j
//             
//             sumCoverageValues = sumCoverageValues/times;            
//       }        
//       
//       fitness = sumCoverageValues;  
    	
    	double sumCoverageValues  = 0.0;
    	
    	// Compute coverage (for small FMs)
        if (MAP_test.getInstance().validTSets != null) {
       	 
	       	 MAP_test.getInstance().computeProductsPartialCoverage(products, MAP_test.getInstance().validTSets);      
	       
	       	 for (int j = 0; j < products.size(); j++) {
	            double cov = products.get(j).getCoverage();
	            sumCoverageValues += cov;                
	       	 }  
	       	 
	       	sumCoverageValues = sumCoverageValues/MAP_test.getInstance().validTSets.size() * 100.0;
        }       
        
       fitness = sumCoverageValues;  
       
//      System.out.println(fitness);
    }
    /**
     * Diversity-based fitness
     */
    public void fitnessDiversity() {
    	
    	int k = Math.max(products.size()/2,2);   
//    	int k = products.size();
//    	System.out.println("k" + k);
    	int size = products.size();
    	double noveltyScoreSum = 0.0;
    	
    	double [] dist = new double [size]; 
    	int []    idx =  new int [size]; 
    	
    	worstFitnessID = -1;
    	double worstFitness = Double.MAX_VALUE;
    			
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
    		
    		if (noveltyScore < worstFitness) {
    			worstFitness = noveltyScore;
    			worstFitnessID = i;
    		}
    			
    	} // for i
    	
    	fitness = noveltyScoreSum;
   
//    	System.out.println(fitness);
    }
    
    public double getFitness() {
        return fitness;
    }

    public int getSize() {
        return products.size();
    }

    public List<Product> getProducts() {
        return products;
    }


    public void mutate(int mutateType) throws Exception {

        Product p;
        do {
            p = MAP_test.getInstance().getUnpredictableProduct();
        } while (products.contains(p));
        switch (mutateType) {
            case MUTATE_WORST:
                products.set(products.size() - 1, p);
                break;
            case MUTATE_BEST:
                products.set(0, p);
                break;
            case MUTATE_RANDOM:
                int ind = (int) (Math.random() * (products.size() - 2)) + 1;
                products.set(ind, p);
                break;
            default:
                ;
        }

    }
	/**
	 * Mutate an individual
	 * 1. Generate a new individual for addition or substitute
	 * 2. Update noOfTrue
	 * 3. Update distance matrix (if necessary)
	 * @throws Exception
	 */
    public int mutate() throws Exception {
  	
    	int operatedIndex = -1;
    	int operatedType = 1; // 1(sub), 2(add), 3(del)    	
    	
    	// Construct the prob vector
    	for (int i=0;i < noOfTrue.length;i++) {
    		prob[i] = noOfTrue[i]/(double)products.size();     	
    	}
    	
    	Product p = null;    	
    	Binary bin_operated = null;	
    	    	
    	if (minProductsNo == maxProductsNo) { //Direct search
    		p = getOneProduct(prob);
    		int ind = PseudoRandom.randInt(0, products.size() - 1);    
    		bin_operated = Product2Bin(products.get(ind)); // fix a bug
    		
			products.set(ind, p); //sub
			operatedType = 1; operatedIndex = ind;
    	} else {
    		
	    	if (products.size() == minProductsNo) { // With minimum number of products 
	    		
	    		p = getOneProduct(prob);
	    		
	    		if ( PseudoRandom.randDouble() < 2.0/3.0) {    			
	    			int ind = PseudoRandom.randInt(0, products.size() - 1);    
	    			bin_operated = Product2Bin(products.get(ind)); // fix a bug
	    			
	    			products.set(ind, p); //sub
	    			operatedType = 1; operatedIndex = ind;
	    		} else { // add
	    			products.add(p);
	    			operatedType = 2;operatedIndex = products.size() - 1;
	    		}  		
	    			
	    	} else if (products.size() == maxProductsNo) { // Reach max value
	    		
	    		if (PseudoRandom.randDouble() < 2.0/3.0) {    	 
	    			p = getOneProduct(prob);
	    			int ind = PseudoRandom.randInt(0, products.size() - 1);     
	    			bin_operated = Product2Bin(products.get(ind)); // fix a bug
	    			
	    			products.set(ind, p); //sub
	    			operatedType = 1; operatedIndex = ind;
	    			
		   		} else { // Del
		   			int ind = PseudoRandom.randInt(0, products.size() - 1);     
	        		products.remove(ind);
	        		operatedType = 3; operatedIndex = ind;
		   		} 
	    		
	    	} else {
	    		
	    		double r = PseudoRandom.randDouble() ;
	    		
	        	if (r>=0 && r < 1.0/3.0) {// Sub
	        		p = getOneProduct(prob);
	        		int ind = PseudoRandom.randInt(0, products.size() - 1);  
	        		bin_operated = Product2Bin(products.get(ind)); // fix a bug
	        		
	                products.set(ind, p);
	                operatedType = 1;   operatedIndex = ind;
	                 
	        	} else if (r>=1.0/3.0 && r<2.0/3.0) { //   Addition  
	        		p = getOneProduct(prob);
		       		products.add(p) ; 
		    		operatedType = 2;operatedIndex = products.size() - 1;
	        	} else { // del
	    			int ind = PseudoRandom.randInt(0, products.size() - 1);     
	    			products.remove(ind);
	    			operatedType = 3; operatedIndex = ind;
	        	} //if 
	        	
	    	}
    	}
    	
       	// --------------------------Update noOfTrue------------------------------
    	for (int i = 0; i < noOfTrue.length;i++)
    		noOfTrue[i] = 0;
    	
    	for (Product prod:products)  {			
			for (Integer i:prod) {	        	
	        	if (i > 0){
	        		noOfTrue[i-1] ++ ;
	        	} 	        		
	        } // for i					
		} // prod    	    
    	
    	/**the following code checks whether the update of noOfTrue is right */    	
//    	for (int k = 0; k < noOfTrue.length;k++) {
//    		int number = 0;
//    		for (int j=0;j<products.size();j++) {
//    			Binary bp =  Product2Bin(products.get(j));
//    			
//    			if(bp.getIth(k) == true) number++;
//    		}
//    		
//    		if (number != noOfTrue [k]) System.out.println("number = " + number + ";noOfTrue [k] = " + noOfTrue [k]);
//    	}
    	// --------------------------Update noOfTrue end------------------------------    	

    	 // -------------------------------------Update distance matrix-------------------------
    	if (useDistanceAsFitness == true) {
//    		System.out.println("Update distance");
    		if (operatedType == 1) {// sub
//    			System.out.println("sub");
    			// TODO: calculate distances 
    			for(int i=0;i<products.size();i++) {
    				distancesMatrix[operatedIndex][i]= DistancesUtil.getAntiDiceDistance(p, products.get(i));   
//    				distancesMatrix[operatedIndex][i]= DistancesUtil.getHammingDistance(p, products.get(i));  
    				distancesMatrix[i][operatedIndex] = distancesMatrix[operatedIndex][i];
    			}
    			
    		} else if (operatedType == 2) { // add
//    			System.out.println("add");
    			distancesMatrix = matrixCopy(distancesMatrix, -1, ADD_COPY);
    			// TODO: calculate distances 
    			for(int i=0;i<products.size();i++) {
    				distancesMatrix[operatedIndex][i]= DistancesUtil.getAntiDiceDistance(p, products.get(i));   
//    				distancesMatrix[operatedIndex][i]= DistancesUtil.getHammingDistance(p, products.get(i));  
    				distancesMatrix[i][operatedIndex] = distancesMatrix[operatedIndex][i];
    			}
    			
    		} else {
//    			System.out.println("del");
    			distancesMatrix = matrixCopy(distancesMatrix, operatedIndex, DEL_COPY);
    		}
    			
    	}//if
   	 // -------------------------------------Update distance matrix(end)-------------------------
    	
    	return operatedType;
    }
    
    /**
     * Note that prob is not used
     * @param prob
     * @return
     * @throws Exception
     */
    public Product getOneProduct(double [] prob) throws Exception {
    	Product p = null;
    	do {
          p = MAP_test.getInstance().getOneRandomProductSAT4J(); // SAT4J
//          p = MAP_test.getInstance().getOneRandomProductRandomlizedSAT4J();//rSAT4J
//          p = MAP_test.getInstance().getRandomProducts(0.1, prob);    
//  		 p = MAP_test.getInstance().getRandomProducts(1, prob); 
        } while (products.contains(p));
    	
    	counter++;
    	
    	return p;
    }
    
    
    /**
	 * Mutate an individual
	 * 1. Generate a new individual for addition or substitute
	 * 2. Update noOfTrue
	 * 3. Update distance matrix (if necessary)
	 * @throws Exception
	 */
    public void mutateWorst() throws Exception {

    	int operatedIndex = -1;
    	int operatedType = 1; // 1(sub), 2(add), 3(del)    	
    	
    	// Construct the prob vector
//    	for (int i=0;i < noOfTrue.length;i++) {
//    		prob[i] = noOfTrue[i]/(double)products.size();     	
//    	}
    	
    	Product p = null;
    	
    	if (minProductsNo == maxProductsNo) { //Direct search 	
    		
        	p = getOneProduct(prob);
			products.set(worstFitnessID, p); //sub
			
			operatedType = 1; operatedIndex = worstFitnessID;
			
    	} else {
    		
	    	if (products.size() == minProductsNo) { // With minimum number of products 
	    		
	    		p = getOneProduct(prob);
	        	
	    		if ( PseudoRandom.randDouble() < 2.0/3.0) {   
	    			products.set(worstFitnessID, p); //sub
	    			operatedType = 1; operatedIndex = worstFitnessID;
	    		} else { // add
	    			products.add(p);
	    			operatedType = 2;operatedIndex = products.size() - 1;
	    		}  		
	    			
	    	} else if (products.size() == maxProductsNo) { // Reach max value
	    			    			        	
	    		if (PseudoRandom.randDouble() < 2.0/3.0) {    
	    			p = getOneProduct(prob);
	    			products.set(worstFitnessID, p); //sub
	    			operatedType = 1; operatedIndex = worstFitnessID;	    			
		   		} else { // Del
		       		products.remove(worstFitnessID);
	        		operatedType = 3; operatedIndex = worstFitnessID;
		   		} 
	    		
	    	} else {
	    		
	    		double r = PseudoRandom.randDouble() ;
	    		
	        	if (r>=0 && r < 1.0/3.0) {// Sub	
	        		
	        		p = getOneProduct(prob);
	            	
	                products.set(worstFitnessID, p);
	                operatedType = 1;   operatedIndex = worstFitnessID;	                 
	        	} else if (r>=1.0/3.0 && r<2.0/3.0) { //   Addition     
	        		
	        		p = getOneProduct(prob);
	            	
		       		products.add(p) ; 
		    		operatedType = 2;operatedIndex = products.size() - 1;
	        	} else { // del	       
	    			products.remove(worstFitnessID);
	    			operatedType = 3; operatedIndex = worstFitnessID;
	        	} //if 
	        	
	    	}
    	}
    	
       	// --------------------------Update noOfTrue------------------------------
//    	for (int i = 0; i < noOfTrue.length;i++)
//    		noOfTrue[i] = 0;
//    	
//    	for (Product prod:products)  {			
//			for (Integer i:prod) {	        	
//	        	if (i > 0){
//	        		noOfTrue[i-1] ++ ;
//	        	} 	        		
//	        } // for i					
//		} // prod   
    	
    	// --------------------------Update noOfTrue end------------------------------    	

    	 // -------------------------------------Update distance matrix-------------------------
    	if (useDistanceAsFitness == true) {
//    		System.out.println("Update distance");
    		if (operatedType == 1) {// sub
//    			System.out.println("sub");
    			// TODO: calculate distances 
    			for(int i=0;i<products.size();i++) {
    				distancesMatrix[operatedIndex][i]= DistancesUtil.getAntiDiceDistance(p, products.get(i));   
//    				distancesMatrix[operatedIndex][i]= DistancesUtil.getHammingDistance(p, products.get(i));  
    				distancesMatrix[i][operatedIndex] = distancesMatrix[operatedIndex][i];
    			}
    			
    		} else if (operatedType == 2) { // add
//    			System.out.println("add");
    			distancesMatrix = matrixCopy(distancesMatrix, -1, ADD_COPY);
    			// TODO: calculate distances 
    			for(int i=0;i<products.size();i++) {
    				distancesMatrix[operatedIndex][i]= DistancesUtil.getAntiDiceDistance(p, products.get(i));   
//    				distancesMatrix[operatedIndex][i]= DistancesUtil.getHammingDistance(p, products.get(i));  
    				distancesMatrix[i][operatedIndex] = distancesMatrix[operatedIndex][i];
    			}
    			
    		} else {
//    			System.out.println("del");
    			distancesMatrix = matrixCopy(distancesMatrix, operatedIndex, DEL_COPY);
    		}
    			
    	}//if
   	 // -------------------------------------Update distance matrix(end)-------------------------
    	
    }
    
	/**
	 * Mutate an individual
	 * 1. Generate a new individual for addition or substitute
	 * 2. Update noOfTrue
	 * 3. Update distance matrix (if necessary)
	 * @throws Exception
	 */
    public void mutateSingleTask() throws Exception {
  	
    	int operatedIndex = -1;
    	int operatedType; // 1(sub), 2(add), 3(del)    	
    	
    	// Construct the prob vector
    	for (int i=0;i < noOfTrue.length;i++) {
    		prob[i] = noOfTrue[i]/(double)products.size(); 
    	}
    	
    	Product p;
    	
    	p = getOneProduct(prob);
    	
    	int ind = PseudoRandom.randInt(0, products.size() - 1);    
    	Binary bin_operated = Product2Bin(products.get(ind));	
    	    	
		products.set(ind, p); //sub
		operatedType = 1; operatedIndex = ind;			
		
       	// --------------------------Update noOfTrue------------------------------
//    	Binary bin_product = Product2Bin(p);
//									
//		for (int k = 0; k < bin_product.getNumberOfBits();k++) {
//			
//			if (bin_product.getIth(k) != bin_operated.getIth(k)) {
//				
//				if (bin_product.getIth(k) == true && bin_operated.getIth(k) == false) 
//					noOfTrue [k] ++; 
//					
//				if (bin_product.getIth(k) == false && bin_operated.getIth(k) == true) 
//					noOfTrue [k] --; 	
//			}
//		}	
		
    	for (int i = 0; i < noOfTrue.length;i++)
    		noOfTrue[i] = 0;
    	
    	for (Product prod:products)  {			
			for (Integer i:prod) {	        	
	        	if (i > 0){
	        		noOfTrue[i-1] ++ ;
	        	} 	        		
	        } // for i					
		} // prod    
		
    	// --------------------------Update noOfTrue end------------------------------
    	
    	 // -------------------------------------Update distance matrix-------------------------
    	if (useDistanceAsFitness == true) {    		    		
			// TODO: calculate distances 
			for(int i=0;i<products.size();i++) {
				distancesMatrix[operatedIndex][i]= DistancesUtil.getAntiDiceDistance(p, products.get(i)); 
//				distancesMatrix[operatedIndex][i]= DistancesUtil.getHammingDistance(p, products.get(i));  
				distancesMatrix[i][operatedIndex] = distancesMatrix[operatedIndex][i];
			}   		
    			
    	}//if
   	 // -------------------------------------Update distance matrix(end)-------------------------
    	
    }
    
    
    /**
	 * Mutate an individual
	 * 1. Generate a new individual for addition or substitute
	 * 2. Update noOfTrue
	 * 3. Update distance matrix (if necessary)
	 * @throws Exception
	 */
    public void mutateWorstSingleTask() throws Exception {
  	
    	int operatedIndex = -1;
    	int operatedType; // 1(sub), 2(add), 3(del)    	
    	
    	// Construct the prob vector
//    	for (int i=0;i < noOfTrue.length;i++) {
//    		prob[i] = noOfTrue[i]/(double)products.size(); 
//    	}
    	
    	Product p;    
    	p = getOneProduct(prob);  
    	    	    	
		products.set(worstFitnessID, p); //sub
		operatedType = 1; operatedIndex = worstFitnessID;			
		
       	// --------------------------Update noOfTrue------------------------------
	
    	for (int i = 0; i < noOfTrue.length;i++)
    		noOfTrue[i] = 0;
    	
    	for (Product prod:products)  {			
			for (Integer i:prod) {	        	
	        	if (i > 0){
	        		noOfTrue[i-1] ++ ;
	        	} 	        		
	        } // for i					
		} // prod    
		
    	// --------------------------Update noOfTrue end------------------------------
    	
    	 // -------------------------------------Update distance matrix-------------------------
    	if (useDistanceAsFitness == true) {    		    		
			// TODO: calculate distances 
			for(int i=0;i<products.size();i++) {
				distancesMatrix[operatedIndex][i]= DistancesUtil.getAntiDiceDistance(p, products.get(i)); 
//				distancesMatrix[operatedIndex][i]= DistancesUtil.getHammingDistance(p, products.get(i));  
				distancesMatrix[i][operatedIndex] = distancesMatrix[operatedIndex][i];
			}   		
    			
    	}//if
   	 // -------------------------------------Update distance matrix(end)-------------------------
    	
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Individual other = (Individual) obj;
        if (this.products != other.products && (this.products == null || !this.products.equals(other.products))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Individual{" + "fitness=" + fitness + ", products=" + products + '}';
    }

    @Override
    public int compareTo(Individual o) {
        double tF = getFitness();
        double oF = o.getFitness();
        return Double.compare(oF, tF);
    }
}
