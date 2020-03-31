package pers.weihengsun.nlp.classify;


import java.util.Map;
import java.util.HashMap;

public class NaiveBayesClassifier {
	
	/**
	 * Used to hold how many cases have been seen 
	 * This is the denominator when calculate P(category_i)
	 */
	private int numTrainCase = 0;
	
	/**
	 * Used to hold how many words(including repeats) are in a category
	 * This is used when calculate P(word | category)
	 */
	private Map<String, Integer> catNm2WordCount = null;
	
	/**
	 * Used to hold all possible categories and their counts in training corpus
	 */
	private Map<String,Integer> catNm2Count = null;
	
	/**
	 * Used to hold which word appears how many times in every category
	 * @Key: String a vocabulary
	 * @Value: a category--count Map
	 */
	private Map<String, Map<String,Integer>> vocabulary2CatNm2Count = null;
	
	public NaiveBayesClassifier() {
		this.catNm2WordCount = new HashMap<String, Integer>();
		this.catNm2Count = new HashMap<String,Integer>();
		this.vocabulary2CatNm2Count = new HashMap<String, Map<String,Integer>>();
	}
	
	/**
	 * Used to train a sentence given its category
	 * @param aSentence in the format of String[], every string is a word
	 * @param catNm
	 */
	public void trainACase(String[] aSentence, String catNm) {
		numTrainCase ++;
		//FIRST, maintain count of categories
		catAddOne(catNm);
		//SECOND, maintain count of vocabularies
		for(String curVocab : aSentence)
			vocabularyAddOne(curVocab,catNm);
	}
	
	/**
	 * Used to classify a test sentence in the format String[], every string is a word
	 * @param aTestSentence
	 * @return category name 
	 */
	public String classify(String[] aTestSentence) {
		Map<String,Double> catNm2LogProb = calLogProb(aTestSentence);
		double maxLogProb = Double.NEGATIVE_INFINITY; 
		String bestCat = null;
		for(String aCatNm : catNm2LogProb.keySet()) {
			double curCatLogProb = catNm2LogProb.get(aCatNm);
			if(curCatLogProb>maxLogProb) {
				maxLogProb = catNm2LogProb.get(aCatNm);
				bestCat = aCatNm;
			}
		}
		return bestCat;
	}
	
	/**
	 * Used to add one count for a specific category 
	 * @param catNm
	 */
	private void catAddOne(String catNm) {
		if(catNm2Count.keySet().contains(catNm))
			catNm2Count.put(catNm, catNm2Count.get(catNm)+1);
		else catNm2Count.put(catNm, 1);
	}
	
	/**
	 * Used to add one count for this specific word in this specific category
	 * @param aWord
	 * @param catNm
	 */
	private void vocabularyAddOne(String aWord, String catNm) {
		if(vocabulary2CatNm2Count.keySet().contains(aWord)) {
			Map<String, Integer> catNm2WordCountMap = vocabulary2CatNm2Count.get(aWord);
			if(catNm2WordCountMap.containsKey(catNm)) {
				catNm2WordCountMap.put(catNm,catNm2WordCountMap.get(catNm)+1);
			}else {
				catNm2WordCountMap.put(catNm, 1);
			}
		}else {
			Map<String, Integer> catNm2WordCountMap = new HashMap<String, Integer>();
			catNm2WordCountMap.put(catNm, 1);
			vocabulary2CatNm2Count.put(aWord, catNm2WordCountMap);
		}
		if(catNm2WordCount.containsKey(catNm)) {
			catNm2WordCount.put(catNm, catNm2WordCount.get(catNm)+1);
		}else {
			catNm2WordCount.put(catNm, 1);
		}
	}
	
	/**
	 * Used to calculate the log probability of a testSentence of every possible category
	 * @param aTestSentence
	 * @return Map<String,Double> catNm2LogProb
	 */
	private Map<String,Double> calLogProb(String[] aTestSentence){
		Map<String,Double> catNm2LogProb = new HashMap<String, Double>();
		for(String aCat : catNm2Count.keySet()) {
			double logProb = calLogProb(aTestSentence, aCat);
			catNm2LogProb.put(aCat, logProb);
		}
		return catNm2LogProb;
	}
	
	
	/**
	 * Used to calculate the log probability of a testSentence for a possible category
	 * @param aTestSentence
	 * @param aCatNm
	 * @return log probability of a sentence for a possible category
	 */
	private double calLogProb(String[] aTestSentence, String aCatNm) {
		//FIRST,calculate P(category)
		double probCat = (double)catNm2Count.get(aCatNm)/numTrainCase;
		double logProbCat = Math.log(probCat);
		//SECOND, calculate P(word_i | category)
		double logProbSentenceGivenCat = 0;
		for(String aWord : aTestSentence) {
			//skip unseen vocabulary
			if(!vocabulary2CatNm2Count.containsKey(aWord))
				continue;
			logProbSentenceGivenCat += calLogProb(aWord, aCatNm);
		}
		double res = logProbCat + logProbSentenceGivenCat;
		return res;
	}
	
	/**
	 * Used to calculate the log probability of a word given a category
	 * @Note This method apply add-one smooth during classify process, no smooth strategy when training
	 * @param aWord
	 * @param aCatNm
	 * @return log probability of a word given a category
	 */
	private double calLogProb(String aWord, String aCatNm) {
		int countWordinCat = vocabulary2CatNm2Count.get(aWord).get(aCatNm) == null ?
				0 : vocabulary2CatNm2Count.get(aWord).get(aCatNm);
		double numerator = countWordinCat + 1;
		double denominator = catNm2WordCount.get(aCatNm) + vocabulary2CatNm2Count.size();
		double probWordGivenCat = numerator/denominator;
		return Math.log(probWordGivenCat);
	}
}
