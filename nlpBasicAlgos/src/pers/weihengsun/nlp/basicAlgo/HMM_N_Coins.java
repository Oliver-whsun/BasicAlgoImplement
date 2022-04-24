package pers.weihengsun.nlp.basicAlgo;

import java.util.ArrayList;
import java.util.List;


/**
 * HMM N-Coins Problem
 * 
 * Description:
 * Suppose you have N asymmetrical coins. At the beginning, you pick one of them(base on initial probability PI) and then flip it.
 * Once you have flipped a coin, you decide the next coin will be flipped based on transition probability A. 
 * By continuously choose and flip coins, you can get an observation sequence O (for example, head head tail head).
 * 
 * Analysis:
 * N-Coins Problem can be seen as a very classical HMM problem, and the five basic elements are as blow.
 * States: The hidden states in N-Coins represents which coin is used at each flip.
 * Observations: The observation in N-Coins represents the result (head or tail) at each flip.
 * PI: The initial probability when choosing the first coin.
 * Transition Probability A: N*N matrix identify the probability of choose coin j if you used coin i at the previous flip.
 * Emission Probability B: N*2 matrix identify the probability of head or tail using coin i.
 * 
 * Problems:
 * HMM have 3 classical problems, in N-Coins, they are described as blow.
 * Calculate probability: Given a HMM model and an observation sequence, calculate the probability of this observation.
 * Decode: Given a HMM model and an observation sequence, what is the most possible hidden-state sequence behind this observation. (Which hidden-state sequence explains the observation best?)
 * Learn: Given many observation sequence (the corresponding hidden-state sequence is optional), learn the parameters of this HMM model.
 * @author weiheng.sun
 *
 */

public class HMM_N_Coins {
	public static double calculateProbability(HMM_N_Coins_Model model, String observationSeq) {
		int n = model.N;
		int len = observationSeq.length();
		double[][] dp = new double[n][len];
		//init
		for(int i=0; i<n; i++) {
			int emiIndex = observationSeq.charAt(0)=='H' ? 0 : 1;
			double emiProb = model.emissionProb[i][emiIndex];
			dp[i][0] = model.PI[i] * emiProb;
		}
		//iter
		for(int j=1; j<len; j++) {
			for(int i=0; i<n; i++) {
				double sum = 0.0;
				for(int pre=0; pre<n; pre++) {
					double preProb = dp[pre][j-1];
					double transProb = model.transitionProb[pre][i];
					int emiIndex = observationSeq.charAt(j)=='H' ? 0 : 1;
					double emiProb = model.emissionProb[i][emiIndex];
					double candidate = preProb*transProb*emiProb;
					sum += candidate;
				}
				dp[i][j] = sum;
			}
		}
		//res
		double res = 0.0;
		for(int i=0; i<n; i++) {
			res += dp[i][len-1];
		}
		return res;
	}
	
	public static String viterbiDecode(HMM_N_Coins_Model model, String observationSeq) {
		int n = model.N;
		int len = observationSeq.length();
		double[][] dp = new double[n][len];
		int[][] path = new int[n][len];
		//init
		for(int i=0; i<n; i++) {
			int emiIndex = observationSeq.charAt(0)=='H' ? 0 : 1;
			double emiProb = model.emissionProb[i][emiIndex];
			dp[i][0] = model.PI[i] * emiProb;
		}
		for(int i=0; i<n; i++) {
			for(int j=0; j<len; j++) {
				path[i][j] = -1;
			}
		}
		//iter
		for(int j=1; j<len; j++) {
			for(int i=0; i<n; i++) {
				double max = -1.0;
				int maxIndex = -1;
				for(int pre=0; pre<n; pre++) {
					double preProb = dp[pre][j-1];
					double transProb = model.transitionProb[pre][i];
					int emiIndex = observationSeq.charAt(j)=='H' ? 0 : 1;
					double emiProb = model.emissionProb[i][emiIndex];
					double candidate = preProb*transProb*emiProb;
					if(candidate>max) {
						max = candidate;
						maxIndex = pre;
					}
				}
				dp[i][j] = max;
				path[i][j] = maxIndex;
			}
		}
		//res, find path
		StringBuilder stateSeq = new StringBuilder();
		double max = -1.0;
		int maxIndex = -1;
		for(int i=0; i<n; i++) {
			if(dp[i][len-1]>max) {
				max = dp[i][len-1];
				maxIndex = i;
			}
		}
		stateSeq.append(maxIndex);
		int pre = maxIndex;
		for(int j=len-1; j>0; j--) {
			int tmp = path[pre][j];
			stateSeq.append(tmp);
			pre = tmp;
		}
		stateSeq = stateSeq.reverse();
		return stateSeq.toString();
	}
	
