package pers.weihengsun.nlp.features;

/**
 * A <code>Features</code> object is the numeric representation of one nlp input case.
 * 
 * <p>A <code>Features</code> object can and only can generated by an instance of <code>AbstractFeatureExtractor</code>
 *
 * @author weiheng.sun
 *
 */
public class Features<E extends Number> {
	
	private int dimension;
	
	private E[] values;
	
	public Features(E[] values){
		this.dimension = values.length;
		this.values = values;
	}

	public int getDimension() {
		return dimension;
	}

	public E[] getValues() {
		return values;
	}
	
	@Override
	public String toString() {
		String res = "dimension="+dimension+";\t"+"valuese:[";
		for(E curVal : values) {
			res += curVal + ", ";
		}
		res += "]";
		return res;
	}
}
