package nonAdaptiveSimilarity;

public class CommonSimilarityFunctions {

	/*
	 * The formula implemented here is 
	 * sim=1-editDistance(token1, token2)/max(|token1|,|token2|)
	 */
	public static double normalizedLevensteinSimilarity(String token1, String token2){
		double max=0.0;
		if(token1.length()==0||token2.length()==0)
			return 0.0;
		else
			max=token1.length()>token2.length() ? token1.length() : token2.length();
			
		
		double result=0.0;
		
		result=minDistance(token1, token2);
		//System.out.println(result);
		if(Double.isNaN(result))
				result=0.0;
			
			
		return 1.0-(result/max);
	}
	
	/*
	 * This code was taken from http://www.programcreek.com/2013/12/edit-distance-in-java/
	 * I found the Cohen version to not work properly (or I called it wrong).
	 * I've tested the function and it's working fine.
	 */
	private static int minDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();
	 
		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];
	 
		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}
	 
		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}
	 
		//iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);
	 
				//if last two chars equal
				if (c1 == c2) {
					//update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
	 
					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}
	 
		return dp[len1][len2];
	}
	
	
}
