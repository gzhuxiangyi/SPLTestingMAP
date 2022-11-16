/**
 * This class 实现数据的自动整理，便于采用WFG方法计算HV值（linuex系统下进行计算）
 * By Yi Xiang
 * 1. 运行该程序，汇总计算HV的数据，根目录为HVResults
 * 2. 将汇总的数据复制到虚拟机的HVComputation目录下
 * 3. 编译make march=native，运行命令./wfg，即可产生HV
 * 4. 将文件从虚拟机复制回jmetal即可生成latex表格
 */
package jmetal.myutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import jmetal.myutils.FileUtils;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.JMException;

/**
 * @author Administrator
 *
 */
public class WFG_HV_DataCollectR1 {
	private String experimentName_ ;
	private String[] algName_;
	private String[] problemNames_;
	private String[] paretoFrontFile_;
	private int numberofRuns_ ;
	private int objectives_;
	/**
	 * 
	 */
	public WFG_HV_DataCollectR1(String experimentName,String[] problemNames, String[] paretoFrontFile, int numberofRuns,
			String[] algName,int objectives) {
		
		experimentName_ = experimentName;
		problemNames_ = problemNames;
		paretoFrontFile_ =  paretoFrontFile;
		numberofRuns_ = numberofRuns;	
		algName_ = algName;	
		objectives_ = objectives;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param args
	 * @throws JMException 
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 * @throws MatlabConnectionException 
	 * @throws MatlabInvocationException 
	 */
	public static void main(String[] args) throws JMException, ClassNotFoundException, InterruptedException {
		
		int objectives = 4;
		
		String [] algNames = new String[]{
				
	    		// -----------------The following are used in R1----------------------
	    		"Initial+SATVaEA", // the flips is set to 3w, the initial population is different among algorithms
	    		"Initial+PaD+SATVaEA", // the flips is set to 3w, all the places are PaD
	    		"Initial+ProbSATVaEA", // The algorithm in EMSE paper  
	    		"Initial+PaD+ProbSATVaEA", // The algorithm in EMSE paper, all the places are PaD  
	    		
				
//	    		"SATVaEA", // The algorithm in TOSEM paper
//	    		"SATVaEADiverse", // The algorithm in TOSEM paper diverse SAT4J
//	    		"ProbSATVaEA", // The algorithm in EMSE paper       		
//	    		"ProbSATVaEADiverseSAT4J", // Using only diversified SAT4J    		
//	    		"ProbSATVaEADiverseSAT4JProbSAT",// Using both diversified SAT solvers(with a probability)  
	 	    		
	    		
//	    		"SATVaEASAT4J", // 	LSSATVaEA_Problem(fm,0);
//	    		"SATVaEAMiniSat", // 	LSSATVaEA_Problem(fm,1);
//	    		"SATVaEAPlingeling", // 	LSSATVaEA_Problem(fm,2);
//	    		"SATVaEAGlucose", // 	LSSATVaEA_Problem(fm,3);
//	    		"SATVaEA", // original
//	    		"ProbSATVaEA", // probSAT
//	    		
//	    		"ProbSATVaEAFix", // fix using ProbSAT, i.e., theta = 1.0
//	    		"ProbSATVaEARand", // randoming using ProbSAT, i.e., theta = 0.0
//	    		
//	    		"SATVaEAFix", // fix using WalkSAT, i.e., theta = 1.0
//	    		"SATVaEARand", // randoming using WalkSAT, i.e., theta = 0.0
		};
		
		String [] problemNames = new String[]{
				/******************* Small-scale*****************  */
				"CounterStrikeSimpleFeatureModel", //24
				"HiPAcc",//31
				"SPLSSimuelESPnP",//32
				"JavaGC",//39
				"Polly", //40
//				"DSSample", //41    
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
//	  			/***************Median-scale******************** */
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
////	  			 /*******************  Large-scale ***************** */
				 "busybox-1.18.0",//6796
				 "2.6.28.6-icse11", //6888
				 "uClinux-config", //11254
				 "buildroot", // 14910
				 "freetz", //31012	
				//------------------------------------------------------------------------
		};
				
		 String [] paretoFrontFile = new String[]{  

		  };
		 
		/*
		 * The following should be tuned manually
		 */

		String experimentName = "./Experiment/SATVaEAPaDStudy/";
//		 String experimentName = "./jmetalExperiment/PaRPEAStudyConvergence/M=" + objectives;
		int numberofRuns = 30;			// 
		
		/*
		 * End
		 */
		
		WFG_HV_DataCollectR1 hvData = new WFG_HV_DataCollectR1(experimentName,problemNames,paretoFrontFile,numberofRuns,
				                    algNames,objectives);				
		hvData.execute();			
					
	}
	
	/**
	 * Execute 
	 * @throws ClassNotFoundException
	 * @throws InterruptedException 
	 */
	
	 public void execute() throws ClassNotFoundException, InterruptedException{		

		 String basePath = "./Experiment/SATVaEAPaDStudy/";
		 
    	for (int k = 0; k < algName_.length;k++){ // for each algorithm
    		
    		for (int i = 0;i < problemNames_.length;i++) { // for each problem
    			
    			// Write into 
    			String writePath = basePath + "/HVResults/SATVaEAPaDStudy/" + algName_[k] + "/" + problemNames_[i];  // 

    			FileUtils.CheckDir(writePath);
    			
    			writePath = writePath  +"/allbest.txt";
    			
    			FileUtils.resetFile(writePath);
    			    			
    			for (int j = 0; j < numberofRuns_;j++) { // for each run
    				String readPath = experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i]+"/FUN."+ j;
//    				System.out.println(readPath);
    				double [][] data = FileUtils.readMatrix(readPath);    				
    				    
    				if  (problemNames_[i].equalsIgnoreCase("DTLZ1")){ 
    					
 		    			for (int r = 0; r < data.length; r ++) {
 		    				for(int o = 0; o < objectives_;o++) {
 		    					data[r][o] = data[r][o]/0.5;
 		    					
 	    	    			}
 		    			} // for 
 		    	 		    			
    				} else if (problemNames_[i].equalsIgnoreCase("DTLZ2") || problemNames_[i].equalsIgnoreCase("DTLZ3")
    						 || problemNames_[i].equalsIgnoreCase("DTLZ4") ||problemNames_[i].equalsIgnoreCase("ConvexDTLZ2")
    						 || problemNames_[i].equalsIgnoreCase("ConvexDTLZ4") || problemNames_[i].contains("DTLZ3P") ){
	    				// Do nothing. Objective values are in [0,1]
    				
	    			} else if (problemNames_[i].equalsIgnoreCase("DTLZ5") || problemNames_[i].equalsIgnoreCase("DTLZ6")
   						 || problemNames_[i].equalsIgnoreCase("DTLZ7")) {
	    				
	    			   Hypervolume indicators = new Hypervolume();
	    			   MetricsUtil utils_ = new jmetal.qualityIndicator.util.MetricsUtil();
	    			   
	    			   double[][] trueFront =  indicators.utils_.readFront("./paretoFronts/DTLZ&WFG/" + paretoFrontFile_[i]);
	    			   
	    			   double[] maximumValue = utils_.getMaximumValues(trueFront, objectives_);
	    			   double[] minimumValue = utils_.getMinimumValues(trueFront, objectives_);
	    			   
	    			   data  = utils_.getNormalizedFront(data, maximumValue, minimumValue);

	    			} else if (problemNames_[i].equalsIgnoreCase("MinusDTLZ1") ) {
	    				
	    				for (int r = 0; r < data.length; r ++) {
 		    				for(int o = 0; o < objectives_;o++) {
 		    					data[r][o] = (data[r][o] - (-551.15066)) / (0 - (-551.15066));
 	    	    			}
 		    			} // for 
	    	
	    			} else if (problemNames_[i].equalsIgnoreCase("MinusDTLZ2") || problemNames_[i].equalsIgnoreCase("MinusDTLZ4")) {
	    				
	    				for (int r = 0; r < data.length; r ++) {
 		    				for(int o = 0; o < objectives_;o++) {
 		    					data[r][o] = (data[r][o] - (-3.5))/ (0 - (-3.5));
 	    	    			}
 		    			} // for 
	    		
	    			} else if (problemNames_[i].equalsIgnoreCase("MinusDTLZ3") ) {
	    				
	    				for (int r = 0; r < data.length; r ++) {
 		    				for(int o = 0; o < objectives_;o++) {
 		    					data[r][o] = (data[r][o] - (-2203.603))/ (0 - (-2203.603));
 	    	    			}
 		    			} // for 
	    	
	    			} else if (problemNames_[i].length() >= 4 && problemNames_[i].substring(0,3).equalsIgnoreCase("WFG")){ 
    	    			
	    				for (int r = 0; r < data.length; r ++) {
    	    				for(int o = 0; o < objectives_;o++) {
    	    					data[r][o] = data[r][o]/(2*(o+1));
        	    			}
    	    			}    
    	    	    			
	    			} else if (problemNames_[i].length() >= 9 && problemNames_[i].substring(0,8).equalsIgnoreCase("MinusWFG")){ 
    	    			
	    				for (int r = 0; r < data.length; r ++) {
    	    				for(int o = 0; o < objectives_;o++) {
    	    					data[r][o] = (data[r][o] - (-2*(o+1) - 1)) / (-1 - (-2*(o+1) - 1) );
        	    			}
    	    			}  
	    			
	    				
    	    		} else if (problemNames_[i].equalsIgnoreCase("ScaledDTLZ1")){
    	    			double factor ;
	    				if (objectives_ == 3)
	    					factor = 10.0;
	    				else if (objectives_ == 5)
	    					factor = 10.0;
	    				else if (objectives_ == 8)
	    					factor = 3.0;
	    				else if (objectives_ == 10)
	    					factor = 2.0;
	    				else if (objectives_ == 15)
	    					factor = 1.2;
	    				else if (objectives_ == 20)
	    					factor = 1.2;
	    				else if (objectives_ == 25)
	    					factor =  1.1;
	    				else 
	    					factor = 1.0;
    	    			 
    	    			for (int r = 0; r < data.length; r ++) {
    	    				for(int o = 0; o < objectives_;o++) {
    	    					
    	    					data[r][o] = data[r][o]/(Math.pow(factor, o));
    	    					data[r][o] = data[r][o]/0.5;
        	    			}
    	    			}
    	    		}else if (problemNames_[i].equalsIgnoreCase("ScaledDTLZ2")){
    	    			double factor ;
	    				if (objectives_ == 3)
	    					factor = 10.0;
	    				else if (objectives_ == 5)
	    					factor = 10.0;
	    				else if (objectives_ == 8)
	    					factor = 3.0;
	    				else if (objectives_ == 10)
	    					factor = 3.0;
	    				else if (objectives_ == 15)
	    					factor = 2.0;
	    				else if (objectives_ == 20)
	    					factor = 1.2;
	    				else if (objectives_ == 25)
	    					factor =  1.1;
	    				else 
	    					factor = 1.0;
    	    			 
    	    			for (int r = 0; r < data.length; r ++) {
    	    				for(int o = 0; o < objectives_;o++) {
    	    					
    	    					data[r][o] = data[r][o]/(Math.pow(factor, o));
    	    					
        	    			}
    	    			}
//    	    		} else if (problemNames_[i].substring(0,9).equalsIgnoreCase("NormalWFG")){ 
//    	    			//System.out.println("NormalWFG");
//    	    		} // if 
    	    			
        	    	} else { 
        	    	   	
        	    	   Hypervolume indicators = new Hypervolume();
 	    			   MetricsUtil utils_ = new jmetal.qualityIndicator.util.MetricsUtil();
 	    			   
 	    			   double[][] trueFront =  indicators.utils_.readFront(basePath + "/referenceFronts/" + problemNames_[i] + ".rf");
 	    			   
 	    			   double[] maximumValue = utils_.getMaximumValues(trueFront, objectives_);
 	    			   double[] minimumValue = utils_.getMinimumValues(trueFront, objectives_);
 	    			   
 	    			   System.out.println("-----------Problem name " + problemNames_[i]);
 	    			   System.out.println("Minimum objs are ");
 	    			   for (int c = 0; c < objectives_;c++) {
 	    				  System.out.println("objective " + c + "=" + minimumValue[c]);
 	    			   }
 	    			   System.out.println("Maximum objs are ");
	    			   for (int c = 0; c < objectives_;c++) {
	    				  System.out.println("objective " + c + "=" + maximumValue[c]);
	    			   }
 	    			   
 	    			   data  = utils_.getNormalizedFront(data, maximumValue, minimumValue);
    	    		} // if 	
    	    		
    				// 个数输出数据
    				
    				if (j < numberofRuns_ - 1) 
    					FileUtils.writeMatrix(writePath, data);
    				else 
    					FileUtils.writeMatrixEnd(writePath, data);
    				
    			} // For 
    			
    		} // for i
    	}// for k   
    	   	
    	
}//execute

	 public String getExperimentName_() {
			return experimentName_;
		}

		public void setExperimentName_(String experimentName_) {
			this.experimentName_ = experimentName_;
		}

		public String[] getAlgName_() {
			return algName_;
		}

		public void setAlgName_(String[] algName_) {
			this.algName_ = algName_;
		}
			
		
		public int getNumberofRuns_() {
			return numberofRuns_;
		}

		public void setNumberofRuns_(int numberofRuns_) {
			this.numberofRuns_ = numberofRuns_;
		}			
	
	
	  /**
	    * 使用文件通道的方式复制文件
	    * 
	    * @param s
	    *            源文件
	    * @param t
	    *            复制到的新文件
	    */
	    public void fileChannelCopy(File s, File t) {
	        FileInputStream fi = null;
	        FileOutputStream fo = null;
	        FileChannel in = null;
	        FileChannel out = null;
	        try {
	            fi = new FileInputStream(s);

	            fo = new FileOutputStream(t);
	            in = fi.getChannel();//得到对应的文件通道
	            out = fo.getChannel();//得到对应的文件通道
	            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {

	            try {
	                fi.close();
	                in.close();
	                fo.close();
	                out.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	 }
