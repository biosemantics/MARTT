package miner;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.regex.*;
import learning.Utilities;

/**
 * <p>Title: RelativePosition</p>
 * <p>Description: learn the relative postion of elements.
 *                 answer questions such as the probability of e1 occurs n position
 *                 after e2</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */

public class RelativePosition implements Serializable{
  private String[] classes = null; //all possible unique elements
  private Hashtable[] positions = null; //probabilities, one for each class
  private int[] classcount = null;
  private Hashtable[] positioncounts = null;
  private Hashtable selftransfer = null; //Max.likelihood of n self transfer for each class
  public final static String START = "START";
  public final static String END = "END";
  public final static String[] delim = new String[]{ ";",".",":"};

  public RelativePosition(String[] trainingexamples, String[] classes) {
    this.selftransfer = new Hashtable();
    this.classes = new String[classes.length+2];
    this.classes[0] = START;
    this.classes[1] = END;
    for(int i = 0; i < classes.length; i++){
      this.classes[i+2] = classes[i];
    }
    String[][] elements = getElementMatrix(trainingexamples);
    calculateInterElem(elements);
    calculateInnerElem(); //selftransfer
  }

  public RelativePosition(RelativePosition rp){
    this.classcount = rp.getClassCount();
    this.classes = rp.getClasses();
    this.positioncounts = rp.getPositionCounts();
    this.positions = rp.getPositions();
    this.selftransfer = rp.getSelfTransfer();
  }
  /**
   * example by element, elements are not aligned.
   * @param examples
   * @return
   */
  private String[][] getElementMatrix(String[] examples) {
    String[][] matrix = new String[examples.length][];
    for (int f = 0; f < examples.length; f++) {
      Pattern p = Pattern.compile("^\\s*<(.*?)>(.*?)</\\1>\\s*$");
      Matcher m = p.matcher(examples[f]);
      String text = null;
      if(m.matches()){
        text = m.group(2);
      }else{
        text = examples[f];
      }
      Vector v = Utilities.getTagList(text, delim);
      ArrayList tags = (ArrayList)v.get(0);
      Hashtable repeat = (Hashtable)v.get(1);
      tags.add(0, START);
      tags.add(tags.size(),END);
      matrix[f] = (String[]) tags.toArray(new String[0]);
      updateSelfTransfer(repeat);
    }
    return matrix;
  }

  /**
   * add counts from repeat to selftransfer
   * @param repeat Hashtable
   */

  private void updateSelfTransfer(Hashtable repeat){
    Enumeration en = repeat.keys();
    while(en.hasMoreElements()){
      String tag = (String)en.nextElement();
      selftransfer.put(tag,
                   selftransfer.get(tag) == null ? repeat.get(tag) :
                   ( (String) selftransfer.get(tag) + " "+(String) repeat.get(tag)));
    }
  }

  /**
   * turn raw counts in selftransfer to likelihood
   * e.g. the probability of self tranfer for up to n times
   */
  private void calculateInnerElem(){
    Enumeration en = selftransfer.keys();
    while(en.hasMoreElements()){
      String tag = (String) en.nextElement();
      String counts = (String)selftransfer.get(tag);
      selftransfer.put(tag, counts2prob(counts));
    }
  }
  /**
   * the probability of self tranfer for at least n times
   * @param counts String
   * @return Hashtable
   */
  private Hashtable counts2prob(String counts){
    Hashtable prob = new Hashtable();
    String[] cnts = counts.trim().split(" ");
    Integer[] icnts = new Integer[cnts.length];
    for(int i = 0; i < cnts.length; i++){//convert to Integer
      icnts[i] = new Integer(Integer.parseInt(cnts[i]));
    }
    Arrays.sort(icnts);
    ArrayList list = new ArrayList(Arrays.asList(icnts));//with sorted elements
    int len = list.size();
    for(int i = len-1; i >=0; ){
      Object o = list.get(i);//o is an Integer
      int first = list.indexOf(o);
      float pr = (len - first)/(float)len;
      prob.put(o, new Float(pr));
      i = first - 1;
    }
    return prob;
  }

