package jds.sort;

import java.util.Enumeration;
import jds.FindNth;
import jds.SortAlgorithm;
import java.util.Comparator;
import jds.Indexed;

/**
 * Partition - algorithms involving partitioning a vector into groups;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 */

public class Partition implements FindNth, SortAlgorithm {
	/**
	 * the collection that will hold the data values
	 */
	protected Indexed elementData;

	/**
	 * the Comparator object used to place elements in order
	 */
	protected Comparator test;

	/**
	 * initialize a new Parition object
	 *
	 * @param c the Comparator used to place values in order
	 */
	public Partition (Comparator c) { test =c ; }

	/**
	 * initialize a new Parition object
	 *
	 * @param v an Indexed object of initial values
	 * @param c the Comparator used to place values in order
	 */
	public Partition (Indexed v, Comparator c) 
		{ test = c; elementData = v; }

		// the FindNth interface
	/**
	 * find nth smallest value in collection
	 *
	 * @param	index of value, from 0 to <code>(size-1)</code>
	 * @return	value of element
	 * @exception	java.util.NoSuchElementException index is illegal
	 */
	public Object findNth (int n) 
		{ return findNth (n, 0, elementData.size()); }

	private Object findNth (int n, int low, int high) {
		int pivotIndex = pivot(low, high, (high + low)/2);
		if (pivotIndex == n)
			return elementData.elementAt(n);
		if (n < pivotIndex)
			return findNth(n, low, pivotIndex);
		return findNth(n, pivotIndex+1, high);
	}

		// the QuickSort algorithm
	/**
	 * rearrange collection into asending order
	 *
	 * @param data the values to be ordered
	 */
	public void sort (Indexed data) {
		elementData = data;
		quickSort(0, data.size());
	}

	private void quickSort(int low, int high) {
		if (low >= high) return;
		int pivotIndex = (low + high) / 2;
		pivotIndex = pivot(low, high, pivotIndex);
		quickSort(low, pivotIndex);
		quickSort(pivotIndex + 1, high);
	}

		// the pivot algorithm
	private void swap (int i, int j) {
		if (i == j) return;
		Object temp = elementData.elementAt(i);
		elementData.setElementAt(elementData.elementAt(j), i);
		elementData.setElementAt(temp, j);
	}

	private int pivot (int start, int stop, int position) {
		// swap pivot into start position
		swap(start, position);

			// partition index values
		int low = start + 1;
		int high = stop;
		while (low < high) {
			if (test.compare(elementData.elementAt(low),
				elementData.elementAt(start)) < 0)
					low++;
			else if (test.compare(elementData.elementAt(--high),
				elementData.elementAt(start)) < 0) 
					swap(low, high);
		}
			// swap pivot back into place
		swap(start, --low);
		return low;
	}
}

