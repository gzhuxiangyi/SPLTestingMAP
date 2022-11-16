/**
 * The class BoxPlot is used to plot boxplots for a set of algorithms, often controlled by a newly proposed algorithm.
 * The boxplots are stored in folder :experimentName/BoxPlot/cotrolalg/
 * Created by Yi Xiang
 * Last modified 2014/2/22
 */
package spl;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;

import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.Configuration;
import spl.utils.FileUtils;

/**
 * @author Administrator
 *
 */
public class ScatterPlotMain {
	String experimentName_;
	String [] algNames_;
	String controlAlg_;// The control algorithm, often be the newly proposed algorithms
	String [] proNames_;
	String [] indicatorNames_;
	int numberOfRuns_;
	int t_;
	int nproducts_;
	long timeMS_;
	/**
	 * 
	 */
	public ScatterPlotMain(String experimentName,String [] algNames,String controlAlg, String [] proNames, 
			String []indicatorNames,int numberOfRuns) {
		experimentName_ = experimentName;
		algNames_ = algNames;
		proNames_ = proNames;
		indicatorNames_ = indicatorNames;
		numberOfRuns_ = numberOfRuns;
		controlAlg_ = controlAlg;
		// TODO Auto-generated constructor stub
	}

	public ScatterPlotMain(String experimentName,String [] algNames,String controlAlg, String [] proNames, 
			String []indicatorNames,int numberOfRuns, int t, int nproducts, long timeMS) {
		experimentName_ = experimentName;
		algNames_ = algNames;
		proNames_ = proNames;
		indicatorNames_ = indicatorNames;
		numberOfRuns_ = numberOfRuns;
		controlAlg_ = controlAlg;
		t_ = t;
		nproducts_ = nproducts;
		timeMS_ = timeMS;
		// TODO Auto-generated constructor stub
	}	
	
	
	
	public void execut(){
		MetricsUtil meticsUtil = new MetricsUtil();
		NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2); 
     
		for (int k = 0;k < proNames_.length;k++){ // for each problem
			 FileUtils.CheckDir(experimentName_ + "/mBoxPlot/");    
			 String fmFileName = proNames_[k];
			 
			 String mFileName = "FM_" + fmFileName;
	         mFileName =  mFileName.replace('.', '_');
	         mFileName =  mFileName.replace('-', '_');
	         mFileName = mFileName.replace(',', '_');
	         
	         for(int j = 0;j < indicatorNames_.length; j++) {//for each indicator
	        	 
	        	 String indicator = indicatorNames_[j];
	        	 String storePath = experimentName_ + "/mBoxPlot/" + t_ + "wise/";
	        	 FileUtils.CheckDir(storePath);
	        	 
        		 storePath = storePath + mFileName + "_" + indicator +"_BoxPlot.m";
    	    	 FileUtils.resetFile(storePath);System.out.println(storePath); 
    	    	 
    	    	 double [][] indicatorValues = new double[numberOfRuns_][algNames_.length];	
    	    	 
    	    	 
    	    	 // Read data for each algorithm
    	    	 for(int i = 0;i < algNames_.length;i++) {//for each algorithm
    	    		 
    	    		String indicatorPath = experimentName_  + algNames_[i] + "/" +  proNames_[k] + "/"
    	    				+ t_ + "wise/" + nproducts_ + "prods/"+ timeMS_ + "ms/" + indicatorNames_[j];
    	    		
 					System.out.println(indicatorPath);
 					double [][] values = meticsUtil.readFront(indicatorPath);
// 					System.out.println(values.length);
 					for (int r = 0;r < numberOfRuns_;r++){
// 						System.out.println(values[r][0]);
 						indicatorValues[r][i] = values[r][0];
 					}//for r					
 					
    	    	 } // for i
    	    	 
    	    	 try {
    				 /* Open the file */
    			      FileOutputStream fos   = new FileOutputStream(storePath)     ;
    			      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
    			      BufferedWriter bw      = new BufferedWriter(osw)        ;
    			      
    			      bw.write("clear ");
    			      bw.newLine();
    			      bw.write("clc ");
    			      bw.newLine();
    			      bw.write("set(gca,'NextPlot','add','Box','on','Fontname','Times New Roman','FontSize',25,'LineWidth',1.3);");
    			      bw.newLine();
    			    		  
    			      bw.write("indicatorValues = [ ");
    			      String strToWrite;
    			      
    			      for(int ii=0;ii < numberOfRuns_;ii++){
    			    	  strToWrite ="";
    			    	  for(int jj=0;jj < algNames_.length;jj++){
    			    		  strToWrite = strToWrite +  nf.format(indicatorValues[ii][jj]) +" ";
    			    		  
    			    		 
    			    	  }
    			    	  bw.write(strToWrite);
    			    	  bw.newLine();
    			      }
    			      bw.write("]; ");
    			      bw.newLine();    			      
    			          			      
    			      bw.write("meanValue = mean(indicatorValues);");bw.newLine();
    			      
//    			      bw.write("plot(meanValue,'LineStyle','g--','MarkerType', 'o', 'MarkerFaceColor',m,"
//    			      		+ "'linewidth',2)");bw.newLine();
    			      		
    			      bw.write("plot(meanValue,'LineStyle','--','color','[0.07,0.21,0.14]','Marker', 'o', "
    			      		+ "'MarkerFaceColor','g','MarkerEdgeColor','[0.07,0.21,0.14]','linewidth',1.5)");
    			      bw.newLine();
    			      		
    			      bw.write("hold on");bw.newLine();
    			    		  
    			      bw.write("h = boxplot(indicatorValues," + "'sym'"+ "," + "'r*'," + "" + "'outliersize',3" +  ",'notch','on');");
    			      bw.newLine();
    			      
    			      bw.write("set(h,'LineWidth',2.5);");  bw.newLine();
    			      
//    			      bw.write(" tit = title('Parameter" + "');");
//    			      bw.newLine();
//    			      bw.write("set(tit,'fontsize',18)");
//    			      bw.newLine();
    			      bw.write("set(gca,'fontsize',25)");
    			      bw.newLine();
    			      String xtick = "[";
    			      String xtickLabel = "{'";
    			      for (int kk = 0; kk< algNames_.length-1;kk++ ) {
    			    	  xtick = xtick + (kk+1) + " ";
    			    	  xtickLabel = xtickLabel + algNames_[kk]+ "',' ";
    			      }
    			      xtick = xtick + algNames_.length + "]";
    			      xtickLabel = xtickLabel + algNames_[algNames_.length-1]+ "'}";
    			      bw.write("set(gca, 'XTick'," + xtick+")") ;
    			      bw.newLine();
    			      bw.write("set(gca,'XTickLabel',"+xtickLabel+")") ;
    			      bw.newLine();			      
    			      
//    			      xticklabel_rotate([1:12],60,{'dMOABC',' MOEAD',' SMPSO',' GDE3',' AbYSS',' CellDE',' IBEA',' MOCell',' OMOPSO',' NSGAII',' PAES',' SPEA2'},'interpreter','none')			      
    			      bw.write(" xl = xlabel('\\itr');");
    			      bw.newLine();			      
//    			      bw.write("set(xl,'fontsize',16)");
//    			      bw.newLine();
    			      bw.write("set(gca,'XTickLabel',{'0.0','0.1','0.2','0.3','0.4','0.5','0.6','0.7','0.8','0.9','1.0'})");
    			      bw.newLine();
    			      bw.write(" yl = ylabel('"+ indicatorNames_[j]+ " (%)');");
    			      bw.newLine();
    			      bw.write("set(yl,'fontsize',25)");
    			      bw.newLine();
//    			      bw.write("xticklabel_rotate(["+ "1:"+algNames_.length+"],30,"+ xtickLabel+ ",'interpreter','none')");
    			      bw.newLine();
    			      
    			      
    			      /* Close the file */
    			      bw.close();
    			    }catch (IOException e) {
    				      Configuration.logger_.severe("Error acceding to the file");
    				      e.printStackTrace();
    			    }//catch
    	    	 
	         } // for j  
			 
		} // for k	
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		    
	    String experimentName = "./outputParameter/";   
	    
