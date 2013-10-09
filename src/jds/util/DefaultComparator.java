package jds.util;

import java.util.Comparator;
import java.io.Serializable;
/**
 * DefaultComparator - comparator object for values that satisfy comparable interface;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>,
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see java.io.Serializable
 * @see java.util.Comparator
 */

public class DefaultComparator implements Serializable, Comparator{

	/**
	 * determine order of two object; -1, 0 or 1
	 *
	 * @param left first object
	 * @param right second object
	 * @return -1 if left less than right, 0 if equal, 1 otherwise
	 */
	public int compare (Object left, Object right) {
		Comparable cleft = (Comparable) left;
		return cleft.compareTo (right);
	}

	public boolean equals (Object obj) {
		return compare(this, obj) == 0;
	}
}

