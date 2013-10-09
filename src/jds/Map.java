package jds;

import jds.Collection;

/**
 * Map - collection of key/value pairs;
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

public interface Map extends Collection {

	/**
	 * see if collection contains element with given key
	 *
	 * @param key index for element to be tested
	 * @return true if collection has entry with given key
	 */
	public boolean containsKey (Object key);

	/**
	 * return object stored under given key
	 *
	 * @param key index for element to be accessed
	 * @return value of object stored with given key
	 * @exception java.util.NoSuchElementException no value with given key
	 */
	public Object get (Object key);

	/**
	 * remove element with given key
	 *
	 * @param key index for element to be removed
	 * @exception java.util.NoSuchElementException no value with given key
	 */
	public void removeKey (Object key);

	/**
	 * establish new key/value connection, or replace value associated with key
	 *
	 * @param key index for element to be tested
	 * @param value element to be associated with key
	 */
	public void set (Object key, Object value);
}

