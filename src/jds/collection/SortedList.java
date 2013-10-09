package jds.collection;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import jds.Collection;
import jds.Sorted;
import jds.Bag;
import jds.Set;
import jds.FindMin;
import java.util.Comparator;
import jds.util.DoubleLink;

/**
 * SortedList - a linked list that is maintained in order;
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

public class SortedList implements Sorted, Set, FindMin {
	private LinkedList elementData;
	private Comparator test;

	/**
	 * initialize a sorted list
	 *
	 * @param c the comparator object
	 */
	public SortedList (Comparator c) 
		{ elementData = new LinkedList(); test = c; }


	/**
	 * Yields enumerator for collection
	 *
	 * @return an <code>Enumeration</code> that will yield the elements of the collection
	 * @see java.util.Enumeration
	 */
	public Enumeration elements () { return elementData.elements(); }

	/**
	 * Determines whether the collection is empty
	 *
	 * @return true if the collection is empty
	 */
	public boolean isEmpty () { return elementData.isEmpty(); }

	/**
	 * Determines number of elements in collection
	 *
	 * @return number of elements in collection as integer
	 */
	public int size () { return elementData.size(); }

		// Bag interface
	/**
	 * add a new value to the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public void addElement (Object newElement) {
		DoubleLink ptr = elementData.firstLink;
		while (ptr != elementData.sentinel) {
			if (test.compare(newElement, ptr.value) < 0) {
				ptr.insert(newElement);
				return; 
			}
			ptr = ptr.next;
		}
		ptr.insert(newElement); // add to end
	}

	/**
	 * see if collection contains value
	 *
	 * @param value element to be tested
	 * @return true if collection contains value
	 */
	public boolean containsElement (Object val)
		{ return elementData.containsElement(val); }

	/**
	 * find element that will test equal to value
	 *
	 * @param value element to be tested
	 * @return first value that is <code>equals</code> to argument
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public Object findElement (Object val) 
		{ return elementData.findElement(val); }

	/**
	 * remove a new value from the collection
	 *
	 * @param value element to be removed from collection
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public void removeElement (Object val)
		{ elementData.removeElement(val); }

		// FindMin operations
	/**
	 * yields the smallest element in collection
	 *
	 * @return the first (smallest) value in collection
	 */
	public Object getFirst () { return elementData.getFirst(); }

	/**
	 * removes the smallest element in collection
	 *
	 */
	public void removeFirst () { elementData.removeFirst(); }

		// Set operations
	public void mergeWith (Collection newSet) {
		DoubleLink ptr = elementData.firstLink;
		for (Enumeration e = newSet.elements(); e.hasMoreElements(); ) {
			Object newElement = e.nextElement();
			while (true)
				if (ptr == elementData.sentinel) 
					{ ptr.insert(newElement); break; }
				else if (test.compare(ptr.value, newElement) < 0)
					ptr = ptr.next;
				else 
					{ ptr.insert(newElement); break; }
		}
	}

	/**
	 * form union with argument set
	 *
	 * @param aSet collection to be joined to current
	 */
	public void unionWith (Bag newSet) {
		DoubleLink ptr = elementData.firstLink;
		for (Enumeration e = newSet.elements(); e.hasMoreElements(); ) {
			Object newElement = e.nextElement();
			while (true)
				if (ptr == elementData.sentinel) 
					{ ptr.insert(newElement); break; }
				else if (test.compare(ptr.value, newElement) < 0)
					ptr = ptr.next;
				else  {
					if (test.compare(newElement, ptr.value) < 0)
						ptr.insert(newElement); 
					break; 
				}
		}
	}

	/**
	 * form intersection with argument set
	 *
	 * @param aSet collection to be intersected with current
	 */
	public void intersectWith (Bag newSet) {
		DoubleLink ptr = elementData.firstLink;
		LinkedList intersect = new LinkedList();
		for (Enumeration e = newSet.elements(); e.hasMoreElements(); ) {
			Object newElement = e.nextElement();
				// skip smaller elements
			while ((ptr != elementData.sentinel) && 
				(test.compare(ptr.value, newElement) < 0))
					ptr = ptr.next;
				// save if in both sets
			if ((ptr != elementData.sentinel) && 
				ptr.value.equals(newElement))
					intersect.addLast(ptr.value);
		}
			// change elements to intersection
		elementData = intersect;
	}

	/**
	 * form difference from argument set
	 *
	 * @param aSet collection to be compared to current
	 */
	public void differenceWith (Bag newSet) {
		DoubleLink ptr = elementData.firstLink;
		for (Enumeration e = newSet.elements(); e.hasMoreElements(); ) {
			Object newElement = e.nextElement();
			while (true)
				if (ptr == elementData.sentinel) 
					break;
				else if (test.compare(ptr.value, newElement) < 0)
					ptr = ptr.next;
				else  {
					if (test.compare(newElement, ptr.value) < 0)
						break;
					DoubleLink rptr = ptr;
					ptr = ptr.next;
					rptr.remove();
				}
		}
	}

	/**
	 * see if current set is subset of argument set
	 *
	 * @param aSet collection to be tested against
	 * @return true if current collection is subset of argument collection
	 */
	public boolean subsetOf (Bag newSet) { return false; }
}

