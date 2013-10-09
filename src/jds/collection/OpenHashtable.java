package jds.collection;

import java.util.NoSuchElementException;
import java.util.Enumeration;
import jds.Bag;
import jds.Indexed;

/**
 * OpenHashtable - collection stored using open address hashing;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 */

public class OpenHashtable implements Bag {
	Indexed elementData;
	int elementCount = 0;

		// contructors
	/**
	 * initialize newly created hash table
	 *
	 * @param size number of buckets in the initial table
	 */
	public OpenHashtable (int size) 
		{ elementData = new Vector(); elementData.setSize(size); }

	/**
	 *
	 */
	public OpenHashtable (Indexed ed)
		{ elementData = ed; }

		// the Collection interface
	public boolean isEmpty () { return elementCount == 0; }
	public int size () { return elementCount; }
	public Enumeration elements () 
		{ return new OpenHashtableEnumerator(); }

		// the Bag interface
	public void addElement (Object val) {
			// make certain we have room for element
		if (elementCount + 1 >= elementData.size()) reAdjustTable();
			// then add to table
		int index = Math.abs(val.hashCode()) % elementData.size();
		while (elementData.elementAt(index) != null)
			if (++index >= elementData.size()) index = 0;
		elementData.setElementAt(val, index);
		elementCount++;
	}

	public boolean containsElement (Object val) {
		int index = Math.abs(val.hashCode()) % elementData.size();
		while (elementData.elementAt(index) != null) {
			if (val.equals(elementData.elementAt(index)))
				return true;
			if (++index >= elementData.size()) index = 0;
			}
		return false;
	}

	public Object findElement (Object val) {
		int index = Math.abs(val.hashCode()) % elementData.size();
		while (elementData.elementAt(index) != null) {
			if (val.equals(elementData.elementAt(index)))
				return elementData.elementAt(index);
			if (++index >= elementData.size()) index = 0;
			}
		throw new NoSuchElementException(val.toString());
	}

	public void removeElement (Object val) {
		throw new NoSuchElementException(val.toString());
	}

	private void reAdjustTable () {
		Indexed oldTable = elementData;
		elementData = new Vector();
		elementData.setSize(oldTable.size() * 2);
		elementCount = 0;
		for (int i = 0; i < oldTable.size(); i++) {
			Object val = oldTable.elementAt(i);
			if (val != null) addElement(val);
		}
	}

	private class OpenHashtableEnumerator implements Enumeration {
		private int index = -1;

		public boolean hasMoreElements () {
			while (++index < elementData.size()) {
				if (elementData.elementAt(index) != null)
					return true;
			}
			return false;
		}

		public Object nextElement () { return elementData.elementAt(index); }
	}
}

