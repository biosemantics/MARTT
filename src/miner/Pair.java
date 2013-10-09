package miner;

/**
 * <p>Title: Pair</p>
 * <p>Description: used with RelativePosition to hold tag and it's probability of occuring at
 * certain position.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

//other query methods
public class Pair{
  Object tag = null;
  float prob = 0f;

  public Pair(Object label, float prob){
    this.tag = label;
    this.prob = prob;
  }

  public float getProb(){
    return this.prob;
  }

  public Object getTag(){
    return this.tag;
  }

  public String toString(){
    return tag.toString()+":"+prob;
  }
}
