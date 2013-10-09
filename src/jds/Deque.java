package jds;

import jds.Stack;
import jds.Queue;

/**
 * Deque - collection with both stack-like and queue-like behavior;
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

public interface Deque extends Stack, Queue {

	/**
	 * add a new value to front of the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public void addFirst (Object value);
};
