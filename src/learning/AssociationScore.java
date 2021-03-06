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

/*this class calculate association score for each term
 p(t | c)/p(t)
 the score says the probability of a string with term t belongs to class c.
*/
public class AssociationScore
    extends TermScore {
  private int[] termcounts = null; //term count for each class
  private int termcount = 0; //count of all terms in the collection


  /**
   * @param termtree term by class binary search tree with <code>Term</code> as nodes.
   * @param classes a array of class labels.
   * @param termcounts4classes: total terms in each class
   * @param termcount: total terms
   */
  public AssociationScore(BinarySearchTree termtree, String[] classes,
                    int[] termcounts4classes, int termcount) {
    super(termtree, classes);
    this.termcounts = termcounts4classes;
    this.termcount = termcount;
  }

  /**
   * score = p(t | c)/p(t)
   *
   * @return an array of ArrayList of <code>Term</code> for each class
   *
   */
  public ArrayList[] scoredTerms(String alg) {
    ArrayList[] scoredterms = new ArrayList[classes.length]; //a list for a class

    //initialize scored[]
    for (int c = 0; c < classes.length; c++) {
      scoredterms[c] = new ArrayList();
    }

    Enumeration terms = termtree.elements();
    while (terms.hasMoreElements()) {
      //for each term find it score
      Term term = (Term) terms.nextElement();
      int t =  term.getTotalOccurance();
      if(t >1){
        float p_t = (float) t / termcount;
        for (int c = 0; c < classes.length; c++) {

          /*find p(t|c)*/
          float tc = (float) (term.getOccuranceIn(classes[c]) == 0 ? 0.01 :
                              term.getOccuranceIn(classes[c]));
          float p_tc = tc / termcounts[c];
          float score = p_tc / p_t;
          if (Float.compare(score, Float.POSITIVE_INFINITY) == 0) {
            System.out.print("");
          }
          term.setScore(score, classes[c]);
          scoredterms[c].add(term);
        }
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
