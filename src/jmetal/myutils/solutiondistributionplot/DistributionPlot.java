/**
 * This class plots given algorithms' parallel Coordinates 
 * Pareto fronts for comparing , often a control algorithm is needed,or a new algorithm
 * Created by Yi Xiang 2013/12/7
 */
package jmetal.myutils.solutiondistributionplot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import jmetal.myutils.FileUtils;
import jmetal.myutils.QSort;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.Configuration;

/**
 * @author Administrator
 *
 */
public class DistributionPlot {
	private String experimentName_ ;
	private String[] algName_;
	private String problemName_="" ;
	private String[] dataPath_ ;
	private String[] storePath_;
	private int numberofRuns_ ;
	private String indicatorName_;
	private int objectives_;
	/**
	 * 
	 */
	public DistributionPlot(String experimentName,String problemName, int numberofRuns,
			String indicatorName,String[] algName,int objectives) {
		
		experimentName_ = experimentName;
		problemName_ = problemName;
		numberofRuns_ = numberofRuns;
		indicatorName_ = indicatorName;
		algName_ = algName;		
		dataPath_ = new String [algName_.length];
		storePath_ = new String [algName_.length];
		objectives_ = objectives;
		// TODO Auto-generated constructor stub
	}
	
	 public void execute() throws ClassNotFoundException{
		MetricsUtil meticsUtil = new MetricsUtil();		
	    	
		String basdPath = experimentName_ + "/distributionFigures/";    
		
    	File experimentDirectory = new File(basdPath);
    	
    	if (experimentDirectory.exists()) {
			System.out.println("Experiment directory exists");
			if (experimentDirectory.isDirectory()) {
				System.out.println("Experiment directory is a directory");
			} else {
				System.out.println("Experiment directory is not a directory. Deleting file and creating directory");
			}
			experimentDirectory.delete();
			new File(basdPath).mkdirs();
		} // if
		else {
			System.out.println("Experiment directory does NOT exist. Creating");
			new File(basdPath).mkdirs();
		} // else
       	    	
    	String storePath = basdPath + algName_[0] +"_"+ problemName_.substring(0, problemName_.length()-7)+ "_SolutionDistrFig.m"; // The first algorithm 
    	FileUtils.resetFile(storePath);
    	
    	try {
		      /* Open the file */
		      FileOutputStream fos   = new FileOutputStream(storePath)     ;
		      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
		      BufferedWriter bw      = new BufferedWriter(osw)        ;
		      /**
		       * Plot figure of the true front
		       */
		      bw.write("clear ");    bw.newLine();
		      bw.write("clc ");     bw.newLine();
		      bw.write("h=figure;");  bw.newLine();
		      bw.write("set(h,'position',[300,300,390,400]) \n");

		      /* Close the file */
		      bw.close();
		    }catch (IOException e) {
		      Configuration.logger_.severe("Error acceding to the file");
		      e.printStackTrace();
		  }//catch
    	    	
    	
    	for (int k = 0;k<algName_.length;k++){
    		
	    	String indicatorPath = experimentName_ + "output/" + algName_[k] + "/" + problemName_+"/"+ indicatorName_;
	    	System.out.println(indicatorPath);
	    	double [][] indicatorValues = meticsUtil.readFront(indicatorPath);

	    	/*
			 *  Find the median indicator 
			 */
	    	double [] values = new double [indicatorValues.length];
	    	int [] index = new int  [indicatorValues.length];
	    	double mean = 0.0;
	    	
	    	for (int i = 0; i < numberofRuns_;i ++) {	    		
	    		values [i] = indicatorValues[i][0];	  
	    		mean = mean + values [i];
	    	}
	    		
	    	mean = mean/numberofRuns_;
	    	
	    	QSort.quicksort(values, index, 0, indicatorValues.length - 1);
	    		    	
	    	double maxVal, minVal;
	    	int bestIndex;
	    	
	    	maxVal =  values[indicatorValues.length - 1];
	    	minVal =  values[0];	    	
	    	
//	    	double median ;
//	    	
//	    	// Median
//			if (indicatorValues.length % 2 != 0) {
//				median = (Double) values[indicatorValues.length/2];
//			} else {
//				median = ((Double) values[indicatorValues.length/2 - 1] +
//						(Double)values[indicatorValues.length/2]) / 2.0;
//			} // if
	    	
	    	double bestDiff = Math.abs(indicatorValues[0][0] - mean);
	    	bestIndex = 0;
	    	
	    	for (int i = 1; i < numberofRuns_;i++){
	    		 double diff = Math.abs(indicatorValues[i][0]  - mean);
	    		 if (diff < bestDiff){
	    			 bestDiff = diff;
	    			 bestIndex = i;
	    		  }
	    	}    
	    	
	    	System.out.println("-------------------BestIndex = "  + bestIndex);
	    	
	    	//**********************************************************
	       dataPath_[k] = experimentName_ +  "output/" + algName_[k] + "/" + problemName_+"/Fitness." + bestIndex;
	       
	       double [][] data = meticsUtil.readFront(dataPath_[k]);      
	       
	       int      rows = data.length;
	       
			String strToWrite ;	
		
			try {
			      /* Open the file */
			      FileOutputStream fos   = new FileOutputStream(storePath,true)     ;
			      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
			      BufferedWriter bw      = new BufferedWriter(osw)        ;
			      						    
			      // write front
			      bw.write("data" +(k+1)+" = [ ");
			      
			      for(int i=0;i<rows;i++){
			    	  strToWrite ="";
			    	 
			    	 strToWrite = strToWrite + String.valueOf(data[i][0])+" ";			    
			    	  bw.write(strToWrite);
			    	  bw.newLine();
			      }
			      
			    bw.write("];");
			    bw.newLine();
			     
			    /* Close the file */
			      bw.close();
			    }catch (IOException e) {
			      Configuration.logger_.severe("Error acceding to the file");
			      e.printStackTrace();
			    }//catch
			    
			    
		  	}//for 
			
    	try {
		      /* Open the file */
		      FileOutputStream fos   = new FileOutputStream(storePath,true)     ;
		      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
		      BufferedWriter bw      = new BufferedWriter(osw)        ;

		      bw.write("plot(data1,'-ro','markersize',8,'linewidth',1.5,'markerfacecolor','w')%[0.925,0.839,0.839]\n");
		      bw.write("hold on\n");
		      bw.write("plot(data2,'--bs','markersize',8,'linewidth',1.5,'markerfacecolor','w')%[0.729,0.831,0.957]\n");

//		      bw.write("for i=1:6\n");	
//			  bw.write("\t text(i,data1(i)-0.15,num2str(data1(i),'%.2f'),'color','r','fontname','Times New Roman','fontsize',14,'ROtation',60)\n");
//			  bw.write(" \ttext(i,data2(i)-0.15,num2str(data2(i),'%.2f'),'color','b','fontname','Times New Roman','fontsize',14,'ROtation',60)\n");
//			  bw.write("end\n");

			  bw.write("legend({'" + algName_[0] + "','" + algName_[1] + "'},'Orientation','horizontal','Location','northwest','box','off')  \n");
			  bw.write("set(gca,'fontname','times new roman')  \n");
			  bw.write("set(gca,'xtick',[1:6])  \n");
			  bw.write("set(gca,'xticklabel',{'TS_1','TS_2','TS_3','TS_4','TS_5','TS_6'})  \n");
			  bw.write("ylabel('Fitness') \n");
			  bw.write("xlim([0.7,6.3]); \n");
    		  //%ylim([0,1.2*max(max(sumdata))]);
    		  bw.write("grid on \n");
			  bw.write("set(gca,'FontSize',18) \n");	    		  
		    
			    /* Close the file */
			      bw.close();
			    }catch (IOException e) {
			      Configuration.logger_.severe("Error acceding to the file");
			      e.printStackTrace();
	     }//catch  
    	
		System.out.println("File has been written!");		
    	
	 }//execute

	
    public double [][] readBoundsFromReferenceFront() {
	 
		 double [][] minMax = new double [objectives_][2];
		 String basePath = experimentName_ + "referenceFronts/";
//		 String basePath = "./paretoFronts/";
		 
			 String readPath = basePath + problemName_ + ".rf";
			 System.out.println("front Path = " + readPath);
			 double [][] front =  FileUtils.readMatrix(readPath);
			 
			 for (int obj = 0 ; obj < objectives_;obj ++ ) {
				 minMax[obj][0] = front[0][obj];
				 minMax[obj][1] = front[0][obj];
				 
				 for (int k = 1; k < front.length; k++) {
					 if ( front[k][obj] < minMax[obj][0]) {
						 minMax[obj][0] = front[k][obj];
					 }
					 
					 if ( front[k][obj] > minMax[obj][1]) {
						 minMax[obj][1] = front[k][obj];
					 }
				 }	
		
		 }
		 
		 return minMax;
	 }
    
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

		public String getProblemName_() {
			return problemName_;
		}

		public void setProblemName_(String problemName_) {
			this.problemName_ = problemName_;
		}

		public String[] getdataPath_() {
			return dataPath_;
		}

		public void setdataPath_(String[] dataPath_) {
			this.dataPath_ = dataPath_;
		}

		
		public int getNumberofRuns_() {
			return numberofRuns_;
		}

		public void setNumberofRuns_(int numberofRuns_) {
			this.numberofRuns_ = numberofRuns_;
		}

		public String getIndicatorName_() {
			return indicatorName_;
		}

		public void setIndicatorName_(String indicatorName_) {
			this.indicatorName_ = indicatorName_;
		}	
	
}
