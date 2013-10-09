package learning;

import miner.SemanticLabel;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * <p>Title: MarkedSegment</p>
 * <p>Description: encapsulate the segment and it markup decision procedure, SemanticLable</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class MarkedSegment {
  private String segment = null;
  private ArrayList label = null;

  public MarkedSegment() {
  }

  public MarkedSegment(String segment, Tag label){
    this.segment = segment;
    this.label = new ArrayList();
    this.label.add(label);
  }

  public String getSegment(){
    return segment;
  }

  public ArrayList getLabel(){
    return this.label;
  }

  public void setSegment(String segment){
    this.segment = segment;
  }

  /**
   * set the first label
   */
  public void setLabel(int i, SemanticLabel label){
    this.label.set(i, label);
  }

  public void setLabels(ArrayList label){
    this.label = label;
  }


  public void addLabel(SemanticLabel label){
    this.label.add(label);
  }

  public String toString(){
    StringBuffer sb = new StringBuffer();
    Iterator it = this.label.iterator();
    while(it.hasNext()){
      SemanticLabel l = (SemanticLabel)it.next();
      sb.append("[" + segment + "] => " + l.toString()).append(" ");
    }
    return sb.toString().trim();
  }
}