  /**
   * access method
   * e.g. probability of element self transfer for at least n times?
   * @return float
   */
  public float selfProbability(String tag, int repeat){
    Hashtable h = (Hashtable)selftransfer.get(tag);
    Object pr = h.get(new Integer(repeat));
    if(pr != null){
      return ((Float)pr).floatValue();
    }
    //if "exact n time" find no match, try [-window,+window] range
    int window = h.size();
    float pf = -1f;
    for(int i = 1; i <= window; i++){
      Integer index = new Integer(repeat+i);
      Object ppr = h.get(index);
      if (ppr != null) {
        pf = ( (Float) ppr).floatValue();
        break;
      }
    }
    return pf;
  }

  public Hashtable[] getPositions(){
    return positions;
  }

  public Hashtable[] getPositionCounts(){
    return positioncounts;
  }

  public int[] getClassCount(){
    return classcount;
  }

  public String[] getClasses(){
    return classes;
  }

  public Hashtable getSelfTransfer(){
    return selftransfer;
  }

  public void setSelfTransfer(Hashtable self) {
    this.selftransfer = self;
  }

  public void setPositionCounts(Hashtable[] positioncounts){
    this.positioncounts = positioncounts;
  }

  public void setClassCount(int[] classcount){
    this.classcount = classcount;
  }

  public void setClasses(String[] classes){
    this.classes = classes;
  }


  public void printPositions(Hashtable[] positions){
    for(int i = 0; i<positions.length; i++){
      System.out.println();
      System.out.println(classes[i]);
      Hashtable hclass = positions[i];
      Enumeration pos = hclass.keys();
      while(pos.hasMoreElements()){
        Integer rp = (Integer)pos.nextElement();
        System.out.println("\n"+rp.intValue());
        Hashtable hrp = (Hashtable)hclass.get(rp);
        Enumeration cls = hrp.keys();
        while(cls.hasMoreElements()){
          String label = (String)cls.nextElement();
          System.out.print("\t"+label+((Double)hrp.get(label)).doubleValue());
        }
      }
    }
  }

  /**
   * for ci
   * hashtable key=relative position (+n -n), +n n position after, -n n position before
   *           value = hashtable key = class cj
   *                             value = the probability of cj exact relative-position away from ci
   * @return an array of hashtable, one element for a class
   */
  private void calculateInterElem(String[][] elements) {
    this.classcount = new int[classes.length];
    this.positioncounts = new Hashtable[classes.length];
    for(int i = 0; i < classes.length; i++){
      this.positioncounts[i] = new Hashtable();
    }

    for (int i = 0; i < elements.length; i++) {
      int[] indices = translate(elements[i]);
      process(elements[i], indices);
      updateCounts(indices);
    }
    findProbabilities();
  }

  /**
   * do one instance
   * at the same time update classcount
   * (one occurance of an element may be counted multiple times because of multiple occurance of another element).
   * @param elemlist
   * @param relpos
   */
  private void process(String[] elemlist, int[] indices) {
    int len = elemlist.length;
    String done = "";
    boolean addCount = false;

    for (int i = 0; i < len; i++) { //j relative to i
      Hashtable classi = positioncounts[indices[i]];
      if(done.indexOf(" "+elemlist[i]+" ") >= 0){
        addCount = true;
      }else{
        addCount = false;
        done += " " + elemlist[i] + " ";
      }
      for (int j = 0; j < len; j++) {
        Integer pos = j - i == 0 ? null : new Integer(j - i);
        if (pos != null) {
          classi = increment(classi, pos, elemlist[j]);
          if(addCount){
            classcount[indices[j]]++;
          }
        }
      }
    }
  }

