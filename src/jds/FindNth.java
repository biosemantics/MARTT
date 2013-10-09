package jds;

/**
 * FindNth - find Nth smallest value in collection
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 */

public interface FindNth {

	/**
	 * find nth smallest value in collection
	 *
	 * @param	index of value, from 0 to <code>(size-1)</code>
	 * @return	value of element
	 * @exception	java.util.NoSuchElementException index is illegal
	 */
	public Object findNth (int index);
}
