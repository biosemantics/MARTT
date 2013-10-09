package visitor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import learning.LearnDelimiter;
import learning.Utilities;


/**
 * <p>Title: SegClassScore</p>
 * <p>Description: score segmentation and classification errors</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class SegClassScore
    extends Score {
  private float perfectmatch = 0f;
  private float missingcontent = 0f;
  private float extracontent = 0f;
  private float wrongclass = 0f;
  private float multipleerrors = 0f;
  private Hashtable wrongclasses = null;

  public SegClassScore() {
  }

  public SegClassScore(float perfectmatch, float missingcontent,
                       float extracontent, float wrongclass,
                       Hashtable wrongclasses, float multipleerrors) {
    this.perfectmatch = perfectmatch;
    this.missingcontent = missingcontent;
    this.extracontent = extracontent;
    this.wrongclass = wrongclass;
    this.wrongclasses = wrongclasses;
    this.multipleerrors = multipleerrors;
  }

  /**
   * perfect match, classification error, miss content, extra content and multiple errors
   * @param ec
   * @return
   */
  public Score score(ElementComponent ec) {
    ArrayList answers = ec.getAnswers();
    ArrayList marked = ec.getMarkeds();
    int size = answers.size();
    for (int i = 0; i < size; i++) {
      compare( (Vector) answers.get(i), (Vector) marked.get(i), ec, i); //updates perfectmatch, missingcontent, etc.
    }
    //divide by size
    perfectmatch = perfectmatch / size;
    missingcontent = missingcontent / size;
    extracontent = extracontent / size;
    wrongclass = wrongclass / size;
    Enumeration en = wrongclasses.keys();
    while (en.hasMoreElements()) {
      String c = (String) en.nextElement();
      float count = ( (Integer) wrongclasses.get(c)).floatValue();
      wrongclasses.put(c, new Float(count / size));
    }
    multipleerrors = multipleerrors / size;
    return new SegClassScore(perfectmatch, missingcontent, extracontent,
                             wrongclass, wrongclasses, multipleerrors);
  }

  private void compare(Vector answer, Vector marked, ElementComponent ec, int order) {
    int acount = answer.size();
    int mcount = 0;
    for(int i = 0; i < acount; i++){
      String a = Utilities.strip((String)answer.get(i)).replaceAll("\\W", "");
      mcount = marked.size();
      for(int j = 0; j < mcount; j++){
        String m = Utilities.strip((String)marked.get(j)).replaceAll("\\W", "");
        if(a.compareTo(m) == 0){
          perfectmatch++;
          marked.remove(j);
        }else if(a.matches(".*?m.*?")){
          missingcontent++;
          marked.remove(j);
        }else if(m.matches(".*?a.*?")){
          extracontent++;
          marked.remove(j);
        }else{//a and m overlap but either can cover the other completely
          if(isOverlapped(a, m)){
            multipleerrors++;
            marked.remove(j);
          }
        }
      }
      if(mcount == marked.size()){ //no match in any way
        wrongclass++;
        //update wrongclasses
        //updateWrongClasses(ec, order, a);//if a is marked up into multiple tags, score = 1/n
      }
    }
  }
  /**
   * a is not found in marked, so check ec.parent to find out its marked tags
   * @param <any>
   */
  private void updateWrongClasses(ElementComponent ec, int order, String a){
    ElementComponent parent = ec.getParent();
    String[] pmarked = parent.getMarked(order);//xml segments
    for(int i = 0; i < pmarked.length; i++){
    }
    //could have to back up multiple levels to find the tags
  }

  /**
   * if a ends with starting clause of m, return true
   * @param a
   * @param m
   * @return
   */
  private boolean isOverlapped(String a, String m){
    String[] puncs = LearnDelimiter.puncs;
    int cut = m.length()-1;
    for(int i = 0; i < puncs.length; i++){
      int index = m.indexOf(puncs[i]);
      if(index < cut){
        cut = index;
      }
    }
    return a.endsWith(m.substring(0, cut));
  }

  public String toString() {
    return null;
  }

  public boolean isGood() {
    /**@todo Implement this visitor.Score abstract method*/
    throw new java.lang.UnsupportedOperationException(
        "Method isGood() not yet implemented.");
  }

  public void reset() {
    /**@todo Implement this visitor.Score abstract method*/
  }

  public boolean isZero() {
    /**@todo Implement this visitor.Score abstract method*/
    throw new java.lang.UnsupportedOperationException(
        "Method isZero() not yet implemented.");
  }

  public Score addition(Score parm1) {
    /**@todo Implement this visitor.Score abstract method*/
    throw new java.lang.UnsupportedOperationException(
        "Method addition() not yet implemented.");
  }

  public void divideBy(int k) {
    /**@todo Implement this visitor.Score abstract method*/
  }

}