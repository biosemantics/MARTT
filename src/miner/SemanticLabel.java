package miner;

import learning.Tag;
/**
 * <p>Title: SemanticLabels</p>
 * <p>Description: pack the decision procedure of a semantic label of a N-gram,
 *                 including the label, contributing n-gram/sub-gram, conf and sup
 *                 of the semantic relationship</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class SemanticLabel extends Tag{

  private String label = null;
  private String ngram = null; //transformed
  private String ongram = null;//original
  private float conf = 0f;
  private float sup = 0f;
  private String type = null;
  /*
   B: for instance-based,
   D: for default,
   I: for misplaced element,
   M: for model match,
   P: for pattern match,
   R: for left-right,
   S: for semantic class,
   X: for fix sandwich/interlace*/

  public SemanticLabel() {
  }

  public SemanticLabel(String ongram, String ngram, String label, float conf, float sup, String type){
    this.ongram = ongram;
    this.ngram = ngram;
    this.label = label;
    this.conf = conf;
    this.sup = sup;
    this.type = type;
  }

  public String getOngram(){
    return ongram;
  }

  public String getNgram(){
    return ngram;
  }

  public String getTag() {
    return label;
  }

  public float getConf() {
    return conf;
  }

  public float getSup() {
    return sup;
  }

  public String getType(){
      return type;
  }

  public void setOngram(String ongram) {
    this.ongram = ongram;
  }

  public void setNgram(String Ngram) {
    this.ngram = ngram;
  }

  public void setConf(float conf) {
    this.conf = conf;
  }

  public void setSup(float sup) {
    this.sup = sup;
  }

  public void setTag(String label) {
    this.label = label;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String toString() {
    java.text.DecimalFormat format = new java.text.DecimalFormat("0.00");
    return ngram + "[" + ongram + "]:" + label + "[" + format.format(conf) + "/" + format.format(sup) + "]" +
        type;
  }
}