	    int t   = 6; // t-wise
	    int nproducts = 100; // the number of products
	    long timeMS = (long) 6000; // the time allowed
		  
		int numberofRuns = 30;
		
		String [] algNames = new String[]{
	    		 "NSprobSATP=0.0", "NSprobSATP=0.1",
//				 "NSprobSATP=0.2", "NSprobSATP=0.3", "NSprobSATP=0.4", "NSprobSATP=0.5", 
//	    		 "NSprobSATP=0.6","NSprobSATP=0.7", "NSprobSATP=0.8", "NSprobSATP=0.9", "NSprobSATP=1.0", 
	    		
//	    		"NSprobSATNbr=2","NSprobSATNbr=10","NSprobSATNbr=20","NSprobSATNbr=30","NSprobSATNbr=40","NSprobSATNbr=50",
//	    		"NSprobSATNbr=60","NSprobSATNbr=70","NSprobSATNbr=80","NSprobSATNbr=90","NSprobSATNbr=100",
		};
		
		String [] problemNames = new String[]{				
	    		"CounterStrikeSimpleFeatureModel",//24
//				"SPLSSimuelESPnP", //32
//				"DSSample", //41
//				"WebPortal",//43    
//				"Drupal", //48
//				"ElectronicDrum",//52
//				"SmartHomev2.2",//60
//				"VideoPlayer", // 71
//				"Amazon", // 79
//				"ModelTransformation", // 88
//				"CocheEcologico",//94
//				"Printers",//	172	   	
				
//				// -------------30S------------
//				"E-shop",//	290		    			
//	  			"toybox", //544
//				"axTLS", //684  
				
//				// -------------600S,15 runs------------	
//				"ecos-icse11",// 1244 
//				"freebsd-icse11", // 1396 
//				"Automotive01", //2513 
//				"SPLOT-3CNF-FM-5000-1000-0,30-SAT-1",// 5000
//				"2.6.28.6-icse11", //6888
//				"Automotive02_V3",//18434, YES 
		};
		
		for (int i = 0; i < problemNames.length;i++) {
			problemNames [i] = problemNames [i] + ".dimacs";
		}
				
		
		String [] indicatorNames =new String[]{
	    		"Coverage",
				};		
		
		String controlAlg = "NSprobSAT";//
		
		ScatterPlotMain boxPlot = new ScatterPlotMain(experimentName,algNames,controlAlg,problemNames, 
				indicatorNames,numberofRuns, t, nproducts,timeMS);
		boxPlot.execut();
		// TODO Auto-generated method stub

	}

}
