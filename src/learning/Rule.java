package learning;

import java.io.Serializable;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * <p>Title: Rule</p>
 * <p>Description: Rule can be sorted in decendent order of its support</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class Rule implements Comparator, Serializable{
  String body = "";
  float confidence = -1;
  float support = -1;

  public Rule(){

  }
  public Rule(String body, float sup, float conf) {
    this.body = body;
    this.confidence = conf;
    this.support = sup;
  }

  public float getConfidence(){
      return confidence;
    }

  public float getSupport(){
    return support;
  }

  public String getBody(){
    return body;
  }
  public int compare(Object o1, Object o2){
    int result = -1*(new Float(((Rule)o1).getSupport())).compareTo(new Float(((Rule)o2).getSupport()));
    if(result != 0){
      return result;
    }else{
      return ((Rule)o1).getBody().compareTo(((Rule)o2).getBody());
    }
  }

  public boolean equals(Object o){
    return false;
  }

  public String toString(){
    StringBuffer sb = new StringBuffer();
    sb.append(body + "\t" + support + "\t" +confidence);
    return sb.toString();
  }

  public static void main(String[] args) {

    Rule rule1 = new Rule("red", 0.7f, 0.5f);
    Rule rule2 = new Rule("red", 0.7f, 0.8f);
    TreeSet ts = new TreeSet(rule1);
    ts.add(rule1);
    ts.add(rule2);
    System.out.println(((Rule)ts.first()).toString());
  }

}