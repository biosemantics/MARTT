package jds.collection;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import jds.Bag;
import jds.Deque;
import jds.util.DoubleLink;

/**
 * LinkedList - collection based on a sequence of linked values;
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

public class LinkedList implements Bag, Deque {
	/**
	 * initialize newly created list
	 */
	public LinkedList () { firstLink = sentinel; }
	/*
	 * pointers to first and last element
	 */
	protected DoubleLink firstLink;
	protected final DoubleLink sentinel = new Link(null);

	/*
	 * count of number of elements in collection
	 */
	protected int  elementCount = 0;

		// the Collection interface
	/**
	 * Determines whether the collection is empty
	 *
	 * @return true if the collection is empty
	 */
	public boolean isEmpty () { return firstLink == sentinel; }

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
	public Enumeration elements () { return new ListEnumeration(); }

		// the Deque interface
	/**
	 * add a new value to front of the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public void addFirst (Object newValue) { firstLink.insert(newValue); }

	/**
	 * add a new value to end of the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public void addLast (Object newValue) { sentinel.insert(newValue); }

	/**
	 * access the first value in collection
	 *
	 * @return element at front of collection
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public Object getFirst () {
		if (isEmpty()) throw new NoSuchElementException();
		return firstLink.value;
	}

	/**
	 * remove first value in collection
	 *
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public synchronized void removeFirst () { firstLink.remove(); }

	/**
	 * access the last value in collection
	 *
	 * @return element at top of collection
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public Object getLast () {
		if (isEmpty()) throw new NoSuchElementException();
		return sentinel.prev.value;
	}

	/**
	 * remove last value in collection
	 *
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public synchronized void removeLast () { 
		if (isEmpty()) throw new NoSuchElementException();
		sentinel.prev.remove(); 
	}

		// the Bag interface
	/**
	 * add a new value to the collection
	 *
	 * @param newValue element to be inserted into collection
	 */
	public synchronized void addElement (Object newValue) 
		{ sentinel.insert(newValue); }

	/**
	 * see if collection contains value
	 *
	 * @param test element to be tested
	 * @return true if collection contains value
	 */
	public boolean containsElement (Object test) {
		for (Enumeration e = elements(); e.hasMoreElements(); )
			if (test.equals(e.nextElement()))
				return true;
		return false;
		}

	/**
	 * find element that will test equal to value
	 *
	 * @param value element to be tested
	 * @return first value that is <code>equals</code> to argument
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public Object findElement (Object test) { 
		for (Enumeration e = elements(); e.hasMoreElements(); ) {
			Object testElement = e.nextElement();
			if (test.equals(testElement))
				return testElement;
		}
		throw new NoSuchElementException(test.toString());
	}
		
	/**
	 * remove a new value from the collection
	 *
	 * @param value element to be removed from collection
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public synchronized void removeElement (Object newValue) {
		for (DoubleLink ptr = firstLink; ptr != sentinel; ptr = ptr.next)
			if (newValue.equals(ptr.value)) {
				ptr.remove();
				return;
				}
		throw new NoSuchElementException(newValue.toString());
		}

/**
 * LinkedList.Link - Link in a LinkedList; inner class for LinkedList
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Data Structures in Java, 
 * A Visual and Explorational Approach</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 1999.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see LinkedList
 */

	protected class Link extends DoubleLink {

		/**
		 * initialize a new Link object
		 *
		 * @param v value of link
		 * @param n next link in sequence
		 * @param p previous link in sequence
		 */
		public Link (Object v) { super(v, null, null); }

		/**
		 * insert a new link into the sequence
		 *
		 * @param newValue value held by new link
		 */
		public DoubleLink insert (Object newValue) {
			elementCount++;
			Link newLink = new Link(newValue);
			insertLink(newLink);
			if (newLink.prev == null)
				firstLink = newLink;
			return newLink;
		}

		/**
		 * remove a link from the sequence
		 */
		public void remove () {
			if (next == null)  // cannot remove last element
				throw new NoSuchElementException();
			elementCount--;
			if (prev == null)
				firstLink = next;
			super.remove();
		}
	}

	protected class ListEnumeration implements Enumeration {
		public DoubleLink current = firstLink;

		public boolean hasMoreElements () { return current != sentinel; }
		public Object nextElement () { 
			Object result = current.value;
			current = current.next;
			return result; 
		}
	}
}