  private Hashtable increment(Hashtable classi, Integer pos, String classj) {
    if (classi.containsKey(pos)) {
      Hashtable classtable = (Hashtable) classi.get(pos);
      if (classtable.containsKey(classj)) {
        Integer count = (Integer) classtable.get(classj);
        classtable.put(classj, new Integer(count.intValue() + 1));
      }
      else {
        classtable.put(classj, new Integer(1));
      }
    }
    else {
      Hashtable newone = new Hashtable();
      newone.put(classj, new Integer(1));
      classi.put(pos, newone);
    }
    return classi;
  }

  private void findProbabilities() {
    //make a mem copy for postions from position count by using clone()
    positions = new Hashtable[classes.length];
    for (int i = 0; i < classes.length; i++) {
      positions[i] =(Hashtable)positioncounts[i].clone();
      Hashtable classi = positions[i];
      Enumeration poselems = classi.keys();
      while (poselems.hasMoreElements()) {
        Object key = poselems.nextElement();
        Hashtable classcounts = (Hashtable)((Hashtable) classi.get(key)).clone();
        Enumeration classs = classcounts.keys();
        while (classs.hasMoreElements()) {
          String label = (String) classs.nextElement();
          Integer freq = (Integer) classcounts.get(label);
          classcounts.put(label, new Float(freq.floatValue() / classcount[i]));
          classi.put(key, classcounts);
        }
      }
    }
  }

  private void updateCounts(int[] updates) {
    for (int i = 0; i < updates.length; i++) {
      classcount[updates[i]]++;
    }
  }

  /**
   * translate labels to class indices
   * @param elemlist
   * @return
   */
  private int[] translate(String[] elemlist) {
    int[] indices = new int[elemlist.length];
    for (int i = 0; i < elemlist.length; i++) {
      indices[i] = getClassIndex(elemlist[i]);
    }
    return indices;
  }

  private int getClassIndex(String label) {
    for (int i = 0; i < classes.length; i++) {
      if (label.compareTo(classes[i]) == 0) {
        return i;
      }
    }
    //System.err.println(label+ ": tag is not in the class list");
    return -1;
    /**@todo check to make sure classes include all possible elements)*/
  }

  /**
   * probability of label2 occurs exact "pos" distance away from pivot
   * @param pivot
   * @param label2
   * @param pos
   * @return
   */
  public float probability(String pivot, int pos, String label2){
    //float prob = 0d;
    int occur = 0;
    int indexp = getClassIndex(pivot);
    if(indexp < 0){
      return -1f;
    }
    Hashtable classp = positioncounts[indexp];
    Hashtable elements = (Hashtable) classp.get(new Integer(pos));
    if (elements != null) {
      Object o = elements.get(label2);
      if (o != null) {
        occur += ( (Integer) o).intValue();
      }
    }
    return (float) occur / classcount[indexp];
  }


  /**
   * probability of label2 occurs within pos distance away from pivot
   * @param pivot
   * @param label2
   * @param pos
   * @return
   */
  public float probabilityInRange(String pivot, int pos, String label2){
    //float prob = 0d;
    int occur = 0;
    int indexp = getClassIndex(pivot);
    if(indexp < 0){
      return -1f;
    }
    Hashtable classp = positioncounts[indexp];
    int dir = pos > 0? 1 : -1;
    for(int i = pos*dir; i > 0; i--){
      Hashtable elements = (Hashtable)classp.get(new Integer(i*dir));
      if(elements != null){
        Object o = elements.get(label2);
        if (o != null) {
          occur += ( (Integer) o).intValue();
        }
      }
    }
    //float pr = (float)occur / classcount[getClassIndex(label2)];
    float pr = (float)occur / classcount[indexp];
    if(pr -1 > 0){
      System.err.println("one more");
    }
    return pr;

  }
  /**
     * get all elements that appear within pos position relative to pivot,
     * sort the element by the probabilities
     * @param pivot
     * @param pos +/-, + : after -:before
     * @param prob
     * @return
     */
    public Vector getNeighborElements(String pivot, int pos) {
      Vector elements = new Vector();
      int classindex = getClassIndex(pivot);
      if(classindex == -1){
        return null;
      }
      Hashtable classp = positions[classindex];
      ArrayList problist = elementsInRange(classp, pos);
      Iterator it = problist.iterator();
      StringBuffer labelsb = new StringBuffer();
      while(it.hasNext()){
        Hashtable probs = (Hashtable) it.next();
        Enumeration labels = probs.keys();
        while (labels.hasMoreElements()) {
          String label = (String) labels.nextElement();
          float p = ( (Float) probs.get(label)).floatValue();
          Pair pair = new Pair(label, p);
          if (labelsb.toString().indexOf(" "+label+" ") < 0) {
            insertElementByProb(elements, pair);
            labelsb.append(" ").append(label).append(" ");
          }
        }
      }
      return elements;
    }

