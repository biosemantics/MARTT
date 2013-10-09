package jds.util;

import java.util.Comparator;
import java.io.Serializable;
/**
 * IntegerComapre - place two Integer objects in order;
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

public class IntegerCompare implements Serializable, Comparator {

	/**
	 * determine order of two integer objects; -1, 0 or 1
	 *
	 * @param left first object
	 * @param right second object
	 * @return -1 if left less than right, 0 if equal, 1 otherwise
	 */
	public int compare (Object left, Object right) {
		Integer ileft = (Integer) left;
		Integer iright = (Integer) right;
		if (ileft.intValue() == iright.intValue())
			return 0;
		if (ileft.intValue() < iright.intValue())
			return -1;
		return 1;
	}
}

