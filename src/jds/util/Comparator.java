package jds.util;

import java.io.Serializable;

/**
 * Comparator - place two objects in order;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * This class is now obsolete, having been replaced by java.util.Comparator
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see java.io.Serializable
 */

public interface Comparator extends Serializable {

	/**
	 * determine order of two object; -1, 0 or 1
	 *
	 * @param left first object
	 * @param right second object
	 * @return -1 if left less than right, 0 if equal, 1 otherwise
	 */
	public int compare (Object left, Object right);
}
