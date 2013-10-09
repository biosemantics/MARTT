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

public class PreorderTreeTraversal implements Enumeration {
	/**
	 * initialize a traversal rooted at given node
	 *
	 * @param root start of traversal
	 */
	public PreorderTreeTraversal (BinaryNode root) 
		{ stk.addLast(root); }

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
		if (current.leftChild != null)
			stk.addLast(current.leftChild);
		else {
			BinaryNode parent = (BinaryNode) stk.getLast();
			stk.removeLast();
			while (parent.rightChild == null) {
				if (stk.isEmpty())
					return current.value;
				parent = (BinaryNode) stk.getLast();
				stk.removeLast();
				}
			stk.addLast(parent.rightChild);
		}
		return current.value;
	}

	public static void main (String [ ] args) {
		BinaryNode A = new BinaryNode("A");
		BinaryNode B = new BinaryNode("B");
		BinaryNode C = new BinaryNode("C");
		BinaryNode D = new BinaryNode("D");
		BinaryNode E = new BinaryNode("E");
		BinaryNode F = new BinaryNode("F");
		BinaryNode G = new BinaryNode("G");
		A.leftChild = B; A.rightChild = C;
		B.leftChild = D; B.rightChild = E;
		C.leftChild = F; C.rightChild = G;
		Enumeration e = new PreorderTreeTraversal(A);
		while (e.hasMoreElements())
			System.out.println(e.nextElement());
	}
}

