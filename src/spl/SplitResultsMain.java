/**
 * This class is used to split results obtained by MAP-Elites into single task results
 * For example, the results obtained by MAP-elites are distribued in [minProds,maxProds] are splitted into 
 * [minProds,minProds+1,...,maxProds]
 */
package spl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.JMException;
import spl.utils.FileUtils;

public class SplitResultsMain {

	private String algName;
	private String path;
	public String[] problemList;
	private int minProds;
	private int maxProds;
	private int runs;
	
	public SplitResultsMain() {
		
	}
	
	public static void main(String[] args) throws Exception {
		SplitResultsMain sr = new SplitResultsMain();
		
		sr.path = "E:/javaWorkSpace/SPLTestingMAP/output/";		
		sr.algName = "MAPelites";
		sr.minProds = 40;
		sr.maxProds = 50;
		sr.runs = 10;
		
		sr.problemList= new String[]{
	    		
//				/******************* Small-scale*****************  */
//				"CounterStrikeSimpleFeatureModel", //24
//				"HiPAcc",//31
//				"SPLSSimuelESPnP",//32
//				"JavaGC",//39
//				"Polly", //40
//				"DSSample", //41    
//				"VP9",//42
//				"WebPortal",//43
//				"JHipster", //45
//				"Drupal", //48
//				"SmartHomev2.2",//60
//				"VideoPlayer",//71
//				"Amazon",//79
				"ModelTransformation", //88
//				"CocheEcologico", //94
//				"Printers",//172
//				"fiasco_17_10",//234
//				"uClibc-ng_1_0_29",//269
//				"E-shop",//290
//				"toybox",//544
//				"axTLS", // 684
//				"financial",//771 
//				"busybox_1_28_0", // 998  			
////	  			/***************Median-scale******************** */
//				"mpc50", //1213
//				"ref4955",//1218  		
//				"linux", //1232
//				"csb281", //1233
//				"ecos-icse11", //1244
//				"ebsa285", //1245
//				"vrc4373", // 1247
//				"pati", // 1248
//				"dreamcast", //1252
//				"pc_i82544", //1259
//				"XSEngine",  //1260
//				"refidt334", //1263
//				"ocelot", //1266
//				"integrator_arm9", //1267
//				"olpcl2294", //1273
//				"olpce2294", //1274
//				"phycore", // 1274
//				"hs7729pci", //1298
//				"freebsd-icse11",//1396
//				"fiasco",//1638
//				"uClinux",//1850
//				"Automotive01", //2513 
//				"SPLOT-3CNF-FM-5000-1000-0,30-SAT-1",// 5000  			
//	  			 /*******************  Large-scale ***************** */
//				 "busybox-1.18.0",//6796
//				 "2.6.28.6-icse11", //6888
//				 "uClinux-config", //11254
//				 "buildroot", // 14910

	    		};
	  	MetricsUtil utils = new MetricsUtil();                             	;
               
		// Split
		for (int i = 0; i < sr.problemList.length;i++) { // for each problem
			String readDir = sr.path + sr.algName + "/" + sr.problemList[i]+".dimacs/Samples/Min" 
								+ sr.minProds + "Max"+sr.maxProds+"prods";
			System.out.println(readDir);
					
			// for each run
			for (int r=0; r<sr.runs;r++) {
				
				double[][] fitness =  utils.readFront(readDir + "/Fitness." + r);				 
					
				for (int j = sr.minProds; j<= sr.maxProds;j++) {
					
					String writeDir = sr.path + sr.algName + "/" + sr.problemList[i]+".dimacs/Samples/Min" 
							+ j + "Max"+j+"prods/";					
					
					sr.checkDirectory(writeDir);					
					sr.resetFile(writeDir+"/Fitness." + r);
								
					sr.writeDataToFile(writeDir+"/Fitness." + r, fitness[j-sr.minProds][0]);
				}							
			
		     }
			
		}
		
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
	 * 
	 * @param file
	 */
	public void resetFile(String file) {
		File f = new File(file);
		if(f.exists()){			
			System.out.println("File " + file + " exist.");

			if(f.isDirectory()){
				System.out.println("File " + file + " is a directory. Deleting directory.");
				if(f.delete()){
					System.out.println("Directory successfully deleted.");
				}else{
					System.out.println("Error deleting directory.");
				}
			}else{
				System.out.println("File " + file + " is a file. Deleting file.");
				if(f.delete()){
					System.out.println("File succesfully deleted.");
				}else{
					System.out.println("Error deleting file.");
				}
			}			 
		}else{
			System.out.println("File " + file + " does NOT exist.");
		}
	} // resetFile
	
	private void checkDirectory(String path) {
		File experimentDirectory;

		experimentDirectory = new File(path);
		if (experimentDirectory.exists()) {
			System.out.println("Experiment directory exists");
			if (experimentDirectory.isDirectory()) {
				System.out.println("Experiment directory is a directory");
			} else {
				System.out.println("Experiment directory is not a directory. Deleting file and creating directory");
			}
			experimentDirectory.delete();
			new File(path).mkdirs();
		} // if
		else {
			System.out.println("Experiment directory does NOT exist. Creating");
			new File(path).mkdirs();
		} // else
	} // checkDirectories
} // class
