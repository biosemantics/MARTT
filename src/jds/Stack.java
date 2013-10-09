package jds;

import jds.Collection;

/**
 * Stack - collection with LIFO behavior;
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

public interface Stack extends Collection {

	/**
	 * add a new value to end of the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public void addLast (Object value);

	/**
	 * access the last value in collection
	 *
	 * @return element at top of collection
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public Object getLast ();

	/**
	 * remove last value in collection
	 *
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public void removeLast ();
};

