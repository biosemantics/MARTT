package visitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Vector;
import xmlsimilarity.WordBasedCosineSimilarity;
/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 *
 * <p>Description: Thesis Project, everything in this project</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: UIUC</p>
 *
 * @author not attributable
 * @version 0.1
 */

/**
 * not used as a class.
 * see PRSScore for implementation of score simScore.
 *
 */
public class SimScore
    extends Score {
  private float sim = 0;

  public SimScore() {
    super();
  }

  public float value(){
    return sim;
  }

  public void setValue(float value){
    sim = value;
  }

  /**
   * addition
   *
   * @param s Score
   * @return Score
   * @todo Implement this visitor.Score method
   */
  public Score addition(Score s) {
    SimScore score = new SimScore();
    score.setValue(sim + ((SimScore)s).value());
    return score;
  }

  /**
   * divideBy
   *
   * @param k int
   * @todo Implement this visitor.Score method
   */
  public void divideBy(int k) {
    sim = sim/k;
  }

  /**
   * isGood
   *
   * @return boolean
   * @todo Implement this visitor.Score method
   */
  public boolean isGood() {
    return (!isZero() && Float.compare(sim, Float.NaN) != 0);
  }

  /**
   * isZero
   *
   * @return boolean
   * @todo Implement this visitor.Score method
   */
  public boolean isZero() {
    return Float.compare(sim, 0f) == 0;
  }

  /**
   * reset
   *
   * @todo Implement this visitor.Score method
   */
  public void reset() {
    sim = 0f;
  }

  /**
   * score
   *
   * @param ec ElementComponent
   * @return Score
   * @todo Implement this visitor.Score method
   */
  public Score score(ElementComponent ec) {
    String tag = ec.getTag();
    //1. create an XML file from answers
    ArrayList list = ec.getAnswers();
    String ansxml = constructXml(list, tag);
    //2. do the same for markeds
    list = ec.getMarkeds();
    String markedsxml = constructXml(list, tag);
    //3. find the wbcs between the two files
    WordBasedCosineSimilarity wbcs = new WordBasedCosineSimilarity(WordBasedCosineSimilarity.VERIFICATION);
    sim = wbcs.compute(learning.Utilities.removeTaxon(ansxml), learning.Utilities.removeTaxon(markedsxml));
    return this;
  }

  private String constructXml(ArrayList list, String tag){
    String root = "roooooot";
    String xml = "<"+root+">";
    Iterator it = list.iterator();
    while(it.hasNext()){
      Vector insts = (Vector)it.next();
      Enumeration en = insts.elements();
      while(en.hasMoreElements()){
        String seg = (String)en.nextElement();
        if(seg.replaceAll("^\\s+","").indexOf("<") != 0){
         seg = "<missing"+tag+">"+seg+"</missing"+tag+">";
        }
        xml += seg;
      }
    }
    xml += "</"+root+">";
    return xml;
  }

  public String toString() {
    String output = ":"+sim + ":(s)";
    return output;
  }

  public static void main(String[] args) {
    SimScore simscore = new SimScore();
  }
}
