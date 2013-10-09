package jds.util;

import java.util.Enumeration;
import jds.Stack;
import jds.collection.Vector;

/**
 * IndererTreeTraversal - traverse a binary tree in inorder fashion;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 */

public class InorderTreeTraversal implements Enumeration {
	/**
	 * initialize a traversal rooted at given node
	 *
	 * @param root start of traversal
	 */
	public InorderTreeTraversal (BinaryNode root) { slideLeft(root); }

	private Stack stk = new Vector();

	/**
	 * see if enumeration has at least one more element
	 */
	public boolean hasMoreElements () { return ! stk.isEmpty(); }

	/**
	 * return the next element in the enumeration
	 *
	 * @return the value of the current element
	 */
	public Object nextElement () {
		BinaryNode current = (BinaryNode) stk.getLast();
		stk.removeLast();
		slideLeft (current.rightChild);
		return current.value;
	}

	private void slideLeft (BinaryNode p) {
		while ((p != null) && ! p.isEmpty()) {
			stk.addLast(p);
			p = p.leftChild;
		}
	}
}

