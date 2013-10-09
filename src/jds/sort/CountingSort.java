package jds.sort;

/**
 * CountingSort - implementation of the counting sort algorithm;
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

public class CountingSort {

	/**
	 * sort an array of integer values
	 *
	 * @param data array of positive integer values
	 * @param m maximum value in data array
	 */
	public void sort (int [ ] data, int m) {
	// sort the array of values, each element no larger than m
		int [ ] counts = new int[m];

			// count the occurrences of each value
		for (int i = 0; i < data.length; i++)
			counts[data[i]]++;

			// now put values back into the array
		int i = 0;
		for (int j = 0; j < m; j++)
			for (int k = counts[j]; k > 0; k--)
				data[i++] = j;
	}
}

