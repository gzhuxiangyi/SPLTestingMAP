/* GenerateTablesMain.java
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
 * modified under the terms of the GNU  General Public License
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

import java.io.IOException;

import jmetal.core.Algorithm;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.experiments.util.Friedman;
import jmetal.util.JMException;


public class GenerateTablesMain extends Experiment {

  /**
   * Configures the algorithms in each independent run
   * @param problemName The problem to solve
   * @param problemIndex
   * @throws ClassNotFoundException 
   */
  public void algorithmSettings(String problemName, 
  		                          int problemIndex, 
  		                          Algorithm[] algorithm) throws ClassNotFoundException {
    
  } // algorithmSettings

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    GenerateTablesMain exp = new GenerateTablesMain();

    exp.experimentBaseDirectory_ = "G:/javaWorkSpace/SPLTestingMAP/";
    
    exp.experimentName_ = "output";  // output dir;  

    exp.minNproducts = 0; // the min number of products
    exp.maxNproducts = 0;  // the max number of products
    
    exp.algorithmNameList_ = new String[]{	 
    		
    		/**
    		 * RQ1: mutated.mutate() + getOneRandomProductSAT4J();
    		 */
//    		"MPNoSeedingAccuracy",//RQ1: 
//    		"MPNoSeedingAccuracySingleTask",//RQ1
//    		"MPNoSeedingDiversity",// RQ1:Noseeding + diversity fitness
//    		"MPNoSeedingDiversitySingleTask",//RQ1: Noseeding + diversity fitness
//		    "MPSeedingAccuracy",// Seeding + accuracy fitness+ SAT4J  	
//		    "MPSeedingAccuracySingleTask",// Seeding + accuracy fitness+ SAT4J +SingleTask
//   		    "MPSeedingDiversity",// Seeding + diversity fitness	
//   		    "MPSeedingDiversitySingleTask",// Seeding + diversity fitness 
    		
    		/**
    		 * RQ2: mutated.mutate() + getOneRandomProductSAT4J();
    		 */
//    		 "MPSeedingAccuracy",// Seeding + accuracy fitness+ SAT4J  	
//    		 "MPSeedingAccuracySingleTask",// Seeding + accuracy fitness+ SAT4J +SingleTask 
//     		 "SamplingCA",    		    		
//    		"MPSeedingDiversity",// Seeding + diversity fitness	
//    		"MPSeedingDiversitySingleTask",// Seeding + diversity fitness
//    		"MPSeedingAccuracy",// Seeding + accuracy fitness+ SAT4J  	
//    		"MPSeedingAccuracySingleTask",// Seeding + accuracy fitness+ SAT4J +SingleTask  	

    		/**
    		 * RQ3: mutated.mutateWorst() + getOneRandomProductSAT4J();
    		 */
//    		"MPNoSeedingDiversityForLargeFinal1",	//RQ3
//    		"TSENSE2000",    	//RQ3
//    		"MPNoSeedingDiversityForLargeMutateWorstSingleTask",
//    		"MPNoSeedingDiversityForLargeMutateWorst",// Noseeding + diversity fitness + for larger FMs
//    		"MPNoSeedingDiversityForLargeFinal",
//    		"TSENSE6000",	
    		
    		/**
    		 * RQ4
    		 */    		
    		"MPNoSeedingAccuracy", //
    		"NSGA2-NRRNoSeedingAccuracy",// Do NOT Remove Reduplicated solutions, write into paper
    		
    		//////////////////////////////////////////////////////
//    		"NSGA2NoSeedingAccuracy",//  
//    		"MPSeedingAccuracy",
//    		"NSGA2SeedingAccuracy",// Remove Reduplicated solutions	    		
//    		"NSGA2-NRRSeedingAccuracy",// Do NOT Remove Reduplicated solutions   
//    		"NSGA2SeedingAccuracy",// Remove Reduplicated solutions	    		
   		
//    		"MPSeedingAccuracyTime",
//    		"NSGA2-NRRSeedingAccuracyTime",// Do NOT Remove Reduplicated solutions

    		//-----------------------------------------------------    		
//    		"MPSeedingDiversity",
//    		"NSGA2SeedingDiversity",// 
//    		"MPNoSeedingDiversity",
//    		"NSGA2NoSeedingDiversity",// 

    		
//    		"MPNoSeedingDiversityTwoSAT",
//    		"MPNoSeedingDiversityTwoSATSingleTask",
//    		"MPNoSeedingDiversityForLarge",// Noseeding + diversity fitness + for larger FMs
//    		"MPNoSeedingDiversityForLargeEqual",    		
//    		"MPNoSeedingDiversityForLargeSingleTask",// Noseeding + diversity fitness + for larger FMs
//    		"MPNoSeedingDiversityForLargeFinal",
//    		"MPNoSeedingDiversityForLargeMutateWorst",
//    		"MPNoSeedingDiversityForLargeMutateWorstSingleTask",
//    		"TSENSE2000",
//    		"TSENSE6000",
//    		"SamplingCASimplification",
		};
    
    exp.problemList_ = new String[]{
    		
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
    		
    		//----------------------------RQ2&3-------------------------------
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
//	  		/*******************  Large-scale ***************** */
//			 "busybox-1.18.0",//6796
//			 "2.6.28.6-icse11", //6888
			
			// *****************Not used***************
//			 "uClinux-config", //11254
//			 "buildroot", // 14910  	
			 
    		};

    for (int i = 0;i<exp.problemList_.length;i++) {	
    	 exp.problemList_[i] =  exp.problemList_[i] + ".dimacs"; 	
    }
    	
    exp.indicatorList_ = new String[]{
//			"QDscoreFitness", // RQ1
//    		"QDscore2wiseCoverage", // RQ1
    		
//    		"ImprovementRate",// RQ2
//    		"First100",//RQ2
//    		"RUNTIME", // RQ2

//    		"FaultRate", // RQ3
    		
//    		
//    		"QDscoreFitness",
    		"QDscoreDominanceFitness", // RQ4, 论文中的RQ2
    		"FailNo",
//    		"First100",//RQ4
//    		"RUNTIME", // RQ4
    		
//    		"2wiseCoverage",    		
//    		"QDscoreDiversity",
//    		"Diversity",
//    		"QDscoreFaultRate",
    		
//    		"Fitness",
//    		"Coverage",
//    		"2wiseDistDiff",
//    		"Nscore",    		
//    		"invertedDist",
//    		"4wiseCoverage",
//    		"6wiseCoverage",
//    		"3wiseCoverage",
//    		"6wiseCoverage",
//    		"4wiseCoverage",
//    		"2wiseDistDiff",    		
//    		"Evaluations",
//    		"RUNTIME",
//    		"UnchangedRatio",
//    		"OriginalUnchangedRatio",
//    		"5wiseCoverage",
//    		"Extension",
//    		"STD",
    		};
    
    int numberOfAlgorithms = exp.algorithmNameList_.length;    
  
//    exp.experimentBaseDirectory_ = "./" +  exp.experimentName_ ;
//    exp.experimentBaseDirectory_ =   exp.experimentName_ ;
    
    exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

    exp.independentRuns_ =  30;   

    exp.initExperiment();

    // Run the experiments
    int numberOfThreads ;

    exp.generateQualityIndicators() ;

    // Generate latex tables
    exp.generateLatexTables(false) ;    // generate tables without test symbols
//    exp.generateLatexTables(true) ;    // generate tables with test symbols, should perform the  Mann-Whitney U test first
    // Applying Friedman test
    Friedman test = new Friedman(exp);
//    test.executeTest("MeanFaultRate");
//    test.executeTest("HV");
//    test.executeTest("GSPREAD");
//    test.executeTest("IGD");
//    test.executeTest("RUNTIME");
  } // main
} // 


