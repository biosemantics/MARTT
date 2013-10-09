package learning;

import java.util.ArrayList;
import java.util.Collections;
import jds.collection.BinarySearchTree;

/**
 * <p>Title: TermScore</p>
     * <p>Description: abstract class, the super class of all scoring algorithms</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public abstract class TermScore {
  protected String[] classes = null;
  protected BinarySearchTree termtree = null;

  public TermScore(BinarySearchTree termtree, String[] classes) {
    this.classes = classes;
    this.termtree = termtree;
  }

  public abstract ArrayList[] scoredTerms(String alg);

  public void sortByScore(ArrayList[] scoredterms) {
    for (int c = 0; c < classes.length; c++) {
      Collections.sort(scoredterms[c], new TermScoreComparator(c));
    }
  }

}