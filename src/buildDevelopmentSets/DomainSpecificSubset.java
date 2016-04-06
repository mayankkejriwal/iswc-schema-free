package buildDevelopmentSets;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class DomainSpecificSubset {
	/**
	 * This class is meant for taking the freebase-dbpedia appends file in
	 * experiments\sameAs\derived-data-files\
	 * and using it to randomly generate a duplicates file and non-duplicates file
	 * for development set experiments. The difference between this file
	 * and RandomSubset is that we specify domains by virtue of type pairs
	 */
	/*
	 * appendFile should, for ideal usage, be freebaseDbpediaSameAsAppend
	 * outputFile is where the chosen lines are printed
	 * num is number of desired lines. If this is more than the number
	 * of instances that have the specified type pair, the entire file
	 * will end up getting read, so choose carefully.
	 * typePair is a tab-delimited pair of the form freebase-type \t dbpedia-type
	 * For each line read in from appendFile, we extract the types using typePairs
	 * and check if the line is 'compatible'. If so, we print it. This continues
	 * till the printed count reaches num.
	 * 
	 * We chose the following two type pairs for the two development sets:
	 * "<http://rdf.freebase.com/ns/common.topic>"	"<http://dbpedia.org/ontology/icehockeyplayer>"
	 * "<http://rdf.freebase.com/ns/location.uk_civil_parish>"	"<http://dbpedia.org/ontology/settlement>"
	 */
	public static void selectDuplicates(String appendFile, 
			String outputFile, int num, String typePair){
		Scanner in=null;
		PrintWriter out=null;
		int count=0;
		try{
			in=new Scanner(new FileReader(appendFile));
			out=new PrintWriter(new File(outputFile));
			while(in.hasNextLine()&& count<num){
				String line=in.nextLine();
				Set<String> typePairs=typePairs(line);
				if(typePairs==null)
					continue;
				if(typePairs.contains(typePair)){
					out.println(line);
					count++;
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
			out.close();
		}
	}
	
	/*
	 * Takes the append file as input and outputs type pair counts
	 * We will use this information for building the domain specific datasets
	 */
	public static void printTypeStatistics(String appendFile){
		Scanner in=null;
		HashMap<String, Integer> typeStatistics=new HashMap<String,Integer>();
		try{
			in=new Scanner(new FileReader(appendFile));
			while(in.hasNextLine()){
				String line=in.nextLine();
				Set<String> typePairs=typePairs(line);
				if(typePairs==null)
					continue;
				for(String typePair: typePairs){
					if(!typeStatistics.containsKey(typePair))
						typeStatistics.put(typePair, 0);
					typeStatistics.put(typePair, typeStatistics.get(typePair)+1);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		finally{
			in.close();
		}
		
		for(String k: typeStatistics.keySet())
			System.out.println(k+"\t"+typeStatistics.get(k));
	}
	
	/*
	 * Takes a line from the freebaseDbpedia append file and outputs a set
	 * containing pairs (of tab-delimited strings) of the form 
	 * freebase-type	dbpedia-type
	 * Since a pair of instances can be represented as many pairs of types,
	 * we opt for the set structure. Also, we use the special type property
	 * http://www.w3.org/1999/02/22-rdf-syntax-ns#type
	 * for locating types.
	 */
	public static Set<String> typePairs(String freebaseDbpediaLine){
		Set<String> result=new HashSet<String>();
		String[] fields=freebaseDbpediaLine.split("\t");
		String[] freebaseTypes=null;
		String[] dbpediaTypes=null;
		int count=0;
		for(String field: fields){
			if(field.contains("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
				if(count==0){
					String temp=field.split("\":\\[")[1];
					temp=temp.substring(0, temp.length()-1);
					freebaseTypes=temp.split(", ");
				}
				else if(count==1){
					String temp=field.split("\":\\[")[1];
					temp=temp.substring(0, temp.length()-1);
					dbpediaTypes=temp.split(", ");
				}
				count++;
			}
			
		}
		fields=null;
		if(count<2)
			return null;
		else if(count>2){
			System.out.println("error in typePairs! More than two type fields...");
			System.out.println(freebaseDbpediaLine);
			return null;
		}
		
		for(String freebaseType:freebaseTypes)
			for(String dbpediaType:dbpediaTypes)
				result.add(freebaseType+"\t"+dbpediaType);
		
		return result;
	}
	
	/*
	 * We'll just call the RandomSubset function since the logic is the same.
	 */
	public static void buildNonDuplicates(String duplicatesFile, 
			String outputFile, int num){
		RandomSubset.buildNonDuplicates(duplicatesFile, outputFile, num);
	}
}
