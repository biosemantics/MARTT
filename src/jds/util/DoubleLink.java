package jds.util;

import java.io.Serializable;

/**
 * DoubleLink - one link in a linked list;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 */

public class DoubleLink implements Serializable {
	/**
	 * the value being held by this link
	 */
	public Object value;

	/**
	 * the next and previous links
	 */
	public DoubleLink next, prev;

	/**
	 * initialize a new link value
	 *
	 * @param v initial value for link
	 * @param n next link in sequence
	 * @param p previous link in sequence
	 */
	public DoubleLink (Object v, DoubleLink n, DoubleLink p) 
		{ value = v; next = n; prev = p; }

	/**
	 * insert a new link into a sequence
	 *
	 * @param newlink the link to be inserted
	 */
	public synchronized void insertLink (DoubleLink newLink) {
		newLink.next = this;
		newLink.prev = prev;
		if (prev != null)
			prev.next = newLink;
		prev = newLink;
	}

	/**
	 * insert a value into a sequence
	 *
	 * @param newValue the new value to be inserted
	 */
	public DoubleLink insert (Object newValue) {
		DoubleLink newLink = new DoubleLink(newValue, this, prev);
		insertLink (newLink);
		return newLink;
	}

	/**
	 * remove a link from a sequence
	 */
	public synchronized void remove () {
		value = null;
		if (next != null) next.prev = prev;
		if (prev != null) prev.next = next;
		next = prev = null;
	}
}
