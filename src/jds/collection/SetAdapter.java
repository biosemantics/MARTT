package jds.collection;

import java.util.Enumeration;
import jds.Bag;
import jds.Set;

/**
 * SetAdapter set formed as a wrapper around existing collection;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see java.util.Enumeration
 * @see java.io.Serializable
 */

public class SetAdapter implements Set {
		// constructor
	/**
	 * initialize set adapter by wrapping around an existing set
	 *
	 * @param s existing set to hold underlying data values
	 */
	public SetAdapter (Bag s) { data = s; }

		// data field
	private Bag data;

		// the Collection interface
	/**
	 * Determines whether the collection is empty
	 *
	 * @return true if the collection is empty
	 */
	public boolean isEmpty () { return data.isEmpty(); }

	/**
	 * Determines number of elements in collection
	 *
	 * @return number of elements in collection as integer
	 */
	public int size () { return data.size(); }

	/**
	 * Yields enumerator for collection
	 *
	 * @return an <code>Enumeration</code> that will yield the elements of the collection
	 * @see java.util.Enumeration
	 */
	public Enumeration elements () { return data.elements(); }

		// the Bag interface
	/**
	 * add a new value to the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public void addElement (Object val)  // only add if not there
		{ if (! data.containsElement(val)) data.addElement(val); }

	/**
	 * see if collection contains value
	 *
	 * @param value element to be tested
	 * @return true if collection contains value
	 */
	public boolean containsElement (Object val) { return data.containsElement(val); }

	/**
	 * find element that will test equal to value
	 *
	 * @param value element to be tested
	 * @return first value that is <code>equals</code> to argument
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public Object findElement (Object val) { return data.findElement(val); }

	/**
	 * remove a new value from the collection
	 *
	 * @param value element to be removed from collection
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public void removeElement (Object val) { data.removeElement(val); }

		// the Set interface
	/**
	 * form union with argument set
	 *
	 * @param aSet collection to be joined to current
	 */
	public void unionWith (Bag aSet) {
		for (Enumeration e = aSet.elements(); e.hasMoreElements(); ) 
			addElement(e.nextElement());
	}

	/**
	 * form intersection with argument set
	 *
	 * @param aSet collection to be intersected with current
	 */
	public void intersectWith (Bag aSet) {
		Bag removedItems = new LinkedList();
			// find values to be removed
		Enumeration e;
		for (e = elements(); e.hasMoreElements(); ) {
			Object val = e.nextElement();
			if (! aSet.containsElement(val))
				removedItems.addElement(val);
			}
			// now remove them
		for (e = removedItems.elements(); e.hasMoreElements(); ) 
			removeElement(e.nextElement());
	}

	/**
	 * form difference from argument set
	 *
	 * @param aSet collection to be compared to current
	 */
	public void differenceWith (Bag aSet) {
		Bag removedItems = new LinkedList();
			// find values to be removed
		Enumeration e;
		for (e = elements(); e.hasMoreElements(); ) {
			Object val = e.nextElement();
			if (aSet.containsElement(val))
				removedItems.addElement(val);
		}
			// now remove them
		for (e = removedItems.elements(); e.hasMoreElements(); ) 
			removeElement(e.nextElement());
	}

	/**
	 * see if current set is subset of argument set
	 *
	 * @param aSet collection to be tested against
	 * @return true if current collection is subset of argument collection
	 */
	public boolean subsetOf (Bag aSet) {
		for (Enumeration e = elements(); e.hasMoreElements(); ) {
			Object val = e.nextElement();
			if (! aSet.containsElement(val))
				return false;
		}
		return true;
	}
}
