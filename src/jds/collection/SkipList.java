package jds.collection;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import jds.Sorted;
import jds.Bag;
import jds.SortAlgorithm;
import jds.Indexed;
import java.util.Comparator;
import jds.util.DefaultComparator;
import jds.util.DoubleLink;

/**
 * SkipList - linked list with randomized pointers to middle;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 */

public class SkipList implements Sorted, Bag, SortAlgorithm {
	private SkipLink top;
	private LinkedList bottom;
	private Comparator test;

	/**
	 * initialize skip list with no elements
	 *
	 */
	public SkipList ( ) { 
		test = new DefaultComparator();
		bottom = new LinkedList();
		top = new SkipLink(null, bottom.sentinel);
	}

	/**
	 * initialize skip list with no elements
	 *
	 * @param t comparator to be used in ordering elements
	 */
	public SkipList (Comparator t) { 
		test = t;
		bottom = new LinkedList();
		top = new SkipLink(null, bottom.sentinel);
	}

		// collection interface
	/**
	 * Determines whether the collection is empty
	 *
	 * @return true if the collection is empty
	 */
	public boolean isEmpty () { return bottom.isEmpty(); }

	/**
	 * Determines number of elements in collection
	 *
	 * @return number of elements in collection as integer
	 */
	public int size () { return bottom.size(); }

	/**
	 * Yields enumerator for collection
	 *
	 * @return an <code>Enumeration</code> that will yield the elements of the collection
	 * @see java.util.Enumeration
	 */
	public Enumeration elements () { return bottom.elements(); }

		// Bag interface
	/**
	 * add a new value to the collection
	 *
	 * @param newElement element to be inserted into collection
	 */
	public void addElement (Object newElement) {
		DoubleLink p = slideLeft(top, newElement);
		p = p.insert(newElement);
		if ((p != null) && flip()) {
			top = new SkipLink(null, top);
			top.insertLink(new SkipLink(newElement, p));
			}
	}

	private DoubleLink slideLeft (DoubleLink p, Object testElement) {
		while ((p.prev != null) && 
			(test.compare(p.prev.value, testElement) >= 0))
				p = p.prev;
		return p;
	}

	/**
	 * see if collection contains value
	 *
	 * @param value element to be tested
	 * @return true if collection contains value
	 */
	public boolean containsElement (Object val) {
		DoubleLink p = top;
		while (p != null) {
			p = slideLeft(p, val);
			if ((p.value != null) && (val.equals(p.value))) return true;
			if (p instanceof SkipLink)
				p = ((SkipLink) p).down;
			else p = null;
		}
		return false;
	}

	/**
	 * find element that will test equal to value
	 *
	 * @param value element to be tested
	 * @return first value that is <code>equals</code> to argument
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public Object findElement (Object val) {
		DoubleLink p = top;
		while (p != null) {
			p = slideLeft(p, val);
			if ((p.value != null) && (val.equals(p.value))) return p.value;
			if (p instanceof SkipLink)
				p = ((SkipLink) p).down;
			else p = null;
		}
		throw new NoSuchElementException(val.toString());
	}

	/**
	 * remove a new value from the collection
	 *
	 * @param val element to be removed from collection
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public void removeElement (Object val) {
		DoubleLink p = top;
		while (p != null) {
			p = slideLeft(p, val);
			if ((p.value != null) && (val.equals(p.value))) {
				p.remove();
				return;
			}
			if (p instanceof SkipLink)
				p = ((SkipLink) p).down;
			else p = null;
		}
		throw new NoSuchElementException(val.toString());
	}

	/**
	 * rearrange collection into asending order
	 *
	 * @param data the values to be ordered
	 */
	public void sort (Indexed v) {
		SkipList sk = new SkipList(test);
		for (int i = 0; i < v.size(); i++)
			sk.addElement(v.elementAt(i));
		int i = 0;
		for (Enumeration e = sk.elements(); e.hasMoreElements(); ) 
			v.setElementAt(e.nextElement(), i);
	}

	private boolean flip () { return Math.random() < 0.5; }

	private class SkipLink extends DoubleLink {
		public DoubleLink down;
		public Object value;

		SkipLink (Object v, DoubleLink d) 
			{ super(v, null, null); down = d; }

		public DoubleLink insert (Object newElement) {
			DoubleLink p = slideLeft(down, newElement);
			DoubleLink d = p.insert(newElement);
			if ((d != null) && flip()) {
				SkipLink newLink = new SkipLink(newElement, d);
				super.insertLink(newLink);
				return newLink;
				}
			return null;
		}

		public void remove () 
			{ down.remove(); super.remove(); }
	}
}
