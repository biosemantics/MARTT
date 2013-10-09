package jds;

import jds.Collection;

/**
 * Indexed - collection with elements accessible via index;
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

public interface Indexed extends Collection {

	/**
	 * set number of elements in collection
	 *
	 * @param	size the new size of the collection
	 */
	public void setSize (int size);

	/**
	 * find value at specific index location
	 *
	 * @param	index the index of the desired value
	 * @exception	java.lang.ArrayIndexOutOfBoundsException array index is illegal
	 * @return	the desired value
	 */
	public Object elementAt (int index); 

	/**
	 * set value at specific location
	 *
	 * @param	v the value to be inserted
	 * @param	index the position at which value will be inserted
	 * @exception	java.lang.ArrayIndexOutOfBoundsException array index is illegal
	 */
	public void setElementAt (Object v, int index);

	/**
	 * add a new element into the collection, making collection one element larger
	 *
	 * @param	val the value to be inserted
	 * @param	index the position at which value will be inserted, other elements will be moved upwards
	 */
	public void addElementAt (Object val, int index);

	/**
	 * remove a value from a collection, making collection one element smaller
	 *
	 * @param	index the index of the element to be removed
	 * @exception	java.util.NoSuchElementException array index is illegal
	 */
	public void removeElementAt (int index);
}
