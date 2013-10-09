package jds.collection;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import jds.Bag;
import jds.Sorted;
import jds.FindMin;
import jds.SortAlgorithm;
import jds.Indexed;
import jds.util.BinaryNode;
import java.util.Comparator;
import jds.util.DefaultComparator;
import jds.util.InorderTreeTraversal;


/**
 * AVLTree - set kept in a balanced binary search tree;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see jds.Collection
 */

public class AVLTree implements Bag, Sorted, FindMin, SortAlgorithm {
	/**
	 * initialize newly created AVL tree
	 *
	 * @param t comparator object used to order elements
	 */
	public AVLTree (Comparator t) { test = t; }

	/**
	 * initialize newly created AVL tree
	 *
	 */
	public AVLTree () { test = new DefaultComparator(); }

	private Comparator test;
	private AVLNode root = new AVLSentinel();
	private int elementCount;
	
	/**
	 * Determines whether the collection is empty
	 *
	 * @return true if the collection is empty
	 */
	public boolean isEmpty () { return root.isEmpty(); }

	/**
	 * Determines number of elements in collection
	 *
	 * @return number of elements in collection as integer
	 */
	public int size () { return elementCount; }

	/**
	 * Yields enumerator for collection
	 *
	 * @return an <code>Enumeration</code> that will yield the elements of the collection
	 * @see java.util.Enumeration
	 */
	public Enumeration elements () { return new InorderTreeTraversal(root); }


	/**
	 * see if collection contains value
	 *
	 * @param val element to be tested
	 * @return true if collection contains value
	 */
	public boolean containsElement (Object val) { return root.contains(val); }

	/**
	 * find element that will test equal to value
	 *
	 * @param value element to be tested
	 * @return first value that is <code>equals</code> to argument
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public Object findElement (Object val) { return root.find(val); }

	/**
	 * add a new value to the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public synchronized void addElement (Object val) 
		{ root = root.add(val); elementCount++; }

	/**
	 * remove a new value from the collection
	 *
	 * @param value element to be removed from collection
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public synchronized void removeElement (Object val) 
		{ root = root.remove(val); elementCount--; }

		// the FindMin interface
	/**
	 * return the smallest element from the collection
	 */
	public Object getFirst () { return root.getFirst(); }

	/**
	 * remove the smallest element from the collection
	 */
	public void removeFirst () 
		{ root = root.removeFirst(); elementCount--; }

		// the SortAlgorithm interface
	/**
	 * rearrange collection into asending order
	 *
	 * @param data the values to be ordered
	 */
	public void sort (Indexed data) {
		AVLTree t = new AVLTree(test);
		int n = data.size();
				// copy them in
		for (int i = 0; i < n; i++)
			t.addElement(data.elementAt(i));
				// copy them out
		Enumeration e = t.elements();
		int i = 0;
		while (e.hasMoreElements())
			data.setElementAt(e.nextElement(), i++);
	}

	private class AVLNode extends BinaryNode {
		AVLNode (Object v) { super(v); }
		protected int height = 0;

		private AVLNode left() { return (AVLNode) leftChild; }
		private AVLNode right() { return (AVLNode) rightChild; }

		public AVLNode add (Object newElement) {
			if (test.compare(newElement, value) < 0)
				leftChild = left().add(newElement);
			else
				rightChild = right().add(newElement);
			return setHeight();
		}

		public boolean contains (Object newElement) {
			int testResult = test.compare(newElement, value);
			if (testResult == 0) return true;
			if (testResult < 0)
				return left().contains(newElement);
			return right().contains(newElement);
		}

		public Object find (Object newElement) {
			int testResult = test.compare(newElement, value);
			if (testResult == 0) return value;
			if (testResult < 0)
				return left().find(newElement);
			return right().find(newElement);
		}

		public Object getFirst () {
			if (leftChild.isEmpty()) return this;
			return left().getFirst();
		}

		public AVLNode removeFirst () {
			if (leftChild.isEmpty()) return right();
			leftChild = left().removeFirst();
			return setHeight();
		}

		public AVLNode remove (Object oldElement) {
			int testResult = test.compare(oldElement, value);
			if (testResult == 0) { // found element to remove
				if (right().isEmpty())
					return left();
				value = right().getFirst();
				rightChild = right().removeFirst();
			} else if (testResult < 0)
				leftChild = left().remove(oldElement);
			else
				rightChild = right().remove(oldElement);
			return setHeight();
		}

		private int bf () { return right().height - left().height; }

		private AVLNode balance () {
			if (bf() < 0) {
				if (left().bf() > 0) // dbl rotatin
					leftChild = left().rotateLeft();
				return rotateRight(); // single rotation
			} 
			if (right().bf() < 0)
				rightChild = right().rotateRight();
			return rotateLeft();
		}

		private AVLNode rotateLeft() {
			AVLNode newTop = right();
			rightChild = newTop.leftChild;
			newTop.leftChild = setHeight();
			return newTop.setHeight();
		}

		private AVLNode rotateRight() {
			AVLNode newTop = left();
			leftChild = newTop.rightChild;
			newTop.rightChild = setHeight();
			return newTop.setHeight();
		}

		private AVLNode setHeight() {
			int lh = left().height;
			int rh = right().height;
			height = 1 + ((lh < rh)?rh:lh);
			int nbf = rh - lh; // balance factor
			if ((nbf < -1) || (nbf > 1))
				return balance();
			return this;
		}


		public String toString() {
			return "(" + leftChild.toString() + " " +
				value.toString() + ":" + height + " " +
				rightChild.toString() + ")";
		}
	}

	public String toString() { return root.toString(); }

	private class AVLSentinel extends AVLNode {
		AVLSentinel () { super(null); height = -1; }
		public boolean isEmpty () { return true; }
		public int count () { return 0; }
		public String toString () { return ""; }

		public AVLNode add (Object newElement) { 
			AVLNode newNode = new AVLNode(newElement); 
			newNode.leftChild = this;
			newNode.rightChild = this;
			return newNode;
			}

		public boolean contains (Object newElement) { return false; }

		public Object find (Object newElement) {
			throw new NoSuchElementException(newElement.toString());
		}

		public AVLNode remove (Object oldElement) {
			throw new NoSuchElementException(oldElement.toString());
		}

		public Object getFirst () {
			throw new NoSuchElementException();
		}

		public AVLNode removeFirst () {
			throw new NoSuchElementException();
		}
	}
}
