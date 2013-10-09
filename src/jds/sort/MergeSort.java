package jds.sort;

import java.util.Enumeration;
import jds.SortAlgorithm;
import jds.Indexed;
import java.util.Comparator;
import jds.collection.Vector;

/**
 * MergeSort - implementation of the merge sort algorithm;
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

public class MergeSort implements SortAlgorithm {

	/**
	 * initialize the Merge Sort algorithm
	 *
	 * @param t the Comparator used to place values in sequence
	 */
	public MergeSort (Comparator t) { test = t; }

	private Comparator test;
	private Indexed data;
	private Vector stk = new Vector();

	/**
	 * rearrange collection into asending order
	 *
	 * @param data the values to be ordered
	 */
	public void sort (Indexed v) {
		data = v;
		stk.ensureCapacity(v.size());
		sort(0, v.size());
	}

	private void sort (int low, int high) {
		if (low+1 >= high) return;
		int mid = (low + high) / 2;
		sort(low, mid); sort(mid, high);
		merge(low, mid, high);
	}

	private void merge (int low, int mid, int high) {
		stk.setSize(0);
		int i = low;
		int j = mid;
		while ((i < mid) || (j < high)) 
			if (i < mid)
				if ((j < high) && 
					(test.compare(data.elementAt(j),
						data.elementAt(i)) < 0))
					stk.addLast(data.elementAt(j++));
				else
					stk.addLast(data.elementAt(i++));
			else
				stk.addLast(data.elementAt(j++));
		j = stk.size();
		for (i = 0; i < j; i++)
			data.setElementAt(stk.elementAt(i), low+i);
	}

}

