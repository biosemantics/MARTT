package structure;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.regex.*;
import java.util.StringTokenizer;
import org.w3c.dom.*;
import learning.Utilities;

/**
 * <p>Title: StructureMeasure</p>
 * <p>Description: A measure of structuredness of flora corpus</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

/**
 * scores the regularity of delimitors of classes for a level
 * read 3-gram elements from a file: e1 e2 e3, and make it initial pattern
 * for each class,
 *     a incoming pattern is merged with existing pattern =>generalization
 *     occurance of the patterns are recorded.
 *     measure the regularity using information entropy
 * return the average
 */

public class ClassDelimitor
    extends MeasureAlgorithm {

  public ClassDelimitor(String[] instances, String[] classes) {
    super(instances, classes);//instances: text strings, one for an example
  }

  /**
   * put all classes side by side and find the overall entrophy july, 2006
   * @return float
   */
  public float score() {
    //1. collect patterns for each class
    ArrayList scores = new ArrayList();//instanbyclass hash: class label => # of instances
    Hashtable instanbyclass = new Hashtable(); //# of instances of each class, used to compute max entropy
    Hashtable fields = getPatterns(instanbyclass); // collected patterns: fields is a hashtable with classname as key
    // and pattern hashtable as values. Pattern hashtable: pattern <=>count (float)

    //printPatternHash(fields);
    int s = 0; //used to index classes
    //2. find info entropy for each class
    Object[] instances = new Object[instanbyclass.size()];//instances here hold the instance count for each class
    int num = classes.length;

    ArrayList ptcount = new ArrayList();
    int a = 0;
    float instancecount = 0;

    for (int i = 0; i < num; i++) {
      //copied from ClassPresence
      if (fields.containsKey(classes[i])) {
        instances[s] = instanbyclass.get(classes[i]);
        instancecount += ((Float)instances[s]).floatValue();
        Object[] count = ( (Hashtable) fields.get(classes[i])).values().toArray();//get pattern counts
        for (int c = 0; c < count.length; c++) {
          float value = ( (Float) count[c]).floatValue();
          if (Float.compare(value, 0f) != 0) {
            ptcount.add(a++, count[c]);
          }
        }
      }
    }
    float[] ptcnt = new float[ptcount.size()];//Floats are converted to floats
    for (int p = 0; p < ptcnt.length; p++) {
      ptcnt[p] = ( (Float) ptcount.get(p)).floatValue();
    }
    float entropy = learning.Utilities.getEntropy(ptcnt);
    double p = 1d/instancecount;
    float max =  (float) ( -1d * Math.log(p) / Math.log(2d));  //each instance represents an unique pattern
    if(entropy/max > 1){
      System.out.print("");
    }
    return 1- (float)entropy/max;
    }

  /* find entroy class by class, then do average.
     public float score() {
       //1. collect patterns for each class
       ArrayList scores = new ArrayList();//instanbyclass hash: class label => # of instances
       Hashtable instanbyclass = new Hashtable(); //# of instances of each class, used to compute max entropy
       Hashtable fields = getPatterns(instanbyclass); // collected patterns: fields is a hashtable with classname as key
       // and pattern hashtable as values. Pattern hashtable: pattern <=>count (float)

       //printPatternHash(fields);
       int s = 0; //used to index classes
       //2. find info entropy for each class
       Object[] instances = new Object[instanbyclass.size()];//instances here hold the instance count for each class
       int num = classes.length;
       for (int i = 0; i < num; i++) {
         //copied from ClassPresence
         if (fields.containsKey(classes[i])) {
           instances[s] = instanbyclass.get(classes[i]);
           Object[] count = ( (Hashtable) fields.get(classes[i])).values().toArray();//get pattern counts
           ArrayList ptcount = new ArrayList();
           int a = 0;
           for (int c = 0; c < count.length; c++) {
             float value = ( (Float) count[c]).floatValue();
             if (Float.compare(value, 0f) != 0) {
               ptcount.add(a++, count[c]);
             }
           }
           float[] ptcnt = new float[ptcount.size()];//Floats are converted to floats
           for (int p = 0; p < ptcnt.length; p++) {
             ptcnt[p] = ( (Float) ptcount.get(p)).floatValue();
           }
           scores.add(s++, new Float(learning.Utilities.getEntropy(ptcnt)));//entroy
         }
       }
       //3. find final score
       //max entropies for each class
       int total = instances.length; //the number of occured classes
       float[] max = new float[total];
       for (int i = 0; i < total; i++) {
         float t = ( (Float) instances[i]).floatValue();
         float p = 1 / t; //uniform distribution
         max[i] = (float) ( -1d * p * Math.log(p) / Math.log(2d)) * t;// t equal log terms
         //max[i] = (float) ( -1d * p * Math.log(p) / Math.log(2d));
       }

       float[] values = new float[scores.size()];//Float => float
       for (int p = 0; p < scores.size(); p++) {
         values[p] = ( (Float) scores.get(p)).floatValue();
       }

       float[] subscores = new float[total];
       for (int i = 0; i < total; i++) {
         if (Float.compare(max[i], 0f) != 0 && Float.compare(max[i], -0f) != 0) {
           subscores[i] = 1 - values[i] / max[i];
         }
         else {
           subscores[i] = 0;
         }
       }

       return learning.Utilities.average(subscores);
     }

  */

  /**
   * for each class, find its pattern distribution
   */
  private Hashtable getPatterns(Hashtable instanbyclass) {
    Hashtable fields = new Hashtable();
    int len = instances.length;
    for (int f = 0; f < len; f++) {
      getPatternFromFile(fields, instances[f], instanbyclass);
    }
    return fields;
  }

  /**
   * read 3 gram pattern
   */
  private void getPatternFromFile(Hashtable fields, String text,
                                  Hashtable instanbyclass) {
    ArrayList elements = readElements(text);
    int size = elements.size();
    for (int i = 0; i < size; i++) {
      int pos = 0; //-1:start, +1:end,
      Node[] gram = null;
      if (i == 0) {
        gram = new Node[2];
        gram[0] = (Node) elements.get(0);
        if (size > 1) {
          gram[1] = (Node) elements.get(1);
        }
        else {
          gram[1] = null;
        }
        pos = -1;
      }
      else if (i == size - 1) {
        gram = new Node[2];
        gram[0] = (Node) elements.get(size - 2);
        gram[1] = (Node) elements.get(size - 1);
        pos = 1;
      }
      else {
        gram = new Node[3];
        for (int j = 0; j < 3; j++) {
          gram[j] = (Node) elements.get(i + j - 1);
        }
      }
      parsePattern(gram, fields, pos, instanbyclass);
    }
  }

  /**
   * extract pattern gram
   *
   * @param post -1:start, +1:end,
   */
  private void parsePattern(Node[] gram, Hashtable fields, int pos,
                            Hashtable instanbyclass) {
    String theclass = null;
    String text1 = null;
    String text2 = null;
    String text3 = null;
    if (pos == -1) { //first element is THE class
      theclass = gram[0].getNodeName();
      text1 = " ";
      text2 = structure.Utilities.getText(gram[0]);
      if (gram[1] != null) {
        text3 = structure.Utilities.getText(gram[1]);
      }
      else {
        text3 = " ";
      }
      addToHash(theclass, text1, text2, text3, fields, instanbyclass);
    }
    else if (pos == 1) { //last element is THE class
      theclass = gram[1].getNodeName();
      text3 = " ";
      text1 = structure.Utilities.getText(gram[0]);
      text2 = structure.Utilities.getText(gram[1]);
      addToHash(theclass, text1, text2, text3, fields, instanbyclass);
    }
    else { //middle element is THE class
      theclass = gram[1].getNodeName();
      text3 = structure.Utilities.getText(gram[2]);
      text1 = structure.Utilities.getText(gram[0]);
      text2 = structure.Utilities.getText(gram[1]);
      addToHash(theclass, text1, text2, text3, fields, instanbyclass);
    }
  }

  /**
   * process an element
   * pattern generalization for each class,
   */
  private void addToHash(String classname, String before, String current,
                         String after, Hashtable fields,
                         Hashtable instanbyclass) {
    //have to escape '[' for it's reserved symbol of reg exp.
    before = stripHtml(before);
    before = learning.Utilities.escape(before);
    before = tokenClass(before);

    current = stripHtml(current);
    current = learning.Utilities.escape(current);
    current = tokenClass(current);

    after = stripHtml(after);
    after = learning.Utilities.escape(after);
    after = tokenClass(after);

    /*if (before.indexOf('[') >= 0) {
      before = before.replaceAll("\\[", "\\\\[");
      before = before.replaceAll("\\]", "\\\\]");
      before = stripHtml(before);
      before = tokenClass(before);
    }
    if (current.indexOf('[') >= 0) {
      current = current.replaceAll("\\[", "\\\\[");
      current = current.replaceAll("\\]", "\\\\]");
      current = stripHtml(current);
      current = tokenClass(current);
    }
    if (after.indexOf('[') >= 0) {
      after = after.replaceAll("\\[", "\\\\[");
      after = after.replaceAll("\\]", "\\\\]");
      after = stripHtml(after);
      after = tokenClass(after);
    }*/
    //[p], [font], [sd], and [m04302421] are noises

    if (!fields.containsKey(classname)) { //add the very first pattern for a class
      Hashtable ptns = new Hashtable();
      ptns.put(before + "###" + current + "###" + current + "###" + after,
               new Float(1));
      fields.put(classname, ptns);
      instanbyclass.put(classname, new Float(1));
    }
    else { //patterns exist, generalization
      instanbyclass.put(classname,
                        new Float(1f +
                                  ( (Float) instanbyclass.get(classname)).
                                  floatValue())); //increment count
      Hashtable ptns = (Hashtable) fields.get(classname);
      Enumeration en = ptns.keys();
      boolean merged = false;
      while (en.hasMoreElements()) {
        String pattern = (String) en.nextElement();
        if (Float.compare( ( (Float) ptns.get(pattern)).floatValue(), 0f) != 0) {
          String[] st = pattern.split("#{3}");
          String pat1 = st[0].trim();
          String pat2 = st[1].trim();
          String pat3 = st[2].trim();
          String pat4 = st[3].trim();
          String bcommon = generalize(pat1, before, "r");
          String lcommon = generalize(pat2, current, "l");
          String rcommon = generalize(pat3, current, "r");
          String acommon = generalize(pat4, after, "l");
          if (lcommon.compareTo("") != 0 &&
              rcommon.compareTo("") != 0) {
            //generalization
            String gpattern = (bcommon.compareTo("") == 0 ? " " : bcommon) +
                "###" +
                (lcommon.compareTo("") == 0 ? " " : lcommon) + "###" +
                (rcommon.compareTo("") == 0 ? " " : rcommon) + "###" +
                (acommon.compareTo("") == 0 ? " " : acommon);
            ptns.put(gpattern,
                     new Float(1 + ( (Float) ptns.get(pattern)).floatValue()));
            if (gpattern.compareTo(pattern) != 0) {
              ptns.put(pattern, new Float(0));
            }
            merged = true;
            break;
          }
        }
      }
      if (!merged) {
        //failed to generalize, add pattern
        ptns.put(before + "###" + current + "###" + current + "###" + after,
                 new Float(1));
      }
    }
  }

  /**
   * @param mode: "r" match right end of both patterns
   *              "l"       left
   * @return common part of two patterns at either end if there is any, otherwise ""
   */
  private String generalize(String pattern, String string, String mode) {
    StringBuffer sb = new StringBuffer();
    String reg = null;
    //pattern = learning.Utilities.escape(pattern);
    if (mode.compareTo("r") == 0) {
      reg = ".*?" + pattern;
    }
    else {
      reg = pattern + ".*?";
    }
    Pattern p = Pattern.compile(reg);
    Matcher m = p.matcher(string);
    if (m.matches()) {
      return pattern;
    }
    else {
      StringTokenizer st = new StringTokenizer(pattern, " ,.;", true);
      String[] p1 = getTokens(st);
      st = new StringTokenizer(string, " ,.;", true);
      String[] p2 = getTokens(st);
      if (p1 == null || p2 == null) {
        return sb.toString();
      }
      //String[] p1 = pattern.split("[\\s+.,;]");
      //String[] p2 = string.split("[\\s+.,;]");
      int i1 = p1.length - 1;
      int i2 = p2.length - 1;
      if (mode.compareTo("r") == 0) { //match right end
        String str = "";
        for (; i1 >= 0 && i2 >= 0; i1--, i2--) {
          String com = common(p1[i1], p2[i2]);
          if (com.compareTo("") == 0) {
            break;
          }
          else {
            str = com + " " + str; //reverse
          }
        }
        sb.append(str);
      }
      else { //match left end
        for (int i = 0, j = 0; i <= i1 && j <= i2; i++, j++) {
          String com = common(p1[i], p2[j]);
          if (com.compareTo("") == 0) {
            break;
          }
          else {
            sb.append(com + " ");
          }
        }
      }
    }
    return sb.toString().trim();
  }

  private String common(String a, String b) {
    if (a.compareTo(b) == 0) {
      return String.valueOf(a);
    }
    else if (a.toLowerCase().compareTo(b.toLowerCase()) == 0) {
      return "[" + a.charAt(0) + b.charAt(0) + "]" + a.substring(1);
    }
    else {
      return "";
    }
  }

  /**
   * parse the file (flat xml) and put the elements(nodes) in an arraylist
   * @param filename
   * @return
   */

  private ArrayList readElements(String xml) {
    Pattern p = Pattern.compile("(.*?)<taxon>.*?</taxon>(.*)");
    Matcher m = p.matcher(xml);
    if (m.lookingAt()) {
      xml = m.group(1) + m.group(2);
    }
    ArrayList nodes = new ArrayList();
    if(xml.trim().length() > 0){
      Document doc = learning.Utilities.getDomModel(xml);
      Node node = doc.getDocumentElement();
      Node n = null;
      for (n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
        if (n.getNodeType() == n.ELEMENT_NODE) {
          nodes.add(n);
        }
      }
    }
    return nodes;
  }

  /*private ArrayList readElements(String filename) {
    //copied from classOrder
    File file = new File(filename);
    Enumeration elements = learning.Utilities.readXMLElements(file);
    ArrayList elementls = new ArrayList();
    boolean start = true;
    if (elements == null) {
      System.out.println("Warning: " + file.toString() +
                         " contains no contents");
    }
    else {
      //read elements in an arraylist
      while (elements.hasMoreElements()) {
        Element el = (Element) elements.nextElement();
        String classl = el.getName();
        if (el.getText().trim().compareTo("") == 0) {
          continue;
        }
        else {
          elementls.add(el);
        }
      }
    }
    return elementls;
     }*/

  private String stripHtml(String str) {
    str = str.replaceAll("\\\\\\[[pP]\\\\\\]", "");
    str = str.replaceAll("\\\\\\[/[Pp]\\\\\\]", "");
    str = str.replaceAll("\\\\\\[[Ff][Oo][Nn][Tt].*?\\\\\\]", "");
    str = str.replaceAll("\\\\\\[/[Ff][Oo][Nn][Tt]\\\\\\]", "");
    str = str.replaceAll("\\\\\\[[Ss][Dd]\\\\\\]", "");
    str = str.replaceAll("\\\\\\[/[Ss][Dd]\\\\\\]", "");
    str = str.replaceAll("\\\\\\[[Mm]\\d+\\\\\\]", "");
    str = str.replaceAll("\\\\\\[/[Mm]\\d+\\\\\\]", "");

    return str;
  }

  private String tokenClass(String str) {
    str = str.replaceAll("\\d+", " NUM ");
    return str;
  }

  private String[] getTokens(StringTokenizer st) {
    ArrayList tokens = new ArrayList();
    int i = 0;
    while (st.hasMoreTokens()) {
      String token = null;
      if ( (token = st.nextToken()).compareTo(" ") != 0) {
        tokens.add(i++, token);
      }
    }
    if (tokens.size() > 0) {
      return (String[]) tokens.toArray(new String[1]);
    }
    else {
      return null;
    }
  }

  public static void main(String[] args) {
    /*String srcData = "/home/hongcui/ThesisProject/Exp/Exp2/MarkedExamplesObjects/";
         String[] classes = {
        "PlantHabitAndLifeStyle", "Stems", "Leaves", "Flowers",
        "Fruits", "Cones", "Seeds", "Roots", "SporeRelatedStructures",
        "Flowering", "Fruiting",
        "SeedMaturity", "Compound", "Chromosomes", "Others"};*/
    //String srcData =
    //    "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\Level1\\trainingdir-fnct300-level1\\";
    String srcData = "C:\\Documents and Settings\\hong cui\\Research-Exp\\jasist\\trainingdir-fna133\\";

    String[] classes = {
        "description", "taxon", "plant-habit-and-life-style", "roots", "stems",
        "buds", "leaves", "flowers", "pollen", "fruits",
        "cones", "seeds",
        "spore-related-structures", "gametophytes", "chromosomes", "phenology",
        "compound", "other-features", "other-information"};

    File dir = new File(srcData);
    File[] files = dir.listFiles();
    String[] instances = new String[files.length];
    for (int f = 0; f < files.length; f++) {
      instances[f] = learning.Utilities.readFile(files[f]);
    }
    ClassDelimitor cd = new ClassDelimitor(instances, classes);
    System.out.println("[Regularity Score for Delimitors = " + cd.score() + "]");
  }

}
