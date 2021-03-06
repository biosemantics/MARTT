package jds.collection;

import jds.Matrix;
import java.io.Serializable;
/**
 * SparseMatrix - sparse two dimensional indexed collection;
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

public class SparseMatrix implements Matrix, Serializable {
		// constructors
	/**
	 * initialize a newly created sparse matrix
	 */
	public SparseMatrix () { }

	/**
	 * initialize a newly created sparse matrix
	 *
	 * @param d default value to use for uninitialized fields
	 */
	public SparseMatrix (Object d) { defaultValue = d; }

		// data fields
	private Object defaultValue = null;
	private SparseVector rows = new SparseVector();

	/**
	 * set bounds in number of rows and columns
	 *
	 * @param rows number of rows in matrix
	 * @param columns number of columns in matrix
	 */
	public void setSize (int rows, int columns) { }

	/**
	 * determine number of rows in matrix
	 *
	 * @return number of rows as integer
	 */
	public int numberRows() { return rows.size(); }

	/**
	 * determine number of columns in matrix
	 *
	 * @return number of columns in matrix as integer
	 */
	public int numberColumns() { return 0; }

		// get and set operations
	/**
	 * find element at give location
	 *
	 * @param i index for row dimension of matrix
	 * @param j index for column dimension of matrix
	 * @return object stored at given location
	 * @exception java.lang.ArrayIndexOutOfBoundsException index is illegal
	 */
	public Object elementAt (int i, int j) {
		Object r = rows.elementAt(i);
		if (r == null) return defaultValue;
		SparseVector row = (SparseVector) r;
		return row.elementAt(j);
	}


	/**
	 * change element at given location
	 *
	 * @param v new value for position
	 * @param i index for row dimension of matrix
	 * @param j index for column dimension of matrix
	 * @exception java.lang.ArrayIndexOutOfBoundsException index is illegal
	 */
	public void setElementAt (Object v, int i, int j) {
		Object r = rows.elementAt(i);
		if (r == null) {
			r = new SparseVector(defaultValue);
			rows.setElementAt(r, i);
		}
		SparseVector row = (SparseVector) r;
		row.setElementAt(v, j);
	}
}
