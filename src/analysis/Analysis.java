package analysis;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class Analysis {
	
	/*
	 * Takes the output of basicAnalysis and prints out three columns 
	 * (comma separated with header elements "recall" "precision" and "f-measure")
	 * Note that for each value of recall, we only print out the maximum value
	 * of precision obtained.
	 */
	public static void printRecallPrecisionFMeasure(String basicAnalysisFile, String outfile){
		Map<Double, Double> recPrecMap=new HashMap<Double, Double>();
		Scanner in=null;
		PrintWriter out=null;
		try{
			in=new Scanner(new FileReader(basicAnalysisFile));
			if(in.hasNextLine())
				in.nextLine();
			while(in.hasNextLine()){
				String[] fields=in.nextLine().split("\t");
				double recall=Double.parseDouble(fields[1])/(Double.parseDouble(fields[1])+Double.parseDouble(fields[4]));
				double precision=0.0;
				if((Double.parseDouble(fields[1])+Double.parseDouble(fields[3]))!=0.0)
					precision=Double.parseDouble(fields[1])/(Double.parseDouble(fields[1])+Double.parseDouble(fields[3]));
				if(!recPrecMap.containsKey(recall))
					recPrecMap.put(recall, precision);
				else
				{
					if(recPrecMap.get(recall)<precision)
						recPrecMap.put(recall, precision);
				}
			
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		finally
		{
			in.close();
		}
		
		try{
			out=new PrintWriter(new File(outfile));
			out.println("recall,precision,f-measure");
			List<Double> recall=new ArrayList<Double>(recPrecMap.keySet());
			Collections.sort(recall);
			for(double r: recall)
				if(r==0.0 && recPrecMap.get(r)==0.0)
					out.println("0.0,0.0,0.0");
				else
					out.println(r+","+recPrecMap.get(r)+","+
				(2*r*recPrecMap.get(r)/(r+recPrecMap.get(r))));
			
			
		}catch(IOException e){
			e.printStackTrace();
		}
		finally
		{
			out.close();
		}
		
	}
	
	/*
	 * This function accepts two files (dupFile and nonDupFile) that contain two tab-delimited
	 * fields id \t score. Over threshold range of 0.0 to 1.0 in increments of 0.01, we
	 * print out Threshold \t TP \t FP \t TN \t FN columns (with header) in outfile.
	 * 
	 */
	
	public static void basicAnalysis(String dupFile, String nonDupFile, String outfile){
		ArrayList<Double> thresholds=sameAsAnalysis.Analysis.buildThresholdRange();
		ArrayList<Integer> TPs=new ArrayList<Integer>();
		ArrayList<Integer> FPs=new ArrayList<Integer>();
		ArrayList<Integer> TNs=new ArrayList<Integer>();
		ArrayList<Integer> FNs=new ArrayList<Integer>();
		for(int i=0; i<thresholds.size(); i++){
			TPs.add(0);
			FPs.add(0);
			TNs.add(0);
			FNs.add(0);
		}
		
		Scanner in=null;
		try{
			in=new Scanner(new FileReader(dupFile));
			while(in.hasNextLine()){
				double score=Double.parseDouble(in.nextLine().split("\t")[1]);
				for(int i=0; i<thresholds.size(); i++)
					if(score>=thresholds.get(i))
						TPs.set(i, TPs.get(i)+1);
					else
						FNs.set(i, FNs.get(i)+1);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
		}
		
		try{
			in=new Scanner(new FileReader(nonDupFile));
			while(in.hasNextLine()){
				double score=Double.parseDouble(in.nextLine().split("\t")[1]);
				for(int i=0; i<thresholds.size(); i++)
					if(score<thresholds.get(i))
						TNs.set(i, TNs.get(i)+1);
					else
						FPs.set(i, FPs.get(i)+1);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
		}
		
		PrintWriter out=null;
		try{
			out=new PrintWriter(new File(outfile));
			out.println("threshold\tTP\tTN\tFP\tFN");
			for(int i=0; i<thresholds.size(); i++)
				out.println(thresholds.get(i)+"\t"+TPs.get(i)+"\t"+TNs.get(i)+"\t"+FPs.get(i)+"\t"+FNs.get(i));
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			out.close();
		}
	}

	/*
	 * This function accepts two files (dupFile and nonDupFile) that contain two tab-delimited
	 * fields id \t score. Over threshold range of 0.0 to 0.1 in increments of 0.0001, we
	 * print out Threshold \t TP \t FP \t TN \t FN columns (with header) in outfile.
	 * 
	 */
	
	public static void basicAnalysisTFIDF(String dupFile, String nonDupFile, String outfile){
		ArrayList<Double> thresholds=sameAsAnalysis.Analysis.buildThresholdRange(0.0, 0.1, 0.0001);
		ArrayList<Integer> TPs=new ArrayList<Integer>();
		ArrayList<Integer> FPs=new ArrayList<Integer>();
		ArrayList<Integer> TNs=new ArrayList<Integer>();
		ArrayList<Integer> FNs=new ArrayList<Integer>();
		for(int i=0; i<thresholds.size(); i++){
			TPs.add(0);
			FPs.add(0);
			TNs.add(0);
			FNs.add(0);
		}
		
		Scanner in=null;
		try{
			in=new Scanner(new FileReader(dupFile));
			while(in.hasNextLine()){
				double score=Double.parseDouble(in.nextLine().split("\t")[1]);
				for(int i=0; i<thresholds.size(); i++)
					if(score>=thresholds.get(i))
						TPs.set(i, TPs.get(i)+1);
					else
						FNs.set(i, FNs.get(i)+1);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
		}
		
		try{
			in=new Scanner(new FileReader(nonDupFile));
			while(in.hasNextLine()){
				double score=Double.parseDouble(in.nextLine().split("\t")[1]);
				for(int i=0; i<thresholds.size(); i++)
					if(score<thresholds.get(i))
						TNs.set(i, TNs.get(i)+1);
					else
						FPs.set(i, FPs.get(i)+1);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			in.close();
		}
		
		PrintWriter out=null;
		try{
			out=new PrintWriter(new File(outfile));
			out.println("threshold\tTP\tTN\tFP\tFN");
			for(int i=0; i<thresholds.size(); i++)
				out.println(thresholds.get(i)+"\t"+TPs.get(i)+"\t"+TNs.get(i)+"\t"+FPs.get(i)+"\t"+FNs.get(i));
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			out.close();
		}
	}
	
	

}
