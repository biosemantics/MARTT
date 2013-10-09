package visitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.*;
import learning.MarkedSegment;

/**
 * <p>Title: XML Hierarchy Using Visitor Pattern</p>
 * <p>Description: ElementComponent uses Composite pattern and is the abstract class</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public abstract class ElementComponent
    implements Visitable, Serializable {
  protected ArrayList children = null; //child elements of this
  protected String tag = null; //tag of this
  protected ElementComposite parent = null;
  protected int type = 0; //by default, a node is non-leaf
  protected ArrayList trainingexamples = null;
  protected ArrayList markeds = null;
  protected ArrayList markedsegs = null;
  protected ArrayList answers = null;
  protected Score score = null;
  protected Hashtable trainfilenames = null;//training filenames, save genus or if absent, family name.

  /**
   *
   * @param xml xml text. if "this" is the root, then xml includes <?...?>
   * otherwise, it a well-formed chunck of xml
   */
  public ElementComponent(String xml, String fname, boolean train) {
    trainfilenames = new Hashtable();
    children = new ArrayList();
    trainingexamples = new ArrayList();
    markeds = new ArrayList();
    markedsegs = new ArrayList();
    answers = new ArrayList();
    if (train) {
      addTrain(xml, fname);
    }
    /*else {
      addAnswer(xml);
    }*/
  }

  public ElementComponent() {
    trainfilenames = new Hashtable();
    children = new ArrayList();
    trainingexamples = new ArrayList();
    markeds = new ArrayList();
    markedsegs = new ArrayList();
    answers = new ArrayList();
  }

  /**
   *
   * @param e An ElementComponent to be added to the children
   * @return ture, if e is added, otherwise false
   */
  public abstract boolean addChild(ElementComponent e);

  /**
   *
   * @param e An ElementComponent to be removed from the children
   * @return ture, if e is removed, otherwise false
   */
  public abstract boolean removeChild(ElementComponent e);

  /**
   * find in the children an elementcomponent with tag
   * @param tag
   * @return null if not found, otherwise return the elementcomponent
   */
  public abstract ElementComponent findChild(String tag);

  /**
   *
   * @param exp a new training example
   */
  public abstract void addTrainingExample(String exp, String fname);

  public ArrayList getMarkeds() {
    return markeds;
  }

  public ArrayList getMarkedSegs() {
    return markedsegs;
  }

  public void setMarkeds(ArrayList markeds) {
    this.markeds = markeds;
  }

  public ArrayList getAnswers() {
    return answers;
  }

  /**
   * Obtain an iterator of the children of the component
   * @return an iterator
   */
  public abstract Iterator iterator();

  /**
   * Obtain the parent ElementComponent
   * @return the parent ElementComponent
   */
  public ElementComposite getParent() {
    return parent;
  }

  public abstract ArrayList getChildClasses();
  public abstract ArrayList getChildSimpleClasses();
  public abstract ArrayList getAllClasses();
  public abstract ArrayList getAllScores();

  public String getParentTags() {
    StringBuffer tags = new StringBuffer();
    if (parent != null) {
      tags.append(parent.getParentTags());
      tags.append("/"+parent.getTag());
    }
    return tags.toString();
  }

  /**
   * Set the parent ElementComponent
   *
   */
  public void setParent(ElementComposite ec) {
    parent = ec;
  }

  public abstract void print();

  /**
   * extract immediate child elements and add them to the arraylist children
   */
  public abstract void populateChildren(String xml, String fname, boolean train);

  /**
   * add an xml file to the hierarchy
   * @param xml
   */
  public void addTrain(String xml, String filename) {
    addTrainingExample(xml, filename);
    populateChildren(xml, filename, true);
  }
  /**
   * false for answers
   * @param xml
   */
 /* public void addAnswer(String xml) {
    addAnswerExample(xml);
    populateChildren(xml, false);//false: for answers, true: for training examples
  }*/

  public abstract void addAnswerExample(String xml);

  /**
   * obtain the type of the component
   * @return 0 if internal node, 1 if leaf node
   */
  public int getType() {
    return type;
  }

  /**
   * set the type of the component
   * @param i
   * @return true if correct type is set, false if attempting to set a type other than 0 and 1
   */
  public boolean setType(int i) {
    if (i == 0 || i == 1) {
      type = i;
      return true;
    }
    return false;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
  this.tag = tag;
}


  public ArrayList getTrainingExamples() {
    return trainingexamples;
  }

  public Hashtable getTrainFileNames(){
    return trainfilenames;
  }
  /**
   * string
   * @param markeds
   * @todo the vectors may not be in the original order, due to the insertion of markeds from sibling node
   */
  public void addMarkeds(Vector markeds, int order) {
    if(this.markeds.size() <= order){
      this.markeds.add(markeds);
    }else{//insert to replace a placeholder
      Vector exist = (Vector)this.markeds.get(order);
      for(int i = 0 ; i < exist.size(); i++){
        if(((String)exist.get(i)).compareTo("") == 0){
          exist.remove(i);
          exist.addAll(i, markeds);
          if(parent != null){
            parent.adjustMarkeds(markeds, order, tag);
          }
        }
      }
    }
  }
  /**
   * markedsegments
   * @param markedsegments
   */
  public void addMarkedSegs(Vector markedsegments, int order) {
    if (this.markedsegs.size() <= order) {
      this.markedsegs.add(markedsegments);
    }
    else {
      Vector exist = (Vector)this.markedsegs.get(order);
      //replace a placeholder
      for (int i = 0; i < exist.size(); i++) {
        if ( ((MarkedSegment) exist.get(i)).getSegment() == null) {
          exist.remove(i);
          exist.addAll(i, markedsegments);
          //if(parent != null){
          //  parent.adjustMarkedSegs(markedsegments, order, tag);
          //}
        }
      }
    }
  }

  public void addAnswer(Vector markedsegments) {
     answers.add(markedsegments);
   }

  public abstract String[] getMarked(int index);

  public abstract void resetAnswers();
  public abstract void resetTrains();

  public abstract void resetMarkeds();
  public abstract void resetMarkedSegs();

  public void setScore(Score score) {
    this.score = score;
  }

  public Score getScore(){
    return score;
  }


  public abstract ArrayList getChildScores();

  public abstract void resetScores();

  /**
   * visitor pattern
   * @param v
   * @param algorithm
   *
   */
  public abstract void accept(ReflectiveVisitor v, String alg);
}
