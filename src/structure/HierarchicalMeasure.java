package structure;

import java.io.File;
import java.util.*;
import visitor.ElementComposite;
import visitor.ElementComponent;

/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: Thesis Project, everything in this project</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author not attributable
 * @version 0.1
 */

/**
 * quantatitive measure of structuredness of xml collections
 * take into the consideration of hierarchical structure of xml documents
 *
 */
public class HierarchicalMeasure {
  private File[] files = null;
  private int m = 0; //the first m words of each instance
  public Hashtable rarelist = null;

  public HierarchicalMeasure(File[] files, int m) {
    this.m = m;
    this.files = files;
    this.rarelist = new Hashtable(2000);
    termfreq();
  }

  public Hashtable getRarelist() {
    return rarelist;
  }

  private void termfreq() {
    Hashtable words = new Hashtable();
    for (int i = 0; i < files.length; i++) {
      String text = learning.Utilities.readFile(files[i]);
      text = text.toLowerCase();
      text = text.replaceAll("</?[^<]*?>", " ").replaceAll("\\s+", " ");
      if (i == 50) {
        System.out.print("");
      }

      String[] tokens = learning.Tokenizer.tokenize(text, true);
      for (int j = 0; j < tokens.length; j++) {

        if (words.get(tokens[j]) == null) {
          words.put(tokens[j], "1");
        }
        else {
          words.put(tokens[j], words.get(tokens[j]) + "1");
        }
      }
    }
    Enumeration en = words.keys();
    while (en.hasMoreElements()) {
      Object t = en.nextElement();
      String count = (String) words.get(t);
      /*if(((String) t).compareTo("across") == 0){
         System.out.print("");
       }*/

      if (count.length() == 1) {
        rarelist.put(t, "1");
        //System.out.println((String)t);
      }
    }
  }

  /**
   * produce quantative measures of the structuredness,
   * including class delimitors, class order, class presence,
   * and term distribution
   *
   * attempt to generate values at different levels of the markup
   *
   * in ec, each parent node has a set of structure measure.
   * list raw scores for the time being
   */
  public void structureMeasure() {
    XMLDTDExtractor xder = new XMLDTDExtractor(files);
    //System.out.println("~~~~~~~~~~~~~~~~Corpus Structure~~~~~~~~~~~~~~~~~~~~~~~");
    //System.out.print(xder.toString());
    ElementComposite ec = xder.getXMLTree();
    recursiveMeasure(xder, ec, "");
  }

  private void recursiveMeasure(XMLDTDExtractor xder, ElementComponent comp,
                                String pathprefix) {
    if (comp instanceof ElementComposite) { //parent node
      String tag = comp.getTag();
      if (tag.compareTo("flowers") == 0) {
        System.out.print("");
      }
      String[] classes = xder.getChildElements(pathprefix + tag);
      String[] instances = (String[]) comp.getTrainingExamples().toArray(new
          String[] {});
      instances = wrap(instances, tag);
      pathprefix = pathprefix + tag + "/";
      levelMeasure(tag, instances, classes, pathprefix);
      Iterator it = comp.iterator(); //traverse child nodes
      while (it != null && it.hasNext()) {
        recursiveMeasure(xder, (ElementComponent) it.next(), pathprefix);
      }
    }
  }

  private String[] wrap(String[] instances, String tag) {
    for (int i = 0; i < instances.length; i++) {
      instances[i] = "<" + tag + ">" + instances[i] + "</" + tag + ">";
    }
    return instances;
  }

  private void levelMeasure(String tag, String[] instances, String[] classes,
                            String fullpath) {
    System.out.println("================StructuredMeasure for [" + fullpath +
                       "]=========");
    System.out.println("[Number of Instances in Class " + tag + " = " +
                       instances.length + "]");
    ClassDelimitor cd = new ClassDelimitor(instances, classes);
    System.out.println("[Regularity Score for Class Delimitors = " + cd.score() +
                       "]");

    ClassOrder co = new ClassOrder(instances, classes);
    System.out.println("[Regularity Score for Class Order = " + co.score() +
                       "]");

    ClassPresence cp = new ClassPresence(instances, classes);
    System.out.println("[Regularity Score for Class Presence = " + cp.score() +
                       "]");

    NGramDistribution ngd = new NGramDistribution(instances, classes, 0f, 1, -1,
                                                  rarelist);
    System.out.println("[Regularity Score for Term Distribution (1-gram)= " +
                       ngd.score() + "]");

    ngd = new NGramDistribution(instances, classes, 0f, 2, -1, rarelist);
    System.out.println("[Regularity Score for Term Distribution (2-gram)= " +
                       ngd.score() + "]");

    ngd = new NGramDistribution(instances, classes, 0f, 3, -1, rarelist);
    System.out.println("[Regularity Score for Term Distribution (3-gram)= " +
                       ngd.score() + "]");

    ngd = new NGramDistribution(instances, classes, 0f, 1, m, rarelist);
    System.out.println("[Regularity Score for Leading " + m +
                       " Term Distribution (1-gram)= " + ngd.score() + "]");

    ngd = new NGramDistribution(instances, classes, 0f, 2, m, rarelist);
    System.out.println("[Regularity Score for Leading " + m +
                       " Term Distribution (2-gram)= " + ngd.score() + "]");

    ngd = new NGramDistribution(instances, classes, 0f, 3, m, rarelist);
    System.out.println("[Regularity Score for Leading " + m +
                       " Term Distribution (3-gram)= " + ngd.score() + "]");

    System.out.println(
        "=============================================================");
  }

  /**
   * for each instances, keep only the leading m words
   * @param instances String[]
   * @return String[]
   */
  private String[] selectFirst(String[] instances) {
    return null;
  }

  public static void main(String[] args) {
    String[] data = new String[] {
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_100_0\\",
    "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_100_1\\",
    "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_100_2\\",
    "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_100_3\\",
    "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_100_4\\",
    "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_100_5\\",
    "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc492_100_0\\",
    "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc492_100_1\\",
    "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc492_100_2\\",
    "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc492_100_3\\",
    "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct378_100_0\\",
    "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct378_100_1\\",
    "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct378_100_2\\"




        /*"U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_50_0\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_50_1\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_50_2\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_50_3\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_50_4\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_50_5\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_50_6\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_50_7\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_50_8\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_50_9\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_50_10\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633_50_11\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc492_50_0\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc492_50_1\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc492_50_2\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc492_50_3\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc492_50_4\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc492_50_5\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc492_50_6\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc492_50_7\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc492_50_8\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct378_50_0\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct378_50_1\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct378_50_2\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct378_50_3\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct378_50_4\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct378_50_5\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct378_50_6\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct63\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct126\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct189\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct252\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct315\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fnct378\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc92\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc192\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc292\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc392\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-foc492\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna133\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna233\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna333\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna433\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna533\\",
        "U:\\Research\\projects\\structmeasure\\hierarchy-structuctmeasure\\data\\trainingdir-fna633\\"*/

    };

    for (int i = 0; i < data.length; i++) {
      File dir = new File(data[i]);
      File[] files = dir.listFiles();
      HierarchicalMeasure hm = new HierarchicalMeasure(files, 3);
      System.out.println("############" + data[i] + "############");
      hm.structureMeasure();
    }
  }

}
