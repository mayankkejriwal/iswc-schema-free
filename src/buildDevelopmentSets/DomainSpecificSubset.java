package buildDevelopmentSets;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class DomainSpecificSubset {

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
	private static Set<String> typePairs(String freebaseDbpediaLine){
		Set<String> result=new HashSet<String>();
		String[] fields=freebaseDbpediaLine.split("\t");
		String freebaseTypes=null;
		String dbpediaTypes=null;
		int count=0;
		for(String field: fields){
			if(field.contains("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
				if(count==0)
					freebaseTypes=new String(field);
				else if(count==1)
					dbpediaTypes=new String(field);
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
		
		String[] fTypes;
		String[] dTypes;
		
		return result;
	}
}
