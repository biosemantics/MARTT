package jds.collection;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import jds.FindMin;
import jds.SortAlgorithm;
import jds.Indexed;
import java.util.Comparator;
import jds.util.BinaryNode;

/**
 * SkewHeap - priority queue implemented using skew heap algorithms;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see java.util.Enumeration
 * @see java.io.Serializable
 */

public class SkewHeap implements FindMin, SortAlgorithm {

		// constructor
	/**
	 * initialize newly created heap
	 *
	 * @param t comparator to be used to order values
	 */
	public SkewHeap (Comparator t) { test = t; }
	private Comparator test;
	
		// Collection interface
	/**
	 * Determines whether the collection is empty
	 *
	 * @return true if the collection is empty
	 */
	public boolean isEmpty () { return root == null; }

	/**
	 * Yields enumerator for collection
	 *
	 * @return an <code>Enumeration</code> that will yield the elements of the collection
	 * @see java.util.Enumeration
	 */
	public Enumeration elements () { return null; } // do later

	/**
	 * Determines number of elements in collection
	 *
	 * @return number of elements in collection as integer
	 */
	public int size () { return 0; } // do later


	/**
	 * add a new value to the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public void addElement (Object val) 
		{ root = merge(root, new BinaryNode(val)); }

	/**
	 * yields the smallest element in collection
	 *
	 * @return the first (smallest) value in collection
	 */
	public Object getFirst () { 
		if (root == null)
			throw new NoSuchElementException();
		return root.value;
	}

	/**
	 * removes the smallest element in collection
	 *
	 */
	public void removeFirst () {
		if (root == null)
			throw new NoSuchElementException();
		root = merge(root.leftChild, root.rightChild);
	}

		// merge method
	/**
	 * merge this heap with another
	 *
	 * @param right heap to be combined with current heap
	 */
	public void mergeWith (SkewHeap right) {
		root = merge (root, right.root);
		right.root = null;
	}

		// sortAlgorithm interface
	/**
	 * rearrange collection into asending order
	 *
	 * @param data the values to be ordered
	 */
	public void sort (Indexed data) {
		// first put into heap
		int max = data.size();
		for (int i = 0; i < max; i++)
			addElement(data.elementAt(i));
		// then pull out
		for (int i = 0; i < max; i++) {
			data.setElementAt(getFirst(), i);
			removeFirst();
		}
	}

		// internal data fields

	private BinaryNode root = null;

	private BinaryNode merge (BinaryNode left, BinaryNode right) {
		if (left == null) return right;
		if (right == null) return left;

		Object leftValue = left.value;
		Object rightValue = right.value;

		if (test.compare(leftValue, rightValue) < 0) {
			BinaryNode swap = left.leftChild;
			left.leftChild = merge(left.rightChild, right);
			left.rightChild = swap;
			return left;
		} else {
			BinaryNode swap = right.rightChild;
			right.rightChild = merge(right.leftChild, left);
			right.leftChild = swap;
			return right;
		}
	}
}
