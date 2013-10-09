package jds.util;

import java.io.Serializable;

/**
 * BinaryNode - one node in a binary tree;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 */

public class BinaryNode implements Serializable {
	/**
	 * initialize a newly created binary node
	 */
	public BinaryNode () { value = null; }

	/**
	 * initialize a newly created binary node
	 *
	 * @param v value to be associated with new node
	 */
	public BinaryNode (Object v) { value = v; }

	/**
	 * value being held by node
	 */
	public Object value;

	/**
	 * left child of node
	 */
	public BinaryNode leftChild = null;

	/**
	 * right child of node
	 */
	public BinaryNode rightChild = null;

	/**
	 * return true if we are not a sentinel node
	 */
	public boolean isEmpty() { return false; }
}
