package structure;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import org.w3c.dom.*;


/**
 * <p>Title: StructureMeasure</p>
 * <p>Description: A measure of structuredness of flora corpus</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

/**
 * score the regularity of missing pattern of the fields for a level
 * Input: a sample of marked up segments of a level
 *        a complete list of all possible fields that may appear at this level
 * Output: a score in [0,1]
 *
 * 1. for each segments in the collection find the missing fields
 * 2. count occurance of each missing pattern
 * 3. find info entropy of the distribution of the missing pattern
 * 4. max info entropy is obtained when all possible(combinations) of missing patterns have an uniform distribution
 * 4. return 1 - entropy / max entropy
 */
public class ClassPresence extends MeasureAlgorithm{

  public ClassPresence(String[] instances, String[] classes) {
    super(instances, classes);
  }

  public float score (){
    List classlist = Arrays.asList(classes);
    HashSet standard = new HashSet(classlist);
    Hashtable difference = new Hashtable();
    //read files
    for (int f = 0; f < instances.length; f++) {
      HashSet thisclasses = getFields(instances[f]);
      HashSet standclasses = (HashSet) standard.clone();
      standclasses.removeAll(thisclasses); //after this, standclass is the difference between the two
      if (difference.containsKey(standclasses)) { //increment count
        difference.put(standclasses,
                       new Integer(1 +
                                   ( (Integer) difference.get(standclasses)).intValue()));
      }
      else {
        difference.put(standclasses, new Integer(1));
      }
    }
    //printDifferenceHash(difference);

    Object[] count = difference.values().toArray();
    int[] diffcount = new int[count.length];
    for (int c = 0; c < count.length; c++) {
      diffcount[c] = ( (Integer) count[c]).intValue();
    }
    float score = learning.Utilities.getEntropy(diffcount);
    //find max entropy for this collection
    int combs = combinations();
    combs = combs == 0? 1 : combs;

    int occur = 0;
    int total = instances.length;
    if(combs > instances.length){
      occur = 1;
    }else{
      occur = instances.length / combs;
    }
    float p = (float)occur / total;
    float max =   (float)(-1d *( p * Math.log(p) / Math.log(2d))*total);
    max = Float.compare(max, -0.0f) == 0? 0f: max;
    if(score/max > 1 || score/max < 0){
      System.out.print("");
    }
    return 1 - score/max;
  }
  /**
   * different missing patterns of ccount (n) classes
   * Cn 1 + Cn 2 + ... + Cn n-1
   * @return int
   */
  private int combinations(){
      double combs = 0d;
      int ccount = classes.length;
      for(int c = 1; c<=ccount - 1; c++){
        combs += MathFunctions.nCr(ccount, c);
      }
      return (int)Math.ceil(combs);
  }

  /*private int combinations(){
    double combs = 0d;
    int ccount = classes.length;
    for(int c = 1; c<ccount / 2; c++){
      combs += MathFunctions.nCr(ccount, c);
    }
    if(ccount % 2 == 0){
      combs = combs*2 - MathFunctions.nCr(ccount, ccount/2) + 1;
    }else{
      combs = combs *2 + 1;
    }
    return (int)Math.ceil(combs);
  }*/

  private void print(HashSet standclasses){
    Iterator it = standclasses.iterator();
    while(it.hasNext()){
      System.out.print(it.next().toString()+" ");
    }
    System.out.println();
  }

  private HashSet getFields(String xml){
    HashSet fields = new HashSet();
    Document doc = learning.Utilities.getDomModel(xml);
    Node root = doc.getDocumentElement();
    for(Node node = root.getFirstChild(); node != null; node = node.getNextSibling()){
      if(node.getNodeType() == node.ELEMENT_NODE){
        fields.add(node.getNodeName());
      }
    }
    return fields;
    /*if (elements == null) {
        System.out.println("Warning: " + file.toString() +
                           " contains no contents");
    }else {
      while (elements.hasMoreElements()) {
        Element el = (Element) elements.nextElement();
        String classl = el.getName();
        if(el.getText().trim().compareTo("") != 0){
          fields.add(classl);
        }
      }
    }
    return fields;*/
  }

  /**
   *
   */
  private void printDifferenceHash(Hashtable order){
    Enumeration en = order.keys();
    while(en.hasMoreElements()){
      HashSet difference = (HashSet)en.nextElement();
      Iterator it = difference.iterator();
      System.out.print(learning.Utilities.fixLength(order.get(difference).toString(), 5, ' ') );
      while(it.hasNext()){
        String str = (String)it.next();
        System.out.print(" "+str);
      }
      System.out.println();
    }
  }

  public static void main(String[] args) {
    /*String srcData = "/home/hongcui/ThesisProject/Exp/Exp2/MarkedExamplesObjects/";
    String [] classes = {"PlantHabitAndLifeStyle","Stems","Leaves","Flowers",
       "Fruits","Cones","Seeds","Roots","SporeRelatedStructures","Flowering","Fruiting",
       "SeedMaturity","Compound","Chromosomes","Others"};
    */
   String srcData =
       "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\Level1\\trainingdir-fnct300\\";
   String[] classes = {
    "description", "taxon", "plant-habit-and-life-style", "roots", "stems",
    "buds", "leaves", "inflorescences", "flowers", "pollen", "fruits",
    "cones", "seeds",
    "spore-related-structures", "gametophytes", "chromosomes", "timeline",
    "compound", "other-features", "other-information"};


    File dir = new File(srcData);
    File[] files = dir.listFiles();

    String[] instances = new String[files.length];
    for (int f = 0; f < files.length; f++) {
      instances[f] = learning.Utilities.readFile(files[f]);
    }

    ClassPresence cp = new ClassPresence(instances, classes);
    System.out.println(cp.score());
  }

}
