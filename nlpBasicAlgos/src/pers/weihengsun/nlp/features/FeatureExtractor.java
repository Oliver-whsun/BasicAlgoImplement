package pers.weihengsun.nlp.features;


/**
 * <code>FeatureExtractor</code> is the abstract superclass for all future <code>FeatureExtractor</code>
 * 
 * <p>Any <code>FeatureExtractor</code> must implement all variables and abstract methods defined in <code>AbstractFeatureExtractor</code>
 * 
 * @author weiheng.sun
 *
 */
public abstract class FeatureExtractor {
	
	private int featureDimension;
	
	public FeatureExtractor(int featureDimension) {
		this.featureDimension = featureDimension;
	}
	
	public abstract Features<Double> extractFeatures(String[] input);
	
	public int getDimension() {
		return this.featureDimension;
	}
}
