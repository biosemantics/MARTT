package learning;

import jds.collection.BinarySearchTree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.io.File;

/**
 * <p>Title: BayesScore</p>
 * <p>Description: caculate term scores using Bayes Therom</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

// this class calculate scores for each term by class using Bayes theroem
// p(c | t) = p(t, c) / p(t) = p(c)p(t|c) / p(t)
// the score says the probability of a string with term t belongs to class c.

public class BayesScore
    extends TermScore {
  private int[] classcount = null;
  private int termcount = 0; //count of the unique terms in an example

  /**
   * @param termtree term by class binary search tree with <code>Term</code> as nodes.
   * @param classes a array of class labels.
   * @param classcount the number of examples in each class.
   */
  public BayesScore(BinarySearchTree termtree, String[] classes,
                    int[] classcount, int termcount) {
    super(termtree, classes);
    this.classcount = classcount;
    this.termcount = termcount;
  }

  /**
   * p(c | t) = p(t, c) / p(t) = p(c) p(t|c) / p(t)
   *
   * @return an array of ArrayList of <code>Term</code> for each class
   *
   */
  public ArrayList[] scoredTerms(String alg) {
    int totalexps = 0; // total examples
    ArrayList[] scoredterms = new ArrayList[classes.length]; //a list for a class

    //find total examples and initialize scored[]
    for (int c = 0; c < classes.length; c++) {
      totalexps += classcount[c];
      scoredterms[c] = new ArrayList();
    }

    Enumeration terms = termtree.elements();
    while (terms.hasMoreElements()) {
      //for each term find it score
      Term term = (Term) terms.nextElement();
      //float p_t = (float) term.getTotalOccurance() / totalexps;
      for (int c = 0; c < classes.length; c++) {
        /*float p_tc = (float) term.getOccuranceIn(classes[c]) /
            (classcount[c] == 0 ? 1 : classcount[c]);
                 float p_c = (float) classcount[c] / totalexps;
                 float score = p_c * p_tc / p_t;
         */
        //above three lines is really just score = term.getOccuranceIn(classes[c])/term.getTotalOccurance();
        float score = 0f;
        if(alg.compareTo("NB") != 0){
        score = (float) term.getOccuranceIn(classes[c]) /
            (float) term.getTotalOccurance();
        }else{
          /*follow NB formula, find p(t|c)*/
         score = (float) (term.getOccuranceIn(classes[c]) == 0 ? 0.0001 :
                                 term.getOccuranceIn(classes[c])) /
              classcount[c];
        }
        term.setScore(score, classes[c]);
        scoredterms[c].add(term);
      }
    }
    return scoredterms;
  }

  /* public static ArrayList[] main(String[] args) {
     String termtree = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\Exp1\\MarkedExamplesObjects\\";
     String[] classes = {
       "description", "taxon", "plant-habit-and-life-style", "roots", "stems",
         "buds", "leaves", "inflorescences", "flowers", "pollen", "fruits",
         "cones", "seeds",
         "spore-related-structures", "gametophytes", "chromosomes", "timeline",
         "compound", "other-features", "other-information"
     };
     File dir = new File(termtree);
     File[] files = dir.listFiles();
     String[] filenames = new String[files.length];
     for (int f = 0; f < files.length; f++) {
       filenames[f] = files[f].getPath();
     }
     TermClassTree termTree1 = new TermClassTree(filenames);
     BinarySearchTree bst = termTree1.classTermMatrix(classes, 500);
     int[] classCount = termTree1.getClassCount();
     int termCount = termTree1.getTermCount();
     //System.out.println(Utilities.printBTree(bst));
     BayesScore score1 = new BayesScore(bst, classes, classCount, termCount);
     ArrayList[] scored = score1.scoredTerms();
     //Serializer.serialization("/home/hongcui/ThesisProject/Exp/Exp1/BayesScore", scored);
     //Serializer.serialization("C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\Exp1\\BayesScore", scored);
     //for(int c = 0; c<classes.length; c++){
     //  System.out.println(classes[c]+"\n\n");
     //  System.out.println(Utilities.printBTree(scored[c]));
     //}
     return scored;
   }*/
}