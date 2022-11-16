/**
 * Calcualte spread QI, similar to IGD 
 */

package spl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import java.util.Set;

import spl.fm.Product;
import spl.techniques.SimilarityTechnique;
import spl.utils.FileUtils;


public class GenerateSpread {

	public int nbProds = 100;
	public long timeAllowed; 
	public String outputDir;
	public int runs;
	public String algName;
	 
	public GenerateSpread() {
		
	}
		
	
	public void execute(String fmFile) throws Exception{
		 String sNbProds = "" + nbProds;
         String fmFileName = new File(fmFile).getName();
         SimilarityTechnique st = new SimilarityTechnique(SimilarityTechnique.JACCARD_DISTANCE, SimilarityTechnique.NEAR_OPTIMAL_SEARCH);
                         
         String path = outputDir + algName  + "/" + fmFileName +"/Samples/"  + sNbProds + "prods/";
         FileUtils.CheckDir(path); 
                 
         int numFeature = MAP_test.getInstance().numFeatures;        		 
         int lowerBound = (MAP_test.getInstance().mandatoryFeaturesIndices).size();
         int upperBound = numFeature - (MAP_test.getInstance().deadFeaturesIndices).size();
        
         List<Integer> referenceSet = new ArrayList<Integer>();
         
         for (int i = lowerBound; i <= upperBound;i++) {
        	 referenceSet.add(i);        	 
         }
         
         System.out.println("referenceSet " + referenceSet.size());
         System.out.println("lowerBound " + lowerBound);
         System.out.println("upperBound " + upperBound);
         
         for (int r = 0; r < runs; r++) {
//           System.out.println("run " + (i + 1));         	
             List<Product> products = null;    
                          
             // Load products                            
             products = MAP_test.getInstance().loadProductsFromFile(path + "Products." + r);
             int [] selectedNum = new int[products.size()];
                  
             Collections.sort((List<Product>) products); 
       	   	 for (int i = 0; i < products.size();i++) {
       	   	    selectedNum [i] = products.get(i).getSelectedNumber();
//       	   	    System.out.print(selectedNum[i] + " ");
       	   	 }    
                            
       	   	 //For each reference point    
       	   	 double invertedDist = 0.0;
       	   	 
             for (int i = 0; i < referenceSet.size(); i++) {
            	 int pointInRef = referenceSet.get(i);
            	 int minDist = Math.abs(pointInRef - selectedNum[0]);
            	 
            	 for (int j = 1; j < selectedNum.length; j++) {
            		 if (Math.abs(pointInRef - selectedNum[j]) < minDist) {
            			 minDist = Math.abs(pointInRef - selectedNum[j]);
            		 }
            	 }
            	 
            	 invertedDist = invertedDist + minDist;
             }

             invertedDist = invertedDist/referenceSet.size();
                                      
             System.out.print("\ninvertedDist " + invertedDist + "\n");                        
             
             // Calculate extension             
             double dl = selectedNum [0] - lowerBound;
             double du = upperBound - selectedNum [selectedNum.length - 1];
            		             		 
             System.out.println("extension = " + (dl + du)/numFeature);
                          
             // Calculate frequency             
             List <Integer> frequency = new ArrayList<Integer> ();
                         
             int currentNum = selectedNum[0];
             int counter = 1;
             
             for (int i = 1; i < selectedNum.length;i++) {
            	 if (selectedNum[i] == currentNum) {
            		 counter++;
            	 }else {
            		 currentNum = selectedNum[i];
            		 frequency.add(counter);          
            		 counter = 1;
            	 }
             }
             // The last one should be added
             frequency.add(counter);
             
             int sum = 0;
             for (int i = 0;i < frequency.size();i++) {
            	 sum = sum + frequency.get(i);
             }
             
             double freAvg = sum/(double)frequency.size();
               
             //Calculate std
             double std = 0.0;
             
             for (int i = 0;i < frequency.size();i++) {
            	 std = std + (frequency.get(i) - freAvg) * (frequency.get(i) - freAvg);
             }
             
             std = Math.sqrt(std/frequency.size());
             
             System.out.println("std = " + std);
             
//             double fitness = st.noveltyScoreSum(products, 15);
             
             MAP_test.getInstance().writeDataToFile(path + "invertedDist." + r, invertedDist);// write coverage
//             SPL_sampler.getInstance().writeDataToFile(path + "Extension." + r, (dl + du)/numFeature);// write fitness        
//             SPL_sampler.getInstance().writeDataToFile(path + "STD." + r, std);// write fitness        
//             SPL.getInstance().writeDataToFile(path + "Fitness." + r, fitness);// write fitness  
         } // for runs         
        
	}

