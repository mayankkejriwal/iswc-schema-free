package entry;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import buildDevelopmentSets.DomainSpecificSubset;

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
	
	
	static String iceHockeyDuplicates=outputPath+"freebaseDbpediaDuplicatesIceHockey8044";
	static String iceHockeyNonDuplicates=outputPath+"freebaseDbpediaNonDuplicatesIceHockey8044";
	
	static String iceHockeyTypePair="\"<http://rdf.freebase.com/ns/common.topic>\"\t\"<http://dbpedia.org/ontology/icehockeyplayer>\"";
	static int iceHockeyNumTypePairs=8044;
	
	static String civilParishDuplicates=outputPath+"freebaseDbpediaDuplicatesCivilParish6365";
	static String civilParishNonDuplicates=outputPath+"freebaseDbpediaNonDuplicatesCivilParish6365";
	
	
	static String civilParishTypePair="\"<http://rdf.freebase.com/ns/location.uk_civil_parish>\"\t\"<http://dbpedia.org/ontology/settlement>\"";
	static int civilParishNumTypePairs=6365;
	
	
	public static void main(String[] args){
		nonAdaptiveSimilarity.Jaccard.withAlphaPreprocessing(
				randomNonDuplicates, outerPath+"jaccard\\f-db-rand-nonDups-Alpha-jaccard");
		
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
	
	/*
	 * This function is going to be very fluid. 
	 */
	private static void testThings(){
		List<String> lines=fetchFirstNLines(freebaseDbpediaAppend, 100);
		for(String line: lines){
			Set<String> typePairs=
					DomainSpecificSubset.typePairs(line);
			if(typePairs==null)
				continue;
			System.out.println("For line: \n"+line);
			for(String typePair: typePairs)
				System.out.println(typePair);
		}
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
