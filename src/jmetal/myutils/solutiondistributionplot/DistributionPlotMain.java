package jmetal.myutils.solutiondistributionplot;

import jmetal.util.JMException;

public class DistributionPlotMain {

	/**
	 * @param args
	 * @throws JMException 
	 * @throws ClassNotFoundException 
	 * @throws MatlabConnectionException 
	 * @throws MatlabInvocationException 
	 */
	public static void main(String[] args) throws JMException, ClassNotFoundException {
		String [] algNames = new String[]{	
//	    		"MPNoSeedingAccuracy",//RQ1: 
//	    		"MPNoSeedingAccuracySingleTask",//RQ1	
//	    		"MPNoSeedingDiversity",// RQ1:Noseeding + diversity fitness
//	    		"MPNoSeedingDiversitySingleTask",//RQ1: Noseeding + diversity fitness
//			    "MPSeedingAccuracy",// Seeding + accuracy fitness+ SAT4J  	
//			    "MPSeedingAccuracySingleTask",// Seeding + accuracy fitness+ SAT4J +SingleTask
	   		    "MPSeedingDiversity",// Seeding + diversity fitness	
	   		    "MPSeedingDiversitySingleTask",// Seeding + diversity fitness 
		};
		
		String [] problemNames = new String[]{
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
		};

		/*
		 * The following should be tuned manually
		 */
		for (int i =0;i<problemNames.length;i++) {
			problemNames [i] = problemNames [i] + ".dimacs";
		}
		
		String experimentName = "G:/javaWorkSpace/SPLTestingMAP/";

		int numberofRuns = 30;
		String indicatorName = "QDscoreFitness";
		
		for(int j=0;j<problemNames.length;j++){
			String problemName = problemNames[j];
			DistributionPlot prlPlot = new DistributionPlot(experimentName,problemName,numberofRuns,indicatorName,algNames,0);	
			prlPlot.execute();
		}			

	}
	
}