	public static HMM_N_Coins_Model learnModel(List<String> listOfObservationSeqs) {
		//to-do
		return null;
	}
	
	public static HMM_N_Coins_Model learnModel(List<String> listOfObservationSeqs, List<String> listOfStateSeqs, int n) {
		//PI
		int numOfSample = listOfStateSeqs.size();
		double[] pi = new double[n];
		for(int i=0; i<numOfSample; i++) {
			pi[listOfStateSeqs.get(i).charAt(0) - '0']++;
		}
		for(int i=0; i<numOfSample; i++) {
			pi[listOfStateSeqs.get(i).charAt(0) - '0'] /= numOfSample;
		}
		
		//transition probability
		double[][] transitionProb = new double[n][n];
		for(int i=0; i<numOfSample; i++) {
			String stateSeq = listOfStateSeqs.get(i);
			char[] stateSeqCharArr = stateSeq.toCharArray();
			for(int j=0; j<stateSeqCharArr.length-1; j++) {
				transitionProb[stateSeqCharArr[j]-'0'][stateSeqCharArr[j+1]-'0']++;
			}
		}
		for(int i=0; i<n; i++) {
			int sum = 0;
			for(int j=0; j<n; j++) {
				sum += transitionProb[i][j];
			}
			for(int j=0; j<n; j++) {
				if(sum==0) 
					transitionProb[i][j] = 1.0/n;
				else 
					transitionProb[i][j] /= sum;
			}
		}
		//emission probability
		double[][] emissionProb = new double[n][2];
		for(int i=0; i<numOfSample; i++) {
			String stateSeq = listOfStateSeqs.get(i);
			String observSeq = listOfObservationSeqs.get(i);
			int seqLen = stateSeq.length();
			for(int j=0; j<seqLen; j++) {
				if(observSeq.charAt(j)=='H') {
					emissionProb[stateSeq.charAt(j)-'0'][0]++;
				}else {
					emissionProb[stateSeq.charAt(j)-'0'][1]++;
				}
			}
		}
		for(int i=0; i<n; i++) {
			double sum = emissionProb[i][0] + emissionProb[i][1];
			if(sum==0.0) {
				emissionProb[i][0] = 0.5;
				emissionProb[i][1] = 0.5;
			}else {
				emissionProb[i][0] /= sum;
				emissionProb[i][1] /= sum;
			}
			
		}
		//res
		return new HMM_N_Coins_Model(n, pi, transitionProb, emissionProb);
	}
	
	
	
	public static void main(String[] args) {
		HMM_N_Coins_Model model = new HMM_N_Coins_Model(2, 
														new double[] {0.9, 0.1}, 
														new double[][] {{0.9, 0.1}, {0.9, 0.1}}, 
														new double[][] {{0.9, 0.1}, {0.1, 0.9}});
		List<String> observationSeqs = new ArrayList<>();
		observationSeqs.add("HHT");
		observationSeqs.add("THT");
		List<String> stateSeqs = new ArrayList<>();
		stateSeqs.add("012");
		stateSeqs.add("112");
		System.out.println(calculateProbability(model, "HT"));
		System.out.println(viterbiDecode(model, "HT"));
		learnModel(observationSeqs, stateSeqs, 3);
	}
}

class HMM_N_Coins_Model{
	public int N;
	public final String[] states = new String[] {"H", "T"}; //head and tail
	public double[] PI;
	public double[][] transitionProb;
	public double[][] emissionProb;
	
	public HMM_N_Coins_Model(int N, double[] PI, double[][] transitionProb, double[][] emissionProb) {
		this.N = N;
		this.PI = PI;
		this.transitionProb = transitionProb;
		this.emissionProb = emissionProb;
	}
}
