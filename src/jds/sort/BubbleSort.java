package jds.sort;

import java.util.Enumeration;
import jds.SortAlgorithm;
import java.util.Comparator;
import jds.Indexed;

/**
 * BubbleSort - implementation of the bubble sort algorithm;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see jds.Indexed
 * @see java.util.Comparator
 */

public class BubbleSort implements SortAlgorithm {

	/**
	 * initialize the Bubble Sort algorithm
	 *
	 * @param t the Comparator used to place values in sequence
	 */
	public BubbleSort (Comparator t) { test = t; }
	private Comparator test;

	/**
	 * rearrange collection into asending order
	 *
	 * @param data the values to be ordered
	 */
	public void sort (Indexed v) {
		int n = v.size();
			// find the largest remaining value
			// and place into v[i]
		for (int i = n - 1; i > 0; i--) {

				// move large values to the top
			for (int j = 0; j < i; j++) {

					// if out of order
				if (test.compare(v.elementAt(j),
					v.elementAt(j+1)) > 0) {
						// then swap
					Object temp = v.elementAt(j);
					v.setElementAt(v.elementAt(j+1), j);
					v.setElementAt(temp, j+1);
				}
			}
		}

	}
}

