package pers.weihengsun.nlp.features;


/**
 * <code>AbstractFeatureExtractor</code> is the abstract superclass for all future <code>FeatureExtractor</code>
 * 
 * <p>Any <code>FeatureExtractor</code> must implement all variables and abstract methods defined in <code>AbstractFeatureExtractor</code>
 * 
 * @author weiheng.sun
 *
 */
public abstract class AbstractFeatureExtractor {
	
	public int featureDimension;
	
	public AbstractFeatureExtractor(int featureDimension) {
		this.featureDimension = featureDimension;
	}
	
	public abstract Features<? extends Number> extractFeatures(String[] input);
}
