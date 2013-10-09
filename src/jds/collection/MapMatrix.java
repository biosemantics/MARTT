package jds.collection;

import jds.Map;
import jds.collection.MapAdapter;
import jds.collection.LinkedList;

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

public class MapMatrix {
		// constructors
	/**
	 * initialize newly created matrix map
	 */
	public MapMatrix () { }

	/**
	 * initialize newly created matrix map
	 *
	 * @param d default value for uninitialized entiries
	 */
	public MapMatrix (Object d) { defaultValue = d; }

	private Object defaultValue = null;
	private Map rows = new MapAdapter(new LinkedList());

		// get and set operations
	/**
	 * get value stored at given key
	 *
	 * @param key1 row index
	 * @param key2 column index
	 * @return value stored at given position, or default value
	 */
	public Object get (Object key1, Object key2) {
		if (rows.containsKey(key1)) {
			Map row = (Map) rows.get(key1);
			if (row.containsKey(key2))
				return row.get(key2);
		}
		return defaultValue;
	}
			
	/**
	 * place value into given location
	 *
	 * @param key1 row index
	 * @param key2 column index
	 * @param v value to be stored
	 */
	public void set (Object key1, Object key2, Object v) {
		Map row;
		if (rows.containsKey(key1))
			row = (Map) rows.get(key1);
		else {
			row = new MapAdapter(new LinkedList());
			rows.set(key1, row);
		}
		row.set(key2, v);
	}
}

