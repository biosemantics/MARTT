package miner;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.*;
import java.util.TreeSet;
import learning.Term;
import learning.Utilities;
import jds.collection.BinarySearchTree;

/**
 * <p>Title: SemanticClass</p>
 * <p>Description: learn leading terms/phrases that belongs to semantic classes/elements
 *                 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */

public class TermSemantic
    implements Serializable {
  private String[] classes = null;
  transient private float[] minsup = null;
  private Hashtable classcount = null;
  public static String semanticfile =
      "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level1\\semanticfile";
  private BinarySearchTree[] semantics = null; //
  transient private int minsize = 10; //the threshold for "sparseness" of training examples
  private boolean useStopList = true;
  public final static String symbol = "_"; //one char, must not be "-", which is used in tag names.

  public TermSemantic() {

  }

  public boolean getUseStopList(){
      return this.useStopList;
  }
  public TermSemantic(TermSemantic t){
    this.semantics = t.getSemantics();
    this.classcount = t.getClasscountHash();
    this.classes = t.getClasses();
    this.useStopList = t.useStopList;
 }

  public TermSemantic(String[] xmls, String[] classes, int n, float confhold,
                      float suphold, String[] delim, boolean useStopList) {
    this.classes = classes;
    this.semantics = new BinarySearchTree[n];
    this.useStopList = useStopList;
    this.classcount = new Hashtable(); //number of text segments for classes
    String ignored = " compound multiple phenology chromosomes ";
    this.semantics = calculate(xmls, n, ignored, suphold, confhold, delim); //terms by classes
    //printTermsForClasses(suphold, confhold);
    //prune1GramTerms();
  }

  /**
   * couldn't get a good POStagger for this collection
   * try simple methods first: pure statitical approach
   *
   * do 1- to n-gram, for each, create a BT of Terms, where "terms" are n-grams
   *
   * @return
   */
  public BinarySearchTree[] calculate(String[] xmls, int n, String ignored, float suphold, float confhold, String[] delim) {
    for (int i = 0; i < n; i++) {
      semantics[i] = new BinarySearchTree(new Term());
    }
    int size = xmls.length;
    for (int i = 0; i < size; i++) {
      String xml = xmls[i];
      Pattern p = Pattern.compile(".*?<description>(.*?)</description>");
      /**@todo: fix this**/
      //Pattern p = Pattern.compile(".*?<gametophytes>(.*?)</gametophytes>");
      //Pattern p = Pattern.compile(".*?<stems>(.*?)</stems>");
      Matcher m = p.matcher(xml);
      if (m.lookingAt()) {
        xml = m.group(1);
      }
      getCountFrom(xml.trim(), n, ignored, delim);
    }
    //System.out.println("proceed to score the terms ...");
    scoreTerms(true, suphold, confhold);
    //System.out.println("done with score the terms!");

    return semantics;
  }

  /**
   * @todo impletement plan G.
   * rendered negative effects.
   */
  /*private void prune1GramTerms() {
    //get token counts of all grams, using stemming
    for (int c = 0; c < classes.length; c++) {
      Hashtable tokens = getTokenCounts(classes[c]);
      BinarySearchTree onegrams = semantics[0];
      Enumeration en = onegrams.elements();
      while (en.hasMoreElements()) {
        Term t = (Term) en.nextElement();
        SCScore s = (SCScore) ( (Hashtable) t.getScore()).get(classes[c]);
        if (s != null && Double.compare(s.getConfidence(), confhold) >= 0 &&
            Double.compare(s.getSupport(), suphold) >= 0) {
          String token = new Stemmer().stem(t.getTerm().toLowerCase());
          if (classcount.get(classes[c]) != null &&
              ( (Integer) classcount.get(classes[c])).intValue() > this.minsize &&
              Integer.parseInt( (String) tokens.get(token)) < this.n && Double.compare(s.getSupport(), minsup[c]) <= 0) {
            s.setConfidence(0d);
            s.setSupport(0d);

            System.err.println("remove 1 gram:["+t.getTerm()+"] from ["+classes[c]+"]");
          }
        }
      }
    }
  }*/

  /*private Hashtable getTokenCounts(String clabel, float suphold, float confhold) {
    Hashtable tokens = new Hashtable();
    for (int i = 0; i < semantics.length; i++) {
      BinarySearchTree grams = semantics[i];
      Enumeration en = grams.elements();
      while (en.hasMoreElements()) {
        Term t = (Term) en.nextElement();
        SCScore s = (SCScore) ( (Hashtable) t.getScore()).get(clabel);
        if (s != null && Double.compare(s.getConfidence(), confhold) >= 0 &&
            Double.compare(s.getSupport(), suphold) >= 0) {
          String[] toks = ( (String) t.getTerm()).split(this.symbol);
          for (int j = 0; j < toks.length; j++) {
            String tok = new Stemmer().stem(toks[j].toLowerCase());
            if (tokens.containsKey(tok)) {
              int count = Integer.parseInt( (String) tokens.get(tok));
              count++;
              tokens.put(tok, "" + count);
            }
            else {
              tokens.put(tok, "" + 1);
            }
          }
        }
      }
    }
    return tokens;
  }*/

  /**
   * remove 1-grams with minimum sup from semantics
   * if an element has only one example, then leave the element alone (for
       * min = max in this case)...hmm, no use, we will not use these grams to classify
   * in test.
   *
   * negative effects
   */
  /*private void pruneTerms() {
    Enumeration en = semantics[0].elements();
    //remove min by setting its sup and conf zero
    while (en.hasMoreElements()) {
      Term t = (Term) en.nextElement();
      Hashtable scores = (Hashtable) t.getScore();
      for (int i = 0; i < classes.length; i++) {
        SCScore s = (SCScore) scores.get(classes[i]);
        if (s != null &&
            ( (Integer) classcount.get(classes[i])).intValue() > 15 &&
            Double.compare(s.getSupport(), minsup[i]) <= 0) {
          s.setConfidence(0d);
          s.setSupport(0d);
        }
      }
    }
  }*/

  /**
   * leaves10000
   *
   * if a n-gram and its sub-grams have the same sup and conf, set sub-grams to be zero
       * not very effective, if leaves-aaa-bbb is good, then leaves will be removed,
   * but in another example it has leaves-bbb-aaa, it will be missed
   */
  /*private void pruneTerms() {
    for (int i = n - 1; i >= 0; i--) {
      Enumeration en = semantics[i].elements();
      while (en.hasMoreElements()) {
        Term t = (Term) en.nextElement();
        Hashtable scores = (Hashtable) t.getScore();
        Enumeration labels = scores.keys();
        while (labels.hasMoreElements()) {
          String label = (String) labels.nextElement();
          SCScore score = (SCScore) scores.get(label);
          String ngram = t.getTerm();
          ArrayList[] subgrams = getNGrams(ngram.replaceAll(symbol, " "));
          int size = subgrams.length;
          for (int j = size - 2; j >= 0; j--) {
            for (int k = 0; k < subgrams[j].size(); k++) {
              Term subt = new Term( (String) subgrams[j].get(k));
              subt = (Term) semantics[j].findElement(subt);
              SCScore sc = (SCScore) ( (Hashtable) subt.getScore()).get(label);
              if (sc != null) {
       if (Double.compare(score.getConfidence(), sc.getConfidence()) ==
                    0 &&
                    Double.compare(score.getSupport(), sc.getSupport()) == 0) {
                  sc.setConfidence(0d);
                  sc.setSupport(0d);
                  subt.setScore(sc, label);
                }
              }
            }
          }
        }
      }
    }
     }*/

  /**
   * query method: get semantices class for a n-gram phrase
   * n = n
   * while n > 0
   *    find semantic classes for n-gram that have a confidence >= threshold
   *    add (class, score) to a treeset that is sorted by support
   * return treeset.toarray
   *
   * @param ngram has format of t1-t2-tn
   * @param threshold confidence threshold
   * @return classes in order: sort on support, and pick confidence > threshold
   */

  public SemanticLabel[] semanticClassFor(String ngramstring, float conf,
                                          float sup, int n) {
    //int n = semantics.length;
    ArrayList[] ngrams = getNGrams(ngramstring, n);
    ArrayList labels = getClassForNGrams(ngrams, conf, sup, true);
    if (uncertain(labels, ngramstring)) { //if petioles fails, try petiole
      //labels = getClassForNGrams(ngrams, conf, sup, true);
    }
    return (SemanticLabel[]) labels.toArray(new SemanticLabel[1]);
  }

  /**
   * problem: if uncertain due to :endsWith, then try other form. it could have transformed "endsWith" replace original, is this what we want?
   * this situation will not occur because findMatchForm will return the original if it exists in the tree.
   * @param labels
   * @return true if labels contain all ""s or the only the last label is non-empty, otherwise return false
   */
  private boolean uncertain(ArrayList labels, String ngramstring) {
    if (labels.size() < 1 ||
        (labels.size() == 1 &&
         ngramstring.endsWith( ( (SemanticLabel) labels.get(0)).getOngram()))) {
      return true;
    }
    return false;

    /*for(int i = 0; i < labels.length-1; i++){
      if(labels[i].compareTo("") != 0){
        return false;
      }
         }
         return true;*/
  }

  /**
   * check for each ngrams for its semantic class
   * if there is no semantic class found for a ngram, let it be ""
   * starting with the longest ngrams
   *
   * old version didn't save "", but give classes more weight if the n gram contain 1st word of the string
   * @param ngrams
   * @param conf
   * @param sup
   * @param tryforms
   * @return
   */
  public ArrayList getClassForNGrams(ArrayList[] ngrams, float conf,
                                     float sup, boolean tryforms) {
    int ran = ngrams.length;
    ArrayList labels = new ArrayList();
    //String[] ordered = new String[ran * (ran + 1) / 2]; //a-b-c, a-b, b-c, a, b, c
    //int index = 0;
    float confcopy = conf;
    float supcopy = sup;
    for (int i = ran - 1; i >= 0; i--) {
      Iterator it = ngrams[i].iterator();
      while (it.hasNext()) {
        String ngram = (String) it.next();
        String ongram = ngram;
        if (i < semantics.length) {
          //ordered[index++] = "";//n-grams that are too long for semantics
          if (ngram.split(symbol).length < 2) {
            conf = confcopy + (1 - confcopy) * 0.25f;
            //conf = confcopy;
            sup = supcopy * 1.5f;
          }
          else if (ngram.split(symbol).length > 2) {
            conf = confcopy;
            sup = supcopy * 0.25f;
          }
          else {
            conf = confcopy;
            sup = supcopy;
          }

          if (tryforms) {
            ngram = findMatchForm(ngram, semantics[i], conf, sup);
          }

          Term t = new Term(ngram);
          /*if(t.getTerm().compareTo("Buds") == 0){
            System.out.println("Buds");
                   }*/
          if (semantics[i].containsElement(t)) {
            t = (Term) semantics[i].findElement(new Term(ngram));
            Hashtable chash = (Hashtable) t.getScore();
            Enumeration keys = chash.keys();
            float maxsup = 0f;
            float msconf = 0f;
            String clabel = "";
            while (keys.hasMoreElements()) {
              String label = (String) keys.nextElement();
              SCScore score = (SCScore) chash.get(label);
              //if (t.getTerm().charAt(0) > 'Z') { //'a'=97 'Z'=90 test for lower case terms for level 1
              float s = score.getSupport();
              float c = score.getConfidence();
              if (Double.compare(c, conf) >= 0 &&
                  Double.compare(s, sup) >= 0) {
                if (s > maxsup) {
                  maxsup = s;
                  msconf = c;
                  clabel = label;
                }
              }
            }
            //ordered[index++] = clabel; //may contain more than one same class labels
            if (clabel.compareTo("") != 0) {
              labels.add(new SemanticLabel(ongram, t.getTerm(), clabel, msconf,
                                           maxsup, "S"));
            }
          }
          //else {
          //  ordered[index++] = "";
          //}
        }
      }
    }
    //return ordered;
    return labels;
  }

  /*private String[] getClassForNGrams(ArrayList[] ngrams, float conf,
                                    float sup, boolean tryforms) {
   TreeSet ordered = new TreeSet(new TermSCScore());
   int ran = ngrams.length;
   for (int i = ran - 1; i >= 0; i--) {
     Iterator it = ngrams[i].iterator();
     int order = 0;
     while (it.hasNext()) {
       order++;
       String ngram = (String) it.next();
       if (ngram.split(symbol).length < 2) {
         conf += (1 - conf) * 0.25;
         sup *= sup * 1.5;
       }
       if (tryforms) {
         ngram = findMatchForm(ngram, semantics[i], conf, sup);
       }
       Term t = new Term(ngram);
       if (semantics[i].containsElement(t)) {
         t = (Term) semantics[i].findElement(new Term(ngram));
         Hashtable chash = (Hashtable) t.getScore();
         Enumeration keys = chash.keys();
         while (keys.hasMoreElements()) {
           String label = (String) keys.nextElement();
           SCScore score = (SCScore) chash.get(label);
           //if (t.getTerm().charAt(0) > 'Z') { //'a'=97 'Z'=90 test for lower case terms for level 1
           if (Double.compare(score.getConfidence(), conf) >= 0 &&
               Double.compare(score.getSupport(), sup) >= 0) {
             int nvalue = t.getTerm().split(symbol).length;
             int weight = order == 1 ? 1 + nvalue : nvalue; //a, a-b, and a-b-c are weight more than b, c, b-c
             ordered.add(new TermSCScore(label, score, weight)); //may contain more than one same class labels
           }
         }
       }
     }
   }
   String labels = "";
   ArrayList classlabels = new ArrayList();
   Iterator it = ordered.iterator();
   while (it.hasNext()) {
     TermSCScore ts = (TermSCScore) it.next();
     String label = (String) ts.getTerm();
     if (labels.indexOf(label) < 0) {
       labels += " " + label;
       classlabels.add(label);
     }
   }
   return (String[]) classlabels.toArray(new String[1]);
    }
   */
  /**
   * try plural and single forms of grams
   * @param ngram
   * @param semantics
   * @return
   */
  private String findMatchForm(String gram, BinarySearchTree semantics,
                               float conf, float sup) {
    String copy = gram;
    if (goodForm(gram, semantics, conf, sup)) {
      return gram;
    }
    //leaves10000, try to match case
    if (goodForm(gram.toLowerCase(), semantics, conf, sup)) {
      return gram.toLowerCase();
    }

    if (goodForm(capitalize(gram), semantics, conf, sup)) {
      return capitalize(gram);
    }
    //end leaves10000
    //try other forms: first make single
    if (gram.endsWith("ses")) {
      gram = gram.substring(0, gram.length() - 2);
      if (goodForm(gram, semantics, conf, sup)) {
        return gram;
      }
      else {
        gram = gram + "eses"; //make plural
        if (goodForm(gram, semantics, conf, sup)) {
          return gram;
        }
      }
    }
    else if (gram.endsWith("s")) {
      gram = gram.substring(0, gram.length() - 1);
      if (goodForm(gram, semantics, conf, sup)) {
        return gram;
      }
      else {
        gram = gram + "ses";
        if (goodForm(gram, semantics, conf, sup)) {
          return gram;
        }
      }
    }
    else { //make plural for other endings
      gram = gram + "s";
      if (goodForm(gram, semantics, conf, sup)) {
        return gram;
      }
      else {
        gram = gram.substring(0, gram.length() - 1) + "es";
        if (goodForm(gram, semantics, conf, sup)) {
          return gram;
        }
      }
    }

    return copy;
  }

  private static String capitalize(String str) {
    char first = str.charAt(0);
    if (first >= 'A' && first <= 'Z') {
      return str;
    }
    else {
      String start = str.substring(0, 1);
      String rest = str.substring(1);
      StringBuffer sb = new StringBuffer().append(start.toUpperCase()).append(
          rest);
      return sb.toString();
    }
  }

  /**
   * update: goodForm only when there are classes that scored beyond the thresholds 5/10/04 leaves1000
   * @param gram
   * @param semantics
   * @param conf
   * @param sup
   * @return
   */
  private boolean goodForm(String gram, BinarySearchTree semantics, float conf,
                           float sup) {
    Term t = new Term(gram);
    if (semantics.containsElement(t)) {
      t = (Term) semantics.findElement(new Term(gram));
      Hashtable chash = (Hashtable) t.getScore();
      Enumeration keys = chash.keys();
      while (keys.hasMoreElements()) {
        String label = (String) keys.nextElement();
        SCScore score = (SCScore) chash.get(label);
        if (Double.compare(score.getConfidence(), conf) >= 0 &&
            Double.compare(score.getSupport(), sup) >= 0) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * semantics: terms by classes
   * make it: classes by terms
   *
   * @return one TreeSet for a class. TreeSet contains terms ordered by their confidence and support
   */
  public TreeSet[] termsForClasses() {
    TreeSet[] terms = new TreeSet[classes.length];
    for (int i = 0; i < classes.length; i++) {
      terms[i] = new TreeSet(new TermSCScore());
    }
    for (int i = 0; i < semantics.length; i++) {
      Enumeration en = semantics[i].elements();
      while (en.hasMoreElements()) {
        Term t = (Term) en.nextElement();
        for (int c = 0; c < classes.length; c++) {
          SCScore score = (SCScore) t.getScore(classes[c]);
          if (score != null) {
            terms[c].add(new TermSCScore(t.getTerm(), score, 0));
          }
        }
      }
    }
    return terms;
  }

  public Hashtable getClasscountHash(){
    return classcount;
  }
  /**
   * hashtable => int[]
   * @return
   */
  public int[] getClasscount() {
    int[] count = new int[classes.length];
    for (int i = 0; i < count.length; i++) {
      if (classcount.containsKey(classes[i])) { //non-specified is not in classcount
        count[i] = ( (Integer) classcount.get(classes[i])).intValue();
      }
      else {
        count[i] = 0;
      }
    }
    return count;
  }

  public void setClassCount(int[] classcount, String[] classes){
    this.classcount.clear();
    for(int i = 0; i < classes.length; i++){
      this.classcount.put(classes[i], new Integer(classcount[i]));
   }
   //updateMinsup(classes);
   this.classes = classes;
  }

 /* private void updateMinsup(String[] classes) {
    float[] temp = new float[classes.length];
    Hashtable msup = new Hashtable();
    for (int i = 0; i < this.classes.length; i++) {
      msup.put(this.classes[i], new Float(minsup[i]));
    }
    for (int i = 0; i < classes.length; i++) {
      if (msup.containsKey(classes[i])) {
        temp[i] = ( (Float) msup.get(classes[i])).floatValue();
      }
      else {
        temp[i] = 1f;
      }
    }
    this.minsup = temp;
  }*/
  public void setClasses(String[] classes){
    //update minsup
    //updateMinsup(classes);
    this.classes = classes;
  }

  public BinarySearchTree[] getSemantics(){
    return semantics;
  }

  public void setSemantics(BinarySearchTree[] semantics, String[] classes){
    this.semantics = semantics;
    //updateMinsup(classes);
    this.classes = classes;
  }

  public String[] getClasses(){
    return classes;
  }

  public void printSemantics() {
    int n = semantics.length;
    for (int i = 0; i < n; i++) {
      Enumeration en = semantics[i].elements();
      while (en.hasMoreElements()) {
        Term t = (Term) en.nextElement();
        System.out.print(t.getTerm() + " : ");
        for (int c = 0; c < classes.length; c++) {
          if (t.getOccuranceIn(classes[c]) != 0) {
            System.out.print(classes[c] + " " +
                             ( (SCScore) t.getScore(classes[c])).toString() +
                             " ");
          }
        }
        System.out.println();
      }
    }
  }

  public void printTermsForClasses(float sup, float conf) {
    TreeSet[] termlists = termsForClasses(); //classes by terms
    for (int i = 0; i < classes.length; i++) {
      Iterator it = termlists[i].iterator();
      System.out.println("\n" + classes[i] + " top terms:");
      while (it.hasNext()) {
        TermSCScore ts = (TermSCScore) it.next();
        if (Double.compare(ts.getScore().getConfidence(), conf) >= 0 &&
            Double.compare(ts.getScore().getSupport(), sup) >= 0) {
          System.out.print(ts.toString() + " ");
        }
      }
      System.out.println();
    }
    System.out.println("\n");
  }

  /**
   * maximum likelyhood score = occurance of t in c /occurance of t
   * that is, confidence
   * and not support, support = occurance of t
   *
   * long -> short
   * discount short if long pass the thresholds
   * do not discount when merging two trees, they have already been discounted when they were built
   */
  public void scoreTerms(boolean discount, float suphold, float confhold) {
    int n = semantics.length;
    for (int i = n - 1; i >= 0; i--) {
      Enumeration en = semantics[i].elements();
      en = semantics[i].elements();
      while (en.hasMoreElements()) {
        Term t = (Term) en.nextElement();
        if(t.getTerm().compareTo("Aug")==0){
          System.out.println();
        }
        for (int c = 0; c < classes.length; c++) {
          if (classcount.containsKey(classes[c]) &&
              t.getOccuranceIn(classes[c]) != 0) {
            float conf = (float) t.getOccuranceIn(classes[c]) /
                t.getTotalOccurance();
            float sup = (float) t.getTotalOccurance() /
                ( (Integer) classcount.get(classes[c])).intValue();
            sup = sup > 1f ? 1f : sup;
            //float sup = (float) t.getTotalOccurance();
            t.setScore(new SCScore(sup, conf), classes[c]);
            /*if (i == 0 && Double.compare(conf, confhold) > 0 &&
                Double.compare(sup, suphold) > 0) {
              minsup[c] = minsup[c] > sup ? sup : minsup[c];
            }*/
            //set different suphold according to training size leaves1000
            suphold = (1 +(float)Math.log( ( (Integer) classcount.get(classes[c])).
                                       doubleValue())) /
                ( (Integer) classcount.get(classes[c])).intValue();
            //end leaves1000
            /*if (discount && i != 0 && Double.compare(conf, confhold) > 0 &&
                Double.compare(sup, suphold) > 0 &&
                ( (Integer) classcount.get(classes[c])).intValue() > minsize) {*/
            //only discount subgrams of a full ngram, because only full ngrams generate subgrams
            //discount all subgrams will cause nagivate classOccurance because multiple discounts
            //may be imposed on a subgram.
            if (discount && i == n-1 && Double.compare(conf, confhold) > 0 &&
                Double.compare(sup, suphold) > 0 &&
                ( (Integer) classcount.get(classes[c])).intValue() > minsize) {
              discountShorters(t, classes[c], t.getOccuranceIn(classes[c]));
            }
          }
        }
      }
    }
  }

  private void discountShorters(Term term, String label, int discount) {
    String termstring = term.getTerm();
    termstring = termstring.replaceAll(symbol, " ");
    ArrayList[] shorters = getNGrams(termstring, semantics.length);
    for (int j = shorters.length - 2; j >= 0; j--) {
      Iterator it = shorters[j].iterator();
      while (it.hasNext()) {
        Term tm = new Term( (String) it.next());
        Term t = (Term) semantics[j].findElement(tm);
        if(t.getTerm().compareTo("Aug")==0){
          System.out.println();
        }
        t.setCountForClass(t.getOccuranceIn(label) - discount, label);
      }
    }
  }

  /**
   * fetch 1- to n-gram of lead tokens from each sentence in elements of xml
   * assumes flat xml
   * @param xml
   */
  private void getCountFrom(String xml, int n, String ignored, String[] delim) {
    if (xml == null || xml.compareTo("") == 0) {
      return;
    }
    Pattern p = Pattern.compile(".*?<(.*?)>([^<]*?)</\\1>(.*)");
    while (xml.compareTo("") != 0) {
      Matcher m = p.matcher(xml);
      if (m.lookingAt()) {
        String elem = m.group(1);
        String text = m.group(2).trim();
        xml = m.group(3).trim();
        if (ignored.indexOf(" " + elem + " ") < 0) {
          //if(elem.compareTo("leaflet-blade") == 0){
          //  updateWith("leaf-blade", text);
          //}else{
          updateWith(elem, text, n, delim);
          //}
        }
      }
    }
  }

  private void incrementClasscount(String elem) {
    if (classcount.containsKey(elem)) {
      Integer count = new Integer(1 +
                                  ( (Integer) classcount.get(elem)).intValue());
      classcount.put(elem, count);
    }
    else {
      classcount.put(elem, new Integer(1));
    }
  }

  /**
   * fetch 1- to n-gram from content to update corresponding n-gram BTree
   * n-gram takes format of token1-token2-...-tokenn
   * @param tag
   * @param content
   */
  private void updateWith(String tag, String content, int n, String[] delim) {
    String cont = ""; //a sentence in content
    while (content.compareTo("") != 0) {
      int index = learning.Utilities.findCutPoint(content, delim);
      //int index = learning.Utilities.findCutPoint(content, new String[]{";", "."});
      if (index != -1) {
        cont = content.substring(0, index + 1);
      }
      else {
        cont = content;
      }
      content = content.substring(cont.length());
      int stop = learning.Utilities.stopAt(cont);//stop before any punctuation mark
      String nstring = cont.substring(0, stop);
      ArrayList[] ngrams = getNGrams(nstring, n);
      if (ngrams != null) {
        for (int i = 0; i < ngrams.length; i++) {
          for (int j = 0; j < ngrams[i].size(); j++) {
            addTerm(semantics[i], (String) ngrams[i].get(j), tag);
          }
        }
        incrementClasscount(tag); //increment once for a content seg
      }
    }
  }

  /**
   * extract ngrams from content:a-b-c => a-b-c, a-b, a
   * @param content
   * @return each row contains ngrams of same length
   */
  /*public ArrayList[] getNGrams(String cont){
    ArrayList[] ngrams = new ArrayList[this.n];
    String[] tokens = learning.Tokenizer.tokenize(cont); //tokens without any surrounding spaces
    String token = "";
    int len = this.n > tokens.length? tokens.length : this.n;
    for (int i = 0; i < len; i++) {
      if (tokens != null) {
        token += i == 0 ? tokens[i] : symbol + tokens[i];
        ngrams[i].add(token);
      }
    }
    return ngrams;
     }*/

  /**
   * a b c => 0 C
   *          0 B
   *          0 A
   *          1 B_C
   *          1 A_B
   *          2 A_B_C
   * @param cont is standardized using tokenization and put one space between tokens
   * @return
   */
  public ArrayList[] getNGrams(String cont, int n) {
    String[] tokens = learning.Tokenizer.tokenize(cont, useStopList); //tokens without any surrounding spaces
    if (tokens == null) {
      return null;
    }
    int len = n > tokens.length ? tokens.length : n;//smaller one
    ArrayList[] ngrams = learning.Utilities.getLENGrams(tokens, len, symbol);
    return ngrams;
  }


  public int getN() {
    return semantics.length;
  }

  private void addTerm(BinarySearchTree tree, String term, String tag) {
    term = term.replaceFirst("[\\p{Punct}&&[^=]]$","");
    if(term.compareTo("") == 0){
      return;
    }
    Term t = new Term(term, tag, classes);
    if (tree.containsElement(t)) {
      t = (Term) tree.findElement(t);
      t.addOccuranceIn(tag);
    }
    else {
      tree.addElement(t);
    }

  }

  /**
   * a term's support and confidence scores for a class
   *
   */
  public class TermSCScore
      implements Comparator, Serializable {
    private String term = null;
    private SCScore score = null;
    private int weight = 0;

    public TermSCScore() {

    }

    public TermSCScore(String term, SCScore score, int weight) {
      this.term = term;
      this.score = score;
      this.weight = weight;
    }

    public SCScore getScore() {
      return score;
    }

    public String getTerm() {
      return term;
    }

    public int getWeight() {
      return weight;
    }



    /*public int compare(Object object, Object object1) {
      TermSCScore ts1 = (TermSCScore) object;
      TermSCScore ts2 = (TermSCScore) object1;
      SCScore s1 = ts1.getScore();
      SCScore s2 = ts2.getScore();
      int result = Double.compare(s1.getSupport(), s2.getSupport());
      if (result == 0) {
        result = Double.compare(s1.getConfidence(), s2.getConfidence());
      }
      if (result == 0) {
        result = ts1.getTerm().compareTo(ts2.getTerm());
      }
      return result * -1; //decending order
         }*/

    /**
     * 1-grams' support can't be compared with 3-grams' support directly.
     * @param object
     * @param object1
     * @return
     */
    public int compare(Object object, Object object1) {
      TermSCScore ts1 = (TermSCScore) object;
      TermSCScore ts2 = (TermSCScore) object1;
      SCScore s1 = ts1.getScore();
      SCScore s2 = ts2.getScore();
      int grams1 = ts1.getTerm().split(symbol).length;
      int grams2 = ts2.getTerm().split(symbol).length;
      int result = grams1 - grams2;
      if (result == 0) {
        result = ts1.getWeight() - ts2.getWeight();
      }
      if (result == 0) {
        result = Double.compare(s1.getSupport(), s2.getSupport());
      }
      if (result == 0) {
        result = Double.compare(s1.getConfidence(), s2.getConfidence());
      }
      if (result == 0) {
        result = ts1.getTerm().compareTo(ts2.getTerm());
      }
      return result * -1; //decending order
    }

    public String toString() {
      return term + "[" + score.toString() + "]";
    }


    public boolean equals(Object object) {
      TermSCScore t = (TermSCScore) object;
      SCScore s1 = t.getScore();

      return Double.compare(s1.getConfidence(), score.getConfidence()) == 0 &&
          Double.compare(s1.getSupport(), score.getSupport()) == 0 &&
          t.getTerm().compareTo(term) == 0;
      //return t.getTerm().compareTo(term) == 0;
    }

  }

  public static void main(String[] argv) {
    //System.out.println(capitalize("story"));

    /*TermSemantic sc = new TermSemantic();
     ArrayList[] al = sc.getNGrams("A B C D");
     for (int i = 0; i < al.length; i++) {
       for (int j = 0; j < al[i].size(); j++) {
         System.out.println(al[i].get(j));
       }
     }*/

    /*String[] classes = {
        "taxon", "plant-habit-and-life-style", "roots", "stems", "buds",
        "leaves", "inflorescences", "flowers", "pollen", "fruits", "cones",
        "seeds", "spore-related-structures", "gametophytes", "chromosomes",
        "timeline", "compound", "other-features", "other-information"};*/
    String[] classes = {
        "leaf-general", "compound", "multiple", "other-information", "petiole",
        "leaf-blade",
        "leaflet-blade", "stipule", "tendril", "gland", "ligule", "sheath",
        "abscission-zone",
        "leaflet-general", "buds", "flowers", "spore-related-structures",
        "indument", "spine", "auriole",
        "cataphyll", "rachis", "crownshaft", "petiolule"};
    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\FNA\\descriptionsWithoutHTML-markedup\\";
    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\FOC\\descriptions2-markedup\\";
    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level2\\trainingdir-fna500-merged-bysent-leaves-blades\\";
    String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level2\\trainingdir-fna500-merged-bysent-stems\\";

    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level1\\fna-test-plain-markedup\\";
    File srcdir = new File(dir);
    File[] files = srcdir.listFiles();
    //TermSemantic sc = new TermSemantic(files, classes, 3, 0.8, 0.035,
    //                                   new String[] {".", ";"}, true);
    //visitor.Serializer.serialization(sc.semanticfile, sc);
    //sc.printTermsForClasses(0.01, 0.8);
    /*String[] sclasses = sc.semanticClassFor("Nuts-clusters-involucral", 0.8);
        System.out.println("Nuts-clusters-involucral classes:");
        for (int i = 0; i < sclasses.length; i++) {
     System.out.println(sclasses[i]);
        }*/

  }

}
