package jds.util;

import java.util.Enumeration;
import jds.Queue;
import jds.collection.LinkedList;
import jds.util.BinaryNode;

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

public class LevelorderTreeTraversal implements Enumeration {
	/**
	 * initialize a newly created traversal enumerator
	 *
	 * @param root start node for traversal
	 */
	public LevelorderTreeTraversal (BinaryNode root) 
		{ que.addLast(root); }

	private Queue que = new LinkedList();

	/**
	 * see if enumeration has at least one more element
	 */
	public boolean hasMoreElements () { return ! que.isEmpty(); }

	/**
	 * return the next element in the enumeration
	 *
	 * @return the value of the current element
	 */
	public Object nextElement () {
		BinaryNode current = (BinaryNode) que.getFirst();
		que.removeFirst();
		if (current.leftChild != null)
			que.addLast(current.leftChild);
		if (current.rightChild != null)
			que.addLast(current.rightChild);
		return current.value;
	}
}

