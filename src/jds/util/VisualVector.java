package jds.util;

import java.awt.*;
import java.util.Enumeration;
import jds.Indexed;


/**
 * VisualVector - indexed collection with graphical display;
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

public class VisualVector implements Indexed {

	/**
	 * initialize new visual vector
	 *
	 * @param	data the vector to be displayed
	 */
	public VisualVector (Indexed data) 
		{ elementData = data; display = new VectorPanel(); }

	private Indexed elementData;
	private Panel display;

	/**
	 * set number of elements in collection
	 *
	 * @param	size the new size of the collection
	 */
	public void setSize (int size) {elementData.setSize(size); }

	/**
	 * Determines whether the collection is empty
	 *
	 * @return true if the collection is empty
	 */
	public boolean isEmpty() { return elementData.isEmpty(); }

	/**
	 * Determines number of elements in collection
	 *
	 * @return number of elements in collection as integer
	 */
	public int size () { return elementData.size(); }

	/**
	 * Yields enumerator for collection
	 *
	 * @return an <code>Enumeration</code> that will yield the elements of the collection
	 * @see java.util.Enumeration
	 */
	public Enumeration elements () { return elementData.elements(); }

	/**
	 * find value at specific index location
	 *
	 * @param	index the index of the desired value
	 * @exception	java.lang.ArrayIndexOutOfBoundsException array index is illegal
	 * @return	the desired value
	 */
	public Object elementAt (int indx) 
		{ return elementData.elementAt(indx); }

	/**
	 * set value at specific location
	 *
	 * @param	v the value to be inserted
	 * @param	index the position at which value will be inserted
	 * @exception	java.lang.ArrayIndexOutOfBoundsException array index is illegal
	 */
	public void setElementAt (Object v, int indx) 
		{ elementData.setElementAt(v, indx); 
		pause(); display.repaint(); }

	/**
	 * add a new element into the collection, making collection one element larger
	 *
	 * @param	val the value to be inserted
	 * @param	index the position at which value will be inserted, other elements will be moved upwards
	 */
	public void addElementAt (Object val, int index) 
		{ elementData.addElementAt(val, index); 
		pause(); display.repaint(); }

	/**
	 * remove a value from a collection, making collection one element smaller
	 *
	 * @param	index the index of the element to be removed
	 * @exception	java.util.NoSuchElementException array index is illegal
	 */
	public void removeElementAt (int index) 
		{ elementData.removeElementAt(index); 
		pause(); display.repaint(); }


	/**
	 * yield a panel that will display the contents of the vector
	 *
	 * @return	Panel for vector display
	 */
	public Panel getPanel () { return display; }

	private void pause () {
		try {
			Thread.sleep(50);
		} catch (Exception e) { }
	}

	private class VectorPanel extends Panel {
		public void paint (Graphics g) { 
			int w = getSize().width;
			int h = getSize().height-20;
			int s = elementData.size();
			if (s == 0) return;
			int m = 0;
			for (int i = 0; i < s; i++) {
				Integer in = (Integer) elementData.elementAt(i);
				if (in.intValue() > m) m = in.intValue();
			}
			double hunit = 0;
			if (m != 0) hunit = h / (double) m;
			double wunit = w/ (double)s;
			int leftMargin = (int) (w - s*wunit)/2;
			for (int i = 0; i < s; i++) {
				Integer in = (Integer) elementData.elementAt(i);
				int dh = (int) (in.intValue() * hunit);
				g.fillRect((int) (leftMargin+ i * wunit), 
					(int) (h-dh), (int) (wunit-1), (int) dh);
			}
			super.paint(g);
		}
	}
}

