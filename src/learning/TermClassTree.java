package learning;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import jds.collection.BinarySearchTree;
import visitor.ElementComposite;
import visitor.ElementComponent;
import visitor.ElementLeaf;


/**
 * <p>Title: TermClassTree</p>
 * <p>Description: computes and manipulates term by class binary tree for training data
 *                 It makes use of Term, Stemmer, TextPreprocessing, Tokenizer and Utilities </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public class TermClassTree {
  private String[] classes = null;
  private int[] classCount = null;
  private int[] termCounts = null;
  private int termCount = 0; //total occurance of all/unique terms in all classes
  private boolean unique = false;
  private int ngram = 1; //default 1-grm

  /**
   *
   * @param classes an array of class labels
   *
   */
  public TermClassTree(String[] classes, boolean unique, int ngram) {
    this.classes = classes;
    this.unique = unique;
    this.ngram = ngram;
    classCount = new int[classes.length];
    termCounts = new int[classes.length];
    for (int c = 0; c < classes.length; c++) {
      classCount[c] = 0;
      termCounts[c] = 0;
    }
  }

  /**
   *
   * @param trainingexamples the data set on which the class-term matrix is built.
   * they are flat xml (a set of parallel elements) or nested-element xml. tags with
   * the same name are grouped as one tag.
   * @param selection # of terms to be counted
   * @return a <code>BinarySearchTree</code> of <code>Term</code>
   */
  public BinarySearchTree classTermMatrix(String[] trainingexamples,
                                          int selection) {
    ElementComposite ec = new ElementComposite();
    for (int i = 0; i < trainingexamples.length; i++) {
      String example = trainingexamples[i].replaceAll("^\\s+", "").trim(); //remove leading/ending spaces
      example = learning.Utilities.wellForm(example);
      ec.addTrain(example, "");//text along with filename
    }

    return classTermMatrix(ec, selection);
    /*Term bt = new Term();
    BinarySearchTree bst = new BinarySearchTree(bt);

    buildClassTermMatrixFromEC(ec, bst, selection);
    return bst;*/
  }

  public BinarySearchTree classTermMatrix(ElementComponent ec,
                                            int selection) {

      Term bt = new Term();
      BinarySearchTree bst = new BinarySearchTree(bt);

      buildClassTermMatrixFromEC(ec, bst, selection);
      return bst;
    }



  /**
   * recursive traverse
   * @param ec ElementComponent
   * @param bst BinarySearchTree
   */
  private void buildClassTermMatrixFromEC(ElementComponent ec, BinarySearchTree bst, int selection){
    //traverse ec
    if(ec instanceof ElementLeaf){
      doComponent(ec, bst, selection);
    }else{
      Iterator children = ec.iterator();
      while(children.hasNext()){
        ElementComponent comp = (ElementComponent)children.next();
        if(!(comp instanceof ElementLeaf)){
            doComponent(comp, bst, selection);
        }
        buildClassTermMatrixFromEC(comp, bst, selection);
      }
    }

  }

  private void doComponent(ElementComponent ec, BinarySearchTree bst, int selection){
      String classl = ec.getTag();
      ArrayList examples = ec.getTrainingExamples();
      Iterator it = examples.iterator();
      while(it.hasNext()){
        String text = (String)it.next();
        text = text.replaceAll("</?[^<]*?>"," ").replaceAll("^\\s+", "").trim();
        if (text.trim().compareTo("") == 0) {//@todo: remove <tag> from text
          continue;
        }
        Iterator terms = terms(text, selection);
        addToClassCount(classl);
        int termCountClass = 0;
        while (terms.hasNext()) {
          String t = (String) terms.next();
          Term term = new Term(t, classl, classes);
          if (bst.containsElement(term)) {
            ( (Term) bst.findElement(term)).addOccuranceIn(classl);
          }
          else {
            bst.addElement(term);
          }
          termCount++;
          termCountClass++;
        }
        addToTermCounts(classl, termCountClass);

      }
  }
    /*Term bt = new Term();
    BinarySearchTree bst = new BinarySearchTree(bt);
    int size = trainingexamples.length; //size of training set
    for (int i = 0; i < size; i++) {
      String example = trainingexamples[i].replaceAll("^\\s+", "").trim(); //remove leading/ending spaces
      //@todo: treat nested xml as well
      while (example.compareTo("") != 0) {
        String classl = Utilities.getFirstTag(example);
        String text = Utilities.getFirstText(example).replaceAll("^\\s+", "").trim();
        example = Utilities.removeFirstElement(example);
        example = example.replaceAll("^\\s+", "").trim();;
        if (text.trim().compareTo("") == 0) {
          continue;
        }
        Iterator terms = terms(text, selection);
        addToClassCount(classl);
        int termCountClass = 0;
        while (terms.hasNext()) {
          String t = (String) terms.next();
          Term term = new Term(t, classl, classes);
          if (bst.containsElement(term)) {
            ( (Term) bst.findElement(term)).addOccuranceIn(classl);
          }
          else {
            bst.addElement(term);
          }
          termCount++;
          termCountClass++;
        }
        addToTermCounts(classl, termCountClass);
      }
    }
    return bst;
*/


  public int[] getClassCount() {
    return classCount;
  }

  public int[] getTermCounts() {
    return termCounts;
  }


  public int getTermCount() {
    return termCount;
  }

  /**
   * @param a string text in an element
   * @param selection decides what terms are taken into consideration
   * @param selection selects what terms should be include in the tree.
   *                  0 no term
   *                  n > 0 first n terms in each sentence of each element
   *
   *                  to do: -1 for POS selection
   * @return an array of all/unique terms in the string
   */
  public Iterator terms(String text, int selection) {
    //get sentences. @todo: improve sentence algorithm
    //Pattern p = Pattern.compile("\\W[A-Z]");
    Pattern p = Pattern.compile("\\.\\s*[A-Z]");
    Matcher m = p.matcher(text);
    ArrayList sentences = new ArrayList();
    int start = 0;
    while (m.find()) {
      sentences.add(text.substring(start, m.start()));
      start = m.start();
    }
    sentences.add(text.substring(start));
    if(unique){
      HashSet unique = new HashSet();
      Iterator sit = sentences.iterator();
      while (sit.hasNext()) {
        String st = (String) sit.next();
        //String[] terms = new Tokenizer().tokenize(st, true);
        String[] terms = getNgrams(st);
        if (terms == null) {
          continue;
        }
        int count = 0;
        int total = selection > terms.length ? terms.length : selection;
        for (int t = 0; t < terms.length && count < total; t++) {
          if (terms[t].compareTo(TextPreprocessing.string) != 0) {
            if (unique.add(terms[t])) {
              count++;
            }
          }
        }
      }
      return unique.iterator();
    }else{
      ArrayList allterms = new ArrayList();
      Iterator sit = sentences.iterator();
      while (sit.hasNext()) {
        String st = (String) sit.next();
        //String[] terms = new Tokenizer().tokenize(st, true);
        String[] terms = getNgrams(st);
        if (terms == null) {
          continue;
        }
        int count = 0;
        int total = selection > terms.length ? terms.length : selection;
        for (int t = 0; t < terms.length && count < total; t++) {
          if (terms[t] != null && terms[t].compareTo(TextPreprocessing.string) != 0) {
            if (allterms.add(terms[t])) {
              count++;
            }
          }
        }
      }
      return allterms.iterator();
    }
  }

  private String[] getNgrams(String text){
    String[] tokens = text.split("\\s+");
    int len = tokens.length;
    ArrayList ngrams = new ArrayList();
    for(int j = 0; j <len - ngram + 1; j++){
      String token = "";
      for(int k = j; k < j +ngram; k++){
        token += " "+tokens[k];
      }
      //one n-gram is done
      String stop = "after|all|and|amp|an|are|at|as|a|been|by|from|in|is|of|on|or|some|then|than|the|to|with";

      Pattern p = Pattern.compile("\\w\\s*?\\p{Punct}\\s*?\\w"); //have a punct in between
      Matcher m = p.matcher(token);
      if(!m.lookingAt()){//no punct in the n-gram
        p = Pattern.compile(".*?\\b(:?" + stop + "|\\d+)\\b.*?");
        m = p.matcher(token);
        if (!m.lookingAt()) {//no stop word or numbers
          token = token.substring(1).replaceAll("\\p{Punct}", ""); //remove the first symbol
          if (token.compareTo("") != 0) {
            ngrams.add(token);
          }
        }
      }
    }
    return (String[])ngrams.toArray(new String[]{""});
  }

  /**
   * increase class1's count by 1
   * @param classlabel
   * @return class occurance for each class
   */
  private void addToClassCount(String classl) {
    for (int c = 0; c < classes.length; c++) {
      if (classl.compareTo(classes[c]) == 0) {
        classCount[c]++;
      }
    }
  }

  /**
   * increase class1's count by 1
   * @param classlabel
   * @return class occurance for each class
   */
  private void addToTermCounts(String classl, int count) {
    for (int c = 0; c < classes.length; c++) {
      if (classl.compareTo(classes[c]) == 0) {
        termCounts[c] += count;
      }
    }
  }


  public static void main(String[] args) {

    /*
    String example = "<taxon>ACORACEAE ACORUS calamus</taxon><leaves>Leaves basally white with pink or red, otherwise bright green; single midvein (secondary midrib) prominently raised above leaf surface, usually somewhat off-center, other veins barely or not raised; cross section rhomboid.Vegetative leaves to 1.75 m; sheathing base (proximal part of leaf) 22.1\u201366.5(\u201373.3) cm; distal part of leaf 31.9\u201395.8(\u2013117.6) ´  0.5\u20132 cm, 1.4\u20131.8 times longer than proximal part of leaf, margins sometimes undulate or crisped.Sympodial leaf (29.9\u2013)34.7\u2013159.1(\u2013183.9) cm, usually shorter than to nearly equal to vegetative leaves; sheathing base 16.1\u201376.4(\u2013100.1) cm; distal part of leaf 13.5\u201386.2(\u2013101.2) ´  0.4\u20131.9 cm.</leaves><inflorescences>Spadix (3.8\u2013)4.9\u20138.9 cm ´  5.3\u201310.8 mm at anthesis, post-anthesis spadix 5.5\u20138.7 cm ´  6\u201312.6 mm.</inflorescences><flowers>Flowers 3\u20134 mm; pollen grains not staining in aniline blue.</flowers><fruits>Fruits not produced in North America.</fruits><chromosomes>2n = 36.</chromosomes>";
    System.out.println(example);
    System.out.println(Utilities.getFirstTag(example));
    System.out.println(Utilities.getFirstText(example));
    System.out.println(Utilities.removeFirstElement(example));
    */

   String example = ".ACORACEAE .ACORUS calamus";
   TermClassTree tct = new TermClassTree(new String[]{"taxon"}, true, 1);
   Iterator it = tct.terms(example,10);
   while(it.hasNext()){
     System.out.println(it.next());
   }

    //String[] trainingexamples = args;
    /*
         String[] trainingexamples = new String[20];
         trainingexamples[0] = "";
         trainingexamples[1] = "";
         trainingexamples[2] = "";
         trainingexamples[3] = "";
     */
    /*String[] classes = {
        "description", "taxon", "plant-habit-and-life-style", "roots", "stems",
        "buds", "leaves", "inflorescences", "flowers", "pollen", "fruits",
        "cones", "seeds",
        "spore-related-structures", "gametophytes", "chromosomes", "timeline",
        "compound", "other-features", "other-information"};

    TermClassTree termTree1 = new TermClassTree(classes);
    BinarySearchTree bst = termTree1.classTermMatrix(trainingexamples, 2);
    System.out.println(Utilities.printBTree(bst));
    int[] classCount = termTree1.getClassCount();
    System.out.println(Utilities.print(classCount));
    int termCount = termTree1.getTermCount();
    System.out.println(termCount);
       */
  }

}
