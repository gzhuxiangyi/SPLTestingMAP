/* SPL.java
 * 
 * Author:  Yi Xiang <xiangyi@scut.edu.cn> or <gzhuxiang_yi@163.com>
 *  
 * Reference: 
 *  
 * Yi Xiang, Xiaowei Yang,Han Huang, Miqing Li
 * Sampling Configurations From Software Product Lines Via Probability-aware Diversification and SAT Solving, submitted
 * 
 * Data: 28/11/2021
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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package spl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.minisat.core.IOrder;
import org.sat4j.minisat.core.Solver;
import org.sat4j.minisat.orders.NegativeLiteralSelectionStrategy;
import org.sat4j.minisat.orders.PositiveLiteralSelectionStrategy;
import org.sat4j.minisat.orders.RandomLiteralSelectionStrategy;
import org.sat4j.minisat.orders.RandomWalkDecorator;
import org.sat4j.minisat.orders.VarOrderHeap;
import org.sat4j.reader.DimacsReader;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.beust.jcommander.ParameterException;

import jmetal.encodings.variable.Binary;
//import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.PseudoRandom;
import spl.fm.Product;
import spl.fm.TSet;
import spl.techniques.QD.Individual;
import spl.techniques.QD.IndividualMultiObj;
import spl.techniques.QD.MAP_elites;
import spl.techniques.QD.NSGA2;
import spl.techniques.QD.NoveltySearch1plusN;
//import spl.techniques.ga.GA;
//import spl.techniques.ga.Individual;
import spl.utils.FileUtils;
import splar.core.constraints.CNFClause;
import splar.core.constraints.CNFFormula;
import splar.core.fm.FeatureModel;
import splar.core.fm.XMLFeatureModel;
import splar.plugins.reasoners.sat.sat4j.ReasoningWithSAT;


public class MAP_test {

    private static Random randomGenerator = new Random();
    private FeatureModel fm;
    private ReasoningWithSAT reasonerSAT;
    private ISolver solverIterator, dimacsSolver;
    private ProbSATLocalSearch repairSolver;
    
    private  IOrder order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);

    private static MAP_test instance = null;
    private final int SATtimeout = 1000;
    private final long iteratorTimeout = 150000;
    public  long initializationTime ;
    private boolean dimacs;
    private String dimacsFile;
    private boolean predictable;
    private long termination;
    private  int terminationType; // terminationType = 1 means time in seconds, =2 means evaluations
    
    private List<List<Integer>> featureModelConstraints;
    private int nConstraints; // how many constraints
    public int numFeatures; // how many features
    
    public static List<Integer> mandatoryFeaturesIndices;
    public static List<Integer> deadFeaturesIndices;
    public static List<Integer> featureIndicesAllowedFlip;   
	
    public static List<Integer> featuresList = new ArrayList<Integer>();
    public static Map<Integer, String> featuresMap = new HashMap<Integer, String>(); // Map ID with name
     Map<String, Integer> featuresMapRev = new HashMap<String, Integer>(); // Map name with ID
     
    public Set<TSet> validTSets; // Used in accurate cases
    public List<Set<TSet> > allValidTSets ; // Used in inaccurate cases
 	
    protected MAP_test() {

    }

    public static void main(String[] args) throws Exception {

  	String [] fms = {  			
  			/** ***************** Small-scale N<100*****************  */
			"ZipMe",//8
//			"BerkeleyDBFootprint",//9
//			"Apache",//10
//			"argo-uml-spl",//11
//			"LLVM",//12
//			"PKJab",//12
//			"Curl",//14
//			"Wget",//17
//			"x264",//17
//			"BerkeleyDBC",//18
//			"gpl",//18
//			"BerkeleyDBMemory",//19
//			"fame_dbms_fm",//21
//			"DesktopSearcher",//22  			
//			"CounterStrikeSimpleFeatureModel", //24 OK(TS=12,improved)
//			"BerkeleyDBPerformance",//27
//			"LinkedList",//27
//			"SensorNetwork",//27			
//			"HiPAcc",//31 OK (33,improved)
//			"SPLSSimuelESPnP",//32 OK(11,improved)
//			"TankWar",//37	
//			"JavaGC",//39 OK(46,improved)
//			"Polly", //40 OK (35,improved)
//			"DSSample", //41  OK (96,not improved)
//			"VP9",//42 OK(33,improved)
//			"WebPortal",//43 OK (20,improved)
//			"JHipster", //45 OK (38, improved)
//			"Drupal", //48  (14,not improved)
//			"SmartHomev2.2",//60 (17,not improved)
//			"VideoPlayer",//71 (14,not improved)			
//			"Amazon",//79 (266,improved)
//			"ModelTransformation", //88 (29,not improved)
//			"CocheEcologico", //94 (90,not improved) 	
//			"n30Model1",
//			"n30Model2",
//			"n30Model3",
//			"n30Model4",
//			"n30Model5",
//			"n30Model6",
//			"n30Model7",
//			"n30Model8",
//			"n30Model9",
//			"n30Model10",
//			"n50Model1",
//			"n50Model2",
//			"n50Model3",
//			"n50Model4",
//			"n50Model5",
//			"n50Model6",
//			"n50Model7",
//			"n50Model8",
//			"n50Model9",
//			"n50Model10",	
  			
  			/*---------------------------------------分割线----------------------------------*/
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
////			/***************Median-scale******************** */
//			"Printers",//172
//			"fiasco_17_10",//234
//			"uClibc-ng_1_0_29",//269
//			"E-shop",//290
//			"toybox",//544
//			"axTLS", // 684
//			"busybox_1_28_0", // 998  	
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
//			"freebsd-icse11",//1396,OK
//			"fiasco",//1638， OK
//			"uClinux",//1850, OK
//			"Automotive01", //2513, OK, 
//			"SPLOT-3CNF-FM-5000-1000-0,30-SAT-1",// 5000 , OK			
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-1",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-2",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-3",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-4",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-5",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-6",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-7",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-8",
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-9",			
//			"SPLOT-3CNF-FM-1000-200-0,50-SAT-10",
			
  		     /*******************  Large-scale ***************** */
//			 "busybox-1.18.0",//6796
//			 "2.6.28.6-icse11", //6888
//			 "uClinux-config", //11254
//			 "buildroot", // 14910  			
  	};    	
    
  	//------------------------------------can be manually set------------------  
// 	String outputDir = "./output/";  // output dir 	
 	String outputDir = "G:/javaWorkSpace/SPLTestingMAP/output/";  // output dir
 	 	 	
  	int runs = 30; // How many runs
  	int min_nbProds = 5; // min number of products. Any test suite with size in [min_nbProds,max_nbProds]
  	int max_nbProds = 12; // max number of products. Any test suite with size in [min_nbProds,max_nbProds]
  	int t = 2;
  	
  	//RQ1: 
	String algName = "MPSeedingAccuracy";// Seeding + accuracy fitness+ SAT4J  	
//	String algName = "MPSeedingAccuracySingleTask";// Seeding + accuracy fitness+ SAT4J +SingleTask  	
//	String algName = "MPSeedingDiversity";// Seeding + diversity fitness
//	String algName = "MPSeedingDiversitySingleTask";// Seeding + diversity fitness
  	  	
//	String algName = "MPNoSeedingAccuracy";// RQ1: mutated.mutate() + getOneRandomProductSAT4J();
//	String algName = "MPNoSeedingAccuracySingleTask";//RQ1: mutated.mutate() + getOneRandomProductSAT4J(); 	
//	String algName = "MPNoSeedingDiversity";// Noseeding + diversity fitness
//	String algName = "MPNoSeedingDiversitySingleTask";// Noseeding + diversity fitness
	  	
  	// for large FMs
//	String algName = "MPNoSeedingDiversityForLargeMutateWorstSingleTask";// Noseeding + diversity fitness + for larger FMs
//	String algName = "MPNoSeedingDiversityForLargeFinal1";// Noseeding + diversity fitness + for larger FMs
//	String algName = "MPNoSeedingDiversityForLargeFinal1TwoSAT";// Noseeding + diversity fitness + for larger FMs
//	String algName = "MPNoSeedingDiversityForLarge";// Noseeding + diversity fitness + for larger FMs
//	String algName = "MPNoSeedingDiversityForLargeSingleTask";// Noseeding + diversity fitness + for larger FMs
//	String algName = "MPNoSeedingDiversityForLargeEqual";// Noseeding + diversity fitness + for larger FMs,equal computation distirbuton
//  	String algName = "MPNoSeedingDiversityForLargeMutateWorst";// Noseeding + diversity fitness + for larger FMs

  	//RQ4:
//	String algName = "NSGA2SeedingAccuracy";// 
//	String algName = "NSGA2NoSeedingAccuracy";// 
//	String algName = "NSGA2SeedingDiversity";// 
//	String algName = "NSGA2NoSeedingDiversity";// 	
//	String algName = "NSGA2-NRRSeedingAccuracy";// Do NOT Remove Reduplicated solutions
//	String algName = "NSGA2-NRRNoSeedingAccuracy";// Do NOT Remove Reduplicated solutions
  	
//	String algName = "NSGA2-NRRSeedingAccuracyTime";// To compare time. Do NOT Remove Reduplicated solutions  	
//	String algName = "MPSeedingAccuracyTime";//To compare time.  Seeding + accuracy fitness+ SAT4J  	
	
  	// For explanantion
//  	String algName = "MPNoSeedingAccuracyCounting";// RQ1: mutated.mutate() + getOneRandomProductSAT4J();
  	
 /**==========================Old run==================================*/
