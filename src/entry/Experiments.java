package entry;

import adaptiveSimilarity.FeatureType;
import adaptiveSimilarity.GPClassifier;
import analysis.Analysis;
import weka.classifiers.Classifier;

public class Experiments {
/**
 * This class includes static classes that will be used for generating
 * experimental files. All experiments must be done in trials of 10.
 */
	static String outputFolder="C:\\Users\\Mayank\\SkyDrive\\Documents\\experiments\\"
			+ "iswc-schema-free\\adaptive\\GP\\training-size-1-percent\\random\\";
	
	static String outputFolderTransfer="C:\\Users\\Mayank\\SkyDrive\\Documents\\experiments\\"
			+ "iswc-schema-free\\adaptive\\GP\\transfer-learning-GP\\"
			+ "training-civilParish\\";
	/*
	 * Run GPclassifier and use 1% of data for training. Values
	 * used in this function will have to be changed depending
	 * on which dataset is in use. This function is for transfer-learning
	 * experiments; training and testing are in different domains.
	 * 
	 * Be very careful about changing values for experiments in here.
	 * 
	 * Based on results from GP, we will train a classifier using only 1% of
	 * the labels from one of the three development sets, and test it on the
	 * other two (using GP, and the featureType NONALPHA_JACCARD; see the
	 * experimental-plan document)
	 */
	public static void GPOnePercentTransfer(){
		String dupFileTraining=Entry.civilParishDuplicates;
		String nonDupFileTraining=Entry.civilParishNonDuplicates;
		int numLines=Entry.civilParishNumTypePairs;
		int trainingSetSize=(int) (0.01*numLines);
		System.out.println("Training set size is "+trainingSetSize);	
		
		
		String dupFileTesting=Entry.iceHockeyDuplicates;
		String nonDupFileTesting=Entry.iceHockeyNonDuplicates;
		
		for(int i=1; i<=10; i++){
		GPClassifier g=new GPClassifier(dupFileTraining, nonDupFileTraining,
				numLines, trainingSetSize, FeatureType.NONALPHA_JACCARD);
		Classifier classifier=g.getClassifier();
		
		GPClassifier.printTestResults(dupFileTesting, 
				outputFolderTransfer+"testing-iceHockey\\dup-trial-"+i, 
				nonDupFileTesting, 
				outputFolderTransfer+"testing-iceHockey\\nonDup-trial-"+i,
				classifier, FeatureType.NONALPHA_JACCARD);
	
		}
		
		
			
			
	}

	/*
	 * Run GPclassifier and use 1% of data for training. Values
	 * used in this function will have to be changed depending
	 * on which dataset is in use. This function is for non-transfer-learning
	 * experiments; training and testing are limited to same-domain.
	 */
	public static void GPOnePercentNonTransfer(){
		String dupFile=Entry.iceHockeyDuplicates;
		String nonDupFile=Entry.iceHockeyNonDuplicates;
		int numLines=Entry.iceHockeyNumTypePairs;
		int trainingSetSize=(int) (0.01*numLines);
		System.out.println("Training set size is "+trainingSetSize);	
		/*
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
		
			}*/
			
			for(int i=1; i<=10; i++){
				GPClassifier g=new GPClassifier(dupFile, nonDupFile,
						numLines, trainingSetSize, FeatureType.CONCATENATE_HYBRID);
				
				g.printTestResults(dupFile, outputFolder+"concatenate-hybrid\\dup-trial-"+i, 
						nonDupFile, outputFolder+"concatenate-hybrid\\nonDup-trial-"+i);
			
				}
			
			
	}
	
	/*
	 * Call this once for each of the development sets. Make sure to change the
	 * outputfolder! 
	 */
	public static void analysis(){
		String[] featureTypes={"alpha-jaccard", "non-alpha-jaccard", "concatenate-jaccard",
				"alpha-hybrid", "non-alpha-hybrid", "concatenate-hybrid"};
		for(String featureType: featureTypes){
			String folder=outputFolder+featureType+"\\";
			String outfile=outputFolder+featureType+".csv";
			double[] minMax=Analysis.returnMinMax(folder);
			Analysis.adaptivePrintRecPrecFM(folder, outfile, minMax[0], 
					minMax[1], 0.02);
		}
	}

	/*
	 * Call this once for each of the 'training'
	 *  sets. Make sure to change the
	 * outputFolderTransfer and testTypes! 
	 */
	public static void analysisTransferLearning(){
		String trainType="training-civilParish-";
		String[] testTypes={"testing-iceHockey", "testing-random"};
		for(String testType: testTypes){
			String folder=outputFolderTransfer+testType+"\\";
			String outfile=outputFolderTransfer+trainType+testType+".csv";
			double[] minMax=Analysis.returnMinMax(folder);
			Analysis.adaptivePrintRecPrecFM(folder, outfile, minMax[0], 
					minMax[1], 0.02);
		}
	}
}
