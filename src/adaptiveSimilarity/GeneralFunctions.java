package adaptiveSimilarity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import developmentSet.DevelopmentSet;

public class GeneralFunctions {

	
/**
 * 
 * This class contains functions that will be found useful by all adaptive
 * classifiers implemented in this package.
 */
	
	/*
	 * This file randomly delivers a set of (desiredNum) indices from [0, totalLines) 
	 * and should be called twice (for both the duplicates, and non-duplicates).
	 * The two sets will then be used for training the classifier
	 * 	
	 * The logic is simple and closely modeled after RandomSubset 
	 */
	public static Set<Integer> randomTrainingSet(int totalLines, int desiredNum){
		Set<Integer> indices=new HashSet<Integer>();
		Random r=new Random(System.currentTimeMillis());
		
		while(indices.size()<desiredNum){
			int k=r.nextInt(totalLines);
			indices.add(k);
		}
		
		return indices;
	}
	
	/*
	 * The outward facing function for constructing the feature vector.
	 */
	public static ArrayList<Double> buildFeatureVector(FeatureType featureType, String instance){
		switch(featureType){
		case ALPHA_JACCARD:
			return buildAlphaJaccardFeatureVector(instance);
			
		case NONALPHA_JACCARD:
			return buildNonAlphaJaccardFeatureVector(instance);
			
		case CONCATENATE_JACCARD:
			return buildAlphaAndNonAlphaJaccardFeatureVector(instance);
			
		case ALPHA_HYBRID:
			return buildAlphaHybridFeatureVector(instance);
			
		case NONALPHA_HYBRID:
			return buildNonAlphaHybridFeatureVector(instance);
			
		case CONCATENATE_HYBRID:
			return buildAlphaAndNonAlphaHybridFeatureVector(instance);
			
		default:
			System.out.println("Error! FeatureType is not in here!");
			return null;
		}
	}
	
	/*
	 * Concatenates the two arraylist into a new arraylist and returns it.
	 * Original lists are unmodified.
	 */
	
	private static ArrayList<Double> concatenate(ArrayList<Double> v1, ArrayList<Double> v2){
		ArrayList<Double> result=new ArrayList<Double>(v1.size()+v2.size());
		for(double num: v1)
			result.add(num);
		for(double num: v2)
			result.add(num);
		return result;
	}
	
	/*
	 * All functions below will take an 'instance' (a line from either a duplicates
	 * or non-duplicates file in the development set) and extract a feature vector.
	 * Names are self-explanatory with respect to this. Note that feature vector
	 * size is not the same for all functions below. 
	 * 
	 * These functions are not publicly accessible. buildFeatureVector must
	 * be called, which in turn invokes the correct function from here.
	 */
	private static ArrayList<Double> buildAlphaJaccardFeatureVector(String instance){
		
		String[] fields=instance.split("\t\\{\t|\t\\}");
		String freebase=fields[1];
		String dbpedia=fields[3];
		ArrayList<HashSet<String>> dbpediaTokens=DevelopmentSet.prepForAlphaJaccard(
				DevelopmentSet.parseJSONIntoStringFeatures(dbpedia.split("\t")));
	
		ArrayList<HashSet<String>> freebaseTokens=DevelopmentSet.prepForAlphaJaccard(
				DevelopmentSet.parseJSONIntoStringFeatures(freebase.split("\t")));
		
		return DevelopmentSet.extractJaccardFeatures(dbpediaTokens, freebaseTokens);
	}
	
	private static ArrayList<Double> buildNonAlphaJaccardFeatureVector(String instance){
		
		String[] fields=instance.split("\t\\{\t|\t\\}");
		String freebase=fields[1];
		String dbpedia=fields[3];
		ArrayList<HashSet<String>> dbpediaTokens=DevelopmentSet.prepForNonAlphaJaccard(
				DevelopmentSet.parseJSONIntoStringFeatures(dbpedia.split("\t")));
	
		ArrayList<HashSet<String>> freebaseTokens=DevelopmentSet.prepForNonAlphaJaccard(
				DevelopmentSet.parseJSONIntoStringFeatures(freebase.split("\t")));
		
		return DevelopmentSet.extractJaccardFeatures(dbpediaTokens, freebaseTokens);
	}
	
	private static ArrayList<Double> buildAlphaAndNonAlphaJaccardFeatureVector(String instance){
		
		return concatenate(buildAlphaJaccardFeatureVector(instance),
				buildNonAlphaJaccardFeatureVector(instance));
	}

	private static ArrayList<Double> buildAlphaHybridFeatureVector(String instance){
		
		String[] fields=instance.split("\t\\{\t|\t\\}");
		String freebase=fields[1];
		String dbpedia=fields[3];
		ArrayList<HashSet<String>> dbpediaTokens=DevelopmentSet.prepForAlphaJaccard(
				DevelopmentSet.parseJSONIntoStringFeatures(dbpedia.split("\t")));
	
		ArrayList<HashSet<String>> freebaseTokens=DevelopmentSet.prepForAlphaJaccard(
				DevelopmentSet.parseJSONIntoStringFeatures(freebase.split("\t")));
		
		return nonAdaptiveSimilarity.Hybrid.computeHybridFeatureVector(dbpediaTokens, freebaseTokens);
	}

	private static ArrayList<Double> buildNonAlphaHybridFeatureVector(String instance){
		
		String[] fields=instance.split("\t\\{\t|\t\\}");
		String freebase=fields[1];
		String dbpedia=fields[3];
		ArrayList<HashSet<String>> dbpediaTokens=DevelopmentSet.prepForNonAlphaJaccard(
				DevelopmentSet.parseJSONIntoStringFeatures(dbpedia.split("\t")));
	
		ArrayList<HashSet<String>> freebaseTokens=DevelopmentSet.prepForNonAlphaJaccard(
				DevelopmentSet.parseJSONIntoStringFeatures(freebase.split("\t")));
		
		return nonAdaptiveSimilarity.Hybrid.computeHybridFeatureVector(dbpediaTokens, freebaseTokens);
	}

	private static ArrayList<Double> buildAlphaAndNonAlphaHybridFeatureVector(String instance){
		
		return concatenate(buildAlphaHybridFeatureVector(instance),
				buildNonAlphaHybridFeatureVector(instance));
	}
}
