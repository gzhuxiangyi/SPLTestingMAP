/**
 * Plot the distribution of samples
 */

package spl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import java.util.Set;
import jmetal.util.QSort;
import jmetal.qualityIndicator.util.MetricsUtil;
import spl.fm.Product;
import spl.techniques.SimilarityTechnique;
import spl.utils.FileUtils;


public class PlotDistributionMain {

	public int nbProds = 100;
	public long timeAllowed; 
	public String outputDir;
	public int runs;
	public String algName;
	 
	public PlotDistributionMain() {
		
	}
		
	
	public void execute(String fmFile) throws Exception{
		 String sNbProds = "" + nbProds;
         String fmFileName = new File(fmFile).getName();
         SimilarityTechnique st = new SimilarityTechnique(SimilarityTechnique.JACCARD_DISTANCE, SimilarityTechnique.NEAR_OPTIMAL_SEARCH);
                         
         String path = outputDir + algName  + "/" + fmFileName +"/Samples/"  + sNbProds + "prods/";
         FileUtils.CheckDir(path); 
             
         
         MetricsUtil meticsUtil = new MetricsUtil();		
         
         
         int numFeature = MAP_test.getInstance().numFeatures;        		 
         int lowerBound = (MAP_test.getInstance().mandatoryFeaturesIndices).size();
         int upperBound = numFeature - (MAP_test.getInstance().deadFeaturesIndices).size();
       
         System.out.println("lowerBound " + lowerBound);
         System.out.println("upperBound " + upperBound);
         
   	     List<Integer> difference = new ArrayList<Integer>();
   	     List<Integer> selectedNumList = new ArrayList<Integer>();
   	     selectedNumList.add(lowerBound);
   	     selectedNumList.add(upperBound);
   	  
   	  
         int r = 0;
         
         //Find a proper r
    	String indicatorPath = path +"/invertedDist";
    	System.out.println(indicatorPath);
    	double [][] indicatorValues = meticsUtil.readFront(indicatorPath);
         
    	double [] values = new double [indicatorValues.length];
    	int [] index = new int  [indicatorValues.length];
    	
    	for (int i = 0; i < runs;i ++) {	    		
    		values [i] = indicatorValues[i][0];	    		
    	}
    	
    	QSort.quicksort(values, index, 0, indicatorValues.length - 1);
    	    	
    	double maxVal, minVal;
    	int bestIndex;
    	
    	maxVal =  values[indicatorValues.length - 1];
    	minVal =  values[0];
    	    	
    	double median ;
    	
    	// Median
		if (indicatorValues.length % 2 != 0) {
			median = (Double) values[indicatorValues.length/2];
		} else {
			median = ((Double) values[indicatorValues.length/2 - 1] +
					(Double)values[indicatorValues.length/2]) / 2.0;
		} // if
    	
    	double bestDiff = Math.abs(indicatorValues[0][0] - median);
    	bestIndex = 0;
    	
    	for (int i = 1; i < runs;i++){
    		 double diff = Math.abs(indicatorValues[i][0]  - median);
    		 if (diff < bestDiff){
    			 bestDiff = diff;
    			 bestIndex = i;
    		  }
    	}    
             	
    	System.out.println("bestIndex" + bestIndex);
    	
        List<Product> products = null;    
                      
         // Load products                            
         products = MAP_test.getInstance().loadProductsFromFile(path + "Products." + bestIndex);
         int [] selectedNum = new int[products.size()];
                  
         Collections.sort(products); 
   	   	 for (int i = 0; i < products.size();i++) {
   	   	    selectedNum [i] = products.get(i).getSelectedNumber();
   	   	    selectedNumList.add(selectedNum [i]) ;
   	   	 }   
         
         for(int i = 1; i < products.size();i++) {
        	 difference.add(selectedNum [i] - selectedNum [i-1]);
         }                           
                  
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
         
        String dir = this.outputDir + "/mPlots/" + sNbProds + "prods/" + algName  + "/" + fmFileName +"/";
        FileUtils.CheckDir(dir);
        generateConvergenceMFiles(dir,selectedNumList,fmFileName,algName);
        generateConvergenceMFilesDistribution(dir,selectedNumList,frequency,fmFileName,algName);
        
	}
	
	
	 public void generateConvergenceMFiles(String path,  List<Integer> y, String fmFileName,String algName) throws Exception {
         
	 	 String mPath = path + "scatterPlotSelectedNum.m";
	 
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
          	              
          bw.write(" y = [");
	       
          for (int i = 0; i < y.size() - 1;i++) {
        	  bw.write(y.get(i) + ",");
          }
          
          bw.write(y.get(y.size() - 1) + "];" );
          bw.newLine();	
          
          bw.write("y = y + 0.001;"); bw.newLine();	
        		  
        bw.write(" b=bar(y,'linewidth',2,'edgecolor','[0.5 0 0]', 'facecolor', '[0.5 0 0]');");
	    bw.newLine();	
          
        bw.write(" tit = title('" + fmFileName.substring(0,fmFileName.length() - 7) + "');");
        bw.newLine();	
        bw.write("set(tit,'fontsize',20)");
        bw.newLine();
         
//        bw.newLine();
//        bw.write("xl = xlabel('Time (s)');");
//        bw.newLine();
//        bw.write("set(xl,'fontsize',20)");
//        bw.newLine();
        	         

        bw.write("yl = ylabel(' Difference');");
        bw.newLine();
        bw.write("set(yl,'fontsize',20)");
        bw.newLine();
        	        
        bw.write("axis([0 102 -0.2 max(y)+0.2]);");
        bw.newLine();
        
//        bw.write("set(gca,'xminortick','on') ;");
//        bw.newLine();
//        
//        bw.write("set(gca,'yminortick','on') ;");
//        bw.newLine();
        
        bw.close();         

    }
	 
