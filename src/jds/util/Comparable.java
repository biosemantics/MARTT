package jds.util;

/**
 * Comparable - interface for objects that can be compared to each other;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see java.io.Serializable
 */

public interface Comparable extends java.io.Serializable {
	/*
	 * compare object to argument
	 *
	 * @param o object to be compared against
	 * @return -1 if less than argument, 0 is equal, 1 if greater
	 */
	public int compareTo (Object o);
}

