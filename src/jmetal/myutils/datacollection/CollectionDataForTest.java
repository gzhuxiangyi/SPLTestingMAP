package jmetal.myutils.datacollection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class CollectionDataForTest {
	int objectives_;
	int runs_;
	String expPath_;
	String [] problems_;
	String [] algorithms_;
	String [] indicators_;
	int t_;
	int nbProduts_;
	long timeMS_;
	
	
	public CollectionDataForTest(int objectives,int runs, String expPath,String[] problems,
			String[] algorithms,String[] indicators) {
		this.objectives_ = objectives;
		this.runs_ = runs;
		this.expPath_ = expPath;
		this.problems_ = problems;
		this.algorithms_ = algorithms;
		this.indicators_ = indicators;
		
		// TODO Auto-generated constructor stub
	}
	
	public CollectionDataForTest(int runs, String expPath,String[] problems,
			String[] algorithms,String[] indicators, int t, int nbProduts,long timeMS) {
		this.runs_ = runs;
		this.expPath_ = expPath;
		this.problems_ = problems;
		this.algorithms_ = algorithms;
		this.indicators_ = indicators;
		this.t_ = t;
		this.nbProduts_ = nbProduts;
		this.timeMS_ = timeMS;
		// TODO Auto-generated constructor stub
	}
	
	// 
	public void execute() throws Exception{
			
		String basedPath =  "G:/javaWorkSpace/SPLTestingMAP/latex/" +  "/DataForTest/";  
		
		for (int j = 0 ;j < indicators_.length;j++ ){ // for each indicator 
			String indicator = indicators_[j];
			
	    	File storeDirectory = new File(basedPath + indicator + "/");
	    	
	    	// Check path 
	    	if (storeDirectory.exists()) {
				System.out.println("Experiment directory exists");
				if (storeDirectory.isDirectory()) {
					System.out.println("Experiment directory is a directory");
				} else {
					System.out.println("Experiment directory is not a directory. Deleting file and creating directory");
				}
				storeDirectory.delete();
				new File(basedPath + indicator + "/").mkdirs();
			} // if
			else {
				System.out.println("Experiment directory does NOT exist. Creating");
				new File(basedPath + indicator + "/").mkdirs();
			} // else
		} 
    	
    	
		for (int i = 0;i < problems_.length;i++) { // for each problem 
			String problem = problems_[i] +".dimacs"; // problem name 
				
			for (int j = 0 ;j < indicators_.length;j++ ){ // for each indicator 
				String indicator = indicators_[j];
				
				String tempIndicator = indicator;				
				
				String tempProblem = problem;
				tempProblem = tempProblem.replace('.', '_');
				tempProblem = tempProblem.replace('-', '_');
				tempProblem = tempProblem.replace(',', '_');
				
				String storePath =   basedPath +  indicator + "/"  + "FM_"  + tempProblem +"_" + tempIndicator + ".csv";
				String storePath_m = basedPath + indicator + "/" + "FM_" + tempProblem +"_" + tempIndicator + ".m"; // Store .m
				String storePath_R = basedPath + indicator + "/" + "FM_" + tempProblem +"_" + tempIndicator + ".R"; // Store .R
				String funName_m =   "FM_" + tempProblem + "_" + tempIndicator;
				
				
				double [][] results = new double [runs_][algorithms_.length];
				
				for (int k = 0 ;k < algorithms_.length;k++ ){ // for each algorithm
					
//					String readPath = expPath_  + algorithms_[k]  + "/" + problem + "/Samples/"  +
//							          + nbProduts_ + "prods/" + indicator; 
					
					String readPath = expPath_  + algorithms_[k]  + "/" + problem + "/" + indicator; 
					
					double [][] data =  readFront(readPath);				
					
					System.out.println(algorithms_[k]);
					System.out.println(data.length);
					System.out.println(indicator);
					System.out.println(problem);
										
					
					for (int l = 0;l < runs_;l++) {
						results[l][k] = data[l][0];
					}
				} // for k
				
				this.printForTest(tempProblem,storePath, results);
				boolean minIndicator = true;
				
				if (indicator.equalsIgnoreCase("HV") || indicator.equalsIgnoreCase("PD") || indicator.equalsIgnoreCase("DCI")
						|| indicator.equalsIgnoreCase("Fitness") || indicator.equalsIgnoreCase("Coverage")
						|| indicator.equalsIgnoreCase("2wiseCoverage")|| indicator.equalsIgnoreCase("MeanFaultRate")
						|| indicator.equalsIgnoreCase("QDscoreFitness")|| indicator.equalsIgnoreCase("QDscore2wiseCoverage")
						|| indicator.equalsIgnoreCase("QDscoreDiversity")|| indicator.equalsIgnoreCase("Diversity")
						|| indicator.equalsIgnoreCase("QDscoreFaultRate")
						|| indicator.equalsIgnoreCase("QDscoreDominanceFitness")
						|| indicator.equalsIgnoreCase("FaultRate")) 
					
					minIndicator = false;
				else if (indicator.equalsIgnoreCase("IGD") || indicator.equalsIgnoreCase("GD")|| indicator.equalsIgnoreCase("RUNTIME")
						|| indicator.equalsIgnoreCase("invertedDist") || indicator.equalsIgnoreCase("Extension")
						 || indicator.equalsIgnoreCase("STD")  || indicator.equalsIgnoreCase("First100")) 
					minIndicator = true;
				else {
					System.out.println("Undefined indicator in CollectionDataForTest.java");
					throw new Exception("Undefined indicator in CollectionDataForTest.java");
				}
					
				printMscript(problem, storePath_m, funName_m, results, minIndicator);
				printRscript(tempProblem, storePath_R, funName_m, storePath,minIndicator);
			}			 
			
		} // for each problem
		
		printMscriptRunAll(basedPath) ;
		printRscriptRunAll(basedPath) ;
	}
	
	
	 /**
	   * Print *.R files
	   * @param problem
	   * @param fileName
	   * @param data
	   * @throws IOException
	   */
	  void printRscript(String problem, String fileName, String funName_m, String csvPath,boolean minIndicator) throws IOException {
			resetFile(fileName);
			FileWriter os = new FileWriter(fileName, true);
			String m;	
			// The header				
			os.write("# Compute the effect size \n");
			os.write("library(effsize) \n"); 			
			os.write("data = read.csv(\"" + funName_m + ".csv\", sep=\",\",header=T) \n");
			os.write("#print(data)\n"); 

			
			os.write("noRows = nrow(data) \n");		
			os.write("noCols = ncol(data) \n");			
			os.write("noAlgs = noCols-2 \n ");			
			os.write("writeData = \"" + funName_m + "\" \n" ); 
			
			os.write("for(i in 4:noCols-1) { \n"); 	
			os.write("\t effectSize = VD.A(data[1:noRows ,2],data[1:noRows ,i]) \n");
			os.write("\t writeData [2*i-4] = effectSize[4] \n");
			os.write("\t writeData [2*i-3] = effectSize[3] \n");
			os.write(" } \n");

			os.write("write.table(writeData ,file = \"effectSize.csv\",quote=F,sep=\",\",col.names=F,row.names = FALSE, append = T) \n ");
			os.write(" print( \"" + funName_m + " DONE!!! \")" + "\n");
			os.close();	
			
		} // printRscript	  
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
	
	
	 /**
	   * This method reads a Pareto Front for a file.
	   * @param path The path to the file that contains the pareto front
	   * @return double [][] whit the pareto front
	   **/
	  public double [][] readFront(String path) {
	    try {
	      // Open the file
	      FileInputStream fis   = new FileInputStream(path)     ;
	      InputStreamReader isr = new InputStreamReader(fis)    ;
	      BufferedReader br      = new BufferedReader(isr)      ;
	      
	      List<double []> list = new ArrayList<double []>();
	      int numberOfObjectives = 0;
	      String aux = br.readLine();
	      while (aux!= null && !"".equalsIgnoreCase(aux)) {
	        StringTokenizer st = new StringTokenizer(aux);
	        int i = 0;
	        numberOfObjectives = st.countTokens();
	        double [] vector = new double[st.countTokens()];
	        while (st.hasMoreTokens()) {
	          double value = new Double(st.nextToken());
	          vector[i] = value;
	          i++;
	        }
	        list.add(vector);
	        aux = br.readLine();
	      }
	            
	      br.close();
	      
	      double [][] front = new double[list.size()][numberOfObjectives];
	      for (int i = 0; i < list.size(); i++) {
	        front[i] = list.get(i);
	      }
	      return front;
	      
	    } catch (Exception e) {
	      System.out.println("InputFacilities crashed reading for file: "+path);
	      e.printStackTrace();
	    }
	    return null;
	  } // readFront
	  
	  void printForTest(String problem, String fileName, double[][] data) throws IOException {
			resetFile(fileName);
			FileWriter os = new FileWriter(fileName, true);
			String m;						

			os.write(problem + "+Run#,");
			 for(int i=0;i<algorithms_.length-1;i++) 
				 os.write(algorithms_[i].toString()+",");
			 
			 os.write(algorithms_[algorithms_.length-1].toString()+",");
			 os.write("\n");
			
			for(int i=0; i < data.length;i++){//for each run
				os.write("run " + i +",");
				for(int j=0;j < data[0].length; j++){//for each algorithm
					m = String.format(Locale.ENGLISH, "%20.20f", data[i][j]);
					if(j==data[0].length){
						os.write("\n");
					}
					else
						os.write(m+",");
				}
				os.write("\n");
			}
			
			os.write("\n");		

			os.close();
		} // printForTest
	  
	  /**
	   * Print .m files
	   * @param problem
	   * @param fileName
	   * @param data
	   * @throws IOException
	   */
	  void printMscript(String problem, String fileName, String funName_m, double[][] data,boolean minIndicator) throws IOException {
			resetFile(fileName);
			FileWriter os = new FileWriter(fileName, true);
			String m;	
			// The header
			os.write("%% " + fileName);
			os.write("\n");	
			os.write("%% Compare the algorithm in the first colomn with each algorithm to see whether the medians are different or not \n ");
			os.write("function symbol = " + funName_m + "()\n");	
			os.write("clear;clc \n ");
			os.write("format long;\n ");
			os.write("%% ");			
			for(int i=0;i<algorithms_.length;i++) 
				 os.write(algorithms_[i].toString()+" ");			 
	
			os.write("\n");
			 
			os.write("data = [");
			os.write("\n");	 
			
			for(int i=0; i < data.length;i++){//for each run			
				for(int j=0;j < data[0].length; j++){//for each algorithm
					m = String.format(Locale.ENGLISH, "%.6f", data[i][j]);
					if(j+1==data[0].length ){
						os.write( m + " %#run" + i );
					}
					else
						os.write(m+" ");
				}
				os.write("\n");
			}
			
			os.write("];");	
			os.write("\n");	
			
			os.write("p = -1* ones(size(data,2) ,1); \n");		
			os.write("h = -1* ones(size(data,2) ,1); \n");	
			
			os.write("for i=2:size(data,2)\n");			
			os.write("[p(i),h(i)] = ranksum(data(:,1),data(:, i)); \n");		
			os.write("[p_left(i),h_left(i)] = ranksum(data(:,1),data(:, i),'tail','left'); \n");		
			os.write("[p_right(i),h_right(i)] = ranksum(data(:,1),data(:, i),'tail','right'); \n");		
			os.write("end\n");
			
			os.write("mdn = median(data);\n");
			
			os.write("symbol = 'n';\n");
			
			if (minIndicator == true)  {
				os.write("for i=2:size(data,2)\n");
				os.write("if p(i) > 0.05 || h(i) == 0 \n");
				os.write("\t symbol(i) = '='; \n");
				os.write("else \n");
				os.write("\t if h_left(i) == 1 \n"); // data(i) > data(1)
				os.write("\t\t symbol(i) = '+'; \n");
				os.write("\t else  \n"); // data(i) < data(1)
				os.write("\t\t symbol(i) = '-'; \n");
				os.write("\t end\n");	
				os.write("end\n");
				os.write("end\n");
			}else {
				os.write("for i=2:size(data,2)\n");
				os.write("if p(i) > 0.05 || h(i) == 0 \n");
				os.write("\t symbol(i) = '='; \n");
				os.write("else \n");
				os.write("\t if h_left(i) == 1 \n"); // data(i) > data(1)
				os.write("\t\t symbol(i) = '-'; \n");
				os.write("\t else  \n"); // data(i) < data(1)
				os.write("\t\t symbol(i) = '+'; \n");
				os.write("\t end\n");	
				os.write("end\n");
				os.write("end\n");
			}
			
			os.write("disp('-----test results" + fileName + "------------------------') \n");			
			//os.write("symbol' \n ");
			os.close();
		} // printForTest	  
	   
	  /**
	   * Print .m files
	   * @param problem
	   * @param fileName
	   * @param data
	   * @throws IOException
	   */
	  void printMscriptRunAll(String basePath) throws IOException {		
	  			
			for (int i = 0 ;i < indicators_.length;i++ ){ // for each indicator 
				String indicator = indicators_[i];
				
				String tempIndicator = indicator;	
				if (tempIndicator.equalsIgnoreCase("IGD+")) {
					tempIndicator = "IGDplus";		
				}
				
				String path = basePath + "/" + indicator + "/" + "RunAll" + tempIndicator + ".m";
				resetFile(path);
				
				FileWriter os = new FileWriter(path, true);
								
				os.write("%% Run all .m file to get the test symbols \n ");	
				os.write("function RunAll" + tempIndicator +" ()\n ");		
				os.write("clear;clc \n ");
				os.write("format long;\n ");						
			
				for (int j = 0;j < problems_.length;j++) { // for each problem 
					String problem = problems_[j]+".dimacs"; // problem name 
					
					String tempProblem = problem;
					tempProblem = tempProblem.replace('.', '_');
					tempProblem = tempProblem.replace('-', '_');
					tempProblem = tempProblem.replace(',', '_');
					
					os.write("symbols("+ (j+1) +",:) = "  + "FM_" + tempProblem + "_" + tempIndicator + "();\n");		
					
				}
			
				os.write("savedata (symbols,'" + indicator + ".tr');\n");
				os.write("end \n ");	
				
				os.write("\n ");	
				os.write("\n ");	
				os.write("function savedata(mat,filename) \n");
				os.write("	f=fopen(filename,'w'); \n");
				os.write("	for i=1:size(mat,1) \n");
				os.write("   for j=1:size(mat,2) \n");
				os.write("        fprintf(f,'%c ',mat(i,j)); \n");
				os.write("     end \n");
				os.write("    fprintf(f,'\\r\\n'); \n");
				os.write("end \n");
			    os.write("fclose(f);\n");
				os.write("end \n");
			
				os.close();
			}
		
		} // printMscriptRunAll	  
	  
	  /**
	   * Print .m files
	   * @param problem
	   * @param fileName
	   * @param data
	   * @throws IOException
	   */
	  void printRscriptRunAll(String basePath) throws IOException {		
	  			
			for (int i = 0 ;i < indicators_.length;i++ ){ // for each indicator 
				String indicator = indicators_[i];
				
				String tempIndicator = indicator;	
				if (tempIndicator.equalsIgnoreCase("IGD+")) {
					tempIndicator = "IGDplus";		
				}
				
				String path = basePath + "/" + indicator + "/" + "RunAllEffectSize" + tempIndicator + ".R";
				resetFile(path);
				
				FileWriter os = new FileWriter(path, true);
								
				os.write("# Run all .R file to get effect size \n ");	
				os.write("# setwd(\"~/study/eclipse-workspace/spl_devxy/outputNSbSwithDiversity/" + (basePath + "/" + indicator) + "\")  \n ");	
				//os.write("# setwd(\"" + (basePath + "/" + indicator) + "\")  \n ");	
				os.write("# source(\"RunAllEffectSizeinvertedDist.R\") \n ");	
				for (int j = 0;j < problems_.length;j++) { // for each problem 
					String problem = problems_[j]+".dimacs"; // problem name 
					
					String tempProblem = "FM_" + problem;
					tempProblem = tempProblem.replace('.', '_');
					tempProblem = tempProblem.replace('-', '_');
					tempProblem = tempProblem.replace(',', '_');
					
					os.write("source(\""  + tempProblem + "_" + tempIndicator + ".R\") \n");		
					
				}		
				
				os.close();
			}
		
		} // printMscriptRunAll	  
}
