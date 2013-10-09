package knowledgebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import miner.RelativePosition;
import miner.TermSemantic;
import jds.collection.BinarySearchTree;
import learning.Term;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import miner.SCScore;

/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: Thesis Project, everything in this project</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class Composite extends Component implements Serializable{
  public Composite() {
  }

  public Composite(String tag, Composite parent, RelativePosition rp,
                   TermSemantic ts, int n,
                   float sup, float conf) {
    super(tag, parent, rp, ts, n, sup, conf);
  }

  public void pruneTS(float sup, float conf){
    BinarySearchTree[] trees = ts.getSemantics();
    for (int i = 0; i < trees.length; i++) {
      BinarySearchTree newtree = pruneTree(trees[i], sup, conf);
      trees[i] = newtree;
    }
    //children
    Iterator it = children.iterator();
    while(it.hasNext()){
      Composite com = (Composite)it.next();
      com.pruneTS(sup, conf);
    }
  }


  private BinarySearchTree pruneTree(BinarySearchTree btree, float sup, float conf){
  BinarySearchTree newtree = new BinarySearchTree(new Term());
  int s = 200;
  ArrayList[] terms = new ArrayList[s];//reshuffling of the terms to avoid stackoverflow problem
  for(int i = 0; i < terms.length; i++){
    terms[i] = new ArrayList();
  }
  Enumeration en = btree.elements();
  int i = 0;
  while(en.hasMoreElements()){
    Term t = (Term)en.nextElement();
    Hashtable scores = (Hashtable)t.getScore();
    if(qualify(scores, sup, conf)){
      terms[i%s].add(t);
      i++;
    }
  }
  //insert terms to trees
  int[] order = randomOrder(s);
  for(int j = 0; j < s; j++){
    Iterator it = terms[j].iterator();
    while(it.hasNext()){
      Term tm = (Term)((Term)it.next()).clone();
      newtree.addElement(tm);
    }
  }
  return newtree;
}

/**
 * put 0 - size-1 numbers in random order
 * @param scores
 * @return
 */
private static int[] randomOrder(int size){
  int[] order = new int[size];
  StringBuffer sb = new StringBuffer();
  Random rand = new Random();
  for(int i = 0; i < size; ){
    int r = rand.nextInt(size);
    if(sb.toString().indexOf(" "+r+" ") < 0){
      order[i] = r;
      i++;
      sb.append(" ").append(r).append(" ");
    }
  }
  return order;
}
/**
 * if there is one score in scores is above the thresholds, it is qualified
 * set scores for other classes to be zero.
 * @param scores
 * @return
 */
private static boolean qualify(Hashtable scores, float sup, float conf){
  boolean q = false;
  Enumeration en = scores.elements();
  while(en.hasMoreElements()){
    SCScore s = (SCScore)en.nextElement();
    if(Double.compare(s.getConfidence(), conf) >= 0 && Double.compare(s.getSupport(), sup)>=0){
      q = true;
    }else{
      s.setConfidence(0f);
      s.setSupport(0f);
    }
  }
  return q;
}



  public Component getChild(String tag) {
    Iterator it = children.iterator();
    while (it.hasNext()) {
      Component c = (Component) it.next();
      if (c.getTag().compareToIgnoreCase(tag) == 0) {
        return c;
      }
    }
    return null;

  }

  public boolean addChild(Component child) {
    return children.add(child);
  }


  public ArrayList getChildren() {
    return children;
  }

  public void removeChild(String tag) {
      Component com = getChild(tag);
      children.remove(com);
    }


}