//  	String algName = "MAPelitesSeedingAccuracy";// Seeding + accuracy fitness+ SAT4J  	
//  	String algName = "MAPelitesSeedingAccuracySingleTask";// Seeding + accuracy fitness
//  	String algName = "MAPelitesSeedingAccuracyrSAT";// Seeding + accuracy fitness+ rSAT4J
//	String algName = "MAPelitesSeedingAccuracySAT4J";// Seeding + accuracy fitness+ rSAT4J  	
//	String algName = "MAPelitesSeedingDiversity";// Seeding + diversity fitness
//	String algName = "MAPelitesNoSeedingAccuracy";// Noseeding + accuracy fitness
//	String algName = "MAPelitesNoSeedingAccuracySingleTask";// Noseeding + accuracy fitness + SingleTask	
//  	String algName = "MAPelitesNoSeedingDiversity";// Noseeding + diversity fitness
//  	String algName = "MAPelitesNoSeedingDiversitySingleTask";// Noseeding + diversity fitness+SingleTask
//  	String algName = "MAPelitesNoSeedingDiversitySAT4J";// Noseeding + diversity fitness
//	String algName = "MAPelitesNoSeedingDiversityrSAT";// Noseeding + diversity fitness
  	//------------------------------------can be manually set (end) ------------------  	 
  	
  	String fmFile = null;
  	long evaluations = 12000;  
	boolean seedFlag = true;
	boolean largeFlag = false;
		
  	for (int i = 0;i < fms.length;i++) {
  		fmFile = "./all_FM/testingfm/" + fms[i] + ".dimacs"; 
 			
  		System.out.println(fmFile);  		  		  		
  		MAP_test.getInstance().initializeModelSolvers(fmFile,t);  	
  	
  		//Search-based 
  		MAP_test.getInstance().findProductsMAP(algName,fmFile, outputDir, runs,min_nbProds, max_nbProds, evaluations,seedFlag,largeFlag); //NS
//  		MAP_test.getInstance().findProductsNSGAII(algName,fmFile, outputDir, runs,min_nbProds, max_nbProds, evaluations,seedFlag,largeFlag); //NS
	}
  	
  } // main
    
    
    public static MAP_test getInstance() {
        if (instance == null) {
            instance = new MAP_test();
        }
        return instance;
    }
     
    
    /**
     * The R1 version of NS
     * @param fmFile
     * @param outputDir
     * @param runs
     * @param dimacs
     * @param nbPairs
     * @param t
     * @param nbProds
     * @param timeAllowed
     * @throws Exception
     */
    public void findProductsNS(String fmFile, String outputDir, int runs, int nbProds, long timeAllowed) throws Exception {

    	 String sNbProds = "" + nbProds;
         String fmFileName = new File(fmFile).getName();   
                  
         double p = 0.1; // the probability to use probSAT
                  
         String NSVariant = "TwoArchNormalEDANSp=" + p;  //name your algorithm, here is NS   NewArchEDANSp=
             
         System.out.println("Start findProductsNS..., Variant name:" + NSVariant);        
                  
         //--------------------------------------Nb----------------------
         int Nb = (nbProds)/2;    // Nb = N/2 is better in most cases    
//         int Nb = 2;   		 // Nb = N/2 is better in most cases    
         //--------------------------------------Nb end----------------------          
                   
         // Path to save products
         String path = outputDir + NSVariant + "/" + fmFileName +"/Samples/" + sNbProds + "prods/";
		 FileUtils.CheckDir(path); 			
         
         int [] evaluations = new int [runs]; //          
         
         for (int i = 0; i < runs; i++) {
             System.out.println(NSVariant + "：run " + (i));
             System.out.println("-----------------Nb------------------- = " + Nb);
             
             List<Product> products = null; 

             //Use NS to generate a set of samples                  
             long startTimeMS = System.currentTimeMillis() ;           
             
             NoveltySearch1plusN ns = new NoveltySearch1plusN(nbProds,Nb,0.0,timeAllowed,p);            
             products = ns.runNS(outputDir,fmFileName,i,evaluations);
            		 
             long endTimeMS = System.currentTimeMillis() - startTimeMS;
             
             // Write  products and runtime
             writeProductsToFile(path + "Products." + i, products);
             writeDataToFile(path + "RUNTIME." + i, endTimeMS);// write runtime
             writeDataToFile(path + "Evaluations." + i, evaluations[i]);// write average evaluations
             
         } // for runs        


    }
    

    /**
     * 
     * @param algName
     * @param fmFile
     * @param outputDir
     * @param runs
     * @param min_nbProds
     * @param max_nbProds
     * @param evaluations: number of evaluations
     * @param ifseed: Use seed or not
     * @throws Exception
     */
    public void findProductsMAP(String algName,String fmFile, String outputDir, int runs,
    			int min_nbProds,int max_nbProds, long evaluations, boolean seedFlag,boolean largeFlag) throws Exception {

         String fmFileName = new File(fmFile).getName();   
           
         System.out.println("=================Start running: " + algName+"=================");   
                 
         List<Product> seed = null;
         
         for (int i = 0; i < runs; i++) {
             System.out.println(algName + ":run " + (i));
                       	 
             if(!largeFlag) {// RQ1-2: Small scale FMs      		   
            	//===============Use the result of SamplingCA as seeds==============
                 String seedPath = "G:/javaWorkSpace/SPLTestingMAP/output/SamplingCA/"+fmFileName+"/TS." + i;
                 System.out.println("Seed file:" + seedPath); 
            	 seed = MAP_test.getInstance().loadSeedsFromFile(seedPath);  
            	 
          		 max_nbProds = seed.size();
	    		 min_nbProds = Math.max(max_nbProds - 5,2);	    		 
            
             }  else { // RQ3: Manually set for large FMs            	 
        		 max_nbProds = 101;
        		 min_nbProds = Math.max(max_nbProds - 2,2);        		 
             }
              
    		 System.out.println("max_nbProds: " + max_nbProds + ";min_nbProds: " +min_nbProds);
    
    		 if (!seedFlag) {
    			 seed = null;
    		 }
    	             
             if (seed == null)
            	 System.out.println("******************Use no Seeding **************************");   
             
        	 // Path to save products        	 
             String path = outputDir + algName + "/" + fmFileName +"/";
             FileUtils.CheckDir(path); 	
                 
             writeDataToFile(path + "Minnproducts." + i, min_nbProds);
             writeDataToFile(path + "Maxnproducts." + i, max_nbProds);
             
             //Use MAP-elites to generate a set of samples                  
             long startTimeMS = System.currentTimeMillis() ;
             
             MAP_elites map = new MAP_elites(min_nbProds,max_nbProds,evaluations);   
             
             List<Individual> testSuites =  map.runME(seed);
            		 
//             List<Individual> testSuites =  map.runMECounting(seed);// Recond improvment counting for each operation 
             
             long endTimeMS = System.currentTimeMillis() - startTimeMS;
                                      
             List<Double> fitnessList = new ArrayList <Double> ();
             
             boolean reached = false;
             int reachedPoint = -1;
             
             // Write  products and runtime
             for (int j=0; j < testSuites.size();j++) {            	 
            	 writeProductsToFile(path + "TS." + i+ "_" +j, testSuites.get(j).getProducts());
            	 fitnessList.add(testSuites.get(j).getFitness());
            	 
            	 if (!reached && testSuites.get(j).getFitness()>= 100.0 - 1e-6 ) {
            		 writeDataToFile(path + "First100." + i, (min_nbProds+j));// write the first 
            		 reached = true;
            		 reachedPoint = min_nbProds+j;
            	 }
            	 
             }
                     
        	 for(int k=0; k < max_nbProds - map.getInitialCriticalPoint(); k++) {
        		 fitnessList.add(100.0);
        	 }
        
        	 double QDscore = 0.0;
        	 
        	 for(int k=0;k<fitnessList.size();k++) {
        		 QDscore+=fitnessList.get(k);
        	 }
             
             writeDataToFile(path + "Fitness." + i, fitnessList);// write fitness
             writeDataToFile(path + "QDscoreFitness." + i, QDscore);// write QDscore, i.e., the sum of fitness
             
             
             if (!reached)
            	 writeDataToFile(path + "First100." + i, "INF");// write the first 
             
             writeDataToFile(path + "RUNTIME." + i, endTimeMS);// write runtime
             writeDataToFile(path + "Evaluations." + i, map.getEvaluations());// write average evaluations
             
             System.out.println("#run " + i + ",QDscoreFitness = " + QDscore+",First100="+reachedPoint);
             System.out.println("#run " + i + ",Evaluations = " + map.getEvaluations());//print evaluations
             System.out.println("#run " + i + ",Runtime = " + endTimeMS);//print evaluations
             
         } // for runs        


    }
    
    
    
    
    
    /**
     * The R1 version of NS
     * @param fmFile
     * @param outputDir
     * @param runs
     * @param dimacs
     * @param nbPairs
     * @param t
     * @param nbProds
     * @param timeAllowed
     * @throws Exception
     */
    public void findProductsNSGAII(String algName,String fmFile, String outputDir, int runs, int min_nbProds,
    		int max_nbProds, long evaluations,boolean seedFlag,boolean largeFlag) throws Exception {

         String fmFileName = new File(fmFile).getName();   
                                     
         System.out.println("=================Start running: " + algName+"=================");   
                          
         List<Product> seed = null;
         
         for (int i = 0; i < runs; i++) {
             System.out.println(algName + ":run " + (i));
                
             if(!largeFlag) {// RQ1-2: Small scale FMs      		   
             	//===============Use the result of SamplingCA as seeds==============
                 String seedPath = "G:/javaWorkSpace/SPLTestingMAP/output/SamplingCA/"+fmFileName+"/TS." + i;
                 System.out.println("Seed file:" + seedPath); 
             	 seed = MAP_test.getInstance().loadSeedsFromFile(seedPath);  
             	 
           		 max_nbProds = seed.size();
 	    		 min_nbProds = Math.max(max_nbProds - 5,2);	    		 
             
              }  else { // RQ3: Manually set for large FMs            	 
         		 max_nbProds = 101;
         		 min_nbProds = Math.max(max_nbProds - 2,2);        		 
              }
               
     		 System.out.println("max_nbProds: " + max_nbProds + ";min_nbProds: " +min_nbProds);
     
     		 if (!seedFlag) {
     			 seed = null;
     		 }
     	             
             if (seed == null)
             	System.out.println("******************Use no Seeding **************************");   
             
        	 // Path to save products        	 
             String path = outputDir + algName + "/" + fmFileName +"/";
             FileUtils.CheckDir(path); 	
                 
             writeDataToFile(path + "Minnproducts." + i, min_nbProds);
             writeDataToFile(path + "Maxnproducts." + i, max_nbProds);
             
             //Use MAP-elites to generate a set of samples                  
             long startTimeMS = System.currentTimeMillis() ;
             
             NSGA2 nsga2 = new NSGA2(min_nbProds,max_nbProds,evaluations);   
             
             List<IndividualMultiObj> testSuites =  nsga2.runNSGA2(seed);
            		        
             long endTimeMS = System.currentTimeMillis() - startTimeMS;
                   
             int number = max_nbProds - min_nbProds + 1; // how many solutions
             double [] fitnessList = new double [number];
             
             boolean reached = false;
             int reachedPoint = -1;
             
             
             // Write  products and runtime
             for (int j=0; j < testSuites.size();j++) {            	 
            	writeProductsToFile(path + "TS." + i+ "_" +j, testSuites.get(j).getProducts());
            	 
            	fitnessList[testSuites.get(j).getSize() - min_nbProds] =  - testSuites.get(j).getObjective(0);
                        	 
            	 if (!reached &&  -testSuites.get(j).getObjective(0) >= 100.0 - 1e-6 ) {
            		 writeDataToFile(path + "First100." + i, testSuites.get(j).getSize());// write the first 
            		 reached = true;
            		 reachedPoint = testSuites.get(j).getSize();
            	 }
            	 
             }
                
             // should not be used for diversity
             if (reached) {
	           	 for(int k= reachedPoint - min_nbProds; k < number; k++) {
	        		 fitnessList[k] = 100.0;
	        	 }
             }
        	 
        	 double QDscore = 0.0;
        	 
        	 for(int k=0;k<fitnessList.length;k++) {
        		 QDscore+=fitnessList[k];
        		 System.out.println(fitnessList[k]);
        	 }
             
             writeDataToFile(path + "Fitness." + i, fitnessList);// write fitness
             writeDataToFile(path + "QDscoreFitness." + i, QDscore);// write QDscore, i.e., the sum of fitness
             
             
             if (!reached)
            	 writeDataToFile(path + "First100." + i, "INF");// write the first 
             
             writeDataToFile(path + "RUNTIME." + i, endTimeMS);// write runtime
             writeDataToFile(path + "Evaluations." + i, nsga2.getEvaluations());// write average evaluations
             
             System.out.println("#run " + i + ",QDscoreFitness = " + QDscore+",First100="+reachedPoint);
             System.out.println("#run " + i + ",Evaluations = " + nsga2.getEvaluations());//print evaluations
             System.out.println("#run " + i + ",Runtime = " + endTimeMS);//print evaluations
             
         } // for runs        


    }
    
    /**
     * Get random products using Random + Repair
     * @param count
     * @return
     * @throws Exception
     */
    public Product getRandomProducts(double p,double [] probability) throws Exception {
              
        	Product product = null;
        	        	
//        	 Binary randomBinary = new Binary(numFeatures,probability); // EDA 随机产生一个二进制串   
//        	    for (int f = 0;f < numFeatures;f++) {
//	            	 randomBinary.setIth(f, false);    
//	             }
//        	    
//        	    product = toProduct(randomBinary);   
//	            
//	            if (isValidProduct(product)) {
//	            	System.out.println("******************accept zero-features******************");
//	            	return product;
//	            } else {
//	            	System.out.println("******************accept zero-features (NOT)******************");
//	            }
	            	            
        	 
        	if (randomGenerator.nextDouble() <= p) {
//        		System.out.println("Repair");
	        	 	             
//	            Binary randomBinary = new Binary(numFeatures,probability); // EDA 产生一个二进制串   	
        		
        		Binary randomBinary = new Binary(numFeatures); // NoEDA
        		
	            for (Integer f : this.mandatoryFeaturesIndices) {
	            	randomBinary.setIth(f, true);               	
	             }

	             for (Integer f : this.deadFeaturesIndices) {
	            	 randomBinary.setIth(f, false);    
	             }
	             
	        	Binary repaired = (Binary) repairSolver.execute(randomBinary);        	
	            product = toProduct(repaired);   
	            
	            if (!isValidProduct(product)) {
	            	product = toProduct(solverIterator.findModel());
//	            	product = SPL_sampler.getInstance().getOneRandomProductFromExisting(product);
	            }
	            
        	} else {
        		
        		int rand = (new Random()).nextInt(3);
            	IOrder order;
//                order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
                 
                if (rand == 0) {
	                 order = new RandomWalkDecorator(new VarOrderHeap(new NegativeLiteralSelectionStrategy()), 1);
	             } else if (rand == 1) {
	                 order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);
	             } else {
	                 order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
	             } 
                
            	((Solver) solverIterator).setOrder(order);   	
            	
	        	if (solverIterator.isSatisfiable()) {
	        		product = toProduct(solverIterator.findModel());
	        	}
        	}        	    
     
        return product;
    }
    
    
    
    /**
     * 
     * @param product
     * @return
     * @throws Exception
     */
    private Product getOneRandomProductFromExisting(Product product) throws Exception {
		  	
		Binary randomBinary = Product2Bin(product);
	            
	     //----------------------------------------------------------------
	     HashSet<Integer> blacklist = new HashSet<Integer>();  
         HashSet<Integer> backupBlacklist = new HashSet<Integer>();  
  
         int violated = numViolatedConstraints(randomBinary, blacklist, backupBlacklist);                       
          
         if (violated > 0) {
              IVecInt iv = new VecInt();

              for (int j : featureIndicesAllowedFlip) {
                  int feat = j + 1;
              
                  if (!blacklist.contains(feat)) {
                      iv.push(randomBinary.bits_.get(j) ? feat : -feat);
                  }
           
              }
    
	    //-------------------------------------------------------------        
	            
	       if (solverIterator.isSatisfiable()) {        	
	        	int rand = (new Random()).nextInt(3);
	        	IOrder order;
	             if (rand == 0) {
	                 order = new RandomWalkDecorator(new VarOrderHeap(new NegativeLiteralSelectionStrategy()), 1);
	             } else if (rand == 1) {
	                 order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);
	             } else {
	                 order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
	             } 
		        		        
		       ((Solver) solverIterator).setOrder(order); 		
		       	
	           int[] partialProd = solverIterator.findModel(iv); // partialProd contains only variables in CNF constraints
            
	           // Have another call
	           if (partialProd == null) {
	               System.out.println("not not satisfiable()");
	                        
	               iv.clear();
	               
	               for (int j : featureIndicesAllowedFlip) {
	                      int feat = j + 1;
	                  
	                      if (!backupBlacklist.contains(feat)) {
	                          iv.push(randomBinary.bits_.get(j) ? feat : -feat);
	                      }
	               
	                  }	               
	              
	               partialProd = solverIterator.findModel(iv);
	               
	           }
	           
	           for (int j = 0; j < partialProd.length; j++) {
	               int feat = partialProd[j];
	               
	               int posFeat = feat > 0 ? feat : -feat;
	
	               if (posFeat > 0) {
	               	randomBinary.setIth(posFeat - 1,feat > 0);
	               }
	           }// for	           
	       } 
	       else {//if
	          	System.out.println("solverIterator is not satisfiable()");
	       }   
       }       
       return toProduct(randomBinary);            
  }
	

	/**
     * Get random products using Random + Repair
     * @param count
     * @return
     * @throws Exception
     */
    public Product getRandomProducts_Random(double p) throws Exception {
              
        	Product product = null;
        	              
        	 
        	if (randomGenerator.nextDouble() <= p) {
//        		System.out.println("Repair");
	        	 	                    		
        		Binary randomBinary = new Binary(numFeatures); // no EDA
        		
	            for (Integer f : this.mandatoryFeaturesIndices) {
	            	randomBinary.setIth(f, true);               	
	             }

	             for (Integer f : this.deadFeaturesIndices) {
	            	 randomBinary.setIth(f, false);    
	             }
	             
	        	Binary repaired = (Binary) repairSolver.execute(randomBinary);        	
	            product = toProduct(repaired);   
	            
	            if (!isValidProduct(product)) {
	            	product = toProduct(solverIterator.findModel());
//	            	product = SPL_sampler.getInstance().getOneRandomProductFromExisting(product);
	            }
	            
        	} else {
        		
        		int rand = (new Random()).nextInt(3);
            	IOrder order;
//                order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
                 
                if (rand == 0) {
	                 order = new RandomWalkDecorator(new VarOrderHeap(new NegativeLiteralSelectionStrategy()), 1);
	             } else if (rand == 1) {
	                 order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);
	             } else {
	                 order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
	             } 
                
            	((Solver) solverIterator).setOrder(order);   	
            	
	        	if (solverIterator.isSatisfiable()) {
	        		product = toProduct(solverIterator.findModel());
	        	}
        	}        	    
     
        return product;
    }
    /**
     * @param fmFile
     * @param outputDir
     * @param runs
     * @param nbProds
     * @throws Exception
     */
    public void sampling_SAT4J(String fmFile, String outputDir, int runs, int nbProds) 
    		throws Exception {

		String sNbProds = "" + nbProds;
		String fmFileName = new File(fmFile).getName();  
		
		String NSVariant =  "SAT4J"; // sampler: SAT4J 
		System.out.println("Start sampling..., sampler's name:" + NSVariant);        
		
		// for each run 
		for (int i = 0; i < runs; i++) {
			
			System.out.println(NSVariant + "：run " + (i));
			
			List<Product> sampleSet =  new ArrayList<Product>(nbProds); // sampleSet 
			Product prod;    
			
			long startTimeMS = System.currentTimeMillis() ;   
			
			int count = 0;	
			while (count < nbProds) {  	      		  
		      	prod = MAP_test.getInstance().getOneRandomProductSAT4J(); // Use SAT4J        	
 	
		      	if (!sampleSet.contains(prod)) { // 
		      		sampleSet.add(prod);// 
		      		count = count + 1; 
		    	}	 
		      
		      } // while 
			
			long endTimeMS = System.currentTimeMillis() - startTimeMS;
			
			String path = outputDir + NSVariant + "/" + fmFileName +"/Samples/" + sNbProds + "prods/";
			FileUtils.CheckDir(path); 
			
			writeProductsToFile(path + "Products." + i, sampleSet); // write samples
			writeDataToFile(path + "RUNTIME." + i, endTimeMS);// write runtime
			
		} //for  each run 	
		
    } // sampling_SAT4J
    
    /**
     * @param fmFile
     * @param outputDir
     * @param runs
     * @param nbProds
     * @throws Exception
     */
    public void sampling_rSAT4J(String fmFile, String outputDir, int runs, int nbProds) 
    		throws Exception {

		String sNbProds = "" + nbProds;
		String fmFileName = new File(fmFile).getName();  
		
		String NSVariant =  "rSAT4J"; // sampler: rSAT4J 
		System.out.println("Start sampling..., sampler's name:" + NSVariant);        
		
		// for each run 
		for (int i = 0; i < runs; i++) {
			
			System.out.println(NSVariant + "：run " + (i));
			
			List<Product> sampleSet =  new ArrayList<Product>(nbProds); // sampleSet 
			Product prod;    
			
			long startTimeMS = System.currentTimeMillis() ;   
			
			int count = 0;	
			while (count < nbProds) {  	      		  
		      	prod = MAP_test.getInstance().getOneRandomProductRandomlizedSAT4J(); // Use rSAT4J        	
 	
		      	if (!sampleSet.contains(prod)) { // 
		      		sampleSet.add(prod);// 
		      		count = count + 1;  
		    	}	
		      	
		      } // while 
			
			long endTimeMS = System.currentTimeMillis() - startTimeMS;
			
			String path = outputDir + NSVariant + "/" + fmFileName +"/Samples/" + sNbProds + "prods/";
			FileUtils.CheckDir(path); 
			
			writeProductsToFile(path + "Products." + i, sampleSet); // write samples
			writeDataToFile(path + "RUNTIME." + i, endTimeMS);// write runtime
			
		} //for  each run 	
		
    } // sampling_rSAT4J
    
    
    /**
     * @param fmFile
     * @param outputDir
     * @param runs
     * @param nbProds
     * @throws Exception
     */
    public void sampling_rSAT4JForML(String fmFile, String outputDir, int runs, int nbProds) 
    		throws Exception {

		String sNbProds = "" + nbProds;
		String fmFileName = new File(fmFile).getName();  
		
		String basePath = outputDir + "ModelPredictionResults/Distance-Based_Data_Size/SupplementaryWebsite/";
		
		String NSVariant =  "rSAT4J"; // sampler: rSAT4J 
		System.out.println("Start sampling..., sampler's name:" + NSVariant);        
		
		for (int k = 0; k <= 16; k++) { // 17 instances
			// for each run 
			for (int i = 0; i < runs; i++) {
				
				System.out.println(NSVariant + "：run " + (i));
				
				List<Product> sampleSet =  new ArrayList<Product>(nbProds); // sampleSet 
				Product prod;    
				
				long startTimeMS = System.currentTimeMillis() ;   
				
				int count = 0;	
				while (count < nbProds) {  	      		  
			      	prod = MAP_test.getInstance().getOneRandomProductRandomlizedSAT4J(); // Use rSAT4J        	
	 	
			      	if (!sampleSet.contains(prod)) { // 
			      		sampleSet.add(prod);// 
			      		count = count + 1;  
			    	}	
			      	
			      } // while 
				
				long endTimeMS = System.currentTimeMillis() - startTimeMS;
				
				String path = basePath + "/PerformancePredictions/AllExperiments/x264_" + k + "/" + "x264_"+k+"_"+ (i+1) + "/"
						     +  "sampledConfigurations_"+ NSVariant + "_t1.csv";
				
				if (nbProds == 65) 
					path = basePath + "/PerformancePredictions/AllExperiments/x264_" + k + "/" + "x264_"+k+"_"+ (i+1) + "/"
						     +  "sampledConfigurations_"+ NSVariant + "_t2.csv";
				
				if (nbProds == 212) 
					path = basePath + "/PerformancePredictions/AllExperiments/x264_" + k + "/" + "x264_"+k+"_"+ (i+1) + "/"
						     +  "sampledConfigurations_"+ NSVariant + "_t3.csv";
								
				FileUtils.CheckDir(path); 
				
				String xmlReadPath =  basePath + "/MeasuredPerformanceValues/x264_" + k + "/measurements.xml";
				
				writeProductsToFileForModelPrediction(path, sampleSet,xmlReadPath); // write samples
				//writeDataToFile(path + "RUNTIME." + i, endTimeMS);// write runtime
				
			} //for  each run 
			
		} // for k
		
    } // sampling_rSAT4J
    
    /**
     * @param fmFile
     * @param outputDir
     * @param runs
     * @param nbProds
     * @throws Exception
     */
    public void sampling_probSATForML(String fmFile, String outputDir, int runs, int nbProds) 
    		throws Exception {

		String sNbProds = "" + nbProds;
		String fmFileName = new File(fmFile).getName();  
		
		String basePath = outputDir + "ModelPredictionResults/Distance-Based_Data_Size/SupplementaryWebsite/";
		
		String NSVariant =  "ProbSAT"; // sampler: ProbSAT 
		System.out.println("Start sampling..., sampler's name:" + NSVariant);        
		
		for (int k = 0; k <= 16; k++) { // 17 instances
			// for each run 
			for (int i = 0; i < runs; i++) {
				
				System.out.println(NSVariant + "：run " + (i));
				
				List<Product> sampleSet =  new ArrayList<Product>(nbProds); // sampleSet 
				Product prod;    
				
				long startTimeMS = System.currentTimeMillis() ;   
				
				int count = 0;	
				while (count < nbProds) {  	      		  
			      	prod = MAP_test.getInstance().getOneRandomProductProbSAT(); // Use ProbSAT        	
	 	
			      	if (!sampleSet.contains(prod)) { // 
			      		sampleSet.add(prod);// 
			      		count = count + 1;  
			    	}	
			      	
			      } // while 
				
				long endTimeMS = System.currentTimeMillis() - startTimeMS;
				
				String path = basePath + "/PerformancePredictions/AllExperiments/x264_" + k + "/" + "x264_"+k+"_"+ (i+1) + "/"
						     +  "sampledConfigurations_"+ NSVariant + "_t1.csv";
				
				if (nbProds == 65) 
					path = basePath + "/PerformancePredictions/AllExperiments/x264_" + k + "/" + "x264_"+k+"_"+ (i+1) + "/"
						     +  "sampledConfigurations_"+ NSVariant + "_t2.csv";
				
				if (nbProds == 212) 
					path = basePath + "/PerformancePredictions/AllExperiments/x264_" + k + "/" + "x264_"+k+"_"+ (i+1) + "/"
						     +  "sampledConfigurations_"+ NSVariant + "_t3.csv";
								
				FileUtils.CheckDir(path); 
				
				String xmlReadPath =  basePath + "/MeasuredPerformanceValues/x264_" + k + "/measurements.xml";
				
				writeProductsToFileForModelPrediction(path, sampleSet,xmlReadPath); // write samples
				//writeDataToFile(path + "RUNTIME." + i, endTimeMS);// write runtime
				
			} //for  each run 
			
		} // for k
		
    } // sampling_rSAT4J
    
    /**
     * @param fmFile
     * @param outputDir
     * @param runs
     * @param nbProds
     * @throws Exception
     */
    public void sampling_PaDprobSATForML(String fmFile, String outputDir, int runs, int nbProds) 
    		throws Exception {

		String sNbProds = "" + nbProds;
		String fmFileName = new File(fmFile).getName();  
		
		String basePath = outputDir + "ModelPredictionResults/Distance-Based_Data_Size/SupplementaryWebsite/";
		
		String NSVariant =  "PaDprobSAT"; // sampler: PaDprobSAT 
		System.out.println("Start sampling..., sampler's name:" + NSVariant);        
		
		for (int k = 0; k <= 16; k++) { // 17 instances
			// for each run 
			for (int i = 0; i < runs; i++) {
				
				System.out.println(NSVariant + "：run " + (i));
				
				List<Product> sampleSet =  new ArrayList<Product>(nbProds); // sampleSet 
				Product prod;    
				
				long startTimeMS = System.currentTimeMillis() ;   
				
				int count = 0;	
				while (count < nbProds) {  	      		  
			      	prod = MAP_test.getInstance().getOneRandomProductProbSATDiverse(); // Use PaD+ProbSAT        	
	 	
			      	if (!sampleSet.contains(prod)) { // 
			      		sampleSet.add(prod);// 
			      		count = count + 1;  
			    	}	
			      	
			      } // while 
				
				long endTimeMS = System.currentTimeMillis() - startTimeMS;
				
				String path = basePath + "/PerformancePredictions/AllExperiments/x264_" + k + "/" + "x264_"+k+"_"+ (i+1) + "/"
						     +  "sampledConfigurations_"+ NSVariant + "_t1.csv";
				
				if (nbProds == 65) 
					path = basePath + "/PerformancePredictions/AllExperiments/x264_" + k + "/" + "x264_"+k+"_"+ (i+1) + "/"
						     +  "sampledConfigurations_"+ NSVariant + "_t2.csv";
				
				if (nbProds == 212) 
					path = basePath + "/PerformancePredictions/AllExperiments/x264_" + k + "/" + "x264_"+k+"_"+ (i+1) + "/"
						     +  "sampledConfigurations_"+ NSVariant + "_t3.csv";
								
				FileUtils.CheckDir(path); 
				
				String xmlReadPath =  basePath + "/MeasuredPerformanceValues/x264_" + k + "/measurements.xml";
				
				writeProductsToFileForModelPrediction(path, sampleSet,xmlReadPath); // write samples
				//writeDataToFile(path + "RUNTIME." + i, endTimeMS);// write runtime
				
			} //for  each run 
			
		} // for k
		
    } // sampling_rSAT4J
    
    
    /**
     * @param fmFile
     * @param outputDir
     * @param runs
     * @param nbProds
     * @throws Exception
     */
    public void sampling_PaDrSAT4JForML(String fmFile, String outputDir, int runs, int nbProds) 
    		throws Exception {

		String sNbProds = "" + nbProds;
		String fmFileName = new File(fmFile).getName();  
		
		String basePath = outputDir + "ModelPredictionResults/Distance-Based_Data_Size/SupplementaryWebsite/";
		
		String NSVariant =  "PaDrSAT4J"; // sampler: PaDrSAT4J 
		System.out.println("Start sampling..., sampler's name:" + NSVariant);        
		
		for (int k = 0; k <= 16; k++) { // 17 instances
			// for each run 
			for (int i = 0; i < runs; i++) {
				
				System.out.println(NSVariant + "：run " + (i));
				
				List<Product> sampleSet =  new ArrayList<Product>(nbProds); // sampleSet 
				Product prod;    
				
				long startTimeMS = System.currentTimeMillis() ;   
				
				int count = 0;	
				while (count < nbProds) {  	      		  
			      	prod = MAP_test.getInstance().getOneRandomProductRandomlizedSAT4JDiverse(); // Use PaD+rSAT4J        	
	 	
			      	if (!sampleSet.contains(prod)) { // 
			      		sampleSet.add(prod);// 
			      		count = count + 1;  
			    	}	
			      	
			      } // while 
				
				long endTimeMS = System.currentTimeMillis() - startTimeMS;
				
				String path = basePath + "/PerformancePredictions/AllExperiments/x264_" + k + "/" + "x264_"+k+"_"+ (i+1) + "/"
						     +  "sampledConfigurations_"+ NSVariant + "_t1.csv";
				
				if (nbProds == 65) 
					path = basePath + "/PerformancePredictions/AllExperiments/x264_" + k + "/" + "x264_"+k+"_"+ (i+1) + "/"
						     +  "sampledConfigurations_"+ NSVariant + "_t2.csv";
				
				if (nbProds == 212) 
					path = basePath + "/PerformancePredictions/AllExperiments/x264_" + k + "/" + "x264_"+k+"_"+ (i+1) + "/"
						     +  "sampledConfigurations_"+ NSVariant + "_t3.csv";
								
				FileUtils.CheckDir(path); 
				
				String xmlReadPath =  basePath + "/MeasuredPerformanceValues/x264_" + k + "/measurements.xml";
				
				writeProductsToFileForModelPrediction(path, sampleSet,xmlReadPath); // write samples
				//writeDataToFile(path + "RUNTIME." + i, endTimeMS);// write runtime
				
			} //for  each run 
			
		} // for k
		
    } // sampling_rSAT4J
    
    /**
     * @param fmFile
     * @param outputDir
     * @param runs
     * @param nbProds
     * @throws Exception
     */
    public void sampling_PaDSAT4J(String fmFile, String outputDir, int runs, int nbProds) 
    		throws Exception {

		String sNbProds = "" + nbProds;
		String fmFileName = new File(fmFile).getName();  
		
		String NSVariant =  "PaD+SAT4Jnew"; // sampler: PaD+SAT4J 
		
		System.out.println("Start sampling..., sampler's name:" + NSVariant);        
		
		// for each run 
		for (int i = 0; i < runs; i++) {
			
			System.out.println(NSVariant + "：run " + (i));
			
			List<Product> sampleSet =  new ArrayList<Product>(nbProds); // sampleSet 
			Product prod;    
			
			long startTimeMS = System.currentTimeMillis() ;   
			
			int count = 0;	
			while (count < nbProds) {  
				
				prod = MAP_test.getInstance().getOneRandomProductSAT4JDiverse();	
				
		      	if (!sampleSet.contains(prod)) { // 
		      		sampleSet.add(prod);// 
		      		count = count + 1;  
		    	}	
		      	
		      } // while 
			
			long endTimeMS = System.currentTimeMillis() - startTimeMS;
			
			String path = outputDir + NSVariant + "/" + fmFileName +"/Samples/" + sNbProds + "prods/";
			FileUtils.CheckDir(path); 
			
			writeProductsToFile(path + "Products." + i, sampleSet); // write samples
			writeDataToFile(path + "RUNTIME." + i, endTimeMS);// write runtime
			
		} //for  each run 	
		
    } // sampling_PaDSAT4J
    
    public  Product  getOneRandomProductSAT4JDiverse() throws Exception {
		// Generate a random binary to ensure that all features are considered
		Binary randomBinary = new Binary(numFeatures,PseudoRandom.randDouble(-0.05,1.05));// PaD
		
	    for (Integer f : mandatoryFeaturesIndices) { 
	    	randomBinary.setIth(f, true);               	
	     }
	
	     for (Integer f : deadFeaturesIndices) {
	    	 randomBinary.setIth(f, false);    
	     }
	            
	     //----------------------------------------------------------------
	
     	HashSet<Integer> blacklist = new HashSet<Integer>();  
	    HashSet<Integer> backupBlacklist = new HashSet<Integer>();  

        int violated = numViolatedConstraints(randomBinary, blacklist, backupBlacklist);      		       		      
        
        if (violated > 0) {
            IVecInt iv = new VecInt();

            for (int j : featureIndicesAllowedFlip) {
                int feat = j + 1;
            
                if (!blacklist.contains(feat)) {
                    iv.push(randomBinary.bits_.get(j) ? feat : -feat);
                }
         
            }       
	      //----------------------------------------------------------------        
	            
	       if (solverIterator.isSatisfiable()) {        	
	        	
	        	IOrder order;       
	            order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);	  	            

		       	((Solver) solverIterator).setOrder(order); 		
		       	
	           int[] partialProd = solverIterator.findModel(iv); // partialProd contains only variables in CNF constraints
           
	           // Have another call
	           if (partialProd == null) {
	               System.out.println("not not satisfiable()");
	                        
	               iv.clear();
	               
	               for (int j : featureIndicesAllowedFlip) {
	                      int feat = j + 1;
	                  
	                      if (!backupBlacklist.contains(feat)) {
	                          iv.push(randomBinary.bits_.get(j) ? feat : -feat);
	                      }
	               
	                  }	               
	              
	               partialProd = solverIterator.findModel(iv);
	               
	           }
	           
	           
	           for (int j = 0; j < partialProd.length; j++) {
	               int feat = partialProd[j];
	               
	               int posFeat = feat > 0 ? feat : -feat;
	
	               if (posFeat > 0) {
	               	randomBinary.setIth(posFeat - 1,feat > 0);
	               }
	           }// for	           
	       } 
	       else {//if
	          	System.out.println("solverIterator is not satisfiable()");
	       }   
      }     
       
      Product product  = toProduct(randomBinary);

      return product;            
 }
    /**
     * @param fmFile
     * @param outputDir
     * @param runs
     * @param nbProds
     * @throws Exception
     */
    public void sampling_PaDrSAT4J(String fmFile, String outputDir, int runs, int nbProds) 
    		throws Exception {

		String sNbProds = "" + nbProds;
		String fmFileName = new File(fmFile).getName();  
		
		String NSVariant =  "PaD+rSAT4J"; // sampler: PaD+rSAT4J 
		
		System.out.println("Start sampling..., sampler's name:" + NSVariant);        
		
		// for each run 
		for (int i = 0; i < runs; i++) {
			
			System.out.println(NSVariant + "：run " + (i));
			
			List<Product> sampleSet =  new ArrayList<Product>(nbProds); // sampleSet 
			Product prod;    
			
			long startTimeMS = System.currentTimeMillis() ;   
			
			int count = 0;	
			while (count < nbProds) {  
				
				prod = MAP_test.getInstance().getOneRandomProductRandomlizedSAT4JDiverse();	
				
		      	if (!sampleSet.contains(prod)) { // 
		      		sampleSet.add(prod);// 
		      		count = count + 1;  
		    	}	
		      	
		      } // while 
			
			long endTimeMS = System.currentTimeMillis() - startTimeMS;
			
			String path = outputDir + NSVariant + "/" + fmFileName +"/Samples/" + sNbProds + "prods/";
			FileUtils.CheckDir(path); 
			
			writeProductsToFile(path + "Products." + i, sampleSet); // write samples
			writeDataToFile(path + "RUNTIME." + i, endTimeMS);// write runtime
			
		} //for  each run 	
		
    } // sampling_PaDrSAT4J
    
    
    /**
     * @param fmFile
     * @param outputDir
     * @param runs
     * @param nbProds
     * @throws Exception
     */
    public void sampling_PaDrSAT4JCheckingUnchangedVars(String fmFile, String outputDir, int runs, int nbProds) 
    		throws Exception {

		String sNbProds = "" + nbProds;
		String fmFileName = new File(fmFile).getName();  
		
		String NSVariant =  "CheckingUnchangedVars"; // sampler:  
		
		System.out.println("Start sampling..., sampler's name:" + NSVariant);        
		
		// for each run 
		for (int i = 0; i < runs; i++) {
			
			System.out.println(NSVariant + "：run " + (i));
			
			List<Product> sampleSet =  new ArrayList<Product>(nbProds); // sampleSet 
			Product prod;    
			Binary randomBinary = null;
					
			long startTimeMS = System.currentTimeMillis() ;   
			
			int count = 0;
			int unchangedVars = 0;
			int originalUnchanged = 0;
			
			while (count < nbProds) {  		
				
				//--------------------------before--------------------------------
				randomBinary = new Binary(numFeatures,PseudoRandom.randDouble(-0.05,1.05));// PaD
				
			    for (Integer f : mandatoryFeaturesIndices) { 
			    	randomBinary.setIth(f, true);               	
			     }
			
			     for (Integer f : deadFeaturesIndices) {
			    	 randomBinary.setIth(f, false);    
			     }
			            
			     //----------------------------------------------------------------
			    HashSet<Integer> blacklist = new HashSet<Integer>();  
			    HashSet<Integer> backupBlacklist = new HashSet<Integer>();  
		
		        int violated = numViolatedConstraints(randomBinary, blacklist, backupBlacklist);      		       		      
		        
		        if (violated > 0) {
		            IVecInt iv = new VecInt();

		            for (int j : featureIndicesAllowedFlip) {
		                int feat = j + 1;
		            
		                if (!blacklist.contains(feat)) {
		                    iv.push(randomBinary.bits_.get(j) ? feat : -feat);
		                }
		         
		            }
		        
		            unchangedVars = unchangedVars + iv.size();
		            
		            
		            //Compute original unchanged
		            for (int j : featureIndicesAllowedFlip) {
		                int feat = j + 1;
		            
		                if (!backupBlacklist.contains(feat)) {
		                	originalUnchanged++;
		                }
		         
		            }
		            
			    //-------------------------------------------------------------        
			            
			       if (solverIterator.isSatisfiable()) {        	
			        	int rand = (new Random()).nextInt(3);
			        	IOrder order;
			             if (rand == 0) {
			                 order = new RandomWalkDecorator(new VarOrderHeap(new NegativeLiteralSelectionStrategy()), 1);
			             } else if (rand == 1) {
			                 order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);
			             } else {
			                 order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
			             } 
				        		        
				       ((Solver) solverIterator).setOrder(order); 		
				       	
			           int[] partialProd = solverIterator.findModel(iv); // partialProd contains only variables in CNF constraints
		            
			           if (partialProd == null) {
//			        		System.out.println("not satisfiable()");
			        		unchangedVars = unchangedVars - iv.size();			        		
			        		iv.clear();
			        		
			        		for (int j : featureIndicesAllowedFlip) {
				                int feat = j + 1;
				            
				                if (!backupBlacklist.contains(feat)) {
				                    iv.push(randomBinary.bits_.get(j) ? feat : -feat);
				                }
				         
				            }
			        		
			        		unchangedVars = unchangedVars + iv.size();		
			        		partialProd = solverIterator.findModel(iv);
			        		
			           }
			           
			           for (int j = 0; j < partialProd.length; j++) {
			               int feat = partialProd[j];
			               
			               int posFeat = feat > 0 ? feat : -feat;
			
			               if (posFeat > 0) {
			               	randomBinary.setIth(posFeat - 1,feat > 0);
			               }
			           }// for	           
			       } 
			       else {//if
			          	System.out.println("solverIterator is not satisfiable()");
			       }   
		       }     
		        
		       prod  = toProduct(randomBinary);

				//--------------------------end-----------------------------------
				
		      	if (!sampleSet.contains(prod)) { // 
		      		sampleSet.add(prod);// 
		      		count = count + 1;  
		    	}	
		      	
		      } // while 
			
			double unchangedRatio = (double)unchangedVars/nbProds/featureIndicesAllowedFlip.size();
			double originalUnchangedRatio = (double)originalUnchanged/nbProds/featureIndicesAllowedFlip.size();
			System.out.println("Unchanged vars = " + unchangedRatio);
			System.out.println("Original Unchanged vars = " + originalUnchangedRatio);
			
			long endTimeMS = System.currentTimeMillis() - startTimeMS;
			
			String path = outputDir + NSVariant + "/" + fmFileName +"/Samples/" + sNbProds + "prods/";
			FileUtils.CheckDir(path); 
			
			writeProductsToFile(path + "Products." + i, sampleSet); // write samples
			writeDataToFile(path + "UnchangedRatio." + i, unchangedRatio);// ratio,percent
			writeDataToFile(path + "OriginalUnchangedRatio." + i, originalUnchangedRatio);// ratio,percent
			writeDataToFile(path + "RUNTIME." + i, endTimeMS);// runtime
			
		} //for  each run 	
		
    } // sampling_PaDrSAT4J
    
    
    /**
     * @param fmFile
     * @param outputDir
     * @param runs
     * @param nbProds
     * @throws Exception
     */
    public void sampling_ProbSAT(String fmFile, String outputDir, int runs, int nbProds) 
    		throws Exception {

		String sNbProds = "" + nbProds;
		String fmFileName = new File(fmFile).getName();  
		
		String NSVariant =  "ProbSAT"; // sampler: ProbSATWileRepair: continue repairing if not feasible
		
		System.out.println("Start sampling..., sampler's name:" + NSVariant);        
		
		// for each run 
		for (int i = 0; i < runs; i++) {
			
			System.out.println(NSVariant + "：run " + (i));
			
			List<Product> sampleSet =  new ArrayList<Product>(nbProds); // sampleSet 
			Product prod;    
			
			long startTimeMS = System.currentTimeMillis() ;   
			
			int count = 0;	
			while (count < nbProds) {  
				
				prod = MAP_test.getInstance().getOneRandomProductProbSAT();// probSAT
				
		      	if (!sampleSet.contains(prod)) { // 
		      		sampleSet.add(prod);// 
		      		count = count + 1;  
		    	}	
		      	
		      } // while 
			
			long endTimeMS = System.currentTimeMillis() - startTimeMS;
			
			String path = outputDir + NSVariant + "/" + fmFileName +"/Samples/" + sNbProds + "prods/";
			FileUtils.CheckDir(path); 
			
			writeProductsToFile(path + "Products." + i, sampleSet); // write samples
			writeDataToFile(path + "RUNTIME." + i, endTimeMS);// write runtime
			
		} //for  each run 	
		
    } // sampling_ProbSAT
    
    /**
     * @param fmFile
     * @param outputDir
     * @param runs
     * @param nbProds
     * @throws Exception
     */
    public void sampling_PaDProbSAT(String fmFile, String outputDir, int runs, int nbProds) 
    		throws Exception {

		String sNbProds = "" + nbProds;
		String fmFileName = new File(fmFile).getName();  
		
		String NSVariant =  "PaD+ProbSAT"; // sampler: PaD+ProbSAT
		
		System.out.println("Start sampling..., sampler's name:" + NSVariant);        
		
		// for each run 
		for (int i = 0; i < runs; i++) {
			
			System.out.println(NSVariant + "：run " + (i));
			
			List<Product> sampleSet =  new ArrayList<Product>(nbProds); // sampleSet 
			Product prod;    
			
			long startTimeMS = System.currentTimeMillis() ;   
			
			int count = 0;	
			while (count < nbProds) {  
				
				prod = MAP_test.getInstance().getOneRandomProductProbSATDiverse();// PaD+ProbSAT
				
		      	if (!sampleSet.contains(prod)) { // 
		      		sampleSet.add(prod);// 
		      		count = count + 1;  
		    	}	
		      	
		      } // while 
			
			long endTimeMS = System.currentTimeMillis() - startTimeMS;
			
			String path = outputDir + NSVariant + "/" + fmFileName +"/Samples/" + sNbProds + "prods/";
			FileUtils.CheckDir(path); 
			
			writeProductsToFile(path + "Products." + i, sampleSet); // write samples
			writeDataToFile(path + "RUNTIME." + i, endTimeMS);// write runtime
			
		} //for  each run 	
		
    } // sampling_ProbSAT
    
   
    /**
     * Count the number of files in a dir
     * @param path
     * @return
     */
    public int getFileNum(String path) {
    	int num = 0;
		File file = new File(path);
		if (file.exists()) {
			File[] f = file.listFiles();
			for (File fs : f) {
				if (fs.isFile()) {
//					System.out.println(fs.getName());
					num++;
				} 
//				else {
//					num = num + getFileNum(fs.getAbsolutePath());
//				} 
			}
		}
 
		return num;
	}
          

    public FeatureModel loadFeatureModel(String fmFile) {
        return new XMLFeatureModel(fmFile, XMLFeatureModel.USE_VARIABLE_NAME_AS_ID);
    }

    public List<Product> getUnpredictableProducts(int count) throws Exception {
        List<Product> products = new ArrayList<Product>(count);

        while (products.size() < count) {

            try {
                if (solverIterator.isSatisfiable()) {
                	
                	//----------------------------Set order---------------------------------
//                	int rand = (new Random()).nextInt(3);
//                	IOrder order;
//                     if (rand == 0) {
//                         order = new RandomWalkDecorator(new VarOrderHeap(new NegativeLiteralSelectionStrategy()), 1);
//                     } else if (rand == 1) {
//                         order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);
//                     } else {
                         order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
//                     }
                     
                	((Solver) solverIterator).setOrder(order); 
                	//----------------------------Set order end ---------------------------------
                	                	
                    Product product = toProduct(solverIterator.findModel());

                    int selected = 0;
                 	
                 	for (Integer i : product) {
                 		if (i > 0) selected++;
                 	}
                 	
//                 	System.out.println("# Selected featues" + selected);
                 	
                    if (!products.contains(product)) {
                        products.add(product);
                    }

                } else {
                	System.out.println("Reinitialize solvers");
                    if (!dimacs) {
                        reasonerSAT.init();
                        if (!predictable) {
                            ((Solver) reasonerSAT.getSolver()).setOrder(order);
                        }
                        solverIterator = new ModelIterator(reasonerSAT.getSolver());
                        solverIterator.setTimeoutMs(iteratorTimeout);

                    } else {
                        dimacsSolver = SolverFactory.instance().createSolverByName("MiniSAT");
                        dimacsSolver.setTimeout(SATtimeout);
                        DimacsReader dr = new DimacsReader(dimacsSolver);
                        dr.parseInstance(new FileReader(dimacsFile));
                        if (!predictable) {
                            ((Solver) dimacsSolver).setOrder(order);
                        }
                        solverIterator = new ModelIterator(dimacsSolver);
                        solverIterator.setTimeoutMs(iteratorTimeout);
                    }
                }
            } catch (TimeoutException e) {
            }
        }
        return products;
    }

   
        
    
    public List<Product> getUnpredictableProductsSetOrderDuringRun(int count) throws Exception {
        List<Product> products = new ArrayList<Product>(count);

        while (products.size() < count) {

            try {
                if (solverIterator.isSatisfiable()) {
                	
                	// Reset orders, should be kept
                	int rand = (new Random()).nextInt(3);
                	IOrder order;
                     if (rand == 0) {
                         order = new RandomWalkDecorator(new VarOrderHeap(new NegativeLiteralSelectionStrategy()), 1);
                     } else if (rand == 1) {
                         order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);
                     } else {
                         order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
                     }
                     
                	((Solver) solverIterator).setOrder(order); 
                	
                    Product product = toProduct(solverIterator.findModel());

                    int selected = 0;
                 	
                 	for (Integer i : product) {
                 		if (i > 0) selected++;
                 	}                	
                 	                 	
                    if (!products.contains(product)) {
                        products.add(product);
                        //System.out.println("# Selected featues" + selected);
//                        System.out.println( selected);
                    }

                } else {
                	System.out.println("Reinitialize solvers");
                    if (!dimacs) {
                        reasonerSAT.init();
                        if (!predictable) {
                            ((Solver) reasonerSAT.getSolver()).setOrder(order);
                        }
                        solverIterator = new ModelIterator(reasonerSAT.getSolver());
                        solverIterator.setTimeoutMs(iteratorTimeout);

                    } else {
                        dimacsSolver = SolverFactory.instance().createSolverByName("MiniSAT");
                        dimacsSolver.setTimeout(SATtimeout);
                        DimacsReader dr = new DimacsReader(dimacsSolver);
                        dr.parseInstance(new FileReader(dimacsFile));
                        if (!predictable) {
                            ((Solver) dimacsSolver).setOrder(order);
                        }
                        solverIterator = new ModelIterator(dimacsSolver);
                        solverIterator.setTimeoutMs(iteratorTimeout);
                    }
                }
            } catch (TimeoutException e) {
            }
        }
        return products;
    }
    
   public int selectedFeature (Product product) {
	   int selected = 0;
    	
    	for (Integer i : product) {
    		if (i>0) selected++;
    	}
    	return selected;
    	
   }
	   
    public int numViolatedConstraints(Binary b, HashSet<Integer> blacklist) {

        //IVecInt v = bitSetToVecInt(b);
    	List<List<Integer>> constraints =  featureModelConstraints;
    	HashSet<Integer> temp = new HashSet<Integer>();
    	
        int s = 0;
        for (List<Integer> constraint : constraints) {
            boolean sat = false;

            for (Integer i : constraint) {
                int abs = (i < 0) ? -i : i;
                boolean sign = i > 0;
                if (b.getIth(abs - 1) == sign) {
                    sat = true;
                } else {     
                	temp.add(abs);
                }
            }
            
            if (!sat) { // not satisfied,
                s++;
                blacklist.addAll(temp);
                temp.clear();
            } 

        } // for

        return s;
    }
    
    
    public int numViolatedConstraints(Binary b, HashSet<Integer> blacklist,HashSet<Integer> backupBlacklist) {

        //IVecInt v = bitSetToVecInt(b);
    	List<List<Integer>> constraints =  featureModelConstraints;
    	HashSet<Integer> temp = new HashSet<Integer>();
    	
        int s = 0;
        
        for (List<Integer> constraint : constraints) {
            boolean sat = false;

            for (Integer i : constraint) {
                int abs = (i < 0) ? -i : i;
                boolean sign = i > 0;
                if (b.getIth(abs - 1) == sign) {
                    sat = true;
                } else {     
                	temp.add(abs);
                	backupBlacklist.add(abs);// directly add it 
                }
            }
            
            if (!sat) { // not satisfied,
                s++;
                blacklist.addAll(temp);
                temp.clear();
            } 

        } // for

        return s;
    }
    
    
    public int numViolatedConstraints(Binary b, HashSet<Integer> blacklist1, ArrayList<Integer> blacklist2) {

        //IVecInt v = bitSetToVecInt(b);
    	List<List<Integer>> constraints =  featureModelConstraints;

        int s = 0;
        for (List<Integer> constraint : constraints) {
            boolean sat = false;

            for (Integer i : constraint) {
                int abs = (i < 0) ? -i : i;
                boolean sign = i > 0;
                if (b.getIth(abs - 1) == sign) {
                    sat = true;
                } else {
                	if (constraint.size() == 1)
                		blacklist1.add(abs);
                	else
                		blacklist2.add(abs);
                }
            }
            if (!sat) {
                s++;
            }

        }

        return s;
    }
    
    
    
    public Binary randomProductAssume(Binary bin) {
    	
  		HashSet<Integer> blacklist = new HashSet<Integer>();  	   
  	       
        int violated = numViolatedConstraints(bin, blacklist);
        
        if (violated > 0) {
            IVecInt iv = new VecInt();

            for (int j : featureIndicesAllowedFlip) {
                int feat = j + 1;

                if (!blacklist.contains(feat)) {
                    iv.push(bin.bits_.get(j) ? feat : -feat);
                }

            }

            boolean[] prod = randomProductFromSolution(iv);
            
            for (int j = 0; j < prod.length; j++) {
                bin.setIth(j, prod[j]);
            }
        }
  	    
        return bin;
      }
    
    
    public boolean[] randomProductFromSolution(IVecInt ivi) {        

        boolean[] prod = new boolean[numFeatures];
        for (int i = 0; i < prod.length; i++) {
            prod[i] = randomGenerator.nextBoolean();
        }

        for (Integer f : this.mandatoryFeaturesIndices) {
        	prod[f] = true;
        }

        for (Integer f : this.deadFeaturesIndices) {
        	prod[f] = false;
        }
                

        try {    
        	
//        	int rand = (new Random()).nextInt(3);
//        	IOrder order;
//             if (rand == 0) {
//                 order = new RandomWalkDecorator(new VarOrderHeap(new NegativeLiteralSelectionStrategy()), 1);
//             } else if (rand == 1) {
//                 order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);
//             } else {
//                 order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
//             }
             
        	((Solver) solverIterator).setOrder(order);
        	
            if (solverIterator.isSatisfiable()) {
                int[] i = solverIterator.findModel(ivi);
                for (int j = 0; j < i.length; j++) {
                    int feat = i[j];

                    int posFeat = feat > 0 ? feat : -feat;

                    if (posFeat > 0) {
                        prod[posFeat - 1] = feat > 0;
                    }

                }

            } 
            

        } catch (Exception e) {
        	
        	for (Integer f : this.mandatoryFeaturesIndices) {
              	prod[f] = true;
              }

              for (Integer f : this.deadFeaturesIndices) {
              	prod[f] = false;
              }
//              e.printStackTrace();
      	    return prod;
        }

        return prod;
    }
    
    /**
     * Get random products using Random + Repair
     * @param count
     * @return
     * @throws Exception
     */
    public List<Product> getRandomProducts(int count,Map<Integer, String> featuresMap, List<Integer> featuresList,double p) throws Exception {
        List<Product> products = new ArrayList<Product>(count);

        
        while (products.size() < count) {      
        	Product product = null;

        	if (randomGenerator.nextDouble() <= p) {
     
	        	Binary randomBinary = new Binary(numFeatures); // 随机产生一个二进制串   	        	

	            for (Integer f : this.mandatoryFeaturesIndices) {
	            	randomBinary.setIth(f, true);               	
	             }

	             for (Integer f : this.deadFeaturesIndices) {
	            	 randomBinary.setIth(f, false);    
	             }
	             
	        	Binary repaired = (Binary) repairSolver.execute(randomBinary);        	
	            product = toProduct(repaired);   
	            
	            if (!isValidProduct(product, featuresMap, featuresList)) { // 不可行
	        	   product = toProduct(solverIterator.findModel());
	            }
	        	   
        	} else {
        	
	        	if (solverIterator.isSatisfiable()) {
	        		product = toProduct(solverIterator.findModel());
	        	}
        	}
        	
            if (!products.contains(product) ) { 
                products.add(product);
            } 
            
        }
     
        return products;
    }
    
    
    /**
     * Get random products using Random + Repair
     * @param count
     * @return
     * @throws Exception
     */
    public List<Product> getRandomProductsAssume(int count,Map<Integer, String> featuresMap, List<Integer> featuresList,double p) throws Exception {
        List<Product> products = new ArrayList<Product>(count);

        while (products.size() < count) {      
        	Product product = null;

        	if (randomGenerator.nextDouble() <= p) {
     
	        	Binary randomBinary = new Binary(numFeatures); // 随机产生一个二进制串   	        	

	            for (Integer f : this.mandatoryFeaturesIndices) {
	            	randomBinary.setIth(f, true);               	
	             }

	             for (Integer f : this.deadFeaturesIndices) {
	            	 randomBinary.setIth(f, false);    
	             }
	             
	        	Binary repaired = randomProductAssume(randomBinary);        	
	            product = toProduct(repaired);   
	       
	            
	           if (!isValidProduct(product, featuresMap, featuresList)) { // 不可行
	        	   product = toProduct(solverIterator.findModel());
	           }
	        	   
        	} else {
        	
	        	if (solverIterator.isSatisfiable()) {
	        		product = toProduct(solverIterator.findModel());
	        	}
        	}
        	
            if (!products.contains(product) ) { 
                products.add(product);
            } 
            
        }
     
        return products;
    }
    /**
     * Get random products using Random + Repair
     * @param count
     * @return
     * @throws Exception
     */
    public Product getRandomProducts(double p) throws Exception {
              
        	Product product = null;
        	
        	if (randomGenerator.nextDouble() <= p) {
//        		System.out.println("Repair");
	        	 	             
	            Binary randomBinary = new Binary(numFeatures); // 随机产生一个二进制串   	
	            for (Integer f : this.mandatoryFeaturesIndices) {
	            	randomBinary.setIth(f, true);               	
	             }

	             for (Integer f : this.deadFeaturesIndices) {
	            	 randomBinary.setIth(f, false);    
	             }
	             
	        	Binary repaired = (Binary) repairSolver.execute(randomBinary);        	
	            product = toProduct(repaired);   
	            
	            if (!isValidProduct(product)) {
	            	product = toProduct(solverIterator.findModel());
	            }
	            
        	} else {
        		
        		// Reset orders, should be kept
            	int rand = (new Random()).nextInt(3);
            	IOrder order;
                 if (rand == 0) {
                     order = new RandomWalkDecorator(new VarOrderHeap(new NegativeLiteralSelectionStrategy()), 1);
                 } else if (rand == 1) {
                     order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);
                 } else {
                     order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
                 }
                 
            	((Solver) solverIterator).setOrder(order); 
            	
            	
	        	if (solverIterator.isSatisfiable()) {
	        		product = toProduct(solverIterator.findModel());
	        	}
        	}        	    
     
        return product;
    }

    /**
     * Get random products using Random + Repair
     * @param count
     * @return
     * @throws Exception
     */
    public Product repairProducts(Product prod, double p) throws Exception {
              
        	Product product = null;
        	
        	if (randomGenerator.nextDouble() < p) {        	 	             
           
	            Binary randomBinary = new Binary(numFeatures); // 随机产生一个二进制串  
	            for (Integer f : this.mandatoryFeaturesIndices) {
	            	randomBinary.setIth(f, true);               	
	             }

	             for (Integer f : this.deadFeaturesIndices) {
	            	 randomBinary.setIth(f, false);    
	             }
	             
	        	Binary repaired = (Binary) repairSolver.execute(randomBinary);     // ProbSAT	             
//	        	Binary repaired = randomProductAssume(randomBinary);   // SAT4J
	            
	            product = toProduct(repaired);   

	            if (!isValidProduct(product)) {
//	            	System.out.println("Invalid after repairing");
	            	
//	            	int rand = (new Random()).nextInt(3);
//                	IOrder order;
//                     if (rand == 0) {
//                         order = new RandomWalkDecorator(new VarOrderHeap(new NegativeLiteralSelectionStrategy()), 1);
//                     } else if (rand == 1) {
//                         order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);
//                     } else {
//                         order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
//                     }
//                    
//	            	((Solver) solverIterator).setOrder(order); 
//	            	product = toProduct(solverIterator.findModel());
	            	
//	            	product = toProduct(randomProductAssume(repaired));   // SAT4J
	            	product = getOneRandomProductSAT4J();   // SAT4J
	            }
	            
        	} else {
	        	if (solverIterator.isSatisfiable()) {
	        		
//	        		int rand = (new Random()).nextInt(3);
//                	IOrder order;
//                     if (rand == 0) {
//                         order = new RandomWalkDecorator(new VarOrderHeap(new NegativeLiteralSelectionStrategy()), 1);
//                     } else if (rand == 1) {
//                         order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);
//                     } else {
//                         order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
//                     }
//                     
//	        		((Solver) solverIterator).setOrder(order); 
//	        		product = toProduct(solverIterator.findModel());	        		
	        		
	        		product = getOneRandomProductSAT4J(); 
	        	}
        	}        	                             
     
        return product;
    }

    
    /**
     * Get random products using Random + Repair
     * @param count
     * @return
     * @throws Exception
     */
    public Product getOneRandomProductProbSAT() throws Exception {
              
        	Product product = null;        	       	
	        	 	             
            Binary randomBinary =  new Binary(numFeatures) ;  	            
            
            for (Integer f : this.mandatoryFeaturesIndices) {
            	randomBinary.setIth(f, true);               	
             }

             for (Integer f : this.deadFeaturesIndices) {
            	 randomBinary.setIth(f, false);    
             }
             
        	Binary repaired = (Binary) repairSolver.execute(randomBinary);     // ProbSAT	             
            
            product = toProduct(repaired);   

            if (!isValidProduct(product)) {
            	product = getOneRandomProductRandomlizedSAT4J();   // SAT4J            	
//            	repaired = (Binary) repairSolver.execute(repaired);  
//                product = toProduct(repaired); 
//            	System.out.println("------------------Repair by rSAT4J----------------");
            }	             	                             
     
        return product;
    }
    
    
    /**
     * Get random products using Random + Repair
     * @param count
     * @return
     * @throws Exception
     */
    public Product getOneRandomProductProbSATDiverse() throws Exception {
              
        	Product product = null;        	       	
	        	 	             
        	Binary randomBinary = new Binary(numFeatures,PseudoRandom.randDouble(-0.05,1.05));//PaD             
            
            for (Integer f : this.mandatoryFeaturesIndices) {
            	randomBinary.setIth(f, true);               	
             }

             for (Integer f : this.deadFeaturesIndices) {
            	 randomBinary.setIth(f, false);    
             }
             
        	Binary repaired = (Binary) repairSolver.execute(randomBinary);     // ProbSAT	             
            
            product = toProduct(repaired);   

            if (!isValidProduct(product)) {//            
//            	repaired = (Binary) repairSolver.execute(repaired);  
//                product = toProduct(repaired);   
//                System.out.println("********Repair by rSAT4J****");
            	product = getOneRandomProductRandomlizedSAT4J();   // SAT4J
//            	System.out.println("getOneRandomProductProbSATDiverse: calling getOneRandomProductRandomlizedSAT4J");
           }
     
        return product;
    }
    
    public  Product  getOneRandomProductRandomlizedSAT4J() throws Exception {
		// Generate a random binary to ensure that all features are considered
		Binary randomBinary = new Binary(numFeatures);  // random assignments
		
	    for (Integer f : mandatoryFeaturesIndices) { 
	    	randomBinary.setIth(f, true);               	
	     }
	
	     for (Integer f : deadFeaturesIndices) {
	    	 randomBinary.setIth(f, false);    
	     }
	                  
        if (solverIterator.isSatisfiable()) {
        	int rand = (new Random()).nextInt(3);
        	IOrder order;
             if (rand == 0) {
                 order = new RandomWalkDecorator(new VarOrderHeap(new NegativeLiteralSelectionStrategy()), 1);
             } else if (rand == 1) {
                 order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);
             } else {
                 order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
             }
             
        	((Solver) solverIterator).setOrder(order); 
	
            int[] partialProd = solverIterator.findModel(); // partialProd contains only variables in CNF constraints
             
            for (int j = 0; j < partialProd.length; j++) {
                int feat = partialProd[j];
                
                int posFeat = feat > 0 ? feat : -feat;

                if (posFeat > 0) {
                	randomBinary.setIth(posFeat - 1,feat > 0);
                }
            }// for
            
        } else {//if
        	System.out.println("solverIterator is not satisfiable()");
        }   
        
        Product product  = toProduct(randomBinary);
  
        return product;            
   }
    
    
   public  Product  getOneRandomProductSAT4J() throws Exception {
		// Generate a random binary to ensure that all features are considered
		Binary randomBinary = new Binary(numFeatures); 
		
	    for (Integer f : mandatoryFeaturesIndices) { 
	    	randomBinary.setIth(f, true);               	
	     }
	
	     for (Integer f : deadFeaturesIndices) {
	    	 randomBinary.setIth(f, false);    
	     }
	                  
        if (solverIterator.isSatisfiable()) {    
        	
        	IOrder order;
            order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);     
        	((Solver) solverIterator).setOrder(order); 
	
            int[] partialProd = solverIterator.findModel(); // partialProd contains only variables in CNF constraints
             
            for (int j = 0; j < partialProd.length; j++) {
                int feat = partialProd[j];
                
                int posFeat = feat > 0 ? feat : -feat;

                if (posFeat > 0) {
                	randomBinary.setIth(posFeat - 1,feat > 0);
                }
            }// for
            
        } else {//if
        	System.out.println("solverIterator is not satisfiable()");
        }   
        
        Product product  = toProduct(randomBinary);
  
        return product;            
   }
    
   public  Product  getOneRandomProductRandomlizedSAT4JDiverse() throws Exception {
		// Generate a random binary to ensure that all features are considered
		Binary randomBinary = new Binary(numFeatures,PseudoRandom.randDouble(-0.05,1.05));// PaD
		
	    for (Integer f : mandatoryFeaturesIndices) { 
	    	randomBinary.setIth(f, true);               	
	     }
	
	     for (Integer f : deadFeaturesIndices) {
	    	 randomBinary.setIth(f, false);    
	     }
	            
	     //----------------------------------------------------------------
	     HashSet<Integer> blacklist = new HashSet<Integer>();  
         HashSet<Integer> backupBlacklist = new HashSet<Integer>();  
  
         int violated = numViolatedConstraints(randomBinary, blacklist, backupBlacklist);                       
          
         if (violated > 0) {
              IVecInt iv = new VecInt();

              for (int j : featureIndicesAllowedFlip) {
                  int feat = j + 1;
              
                  if (!blacklist.contains(feat)) {
                      iv.push(randomBinary.bits_.get(j) ? feat : -feat);
                  }
           
              }
    
	    //-------------------------------------------------------------        
	            
	       if (solverIterator.isSatisfiable()) {        	
	        	int rand = (new Random()).nextInt(3);
	        	IOrder order;
	             if (rand == 0) {
	                 order = new RandomWalkDecorator(new VarOrderHeap(new NegativeLiteralSelectionStrategy()), 1);
	             } else if (rand == 1) {
	                 order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);
	             } else {
	                 order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
	             } 
		        		        
		       ((Solver) solverIterator).setOrder(order); 		
		       	
	           int[] partialProd = solverIterator.findModel(iv); // partialProd contains only variables in CNF constraints
            
	           // Have another call
	           if (partialProd == null) {
	               System.out.println("not not satisfiable()");
	                        
	               iv.clear();
	               
	               for (int j : featureIndicesAllowedFlip) {
	                      int feat = j + 1;
	                  
	                      if (!backupBlacklist.contains(feat)) {
	                          iv.push(randomBinary.bits_.get(j) ? feat : -feat);
	                      }
	               
	                  }	               
	              
	               partialProd = solverIterator.findModel(iv);
	               
	           }
	           
	           for (int j = 0; j < partialProd.length; j++) {
	               int feat = partialProd[j];
	               
	               int posFeat = feat > 0 ? feat : -feat;
	
	               if (posFeat > 0) {
	               	randomBinary.setIth(posFeat - 1,feat > 0);
	               }
	           }// for	           
	       } 
	       else {//if
	          	System.out.println("solverIterator is not satisfiable()");
	       }   
       }     
        
       Product product  = toProduct(randomBinary);

       return product;            
  }
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
        
    
    public Product getUnpredictableProduct(Product startProduct) throws Exception {
        Product product = null;
        while (product == null) {
            try {
                if (solverIterator.isSatisfiable()) {
//                	int rand = (new Random()).nextInt(3);
//                	IOrder order;
//                     if (rand == 0) {
//                         order = new RandomWalkDecorator(new VarOrderHeap(new NegativeLiteralSelectionStrategy()), 1);
//                     } else if (rand == 1) {
//                         order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);
//                     } else {
//                         order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
//                     }
//                     
//                	((Solver) solverIterator).setOrder(order);                	         
//                	System.out.println("# Selected featues before " + this.selectedFeature(startProduct));
//                    product = toProduct(solverIterator.findModel(productToIntVec(startProduct)));    
                    product = toProduct(solverIterator.findModel());     
//                    System.out.println("# Selected featues after " + this.selectedFeature(product));
                    
                } else {
                	System.out.println("reinitialize solvers in getUnpredictableProduct()");
                    if (!dimacs) {
                        reasonerSAT.init();
                        if (!predictable) {
                            ((Solver) reasonerSAT.getSolver()).setOrder(order);
                        }
                        solverIterator = new ModelIterator(reasonerSAT.getSolver());
                        solverIterator.setTimeoutMs(iteratorTimeout);
                    } else {
                        dimacsSolver = SolverFactory.instance().createSolverByName("MiniSAT");
                        dimacsSolver.setTimeout(SATtimeout);
                        DimacsReader dr = new DimacsReader(dimacsSolver);
                        dr.parseInstance(new FileReader(dimacsFile));
                        if (!predictable) {
                            ((Solver) dimacsSolver).setOrder(order);
                        }
                        solverIterator = new ModelIterator(dimacsSolver);
                        solverIterator.setTimeoutMs(iteratorTimeout);
                    }
                }
            } catch (TimeoutException e) {
            }
        }
        return product;
    }

    public Product getUnpredictableProduct() throws Exception {
        Product product = null;
        while (product == null) {
            try {
                if (solverIterator.isSatisfiable()) {
                	
                	//-----------------------Set order-----------------------
                	int rand = (new Random()).nextInt(3);
                	IOrder order;
                     if (rand == 0) {
                         order = new RandomWalkDecorator(new VarOrderHeap(new NegativeLiteralSelectionStrategy()), 1);
                     } else if (rand == 1) {
                         order = new RandomWalkDecorator(new VarOrderHeap(new PositiveLiteralSelectionStrategy()), 1);
                     } else {
                         order = new RandomWalkDecorator(new VarOrderHeap(new RandomLiteralSelectionStrategy()), 1);
                     }
                     
                	((Solver) solverIterator).setOrder(order);                	         
                	//-----------------------Set order (end)-----------------------
                	
                	
                    product = toProduct(solverIterator.findModel());     
             
                } else {
                	System.out.println("reinitialize solvers in getUnpredictableProduct()");
                    if (!dimacs) {
                        reasonerSAT.init();
                        if (!predictable) {
                            ((Solver) reasonerSAT.getSolver()).setOrder(order);
                        }
                        solverIterator = new ModelIterator(reasonerSAT.getSolver());
                        solverIterator.setTimeoutMs(iteratorTimeout);
                    } else {
                        dimacsSolver = SolverFactory.instance().createSolverByName("MiniSAT");
                        dimacsSolver.setTimeout(SATtimeout);
                        DimacsReader dr = new DimacsReader(dimacsSolver);
                        dr.parseInstance(new FileReader(dimacsFile));
                        if (!predictable) {
                            ((Solver) dimacsSolver).setOrder(order);
                        }
                        solverIterator = new ModelIterator(dimacsSolver);
                        solverIterator.setTimeoutMs(iteratorTimeout);
                    }
                }
            } catch (TimeoutException e) {
            }
        }
        return product;
    }
    
    
    /**
     * 获得“可预测”的一组测试集
     * @param count
     * @param numberOfFeatures
     * @return
     * @throws Exception
     */
    public List<Product> getPredictableProducts(int count, int numberOfFeatures) throws Exception {
        List<Product> products = new ArrayList<Product>(count);
        while (products.size() < count) {
            try {
                if (solverIterator.isSatisfiable()) {
                    Product product = toProduct(solverIterator.model());
                    if (randomGenerator.nextInt(numberOfFeatures) == numberOfFeatures - 1) {

                        if (!products.contains(product)) {
                            products.add(product);
                        }
                    }
                } else {
                    if (!dimacs) {
                        reasonerSAT.init();
                        if (!predictable) {
                            ((Solver) reasonerSAT.getSolver()).setOrder(order);
                        }
                        solverIterator = new ModelIterator(reasonerSAT.getSolver());
                        solverIterator.setTimeoutMs(iteratorTimeout);
                    } else {
                        dimacsSolver = SolverFactory.instance().createSolverByName("MiniSAT");
                        dimacsSolver.setTimeout(SATtimeout);
                        DimacsReader dr = new DimacsReader(dimacsSolver);
                        dr.parseInstance(new FileReader(dimacsFile));
                        if (!predictable) {
                            ((Solver) dimacsSolver).setOrder(order);
                        }
                        solverIterator = new ModelIterator(dimacsSolver);
                        solverIterator.setTimeoutMs(iteratorTimeout);
                    }
                }

            } catch (TimeoutException e) {
            }
        }
        return products;
    }
      
    

    public void writeDimacsProductToFile(String outFile, Product product) throws Exception {
        BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

        for (Integer i : product) {
            out.write(Integer.toString(i));
            //if (n < product.size()) {
            out.newLine();
            //}
        }
        out.close();
    }

    

    

    
    public void serializeProducts(String outFile, List<Product> products) {
        try {


            FileOutputStream fileOut = new FileOutputStream(outFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(products);
            out.close();
            fileOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeProductsToFile(String outFile, List<Product> products, Map<Integer, String> featuresMap, List<Integer> featuresList) throws Exception {
//        BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

//        out.write("Feature\\Product;");
//
//        for (int i = 0; i < products.size(); i++) {
//            out.write("" + i + ";");
//        }
//
//        out.newLine();
//
//        int featuresCount = featuresList.size() / 2;
//        for (int i = 1; i <= featuresCount; i++) {
//            out.write(featuresMap.get(i) + ";");
//
//            for (Product p : products) {
//                for (Integer n : p) {
//                    if (Math.abs(n) == i) {
//                        if (n > 0) {
//                            out.write("X;");
//                        } else {
//                            out.write("-;");
//                        }
//                    }
//                }
//            }
//            out.newLine();
//        }
//        out.close();


        BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

        int featuresCount = featuresList.size() / 2;
        for (int i = 1; i <= featuresCount; i++) {
            out.write(i + ":" + featuresMap.get(i));
            if (i < featuresCount) {
                out.write(";");
            }
        }
        out.newLine();
        for (Product product : products) {
            List<Integer> prodFeaturesList = new ArrayList<Integer>(product);
            Collections.sort(prodFeaturesList, new Comparator<Integer>() {

                @Override
                public int compare(Integer o1, Integer o2) {
                    return ((Integer) Math.abs(o1)).compareTo(((Integer) Math.abs(o2)));
                }
            });

            int done = 1;
            for (Integer feature : prodFeaturesList) {
                out.write((feature > 0 ? "X" : "-"));
                if (done < featuresCount) {
                    out.write(";");
                }
                done++;
            }

            out.newLine();
        }
        out.close();
    }

    
    /**
     * 将products写入文件
     * @param outFile
     * @param products
     * @throws Exception
     */
    public void writeProductsToFile(String outFile, List<Product> products) throws Exception {

      FileUtils.resetFile(outFile);
      
      BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
          
//      out.write(products.size() + " products");
//      out.newLine();
      
      for (Product product : products) {
          List<Integer> prodFeaturesList = new ArrayList<Integer>(product);
          Collections.sort(prodFeaturesList, new Comparator<Integer>() {

              @Override
              public int compare(Integer o1, Integer o2) {
                  return ((Integer) Math.abs(o1)).compareTo(((Integer) Math.abs(o2)));
              }
          });

          int done = 1;
          for (Integer feature : prodFeaturesList) {
              out.write(Integer.toString(feature));   
              if (done < numFeatures) {
                  out.write(";");
              }
              done++;
          }

          out.newLine();
      }
      out.close();
  }
    
    public void readFromXML(String path,ArrayList <String> configurationString, ArrayList <Double> performance) {

    	 
    	  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          //2、创建一个DocumentBuilder的对象
          try {
              //创建DocumentBuilder对象
              DocumentBuilder db = dbf.newDocumentBuilder();
              //3、通过DocumentBuilder对象的parser方法加载books.xml文件到当前项目下
               Document document = db.parse(path);//传入文件名可以是相对路径也可以是绝对路径
              //获取所有book节点的集合
              NodeList dataList = document.getElementsByTagName("row");
     
              //遍历每一个节点
              for (int i = 0; i < dataList.getLength(); i++) {
                                             
                  Node data = dataList.item(i);        
                   
               //解析data节点的子节点
                  NodeList childNodes = data.getChildNodes();
                //遍历childNodes获取每个节点的节点名和节点值                               
                  
                  configurationString.add(childNodes.item(0).getFirstChild().getNodeValue());
                  performance.add(Double.parseDouble(childNodes.item(1).getFirstChild().getNodeValue()));                  
                  
//                  for (int k = 0; k < childNodes.getLength(); k++) {
//                    //区分出text类型的node以及element类型的node
//                      if(childNodes.item(k).getNodeType() == Node.ELEMENT_NODE){
//                        //获取了element类型节点的节点名
//                          System.out.print("第" + (k + 1) + "个节点的节点名：" + childNodes.item(k).getNodeName());                                                  
//                        //获取了element类型节点的节点值
//                          System.out.println("--节点值是：" + childNodes.item(k).getFirstChild().getNodeValue());                             
////                          System.out.println("--节点值是：" + childNodes.item(k).getTextContent());
//                      }
//                  }
                 
              }

          } catch (ParserConfigurationException e) {
              e.printStackTrace();
          } catch (SAXException e) {
              e.printStackTrace();
          } catch (IOException e) {
              e.printStackTrace();
          }
    }
    
    
    /**
     * 将products写入文件
     * @param outFile
     * @param products
     * @throws Exception
     */
    public void writeProductsToFileForModelPrediction(String outFile, List<Product> products,String xmlReadFile) throws Exception {

      FileUtils.resetFile(outFile);
      
      BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
          
//      out.write(products.size() + " products");
//      out.newLine();
      
      String [] featureNames = {"no_asm","no_8x8dct","no_cabac","no_deblock","no_fast_pskip","no_mbtree","no_mixed_refs","no_weightb", 
      							"rc_lookahead","ref", "rc_lookahead_20","rc_lookahead_40","rc_lookahead_60","ref_1","ref_5","ref_9"};       
      
      //Write performance           
      ArrayList <String> configurationString = new ArrayList <String>();
      ArrayList <Double> performance = new ArrayList <Double>();
      
      //读取数据
      readFromXML(xmlReadFile,configurationString,performance);
      
      
      // Prefix  
      out.write("root;no_asm;no_8x8dct;no_cabac;no_deblock;no_fast_pskip;no_mbtree;no_mixed_refs;no_weightb;rc_lookahead;rc_lookahead_20;rc_lookahead_40;rc_lookahead_60;ref;ref_1;ref_5;ref_9;Performance");
      out.newLine();
      
      for (Product product : products) {
          List<Integer> prodFeaturesList = new ArrayList<Integer>(product);
          Collections.sort(prodFeaturesList, new Comparator<Integer>() {

              @Override
              public int compare(Integer o1, Integer o2) {
                  return ((Integer) Math.abs(o1)).compareTo(((Integer) Math.abs(o2)));
              }
          });
            
          // The output order is root, 1-9,11-13,10,14-16，          
          out.write("1;");  
          
          int done = 0;
          for (int i = 0;i < 13 ;i++) {
        	  
        	  if (i != 9) {
	        	  int feature = prodFeaturesList.get(i);
	        	  
	        	  if (feature > 0) {
	        		  out.write("1;");   
	        		  done++;
	        	  } else
	        		  out.write("0;");  
        	  }
	  
          }
          
          
          int feature = prodFeaturesList.get(9);
         
          if (feature > 0) {
    		  out.write("1;");   
    		  done++;
          } else
    		  out.write("0;"); 
          
          for (int i = 13; i < 16 ;i++) {        	  
        	  
        	  feature = prodFeaturesList.get(i);
        	  
        	  if (feature > 0) {
        		  out.write("1;");   
        		  done++;
        	  } else
        		  out.write("0;");    
          }
                    

          StringTokenizer st  = null;
          
          int matcheID = -1;
          boolean found = false;
          
          // write performance
          for (int i = 0; i < configurationString.size();i++) {
        	  String tempConf = configurationString.get(i);
        	  
        	  st = new StringTokenizer(tempConf,",");        	   
        	  
        	  if (done == st.countTokens()) {
        		  boolean matched = true;
        		  
        	 	  //Mach
                  for (int j = 0; j <  prodFeaturesList.size(); j++) {
                	  
                      if (prodFeaturesList.get(j) > 0) {
                    	  String featureName = featureNames[prodFeaturesList.get(j) - 1];
                    	  
                    	  if (!tempConf.contains(featureName)) {
                    		  matched = false; break;
                    	  }
                    		  
                      }
                      
                  }
                  
                  if (matched == true) {
                	  matcheID = i; 
                	  found = true;
                	  break;
                  }
                  
        	  } // if  (done == st.countTokens())   	  
       
        	  if ( found == true)  break;
          } // for        
                    
          out.write(performance.get(matcheID) + ";");   

          out.newLine();
      }
      
      out.close();
  }
    
    /**
     * 从文件读取products
     * @param outFile
     * @param products
     * @throws Exception
     */
    public List<Product> loadProductsFromFile(String inFile) throws Exception {
    	List<Product> products = new  ArrayList <Product>();
    	
        BufferedReader in = new BufferedReader(new FileReader(inFile));
        String line;
       
        while ((line = in.readLine()) != null && !(line.isEmpty())) {
           
        	StringTokenizer tokenizer = new StringTokenizer(line, ";");
            Product product = new Product();     
            
            while (tokenizer.hasMoreTokens()) {
                String tok = tokenizer.nextToken().trim();
                product.add(Integer.parseInt(tok));
            }
             
            products.add(product);
          
        }       
        
        in.close();
        
    	return products;
   
  }
    
    /**
     * 从文件读取products
     * @param outFile
     * @param products
     * @throws Exception
     */
    public List<Product> loadProductsFromFile(String inFile,String str_tokenizer) throws Exception {
    	List<Product> products = new  ArrayList <Product>();
    	
        BufferedReader in = new BufferedReader(new FileReader(inFile));
        String line;
       
        while ((line = in.readLine()) != null && !(line.isEmpty())) {
           
        	StringTokenizer tokenizer = new StringTokenizer(line, str_tokenizer);
            Product product = new Product();     
            
            while (tokenizer.hasMoreTokens()) {
                String tok = tokenizer.nextToken().trim();
                product.add(Integer.parseInt(tok));
            }
             
            products.add(product);
          
        }       
        
        in.close();
        
    	return products;
   
  }
    
    /**
     * 从文件读取products
     * @param outFile
     * @param products
     * @throws Exception
     */
    public List<Product> loadSeedsFromFile(String inFile) throws Exception {
    	List<Product> products = new  ArrayList <Product>();
    	
        BufferedReader in = new BufferedReader(new FileReader(inFile));
        String line;
       
        while ((line = in.readLine()) != null && !(line.isEmpty())) {
           
        	StringTokenizer tokenizer = new StringTokenizer(line, " ");
            Product product = new Product();     
            int i = 0;
            
            while (tokenizer.hasMoreTokens()) {
                String tok = tokenizer.nextToken().trim();
                 // i++;
//                if (Integer.parseInt(tok) == 1) {
//                	 product.add(i);
//                }else {
//                	 product.add(-i);
//                }
                product.add(Integer.parseInt(tok));
//               System.out.print(" " + tok+" ");
            }
             
//            System.out.println("\n"+product.toString());
            
            products.add(product);
          
        }       
        
        in.close();
        
    	return products;
   
  }
    
    /**
     * Write products into files
     * @param outFile
     * @param products
     * @throws Exception
     */
    public void writeDataToFile(String outFile, double data) throws Exception {

      FileUtils.resetFile(outFile);
      
      BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
          
      out.write(Double.toString(data));
      
      out.close();

  }
    
    /**
     * Write products into files
     * @param outFile
     * @param products
     * @throws Exception
     */
    public void writeDataToFile(String outFile, String data) throws Exception {

      FileUtils.resetFile(outFile);
      
      BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
          
      out.write(data);
      
      out.close();

  }
    
    /**
     * Write data into files
     * @param outFile
     * @param products
     * @throws Exception
     */
    public void writeDataToFile(String outFile, double [] data) throws Exception {

      FileUtils.resetFile(outFile);
      
      BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
          
      int done = 0;
      
      for (int i = 0; i < data.length;i++) {
    	  out.write(Double.toString(data[i]));
    	  done++;
    	  
    	  if(done < data.length) {
    		  out.newLine();
    	  }
      }
            
      out.close();
  }
    
    /**
     * Write products into files
     * @param outFile
     * @param products
     * @throws Exception
     */
    public void writeDataToFile(String outFile, List <Double> data) throws Exception {

      FileUtils.resetFile(outFile);
      
      BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
          
      int done = 0;
      
      for (int i = 0; i < data.size();i++) {
    	  out.write(Double.toString(data.get(i)));
    	  done++;
    	  
    	  if(done < data.size()) {
    		  out.newLine();
    	  }
      }
            
      out.close();
  }
    
    /**
     * Write data into files
     * @param outFile
     * @param products
     * @throws Exception
     */
    public void writeDataToFile(String outFile, long data) throws Exception {

      FileUtils.resetFile(outFile);
      
      BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
          
      out.write(Long.toString(data));
      
      out.close();

  }
    
    public boolean isValidProduct(Product product, Map<Integer, String> featuresMap, List<Integer> featuresList) throws Exception {
        IVecInt prod = new VecInt(product.size());

        for (Integer s : product) {
        	if (!dimacs) {
	            if (s < 0) {
	                prod.push(-reasonerSAT.getVariableIndex(featuresMap.get(featuresList.get((-s) - 1))));
	            } else {
	                prod.push(reasonerSAT.getVariableIndex(featuresMap.get(featuresList.get(s - 1))));
	            }
        	} else {
        		 prod.push(s);
        	}
        }
        if (!dimacs) {
        	return reasonerSAT.getSolver().isSatisfiable(prod);
        } else {
        	return dimacsSolver.isSatisfiable(prod);
        }
    }

    public boolean isValidProduct(Product product) throws Exception {
        IVecInt prod = new VecInt(product.size());

        for (Integer s : product) {        	
        		 prod.push(s);        	
        }
        
        if (!dimacs) {
        	return reasonerSAT.getSolver().isSatisfiable(prod);
        } else {
        	return dimacsSolver.isSatisfiable(prod);
        }
    }
    
    public boolean isValidPair(TSet pair, Map<Integer, String> featuresMap, List<Integer> featuresList) throws Exception {

        IVecInt prod = new VecInt(pair.getSize()); // Before it is 2, now apply to any t

        for (Integer fi : pair.getVals()) {
            if (!dimacs) {
                if (fi < 0) {
                    prod.push(-reasonerSAT.getVariableIndex(featuresMap.get(featuresList.get((-fi) - 1))));
                } else {
                    prod.push(reasonerSAT.getVariableIndex(featuresMap.get(featuresList.get(fi - 1))));
                }
            } else {
                prod.push(fi);
            }
        }// for 
        
        if (!dimacs) {
            return reasonerSAT.getSolver().isSatisfiable(prod);
        } else {
            return dimacsSolver.isSatisfiable(prod);
        }

    }

      
    
    /**
     * 
     * @param outFile
     * @param validTSet
     * @throws Exception
     */
    private void writeValidPairsToFile(String outFile, Set<TSet> validTSet) throws Exception {

        BufferedWriter out = new BufferedWriter(new FileWriter(outFile));       
       
               	
    	for(TSet set:validTSet) { // for each Tset
    		  
    		 int i = 1;
    		 
    		 for (Integer id: set.getVals()) { // for each 
    			 
    			 if (i < set.getVals().size()) {
    				 out.write(Integer.toString(id) + ";");
    			 } else {
    				 out.write(Integer.toString(id));
				 }
				 
				 i++;
				 
    		 }  	
             
             out.newLine();
             
    	}        	        	
   
        out.close();
        
    }
    
    public void computeFeatures(ReasoningWithSAT reasonerSAT, Map<Integer, String> featuresMap, Map<String, Integer> featuresMapRev, List<Integer> featuresList, boolean dimacs, String dimacsFile) throws Exception {

        if (!dimacs) {
            String[] features = reasonerSAT.getVarIndex2NameMap(); // 

            for (int i = 0; i < features.length; i++) {
                String feature = features[i];
                int n = i + 1;
                featuresList.add(n); // ID
                featuresMap.put(n, feature);
                featuresMapRev.put(feature, n);
            }


        } else {
            BufferedReader in = new BufferedReader(new FileReader(dimacsFile));
            String line;
            int n = 0;
            while ((line = in.readLine()) != null && (line.startsWith("c")||line.startsWith("p"))) {
               
            	if (line.startsWith("c")) {
            		 StringTokenizer st = new StringTokenizer(line.trim(), " ");
            		 st.nextToken();
                     n++;
                     String sFeature = st.nextToken().replace('$', ' ').trim(); // 有些特征ID后面加了$，表明该特征名是系统产生的
                     int feature = Integer.parseInt(sFeature);
//                     if (n != feature) { // ID 要按照从小到大的顺序排列
//                         throw new Exception("Incorrect dimacs file, missing feature number " + n + " ?");
//                     }
                     String featureName = st.nextToken();
                     featuresList.add(feature);
                     featuresMap.put(feature, featureName);
                     featuresMapRev.put(featureName, feature);
            	}
            	
            	if (line.startsWith("p")) {
            		 StringTokenizer st = new StringTokenizer(line.trim(), " ");
            		 st.nextToken(); st.nextToken();
            		 numFeatures = Integer.parseInt(st.nextToken());
//            		 System.out.println("numFeatures in computeFeatures " + numFeatures);
            	}
               
            } // while             
            
            in.close();
            
            for (int i = 1;i <= numFeatures;i++) {
            	if (!featuresList.contains(i)) { // 
            		  featuresList.add(i);
                      featuresMap.put(i, Integer.toString(i));
                      featuresMapRev.put(Integer.toString(i), i);
            	}
            }
            
        }

        int n = 1;
        int featuresCount = featuresList.size();
        while (n <= featuresCount) {
            featuresList.add(-n); // 把负ID也加入featureList
            n++;
        }

    }

    /**
     * load constraints 
     * @param reasonerSAT
     * @param featuresMap
     * @param featuresMapRev
     * @param featuresList
     * @param dimacs
     * @param dimacsFile
     * @throws Exception
     */
    public void computeConstraints(ReasoningWithSAT reasonerSAT, boolean dimacs, String dimacsFile) 
    		throws Exception {
    	
//    	  if (!dimacs) {
//              fm = loadFeatureModel(fmFile);
//              fm.loadModel();
//              reasonerSAT = new FMReasoningWithSAT("MiniSAT", fm, SATtimeout);
//              reasonerSAT.init();
//          } else {
//              dimacsSolver = SolverFactory.instance().createSolverByName("MiniSAT");
//              dimacsSolver.setTimeout(SATtimeout);
//
//              DimacsReader dr = new DimacsReader(dimacsSolver);
//              dr.parseInstance(new FileReader(fmFile));
//          }
    	  
    	  
    	if (!dimacs) {   	      

         CNFFormula formula = fm.FM2CNF();
         nConstraints = formula.getClauses().size();
         
         featureModelConstraints = new ArrayList<List<Integer>>(nConstraints);
                  
         
         for (CNFClause clause : formula.getClauses()) {
        	
        	 // 每个字句对应一个 List<Integer> 
             List<Integer> constraint = new ArrayList<Integer>(clause.getLiterals().size());
             
             for (int i = 0; i < clause.getLiterals().size(); i++) { // 子句的每个文字
            	            	 
                 int signal = clause.getLiterals().get(i).isPositive() ? 1 : -1;
                 int varID = reasonerSAT.getVariableIndex(clause.getLiterals().get(i).getVariable().getID());
       
                 if (signal < 0) {
                	 constraint.add(- varID);
                 } else {
                	 constraint.add(varID);
                 }
             } // for i
             
             featureModelConstraints.add(constraint);
         }
         
    	} else { // dimacs形式，则从文件读取约束
        	 
        	 BufferedReader in = new BufferedReader(new FileReader(dimacsFile));
             String line;

             while ((line = in.readLine()) != null) {
                 if (line.startsWith("p")) {
                     StringTokenizer st = new StringTokenizer(line.trim(), " ");
                     st.nextToken();
                     st.nextToken();
                     st.nextToken();
                     nConstraints = Integer.parseInt(st.nextToken());
                     break;

                 }
             }
             
             in.close();

             featureModelConstraints = new ArrayList<List<Integer>>(nConstraints);
             
             // -------------------------------------------------------------
             in = new BufferedReader(new FileReader(dimacsFile));
         
             while ((line = in.readLine()) != null) {
                 if (!line.startsWith("c") && !line.startsWith("p") && !line.isEmpty()) {
                	 List<Integer> constraint = new ArrayList<Integer>();
                     StringTokenizer st = new StringTokenizer(line.trim(), " ");

                     while (st.hasMoreTokens()) {
                         int f = Integer.parseInt(st.nextToken());

                         if (f != 0)  constraint.add(f);   
                     }  
                     
                     featureModelConstraints.add(constraint);  
                 } // if  
                 
             } // while            
             in.close();
             
             //-----------------print 
//             for (int i = 0; i < featureModelConstraints.size();i++) {
//            	 for (int j = 0;j < featureModelConstraints.get(i).size();j++)  {
//            		 System.out.print(featureModelConstraints.get(i).get(j) + " ");
//            	 }
//            	 System.out.println();
//             }
    	}     
             
    } //
    
    
    public void normalizeDataFile(String inputDir) throws Exception {

        File inDir = new File(inputDir);
        if (!inDir.exists()) {
            throw new ParameterException("Input directory does not exist");
        }

        File[] datFiles = inDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".dat") && !name.toLowerCase().contains("norm");
            }
        });

        for (File file : datFiles) {

            int count = countUncommentedLines(file);

            double[] coverageValues = new double[count];
            double[] fitnessValues = new double[count];

            BufferedReader in = new BufferedReader(new FileReader(file));

            int i = 0;
            String line;

            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("#")) {
                    StringTokenizer st = new StringTokenizer(line, ";");

                    coverageValues[i] = Double.parseDouble(st.nextToken().trim());
                    fitnessValues[i] = Double.parseDouble(st.nextToken().trim());
                    i++;
                }
            }
            in.close();

            double[] normalizedCoverageValues = new double[101];
            double[] normalizedFitnessValues = new double[101];

            for (int j = 0; j < normalizedCoverageValues.length; j++) {
                int prodIndex = (int) ((double) j / 100.0 * (coverageValues.length - 1));
                normalizedCoverageValues[j] = coverageValues[prodIndex];
                normalizedFitnessValues[j] = fitnessValues[prodIndex] / fitnessValues[fitnessValues.length - 1] * 100;
            }


            String outFile = file.toString().replace(".dat", "-Norm.dat");
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
            out.write("#coverage of pairs (in %, starting from 0% of products selected (normalized));Fitness func (normalized)");
            out.newLine();
            for (int k = 0; k < normalizedCoverageValues.length; k++) {
                out.write(Double.toString(normalizedCoverageValues[k]) + ";" + Double.toString(normalizedFitnessValues[k]));
                out.newLine();
            }
            out.close();
        }

    }

    /**
     * 将vector转换成product，直接vector的元素直接加入product集合即可
     * @param vector
     * @return
     */
    public Product toProduct(int[] vector) {

        Product product = new Product();
        for (int i : vector) {
            product.add(i);
        }
        return product;
    }

    /**
     * 将product转换成IVecInt
     * @param vector
     * @return
     */
    public IVecInt productToIntVec(Product product) {

    	 IVecInt iv = new VecInt();

         for (Integer j: product) {            
             iv.push(j);   
         }
         
        return iv;
    }
    
    /**
     * 将Binary转换成product
     * @param vector
     * @return
     */
    public Product toProduct(Binary bin) {

        Product product = new Product();
        
        for (int i = 0; i < bin.getNumberOfBits();i++) {
        	
        	if (bin.getIth(i) == true){
        		product.add(i + 1);
        	} else {
        		product.add(-(i + 1));
        	}
        		
        } // for i
        return product;
    }
    
    public void computeAverageDataFiles(String inputDir, String outputDir, final boolean noNorm) throws Exception {
        File inDir = new File(inputDir);
        if (!inDir.exists()) {
            throw new ParameterException("Input directory does not exist");
        }

        if (outputDir.equals("Same as input")) {
            outputDir = inputDir;
        }

        if (!new File(outputDir).exists()) {
            throw new ParameterException("Output directory does not exist");
        }
        File[] datFiles = inDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (!noNorm) {
                    return name.endsWith("-Norm.dat");
                } else {
                    return name.endsWith(".dat") && !name.toLowerCase().contains("norm");
                }
            }
        });

        Set<String> types = new HashSet<String>();
        for (File file : datFiles) {
            String sFile = file.toString();
            String type = sFile.substring(sFile.lastIndexOf("_") + 1, sFile.length());
            types.add(type);
        }
        for (final String type : types) {
            datFiles = inDir.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(type);
                }
            });
            int n = 0;
            double[] coverageValues, fitnessValues;
            if (!noNorm) {
                coverageValues = new double[101];
                fitnessValues = new double[101];
            } else {
                int count = minUncommentedLinesCount(datFiles);
                coverageValues = new double[count];
                fitnessValues = new double[count];
            }

            String firstLine = "";
            for (File dat : datFiles) {
                int i = 0;
                BufferedReader in = new BufferedReader(new FileReader(dat));
                String line;
                while ((line = in.readLine()) != null && i < coverageValues.length) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        if (line.startsWith("#")) {
                            firstLine = line;
                        } else {
                            StringTokenizer tokenizer = new StringTokenizer(line, ";");
                            double cov = Double.parseDouble(tokenizer.nextToken());
                            double fit = Double.parseDouble(tokenizer.nextToken());
                            coverageValues[i] += cov;
                            fitnessValues[i] += fit;
                            i++;
                        }
                    }
                }
                in.close();
                n++;

            }

            for (int i = 0; i < coverageValues.length; i++) {
                coverageValues[i] /= (double) n;
                fitnessValues[i] /= (double) n;
            }

            String outFile = outputDir;
            if (!outFile.endsWith("/")) {
                outFile += "/";
            }
            outFile = outFile + "AVG_ON_ALL_" + type;
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

            out.write(firstLine);
            out.newLine();
            for (int i = 0; i < coverageValues.length; i++) {
                out.write(Double.toString(coverageValues[i]) + ";" + Double.toString(fitnessValues[i]));
                out.newLine();
            }
            out.close();
        }
    }

    public int countUncommentedLines(File file) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line;
        int n = 0;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
                n++;
            }
        }
        in.close();
        return n;
    }

    public int minUncommentedLinesCount(File[] files) throws Exception {
        int min = countUncommentedLines(files[0]);

        for (int i = 1; i < files.length; i++) {
            int count = countUncommentedLines(files[i]);
            if (count < min) {
                min = count;
            }
        }

        return min;
    }

    public List<Product> loadProductsFromCSVFile(File csvFile, Map<String, Integer> featuresMapRev) throws Exception {
        List<Product> products = new ArrayList<Product>();
        BufferedReader in = new BufferedReader(new FileReader(csvFile));
        String line;
        boolean firstLine = true;
        List<String> features = null;

        if (featuresMapRev != null) {
            features = new ArrayList<String>();
        }
        while ((line = in.readLine()) != null) {
            StringTokenizer tokenizer = new StringTokenizer(line, ";");
            if (firstLine) {
                if (featuresMapRev != null) {
                    while (tokenizer.hasMoreTokens()) {
                        features.add(tokenizer.nextToken().trim());
                    }
                }
                firstLine = false;
            } else {
                Product product = new Product();
                int count;
                if (featuresMapRev != null) {
                    count = 0;
                } else {
                    count = 1;
                }
                while (tokenizer.hasMoreTokens()) {
                    String tok = tokenizer.nextToken().trim();
                    if (tok.equals("X")) {
                        if (featuresMapRev != null) {
                            product.add(featuresMapRev.get(features.get(count)));
                        } else {
                            product.add(count);
                        }
                    } else if (tokenizer.equals("-")) {
                        if (featuresMapRev != null) {
                            product.add(-featuresMapRev.get(features.get(count)));
                        } else {
                            product.add(-count);
                        }
                    }
                    count++;

                }
                products.add(product);
            }
        }
        return products;
    }

    
   
    /**
     * Load mandatory and dead indices from files
     * @param mandatory
     * @param dead
     * @throws Exception
     */
    public void loadMandatoryDeadFeaturesIndices(String mandatory, String dead) throws Exception {

        mandatoryFeaturesIndices = new ArrayList<Integer>(numFeatures);
        deadFeaturesIndices = new ArrayList<Integer>(numFeatures);
        featureIndicesAllowedFlip = new ArrayList<Integer>(numFeatures);               

        File file = new File(mandatory);   
        
        if (file.exists()) {      
        
	        BufferedReader in = new BufferedReader(new FileReader(mandatory));
	        String line;
	        while ((line = in.readLine()) != null) {
	            if (!line.isEmpty()) {
	                int i = Integer.parseInt(line) - 1;
	                mandatoryFeaturesIndices.add(i);	
//	                System.out.println(i);
	            }
	
	        }
	        
	        in.close();
        } 
        
        file = new File(dead);   
        
        if (file.exists()) {           	
        
	        BufferedReader  in = new BufferedReader(new FileReader(dead));
	        String line;
	        while ((line = in.readLine()) != null) {
	            if (!line.isEmpty()) {
	                int i = Integer.parseInt(line) - 1;
	                deadFeaturesIndices.add(i);	        
//	               System.out.println(i);
	            }
	
	        }
	        
	        in.close();
        } // if 
        
         for (int i = 0; i < numFeatures; i++) {
            if (! mandatoryFeaturesIndices.contains(i) && !deadFeaturesIndices.contains(i))
                featureIndicesAllowedFlip.add(i);
            
        }

         System.out.println("mandatoryFeaturesIndices.size() = " + mandatoryFeaturesIndices.size());
         System.out.println("deadFeaturesIndices.size() = " + deadFeaturesIndices.size());
//         System.out.println("featureIndicesAllowedFlip.size() = " + featureIndicesAllowedFlip.size());
         
    }
       

    /**
     * 初始化模型及求解器
     * @param fmFile
     * @param nbPairs
     * @param t
     * @throws Exception
     */
    public void initializeModelSolvers(String fmFile,int t) throws Exception {
 
        if (!new File(fmFile).exists()) {
            throw new ParameterException("The specified FM file does not exist");
        }

        this.predictable = false;// 
        this.dimacs = true; //
        this.dimacsFile = fmFile;// 
        
        //System.out.println("--------------Initialize SAT solvers-------------");
        /**
         * ---------------------Initialize SAT4J solvers--------------------------------
         */
        dimacsSolver = SolverFactory.instance().createSolverByName("MiniSAT");
        dimacsSolver.setTimeout(SATtimeout);

        DimacsReader dr = new DimacsReader(dimacsSolver);
        dr.parseInstance(new FileReader(fmFile));
        
    	if (!predictable) {
    		((Solver) dimacsSolver).setOrder(order);
    	}
//    	
//        solverIterator = new ModelIterator(dimacsSolver);
        solverIterator =  dimacsSolver; 
        solverIterator.setTimeoutMs(iteratorTimeout);
        // ---------------------Initialize SAT4J solvers（End）-------------------------------
         
        //System.out.println("--------------读取特征、约束及core、dead features-------------");
        /**
         * ---------------------Load features, constraints, core and dead features---------------------
         */
        featuresList   = new ArrayList<Integer>();
        featuresMap    = new HashMap<Integer, String>(); 
        featuresMapRev = new HashMap<String, Integer>(); 
       
        computeFeatures(null, featuresMap, featuresMapRev, featuresList, true, fmFile);
        computeConstraints(null, true, fmFile);               
        
        System.out.println("numFeatures"  + numFeatures);
        System.out.println("numConstraints"  + nConstraints);
        
        //Read indexes for mandatory and dead features (= ID-1)
        loadMandatoryDeadFeaturesIndices(fmFile+".mandatory", fmFile+".dead");
        // ---------------------Load features, constraints, core and dead features (end)--------------------
             
        // Initialize probSAT solver      
        HashMap  localSearchParameters ;     
        localSearchParameters = new HashMap() ;
        localSearchParameters.put("constraints",featureModelConstraints); //featureModelConstraints 在computeConstraints中读取了
        localSearchParameters.put("num_vars",numFeatures); 
        localSearchParameters.put("max_flips",30000);
        localSearchParameters.put("wp", 0.567);

        repairSolver = new ProbSATLocalSearch(localSearchParameters);// ProbSAT                   
//        validTSets = loadValidTSet(fmFile + ".faults.obj");  
//    	System.out.println("Load: Number of validTSet" + validTSets.size());
                
        // Read T-set            
        String validTsetFile = fmFile + ".valid" + t + "-Set"  ;    	     
        String validTsetFolder= fmFile + ".valid" + t + "-Sets/"  ;    	
        
        validTSets = null;
        allValidTSets = new ArrayList<Set<TSet>>() ;
        
        if (validTsetFile != null && (new File(validTsetFile).exists())) {        	
        	validTSets = loadValidTSet(validTsetFile);  
        	System.out.println("****************Load: Number of validTSets " + validTSets.size());
        	
        } else {
        	        	        	
        	if(t <= 5 && numFeatures <= 30) {
        		System.out.println("---------Generate validTSets------------");
        		validTSets = computeExactValidTSet(featuresMap, featuresList, null, false, null, t);// 精确算法
        		writeValidPairsToFile(validTsetFile, validTSets);
        		
        	}else  if (t == 2 && numFeatures > 30 && numFeatures <= 1000)  {
        		System.out.println("---------Generate validTSets------------");
        		validTSets = computeExactValidTSet(featuresMap, featuresList, null, false, null, t);// 精确算法
        		writeValidPairsToFile(validTsetFile, validTSets);
        		System.out.println("---------Generate validTSets------------");
        	} else if (t == 3 && numFeatures > 30  && numFeatures <= 90) {
        		
        		validTSets = computeExactValidTSet(featuresMap, featuresList, null, false, null, t);// 精确算法
        		writeValidPairsToFile(validTsetFile, validTSets);
        		
        	} else {
        		        		
        		 
        		File pathDirectory = new File(validTsetFolder);
            	
        		if (!pathDirectory.exists()) {
        			
        			new File(validTsetFolder).mkdirs();
        			
        			int  nbPairs;        		
             		
               		nbPairs = t * 50000; // Any number you prefer. 
            		
            		System.out.println("Number of valid sets to be generated " + nbPairs);
            		
            		   //Repeat multiple times
                    for (int i = 0; i < 10;i++) { 
                    	System.out.println("Generate t set" + i);
                    	writeValidPairsToFile(validTsetFolder+"tSet."+i, computeNRandValidTSets(featuresMap, featuresList, nbPairs, t));            	
                    }            
            		
            	} else { // 
            		
//            		allValidTSets = null;
//              	    for (int j = 0; j < 1; j++) { 
//            	    	allValidTSets.add( MAP_test.getInstance().loadValidTSet(validTsetFolder+"tSet."+j));  
//              	  	}
              	    
            	}
        	}   
     }  // IF  
        
        if (new File(validTsetFolder).exists()){        	
        	
      	    for (int j = 0; j < 1; j++) { 
      	    	System.out.println(validTsetFolder+"tSet."+j);      	    	
    	    	allValidTSets.add( MAP_test.getInstance().loadValidTSet(validTsetFolder+"tSet."+j));  
      	  	}
      	   System.out.println("******************Load allValidTSets************************");
      	   
        } else {
        	
//        	allValidTSets = null;
        	
        	new File(validTsetFolder).mkdirs();
        	Set<TSet> temp = null;
        	
            for (int i = 0; i < 1;i++) { 
            	System.out.println("Generate t set" + i);
            	temp = computeNRandValidTSets(featuresMap, featuresList, numFeatures, t);
            	allValidTSets.add(temp);
            	writeValidPairsToFile(validTsetFolder+"tSet."+i,temp );            	
            } 
        }
        
        System.out.println("-------------initializeModelSolvers end-------------");
    } // end of class   
        
    
    /**
     * Used when calculating indicators
     * @param fmFile
     * @param nbPairs
     * @param t
     * @throws Exception
     */
    public void initializeModelSolversCalIndicators(String fmFile, int t) throws Exception {
 
        if (!new File(fmFile).exists()) {
            throw new ParameterException("The specified FM file does not exist");
        }

        this.predictable = false;// 
        this.dimacs = true; //
        this.dimacsFile = fmFile;// 
        
        //System.out.println("--------------Initialize SAT solvers-------------");
        /**
         * ---------------------Initialize SAT4J solvers--------------------------------
         */
        dimacsSolver = SolverFactory.instance().createSolverByName("MiniSAT");
        dimacsSolver.setTimeout(SATtimeout);

        DimacsReader dr = new DimacsReader(dimacsSolver);
        dr.parseInstance(new FileReader(fmFile));
        
    	if (!predictable) {
    		((Solver) dimacsSolver).setOrder(order);
    	}
    	
//        solverIterator = new ModelIterator(dimacsSolver);
        solverIterator =  dimacsSolver; 
        solverIterator.setTimeoutMs(iteratorTimeout);
        // ---------------------Initialize SAT4J solvers（End）-------------------------------
         
        //System.out.println("--------------读取特征、约束及core、dead features-------------");
        /**
         * ---------------------Load features, constraints, core and dead features---------------------
         */
        featuresList   = new ArrayList<Integer>();
        featuresMap    = new HashMap<Integer, String>(); 
        featuresMapRev = new HashMap<String, Integer>(); 
       
        computeFeatures(null, featuresMap, featuresMapRev, featuresList, true, fmFile);
        computeConstraints(null, true, fmFile);               
        
        System.out.println("numFeatures "  + numFeatures);
        System.out.println("numConstraints "  + nConstraints);
        
        //Read indexes for mandatory and dead features (= ID-1)
        loadMandatoryDeadFeaturesIndices(fmFile+".mandatory", fmFile+".dead");
        // ---------------------Load features, constraints, core and dead features (end)--------------------
             
        // Initialize probSAT solver      
//        HashMap  localSearchParameters ;     
//        localSearchParameters = new HashMap() ;
//        localSearchParameters.put("constraints",featureModelConstraints); //featureModelConstraints 在computeConstraints中读取了
//        localSearchParameters.put("num_vars",numFeatures); 
//        localSearchParameters.put("max_flips",8000);
//        localSearchParameters.put("wp", 0.567);
//
//        repairSolver = new ProbSATLocalSearch(localSearchParameters);// ProbSAT                    
           
        //-------------------------------Not used when running --------------------
        // Read T-set            
        String validTsetFile = fmFile + ".valid" + t + "-Set"  ;    	     
    
        validTSets = null;
        
        if (validTsetFile != null && (new File(validTsetFile).exists())) {        	
//        	validTSets = loadValidTSet(validTsetFile);  
//        	System.out.println("Load: Number of validTSets " + validTSets.size());
        	
        } else {
        	        	        	
        	if(t <= 5 && numFeatures <= 30) {
        		System.out.println("---------Generate validTSets------------");
        		validTSets = computeExactValidTSet(featuresMap, featuresList, null, false, null, t);// 精确算法
        		writeValidPairsToFile(validTsetFile, validTSets);
        		
        	}else  if (t == 2 && numFeatures > 30 && numFeatures <= 1000)  {
        		System.out.println("---------Generate validTSets------------");
        		validTSets = computeExactValidTSet(featuresMap, featuresList, null, false, null, t);// 精确算法
        		writeValidPairsToFile(validTsetFile, validTSets);
        		System.out.println("---------Generate validTSets------------");
        	} else if (t == 3 && numFeatures > 30  && numFeatures <= 90) {
        		
        		validTSets = computeExactValidTSet(featuresMap, featuresList, null, false, null, t);// 精确算法
        		writeValidPairsToFile(validTsetFile, validTSets);
        		
        	} else {
        		
        		String validTsetFolder= fmFile + ".valid" + t + "-Sets/"  ;    	
        		 
        		File pathDirectory = new File(validTsetFolder);
            	
        		if (!pathDirectory.exists()) {
        			
        			new File(validTsetFolder).mkdirs();
        			
        			int  nbPairs;        		
             		
               		nbPairs = t * 50000; // Any number you prefer. 
            		
            		System.out.println("Number of valid sets to be generated " + nbPairs);
            		
            		   //Repeat multiple times
                    for (int i = 0; i < 10;i++) { 
                    	System.out.println("Generate t set" + i);
                    	writeValidPairsToFile(validTsetFolder+"tSet."+i, computeNRandValidTSets(featuresMap, featuresList, nbPairs, t));            	
                    }            
            		
            	}            		                   		
        	}            	
    	
        	
     }  // IF  
       
      System.out.println("-------------initializeModelSolvers end-------------");
  } // end of class
    
    
    
    public Set<TSet> loadValidTSet(String validFile) throws Exception {
        if (!new File(validFile).exists()) {
            throw new ParameterException("The specified valid file does not exist");
        }

        LineNumberReader lnr = new LineNumberReader(new FileReader(validFile));
        lnr.skip(Long.MAX_VALUE);

        List<TSet> tset = new ArrayList<TSet>(lnr.getLineNumber());

        BufferedReader in = new BufferedReader(new FileReader(validFile));
        String line;

        while ((line = in.readLine()) != null) {
            if (!line.isEmpty()) {
                StringTokenizer st = new StringTokenizer(line, ";");

                TSet set = new TSet();
                
                while (st.hasMoreTokens()) {
	                set.add(Integer.parseInt(st.nextToken()));	               
                }
                
                tset.add(set);
            }//if
        }

        in.close();

        Set<TSet> validTSet = new HashSet<TSet>(tset);
        return validTSet;
    }
    
    private Set<TSet> computeExactValidTSet(Map<Integer, String> featuresMap, List<Integer> featuresList,
            String outFile, boolean dimacs, ISolver dimacsSolver, int t) throws Exception {    

        Set<TSet> pairs = new HashSet<TSet>(); 

        int size = featuresList.size();

        nCk(size, t, pairs, featuresMap, featuresList);
//        System.out.println(pairs);
        System.out.println("number of valid T-sets " + pairs.size());
        return pairs;
    }
    
    public void nCk(int n, int k, Set<TSet> tsets, Map<Integer, String> featuresMap, List<Integer> featuresList) throws Exception {
        int[] a = new int[k];
        nCkH(n, k, 0, a, k, tsets, featuresMap, featuresList);
    }
    
    public void nCkH(int n, int loopno, int ini, int[] a, int k, Set<TSet> tsets, Map<Integer, String> featuresMap, List<Integer> featuresList) throws Exception {

        if (k == 0) {
            return;
        }

        int i;
        loopno--;
        if (loopno < 0) {
            a[k - 1] = ini - 1;
            TSet p = new TSet();
            for (i = 0; i < k; i++) {
                p.add(featuresList.get(a[i]));
            }
            if (isValidPair(p, featuresMap, featuresList)) {
                tsets.add(p);
            }
            return;

        }
        for (i = ini; i <= n - loopno - 1; i++) {
            a[k - 1 - loopno] = i;
            nCkH(n, loopno, i + 1, a, k, tsets, featuresMap, featuresList);
        }
    }
    
    private Set<TSet> computeNRandValidTSets(Map<Integer, String> featuresMap, List<Integer> featuresList, int n, int t) throws Exception {

        Set<TSet> pairs = new HashSet<TSet>(n);

        int size = featuresList.size();
        double total = getBinomCoeff(size, t);
        while (pairs.size() < n) {
            TSet set = getITSet(size, t, Math.floor(Math.random() * total), featuresList);
            if (isValidPair(set, featuresMap, featuresList)) {
                pairs.add(set);
            }
        }
        return pairs;
    }
    
    public double getBinomCoeff(int n, int k) {
        if (k > n) {
            return 0.0;
        } else if (n == k || k == 0) {
            return 1.0;
        } else {
            return ArithmeticUtils.binomialCoefficientDouble(n, k);
        }
    }

    public TSet getITSet(int n, int k, double m, List<Integer> featuresList) {

        double total = getBinomCoeff(n, k);
        if (m >= total) {
            m = total - 1.0;
        }
        TSet tSet = new TSet();
        int a = n;
        int b = k;
        double x = (total - 1.0) - m;  // x is the "dual" of m

        for (int i = 0; i < k; i++) {
            a = largestV(a, b, x);          // largest value v, where v < a and vCb < x
            x = x - getBinomCoeff(a, b);
            b = b - 1;
            tSet.add(featuresList.get(n - 1 - a));
        }

        return tSet;
    }
    
    private int largestV(int a, int b, double x) {
        int v = a - 1;

        while (getBinomCoeff(v, b) > x) {
            v--;
        }

        return v;
    } // LargestV()
    
    /**
     * 计算一个测试集中，每个测试实例的（部分）覆盖率。注意：不是绝对覆盖率。如果t-set不准确，则覆盖率不太可靠
     * @param products
     * @param pairs
     */
    public void computeProductsPartialCoverage(List<Product> products, Set<TSet> pairs) {
        double pairsSize = pairs.size();
        Set<TSet> pairsCopy = new HashSet<TSet>(pairs);
        for (Product product : products) {
            int initialSize = pairsCopy.size();
            Set<TSet> productPairs = product.computePartialCoveredPairs(pairsCopy);
            pairsCopy.removeAll(productPairs);
            double removed = initialSize - pairsCopy.size();
//            double coverage = removed / pairsSize * 100.0;
            double coverage = removed ;
            product.setCoverage(coverage);
        }
        pairsCopy = null;
    }

    /**
     * @param products
     * @param pairs
     */
    public void computeProductsPartialFaults(List<Product> products, Set<TSet> pairs) {
        double pairsSize = pairs.size();
        Set<TSet> pairsCopy = new HashSet<TSet>(pairs);
        for (Product product : products) {
            int initialSize = pairsCopy.size();
            Set<TSet> productPairs = product.computePartialCoveredPairs(pairsCopy);
            pairsCopy.removeAll(productPairs);
            double removed = initialSize - pairsCopy.size();           
            product.setCoverage(removed);
        }
        
        pairsCopy = null;
    }

	public String getDimacsFile() {
		return dimacsFile;
	}

	public void setDimacsFile(String dimacsFile) {
		this.dimacsFile = dimacsFile;
	}
    

}
