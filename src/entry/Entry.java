package entry;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import analysis.Analysis;
import buildDevelopmentSets.DomainSpecificSubset;
import nonAdaptiveSimilarity.CommonSimilarityFunctions;


public class Entry {

	static String inputPath="C:\\Users\\Mayank\\SkyDrive\\Documents\\"
			+ "experiments\\sameAs\\derived-data-files\\";
	static String outputPath="C:\\Users\\Mayank\\SkyDrive\\Documents\\"
			+ "experiments\\iswc-schema-free\\developmentSets\\";
	static String outerPath="C:\\Users\\Mayank\\SkyDrive\\Documents\\"
			+ "experiments\\iswc-schema-free\\";
	static String freebaseDbpediaAppend=inputPath+"freebaseDbpediaSameAsAppend";
	static int freebaseDbpediaAppendNumlines=2093007;
	
	static String randomDuplicates=outputPath+"freebaseDbpediaDuplicatesRandom10000";
	static String randomNonDuplicates=outputPath+"freebaseDbpediaNonDuplicatesRandom10000";
	static int randomNumLines=10000;
	
	static String iceHockeyDuplicates=outputPath+"freebaseDbpediaDuplicatesIceHockey8044";
	static String iceHockeyNonDuplicates=outputPath+"freebaseDbpediaNonDuplicatesIceHockey8044";
	
	static String iceHockeyTypePair="\"<http://rdf.freebase.com/ns/common.topic>\"\t\"<http://dbpedia.org/ontology/icehockeyplayer>\"";
	static int iceHockeyNumTypePairs=8044;
	
	static String civilParishDuplicates=outputPath+"freebaseDbpediaDuplicatesCivilParish6365";
	static String civilParishNonDuplicates=outputPath+"freebaseDbpediaNonDuplicatesCivilParish6365";
	
	
	static String civilParishTypePair="\"<http://rdf.freebase.com/ns/location.uk_civil_parish>\"\t\"<http://dbpedia.org/ontology/settlement>\"";
	static int civilParishNumTypePairs=6365;
	
	
	public static void main(String[] args){
	
		//Experiments.GPOnePercent();
	}
	
	/*
	 * A 'script' that calls Analysis.printRecallPrecisionFMeasure 
	 * on the 12 files output by Analysis.basicAnalysis in nonAdaptive\\analyses
	 */
	static void buildRecallPrecisionFilesHybridJaccard(){
		String prefix=outerPath+"nonAdaptive\\analyses\\f-db-";
		String[] datasets={"rand-","iceHockey-","civilParish-"};
		String[] alphas={"Alpha-", "woAlpha-"};
		String[] methods={"jaccard", "hybrid"};
		for(String dataset: datasets)
			for(String alpha: alphas)
				for(String method: methods)
					Analysis.printRecallPrecisionFMeasure(prefix+dataset+"basicAnalysis-"+alpha+method, 
							prefix+dataset+"recPrecFM-"+alpha+method+".csv");
	}
	
	/*
	 * A 'script' that calls Analysis.basicAnalysis on the 24 files in nonAdaptive
	 * 
	 * Note that, since running this 'script', we've done some moving around
	 * and hybrid and jaccard files are now in their separate folders (in nonAdaptive)
	 * Thus, if this code needs to be re-run, the prefixInput will have to
	 * be appropriately modified. Otherwise, everything is fine.
	 */
	static void buildAnalysisFiles(){
		String prefixInput=outerPath+"nonAdaptive\\f-db-";
		String prefixOutput=outerPath+"nonAdaptive\\analyses\\f-db-";
		String[] datasets={"rand-","iceHockey-","civilParish-"};
		String dup="dups-";
		String nondup="nonDups-";
		String[] alphas={"Alpha-", "woAlpha-"};
		String[] methods={"jaccard", "hybrid"};
		for(String dataset: datasets)
			for(String alpha: alphas)
				for(String method: methods)
					Analysis.basicAnalysis(prefixInput+dataset+dup+alpha+method, 
							prefixInput+dataset+nondup+alpha+method, 
							prefixOutput+dataset+"basicAnalysis-"+alpha+method);
	}
	
	/*
	 * A 'script' that calls Analysis.printRecallPrecisionFMeasure
	 * on the 6 tfidf files output by Analysis.basicAnalysis in nonAdaptive\\analyses
	 */
	static void buildRecallPrecisionFilesTFIDF(){
		String prefix=outerPath+"nonAdaptive\\analyses\\f-db-";
		String[] datasets={"rand-","iceHockey-","civilParish-"};
		String[] alphas={"woAlpha-"};
		String[] methods={"logtfidf"};
		for(String dataset: datasets)
			for(String alpha: alphas)
				for(String method: methods)
					Analysis.printRecallPrecisionFMeasure(prefix+dataset+"basicAnalysis-"+alpha+method, 
							prefix+dataset+"recPrecFM-"+alpha+method+".csv");
	}

	/*
	 * A 'script' that calls Analysis.basicAnalysis on the 6 files in nonAdaptive/logtfidf
	 * 
	 */
	static void buildTFIDFAnalysisFiles(){
		String prefixInput=outerPath+"nonAdaptive\\logtfidf\\f-db-";
		String prefixOutput=outerPath+"nonAdaptive\\analyses\\f-db-";
		String[] datasets={"rand-","iceHockey-","civilParish-"};
		String dup="dups-";
		String nondup="nonDups-";
		String[] alphas={"woAlpha-"};
		String[] methods={"logtfidf"};
		for(String dataset: datasets)
			for(String alpha: alphas)
				for(String method: methods)
					Analysis.basicAnalysisTFIDF(prefixInput+dataset+dup+alpha+method, 
							prefixInput+dataset+nondup+alpha+method, 
							prefixOutput+dataset+"basicAnalysis-"+alpha+method);
	}

	/*
	 * This function is going to be very fluid. 
	 */
	static void testThings(){
		
	}

	public static List<String> fetchFirstNLines(String file, int n){
		List<String> lines=new ArrayList<String>();
		Scanner in=null;
		int count=0;
		try{
			in=new Scanner(new FileReader(file));
			while(in.hasNextLine()&& count<n){
				lines.add(in.nextLine());
				count++;
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}finally{in.close();}
		return lines;
	}
	
	public static void printFirstNLines(String file, int n){
		
		Scanner in=null;
		int count=0;
		try{
			in=new Scanner(new FileReader(file));
			while(in.hasNextLine()&& count<n){
				System.out.println(in.nextLine());
				count++;
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}finally{in.close();}
		
	}
	
	/**
	 * Prints the number of lines in file
	 * 
	 * 
	 */
	public static void printNumLines(String file){
		int count=0;
		Scanner in=null;
		try{
			in=new Scanner(new FileReader(file));
		
		
			while(in.hasNextLine()){
				in.nextLine();
				count++;
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
		}
		in.close();
		System.out.println("Total number of lines: "+count);
	}

}
