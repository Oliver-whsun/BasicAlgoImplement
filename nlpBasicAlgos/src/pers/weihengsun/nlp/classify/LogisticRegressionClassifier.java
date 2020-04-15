package pers.weihengsun.nlp.classify;

import pers.weihengsun.nlp.features.AbstractFeatureExtractor;
import pers.weihengsun.nlp.statistics.LogisticRegression;

public class LogisticRegressionClassifier {
	// A LR Classifier have to use an inner featureExtractor to parse input text to numerical features
	private AbstractFeatureExtractor featureExtractor = null;
	
	// A LR Classifier have an inner LogisticRegression to record weights and bias, do gradient descent etc.
	private LogisticRegression lr = null;
	
	
}
