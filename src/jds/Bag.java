package jds;

import jds.Collection;

/**
 * Bag - simple collection with insertion, removal, and test;
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

public interface Bag extends Collection {

	/**
	 * add a new value to the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public void addElement (Object value);

	/**
	 * see if collection contains value
	 *
	 * @param value element to be tested
	 * @return true if collection contains value
	 */
	public boolean containsElement (Object value);

	/**
	 * find element that will test equal to value
	 *
	 * @param value element to be tested
	 * @return first value that is <code>equals</code> to argument
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public Object findElement (Object value);

	/**
	 * remove a new value from the collection
	 *
	 * @param value element to be removed from collection
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public void removeElement (Object value);
}