 public void generateConvergenceMFilesDistribution(String path,  List<Integer> y, List<Integer> frequency, String fmFileName,String algName) throws Exception {
         
	 	 String mPath = path + "distributionPlot.m";
	 
         FileUtils.resetFile(mPath);     
         
         // Write header
          FileOutputStream fos   = new FileOutputStream(mPath,false)     ;
 	      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
 	      BufferedWriter bw      = new BufferedWriter(osw)        ;
 	    
 	            	       	
    	  bw.write("%% Plots for distribution");bw.newLine();	
    	  bw.write("figure ");
    	  bw.newLine();
	      bw.write("clear ");
	      bw.newLine();
	      bw.write("clc ");
	      bw.newLine();
	      
	      bw.write("set(gcf,'Position',[500 200 500 150])"); // ����ͼ����ʾλ��
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
          	              
          bw.write("y = [");
	       
          for (int i = 0; i < y.size() - 1;i++) {
        	  bw.write(y.get(i) + ",");
          }
          
          bw.write(y.get(y.size() - 1) + "];" );
          bw.newLine();	
          
          bw.write("frq = [");
	       
          for (int i = 0; i < frequency.size() - 1;i++) {
        	  bw.write(frequency.get(i) + ",");
          }
          
          bw.write(frequency.get(frequency.size() - 1) + "];" );
          bw.newLine();	
          
          bw.write("bound = y(1:2);"); bw.newLine();	
          bw.write("plot(bound,0,'bp','markersize',12,'markerfacecolor','[0 0.2 0.6]')"); bw.newLine();	
          bw.write("hold on"); bw.newLine();	
          bw.write("line([0,y(2)+1],[0, 0],'linewidth',1,'linestyle','--')"); bw.newLine();	
          
          bw.write("y(1:2) = [];") ; bw.newLine();	
          bw.write("y = unique(y)"); bw.newLine();	
          
          
          bw.write("for i=1:length(y)");bw.newLine();
//          bw.write("	 marker_size = 8 + (frq(i) - min(frq))/ (max(frq) - min(frq)) * (14-8);"); bw.newLine();	
          bw.write("	 marker_size = 8;"); bw.newLine();	
          
		  bw.write("	 plot(y(i),0,'r','marker','o','markersize',marker_size,'markerfacecolor','[1 0.6 0.6]','linewidth',1.2);"); bw.newLine();	
          
//          bw.write("	 plot(y(i),0,'g','marker','o','markersize',marker_size,'markerfacecolor','[0 1 0]','markerEdgeColor','[0 0.5 0]','linewidth',1.2);"); bw.newLine();	
          
		  bw.write("	 hold on");bw.newLine();	
		  bw.write("end");bw.newLine();	
        		
        		
//        bw.write("plot(y,0,'r','marker','o','markersize',8,'markerfacecolor','r');");
//	    bw.newLine();	
		  
	    bw.write("set(gca,'ytick',[],'yticklabel',[])"); bw.newLine();			  
		  
//        bw.write("tit = title('" + fmFileName.substring(0,fmFileName.length() - 7) + "');"); //NAME by FM names
        
        bw.write("tit = title('" + algName + "');"); //NAME by FM names
        
        bw.newLine();	
        bw.write("set(tit,'fontsize',20)");
        bw.newLine();
        
        bw.write("axis([bound(1),bound(2) -0.2 0.2])");
        bw.newLine();
        
        bw.write("box on");
        bw.newLine();
//        bw.newLine();
//        bw.write("xl = xlabel('Time (s)');");
//        bw.newLine();
//        bw.write("set(xl,'fontsize',20)");
//        bw.newLine();
        	         

//        bw.write("yl = ylabel(' Difference');");
//        bw.newLine();
//        bw.write("set(yl,'fontsize',20)");
//        bw.newLine();
        	        
//        bw.write("axis([0 102 -0.2 max(y)+0.2]);");
//        bw.newLine();
        
//        bw.write("set(gca,'xminortick','on') ;");
//        bw.newLine();
//        
//        bw.write("set(gca,'yminortick','on') ;");
//        bw.newLine();
        
        bw.close();         

    }

