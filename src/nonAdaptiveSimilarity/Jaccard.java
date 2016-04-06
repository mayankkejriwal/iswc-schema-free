package nonAdaptiveSimilarity;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Set;
import developmentSet.DevelopmentSet;


public class Jaccard {

	/**
	 * This class is mainly for supporting Experiment 2.4 in experimental-plan,
	 * although we might extend it for further experiments
	 */
	
	/*
	 * In this function, we compute jaccard over all tokens; we don't
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
				out.println(id+"\t"+similarities.Jaccard.computeJaccard(dbpediaTokens, freebaseTokens));
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
				out.println(id+"\t"+similarities.Jaccard.computeJaccard(dbpediaTokens, freebaseTokens));
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
			out.close();
		}
	}
}