    private void insertElementByProb(Vector elements, Pair pair){
      int size = elements.size();
      if(size == 0){
        elements.add(pair);
        return;
      }
      for(int i = 0; i < size; i++){
        Pair p = (Pair)elements.get(i);
        if(Double.compare(p.getProb(), pair.getProb()) < 0){
          elements.insertElementAt(pair, i);
          return;
        }
      }
      elements.add(pair);
    }
  /**
   * get all elements that appear within pos position relative to pivot with probability > prob
   * @param pivot
   * @param pos +/-, + : after -:before
   * @param prob
   * @return
   */
  public Vector getLikelyNeighborElements(String pivot, int pos, float prob) {
    Vector elements = new Vector();
    int classindex = getClassIndex(pivot);
    Hashtable classp = positions[classindex];
    ArrayList problist = elementsInRange(classp, pos);
    Iterator it = problist.iterator();
    while(it.hasNext()){
      Hashtable probs = (Hashtable) it.next();
      Enumeration labels = probs.keys();
      while (labels.hasMoreElements()) {
        String label = (String) labels.nextElement();
        float p = ( (Float) probs.get(label)).floatValue();
        if (Double.compare(p, prob) >= 0 && !elements.contains(label)) {
          elements.add(label);
        }
      }
    }
    return elements;
  }
  /**
   * key = [0, pos]  or [pos, 0]
   * return all the corresponding elements of "position"
   * @param position
   * @param pos
   * @return
   */
  private ArrayList elementsInRange(Hashtable position, int pos){
    ArrayList list = new ArrayList();
    int dir = pos > 0? 1: -1;
    for(int i = pos*dir; i > 0; i--){
      list.add(position.get(new Integer(i*dir)));
    }
    return list;
  }


  public static void main(String[] argv) {
    String[] classes = {
        "taxon", "plant-habit-and-life-style", "roots", "stems", "buds",
        "leaves", "inflorescences", "flowers", "pollen", "fruits", "cones",
        "seeds", "spore-related-structures", "gametophytes", "chromosome",
        "phenology", "compound", "other-features", "other-information"};
    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level1\\trainingdir-fna500\\";
    /*String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level1\\relative-position-test\\";
    File srcdir = new File(dir);
    File[] files = srcdir.listFiles();
    int n = files.length;
    String[] examples = new String[n];
    for (int f = 0; f < files.length; f++) {
      examples[f] = Utilities.readFile(files[f]);
    }*/
    String[] examples = new String[2];
    RelativePosition rp = new RelativePosition(examples, classes);
    rp.printPositions(rp.getPositions());
    Vector elems = rp.getNeighborElements("START", 1);
    System.out.println();
    System.out.println("first element are:");
    Enumeration en = elems.elements();
    while(en.hasMoreElements()){
      System.out.println(((Pair)en.nextElement()).toString());
    }
    System.out.println(rp.probabilityInRange("fruits", -10, "taxon"));
  }

}
