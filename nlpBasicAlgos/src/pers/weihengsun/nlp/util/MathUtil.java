package pers.weihengsun.nlp.util;

import java.util.Iterator;
import java.util.Vector;

public class MathUtil {
	
	public static double innerProduct(Vector<? extends Number> v1, Vector<? extends Number> v2) {
		if(v1==null || v2==null) {
			String msg = "null Vector found!";
			throw new IllegalArgumentException(msg);
		}
		if(v1.size() != v2.size()) {
			String msg = "unmatched Vector dimension: " + v1.size() + "vs. " + v2.size();
			throw new IllegalArgumentException(msg);
		}
		double res = 0.0;
		Iterator<? extends Number> v1Iter = v1.iterator();
		Iterator<? extends Number> v2Iter = v2.iterator();
		while(v1Iter.hasNext()) {
			double v1Component = v1Iter.next().doubleValue();
			double v2Component = v2Iter.next().doubleValue();
			res += v1Component * v2Component;
		}
		return res;
	}
	
	public static double innerProduct(int[] v1, int[] v2) {
		if(v1==null || v2==null) {
			String msg = "null Vector found!";
			throw new IllegalArgumentException(msg);
		}
		if(v1.length != v2.length) {
			String msg = "unmatched Vector dimension: " + v1.length + "vs. " + v2.length;
			throw new IllegalArgumentException(msg);
		}
		double res = 0.0;
		for(int i=0; i<v1.length; i++) {
			res += v1[i] * v2[i];
		}
		return res;
	}
	
	public static double normOfVector(Vector<? extends Number> v) {
		if(v == null) {
			String msg = "null Vector found!";
			throw new IllegalArgumentException(msg);
		}
		double sum = 0.0;
		Iterator<? extends Number> valueIter = v.iterator();
		while(valueIter.hasNext()) {
			double component = valueIter.next().doubleValue();
			sum += component * component;
		}
		double res = Math.sqrt(sum);
		return res;
	}
	
	public static double normOfVector(int[] v) {
		if(v == null) {
			String msg = "null Vector found!";
			throw new IllegalArgumentException(msg);
		}
		double sum = 0.0;
		for(int component : v) {
			sum += component * component;
		}
		double res = Math.sqrt(sum);
		return res;
	}
	
	public static double cosine(Vector<? extends Number> v1, Vector<? extends Number> v2) {
		double res = innerProduct(v1, v2) / normOfVector(v1) / normOfVector(v2);
		return res;
	}
	
	public static double cosine(int[] v1, int[]v2) {
		double res = innerProduct(v1, v2) / normOfVector(v1) / normOfVector(v2);
		return res;
	}
	
	public static void main(String[] args) {
		int[] v1 = new int[] {1,1};
		int[] v2 = new int[] {0,1};
		System.out.print(cosine(v1, v2));
	}
}
