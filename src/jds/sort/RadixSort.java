package jds.sort;

import jds.Queue;
import jds.collection.LinkedList;

/**
 * RadixSort - implementation of the radix sort algorithm;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see jds.Indexed
 */

public class RadixSort {

	/**
	 * place elements in data into order
	 *
	 * @param data array of positive integer values
	 */
	public void radixSort (int [ ] data) {
		boolean flag = true;
		int divisor = 1;
		Queue [ ] buckets = new Queue[10];
		for (int i = 0; i < 10; i++)
			buckets[i] = new LinkedList();

		while (flag) {
			flag = false;
				// first copy the values into buckets
			for (int i = 0; i < data.length; i++) {
				int hashIndex = (data[i] / divisor) % 10;
				if (hashIndex > 0) flag = true;
				buckets[hashIndex].addLast(new Integer(data[i]));
			}
				// then copy the values back into vector
			divisor *= 10;
			int i = 0;
			for (int j = 0; j < 10; j++) {
				while (! buckets[j].isEmpty()) {
					Integer ival = (Integer) buckets[j].getFirst();
					buckets[j].removeFirst();
					data[i++] = ival.intValue();
				}
			}
		}
	}
}

