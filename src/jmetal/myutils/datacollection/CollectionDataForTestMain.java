package jmetal.myutils.datacollection;


public class CollectionDataForTestMain {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws MatlabConnectionException 
	 * @throws MatlabInvocationException 
	 */
	public static void main(String[] args) throws Exception {

		int runs = 30;
		int t = 2; //Not used
		int nbProduts = 100;
		long timeMS = 600000;//Not used
		
		String expPath = "G:/javaWorkSpace/SPLTestingMAP/output/";
		
		String [] problems= new String[]{	
				
				/** ***************** Small-scale N<100*****************  */
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
	  			
	  			/*---------------------------------------分割线----------------------------------*/
//				"n100Model1",
//				"n100Model2",
//				"n100Model3",
//				"n100Model4",
//				"n100Model5",
//				"n100Model6",
//				"n100Model7",
//				"n100Model8",
//				"n100Model9",
//				"n100Model10",				
				/***************Median-scale******************** */
//				"Printers",//172
//				"fiasco_17_10",//234
//				"uClibc-ng_1_0_29",//269
//				"E-shop",//290
//				"toybox",//544
//				"axTLS", // 684
//				"busybox_1_28_0", // 998  	
//	    		"SPLOT-3CNF-FM-1000-200-0,50-SAT-1",
//				"SPLOT-3CNF-FM-1000-200-0,50-SAT-2",
//				"SPLOT-3CNF-FM-1000-200-0,50-SAT-3",
//				"SPLOT-3CNF-FM-1000-200-0,50-SAT-4",
//				"SPLOT-3CNF-FM-1000-200-0,50-SAT-5",
//				"SPLOT-3CNF-FM-1000-200-0,50-SAT-6",
//				"SPLOT-3CNF-FM-1000-200-0,50-SAT-7",
//				"SPLOT-3CNF-FM-1000-200-0,50-SAT-8",
//				"SPLOT-3CNF-FM-1000-200-0,50-SAT-9",			
//				"SPLOT-3CNF-FM-1000-200-0,50-SAT-10",	
//				"freebsd-icse11",//1396,OK
//				"fiasco",//1638， OK
//				"uClinux",//1850, OK
//				"Automotive01", //2513, OK, 
//				"SPLOT-3CNF-FM-5000-1000-0,30-SAT-1",// 5000 , OK	
		  		/*******************  Large-scale ***************** */
//				 "busybox-1.18.0",//6796
//				 "2.6.28.6-icse11", //6888
//				 "uClinux-config", //11254
//				 "buildroot", // 14910  	
//				"mpc50", //1213  M = 0(强制特征为0)
//				"ref4955",//1218  M = 0		
//				"linux", //1232  M = 0	
//				"csb281", //1233 M = 0
//				"ecos-icse11", //1244 M=0
//				"ebsa285", //1245 M=0
//				"vrc4373", // 1247  M=0, dead 是乱码？
//				"pati", // 1248  M=0, dead 是乱码？
//				"dreamcast", //1252 M=0, dead 是乱码？
//				"pc_i82544", //1259  M=0,
//				"XSEngine",  //1260  M=0,
//				"refidt334", //1263  M=0,
//				"ocelot", //1266   M=0, dead 是乱码？
//				"integrator_arm9", //1267 M=0,
//				"olpcl2294", //1273 M=0, dead 是乱码？
//				"olpce2294", //1274 M=0,
//				"phycore", // 1274，M=0, dead 是乱码？
//				"hs7729pci", //1298，M=0, dead 是乱码？	 
				};
		
		String [] algorithms = new String[]{			
	    		/**
	    		 * RQ1: mutated.mutate() + getOneRandomProductSAT4J();
	    		 */
//	    		"MPNoSeedingAccuracy",//RQ1: 
//	    		"MPNoSeedingAccuracySingleTask",//RQ1	
//	    		"MPNoSeedingDiversity",// RQ1:Noseeding + diversity fitness
//	    		"MPNoSeedingDiversitySingleTask",//RQ1: Noseeding + diversity fitness
//		   		 "MPSeedingAccuracy",// Seeding + accuracy fitness+ SAT4J  	
//		   		 "MPSeedingAccuracySingleTask",// Seeding + accuracy fitness+ SAT4J +SingleTask 
//	   		 "MPSeedingDiversity",// Seeding + diversity fitness	
//	   		 "MPSeedingDiversitySingleTask",// Seeding + diversity fitness
				
	    		/**
	    		 * RQ2: mutated.mutate() + getOneRandomProductSAT4J();
	    		 */
//	    		 "MPSeedingAccuracy",// Seeding + accuracy fitness+ SAT4J  	
//	    		 "MPSeedingAccuracySingleTask",// Seeding + accuracy fitness+ SAT4J +SingleTask 
//	     		 "SamplingCA",

	    		/**
	    		 * RQ3: mutated.mutate() + getOneRandomProductSAT4J();
	    		 */
//	    		"MPNoSeedingDiversityForLargeFinal1",	
//	    		"TSENSE2000",    	
//	    		"MPNoSeedingDiversityForLargeMutateWorstSingleTask",
//	    		"TSENSE6000",	
				
				/**
	    		 * RQ4:
	    		 */
	    		"MPSeedingAccuracy",
	    		"NSGA2-NRRSeedingAccuracy",// Do NOT Remove Reduplicated solutions    		
//	    		"MPNoSeedingAccuracy",
//	    		"NSGA2NoSeedingAccuracy",//  
//	    		"NSGA2-NRRNoSeedingAccuracy",// Do NOT Remove Reduplicated solutions
		};
		
		String [] indicators = new String [] {
		  		"QDscoreDominanceFitness", // RQ4
//				"QDscoreFitness",
//	    		"QDscore2wiseCoverage",
//	    		"2wiseCoverage",    		
//	    		"QDscoreDiversity",
//	    		"Diversity",
//	    		"QDscoreFaultRate",
//	    		"FaultRate", 
//				"First100",// RQ2
	    
		};
				
		(new CollectionDataForTest(runs, expPath, problems,algorithms,indicators,t,nbProduts,timeMS)).execute();
	}
	
}
