package pers.weihengsun.nlp.embedding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import pers.weihengsun.nlp.util.MathUtil;

import java.util.Set;



public class CoOccurrenceEmbedding {
	
	private Map<String, Integer> word2indexMap = null;
	
	private Map<String, int[]> word2vecMap = null;
	
	/**
	 * How many words are treated as context for a central target word.
	 * Example: if windowWidth = 2, then for sentence "This includes information available on the Internet ..."
	 * "This", "includes", "available", "on" are context words of target word "information"
	 */
	private int windowWidth = -1;
	
	/**
	 * Go through training corpus, recognize all words, distribute unique index
	 * @param corpusFile
	 */
	private void recognizeVocabularies(File corpusFile) {
		int index = 0;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(corpusFile),"UTF-8"));
			String aLine = null;
			while((aLine=reader.readLine())!= null) {
				String[] words = aLine.split(" ");
				for(String aWord : words) {
					if(!word2indexMap.containsKey(aWord)) {
						word2indexMap.put(aWord, index);
						index ++;
					}
				}
			}
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Vocalubary recognition complete, " + 
				word2indexMap.size() + " words found.");
	}
	
	private void countCoOccurrence(File trainFile) {
		if(windowWidth < 1) {
			String msg = "Window width should be larger than 0, found windowWidth= " +
					windowWidth;
			throw new IllegalArgumentException(msg);
		}
		int trainedNum = 0;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(trainFile),"UTF-8"));
			String aLine = null;
			while((aLine=reader.readLine())!= null) {
				String[] words = aLine.split(" ");
				int length = words.length;
				for(int target=0; target<length; target++) {
					String targetWord = words[target];
					for(int context = target-windowWidth; context <= target+windowWidth; context++) {
						if(context < 0 || context >= length || context == target) continue;
						String contextWord = words[context];
						vectorComponentAddOne(targetWord, contextWord);
					}
				}
				trainedNum ++;
				if(trainedNum%1000 == 0) {
					System.out.println(trainedNum + " utterances has been trained.");
				}
			}
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void vectorComponentAddOne(String target, String context) {
		if(!word2vecMap.containsKey(target)) {
			int vocabSize = word2indexMap.size();
			int[] targetVector = new int[vocabSize];
			word2vecMap.put(target, targetVector);
		}
		int[] targetVector = word2vecMap.get(target);
		int contextIndex = word2indexMap.get(context);
		targetVector[contextIndex]++;
	}
	
	public CoOccurrenceEmbedding(int windowWidth) {
		this.windowWidth = windowWidth;
		this.word2indexMap = new HashMap<String, Integer>();
		this.word2vecMap = new HashMap<String, int[]>();
	}
	
	public void trainEmbedding(String trainCorpusFullPath) {
		File corpusFile = new File(trainCorpusFullPath);
		if(corpusFile==null || !corpusFile.exists()) {
			System.out.println("ERROR! File " + trainCorpusFullPath + " does not exist!");
			return;
		}
		System.out.println("Start to train co-occurrence embedding...");
		recognizeVocabularies(corpusFile);
		
		countCoOccurrence(corpusFile);
		
		reviewEmbedding();
	}
	
	public int vocabularySize() {
		return word2vecMap.size();
	}
	
	public void showVocabulary(int showNum) {
		Set<String> vocabularySet = word2vecMap.keySet();
		Iterator<String> vocabularyIter = vocabularySet.iterator();
		StringBuilder res = new StringBuilder();
		for(int i=0; i<showNum; i++) {
			res = res.append(vocabularyIter.next() + ", ");
		}
		res = res.append("...");
		System.out.println("The first " + showNum + " vocabularies are " + res);
	}
	
	public void showVocabularyByIndex(int index) {
		Set<String> vocalSet = word2indexMap.keySet();
		for(String aWord : vocalSet) {
			if(word2indexMap.get(aWord) == index) {
				System.out.println("The " + index + "th word in vocabulary is " + aWord);
				return;
			}
		}
	}
	
	public double calSimilarity(String word1, String word2) {
		if(!word2vecMap.containsKey(word1)) {
			System.out.println("Embedding does not contain word: " + word1);
		}
		if(!word2vecMap.containsKey(word2)) {
			System.out.println("Embedding does not contain word: " + word2);
		}
		int[] wordVec1 = word2vecMap.get(word1);
		int[] wordVec2 = word2vecMap.get(word2);
		double cos = MathUtil.cosine(wordVec1, wordVec2);
		
		return cos;
	}
	
	public String nearestWord(String target) {
		double maxSimilarity = 0.0;
		String nearestWord = null;
		for(String aWord : word2vecMap.keySet()) {
			double cos = calSimilarity(aWord, target);
			if(cos > maxSimilarity) {
				if(target.equals(aWord)) continue;
				nearestWord = aWord;
				maxSimilarity = cos;
			}
		}
		return nearestWord;
	}
	
	public void reviewEmbedding() {
		System.out.println("Vocabulary size: " + vocabularySize());
		showVocabulary(100);
		System.out.println("Use commands below to further review trained embedding.");
		System.out.println("To see the first Ns words: -f n; example: -f 100");
		System.out.println("To see the I-th word: -i I; example: -i 99");
		System.out.println("To compare the similarity between words: -c word1 word2; example: -c good well");
		System.out.println("To see the nearest-word of given word: -n Word; example: -n good");
		System.out.println("To quit review progress: -q ; example: -q");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        try {
	        do {
	        	userInput = br.readLine();
	        	if(userInput.startsWith("-f")) {
	        		int showNum = Integer.parseInt(userInput.split(" ")[1]);
	        		showVocabulary(showNum);
	        	}else if(userInput.startsWith("-i")) {
	        		int showIndex = Integer.parseInt(userInput.split(" ")[1]);
	        		showVocabularyByIndex(showIndex);
	        	}else if(userInput.startsWith("-c")) {
	        		String args[] = userInput.split(" ");
	        		String word1 = args[1];
	        		String word2 = args[2];
	        		System.out.println("Similarity between " + word1 + " "
	        				+ word2 + " is " + calSimilarity(word1, word2));
	        	}else if(userInput.startsWith("-n")) {
	        		String targetWord = userInput.split(" ")[1];
	        		System.out.println("The nearest-word of " + targetWord +
	        				" is " + nearestWord(targetWord));
	        	}else if(userInput.startsWith("-q")){
	        		System.out.println("Quit review.");
	        		return;
	        	}else {
	        		System.out.println("Invalid input! Use commands below to further review trained embedding.");
	        		System.out.println("To see the first Ns words: -f n; example: -f 100");
	        		System.out.println("To see the I-th word: -i I; example: -i 99");
	        		System.out.println("To compare the similarity between words: -c word1 word2; example: -c good well");
	        		System.out.println("To see the nearest-word of given word: -n Word; example: -n good");
	        		System.out.println("To quit review progress: -q ; example: -q");
	        	}
	        }while(true);
        }catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		CoOccurrenceEmbedding embed = new CoOccurrenceEmbedding(2);
		embed.trainEmbedding("./corpus/word_embedding_train.txt");
	}
}
