package structure;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
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
 * score the regularity of the order of the fields
 * Input: a sample of markedup segments of level l
 * Output: score of regularity
 *
 * 1. order pattern pool = null
 * 2. for each segment
 *        find field order pattern p
 *        if p is subsumed by some patterns in the pool, increment the count for the pattern by 1 / #matches
 *        else if p subsumes some patterns in the pool, remove patterns from the pool and add p in the pool,
 *                  p 's count = 1 + the sum of all sumsumed counts
 *        else add p to the pool with count = 1
 * 3. find info entropy for the collection
 * 4. find max possible entropy for the collection
 * 5. return 1 - entropy/max
 *
 *
 * the order in which segments are processed affects pattern scores, for example if a short patten
 * is the first pattern saved, the next pattern which subsumes it gets its score; however, if the short
 * pattern were the last one, then every pattern in the pool will get a fraction of this pattern (1)
 *
 * to avoid this problem, save all the unique patterns first and then performs subsumption, starting from the
 * shortest pattern.
 */

/**
 * @todo: superclass for classPresence & classOrder
 */
public class ClassOrder
    extends MeasureAlgorithm {

  public ClassOrder(String[] instances, String[] classes) {
    super(instances, classes);
  }

  public float score() {
    Hashtable pool = new Hashtable();
    for (int f = 0; f < instances.length; f++) {
      //String pcandid = getOrderPattern(files[f]);
      String xml =instances[f];
      Pattern p = Pattern.compile("(.*?)<taxon>.*?</taxon>(.*)");
      Matcher m = p.matcher(xml);
      if(m.lookingAt()){
        xml = m.group(1)+m.group(2);
      }
      String pcandid = getOrderPattern(xml);
      updatePool(pool, pcandid);
    }
    //printOrderHash(pool);
    //copied from ClassPresence
    Object[] count = pool.values().toArray();
    float[] ordercount = new float[count.length];
    for (int c = 0; c < count.length; c++) {
      ordercount[c] = ( (Float) count[c]).floatValue();
    }
    float score = learning.Utilities.getEntropy(ordercount);
    //find max entropy for this collection
    int combs = (int) (MathFunctions.fact(classes.length)+0.5);

    int occur = 0;
    int total = instances.length;
    if (combs > instances.length) {
      occur = 1;
    }
    else {
      occur = instances.length / combs;
    }
    float p = (float) occur / total;

    float max  = (float)(-1f * (p * Math.log(p) / Math.log(2d))*total);
    if(score/max > 1 || score/max < 0){
          System.out.print("");
    }
    return 1 - score / max;
  }

  /**
   *
   */
  private void updatePool(Hashtable pool, String pcandid) {
    if(pcandid.length() < 1){
      return;
    }
    if (pool.isEmpty()) {
      pool.put(pcandid, new Float(1));
      return;
    }
    Enumeration ptns = pool.keys();
    ArrayList plist = new ArrayList();
    boolean subsumed = false;
    //subsumed by any pattern in pool?
    while (ptns.hasMoreElements()) {
      String pts = (String) ptns.nextElement();
      String pt = pts.replace(' ', '?'); //make pattern "A?B?C?"
      Pattern p = Pattern.compile(pt);
      Matcher m = p.matcher(pcandid.replaceAll(" ", "").replaceAll("\\)", "").
                            replaceAll("\\(", ""));
      if (m.matches()) {
        plist.add(pts);
        subsumed = true;
      }
    }
    if (subsumed) {
      float incre = 1f / plist.size();
      Iterator it = plist.iterator();
      while (it.hasNext()) {
        String pts = (String) it.next();
        pool.put(pts, new Float( ( (Float) pool.get(pts)).floatValue() + incre));
      }
      return;
    }
    //subsume any pattern in pool?
    ptns = pool.keys();
    float count = 0f;
    boolean subsume = false;
    while (ptns.hasMoreElements()) {
      String pts = (String) ptns.nextElement();
      String pt = pcandid.replace(' ', '?'); //make pattern "A?B?C?"
      Pattern p = Pattern.compile(pt);
      Matcher m = p.matcher(pts.replaceAll(" ", "").replaceAll("\\)", "").
                            replaceAll("\\(", ""));
      if (m.matches()) {
        count += ( (Float) pool.get(pts)).floatValue();
        pool.remove(pts);
        subsume = true;
      }
    }
    if (subsume) {
      pool.put(pcandid, new Float(count + 1));
    }
    if (!subsumed && !subsume) {
      pool.put(pcandid, new Float(1));
    }
  }

  /**
   * returned pattern string has format of "A B C"
   */

  private String getOrderPattern(String xml) {
    StringBuffer pattern = new StringBuffer();

    if(xml.trim().length() > 0){
      Document doc = learning.Utilities.getDomModel(xml);
      Node root = doc.getDocumentElement();
      for (Node node = root.getFirstChild(); node != null;
           node = node.getNextSibling()) {
        if (node.getNodeType() == node.ELEMENT_NODE) {
          String tag = node.getNodeName();
          //String text = Utilities.getText(node);
          pattern.append("(" + tag + ")" + " ");
        }
      }
    }
    return pattern.toString();

    /*StringBuffer pattern = new StringBuffer();
    File file = new File(filename);
    Enumeration elements = bdlearner.Utilities.readXMLElements(file);
    if (elements == null) {
      System.out.println("Warning: " + file.toString() +
                         " contains no contents");
    }
    else {
      while (elements.hasMoreElements()) {
        Element el = (Element) elements.nextElement();
        String classl = el.getName();
        if (el.getText().trim().compareTo("") != 0) {
          pattern.append("(" + classl + ")" + " ");
        }
      }
    }
    return pattern.toString();*/

  }

  /**
   *
   */
  private void printOrderHash(Hashtable order) {
    Enumeration en = order.keys();
    while (en.hasMoreElements()) {
      String pattern = (String) en.nextElement();
      System.out.println(learning.Utilities.fixLength(order.get(pattern).
          toString(), 5, ' ') + " " + pattern);
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
        "spore-related-structures", "gametophytes", "chromosomes", "phenology",
        "compound", "other-features", "other-information"};

    File dir = new File(srcData);
    File[] files = dir.listFiles();
    String[] instances = new String[files.length];
    for (int f = 0; f < files.length; f++) {
      instances[f] = learning.Utilities.readFile(files[f]);
    }
    ClassOrder co = new ClassOrder(instances, classes);
    System.out.println(co.score());
  }

}
