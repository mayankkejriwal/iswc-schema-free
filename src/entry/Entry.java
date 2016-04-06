package entry;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Entry {

	static String inputPath="C:\\Users\\Mayank\\SkyDrive\\Documents\\"
			+ "experiments\\sameAs\\derived-data-files\\";
	static String outputPath="C:\\Users\\Mayank\\SkyDrive\\Documents\\"
			+ "experiments\\iswc-schema-free\\developmentSets\\";
	static String freebaseDbpediaAppend=inputPath+"freebaseDbpediaSameAsAppend";
	static String randomDuplicates=outputPath+"freebaseDbpediaDuplicatesRandom10000";
	
	public static void main(String[] args){
		///buildDevelopmentSets.RandomSubset.selectDuplicates(
			//	freebaseDbpediaAppend, randomDuplicates, 10000, 2093007);
		
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
