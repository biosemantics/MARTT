package miner;

import java.io.Serializable;
/**
 * <p>Title: Miner</p>
 * <p>Description: Learn/mine domain knowledge and conventions</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */

public class SCScore implements Serializable{
  private float support = 0;
  private float confidence = 0;

  public SCScore(float support, float confidence) {
    this.support = support;
    this.confidence = confidence;
  }

  public SCScore(SCScore s){
    this.support = s.getSupport();
    this.confidence = s.getConfidence();
  }
  public float getSupport() {
    return support;
  }

  public float getConfidence() {
    return confidence;
  }

  public void setSupport(float support) {
    this.support = support;
  }

  public void setConfidence(float confidence) {
    this.confidence = confidence;
  }

  public String toString(){
    java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
    return "conf="+df.format(confidence)+" sup="+df.format(support);
  }
}