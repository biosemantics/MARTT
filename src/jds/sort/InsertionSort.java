package jds.sort;

import java.util.Enumeration;
import jds.SortAlgorithm;
import java.util.Comparator;
import jds.Indexed;

/**
 * InsertionSort - implementation of the insertion sort algorithm;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see jds.Indexed
 */

public class InsertionSort implements SortAlgorithm {

	/**
	 * initialize the Bubble Sort algorithm
	 *
	 * @param t the Comparator used to place values in sequence
	 */
	public InsertionSort (Comparator t) { test = t; }

	private Comparator test;

	/**
	 * rearrange collection into asending order
	 *
	 * @param data the values to be ordered
	 */
	public void sort (Indexed v) {
		int n = v.size();
		for (int i = 1; i < n; i++) {
			Object element = v.elementAt(i);
			int j = i - 1;
			while (j >= 0 && 
			  (test.compare(element,v.elementAt(j)) < 0)) {
				Object val = v.elementAt(j);
				v.setElementAt(val, j+1);
				j = j - 1;
			}
		v.setElementAt(element, j+1);
		}
	}
}