  /**
   * Main method
   * @param args
 * @throws Exception 
   */
  public static void main(String[] args) throws Exception {
    GenerateSpread gfr = new GenerateSpread();
    
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
			"mpc50", //1213
			"ref4955",//1218  		
			"linux", //1232
			"csb281", //1233
			"ecos-icse11", //1244
			"ebsa285", //1245
			"vrc4373", // 1247
			"pati", // 1248
			"dreamcast", //1252
			"pc_i82544", //1259
			"XSEngine",  //1260
			"refidt334", //1263
			"ocelot", //1266
			"integrator_arm9", //1267
			"olpcl2294", //1273
			"olpce2294", //1274
			"phycore", // 1274
			"hs7729pci", //1298
			"freebsd-icse11",//1396
			"fiasco",//1638
			"uClinux",//1850
			"Automotive01", //2513 
			"SPLOT-3CNF-FM-5000-1000-0,30-SAT-1",// 5000  			
////  			 /*******************  Large-scale ***************** */
			 "busybox-1.18.0",//6796
			 "2.6.28.6-icse11", //6888
			 "uClinux-config", //11254
			 "buildroot", // 14910
			 "freetz", //31012	
	};

	gfr.nbProds     = 100;
	gfr.outputDir   = "./output/";
//	gfr.outputDir = "/media/xiangyi/TOSHIBA EXT/result/TSE_NS/OutputDiverse/";     
	gfr.runs        = 30;
	
//	gfr.algName     = "SAT4JnoOrder";
//	gfr.algName     = "PaD+SAT4JnoOrder";	
//	gfr.algName     = "CheckingUnchangedVars"; 	
//	gfr.algName     = "SAT4J";
//	gfr.algName     = "PaD+SAT4J";
//	gfr.algName     = "PaD+SAT4Jnew";	
	
	gfr.algName     = "rSAT4J";// Specify the name of the sampler
//	gfr.algName     = "rSAT4JRerun";
//	gfr.algName     = "PaD+rSAT4J";
//	gfr.algName     = "PaD+rSAT4Jnew";
//	gfr.algName     = "PaD+rSAT4JnewRerun";
	
//	gfr.algName     = "ProbSAT";
//	gfr.algName     = "PaD+ProbSAT";
	
//	gfr.algName     = "ProbSATRerun";
//	gfr.algName     = "PaD+ProbSATRerun";
	
//	gfr.algName     = "ProbSATWhileRepair";
//	gfr.algName     = "PaD+ProbSATWhileRepair";
	
//	gfr.algName     = "RandomlizedSAT4J";
//	gfr.algName     = "RandomlizedSAT4JDiverse"; 
//	gfr.algName     = "ProbSAT"; 
//	gfr.algName     = "ProbSATDiverse"; 
	

	long timeAllowed = 0; 		

	String fmFile = null;	
	for (int i = 0;i < fms.length;i++) {				
		fmFile = "./all_FM/Selected/" + fms[i] + ".dimacs"; 	
			
		System.out.println(fmFile);
		MAP_test.getInstance().initializeModelSolvers(fmFile);
				
		gfr.timeAllowed  = timeAllowed;
		
		gfr.execute(fmFile);		
 
	} // main
  }
} // 


