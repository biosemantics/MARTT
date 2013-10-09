package visitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.*;
import java.util.Vector;

/**
 * <p>Title: XML Hierarchy Using Visitor Pattern</p>
 * <p>Description: The leaf class in the Composite Pattern</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class ElementLeaf
    extends ElementComponent {
  private int type = 1;
  private ArrayList children = null;

  public ElementLeaf(String xml, String fname, boolean train) {
    super(xml,fname, train);
    children = null;
  }

  public ElementLeaf() {
      super();
      children = null;
    }

  public void addTrainingExample(String xml, String fname){
    String text = null;
    Matcher m = Pattern.compile("<(.*?)>(.*?)</\\1>\\s*$").matcher(xml);
    if (m.lookingAt()) {
      tag = m.group(1);
      text = m.group(2);
    }
    if(text!=null){
      text = text.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
      trainingexamples.add(text);
      //do not need filenames in leaf nodes because they do not do markup.
      //add filenames in leaf nodes for unsupervised learning and visaulization, may, 2006
      if (trainfilenames.containsKey(fname)) {
      ( (ArrayList) trainfilenames.get(fname)).add(text);
    }
    else {
      ArrayList exps = new ArrayList();
      exps.add(text);
      trainfilenames.put(fname, exps);
    }

    }
  }

  public void populateChildren(String xml, String fname, boolean train) {
    children = null;
  }

  public Iterator iterator() {
    return null;
  }

  public boolean removeChild(ElementComponent ec) {
    return false;
  }

  /**
   * find in the children an elementcomponent with tag
   * @param tag
   * @return null if not found, otherwise return the elementcomponent
   */
  public ElementComponent findChild(String tag) {
    return null;
  }

  /**
   * @todo it's possible we need to convert a leaf to a composite
   * @param ec
   * @return
   */
  public boolean addChild(ElementComponent ec) {
    return false;
  }

  public void print() {
    Iterator it = trainingexamples.iterator();
    while (it.hasNext()) {
      System.out.println(it.next());
    }
  }

  /**
   *
   * @return
   */
  public String[] getMarked(int index) {
    if(markeds.size() > index){
      Vector v = (Vector)markeds.get(index);
      String [] marked = new String[v.size()];
      for(int i = 0; i < marked.length; i++){
        String content = (String)v.get(i);
        if(content.trim().compareTo("") != 0){
          marked[i] = "<" + tag + ">" + (String) v.get(i) + "</" + tag + ">";
        }else{
          marked[i] = "";
        }
      }
      return marked;
    }else{
      System.err.println("Ask for marked example(s) that are out of array boundary");
      return null;
    }
  }

  public void addAnswerExample(String xml){
    answers.add(xml);
  }

  public ArrayList getChildClasses(){
    return null;
  }

  public ArrayList getChildSimpleClasses(){
    return null;
  }

  public ArrayList getAllClasses(){
    ArrayList classes = new ArrayList();
    classes.add(tag);
    return classes;
  }

  public ArrayList getAllScores(){
    ArrayList scores = new ArrayList();
    String size = ""+trainingexamples.size();
    scores.add(new MapEntry(getParentTags()+"/"+tag, score, size));
    return scores;
  }

  public ArrayList getChildScores(){
    return null;
  }

  public void resetScores(){
    score = null;
  }

  public void resetAnswers() {
    answers = new ArrayList();
  }

  public void resetTrains() {
      trainingexamples = new ArrayList();
    }

  public void resetMarkeds() {
    markeds = new ArrayList();
  }
  public void resetMarkedSegs() {
    markedsegs = new ArrayList();
  }

  public void accept(ReflectiveVisitor visitor, String alg){
    visitor.dispatch(this, alg);
  }
  /*public static void main(String[] args){
    String xml ="<example>a short string in example tag</example>";
    ElementLeaf el  = new ElementLeaf(xml);
    System.out.println("tag: "+el.getTag());
    System.out.println("text: "+el.getText());
    System.out.println("parent: "+el.getParent());
    el.print();
     }*/
}
