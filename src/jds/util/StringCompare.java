package jds.util;

import java.util.Comparator;

/**
 * StringCompare - place two String objects in order;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see java.util.Comparator
 */

public class StringCompare implements Comparator {
	public int compare (Object left, Object right) {
		String sleft = (String) left;
		return sleft.compareTo((String) right);
	}
}
