package entry;

import adaptiveSimilarity.FeatureType;
import adaptiveSimilarity.GPClassifier;

public class Experiments {
/**
 * This class includes static classes that will be used for generating
 * experimental files. All experiments must be done in trials of 10.
 */
	static String outputFolder="C:\\Users\\Mayank\\SkyDrive\\Documents\\experiments\\"
			+ "iswc-schema-free\\adaptive\\GP\\training-size-1-percent\\iceHockey\\";
	/*
	 * Run GPclassifier and use 1% of data for training. Values
	 * used in this function will have to be changed depending
	 * on which dataset is in use.
	 */
	public static void GPOnePercent(){
		String dupFile=Entry.iceHockeyDuplicates;
		String nonDupFile=Entry.iceHockeyNonDuplicates;
		int numLines=Entry.iceHockeyNumTypePairs;
		int trainingSetSize=(int) (0.01*numLines);
		System.out.println("Training set size is "+trainingSetSize);	
		
		for(int i=1; i<=10; i++){
			GPClassifier g=new GPClassifier(dupFile, nonDupFile,
					numLines, trainingSetSize, FeatureType.ALPHA_JACCARD);
			
			g.printTestResults(dupFile, outputFolder+"alpha-jaccard\\dup-trial-"+i, 
					nonDupFile, outputFolder+"alpha-jaccard\\nonDup-trial-"+i);
		
			}
		
		for(int i=1; i<=10; i++){
		GPClassifier g=new GPClassifier(dupFile, nonDupFile,
				numLines, trainingSetSize, FeatureType.NONALPHA_JACCARD);
		
		g.printTestResults(dupFile, outputFolder+"non-alpha-jaccard\\dup-trial-"+i, 
				nonDupFile, outputFolder+"non-alpha-jaccard\\nonDup-trial-"+i);
	
		}
		
		for(int i=1; i<=10; i++){
			GPClassifier g=new GPClassifier(dupFile, nonDupFile,
					numLines, trainingSetSize, FeatureType.CONCATENATE_JACCARD);
			
			g.printTestResults(dupFile, outputFolder+"concatenate-jaccard\\dup-trial-"+i, 
					nonDupFile, outputFolder+"concatenate-jaccard\\nonDup-trial-"+i);
		
			}
		
		for(int i=1; i<=10; i++){
			GPClassifier g=new GPClassifier(dupFile, nonDupFile,
					numLines, trainingSetSize, FeatureType.ALPHA_HYBRID);
			
			g.printTestResults(dupFile, outputFolder+"alpha-hybrid\\dup-trial-"+i, 
					nonDupFile, outputFolder+"alpha-hybrid\\nonDup-trial-"+i);
		
			}
		
		for(int i=1; i<=10; i++){
			GPClassifier g=new GPClassifier(dupFile, nonDupFile,
					numLines, trainingSetSize, FeatureType.NONALPHA_HYBRID);
			
			g.printTestResults(dupFile, outputFolder+"non-alpha-hybrid\\dup-trial-"+i, 
					nonDupFile, outputFolder+"non-alpha-hybrid\\nonDup-trial-"+i);
		
			}
			/*
			for(int i=1; i<=10; i++){
				GPClassifier g=new GPClassifier(dupFile, nonDupFile,
						numLines, trainingSetSize, FeatureType.CONCATENATE_HYBRID);
				
				g.printTestResults(dupFile, outputFolder+"concatenate-hybrid\\dup-trial-"+i, 
						nonDupFile, outputFolder+"concatenate-hybrid\\nonDup-trial-"+i);
			
				}*/
	}
}
