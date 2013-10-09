package jds.collection;

import java.io.Serializable;


/**
 * BitSet - set of positive integer values;
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

public class BitSet implements Serializable {
		// constructor
	/**
	 * initialize newly created bitset
	 *
	 * @param size maximum bit index value
	 */
	public BitSet (int size) { ensureCapacity(size); }

		// data area
	private long [ ] bits = new long[1];

		// operations
	/**
	 * clear bit value at given index
	 *
	 * @param indx position of value to be cleared
	 */
	public void clear (int indx) { 
		if (index(indx) < bits.length)
			bits[index(indx)] &= ~ mask(indx); 
	}

	/**
	 * get bit value at given index
	 *
	 * @param indx position of value to be acessed
	 * @return true if bit is set, false otherwise
	 */
	public boolean get (int indx) { 
		if (index(indx) < bits.length)
			return 0 != (bits[index(indx)] & mask(indx)); 
		return false; //  not set
	}

	/**
	 * set bit value at given index
	 *
	 * @param indx position of value to be set
	 */
	public void set (int indx) { 
		ensureCapacity(indx);
		bits[index(indx)] |= mask(indx); 
	}

	/**
	 * combine bitset with another bitset
	 *
	 * @param set the second set, corresponding values are anded
	 */
	public void and (BitSet set) { 
		for (int i = 0; i < bits.length; i++)
			if (i < set.bits.length)
				bits[i] &= set.bits[i];
			else
				bits[i] = 0;
	}

	public void andNot (BitSet set) { }

	/**
	 * combine bitset with another bitset
	 *
	 * @param set the second set, corresponding values are ored
	 */
	public void or (BitSet set) { 
		for (int i = 0; i < bits.length; i++)
			if (i < set.bits.length)
				bits[i] |= set.bits[i];
	}

	public void xor (BitSet set) { }

		// private operations
	private int index (int i) { return i / 64; }
	private long mask (int i) { return 1L << (i % 64); }
	private void ensureCapacity (int size) {
		int required = 1 + index(size);
		if (required > bits.length) {
			// must make larger
			long [ ] newArray = new long[required];
			for (int i = 0; i < bits.length; i++)
				newArray[i] = bits[i];
			bits = newArray;
		}
	}
}

