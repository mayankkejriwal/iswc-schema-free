package adaptiveSimilarity;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.functions.GaussianProcesses;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class GPClassifier {
/**
 * The goal of this class is to use Weka's Gaussian Processes classifier with 
 * a range of feature sets to implement Step 4 in the experimental plan
 * document
 */
	
	Set<Integer> positiveTrainingSetIndices;
	Set<Integer> negativeTrainingSetIndices;
	FeatureType featureType;
	Classifier classifier;
	
	/*
	 * This is a fairly do-it-all constructor. It takes the two instance-pairs
	 * files (duplicatesFile and nonDuplicatesFile), along with the number
	 * of lines in each (in the development set, we did balanced sampling
	 * on duplicates and non-duplicates so this should be the same for both
	 * files), the training set size required (we will do balanced training,
	 * so we will end up having trainingSize duplicates and trainingSize
	 * nonDuplicates- be careful!) and the specific feature set as indicated
	 * by FeatureType.
	 */
	public GPClassifier(String duplicatesFile, String nonDuplicatesFile, 
			int numLinesinFile, int trainingSize, FeatureType f){
		positiveTrainingSetIndices=GeneralFunctions.randomTrainingSet(numLinesinFile, trainingSize);
		negativeTrainingSetIndices=GeneralFunctions.randomTrainingSet(numLinesinFile, trainingSize);
		ArrayList<ArrayList<Double>> instances=new ArrayList<ArrayList<Double>>();
		Instances train;
		featureType=f;
		Scanner in=null;
		
		/*
		 * Construct and add duplicates feature vectors
		 */
		try{
			int count=0;
			in=new Scanner(new FileReader(duplicatesFile));
			while(in.hasNextLine()){
				String line=in.nextLine();
				if(positiveTrainingSetIndices.contains(count)){
					ArrayList<Double> featureVector=GeneralFunctions.buildFeatureVector(f, line);
					featureVector.add(1.0);
					instances.add(featureVector);
				}
				count++;
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
		}
		/*
		 * Construct and add non-duplicates feature vectors
		 */
		try{
			int count=0;
			in=new Scanner(new FileReader(nonDuplicatesFile));
			while(in.hasNextLine()){
				String line=in.nextLine();
				if(negativeTrainingSetIndices.contains(count)){
					ArrayList<Double> featureVector=GeneralFunctions.buildFeatureVector(f, line);
					featureVector.add(0.0);
					instances.add(featureVector);
				}
				count++;
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
		}
		
		
		/*
		 * Build the Instances data structure
		 */
		FastVector attributes=createAttributes(instances.get(0).size());
		train=new Instances("instances", attributes, instances.size());
		for(ArrayList<Double> instance: instances)
			train.add(new Instance(1.0,developmentSet.Weka.convertToArray(instance)));
		 train.setClassIndex(train.numAttributes() - 1);
		 
		 //Train GP classifier
		 classifier=new GaussianProcesses();
		 try {
			classifier.buildClassifier(train);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 System.out.println("Size of training instances is : "+train.numInstances());
	}
	
	/*
	 * Be careful here; the 'score' will not necessarily be in the [0,1] range.
	 * The GP will directly try to predict the numeric class 'value', not the probability
	 * of a categorical class.
	 */
	public void printTestResults(String duplicatesFile, String duplicatesResultsFile, 
			String nonDuplicatesFile, String nonDuplicatesResultsFile){
		Scanner in=null;
		PrintWriter out=null;
		
		try{
			in=new Scanner(new FileReader(duplicatesFile));
			out=new PrintWriter(new File(duplicatesResultsFile));
			int count=0;
			while(in.hasNextLine()){
				String line=in.nextLine();
				if(positiveTrainingSetIndices.contains(count)){
					count++;	
					continue;
				}
				ArrayList<Double> featureVector=GeneralFunctions.buildFeatureVector(featureType, line);
				featureVector.add(-1.0);
				FastVector attributes=createAttributes(featureVector.size());
				Instances test=new Instances("instances", attributes, 1);
				test.add(new Instance(1.0,developmentSet.Weka.convertToArray(featureVector)));
				test.setClassIndex(test.numAttributes() - 1);
				double[] distr=classifier.distributionForInstance(test.instance(0));
				if(distr.length!=1)
					System.out.println("distr. length "+distr.length);
				out.println(line.split("\t")[0]+"\t"+distr[0]);
				count++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{in.close(); out.close();}
		
		System.out.println("Finished classification on duplicates file");
		try{
			in=new Scanner(new FileReader(nonDuplicatesFile));
			out=new PrintWriter(new File(nonDuplicatesResultsFile));
			int count=0;
			while(in.hasNextLine()){
				String line=in.nextLine();
				if(negativeTrainingSetIndices.contains(count)){
					count++;	
					continue;
				}
						
				ArrayList<Double> featureVector=GeneralFunctions.buildFeatureVector(featureType, line);
				featureVector.add(-1.0);
				FastVector attributes=createAttributes(featureVector.size());
				Instances test=new Instances("instances", attributes, 1);
				test.add(new Instance(1.0,developmentSet.Weka.convertToArray(featureVector)));
				test.setClassIndex(test.numAttributes() - 1);
				double[] distr=classifier.distributionForInstance(test.instance(0));
				if(distr.length!=1)
					System.out.println("distr. length "+distr.length);
				out.println(line.split("\t")[0]+"\t"+distr[0]);
				count++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{in.close(); out.close();}
		
		System.out.println("Finished classification on nonDuplicates file");
	}
	
	
	/*remember, the last attribute is the classAttribute, which we declare to be numeric
		This function was derived from developmentSet.Weka in the freebase project.
		*
		*/
		private static FastVector createAttributes(int numAttributes){
			FastVector att=new FastVector();
			for(int i=1; i<numAttributes; i++)
				att.addElement(new Attribute("feat"+i));
			att.addElement(new Attribute("classAttribute"));
			return att;
		}
	
}
