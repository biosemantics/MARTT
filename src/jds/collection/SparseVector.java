package jds.collection;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.io.Serializable;
import jds.Indexed;
import jds.Bag;
import jds.Map;
import java.util.Comparator;
import jds.util.DefaultComparator;
import jds.util.Comparable;
import jds.util.IntegerCompare;
import jds.util.IndexedEnumeration;
import jds.collection.SkipList;
import jds.collection.MapAdapter;

/**
 * SparseVector - sparse collection with elements accessible via index;
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

public class SparseVector implements Indexed {
		// constructors
	/**
	 * initialize a newly created sparse vector
	 */
	public SparseVector () { }
	/**
	 * initialize a newly created sparse vector
	 *
	 * @param d default value to return for unassigned index positions
	 */
	public SparseVector (Object d) { defaultValue = d; }

		// data fields
	protected int elementSize = Integer.MAX_VALUE;
	protected Object defaultValue = null;
	protected Map elementData = new MapAdapter(new SkipList(), new IntegerCompare());

		// Collection interface
	/**
	 * Determines whether the collection is empty
	 *
	 * @return true if the collection is empty
	 */
	public boolean isEmpty () { return elementSize == 0; }

	/**
	 * Determines number of elements in collection
	 *
	 * @return number of elements in collection as integer
	 */
	public int size () { return elementSize; }

	/**
	 * Yields enumerator for collection
	 *
	 * @return an <code>Enumeration</code> that will yield the elements of the collection
	 * @see java.util.Enumeration
	 */
	public Enumeration elements () { return new IndexedEnumeration(this); }

		// Indexed interface
	/**
	 * set number of elements in collection
	 *
	 * @param	size the new size of the collection
	 */
	public void setSize (int size) { elementSize = size; }


	/**
	 * find value at specific index location
	 *
	 * @param	indx the index of the desired value
	 * @exception	java.lang.ArrayIndexOutOfBoundsException array index is illegal
	 * @return	the desired value
	 */
	public Object elementAt (int indx) {
		if ((indx < 0) || (indx > elementSize))
			throw new ArrayIndexOutOfBoundsException(indx);
		Integer key = new Integer(indx);
		if (elementData.containsKey(key))
			return elementData.get(key);
		return defaultValue;
	}

	/**
	 * set value at specific location
	 *
	 * @param	v the value to be inserted
	 * @param	index the position at which value will be inserted
	 * @exception	java.lang.ArrayIndexOutOfBoundsException array index is illegal
	 */
	public void setElementAt (Object v, int indx) {
		if ((indx < 0) || (indx > elementSize))
			throw new ArrayIndexOutOfBoundsException(indx);
		Integer key = new Integer(indx);
		elementData.set(key, v);
	}

	/**
	 * add a new element into the collection, making collection one element larger
	 *
	 * @param	val the value to be inserted
	 * @param	index the position at which value will be inserted, other elements will be moved upwards
	 */
	public void addElementAt (Object val, int index) { }


	/**
	 * remove a value from a collection, making collection one element smaller
	 *
	 * @param	index the index of the element to be removed
	 * @exception	java.util.NoSuchElementException array index is illegal
	 */
	public void removeElementAt (int index) { }
}

