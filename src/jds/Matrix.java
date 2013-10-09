package jds;

import java.io.Serializable;

/**
 * Matrix - two dimensional indexed collection;
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

public interface Matrix extends Serializable {

	/**
	 * set bounds in number of rows and columns
	 *
	 * @param rows number of rows in matrix
	 * @param columns number of columns in matrix
	 */
	public void setSize (int rows, int columns);

	/**
	 * determine number of rows in matrix
	 *
	 * @return number of rows as integer
	 */
	public int numberRows();

	/**
	 * determine number of columns in matrix
	 *
	 * @return number of columns in matrix as integer
	 */
	public int numberColumns();

	/**
	 * find element at give location
	 *
	 * @param row index for row dimension of matrix
	 * @param column index for column dimension of matrix
	 * @return object stored at given location
	 * @exception java.lang.ArrayIndexOutOfBoundsException index is illegal
	 */
	public Object elementAt(int row, int column);

	/**
	 * change element at given location
	 *
	 * @param val new value for position
	 * @param row index for row dimension of matrix
	 * @param column index for column dimension of matrix
	 * @exception java.lang.ArrayIndexOutOfBoundsException index is illegal
	 */
	public void setElementAt(Object val, int row, int column);
}
