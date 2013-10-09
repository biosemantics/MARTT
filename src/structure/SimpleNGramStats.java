package structure;

/**
 * <p>Title: StructureMeasure</p>
 * <p>Description: A measure of structuredness of flora corpus</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui adapted from Bob Futrelle, Northeastern U. and bionlp.org
 * version  0.1 23 May 2003
 *
 * @version 1.0
 */

import learning.Term;
import java.util.regex.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.io.*;
import jds.collection.BinarySearchTree;
import org.w3c.dom.*;

public class SimpleNGramStats {

  private Hashtable table;
  //private String[] ngramStrings;
  //private int[] bigramCounts;
  //private Enumeration elements, keys;
  private NgramsCounts[][] fullResults;
  private int N = 1; //1-gram by default
  private String[] instances = null;
  private String[] classes = null;
  private int number = 0;
  private int[] classcount = null;
  private int m = 0; //consider leading m words only if m > 0
  private Hashtable rarelist = null;

  public SimpleNGramStats(int N, String[] instances, String[] classes, int m, Hashtable rarelist) {
    this.N = N;
    this.m = m;
    this.instances = instances;
    this.classes = classes;
    this.rarelist = rarelist;
    this.number = classes.length;
    this.table = new Hashtable();
    for (int i = 0; i < number; i++) {
      this.table.put(classes[i], new Hashtable());
    }
    this.classcount = new int[number];
    for (int i = 0; i < number; i++) {
      this.classcount[i] = 0;
    }
  }

  public int[] getClassCount() {
    return classcount;
  }

 /* public SimpleNGramStats(int N, String[] filenames, String[] classes) {
    this.N = N;
    files = new File[filenames.length];
    for (int f = 0; f < filenames.length; f++) {
      files[f] = new File(filenames[f]);
    }

    this.classes = classes;
    this.number = classes.length;
    this.table = new Hashtable();
    for (int i = 0; i < number; i++) {
      this.table.put(classes[i], new Hashtable());
    }
    this.classcount = new int[number];
    for (int i = 0; i < number; i++) {
      this.classcount[i] = 0;
    }
  }*/

  public BinarySearchTree collect() {
    collectStats();
    BinarySearchTree ngramtree = convertToBTree();
    return ngramtree;
    //collectResults();
    //dumpTable();
    //showAlphaResults();
    //showFrequencyResults();

  } // run()

  void collectStats() {
    //@todo: read entire file to ONE line of string
    //for now, assume entire file is one line already
    for (int i = 0; i < instances.length; i++) {
      readGramsFrom(instances[i]);
    }
  }

  public void readGramsFrom(String xml) {
    //copy from TermClassMatrix
    //Enumeration elements = bdlearner.Utilities.readXMLElements(file);
    Pattern p = Pattern.compile("(.*?)<taxon>.*?</taxon>(.*)");
    Matcher m = p.matcher(xml);
    if (m.lookingAt()) {
      xml = m.group(1) + m.group(2);
    }
    if (xml.trim().length() > 0) {
      Document doc = learning.Utilities.getDomModel(xml);
      if (doc == null) {
        System.out.println("Warning:  fail to create document model from " /*+
                                                    file.toString()*/);
      }
      else {
        Node root = doc.getDocumentElement();
        for (Node node = root.getFirstChild(); node != null;
             node = node.getNextSibling()) {
          // while (elements.hasMoreElements()) {
          if (node.getNodeType() != node.ELEMENT_NODE) {
            continue;
          }
          //Element el = (Element) elements.nextElement();
          String classl = node.getNodeName();
          if (!Utilities.exist(classes, classl)) {
            System.out.println("unrecognized class(" + classl +
                               ") in file:" /*+
                                                            file.getName()*/);
          }
          else {
            String text = structure.Utilities.getText(node).trim();
            if (text.compareTo("") == 0) { //<description>
              continue;
            }
            //end copy
            addToClassCount(classl);
            readGramsFrom(text, classl, (Hashtable) table.get(classl));
          }
        }
      }
    }
  }

  /**
   * copied from TermClassTree
   */
  private void addToClassCount(String classl) {
    for (int c = 0; c < number; c++) {
      if (classl.compareTo(classes[c]) == 0) {
        classcount[c]++;
      }
    }
  }

