package jds.util;

import java.util.Enumeration;
import jds.Stack;
import jds.collection.Vector;

/**
 * PreorderTreeTraversal - traverse a binary tree in preorder fashion;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 */

public class PostorderTreeTraversal implements Enumeration {

	/**
	 * initialize a traversal rooted at given node
	 *
	 * @param root start of traversal
	 */
	public PostorderTreeTraversal (BinaryNode root) 
		{ slideLeft(root); }

	private Stack stk = new Vector();

	private void slideLeft (BinaryNode current) {
		while (current != null) {
			stk.addLast(current);
			current = current.leftChild;
		}
	}

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
		if (! stk.isEmpty()) {
			BinaryNode parent = (BinaryNode) stk.getLast();
			if (parent.rightChild != current)
				slideLeft (parent.rightChild);
		}
		return current.value;
	}
}

