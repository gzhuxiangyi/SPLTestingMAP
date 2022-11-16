package spl.techniques.QD;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jmetal.core.SolutionSet;
import jmetal.util.Distance;
import jmetal.util.comparators.CrowdingComparator;
import jmetal.util.comparators.ObjectiveComparator;
import jmetal.util.ranking.NondominatedRanking;
import jmetal.util.ranking.Ranking;
import spl.MAP_test;
import spl.fm.Product;
import spl.utils.PseudoRandom;

public class NSGA2 {
    public static final int FITNESS_TYPE_ACCURATE = 0;
    public static final int FITNESS_TYPE_APPORIMATE = 1;
    public static final int FITNESS_TYPE_DIVERSITY = 2;
    
	private SolutionSet population;
	private int cellsInEachDim;
	private int lowerBound=1;
	private int upperBound=100;
    private long timeAllowedMS;   // Running time
    private long evaluations = 0;
    private int initialCriticalPoint;
    
    public NSGA2(int cellsInEachDim,long timeAllowedMS) {
    	this.cellsInEachDim = cellsInEachDim;
    	this.timeAllowedMS = timeAllowedMS;
    }
    
    public NSGA2(int lowerBound,int upperBound,long timeAllowedMS) {
    	this.lowerBound = lowerBound;
    	this.upperBound = upperBound;
    	this.cellsInEachDim = upperBound - lowerBound + 1;
    	this.timeAllowedMS = timeAllowedMS;
    }
    
