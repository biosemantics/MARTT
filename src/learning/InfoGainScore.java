package learning;

import jds.collection.BinarySearchTree;
import java.util.Enumeration;
import java.util.regex.*;
import java.io.File;

/**
 * <p>Title: InfoGainScore</p>
 * <p>Description: infomation gain score</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

// this class calculate information gain scores for each term

public class InfoGainScore {
  private BinarySearchTree srcData = null;
  private int[] classCount = null;
  private String[] classes = null;

  /**
   * @param srcData term by class binary search tree with <code>Term</code> as nodes.
   * @param classes a array of class labels.
   * @param classCount the number of examples in each class.
   */
  public InfoGainScore(BinarySearchTree srcData, String[] classes,
                       int[] classCount) {
    this.srcData = srcData;
    this.classes = classes;
    this.classCount = classCount;
  }

  /**
   * IG(term)=Entropy(entire collection) -
   *          { Entropy(subcollection with term)*|sub|/|entire| +
   *            Entropy(subcollection without term)*|sub|/|entire|}
   *
   * Entropy(C) =
   * -1 * Sum-for-all-classes-in-C{log-of-base-2(|class|/|entire|) * |class|/|entir|}
   *
   * @return a binarySearchTree of <code>ScoredTerms</code>
   *
   */
  public BinarySearchTree scoredTerms() {
    //BinarySearchTree scored = new BinarySearchTree(new ScoredTermOrdered());
    BinarySearchTree scored = new BinarySearchTree(new TermScoreComparator(-1));
    int c, totalExps = 0;
    float entroC;

    entroC = Utilities.getEntropy(classCount);
    for (c = 0; c < classes.length; c++) {
      totalExps += classCount[c];
    }
    /*System.out.println("total terms: " + srcData.size());
           Enumeration test = srcData.elements();
           while(test.hasMoreElements()){
      Term tm = (Term) test.nextElement();
      System.out.println("process term:" + tm.getTerm());
           }
           System.out.println("================================");*/
    Enumeration terms = srcData.elements();

    while (terms.hasMoreElements()) {
      //for each term find its info gain
      Term term = (Term) terms.nextElement();
      float logTerm1 = 0;
      float logTerm0 = 0; //the log term for the subcollection with and without the term
      int n1 = 0;
      int n0 = 0; //total number of examples with and without the term
      int exps1, exps0; //number of examples of a class with and without the term
      float entroC1, entroC0; //entropy of subcollection with and without the term
      float infoGain;
      for (c = 0; c < classes.length; c++) {
        exps1 = term.getOccuranceIn(classes[c]);
        exps0 = classCount[c] - exps1;
        n1 += exps1;
        n0 += exps0;
        if (exps1 != 0) {
          logTerm1 +=
              (float) (exps1 * Math.log( (double) exps1) / Math.log(2d));
        }
        if (exps0 != 0) {
          logTerm0 +=
              (float) (exps0 * Math.log( (double) exps0) / Math.log(2d));
        }
      }
      if (n1 == 0) {
        n1 = 1;
      }
      if (n0 == 0) {
        n0 = 1;
      }
      entroC1 = -1f * logTerm1 / n1 +
          (float) (Math.log( (double) n1) / Math.log(2d));
      entroC0 = -1f * logTerm0 / n0 +
          (float) (Math.log( (double) n0) / Math.log(2d));
      infoGain = entroC -
          (entroC1 * n1 / totalExps + entroC0 * n0 / totalExps);
      //ScoredTermOrdered sTerm = new ScoredTermOrdered(term.getTerm(), infoGain);
      term.setScore(infoGain, term.getTerm());
      //System.out.println(term.getTerm()+":"+infoGain);
      scored.addElement(term);
    }

    return scored;
  }

  public static void main(String[] args) {
    //String srcData = "/home/hongcui/ThesisProject/Exp/Exp1/testExampleObjects/";
    //String srcData = "/home/hongcui/ThesisProject/Exp/Exp1/MarkedExamplesObjects/";
    /*String srcData = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\Exp1\\MarkedExamplesObjects\\";
         String [] classes = {"PlantHabitAndLifeStyle","Stems","Leaves","Flowers",
        "Fruits","Cones","Seeds","Roots","SporeRelatedStructures","Flowering","Fruiting",
        "SeedMaturity","Compound","Chromosomes","Others"};*/
    String srcData =
        "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\Level1\\trainingdir-foc500-merged\\";
    String[] classes = {
        "description", "taxon", "plant-habit-and-life-style", "roots", "stems",
        "buds", "leaves", "inflorescences", "flowers", "pollen", "fruits",
        "cones", "seeds",
        "spore-related-structures", "gametophytes", "chromosomes", "phenology",
        "compound", "other-features", "other-information"};

    File dir = new File(srcData);
    File[] files = dir.listFiles();
    String[] trainingexamples = new String[files.length];
    for (int f = 0; f < files.length; f++) {
      String xml = Utilities.readFile(files[f]);
      Pattern p = Pattern.compile(".*?<description>(.*?)</description>.*");
      Matcher m = p.matcher(xml);
      if (m.lookingAt()) {
        trainingexamples[f] = m.group(1);
      }
      p = Pattern.compile("(.*?)<taxon>.*?</taxon>(.*)");
      m = p.matcher(trainingexamples[f]);
      if (m.lookingAt()) {
        trainingexamples[f] = m.group(1) + m.group(2);
      }

    }

    //TermClassesTree termTree1 = new TermClassesTree(filenames, classes, 2);
    TermClassTree termTree1 = new TermClassTree(classes, true, 1);
    BinarySearchTree bst = termTree1.classTermMatrix(trainingexamples, 2);
    int[] classCount = termTree1.getClassCount();
    //System.out.println(Utilities.printBTree(bst));
    InfoGainScore score1 = new InfoGainScore(bst, classes, classCount);
    BinarySearchTree scored = score1.scoredTerms();
    //System.out.println(Utilities.printBTree(scored));
    System.out.println(Utilities.printScoredBTree(scored));
  }
}
