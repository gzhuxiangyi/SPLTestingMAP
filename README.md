# SPLTestingMAP
#### 1. This project is developed based on Java language. Please make sure that Java virtual machine is installed in your computer.
#### 2. Please add all Jar files in ./dist/lib into Java build path.
#### 3. The entry of this project is src/spl/MAP_test/main. You need to specify the following:

	String [] fms = { // Feature models (FM) to be sampled
			"ZipMe",//the first FM
			"HiPAcc",// the second one, etc.
  	};    
	
	 
 	String outputDir = "./output/";  // output dir
  	int runs =30; // How many runs
  	String algName = "MPSeedingAccuracy";// Name your algorithm, here "MPSeedingAccuracy"	


#### 4. To call MAP-Elites, use the following code:
 
  	MAP_test.getInstance().findProductsMAP(algName,fmFile, outputDir, runs,min_nbProds, max_nbProds, evaluations,seedFlag,largeFlag); //
 
#### 5. The Class MAP_elites.java is the core code for MAP-Elites



#### Please feel free to contact Dr. Yi Xiang at gzhuxiang_yi@163.com if you have any questions. 