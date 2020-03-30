package pers.weihengsun.nlp.basicAlgo;

public class MinimumEditDistance {
	int[][] initDpArray(String wordA, String wordB){
		int lenA = wordA.length();
		int lenB = wordB.length();
		int[][] dp = new int[lenA][lenB];
		dp[0][0] = wordA.charAt(0)==wordB.charAt(0) ? 0 : 2;
		boolean metSameCharI = wordA.charAt(0)==wordB.charAt(0);
		for(int i=1; i<lenA; i++){
			if(metSameCharI) dp[i][0] = dp[i-1][0] + 1;
			else{
				if(wordA.charAt(i)==wordB.charAt(0)){
					metSameCharI = true;
					dp[i][0] = dp[i-1][0] - 1;
				}else dp[i][0] = dp[i-1][0] + 1;
			}
		}
		boolean metSameCharJ = wordA.charAt(0)==wordB.charAt(0);
		for(int j=1; j<lenB; j++){
			if(metSameCharI) dp[0][j] = dp[0][j-1] + 1;
			else{
				if(wordA.charAt(0)==wordB.charAt(j)){
					metSameCharJ = true;
					dp[0][j] = dp[0][j-1] - 1;
				}else dp[0][j] = dp[0][j-1] + 1;
			}
		}
		return dp;
	}

	int[][] inferDpArray(int[][] dp, String wordA, String wordB){
		int lenA = wordA.length();
		int lenB = wordB.length();
		for(int i=1; i<lenA; i++){
			for(int j=1; j<lenB; j++){
				int situation1, situation2, situation3 = Integer.MAX_VALUE;
				situation1 = dp[i-1][j] +1;
				situation2 = dp[i][j-1] +1;
				situation3 = wordA.charAt(i)==wordB.charAt(j) ? dp[i-1][j-1] : dp[i-1][j-1] +2;
				dp[i][j] = Math.min(Math.min(situation1,situation2),situation3);
			}
		}
		return dp;
	}

	public static void main(String[] args){
		MinimumEditDistance med = new MinimumEditDistance();
		String wordA = args[0];
		String wordB = args[1];
		int[][] dp = med.initDpArray(wordA,wordB);
		dp = med.inferDpArray(dp,wordA,wordB);
		int res = dp[wordA.length()-1][wordB.length()-1];
		System.out.println("The MinimumEditDistance between "+wordA+" and "+wordB+" is "+res);
	}
}
