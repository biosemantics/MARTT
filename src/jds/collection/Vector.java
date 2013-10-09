package jds.collection;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import jds.Indexed;
import jds.Stack;
import jds.util.IndexedEnumeration;

/**
 * Vector - an indexed collection;
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

public class Vector implements Indexed, Stack {
		// data areas
	private int elementCount = 0;
	private Object [ ] elementData = new Object[10];

		// the Collection interface
	/**
	 * Determines whether the collection is empty
	 *
	 * @return true if the collection is empty
	 */
	public boolean isEmpty () { return elementCount == 0; }

	/**
	 * Determines number of elements in collection
	 *
	 * @return number of elements in collection as integer
	 */
	public int size () { return elementCount; }

	/**
	 * Yields enumerator for collection
	 *
	 * @return an <code>Enumeration</code> that will yield the elements of the collection
	 * @see java.util.Enumeration
	 */
	public Enumeration elements () { return new IndexedEnumeration(this); }

		// the Indexed Interface

	/**
	 * set number of elements in collection
	 *
	 * @param	size the new size of the collection
	 */
	public void setSize (int newSize) {
		while (newSize > elementData.length)
			ensureCapacity(2 * elementData.length);
		elementCount = newSize;
	}

	/**
	 * find value at specific index location
	 *
	 * @param	index the index of the desired value
	 * @exception	java.lang.ArrayIndexOutOfBoundsException array index is illegal
	 * @return	the desired value
	 */
	public Object elementAt (int index) { 
		if ((index < 0) || (index >= elementCount))
			throw new ArrayIndexOutOfBoundsException(index);
		return elementData[index]; 
	}

	/**
	 * set value at specific location
	 *
	 * @param	v the value to be inserted
	 * @param	index the position at which value will be inserted
	 * @exception	java.lang.ArrayIndexOutOfBoundsException array index is illegal
	 */
	public void setElementAt (Object v, int index) {elementData[index] = v;}

	/**
	 * add a new element into the collection, making collection one element larger
	 *
	 * @param	val the value to be inserted
	 * @param	index the position at which value will be inserted, other elements will be moved upwards
	 */
	public synchronized void addElementAt (Object val, int index) {
		if ((index < 0) || (index > elementCount))
			throw new ArrayIndexOutOfBoundsException(index);
		setSize(elementCount+1);
		for (int i = elementCount-1; i > index; i--)
			setElementAt(elementAt(i-1), i);
		setElementAt(val, index);
	}

	/**
	 * remove a value from a collection, making collection one element smaller
	 *
	 * @param	index the index of the element to be removed
	 * @exception	java.util.NoSuchElementException array index is illegal
	 */
	public synchronized void removeElementAt (int index) {
		if ((index < 0) || (index >= elementCount))
			throw new NoSuchElementException();
		elementCount--;
		for (int i = index; i < elementCount; i++)
			elementData[i] = elementData[i+1];
		elementData[elementCount] = null;
	}

		// the Stack Interface
	/**
	 * add a new value to end of the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public void addLast (Object val) {addElementAt(val, elementCount);}

	/**
	 * access the last value in collection
	 *
	 * @return element at top of collection
	 * @exception java.lang.ArrayIndexOutOfBoundsException no matching value
	 */
	public Object getLast () { return elementData[elementCount-1];}

	/**
	 * remove last value in collection
	 *
	 * @exception java.lang.ArrayIndexOutOfBoundsException no matching value
	 */
	public void removeLast () { removeElementAt(elementCount-1); } 

		// Vector specific method
	/**
	 * ensure buffer has sufficient number of elements
	 *
	 * @param newCapacity capacity of collection after operation
	 */
	public synchronized void ensureCapacity (int newCapacity) {
		if (newCapacity <= elementData.length) return; 
		Object [ ] newArray = new Object[newCapacity];
		for (int i = 0; i < elementCount; i++)
			newArray[i] = elementData[i];
		elementData = newArray;
	}
}
