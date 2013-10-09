package jds.collection;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import jds.Queue;

/**
 * Queue - collection with FIFO behavior;
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

public class RingBufferQueue implements Queue {

	/**
	 * initialize an empty ring buffer queue
	 */
	RingBufferQueue () { 
		firstFree = new RingBufferNode(null);
		firstFilled = firstFree;
		firstFree.next = firstFree;
		}
	private RingBufferNode firstFree, firstFilled;

	/**
	 * Determines whether the collection is empty
	 *
	 * @return true if the collection is empty
	 */
	public boolean isEmpty() { return firstFilled == firstFree; }

	/**
	 * Determines number of elements in collection
	 *
	 * @return number of elements in collection as integer
	 */
	public int size () {
		int count = 0;
		RingBufferNode p = firstFilled;
		for (; p != firstFree; p = p.next) count++;
		return count;
	}

	/**
	 * Yields enumerator for collection
	 *
	 * @return an <code>Enumeration</code> that will yield the elements of the collection
	 * @see java.util.Enumeration
	 */
	public Enumeration elements () { return null; }

	/**
	 * add a new value to end of the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public synchronized void addLast (Object val) {
		if (firstFree.next == firstFilled)
			firstFree.next = new RingBufferNode(firstFree.next);
		firstFree.value = val;
		firstFree = firstFree.next;
	}

	/**
	 * access the first value in collection
	 *
	 * @return element at front of collection
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public Object getFirst () { 
		if (firstFilled == firstFree) throw new NoSuchElementException();
		return firstFilled.value; }

	/**
	 * remove first value in collection
	 *
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public synchronized void removeFirst () { 
		if (firstFilled == firstFree) throw new NoSuchElementException();
		firstFilled = firstFilled.next; 
	}
}

class RingBufferNode {
	public Object value;
	public RingBufferNode next;

	public RingBufferNode (RingBufferNode n) 
		{ next = n; }
}
