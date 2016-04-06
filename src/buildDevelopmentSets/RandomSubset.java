package buildDevelopmentSets;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class RandomSubset {

/**
 * This class is meant for taking the freebase-dbpedia appends file in
 * 
 * and using it to randomly generate a duplicates file and non-duplicates file
 * for development set experiments.
 */
	/*
	 * appendFile should, for ideal usage, be freebaseDbpediaSameAsAppend
	 * outputFile is where the chosen lines are printed
	 * num is number of desired lines
	 * totalLines is total number of lines in appendFile. We trust user input
	 * on this one.
	 * freebaseDbpediaSameAsAppend has 2,093,007 lines
	 */
	public static void selectDuplicates(String appendFile, 
			String outputFile, int num, int totalLines){
		Scanner in=null;
		PrintWriter out=null;
		Set<Integer> indices=new HashSet<Integer>(num);
		Random r=new Random(System.currentTimeMillis());
		int count=0;
		while(indices.size()<num){
			int k=r.nextInt(totalLines);
			indices.add(k);
		}
		System.out.println("Selection of line indices complete. Printing lines to file...");
		try{
			in=new Scanner(new FileReader(appendFile));
			out=new PrintWriter(new File(outputFile));
			while(in.hasNextLine()){
				String line=in.nextLine();
				if(indices.contains(count))
					out.println(line);
				count++;
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
			out.close();
		}
	}
	
	
	/*
	 * 
	 */
	public static void buildNonDuplicates(String duplicatesFile, 
			String outputFile, int num){
		
	}
}
