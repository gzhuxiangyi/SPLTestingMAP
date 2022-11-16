package spl.techniques.QD;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import spl.MAP_test;
import spl.fm.Product;
import spl.techniques.QD.Individual;
import spl.utils.PseudoRandom;

public class MAP_elites {
    public static final int FITNESS_TYPE_ACCURATE = 0;
    public static final int FITNESS_TYPE_APPORIMATE = 1;
    public static final int FITNESS_TYPE_DIVERSITY = 2;
    
	private List<Individual> MAP;
	private int cellsInEachDim;
	private int lowerBound=1;
	private int upperBound=100;
    private long maxEvaluations;   // Running time
    private long evaluations = 0;
    private int initialCriticalPoint;
    
    public MAP_elites(int cellsInEachDim,long maxEvaluations) {
    	this.cellsInEachDim = cellsInEachDim;
    	this.maxEvaluations = maxEvaluations;
    }
    
    public MAP_elites(int lowerBound,int upperBound,long maxEvaluations) {
    	this.lowerBound = lowerBound;
    	this.upperBound = upperBound;
    	this.cellsInEachDim = upperBound - lowerBound + 1;
    	this.maxEvaluations = maxEvaluations;
    }
    
    /**
     * Run the MAP-ELITES
     * @throws Exception 
     */
    public List<Individual> runME( List<Product> seed) throws Exception {    	
         
    	 int fitnessType = FITNESS_TYPE_ACCURATE; // pairwise coverage
//    	 int fitnessType = FITNESS_TYPE_DIVERSITY;
//    	 int fitnessType = FITNESS_TYPE_APPORIMATE;
    	 
         if (fitnessType == FITNESS_TYPE_DIVERSITY) {
        	 Individual.useDistanceAsFitness = true;
         }     
         
         long startTimeMS = System.currentTimeMillis();
         Individual.counter = 0;// Reset the <counter>
         
         //Initialize MAP
    	 MAP = new ArrayList<Individual>(cellsInEachDim);// Only one dim, so a list is enough. If more than one dim, use Matrix
   
    	 boolean reachFirst = false;
    	 
    	 initialCriticalPoint = upperBound;
    	     	 
         for (int i = 0;  i < cellsInEachDim; i ++) {
  
        	 List<Product> temp =  new ArrayList<Product>(lowerBound+i); // a temp list 
        	 
        	 if (seed!=null && seed.size() == lowerBound+i) {
        		 temp = seed;
        		 System.out.println("**************Seeding**************");
        	 } else {
        		 
             	 int count = 0;            	 
            	 while (count < lowerBound+i) {  	      		  
            		 temp.add(MAP_test.getInstance().getOneRandomProductSAT4J()); // Use SAT4J         
//            		 temp.add(MAP_test.getInstance().getOneRandomProductRandomlizedSAT4J()); // Use SAT4J         
            		 count++;
            	 }
        	 }
    		      	
             Individual indiv = new Individual(temp,lowerBound,upperBound);  
             indiv.fitness(fitnessType);
             
             MAP.add(i, indiv);// Add to MAP in the correct position
             
             if(fitnessType == FITNESS_TYPE_ACCURATE) {
	             if (!reachFirst && indiv.getFitness() >= 100.0 - 1e-6) { // Coverage >= 100%   
	            	 upperBound = lowerBound+i; // A test suite with <upperBound> test cases is enough to obtain 100% coverage 
	            	 reachFirst = true;
	            	 initialCriticalPoint =  lowerBound+i;
	            	 break; //
	             }
             } // IF

         } // for
                                   
         System.out.println("Initial upperBound = " + upperBound);
         
         //Reset maxProductsNo for each cell
         for (int i = 0; i < upperBound - lowerBound + 1;i++) {        
    		 MAP.get(i).setMaxProductsNo(upperBound);
         }               
         
         evaluations = 0;
         
         //Main loop
//         while (System.currentTimeMillis() - startTimeMS < timeAllowedMS/1) {         
//         while (Individual.counter < 6000) {       
           while (evaluations < maxEvaluations) {       
//        	 System.out.println("Individual.counter= " + Individual.counter + ";evaluations=" + evaluations);
        	 
        	 int ind =  PseudoRandom.randInt(0, upperBound - lowerBound);// Random selection

        	 Individual selected = MAP.get(ind);
        	 
        	 Individual mutated = new Individual(selected);
        	 
        	 //Use multi-task model
        	 int operateType = mutated.mutate(); // Mutation (important) for small feature models       	 
//        	 mutated.mutateWorst(); // Mutation (important) for large feature models
        	         	 
        	 // Use single task model
//        	 mutated.mutateSingleTask();// Mutation (for single tasks, only substitution)          	 
//        	 mutated.mutateWorstSingleTask();// Method used in TSE paper
        	         	         	 
        	 mutated.fitness(fitnessType);// Evaluation (important)
        	 
        	 int locationMutated = mutated.getSize() - lowerBound;  // the number of test cases is the feature            	 

        	 if (mutated.getFitness() > MAP.get(locationMutated).getFitness()) {// Mutation is better than the one occupying the cell
        	
        		 MAP.set(locationMutated, mutated);        	
//        		 System.out.println("ind = " + ind + ";locationMutated = " + (locationMutated ));
        	 } // if
        		 
        	 // Update the upper bound  
        	 if(fitnessType == FITNESS_TYPE_ACCURATE) {
        		 
	        	 if (upperBound > lowerBound) {// if upperBound = lowerBound, we do not need to change upperBound
	        		 
	        		 if (MAP.get(upperBound - lowerBound -1).getFitness() >= 100.0 - 1e-6 
	        		     && MAP.get(upperBound - lowerBound).getFitness() >= 100.0 - 1e-6 ) {
	            		 
	        			 upperBound--;// upperBound points the first one with 100% coverage
	            		 
	            	     //Reset maxProductsNo for each 
	                     for (int i = 0; i < upperBound - lowerBound + 1;i++) {
	                    	 MAP.get(i).setMaxProductsNo(upperBound);
	                     }           
	                     
	            	 }
	        
	        	 }
        	 } 
        	 
        	 evaluations++;
        	 
         }// while 
    
         System.out.println("Final upperBound = " + upperBound);
         System.out.println("evaluations = " + evaluations +"; counter=" + Individual.counter);
         for (int i=0; i < MAP.size();i++) {
        	 System.out.println("the " + i + "th test suite with (size,converage):(" + MAP.get(i).getSize() + "," +MAP.get(i).getFitness()+")");
         }
                         
         return MAP;
    }
    