  public void readGramsFrom(String line, String classl, Hashtable atable) {
    line = cleanup(line);
    StringTokenizer tokens = new StringTokenizer(line);
    String token[] = new String[N];
    StringBuffer ngramsb = new StringBuffer();
    String ngram = "";
    for (int i = 0; i < N - 1; i++) {
      if (tokens.hasMoreTokens()) {
        token[i] = tokens.nextToken();
      }
    }
    while (tokens.hasMoreTokens()) {
      token[N - 1] = tokens.nextToken();
      for (int i = 0; i < N; i++) {
        ngramsb.append(token[i] + " ");
      }
      ngram = ngramsb.toString().trim();
      ngramsb.delete(0, ngramsb.length());
      //@todo: if ngram contains stopwords, or belong to rare word list, ignore it.
      if(isOK(ngram)){
        Object item = atable.get(ngram);
        if (item != null) {
          ( (int[]) item)[0]++;
        }
        else {
          int[] count = {
              1};
          atable.put(ngram, count);
        }
      }
      shiftleft(token); // step forward
    }
  } // collectStats()

  /**
   * if ngram contains stopwords, or belong to rare word list, return false.
   * @param ngram String
   * @return boolean
   */
  private boolean isOK(String ngram){
    String[] tokens = ngram.toLowerCase().split("\\s+");
    for(int i = 0; i < tokens.length; i++){
      if(learning.Tokenizer.stop.indexOf(" "+tokens[i]+" ") >=0){
        return false;
      }
      if(rarelist.get(tokens[i]) != null){
        return false;
      }
    }
    return true;
  }
  /**
   * remove \\W, and make MONTH NUMBER class
   */
  private String cleanup(String line) {
    line = line.replaceAll("\\W", " "); //
    line = line.replaceAll("-", " ");
    line = line.replaceAll("_", " ");
    line = line.replaceAll(" Jan ", " MON ");
    line = line.replaceAll(" Feb ", " MON ");
    line = line.replaceAll(" Mar ", " MON ");
    line = line.replaceAll(" Apr ", " MON ");
    line = line.replaceAll(" May ", " MON ");
    line = line.replaceAll(" Jun ", " MON ");
    line = line.replaceAll(" Jul ", " MON ");
    line = line.replaceAll(" Aug ", " MON ");
    line = line.replaceAll(" Sep ", " MON ");
    line = line.replaceAll(" Oct ", " MON ");
    line = line.replaceAll(" Nov ", " MON ");
    line = line.replaceAll(" Dec ", " MON ");
    line = line.replaceAll("(\\s*MON\\s*)+", " MON ");
    line = line.replaceAll("\\d+", "NUM");
    line = line.replaceAll("(\\s*NUM\\s*)+", " NUM ");
    if(m > 0){
      line = learning.Utilities.getFirstMWords(line, 3," ", false);
    }
    return line;
  }

  /**
   * move each element one step to the left, discard the first element and append "" to the end
   */
  private void shiftleft(String[] array) {
    for (int i = 0; i < array.length; i++) {
      if (i != array.length - 1) {
        array[i] = array[i + 1];
      }
      else {
        array[i] = "";
      }
    }
  }

  /** Local class for ngram count pairs.
   */
  class NgramsCounts {
    int count;
    String ngram;

    NgramsCounts(int count, String ngram) {
      this.count = count;
      this.ngram = ngram;
    }
  }

  /**
   * populate fullResults
   */
  void collectResults() {

    fullResults = new NgramsCounts[number][];

    for (int i = 0; i < number; i++) {
      Hashtable htable = (Hashtable) table.get(classes[i]);
      Enumeration keys = htable.keys();
      Enumeration elements = htable.elements();
      int index = 0;
      fullResults[i] = new NgramsCounts[htable.size()];

      while (keys.hasMoreElements()) {
        elements.hasMoreElements();
        fullResults[i][index++] =
            new NgramsCounts(
            ( (int[]) elements.nextElement())[0],
            (String) keys.nextElement());
      }
    }
  } // collectResults

  /** Dumps entire table.
   */
  void dumpTable() {

  } // dumpTable()

