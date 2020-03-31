package pers.weihengsun.nlp.classify;


import java.util.Arrays;

public class TrainTestNaiveBayesClassifier {
	
	public static void main(String[] args) {
		NaiveBayesClassifier nbClassifier = new NaiveBayesClassifier();
		
		String[] negTrainCase1 = "just plain boring".split(" ");
		String[] negTrainCase2 = "entirely predictable and lacks energy".split(" ");
		String[] negTrainCase3 = "no surprises and very few laughs".split(" ");
		String[] posTrainCase1 = "very powerful".split(" ");
		String[] posTrainCase2 = "the most fun film of the summer".split(" ");
		String[] testCase = "predictable with no fun".split(" ");
		
		nbClassifier.trainACase(negTrainCase1, "neg");
		nbClassifier.trainACase(negTrainCase2, "neg");
		nbClassifier.trainACase(negTrainCase3, "neg");
		nbClassifier.trainACase(posTrainCase1, "pos");
		nbClassifier.trainACase(posTrainCase2, "pos");
		
		String category = nbClassifier.classify(testCase);
		System.out.println("The category of Utterance \""+Arrays.toString(testCase)+"\" is "+category);
	}
}
