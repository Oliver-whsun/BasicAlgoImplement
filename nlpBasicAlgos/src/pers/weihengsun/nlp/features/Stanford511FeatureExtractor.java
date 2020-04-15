package pers.weihengsun.nlp.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * <code>Stanford511FeatureExtractor</code> is a super simple FeatureExtractor used to classify movie reviews.
 * 
 * <p> The definitions of features are exactly same as <bold>Speech_and_Language_processing_Stanford_2019.pdf</bold> section 5.1.1
 * 
 * @author weiheng.sun
 *
 */
public class Stanford511FeatureExtractor extends AbstractFeatureExtractor {
	
	private final String LEXICON_FILE_PATH = "./dictionary/opinion_lexicon/";
	private final String LANGUAGE_CODE = "en";
	private final String LEXICON_FILE_MIDDLE_NAME = "-words-";
	private final String LEXICON_FILE_FORMAT = ".txt";
	private final String NEGATIVE_KEYWORD = "negative";
	private final String POSITIVE_KEYWORD = "positive";
	private final String PRONOUNS_KEYWORD = "pronouns";
	
	private Set<String> positiveLexiconSet = new HashSet<String>();
	private Set<String> negativeLexiconSet = new HashSet<String>();
	private Set<String> pronounsWordSet = new HashSet<String>();

	public Stanford511FeatureExtractor() {
		super(6);
		loadLexiconSet("positive");
		loadLexiconSet("negative");
		loadLexiconSet("pronouns");
	}
	
	@Override
	public Features<Double> extractFeatures(String[] input) {
		Double[] values = new Double[this.featureDimension];
		values[0] = calF1(input);
		values[1] = calF2(input);
		values[2] = calF3(input);
		values[3] = calF4(input);
		values[4] = calF5(input);
		values[5] = calF6(input);
		return new Features<Double>(values);
	}
	
	private void loadLexiconSet(String posORneg) {
		String lexiconFileNm = null;
		switch (posORneg) {
		case "positive":
			lexiconFileNm  = LEXICON_FILE_PATH + POSITIVE_KEYWORD + LEXICON_FILE_MIDDLE_NAME
			+ LANGUAGE_CODE + LEXICON_FILE_FORMAT;
			break;
		case "negative":
			lexiconFileNm = LEXICON_FILE_PATH + NEGATIVE_KEYWORD + LEXICON_FILE_MIDDLE_NAME
			+ LANGUAGE_CODE + LEXICON_FILE_FORMAT;
			break;
		case "pronouns":
			lexiconFileNm = LEXICON_FILE_PATH + PRONOUNS_KEYWORD + LEXICON_FILE_MIDDLE_NAME
			+ LANGUAGE_CODE + LEXICON_FILE_FORMAT;
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + posORneg);
		}
		BufferedReader br = null;
		try {
			File lexiconFile = new File(lexiconFileNm);
			br = new BufferedReader(new FileReader(lexiconFile));
			String curLine = null;
			while((curLine = br.readLine()) != null) {
				if(curLine.isBlank() || curLine.startsWith(";")) continue;
				switch (posORneg) {
				case "positive":
					positiveLexiconSet.add(curLine);
					break;
				case "negative":
					negativeLexiconSet.add(curLine);
					break;
				case "pronouns":
					pronounsWordSet.add(curLine);
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + posORneg);
				}
			}
			br.close();
		}catch (FileNotFoundException e) {
			System.out.println(e.toString()+" Full Path= "+lexiconFileNm);
		}catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println("Loaded lexiconFile from "+lexiconFileNm);
	}
	
	/**
	 * @param input
	 * @return count(positive lexicon) belongs to input
	 */
	private Double calF1(String[] input) {
		double count = 0.0;
		for(String curWord : input) {
			if(positiveLexiconSet.contains(curWord)) count++;
		}
		return count;
	}
	
	/**
	 * @param input
	 * @return count(negative lexicon) belongs to input
	 */
	private Double calF2(String[] input) {
		double count = 0.0;
		for(String curWord : input) {
			if(negativeLexiconSet.contains(curWord)) 
				count++;
		}
		return count;
	}
	
	/**
	 * @param input
	 * @return 1 if "no" belongs to input, otherwise 0
	 */
	private Double calF3(String[] input) {
		for(String aWord : input) {
			if(aWord.toLowerCase().equals("no")) return 1.0;
		}
		return 0.0;
	}
	
	/**
	 * @param input
	 * @return count(1st and 2nd pronouns) belongs to input
	 */
	private Double calF4(String[] input) {
		double count = 0.0;
		for(String curWord : input) {
			if(pronounsWordSet.contains(curWord)) count++;
		}
		return count;
	}
	
	/**
	 * @param input
	 * @return 1 if "!" belongs to input, otherwise 0
	 */
	private Double calF5(String[] input) {
		for(String aWord : input) {
			if(aWord.equals("!")) return 1.0;
		}
		return 0.0;
	}
	
	/**
	 * @param input
	 * @return log(word count of input)
	 */
	private Double calF6(String[] input) {
		return Math.log(input.length);
	}
	
	public static void main(String[] args) {
		String rawInput = "It's hokey . There are virtually no surprises , and the writing is second-rate . So why was it so enjoyable ? For one thing , the cast is great . Another nice touch is the music . I was overcome with the urge to get off the couch and start dancing . It sucked me in , and it'll do the same to you .";
		String[] processedInput = rawInput.toLowerCase().split(" ");
		Stanford511FeatureExtractor s511fe = new Stanford511FeatureExtractor();
		Features<Double> f = s511fe.extractFeatures(processedInput);
		System.out.println("Extracted features: "+f);
	}
}
