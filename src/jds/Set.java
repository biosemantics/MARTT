package jds;

import jds.Bag;

/**
 * Set - union/intersection/difference of collections of values;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @version 1.2 October 2000
 * @see jds.Bag
 */

public interface Set extends Bag {

	/**
	 * form union with argument set
	 *
	 * @param aSet collection to be joined to current
	 */
	public void unionWith (Bag aSet);

	/**
	 * form intersection with argument set
	 *
	 * @param aSet collection to be intersected with current
	 */
	public void intersectWith (Bag aSet);

	/**
	 * form difference from argument set
	 *
	 * @param aSet collection to be compared to current
	 */
	public void differenceWith (Bag aSet);

	/**
	 * see if current set is subset of argument set
	 *
	 * @param aSet collection to be tested against
	 * @return true if current collection is subset of argument collection
	 */
	public boolean subsetOf (Bag aSet);
}

