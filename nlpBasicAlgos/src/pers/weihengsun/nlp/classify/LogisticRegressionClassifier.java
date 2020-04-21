package pers.weihengsun.nlp.classify;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;


import pers.weihengsun.nlp.features.FeatureExtractor;
import pers.weihengsun.nlp.features.Features;
import pers.weihengsun.nlp.statistics.LogisticRegression;

public class LogisticRegressionClassifier {
	// A LR Classifier have to use an inner featureExtractor to parse input text to numerical features
	private FeatureExtractor featureExtractor = null;
	
	// A LR Classifier have an inner LogisticRegression to record weights and bias, do gradient descent etc.
	private LogisticRegression lr = null;
	
	public LogisticRegressionClassifier(FeatureExtractor featureExtractor){
		this.featureExtractor = featureExtractor;
		this.lr = new LogisticRegression(featureExtractor.getDimension());
	}
	
	/**
	 * Train every case in corpus once, one by one.
	 * Training finish when every case is trained.
	 * !IMPORTANT! This method may lead to LR model over-fitting or under-fitting. 
	 * It all depends on training data size and learning rate.
	 * @param corpus
	 * @throws IllegalArgumentException
	 */
	public void sgdTrain(Map<Double, List<String[]>> corpus) { 
//		for(Double curCat : corpus.keySet()) {
//			if(curCat.doubleValue() != 1.0 && curCat.doubleValue() != 0.0) {
//				String msg = "ERROR! The gold value for Logistic Regression is either 1.0 or 0.0 \r\n";
//				msg += "Find value: " + curCat.doubleValue();
//				throw new IllegalArgumentException(msg);
//			}
//			for(String[] aCase : corpus.get(curCat)) {
//				Features<Double> caseFeatures = featureExtractor.extractFeatures(aCase);
//				lr.stochasticGradientDescent(caseFeatures.getValues(), curCat);
//			}
//		}
		ListIterator<String[]> positiveCaseIter = corpus.get(1.0).listIterator();
		ListIterator<String[]> negativeCaseIter = corpus.get(0.0).listIterator();
		while(positiveCaseIter.hasNext() && negativeCaseIter.hasNext()) {
			if(positiveCaseIter.hasNext()) {
				String[] aPosCase = positiveCaseIter.next();
				Features<Double> caseFeatures = featureExtractor.extractFeatures(aPosCase);
				lr.stochasticGradientDescent(caseFeatures.getValues(), 1.0);
			}
			if(negativeCaseIter.hasNext()) {
				String[] aNegCase = negativeCaseIter.next();
				Features<Double> caseFeatures = featureExtractor.extractFeatures(aNegCase);
				lr.stochasticGradientDescent(caseFeatures.getValues(), 0.0);
			}
		}
	}
	
	/**
	 * This method returns the LR output z directly, which belongs to (0,1)
	 * @param inputCase
	 * @return z = sigmoid(w dotProduct x + bias)
	 */
	public double classifyToValue(String[] inputCase) {
		Features<Double> caseFeatures = featureExtractor.extractFeatures(inputCase);
		return lr.calOutput(caseFeatures.getValues());
	}
	
	/**
	 * 
	 * @param inputCase
	 * @return true if input is predicted as positive category, false as negetive
	 */
	public boolean classifyToBool(String[] inputCase) {
		double z = classifyToValue(inputCase);
		if(z > 0.5) return true;
		else return false;
	}
	
	@Override
	public String toString() {
		String res = "Feature Extractor info: " + featureExtractor.getClass() + "\r\n";
		res += lr.toString();
		return res;
	}
	
}
