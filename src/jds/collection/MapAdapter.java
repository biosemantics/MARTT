package jds.collection;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.io.Serializable;
import jds.Map;
import jds.Bag;
import java.util.Comparator;
import jds.util.Comparable;
import jds.util.DefaultComparator;

/**
 * MapAdapter - collection of key/value pairs;
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

public class MapAdapter implements Map {
	private Bag elementData;
	private Comparator test;

		// constructor
	/**
	 * initialize newly created Map
	 *
	 * @param aBag the set to hold the underlying data values
	 */
	public MapAdapter (Bag aBag)
		{ elementData = aBag; test = new DefaultComparator(); }

	/**
	 * initialize newly created Map
	 *
	 * @param aBag the set to hold the underlying data values
	 * @param t the comparator object used to order map keys
	 */
	public MapAdapter (Bag aBag, Comparator t) 
		{ elementData = aBag; test = t; }

		// Collection interface
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

	/**
	 * Yields enumerator for collection
	 *
	 * @return an <code>Enumeration</code> that will yield the elements of the collection
	 * @see java.util.Enumeration
	 */
	public Enumeration elements () 
		{ return new MapEnumerator(elementData.elements()); }

		// Map interface
	/**
	 * see if collection contains element with given key
	 *
	 * @param key index for element to be tested
	 * @return true if collection has entry with given key
	 */
	public boolean containsKey (Object key) {
		Association test = new Association(key, null);
		return elementData.containsElement(test);
	}


	/**
	 * return object stored under given key
	 *
	 * @param key index for element to be accessed
	 * @return value of object stored with given key
	 * @exception java.util.NoSuchElementException no value with given key
	 */
	public Object get (Object key) {
		Association test = new Association(key, null);
		Association pair = (Association) elementData.findElement(test);
		return pair.value;
	}

	/**
	 * remove element with given key
	 *
	 * @param key index for element to be removed
	 * @exception java.util.NoSuchElementException no value with given key
	 */
	public void removeKey (Object key) {
		Association test = new Association(key, null);
		elementData.removeElement(test);
	}

	/**
	 * establish new key/value connection, or replace value associated with key
	 *
	 * @param key index for element to be tested
	 * @param value element to be associated with key
	 */
	public void set (Object key, Object newValue) {
		Association dummy = new Association(key, newValue);
		Association pair;
		try {
			pair = (Association) elementData.findElement(dummy);
			pair.value = newValue;
		} catch (NoSuchElementException e) {
			elementData.addElement(dummy);
		}
	}

	private class Association implements Comparable, Serializable {
		public Object key;
		public Object value;

		Association (Object k, Object v) {key = k; value = v; }

		public int hashCode () { return key.hashCode(); }

		public boolean equals (Object test) {
			if (test instanceof Association) {
				Association pair = (Association) test;
				return key.equals(pair.key);
			} 
			return key.equals(test);
		}

		public int compareTo (Object testElement) {
			Association pair = (Association) testElement;
			return test.compare(key, pair.key);
		}
	}

	private class MapEnumerator implements Enumeration {
		private Enumeration elementEnumeration;

		MapEnumerator (Enumeration ed) { elementEnumeration = ed; }

		public boolean hasMoreElements () 
			{ return elementEnumeration.hasMoreElements(); }

		public Object nextElement () {
			Association pair = (Association) 
			elementEnumeration.nextElement();
			return pair.key;
		}
	}

}
