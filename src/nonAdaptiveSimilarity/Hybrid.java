package nonAdaptiveSimilarity;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import developmentSet.DevelopmentSet;

public class Hybrid {

	/**
	 * This class is mainly for supporting Experiment 2.4 (the 'hybrid' part) in experimental-plan,
	 * Two sets will be taken, and for each token
	 * in set 1, we compute normalized edit similarity (in CommmonSimilarityFunctions).
	 * We take the 'maximum' such similarity over all tokens in set 2, and repeat for all
	 * tokens. We take the average over the set. We do this again by reversing the sets,
	 * then compute the average of the two set scores.	 
	 * */
	
	/*
	 * In this function, we compute the hybrid function over all tokens; we don't
	 * prune out the non-alphabetic tokens.
	 * 
	 * The resultsFile will contain lines of the form id \t score
	 */
	public static void withoutAlphaPreprocessing(
			String duplicatesFile, String resultsFile){
		Scanner in=null;
		PrintWriter out=null;
		try{
			in=new Scanner(new FileReader(duplicatesFile));
			out=new PrintWriter(new File(resultsFile));
			while(in.hasNextLine()){
				String line=in.nextLine();
				String id=line.split("\t")[0];
				String[] fields=line.split("\t\\{\t|\t\\}");
				String freebase=fields[1];
				String dbpedia=fields[3];
				Set<String> dbpediaTokens=DevelopmentSet.prepForNonAlphaJaccard(
						DevelopmentSet.parseJSONIntoStringFeatures(dbpedia.split("\t")).get(3));
			
				Set<String> freebaseTokens=DevelopmentSet.prepForNonAlphaJaccard(
						DevelopmentSet.parseJSONIntoStringFeatures(freebase.split("\t")).get(3));
				out.println(id+"\t"+computeHybrid(dbpediaTokens, freebaseTokens));
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
			out.close();
		}
	}
	
	/*
	 * In this function, we compute jaccard over all tokens after
	 * pruning out the non-alphabetic tokens.
	 * 
	 * The resultsFile will contain lines of the form id \t score
	 */
	public static void withAlphaPreprocessing(
			String duplicatesFile, String resultsFile){
		Scanner in=null;
		PrintWriter out=null;
		try{
			in=new Scanner(new FileReader(duplicatesFile));
			out=new PrintWriter(new File(resultsFile));
			while(in.hasNextLine()){
				String line=in.nextLine();
				String id=line.split("\t")[0];
				String[] fields=line.split("\t\\{\t|\t\\}");
				String freebase=fields[1];
				String dbpedia=fields[3];
				Set<String> dbpediaTokens=DevelopmentSet.prepForAlphaJaccard(
						DevelopmentSet.parseJSONIntoStringFeatures(dbpedia.split("\t")).get(3));
			
				Set<String> freebaseTokens=DevelopmentSet.prepForAlphaJaccard(
						DevelopmentSet.parseJSONIntoStringFeatures(freebase.split("\t")).get(3));
				out.println(id+"\t"+computeHybrid(dbpediaTokens, freebaseTokens));
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
			out.close();
		}
		
	}
	
		/*
		 * I've decided it's best to approach this in a quadratic fashion, and not
		 * over-optimize. For basic optimizations, see code of setAverage
		 */
	private static double computeHybrid(Set<String> set1, Set<String> set2){
			
			return (setAverage(set1, set2)+setAverage(set2, set1))/2;
	}
	
	private static double setAverage(Set<String> primary, Set<String> secondary){
		double result=0.0;
		if(primary.size()==0||secondary.size()==0)
			return result;
		for(String token1: primary){
			
			//a simple optimization
			if(secondary.contains(token1)){
				result+=1.0;
				continue;
			}
			double max=0.0;
			Set<String> cache=new HashSet<String>();
			for(String token2: secondary){
				//a second simple optimization
				if(cache.contains(token2))
					continue;
				double sim=CommonSimilarityFunctions.
						normalizedLevensteinSimilarity(token1, token2);
				if(sim>max){
					max=sim;
					//a third simple optimization
					if(sim==1.0)
						break;
				}
			}
			result+=max;
		}
		return result/primary.size();
	}
	
	/*
	 * The token lists are computed like they are in with/withoutAlphaPreprocessing
	 * except we use all the sets. Pairwise score computation is done by calling
	 * computeHybrid. This function is expected to be used primarily by
	 * the classifiers in adaptiveSimilarity.
	 */
	public static ArrayList<Double> computeHybridFeatureVector(ArrayList<HashSet<String>> dbpediaTokens, ArrayList<HashSet<String>> freebaseTokens){
		ArrayList<Double> result=new ArrayList<Double>(dbpediaTokens.size()*freebaseTokens.size());
		for(int i=0; i<dbpediaTokens.size(); i++)
			for(int j=0; j<freebaseTokens.size(); j++)
				result.add(computeHybrid(dbpediaTokens.get(i),freebaseTokens.get(j)));
				
		return result;
	}
	
}
