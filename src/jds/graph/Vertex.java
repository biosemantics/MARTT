package jds.graph;

import java.util.Enumeration;
import jds.Bag;
import jds.Stack;
import jds.collection.LinkedList;
import jds.collection.OpenHashtable;

public class Vertex {
		// constructors
	/**
	 * initialy a newly created vertex
	 */
	public Vertex () { name = null; }

	/**
	 * initialize a newly created vertex
	 *
	 * @param n the value of the vertex
	 */
	public Vertex (Object n) { name = n; }

		// data fields 
	public final Object name;
	protected Bag edges = new LinkedList();

		// operations 
	/**
	 * add a new edge from this vertex
	 *
	 * @param v the tail of the new edge
	 */
	public void addEdge (Vertex v) { edges.addElement (v); }

	/**
	 * convert vertex into a string
	 */
	public String toString () { return name.toString(); }

	public int hashCode () { return name.hashCode(); }

	/**
	 * find the set of vertices reachable from this vertex
	 *
	 * @return a set of vertices
	 */
	public Bag findReachable () { 
		Stack pendingVertices = new LinkedList();
		pendingVertices.addLast (this); // add ourself as source
		Bag reachable = new OpenHashtable(17);

			// pull vertices from stack one by one
		while (! pendingVertices.isEmpty()) {
			Vertex vertx = (Vertex) pendingVertices.getLast();
			pendingVertices.removeLast();
				// if we haven't visited it yet, then do so now
			if (! reachable.containsElement(vertx)) {
				reachable.addElement(vertx);
					// add vertices that are now reachable
				Enumeration e = vertx.edges.elements();
				while (e.hasMoreElements())
					pendingVertices.addLast(e.nextElement());
			}
		}
		return reachable;
	}
}