  /**
   * Main method
   * @param args
 * @throws Exception 
   */
  public static void main(String[] args) throws Exception {
    PlotDistributionMain gfr = new PlotDistributionMain();
    
    String [] fms = {

//    		 "lrzip",
//			 "LLVM",
//			 "X264",
//			 "Dune",
//			 "BerkeleyDBC",
//			 "HiPAcc",
//			 "JHipster",
//			 "Polly",
//			 "7z",
//			 "JavaGC",
//			 "VP9",					 
//			 "fiasco_17_10",
//			 "axtls_2_1_4",
//			 "fiasco",
//			 "toybox",
//			 "axTLS",
//			 "uClibc-ng_1_0_29",
//			 "toybox_0_7_5",
//			 "uClinux",
//			 "ref4955",
//			 "adderII",			 
//			 "ecos-icse11",
//			 "m5272c3",
//			 "pati",
//			 "olpce2294",
//			 "integrator_arm9",
//			 "at91sam7sek",
//			 "se77x9",
//			 "phycore229x",
//			 "busybox-1.18.0",
//			 "busybox_1_28_0",
//			 "embtoolkit",
//			 "freebsd-icse11",
//			 "uClinux-config",
//			 "buildroot",
//			 "freetz",
//			 "2.6.28.6-icse11",
//			 "2.6.32-2var",
//			 "2.6.33.3-2var"

			/******************* Small-scale*****************  */
//			"X264",//16
//			"Dune", //17
//			"BerkeleyDBC", //18
//			"lrzip", //20
			"CounterStrikeSimpleFeatureModel", //24
			"HiPAcc",//31
			"splSSimuelESPnP",//32
			"JavaGC",//39
			"Polly", //40
			"DSSample", //41    
			"VP9",//42
			"WebPortal",//43
			"7z",// 44
			"JHipster", //45
			"Drupal", //48
			"ElectronicDrum", //52
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

	gfr.nbProds     = 1000;
	gfr.outputDir   = "./output/";
	gfr.runs        = 30;
	
	
//	gfr.algName     = "UnPredictable";
//	gfr.algName     = "NSk=15";
//	gfr.algName     = "NSk=15NoWeight";
//	gfr.algName     = "DDBS";
//	gfr.algName     = "Smarchxy";
//	gfr.algName     = "unigenxy";
//	gfr.algName     = "NSk=15AutoT";
//	gfr.algName     = "NSk=15AutoTNoWeight";
//	gfr.algName     = "NSk=15FixedTimeNoWeight";
//	gfr.algName     = "NSk=15FixedTimeNoDelta";
//	gfr.algName     = "NSbSk=15AutoT";
//	gfr.algName     = "NSbSk=15AutoTDiverse";
	
//	gfr.algName     = "rSAT4J";
//	gfr.algName     = "ProbSATRerun";
	
//	gfr.algName     = "PaD+rSAT4Jnew";
	gfr.algName     = "PaD+ProbSATRerun";
	
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


