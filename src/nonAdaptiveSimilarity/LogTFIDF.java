package nonAdaptiveSimilarity;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import developmentSet.DevelopmentSet;

public class LogTFIDF {

	/*
	 * This class is for implementing LogTFIDF, and enable Experiment 2.3 on
	 * development sets (see experimental-plan)
	 */
	
	static Map<String, Double> LogIDFFreebase;	//see populateLogIDFMap
	static Map<String, Double> LogIDFDBpedia;	//see populateLogIDFMap
	static int numInstances;	//this is the same for dbpedia, freebase, since they come in pairs
	
	/*
	 * Unlike the functions in Jaccard or Hybrid, we have to simultaneously
	 * print out the duplicate and non-duplicates scores for efficiency reasons.
	 * Otherwise, the format is the same. In each file, we have two tab-delimited
	 * fields (id and score).
	 */
	public static void withoutAlphaPreprocessing(String dupFile, String nonDupFile, 
			String resultsDup, String resultsNonDup){
		populateLogIDFMap(dupFile, nonDupFile);
		Scanner in=null;
		PrintWriter out=null;
		try{
			in=new Scanner(new FileReader(dupFile));
			out=new PrintWriter(new File(resultsDup));
			while(in.hasNextLine()){
				String line=in.nextLine();
				String id=line.split("\t")[0];
				String[] fields=line.split("\t\\{\t|\t\\}");
				String freebase=fields[1];
				String dbpedia=fields[3];
				Map<String, Integer> dbpediaBag=
					DevelopmentSet.parseJSONIntoStringFeatures(dbpedia.split("\t")).get(3);
				Map<String, Integer> freebaseBag=
						DevelopmentSet.parseJSONIntoStringFeatures(freebase.split("\t")).get(3);
				Map<String, Double> dbpediaTFIDF=buildNormalizedLogTFIDF(dbpediaBag, true);
				Map<String, Double> freebaseTFIDF=buildNormalizedLogTFIDF(freebaseBag, false);
				
				double score=0.0;
				for(String token: dbpediaTFIDF.keySet())
					if(freebaseTFIDF.containsKey(token))
						score+=(dbpediaTFIDF.get(token)*freebaseTFIDF.get(token));
				if(score>1.0 || score<0.0)
					System.out.println("Error! score is "+score);
				out.println(id+"\t"+score);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
			out.close();
		}
		
		try{
			in=new Scanner(new FileReader(nonDupFile));
			out=new PrintWriter(new File(resultsNonDup));
			while(in.hasNextLine()){
				String line=in.nextLine();
				String id=line.split("\t")[0];
				String[] fields=line.split("\t\\{\t|\t\\}");
				String freebase=fields[1];
				String dbpedia=fields[3];
				Map<String, Integer> dbpediaBag=
					DevelopmentSet.parseJSONIntoStringFeatures(dbpedia.split("\t")).get(3);
				Map<String, Integer> freebaseBag=
						DevelopmentSet.parseJSONIntoStringFeatures(freebase.split("\t")).get(3);
				Map<String, Double> dbpediaTFIDF=buildNormalizedLogTFIDF(dbpediaBag, true);
				Map<String, Double> freebaseTFIDF=buildNormalizedLogTFIDF(freebaseBag, false);
				
				double score=0.0;
				for(String token: dbpediaTFIDF.keySet())
					if(freebaseTFIDF.containsKey(token))
						score+=(dbpediaTFIDF.get(token)*freebaseTFIDF.get(token));
				if(score>1.0 || score<0.0)
					System.out.println("Error! score is "+score);
				out.println(id+"\t"+score);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
			out.close();
		}
	}
	
	/*
	 * Takes the token instance frequency counts of either a dbpedia/freebase instance
	 * and uses the corr. IDF map to return a normalized LogTFIDF 'weight vector'.
	 */
	private static Map<String, Double> buildNormalizedLogTFIDF(Map<String, Integer> bag, boolean dbpedia){
		Map<String, Double> result=new HashMap<String, Double>();
		double total=0.0;
		for(String token: bag.keySet()){
			double idf=0.0;
			if(dbpedia)
				idf=LogIDFDBpedia.get(token);
			else
				idf=LogIDFFreebase.get(token);
			if(idf==0.0)
				System.out.println("Error! Token "+token+" not found in IDF map. dbpedia is set to "+dbpedia);
			
			total+=(Math.log(bag.get(token)+1)*idf);
			result.put(token, Math.log(bag.get(token)+1)*idf);
		}
		
		for(String token: bag.keySet())
			result.put(token, result.get(token)/total);
		
		return result;
	}
	
	/*
	 * For populating LogIDF=log(N/df+1) where df is the number of instances (across
	 * both dupFile and nonDupFile) in which a token occurs (the key in the map) and
	 * N is the total number of instances (again, across both files)
	 */
	static void populateLogIDFMap(String dupFile, String nonDupFile){
		numInstances=0;
		Map<String, Integer> FreebaseDF=new HashMap<String, Integer>();
		Map<String, Integer> DBpediaDF=new HashMap<String, Integer>();
		Scanner in=null;
		try{
			in=new Scanner(new FileReader(dupFile));
			while(in.hasNextLine()){
				String line=in.nextLine();
				numInstances++;
				String[] fields=line.split("\t\\{\t|\t\\}");
				String freebase=fields[1];
				String dbpedia=fields[3];
				Set<String> dbpediaTokens=DevelopmentSet.prepForNonAlphaJaccard(
						DevelopmentSet.parseJSONIntoStringFeatures(dbpedia.split("\t")).get(3));
			
				for(String token: dbpediaTokens){
					if(!DBpediaDF.containsKey(token))
						DBpediaDF.put(token, 0);
					DBpediaDF.put(token, DBpediaDF.get(token)+1);
				}
				
				Set<String> freebaseTokens=DevelopmentSet.prepForNonAlphaJaccard(
						DevelopmentSet.parseJSONIntoStringFeatures(freebase.split("\t")).get(3));
				
				for(String token: freebaseTokens){
					if(!FreebaseDF.containsKey(token))
						FreebaseDF.put(token, 0);
					FreebaseDF.put(token, FreebaseDF.get(token)+1);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
		}
		
		try{
			in=new Scanner(new FileReader(nonDupFile));
			while(in.hasNextLine()){
				String line=in.nextLine();
				numInstances++;
				String[] fields=line.split("\t\\{\t|\t\\}");
				String freebase=fields[1];
				String dbpedia=fields[3];
				Set<String> dbpediaTokens=DevelopmentSet.prepForNonAlphaJaccard(
						DevelopmentSet.parseJSONIntoStringFeatures(dbpedia.split("\t")).get(3));
			
				for(String token: dbpediaTokens){
					if(!DBpediaDF.containsKey(token))
						DBpediaDF.put(token, 0);
					DBpediaDF.put(token, DBpediaDF.get(token)+1);
				}
				
				Set<String> freebaseTokens=DevelopmentSet.prepForNonAlphaJaccard(
						DevelopmentSet.parseJSONIntoStringFeatures(freebase.split("\t")).get(3));
				
				for(String token: freebaseTokens){
					if(!FreebaseDF.containsKey(token))
						FreebaseDF.put(token, 0);
					FreebaseDF.put(token, FreebaseDF.get(token)+1);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
		}
		
		LogIDFFreebase=new HashMap<String, Double>();
		for(String token: FreebaseDF.keySet())
			LogIDFFreebase.put(token, Math.log((numInstances/FreebaseDF.get(token))+1));
		
		LogIDFDBpedia=new HashMap<String, Double>();
		for(String token: DBpediaDF.keySet())
			LogIDFDBpedia.put(token, Math.log((numInstances/DBpediaDF.get(token))+1));
		
		
		System.out.println("Map building complete. numInstances = "+numInstances);
	}
}
