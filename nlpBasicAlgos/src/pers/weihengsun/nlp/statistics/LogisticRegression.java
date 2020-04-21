package pers.weihengsun.nlp.statistics;

import java.util.Arrays;

public class LogisticRegression {
	

	private double[] weights;
	private double bias;
	private double learningRate = 0.0001;
	
	/**
	 * Constructor when given a dimension, set weights and bias to 0.0 
	 * @param dimension
	 */
	public LogisticRegression(int dimension){
		weights = new double[dimension];
		bias = 0.0;
	}
	/**
	 * Constructor when given the values of weights and bias in one array
	 */
	public LogisticRegression(double[] weightsAndBias){
		weights = Arrays.copyOfRange(weightsAndBias, 0, weightsAndBias.length-1);
		bias = weightsAndBias[weightsAndBias.length-1];
	}
	/**
	 * Constructor when given the values of weights and bias separated
	 */
	public LogisticRegression(double[] weights, double bias){
		this.weights = weights.clone();
		this.bias = bias;
	}
	
	/**
	 * Calculate output y where y=sigmoid(z), z = w dotProduct x + b
	 * @param input
	 * @return
	 */
	public double calOutput(Number[] input) {
		validateDimension(input);
		double y;
		double z = 0.0;
		for(int i=0; i<weights.length; i++) {
			z += weights[i] * (double)input[i];
		}
		z += bias;
		y = sigmoid(z);
		return y;
	}
	
	public double calCrossEntropy(double goldValue, Number[] input) {
		double predictedValue = calOutput(input);
		return calCrossEntropy(goldValue, predictedValue);
	}
	
	public double calCrossEntropy(double goldValue, double predictedValue) {
		double crossEntropy = -(goldValue*Math.log(predictedValue) + (1-goldValue)*Math.log(1-goldValue));
		return crossEntropy;
	}
	
	/**
	 * Stochastic Gradient Descent is one way to update weights and bias given a training case.
	 * Corresponding to SGD(Stochastic Gradient Descent) is the batch training and mini_batch training, which update weights and bias using many training cases
	 * @param aCaseToLearn
	 * @param goldValue
	 */
	public void stochasticGradientDescent(Number[] aCaseToLearn, double goldValue) {
		double predictedValue = calOutput(aCaseToLearn);
		//update every component in weight
		for(int i=0; i<aCaseToLearn.length; i++) {
			double partialDerivative = (predictedValue - goldValue) * (double)aCaseToLearn[i];
			weights[i] -= learningRate * partialDerivative;
		}
		//update bias
		double partialDerivative = predictedValue - goldValue;
		bias -= learningRate * partialDerivative;
	}
	
	@Override
	public String toString() {
		String res = "LogisticRegression instance information:\r\n";
		res += "dimension = " + weights.length + "\r\n";
		res += "weights = [";
		for(double weight_i : weights)
			res += weight_i + ", ";
		res += "]\r\n";
		res += "bias = " + bias + "\r\n";
		res += "learning rate = " + learningRate;
		res += "\r\n";
		return res;
	}
	
	
	//################### private functions below ###################
	
	private void validateDimension(Number[] input) {
		if(input.length != this.weights.length)
			throw new IllegalArgumentException("LogisticRegression weights dimension is "+weights.length+"; while input dimension is "+input.length+"!/r/n");
		else return;
	}
	
	private double sigmoid(double z) {
		return 1/(1 + Math.exp(-z));
	}
	
	private double calL1Regularization() {
		double l1 = 0.0;
		for(double weight_i : weights)
			l1 += Math.abs(weight_i);
		return l1;
	}
	
	private double calL2Regularization() {
		double l2 = 0.0;
		for(double weight_i : weights)
			l2 += Math.pow(weight_i,2);
		return l2;
	}
	
	//############## main function blow #########
	public static void main(String[] args) {
		Number[] aTrainCase = new Double[]{3.0, 2.0};
		LogisticRegression lr = new LogisticRegression(2);
		lr.stochasticGradientDescent(aTrainCase, 1.0);
		System.out.println(lr);
	}
}


