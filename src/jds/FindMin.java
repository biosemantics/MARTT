package jds;

import jds.Collection;

/**
 * FindMin - find smallest element in collection (priority queue);
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

public interface FindMin extends Collection {

	/**
	 * add a new value to the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public void addElement (Object value);

	/**
	 * yields the smallest element in collection
	 *
	 * @return the first (smallest) value in collection
	 */
	public Object getFirst ();

	/**
	 * removes the smallest element in collection
	 *
	 */
	public void removeFirst ();
}
