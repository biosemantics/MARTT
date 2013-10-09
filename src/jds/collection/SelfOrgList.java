package jds.collection;

import jds.collection.LinkedList;
import jds.util.DoubleLink;

/**
 * SelfOrgList - self organizing linked list;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 */

public class SelfOrgList extends LinkedList {

	/**
	 * see if collection contains value
	 *
	 * @param test element to be tested
	 * @return true if collection contains value
	 */
	public boolean containsElement (Object testElement) {
		for (DoubleLink ptr = firstLink; ptr != sentinel; ptr = ptr.next) {
			if (testElement.equals(ptr.value)) {
					// move to front
				ptr.remove();
				addFirst(testElement);
					// return true
				return true;
			}
		}
		return false;
	}
}
