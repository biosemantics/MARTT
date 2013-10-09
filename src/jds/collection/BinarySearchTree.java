package jds.collection;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Enumeration;
import jds.Bag;
import jds.Sorted;
import jds.FindMin;
import java.util.Comparator;
import jds.util.BinaryNode;
import jds.util.InorderTreeTraversal;



/**
 * BinarySearchTree - set kept in a binary search tree;
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



public class BinarySearchTree implements Bag, Sorted, FindMin {
  /**
   * initialize a newly created binary search tree
   *
   * @param t comparator object used to place elements in sequence
   */
  public BinarySearchTree(Comparator t) {
    test = t;
  }

  private Comparator test;
  private BSTNode root = new BSTSentinel();
  private int elementCount = 0;

  /**
   * Determines whether the collection is empty
   *
   * @return true if the collection is empty
   */
  public boolean isEmpty() {
    return root.isEmpty();
  }

  /**
   * Determines number of elements in collection
   *
   * @return number of elements in collection as integer
   */
  public int size() {
    return elementCount;
  }

  /**
   * Yields enumerator for collection
   *
   * @return an <code>Enumeration</code> that will yield the elements of the collection
   * @see java.util.Enumeration
   */
  public Enumeration elements() {
    return new InorderTreeTraversal(root);
  }

  /**
   * see if collection contains value
   *
   * @param val element to be tested
   * @return true if collection contains value
   */
  public boolean containsElement(Object val) {
    return root.contains(val);
  }

  /**
   * find element that will test equal to value, and replace value for the element
   *
   * @param val element to be tested
   * @return
   */
  public synchronized void updateElement(Object val) {
    root.update(val);
  }
  /**
   * add a new value to the collection
   *
   * @param value element to be inserted into collection
   */
  public synchronized void addElement(Object val) {
    root = root.add(val);
    elementCount++;
  }

  /**
   * remove a new value from the collection
   *
   * @param value element to be removed from collection
   * @exception java.util.NoSuchElementException no matching value
   */
  public synchronized void removeElement(Object val) {
    root = root.remove(val);
    elementCount--;
  }

  public Object getFirst() {
    return root.getFirst();
  }

  public void removeFirst() {
    root = root.removeFirst();
    elementCount--;
  }

  /**
   * find element that will test equal to value
   *
   * @param value element to be tested
   * @return first value that is <code>equals</code> to argument
   * @exception java.util.NoSuchElementException no matching value
   */
  public Object findElement(Object val) {
    return root.find(val);
  }

  private class BSTNode
      extends BinaryNode {

    public BSTNode(Object v) {
      super(v);
    }

    public BSTNode left() {
      return (BSTNode) leftChild;
    }

    public BSTNode right() {
      return (BSTNode) rightChild;
    }

    public boolean isEmpty() {
      return false;
    }

    public boolean contains(Object newElement) {
      int testResult = test.compare(newElement, value);
      if (testResult == 0)
        return true;
      if (testResult < 0)
        return left().contains(newElement);
      return right().contains(newElement);
    }

    public Object find(Object newElement) {
      int testResult = test.compare(newElement, value);
      if (testResult == 0)
        return value;
      if (testResult < 0)
        return left().find(newElement);
      return right().find(newElement);
    }

    public void update(Object newElement) {
      int testResult = test.compare(newElement, value);
      if (testResult == 0){
        value = newElement;
        return;
      }
      if (testResult < 0)
         left().update(newElement);
      else right().update(newElement);
    }

    public BSTNode add(Object newElement) {
      if (test.compare(newElement, value) < 0)
        leftChild = left().add(newElement);
      else
        rightChild = right().add(newElement);
      return this;
    }

    public Object getFirst() {
      if (leftChild.isEmpty())
        return this;
      return left().getFirst();
    }

    public BSTNode removeFirst() {
      if (leftChild.isEmpty())
        return right();
      leftChild = left().removeFirst();
      return this;
    }

    public BSTNode remove(Object oldElement) {
      int testResult = test.compare(oldElement, value);
      if (testResult == 0) { // found it
        if (right().isEmpty())
          return left();
        value = right().getFirst();
        rightChild = right().removeFirst();
      }
      else if (testResult < 0)
        leftChild = left().remove(oldElement);
      else
        rightChild = right().remove(oldElement);
      return this;
    }

    public String toString() {
      return "(" + leftChild.toString() + " " +
          value.toString() + " " +
          rightChild.toString() + ")";
    }
  }

  public String toString() {
    return root.toString();
  }

  private class BSTSentinel
      extends BSTNode {
    BSTSentinel() {
      super(null);
    }

    public boolean isEmpty() {
      return true;
    }

    public String toString() {
      return "";
    }

    public BSTNode add(Object newElement) {
      BSTNode newNode = new BSTNode(newElement);
      newNode.leftChild = this;
      newNode.rightChild = this;
      return newNode;
    }

    public boolean contains(Object newElement) {
      return false;
    }

    public Object find(Object newElement) {
      throw new NoSuchElementException();
    }

    public BSTNode remove(Object oldElement) {
      throw new NoSuchElementException();
    }

    public Object getFirst() {
      throw new NoSuchElementException();
    }

    public BSTNode removeFirst() {
      throw new NoSuchElementException();
    }
  }
}


