package jds.util;

import java.util.Enumeration;
import jds.Indexed;

/**
 * IndexedEnumeration - enumeration for indexed collections;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see jds.Indexed
 */

public class IndexedEnumeration implements Enumeration {

	/**
	 * initialize newly created IndexedEnumeration
	 *
	 * @param d collection to enumerate over
	 */
	public IndexedEnumeration (Indexed d) { data = d; }
	private Indexed data;
	private int index = 0;

	/**
	 * see if enumeration should continue
	 *
	 * @return true if enumeration has at least one more element
	 */
	public boolean hasMoreElements () { return index < data.size(); }

	/**
	 * get next element in enumeration
	 *
	 * @return value of next element in enumeration
	 */
	public Object nextElement () { return data.elementAt(index++); }
}