    /**
     * Run the population-ELITES
     * @throws Exception 
     */
    public List<IndividualMultiObj> runNSGA2(List<Product> seed) throws Exception {    	
    	 Distance distance = new Distance();
    	 
    	 int fitnessType = FITNESS_TYPE_ACCURATE;
//    	 int fitnessType = FITNESS_TYPE_DIVERSITY;
//    	 int fitnessType = FITNESS_TYPE_APPORIMATE;
    	 
         if (fitnessType == FITNESS_TYPE_DIVERSITY) {
        	 IndividualMultiObj.useDistanceAsFitness = true;
         }         

         IndividualMultiObj.counter = 0;// Reset the <counter>
         int populationSize = cellsInEachDim;
         
         //Initialize population
    	 population = new SolutionSet(populationSize);// Only one dim, so a list is enough. If more than one dim, use Matrix
   
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
//            		 temp.add(population_test.getInstance().getOneRandomProductRandomlizedSAT4J()); // Use SAT4J         
            		 count++;
            	 }
        	 }
    		      	
        	 IndividualMultiObj indiv = new IndividualMultiObj(temp,lowerBound,upperBound);  
             indiv.fitness(fitnessType);
             
             population.add(i,indiv);// Add to population in the correct position
             
             if(fitnessType == FITNESS_TYPE_ACCURATE) {
	             if (!reachFirst && -indiv.getObjective(0) >= 100.0 - 1e-6) { // Coverage >= 100%   
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
    		 population.get(i).setMaxProductsNo(upperBound);
         }     
         
         
         //
//         System.out.println("=======================start========================");
//         for (int i=0; i < population.size();i++) {
//        	 System.out.println("the " + i + "th test suite with (size,converage):(" 
//        			 + population.get(i).getSize() + "," + (-population.get(i).getObjective(0))+")");
//         }
         
         
         evaluations = 0;
         
         List<IndividualMultiObj> offspringPopulation = new ArrayList<IndividualMultiObj>(populationSize);
         Ranking ranking = null;
         
         //Main loop    
//         while (Individual.counter < 6000) {       
           while (evaluations < 12000) {       
//        	 System.out.println("Individual.counter= " + Individual.counter + ";evaluations=" + evaluations);
        	 
        	 offspringPopulation.clear(); 
        	  
        	 // Create the offspring population
        	 for (int i = 0; i < populationSize;i++) {
        		 
            	 int ind =  PseudoRandom.randInt(0, upperBound - lowerBound);// Random selection

            	 IndividualMultiObj selected = population.get(ind);
            	 
            	 IndividualMultiObj mutated = new IndividualMultiObj(selected);
            	 
            	 //Use multi-task model
            	 int operateType = mutated.mutate(); // Mutation (important)        	 
//            	 mutated.mutateWorst(); // Mutation (important) for large feature models
            	 
            	 mutated.fitness(fitnessType);// Evaluation (important)
            	 evaluations++;
            	 
            	 offspringPopulation.add(mutated);
        	 }

        	// Create the solutionSet union of solutionSet and offSpring
             SolutionSet union_ = population.union(offspringPopulation);
           
             //************The following code removes same solutions************
//             SolutionSet unique = new SolutionSet(union_.size());
//             unique.add(union_.get(0));
//             
//             for (int i = 1;i < union_.size();i++) {
//            	 IndividualMultiObj soli = union_.get(i);
//            	 boolean addFlag = true;
//            	 
//            	 for (int j = 0; j < unique.size();j++) {
//            		 if (soli.equals(unique.get(j))) {
//            			 addFlag = false;
//                		 break;
//                	 }
//                	 
//            	 }
//            	 
//            	 if (addFlag) {
//            		 unique.add(soli);
//            	 }    
//             }
             //************The following code removes same solutions************
             
             // Ranking the union
             ranking = new NondominatedRanking(union_);

             int remain = populationSize;
             int index = 0;
             SolutionSet front;
             population.clear();

             // Obtain the next front
             front = ranking.getSubfront(index);
          
             while ((remain > 0) && (remain >= front.size())) {
                 //Assign crowding distance to individuals
                 distance.crowdingDistanceAssignment(front, 2);
                 //Add the individuals of this front
                 for (int k = 0; k < front.size(); k++) {
                     population.add(front.get(k));
                 } // for

                 //Decrement remain
                 remain = remain - front.size();

                 //Obtain the next front
                 index++;
                 if (remain > 0) {
                     front = ranking.getSubfront(index);
                 } // if
             } // while

             // Remain is less than front(index).size, insert only the best one
             if (remain > 0) {  // front contains individuals to insert
                 distance.crowdingDistanceAssignment(front, 2);
                 front.sort(new CrowdingComparator());
                 for (int k = 0; k < remain; k++) {
                     population.add(front.get(k));
                 } // for
             } // if
               
   
        	 // Update the upper bound  
//        	 if(fitnessType == FITNESS_TYPE_ACCURATE) {
//        		 
//	        	 if (upperBound > lowerBound) {// if upperBound = lowerBound, we do not need to change upperBound
//	        		 
//	        		 if ((-population.get(upperBound - lowerBound -1).getObjective(0)) >= 100.0 - 1e-6 
//	        		     && (-population.get(upperBound - lowerBound).getObjective(0)) >= 100.0 - 1e-6 ) {
//	            		 
//	        			 upperBound--;// upperBound points the first one with 100% coverage
//	            		 
//	            	     //Reset maxProductsNo for each 
//	                     for (int i = 0; i < upperBound - lowerBound + 1;i++) {
//	                    	 population.get(i).setMaxProductsNo(upperBound);
//	                     }           
//	                     
//	            	 }
//	        
//	        	 }
//        	 } 
        	 
        	
        	 
         }// while 
          
         System.out.println("Final upperBound = " + upperBound);
         System.out.println("evaluations = " + evaluations +"; counter=" + Individual.counter);
                  
         ranking = new NondominatedRanking(population);
         SolutionSet firstFront = ranking.getSubfront(0);
                 
         firstFront.sort(new ObjectiveComparator(0,true));
         
         for (int i=0; i < firstFront.size();i++) {
        	 System.out.println("the " + i + "th test suite with (size,converage):(" 
        			 + firstFront.get(i).getSize() + "," + ( - firstFront.get(i).getObjective(0))+")");
         }
         
         return firstFront.getSolutionsList();
    }
           
     
    public void execute(int [][] data) throws Exception{
		
		String basedPath =  "G:/javaWorkSpace/SPLTestingpopulation/latex/" +  "/SuccessImprovementFigs/";  
			
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