  void showAlphaResults() {

    Comparator comp = new Comparator() {
      public int compare(Object o1, Object o2) {
        return ( ( (NgramsCounts) o1).ngram).compareTo( ( (NgramsCounts) o2).
            ngram);
      }
    }; // Comparator comp
    for (int j = 0; j < number; j++) {
      //  if (fullResults[j] != null) {
      Arrays.sort(fullResults[j], comp);
      System.out.println("Results sorted alphabetically for class:" +
                         classes[j]);
      for (int i = 0; i < fullResults[j].length; i++) {
        System.out.println(fullResults[j][i].count + " " +
                           fullResults[j][i].ngram);

      }
    }
    //}
  } // showAlphaResults()

  void showFrequencyResults() {

    Comparator comp = new Comparator() {
      public int compare(Object o1, Object o2) {
        int i1 = ( (NgramsCounts) o1).count;
        int i2 = ( (NgramsCounts) o2).count;
        if (i1 == i2) {
          return 0;
        }
        return ( (i1 > i2) ? -1 : +1);
      }
    }; // Comparator comp

    for (int j = 0; j < number; j++) {
      //if(fullResults[j] != null){
      Arrays.sort(fullResults[j], comp);
      System.out.println("Results sorted by ngram count for class:" +
                         classes[j]);
      for (int i = 0; i < fullResults[j].length; i++) {
        System.out.println(fullResults[j][i].count + " " +
                           fullResults[j][i].ngram);
      }
      //}
    }
  } // showFrequencyResults()

  /**
   * create a BinarySearchTree of BTerm from table
   * @return a b-tree of BTerms
   */
  private BinarySearchTree convertToBTree() {
    BinarySearchTree bt = new BinarySearchTree(new Term());
    for (int i = 0; i < number; i++) {
      Hashtable ht = (Hashtable) table.get(classes[i]);
      Enumeration en = ht.keys();
      while (en.hasMoreElements()) {
        String ngram = (String) en.nextElement();
        int occ = ( (int[]) ht.get(ngram))[0];
        if (occ > 0) { //take only ngrams that have occurance > n
          Term term = new Term(ngram, classes[i], classes);
          if (bt.containsElement(term)) {
            for (int j = 0; j < occ; j++) {
              ( (Term) bt.findElement(term)).addOccuranceIn(classes[i]);
            }
          }
          else {
            bt.addElement(term);
            for (int j = 1; j < occ; j++) {
              Term b0 = (Term) bt.findElement(term);
              b0.addOccuranceIn(classes[i]);
            }
          }
        }
      }
    }
    return bt;
  }

  public static void main(String[] args) {
    //String filename =
    //    "/home/hongcui/ThesisProject/FOC/descriptions/Species/Abies-georgei.dscrpt";
    //String filename =
    //  "/home/hongcui/ThesisProject/Exp/Exp1/MarkedExamplesObjects/";
    /*String filename =
         "/home/hongcui/ThesisProject/Exp/Exp2/MarkedExamplesObjects/";
         String[] classes = {
        "PlantHabitAndLifeStyle", "Stems", "Leaves", "Flowers",
        "Fruits", "Cones", "Seeds", "Roots", "SporeRelatedStructures",
        "Flowering", "Fruiting",
        "SeedMaturity", "Compound", "Chromosomes", "Others"};
     */
    String filename =
        "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\Level1\\trainingdir-fna500-merged\\";
    String[] classes = {
        "description", "taxon", "plant-habit-and-life-style", "roots", "stems",
        "buds", "leaves", "inflorescences", "flowers", "pollen", "fruits",
        "cones", "seeds",
        "spore-related-structures", "gametophytes", "chromosomes", "timeline",
        "compound", "other-features", "other-information"};
    File src = new File(filename);
    File[] files = src.listFiles();
    /*SimpleNGramStats stats = new SimpleNGramStats(3, files, classes);
    BinarySearchTree termtree = stats.collect();
    int[] classCount = stats.getClassCount();
    InfoGainScore score1 = new InfoGainScore(termtree, classes, classCount);
    BinarySearchTree scored = score1.scoredTerms();
    System.out.println(learning.Utilities.printBTree(scored));
    *///stats.readGramsFrom("this this this this", "Leaves", new Hashtable());
  } // main()

} // class SimpleNgramStats
/*
 Using the Watson and Crick April 1953 Letter to Nature.
 Number of bigrams is: 652
 Here are the largest entries from the count sorted result:
 11 of the
 7 that the
 6 and the
 5 it is
 5 on the
 5 in the
 4 to the
 4 the other
 4 the bases
 */
