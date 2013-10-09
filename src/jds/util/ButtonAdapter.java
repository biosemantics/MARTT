package jds.util;

import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * ButtonAdapter - simple button builder;
 * for use with book
 * <a href="http://www.cs.orst.edu/~budd/books/jds/">Classic Data Structures 
 * in Java</a>
 * by <a href="http://www.cs.orst.edu/~budd">Timothy A Budd</a>, 
 * published by <a href="http://www.awl.com">Addison-Wesley</a>, 2001.
 *
 * @author Timothy A. Budd
 * @version 1.1 September 1999
 * @see java.awt.Button
 * @see java.awt.event.ActionListener
 * @see java.awt.event.ActionEvent
 */

abstract public class ButtonAdapter extends Button implements ActionListener {

	/**
	 * initialize a button, establish ourself as listener for presses
	 *
	 * @param name label for button
	 */
	public ButtonAdapter (String name) {
		super(name);
		addActionListener (this);
	}

	/**
	 * when told that a button has been pressed, respond to it;
	 * this method is not generally overridden by user.
	 *
	 * @param e the particular action that button is responding to
	 */
	public void actionPerformed (ActionEvent e) { pressed(); }

	/**
	 * action to perform when button is pressed; 
	 * this must be overridden by user
	 *
	 */
	public abstract void pressed ();
}
