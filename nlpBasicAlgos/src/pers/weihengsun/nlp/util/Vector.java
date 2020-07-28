package pers.weihengsun.nlp.util;

public abstract class Vector<E extends Number> {
	
	private int dimension;
	
	public int getDimension(){
		return dimension;
	}
	
	abstract public double norm();
	
	abstract public double innerProduct(Vector<E> v);
	
	public double cosine(Vector<E> v) {
		double res = innerProduct(v) / this.norm() / v.norm();
		return res;
	}
}
