package jds.collection;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import jds.Sorted;
import jds.Bag;
import jds.FindMin;
import jds.FindNth;
import jds.Indexed;
import jds.SortAlgorithm;
import java.util.Comparator;
import jds.sort.Partition;
import jds.collection.Vector;

/**
 * SortedVector - an indexed collection that keeps values ordered;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see jds.Sorted
 * @see jds.Bag
 * @see jds.FindMin
 * @see jds.FindNth
 * @see jds.SortAlgorithm
 */

public class SortedVector implements Sorted, Bag, FindMin, FindNth, SortAlgorithm   {
	protected Indexed elementData;
	protected Comparator test;

	/**
	 * initialize new sorted vector
	 *
	 * @paran v vector containing initial data
	 * @param c comparator object used to place values in order
	 */
	public SortedVector (Indexed v, Comparator c) 
		{ test = c; sort(v); elementData = v; }

	/**
	 * initialize new sorted vector
	 *
	 * @param c comparator object used to place values in order
	 */
	public SortedVector (Comparator c) 
		{ elementData = new Vector(); test = c; }

		// collection interface
	/**
	 * Yields enumerator for collection
	 *
	 * @return an <code>Enumeration</code> that will yield the elements of the collection
	 * @see java.util.Enumeration
	 */
	public Enumeration elements () { return elementData.elements(); }

	/**
	 * Determines whether the collection is empty
	 *
	 * @return true if the collection is empty
	 */
	public boolean isEmpty () { return elementData.isEmpty(); }

	/**
	 * Determines number of elements in collection
	 *
	 * @return number of elements in collection as integer
	 */
	public int size () { return elementData.size(); }

		// FindMin interface
	/**
	 * add a new value to the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public void addElement (Object value) 
		{ elementData.addElementAt(value, binarySearch(value)); }

	/**
	 * yields the smallest element in collection
	 *
	 * @return the first (smallest) value in collection
	 */
	public Object getFirst () { return elementData.elementAt(0); }

	/**
	 * removes the smallest element in collection
	 *
	 */
	public void removeFirst () { elementData.removeElementAt(0); }

	/**
	 * find nth smallest value in collection
	 *
	 * @param	index of value, from 0 to <code>(size-1)</code>
	 * @return	value of element
	 * @exception	java.util.NoSuchElementException index is illegal
	 */
	public Object findNth (int n) { return elementData.elementAt(n); }


	/**
	 * see if collection contains value
	 *
	 * @param value element to be tested
	 * @return true if collection contains value
	 */
	public boolean containsElement (Object value) { 
		int index = binarySearch(value);
		return index < elementData.size() && elementData.elementAt(index).equals(value);
		}


	/**
	 * find element that will test equal to value
	 *
	 * @param value element to be tested
	 * @return first value that is <code>equals</code> to argument
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public Object findElement (Object obj) {
		int index = binarySearch(obj);
		if (index < elementData.size() && 
			elementData.elementAt(index).equals(obj)) 
				return elementData.elementAt(index);
		else throw new NoSuchElementException(String.valueOf(obj));
	}

	/**
	 * remove a new value from the collection
	 *
	 * @param value element to be removed from collection
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public void removeElement (Object obj) { 
		int index = binarySearch(obj);
		if (index < elementData.size() && elementData.elementAt(index).equals(obj)) {
			elementData.removeElementAt(index);
			}
		else throw new NoSuchElementException(String.valueOf(obj));
		}


		// sortAlgorithm method
	/**
	 * rearrange collection into asending order
	 *
	 * @param data the values to be ordered
	 */
	public void sort (Indexed vec) {
		SortAlgorithm alg = new Partition(test);
		alg.sort(vec);
	}

		// internal method
	/**
	 * find index location for element
	 *
	 * @param obj the value to be tested
	 * @return the index of first location containing value smaller than argument
	 */
	protected int binarySearch (Object obj) {
		int low = 0;
		int high = elementData.size();
		while (low < high) {
			int mid = (low + high)/2;
			if (test.compare(elementData.elementAt(mid), obj) < 0)
				low = mid + 1;
			else
				high = mid;
			}
		return low;
		}
}
