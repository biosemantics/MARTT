package jds.collection;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import jds.Indexed;
import jds.Deque;
import jds.util.IndexedEnumeration;

/**
 * IndexedDeque - Deque implemented in the fashion of a Vector;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see jds.Vector
 */

public class IndexedDeque implements Indexed, Deque {

	private int firstFilled = 0;
	private int elementCount = 0;
	private Object [ ] elementData = new Object [5];

		// code to support the inherited interface
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

		// code to support the Indexed interface

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
		return elementData[(firstFilled+index) % elementData.length];
	}

	/**
	 * set value at specific location
	 *
	 * @param	v the value to be inserted
	 * @param	index the position at which value will be inserted
	 * @exception	java.lang.ArrayIndexOutOfBoundsException array index is illegal
	 */
	public void setElementAt (Object val, int index) {
		if ((index < 0) || (index >= elementCount))
			throw new ArrayIndexOutOfBoundsException(index);
		elementData[(firstFilled+index) % elementData.length] = val;
	}

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
	 * capacity return the capacity of this structure
	 *
	 * @return an integer indicating current capacity
	 */
	public int capacity () { return elementData.length; }

	/**
	 * ensure that buffer has sufficient capacity
	 *
	 * @param newCapacity proposed new capacity of buffer
	 */
	public synchronized void ensureCapacity (int newCapacity) {
		if (newCapacity <= elementData.length) return;
		Object [ ] newArray = new Object [newCapacity];
		int count = 0;
		if (firstFilled + elementCount <= elementData.length) {
			for (int i = firstFilled; count < elementCount; i++)
				newArray[count++] = elementData[i];
		} else {
			for (int i = firstFilled; i < elementData.length; i++)
				newArray[count++] = elementData[i];
			for (int i = 0; count < elementCount; i++)
				newArray[count++] = elementData[i];
		}
		firstFilled = 0;
		elementData = newArray;
	}

	/**
	 * add a new element into the collection, making collection one element larger
	 *
	 * @param	val the value to be inserted
	 * @param	index the position at which value will be inserted, other elements will be moved upwards
	 */
	public synchronized void addElementAt (Object val, int index) {
		if ((index < 0) || (index > elementCount))
			throw new ArrayIndexOutOfBoundsException(index);
		setSize(++elementCount);
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
			throw new ArrayIndexOutOfBoundsException(index);
		while (++index < elementCount)
			setElementAt(elementAt(index), index-1);
		setElementAt(null, index-1);
		elementCount--;
	}

		// code to support the deque interface

	/**
	 * access the first value in collection
	 *
	 * @return element at front of collection
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public Object getFirst () { return elementAt(0); }

	/**
	 * access the last value in collection
	 *
	 * @return element at top of collection
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public Object getLast () { return elementAt(elementCount-1); }

	/**
	 * add a new value to end of the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public void addLast (Object val) { addElementAt(val, elementCount); }

	/**
	 * add a new value to front of the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public synchronized void addFirst(Object val) {
		if (++elementCount >= elementData.length)
			ensureCapacity (2 * elementData.length);
		if (--firstFilled < 0) firstFilled = elementData.length-1;
		setElementAt(val, 0);
	}

	/**
	 * remove last value in collection
	 *
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public void removeLast () { removeElementAt(elementCount-1); }

	/**
	 * remove first value in collection
	 *
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public synchronized void removeFirst () {
		if (elementCount == 0) throw new NoSuchElementException();
		elementData[firstFilled] = null;
		if (++firstFilled == elementData.length) firstFilled = 0;
		elementCount--;
	}
}

