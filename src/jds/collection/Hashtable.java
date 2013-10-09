package jds.collection;

import java.util.Enumeration;
import jds.Bag;
import java.util.Comparator;

/**
 * Hashtable - collection based on a vector of buckets;
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

public class Hashtable implements Bag {
	private Bag [ ] buckets;

		// constructors
	/**
	 * initialize a newly created Hash table
	 *
	 * @param n the number of buckets in the hash table
	 */
	public Hashtable (int n) { 
		buckets = new Bag[n];
		for (int i = 0; i < n; i++) buckets[i] = new LinkedList();
	}

	/**
	 * initialize a newly created hash table
	 *
	 * @param n the number of buckets in the hash table
	 * @param test a comparator used to order values
	 */
	public Hashtable (int n, Comparator test) { 
		buckets = new Bag[n];
		for (int i = 0; i < n; i++) buckets[i] = new SkipList(test);
	}

		// the Collection interface
	/**
	 * Determines whether the collection is empty
	 *
	 * @return true if the collection is empty
	 */
	public boolean isEmpty () { return 0 == size(); }

	/**
	 * Determines number of elements in collection
	 *
	 * @return number of elements in collection as integer
	 */
	public int size () {
		int count = 0;
		for (int i = 0; i < buckets.length; i++)
			count += buckets[i].size();
		return count;
	}

	/**
	 * Yields enumerator for collection
	 *
	 * @return an <code>Enumeration</code> that will yield the elements of the collection
	 * @see java.util.Enumeration
	 */
	public Enumeration elements () { return new HashtableEnumerator(); }

		// the Bag interface
	/**
	 * add a new value to the collection
	 *
	 * @param value element to be inserted into collection
	 */
	public void addElement (Object val) 
		{ bucket(val).addElement(val); }

	/**
	 * see if collection contains value
	 *
	 * @param value element to be tested
	 * @return true if collection contains value
	 */
	public boolean containsElement (Object val)
		{ return bucket(val).containsElement(val); }

	/**
	 * find element that will test equal to value
	 *
	 * @param value element to be tested
	 * @return first value that is <code>equals</code> to argument
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public Object findElement (Object val)
		{ return bucket(val).findElement(val); }

	/**
	 * remove a new value from the collection
	 *
	 * @param value element to be removed from collection
	 * @exception java.util.NoSuchElementException no matching value
	 */
	public void removeElement (Object val)
		{ bucket(val).removeElement(val); }

	private Bag bucket (Object val) 
		{ return buckets[Math.abs(val.hashCode()) % buckets.length];}

        private class HashtableEnumerator implements Enumeration {
		Enumeration currentEnumeration;
		int index;

		public HashtableEnumerator () {
			index = 0;
			currentEnumeration = buckets[0].elements();
		}

												public boolean hasMoreElements() {
			if (currentEnumeration.hasMoreElements()) return true;
			while (++index < buckets.length) {
				currentEnumeration = buckets[index].elements();
				if (currentEnumeration.hasMoreElements())
					return true;
			}
			return false;
		}

		public Object nextElement()
			{ return currentEnumeration.nextElement(); }
	}
}

