package pers.weihengsun.nlp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ImdbDatasetHandler {
	
	public static void main(String[] args) {
		String corpusFullPath = "./corpus/raw_corpus/imdb-dataset-of-50k-movie-reviews.csv";
		String trainSetFullPath = "./corpus/LRClassifier_train.txt";
		String testSetFullPath = "./corpus/LRClassifier_test.txt";
		
		try {
			File rawCorpus = new File(corpusFullPath);
			File trainSet = new File(trainSetFullPath);
			File testSet = new File(testSetFullPath);
			
			BufferedReader reader = new BufferedReader(new FileReader(rawCorpus));
			FileWriter trainWriter = new FileWriter(trainSet);
			FileWriter testWriter = new FileWriter(testSet);
			
			String aLine = null;
			Pattern uttPattern = Pattern.compile("^\".*\",");
			Pattern catPattern = Pattern.compile("(positive|negative)$");
			String utt = null;
			String cat = null;
			int count = 0;
			while((aLine=reader.readLine())!= null) {
				if(aLine.equals("review,sentiment")) continue;
				Matcher uttMatcher = uttPattern.matcher(aLine);
				Matcher catMatcher = catPattern.matcher(aLine);
				if(uttMatcher.find() && catMatcher.find()) {
					count ++;
					utt = uttMatcher.group();
					cat = catMatcher.group();
					utt = utt.substring(1, utt.length()-2);
					cat = cat.equals("positive") ? "+" : "-";
					if(count%10 == 0) {
						testWriter.write(cat + "\t" + utt + "\r\n");
					}else {
						trainWriter.write(cat + "\t" + utt + "\r\n");
					}
				}
			}
			reader.close();
			trainWriter.close();
			testWriter.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
