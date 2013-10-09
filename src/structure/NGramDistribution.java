package structure;

import learning.Term;
import learning.InfoGainScore;
import learning.TermClassTree;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import jds.collection.BinarySearchTree;

/**
 * bugs: when run 2-gram for FOC, overFlowStack error is threw.
 */
/**
 * <p>Title: StructureMeasure</p>
 * <p>Description: A measure of structuredness of flora corpus</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

/**
 * scoring the structuredness of a collection in terms of n-gram distribution for a level
 *
 * 2. find all the n-grams with infogain score above a threshold => term list
 * 1. create a BTerm binary search tree
 * 3. for each n-gram in n-gram list, find (the number of classes it occurs in - 1)
 * 4. sum freq of all n-grams
 * 5. return 1 - (sum / number of n-grams) / number of classes
 */

public class NGramDistribution
    extends MeasureAlgorithm {
  private float threshold = 0f;
  private int n = 1; //default 1-gram
  private int m = 0; //consider leading m words only
  private Hashtable rarelist = null;
  public NGramDistribution(String[] instances, String[] classes, float threshold,
                           int n, int m, Hashtable rarelist) {
    super(instances, classes);
    this.threshold = threshold;
    this.n = n;
    this.m = m;
    this.rarelist = rarelist;
  }

  public float score() {
    SimpleNGramStats ngram = new SimpleNGramStats(n, instances, classes, m, rarelist);
    BinarySearchTree termtree = ngram.collect();
    //int sum = 0;
    int termcount = 0;
    int nonzeroterms = 0;
    float totalpercentile = 0f;
    int[] classCount = ngram.getClassCount();
    InfoGainScore score1 = new InfoGainScore(termtree, classes, classCount);
    BinarySearchTree scored = score1.scoredTerms();
    Enumeration e = scored.elements();
    while (e.hasMoreElements()) {
      Term st = (Term) e.nextElement();
      if (Float.compare( ( (Float) st.getScore(st.getTerm())).floatValue(),
                        threshold) >= 0) {
        termcount++;
        Term bterm = (Term) termtree.findElement(new Term(st.getTerm()));
        //measure the peak occurance of the term against total occurance of the term
        int sum = 0;
        int max = 0;
        for (int i = 0; i < classes.length; i++) {
          int ccount = bterm.getOccuranceIn(classes[i]);
          sum += ccount >= 0 ? ccount : 0;
          max = max < ccount ? ccount : max;
        }
        if (sum > 0 && max > 0) {
          nonzeroterms++;
          //float coeff = (sum / 10) >= 1 ? 1f : (float)sum / 10; //deduct effect of rare term (max = sum = 1)
          float coeff = (sum-1)/(float)sum;//deduct effect of rare term (max = sum = 1)
          totalpercentile += coeff * (float) max / sum;
        }

        //int classfreq = Utilities.nonZero(bterm.getClassCount());
        //sum += classfreq - 1;
        //System.out.print("[" + classfreq + "] " + bterm.toString());
      }
    }
    if (termcount == 0) {
      System.err.println("\nThreshold set too high");
      //System.exit(1);
    }
    int n = classes.length;
    System.out.println("total term: " + termcount + " nonzero terms: " +
                       nonzeroterms + " total percentile: " + totalpercentile +
                       " , class number: " + n);
    float t1 = (float) nonzeroterms / termcount;
    float t2 = totalpercentile / nonzeroterms;
    System.out.println("percent of nonzero terms:" + t1);
    System.out.println("average percentile of the most singling term:" + t2);
    return t1 * t2;
  }

  /*public float score() {
    SimpleNGramStats ngram = new SimpleNGramStats(n, files, classes);
    BinarySearchTree termtree = ngram.collect();
    int sum = 0;
    int termcount = 0;
    int[] classCount = ngram.getClassCount();
    InfoGainScore score1 = new InfoGainScore(termtree, classes, classCount);
    BinarySearchTree scored = score1.scoredTerms();
    Enumeration e = scored.elements();
    while (e.hasMoreElements()) {
      Term st = (Term) e.nextElement();
      if (Float.compare(((Float)st.getScore(st.getTerm())).floatValue(), threshold) >= 0) {
        termcount++;
        Term bterm = (Term) termtree.findElement(new Term(st.getTerm()));
        int classfreq = Utilities.nonZero(bterm.getClassCount());
        sum += classfreq - 1;
       System.out.println("[in " + classfreq + " classes]" + bterm.toString());
      }
    }
    if (termcount == 0) {
      System.err.println("Threshold set too high");
      //System.exit(1);
    }
    int n = classes.length;
    System.out.println("total term: " + termcount + " , class number: " + n);
    return 1 - ( (float) sum / (termcount * n));
     }*/

  public static void main(String[] args) {
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

    String[] instances = new String[files.length];
    for (int f = 0; f < files.length; f++) {
      instances[f] = learning.Utilities.readFile(files[f]);
    }

    //NGramDistribution ngd = new NGramDistribution(instances, classes, 0.00f, 2, 3);
    //System.out.println("term distribution score: " + ngd.score());

  }
}
