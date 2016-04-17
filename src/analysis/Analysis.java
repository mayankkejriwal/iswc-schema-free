package analysis;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class Analysis {
	
	public static double[] returnMinMax(String folder){
		double[] results=new double[2];
		results[0]=Double.MAX_VALUE;
		results[1]=Double.MIN_VALUE;
				
		for(int i=1; i<=10; i++){
			String file=folder+"dup-trial-"+i;
			Scanner in=null;
			try{
				in=new Scanner(new FileReader(file));
				while(in.hasNextLine()){
					
					double score=Double.parseDouble(in.nextLine().split("\t")[1]);
					if(score<results[0])
						results[0]=score;
					if(score>results[1])
						results[1]=score;
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				in.close();
			}
			
			file=folder+"nonDup-trial-"+i;
			try{
				in=new Scanner(new FileReader(file));
				while(in.hasNextLine()){
					
					double score=Double.parseDouble(in.nextLine().split("\t")[1]);
					if(score<results[0])
						results[0]=score;
					if(score>results[1])
						results[1]=score;
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				in.close();
			}
		}
		return results;
	}

	/*
	 * This class takes a folder containing the ten trial files (each, for
	 * duplicates and non-duplicates) and in outfile, places three columns
	 * for recall, precision and f-measure. Threshold parameters
	 * must be manually determined by doing mean variance analysis. I've included
	 * a function for this in this file. 
	 * 
	 * Results are averaged across all the 10 trials, and it's these
	 * averages that are printed out to outputFile.
	 */
	public static void adaptivePrintRecPrecFM(String folder, String outfile,
			double thresholdStart, double thresholdEnd, double increment){
		ArrayList<Double> thresholds=sameAsAnalysis.Analysis.buildThresholdRange
				(thresholdStart, thresholdEnd, increment);
		ArrayList<ArrayList<Integer>> avgResults=basicAnalysis(folder+"dup-trial-1",
				folder+"nonDup-trial-1", thresholds);
		int numRows=avgResults.get(0).size();
		Map<Double, Double> recPrecMap=new HashMap<Double, Double>();
		
		for(int i=2; i<=10; i++){
			String dupFile=folder+"dup-trial-"+i;
			String nonDupFile=folder+"nonDup-trial-"+i;
			addArrays(avgResults, basicAnalysis(dupFile, nonDupFile, thresholds));
		}
		
		//avgResults does not actually contain the average, 
		//only the total, make sure to do this computation in the functions below
		
		for(int i=0; i<numRows; i++){
			double[] fields=new double[5];	//fields[0] is meaningless
			fields[1]=avgResults.get(0).get(i)*1.0/10;
			fields[2]=avgResults.get(1).get(i)*1.0/10;
			fields[3]=avgResults.get(2).get(i)*1.0/10;
			fields[4]=avgResults.get(3).get(i)*1.0/10;
			double recall=(fields[1])/(fields[1]+fields[4]);
			double precision=0.0;
			if((fields[1]+fields[3])!=0.0)
				precision=fields[1]/(fields[1]+fields[3]);
			if(!recPrecMap.containsKey(recall))
				recPrecMap.put(recall, precision);
			else
			{
				if(recPrecMap.get(recall)<precision)
					recPrecMap.put(recall, precision);
			}
		}
		
		PrintWriter out=null;
		try{
			out=new PrintWriter(new File(outfile));
			out.println("recall,precision,f-measure");
			List<Double> recall=new ArrayList<Double>(recPrecMap.keySet());
			Collections.sort(recall);
			for(double r: recall)
				if(r==0.0 && recPrecMap.get(r)==0.0)
					continue;
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
	 * Does a1=a1+a2. 
	 */
	private static void addArrays(ArrayList<ArrayList<Integer>> a1, ArrayList<ArrayList<Integer>> a2){
		if(a1.size()!=a2.size()){
			System.out.println("error in addArrays! Arrays not of compatible size");
			return;
		}
		
		for(int i=0; i<a1.size(); i++)
			if(a1.get(i).size()!=a2.get(i).size()){
				System.out.println("error in addArrays! Sub-arrays not of compatible size");
				return;
			}else
				for(int j=0; j<a1.get(i).size(); j++)
					a1.get(i).set(j, a1.get(i).get(j)+a2.get(i).get(j));
			
				
		
	}
	
	/*
	 * Mean-variance analysis of trial files output by the adaptive experiments
	 * into the folder.
	 */
	
	/*
	 * This function accepts two files (dupFile and nonDupFile) that contain two tab-delimited
	 * fields id \t score. Over thresholds specified by 'thresholds', we
	 * compute corr. TPs, FPs, TNs, and FNs and return them as lists (in stated order) in a list.
	 * Meant to be called by adaptive procedures.
	 */
	
	private static ArrayList<ArrayList<Integer>> basicAnalysis(String dupFile, 
			String nonDupFile, ArrayList<Double> thresholds){
		ArrayList<ArrayList<Integer>> results=new ArrayList<ArrayList<Integer>>();
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
		
		results.add(TPs);
		results.add(FPs);
		results.add(TNs);
		results.add(FNs);
		
		return results;
		
	}

	public static void printMinMaxMeanVariance(String folder){
		double min=Double.MAX_VALUE;
		double max=Double.MIN_VALUE;
		double total=0.0;
		double totalSqrd=0.0;
		double variance=0.0;
		int count=0;
		
		for(int i=1; i<=10; i++){
			String file=folder+"dup-trial-"+i;
			Scanner in=null;
			try{
				in=new Scanner(new FileReader(file));
				while(in.hasNextLine()){
					count++;
					double score=Double.parseDouble(in.nextLine().split("\t")[1]);
					if(score<min)
						min=score;
					if(score>max)
						max=score;
					totalSqrd+=(Math.pow(score, 2));
					total+=score;
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				in.close();
			}
			
			file=folder+"nonDup-trial-"+i;
			try{
				in=new Scanner(new FileReader(file));
				while(in.hasNextLine()){
					count++;
					double score=Double.parseDouble(in.nextLine().split("\t")[1]);
					if(score<min)
						min=score;
					if(score>max)
						max=score;
					totalSqrd+=(Math.pow(score, 2));
					total+=score;
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				in.close();
			}
		}
		variance=(totalSqrd/count)-Math.pow(total/count, 2);
		System.out.println("Across all dup and nonDup trial files...");
		System.out.println("min: "+min);
		System.out.println("max: "+max);
		System.out.println("average: "+(total/count));
		System.out.println("variance: "+(variance));
		System.out.println("std. dev: "+Math.sqrt(variance));
	}
	
	/*
	 * All functions below are designed to analyze results of non-adaptive experiments
	 */
	
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
					continue;
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

	/*
	 * This function accepts two files (dupFile and nonDupFile) that contain two tab-delimited
	 * fields id \t score. Over threshold range of 0.0 to 1.0 in increments of 0.01, we
	 * print out Threshold \t TP \t FP \t TN \t FN columns (with header) in outfile.
	 * 
	 */
	
	

}
