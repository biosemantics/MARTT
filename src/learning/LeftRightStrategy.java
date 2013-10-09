package learning;

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.*;
import jds.collection.BinarySearchTree;

/**
 * <p>Title: LeftRightStrategy</p>
 * <p>Description: learn neccessary models for LeftRightSegmentation</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public abstract class LeftRightStrategy
    extends LearningStrategy {
  private int[] classcount = null;
  public LeftRightStrategy(String[] trainingexamples, String[] classes,
                           String[] delim, String alg) {
    super(trainingexamples, classes, delim, alg);
    //markup non-specified section with <non-specified>
    /*for (int i = 0; i < trainingexamples.length; i++) {
      System.out.println(trainingexamples[i]);
      System.out.println();
         }
         System.out.println("class list: =================");
         for (int i = 0; i < classes.length; i++) {
      System.out.println(classes[i]);
         }
         System.out.println();
     */

  }

  public void setClasses(String[] classes) {
    super.classes = classes;
  }

  public Model learnModel(boolean debug) {
    TermClassTree tct = new TermClassTree(classes, true, 1);
    /**@todo 500 => parameter*/
    BinarySearchTree bst = tct.classTermMatrix(trainingexamples, 500);
    if (debug) {
      System.out.println(Utilities.printBTree(bst));
    }
    classcount = tct.getClassCount();

    if (debug) {
      System.out.println(Utilities.print(classcount));
    }

    int termcount = tct.getTermCount();
    if (debug) {
      System.out.println(termcount);
    }
    BayesScore score = new BayesScore(bst, classes, classcount, termcount);
    ArrayList[] terms = score.scoredTerms(alg); //class by terms matrix, "true" to order by scores
    score.sortByScore(terms);
    if (debug) {
      printScoredTerms(terms, classes);
    }
    LearnDelimiter del = new LearnDelimiter(trainingexamples, classes);
    Vector rules = del.learn();
    //float matrix[][] = new LearnTransition(trainingexamples, classes, classcount).learnConditionMatrix();
    float matrix[][] = new LearnTransition(trainingexamples, classes,
                                           classcount).learnTransMatrix();
    return returnModel(classes, terms, rules, matrix, classcount);
  }

  public abstract Model returnModel(String[] classes, ArrayList[] terms, Vector rules, float[][] matrix, int[] classcount);

  private void printScoredTerms(ArrayList[] terms, String[] classes) {
    int len = terms.length;
    for (int i = 0; i < len; i++) {
      System.out.print(classes[i] + " | ");
    }
    System.out.println();
    for (int i = 0; i < len; i++) {
      int size = terms[i].size();
      System.out.println(classes[i] + "============================");
      for (int j = 0; j < size; j++) {
        System.out.println( ( (Term) terms[i].get(j)).getTerm());
        for (int c = 0; c < len; c++) {
          System.out.print( ( (Term) terms[i].get(j)).getScore(c) + " | ");
        }
        System.out.println();
      }
    }
  }

  public static void main(String[] args) {
    String[] classes = {
        "plant-habit-and-life-style", "leaves", "inflorescences", "flowers"};
    String[] trainingexamples = new String[2];
    trainingexamples[0] = "<plant-habit-and-life-style>Herbs, annual or perennial, stout, to 70 cm; rhizomes present.</plant-habit-and-life-style><leaves>Leaves emersed or submersed; submersed leaves mostly absent; petiole terete to triangular, 2\u201336 cm; blade with translucent markings present as distinct lines, elliptic, lanceolate, or ovate, 2.6\u201315.5 ´  0.5\u201320 cm, base truncate or occasionally cordate to tapering.</leaves><inflorescences>Inflorescences racemes, rarely panicles, of 1\u20139 whorls, each 1\u20133(\u20134)-flowered, erect, 1.5\u201340 ´  1.7\u201350 cm, not proliferating; peduncles 3\u20135-ridged, 2.1\u201357 cm; rachis triangular; bracts distinct, lanceolate, 0.3\u20132.5 cm, coarse, margins scarious; pedicels spreading to ascending, 0.6\u20132.8 cm.</inflorescences><flowers>Flowers 6\u201311 mm wide; sepals spreading to recurved, 9\u201313-veined, veins not papillate; petals clawed; stamens 9\u201315; anthers versatile; pistils 45\u2013200.</flowers>";
    trainingexamples[1] = "<leaves>Leaves basally white with pink or red, otherwise bright green; single midvein (secondary midrib) prominently raised above leaf surface, usually somewhat off-center, other veins barely or not raised; cross section rhomboid.Vegetative leaves to 1.75 m; sheathing base (proximal part of leaf) 22.1\u201366.5(\u201373.3) cm; distal part of leaf 31.9\u201395.8(\u2013117.6) ´  0.5\u20132 cm, 1.4\u20131.8 times longer than proximal part of leaf, margins sometimes undulate or crisped.Sympodial leaf (29.9\u2013)34.7\u2013159.1(\u2013183.9) cm, usually shorter than to nearly equal to vegetative leaves; sheathing base 16.1\u201376.4(\u2013100.1) cm; distal part of leaf 13.5\u201386.2(\u2013101.2) ´  0.4\u20131.9 cm.</leaves><inflorescences>Spadix (3.8\u2013)4.9\u20138.9 cm ´  5.3\u201310.8 mm at anthesis, post-anthesis spadix 5.5\u20138.7 cm ´  6\u201312.6 mm.</inflorescences>";
    //LeftRightStrategy lrs = new LeftRightStrategy(trainingexamples, classes,
    //                                              new String[] {""});
    //lrs.learnModel(false);
    //System.out.println(lrs.markNonspecified("ooo<aa>aaa</aa>000<bb>bbb</bb>000<cc>ccc</cc>ooo"));
  }

}
