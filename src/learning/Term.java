package learning;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Hashtable;
/**
 * <p>Title: Learning</p>
 * <p>Description: Learning algorithms for marking up</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

/**
 * Term is a node in a BinarySearchTree. The distribution of classes across the
 * examples containing the term. and total examples containing the term.
 */
public class Term implements Comparator, Comparable, Serializable, Cloneable{
  private Hashtable scores = null;
  private String term = null;
  private String[] classes = null;
  private int[] count = null; //occurance of classes
  //private int total = 0;

  public Term(){
  }

  public Term(String term){
    this.term = term;
  }

  /*public Term(String term, float score, String[] classes){
    this.term = term;
    this.scores = new float[classes.length];
    this.classes = classes;
  }*/

  public Term(String term, String label, String[] classes) {
    this.term = term;
    this.classes = classes;
    //this.scores = new float[classes.length];
    this.scores = new Hashtable();
    this.count = new int[classes.length];
    for(int i = 0; i< classes.length; i++){
      if(classes[i].compareTo(label) == 0){
        count[i] = 1;
      }else{
        count[i] = 0;
      }
    }
    //total++;
  }

  public Object clone(){
    Term clone = new Term();
    clone.setClasses(classes);
    clone.setCount(count);
    clone.setScores(scores);
    clone.setTerm(term);
    //clone.setTotal(total);
    return clone;
  }

  public void setClasses(String[] classes) {
    this.classes = classes;
  }

  public void setCount(int[] count) {
    this.count = count;
  }

  public void setScores(Hashtable scores) {
    this.scores = scores;
  }

  public void setTerm(String term) {
    this.term = term;
  }

  /*public void setTotal(int total) {
    this.total = total;
  }*/


  public String getTerm(){
    return term;
  }

  public void setCountForClass(int count, String label){
    for (int i = 0; i < classes.length; i++) {
      if (label.compareTo(classes[i]) == 0) {
        //int diff = this.count[i] - count;
        //total = total - diff;
        this.count[i] = count;
        return;
      }
    }
    //new class
    StringBuffer sb = new StringBuffer();
    int[] temp = new int[this.count.length+1];
    int i = 0;
    for(; i < classes.length; i++){
      sb.append(classes[i]).append(" ");
      temp[i] = this.count[i];
    }
    temp[i] = count;
    this.count = temp;
    //this.total += count;
    sb.append(label);
    this.classes = sb.toString().split(" ");
    int product = 1;
    for(int j = 0; j < this.count.length; j++){
      product *= this.count[j];
    }
    }

  public void setScore(float score, String label){
    /*for(int i = 0; i < scores.length; i++){
      if(classes[i].compareTo(label) == 0){
        scores[i] = score;
      }
    }*/
    scores.put(label, new Float(score));
  }

  public void setScore(Object score, String label){
    scores.put(label, score);
  }

  public Object getScore(){
    return scores;
  }

  public float getScore(int c){
    //return scores[c];
    return ((Float)scores.get(classes[c])).floatValue();
  }

  public Object getScore(String label){
      //return scores[c];
      return scores.get(label);
    }

  public int[] getClassCount(){
    return count;
  }

  public void addOccuranceIn(String label){
    for(int i = 0; i< classes.length; i++){
       if(classes[i].compareTo(label) == 0){
         this.count[i] += 1;
         break;
       }
     }
     //total++;
  }

  public int getOccuranceIn(String label){
    int i;
    for( i = 0; i< classes.length; i++){
       if(classes[i].compareTo(label) == 0){
         return count[i];
       }
     }
     return 0;
  }

  public int getTotalOccurance(){
    int sum = 0;
    for(int i = 0; i< classes.length; i++){
      sum += count[i];
    }
    return sum;
    //return total;
  }

  public String printScore(){
    return term+" "+((Float)getScore(term)).floatValue()+"\n";
  }

  public String toString(){
    StringBuffer sb = new StringBuffer();
    sb.append(term+"\t");
    for(int i = 0; i< classes.length; i++){
      if (getOccuranceIn(classes[i]) > 0) {
        sb.append(classes[i] + "=" + getOccuranceIn(classes[i]) + "\n\t\t");
      }
    }
    sb.append("\n");
    return sb.toString();
   }
   /**
    * for interface Comparator
    * @param bBterm
    * @param aBterm
    * @return
    */
  public int compare(Object bTerm, Object aTerm){
    return ((Term)bTerm).getTerm().compareTo(((Term)aTerm).getTerm());
  }

  /**
   * for interface Comparator
   * @param aTerm
   * @return
   */
  public boolean equals(Object aTerm){
    return term.equals(((Term)aTerm).getTerm());
  }
  /**
   * for interface Comparable
   * @param o
   * @return
   * @throws java.lang.ClassCastException
   */
  public int compareTo(Object o) throws ClassCastException{
    Term t = (Term)o;//if cast fails, throws exception
    return term.compareTo(t.getTerm());
  }

  public static void main(String[] args) {
    Term BTerm1 = new Term("six");
    Term BTerm2 = new Term("five");
    System.out.println((new Term()).compare(BTerm1, BTerm2));
  }

}
