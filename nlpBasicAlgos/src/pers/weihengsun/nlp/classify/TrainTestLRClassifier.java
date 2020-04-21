package pers.weihengsun.nlp.classify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pers.weihengsun.nlp.features.FeatureExtractor;
import pers.weihengsun.nlp.features.Stanford511FeatureExtractor;



public class TrainTestLRClassifier {
	public static String filePath = "./corpus/";
	public static String trainFileName = "LRClassifier_train.txt";
	public static String testFileName = "LRClassifier_test.txt";
	public static String resultFileName = "LRClassifier_result.txt";
	
	private Map<Double, List<String[]>> loadTrainTestFile(String fileFullPath){
		Map<Double, List<String[]>> corpus = new HashMap<Double, List<String[]>>();
		corpus.put(Double.valueOf(1.0), new LinkedList<String[]>());
		corpus.put(Double.valueOf(0.0), new LinkedList<String[]>());
		BufferedReader br = null;
		try {
			File file = new File(fileFullPath);
			br = new BufferedReader(new FileReader(file));
			String curLine = null;
			while((curLine = br.readLine()) != null) {
				if(curLine.isBlank()) continue;
				String[] tagAndUtter = curLine.split("\t");
				if(tagAndUtter.length < 2) {
					String msg = "WARNING! File " + fileFullPath + " wrong format.";
					msg += "\r\n" + "Use tag to split category and utterance.";
					System.out.println(msg);
					continue;
				}
				String tag = tagAndUtter[0];
				String utt = tagAndUtter[1];
				String[] uttArr = utt.split(" ");
				switch (tag) {
				case "+":
					corpus.get(Double.valueOf(1.0)).add(uttArr);
					break;
				case "-":
					corpus.get(Double.valueOf(0.0)).add(uttArr);
					break;
				default:
					String msg = "WARNING! File " + fileFullPath + " wrong format.";
					msg += "\r\n" + "Use \"+\" to represent positive categpery, \"-\" for negative.";
					System.out.println(msg);
					continue;
				}
			}
			br.close();
		}catch (FileNotFoundException e) {
			System.out.println(e.toString()+" Full Path= "+fileFullPath);
		}catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println("Loaded File from "+fileFullPath);
		return corpus;
	}
	
	private String printCase(String[] aCase) {
		String res = "";
		for(String curWord : aCase) res += curWord + " ";
		return res;
	}
	
	private String getCurrentDateStr() {
		Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        return sdf.format(date);
	}
	
	public LogisticRegressionClassifier trainLRClassifier(
				FeatureExtractor featureExtractor, String trainFileFullPath) {
		System.out.println("Start to do training.");
		LogisticRegressionClassifier lrc = new LogisticRegressionClassifier(featureExtractor);
		Map<Double, List<String[]>> trainCorpus = loadTrainTestFile(trainFileFullPath);
		lrc.sgdTrain(trainCorpus);
		return lrc;
	}
	
	public void testLRClassifier(LogisticRegressionClassifier lrc, String testFileFullPath, String resultFileFullPath) {
		System.out.println("Start to do testing.");
		Map<Double, List<String[]>> testCorpus = loadTrainTestFile(testFileFullPath);
		int numTestCase = 0;
		int numWrong = 0;
		try {
			File resultFile = new File(resultFileFullPath);
			FileWriter writer = new FileWriter(resultFile);
			writer.write("Gold Category"+"\t"+"Preticted"+"\t"+"Utterance"+"\r\n");
			for(String[] aPosCase : testCorpus.get(1.0)) {
				boolean res = lrc.classifyToBool(aPosCase);
				if(res == true) numTestCase++;
				else {
					numTestCase++;
					numWrong++;
					String log = "+" + "\t" + "-" + "\t" + printCase(aPosCase) + "\r\n";
					writer.write(log);
				}
			}
			for(String[] aNegCase : testCorpus.get(0.0)) {
				boolean res = lrc.classifyToBool(aNegCase);
				if(res == false) numTestCase++;
				else {
					numTestCase++;
					numWrong++;
					String log = "-" + "\t" + "+" + "\t" + printCase(aNegCase) + "\r\n";
					writer.write(log);
				}
			}
			String summary = "==== "+"Test Number: " + numTestCase + " Wrong Number: " + numWrong;
			summary += " Test Date: " + getCurrentDateStr();
			writer.write(summary);
			writer.close();
			System.out.println(summary);
			System.out.println("Detailed results are written to "+resultFileFullPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) {
		TrainTestLRClassifier trainTest = new TrainTestLRClassifier();
		LogisticRegressionClassifier lrc = trainTest.trainLRClassifier(
				new Stanford511FeatureExtractor(), 
				filePath+trainFileName);
		System.out.println(lrc);
		trainTest.testLRClassifier(lrc, filePath+testFileName, filePath+resultFileName);
	}
}