    /**
     * Run the MAP-ELITES
     * @throws Exception 
     */
    public List<Individual> runMECounting( List<Product> seed) throws Exception {    	
         
    	 int fitnessType = FITNESS_TYPE_ACCURATE;
//    	 int fitnessType = FITNESS_TYPE_DIVERSITY;
//    	 int fitnessType = FITNESS_TYPE_APPORIMATE;
    	 
         if (fitnessType == FITNESS_TYPE_DIVERSITY) {
        	 Individual.useDistanceAsFitness = true;
         }         
         
         int [][] improveCount = new int[cellsInEachDim][3];
         
         long startTimeMS = System.currentTimeMillis();
         Individual.counter = 0;// Reset the <counter>
         
         //Initialize MAP
    	 MAP = new ArrayList<Individual>(cellsInEachDim);// Only one dim, so a list is enough. If more than one dim, use Matrix
   
    	 boolean reachFirst = false;
    	 
    	 initialCriticalPoint = upperBound;
    	     	 
         for (int i = 0;  i < cellsInEachDim; i ++) {
  
        	 List<Product> temp =  new ArrayList<Product>(lowerBound+i); // a temp list 
        	 
        	 if (seed!=null && seed.size() == lowerBound+i) {
        		 temp = seed;
        		 System.out.println("**************Seeding**************");
        	 } else {
        		 
             	 int count = 0;            	 
            	 while (count < lowerBound+i) {  	      		  
            		 temp.add(MAP_test.getInstance().getOneRandomProductSAT4J()); // Use SAT4J         
//            		 temp.add(MAP_test.getInstance().getOneRandomProductRandomlizedSAT4J()); // Use SAT4J         
            		 count++;
            	 }
        	 }
    		      	
             Individual indiv = new Individual(temp,lowerBound,upperBound);  
             indiv.fitness(fitnessType);
             
             MAP.add(i, indiv);// Add to MAP in the correct position
             
             if(fitnessType == FITNESS_TYPE_ACCURATE) {
	             if (!reachFirst && indiv.getFitness() >= 100.0 - 1e-6) { // Coverage >= 100%   
	            	 upperBound = lowerBound+i; // A test suite with <upperBound> test cases is enough to obtain 100% coverage 
	            	 reachFirst = true;
	            	 initialCriticalPoint =  lowerBound+i;
	            	 break; //
	             }
             } // IF

         } // for
                                   
         System.out.println("Initial upperBound = " + upperBound);
         
         //Reset maxProductsNo for each cell
         for (int i = 0; i < upperBound - lowerBound + 1;i++) {        
    		 MAP.get(i).setMaxProductsNo(upperBound);
         }               
         
         evaluations = 0;
         
         //Main loop
//         while (System.currentTimeMillis() - startTimeMS < timeAllowedMS/1) {         
//         while (Individual.counter < 6000) {       
           while (evaluations < 12000) {       
//        	 System.out.println("Individual.counter= " + Individual.counter + ";evaluations=" + evaluations);
        	 
        	 int ind =  PseudoRandom.randInt(0, upperBound - lowerBound);// Random selection

        	 Individual selected = MAP.get(ind);
        	 
        	 Individual mutated = new Individual(selected);
        	 
        	 //Use multi-task model
        	 int operateType = mutated.mutate(); // Mutation (important)        	 
//        	 mutated.mutateWorst(); // Mutation (important)       	 
        	 
        	 // Use single task model
//        	 mutated.mutateSingleTask();// Mutation (for single tasks, only substitution)          	 
//        	 mutated.mutateWorstSingleTask();// Method used in TSE paper
        	         	         	 
        	 mutated.fitness(fitnessType);// Evaluation (important)
        	 
        	 int locationMutated = mutated.getSize() - lowerBound;  // the number of test cases is the feature            	 

        	 if (mutated.getFitness() > MAP.get(locationMutated).getFitness()) {// Mutation is better than the one occupying the cell
        	
        		 MAP.set(locationMutated, mutated);        	
//        		 System.out.println("ind = " + ind + ";locationMutated = " + (locationMutated ));
        		 
        		 if (operateType == 1) {//1(sub)
        			 improveCount[locationMutated][0] ++;
        		 } else if (operateType == 2) {//2(add)
        			 improveCount[locationMutated][1] ++;
        		 } else { // operateType == 3(del)    	
        			 improveCount[locationMutated][2] ++;
        		 }
        		 
        	 } // if
        		 
        	 // Update the upper bound  
        	 if(fitnessType == FITNESS_TYPE_ACCURATE) {
        		 
	        	 if (upperBound > lowerBound) {// if upperBound = lowerBound, we do not need to change upperBound
	        		 
	        		 if (MAP.get(upperBound - lowerBound -1).getFitness() >= 100.0 - 1e-6 
	        		     && MAP.get(upperBound - lowerBound).getFitness() >= 100.0 - 1e-6 ) {
	            		 
	        			 upperBound--;// upperBound points the first one with 100% coverage
	            		 
	            	     //Reset maxProductsNo for each 
	                     for (int i = 0; i < upperBound - lowerBound + 1;i++) {
	                    	 MAP.get(i).setMaxProductsNo(upperBound);
	                     }           
	                     
	            	 }
	        
	        	 }
        	 } 
        	 
        	 evaluations++;
        	 
         }// while 
           
         System.out.println("Final upperBound = " + upperBound);
         System.out.println("evaluations = " + evaluations +"; counter=" + Individual.counter);
         for (int i=0; i < MAP.size();i++) {
        	 System.out.println("the " + i + "th test suite with (size,converage):(" + MAP.get(i).getSize() + "," +MAP.get(i).getFitness()+")");
         }
         
         for(int i = 0; i < cellsInEachDim;i++) {
        	 System.out.println("i=" + i +";#sub=" + improveCount[i][0]+";#add=" + improveCount[i][1]+";#del=" + improveCount[i][2]);
         }         
             
         execute(improveCount);
         
         return MAP;
    }
    
    
    public void execute(int [][] data) throws Exception{
		
		String basedPath =  "G:/javaWorkSpace/SPLTestingMAP/latex/" +  "/SuccessImprovementFigs/";  
			
	    File storeDirectory = new File(basedPath);
	    
	    // Check path 
    	if (storeDirectory.exists()) {
			System.out.println("Experiment directory exists");
			if (storeDirectory.isDirectory()) {
				System.out.println("Experiment directory is a directory");
			} else {
				System.out.println("Experiment directory is not a directory. Deleting file and creating directory");
			}
			storeDirectory.delete();
			new File(basedPath ).mkdirs();
		} // if
		else {
			System.out.println("Experiment directory does NOT exist. Creating");
			new File(basedPath).mkdirs();
		} // else

    	String problem = MAP_test.getInstance().getDimacsFile();
		problem = problem.substring(problem.lastIndexOf("/")+1);
		
		String tempProblem = problem;
		tempProblem = tempProblem.replace('.', '_');
		tempProblem = tempProblem.replace('-', '_');
		tempProblem = tempProblem.replace(',', '_');		
		
		String storePath_m = basedPath +  "FM_" + tempProblem + ".m"; // Store .m				
				
		resetFile(storePath_m);
		FileWriter os = new FileWriter(storePath_m, true);
		String m;	
		
		// The header
		os.write("%% " + storePath_m);
		os.write("\n");	
		os.write("clear;clc \n ");	
		
		os.write("h=figure;\n");
		os.write("set(h,'position',[300,300,390,400]) \n");
		
		os.write("data = [");
		os.write("\n");	 
		
		for(int i=0; i < data.length;i++) { //	
			
			for(int j=0;j < data[0].length; j++){//	
				m = Integer.toString(data[i][j]);
				os.write(m+" ");
			}
			
			os.write("\n");
			
		}
		
		
		os.write("];");	
		os.write("\n");	
		
		os.write("sumdata = data;\n");
		os.write("sumdata(:,2) = sumdata(:,2) + sumdata(:,1);\n");
		os.write("sumdata(:,3) = sumdata(:,3) + sumdata(:,2);\n");
		 
		os.write("bar(sumdata(:,3),'FaceColor',[0.6 0.0 0.2],'edgeColor','w') \n");
		os.write("hold on\n");
		os.write("bar(sumdata(:,2),'FaceColor',[0.078 0.169 0.549],'edgeColor','w')\n");
		os.write("hold on\n");
		os.write("bar(sumdata(:,1),'FaceColor',[0.0 0.498 0.0],'edgeColor','w')\n");
		
		os.write("for i=1:6\n");
		os.write("\tif data(i,3) > 0\n");   
		os.write("\t\t text(i-0.1,(sumdata(i,3) + sumdata(i,2))/2,num2str(data(i,3),'%.0f'),'color','w','fontname','Times New Roman','fontsize',16)\n");
		os.write("\tend\n");
		os.write("\tif data(i,2) > 0\n");  
		os.write("\t\t text(i-0.1,(sumdata(i,2) + sumdata(i,1))/2,num2str(data(i,2),'%.0f'),'color','w','fontname','Times New Roman','fontsize',16)\n");
		os.write("\tend\n");
		os.write("\tif data(i,1) > 0\n");  
		os.write("\t\t text(i-0.1,(sumdata(i,1))/2,num2str(data(i,1),'%.0f'),'color','w','fontname','Times New Roman','fontsize',16)\n");
		os.write("\tend\n");
		os.write("end\n");
		os.write("legend({'Del','Add','Sub'},'Orientation','horizontal','Location','northwest','box','off') \n");
		 
		os.write("set(gca,'fontname','times new roman') \n");
		os.write("set(gca,'xticklabel',{'TS_1','TS_2','TS_3','TS_4','TS_5','TS_6'}) \n");
		
		os.write("ylabel('Successful update(#)')\n");
		os.write("xlim([0.4,6.6]);\n");
		os.write("ylim([0,1.2*max(max(sumdata))]);\n");
		os.write("grid on \n");		
		os.write("set(gca,'FontSize',18)\n");		
		os.close();
		
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
    
	public long getEvaluations() {
		return evaluations;
	}

	public void setEvaluations(long evaluations) {
		this.evaluations = evaluations;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}

	public int getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}

	public int getInitialCriticalPoint() {
		return initialCriticalPoint;
	}

	public void setInitialCriticalPoint(int initialCriticalPoint) {
		this.initialCriticalPoint = initialCriticalPoint;
	}    
    
}// Class 
