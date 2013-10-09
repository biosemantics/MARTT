package learning;

import java.util.ArrayList;
import java.util.regex.*;
import java.util.Vector;

/**
 * <p>Title: LeftRightSegmentationModel</p>
 * <p>Description: The class saving learned models for LeftRightSegmentation learning algorithm</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public class LeftRightSegmentationModel extends Model{
  private Vector delimiterrules = null;
  private ArrayList[] scoredterms = null;
  private String[] classes = null;
  private float[][] transmatrix = null;
  private int[] classcount = null;
  //public static final String modelfile = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level1\\leftright.model";
  protected String alg = null;

public LeftRightSegmentationModel(){
    super();
  }
  public LeftRightSegmentationModel(String[] classes, ArrayList[] scoredterms, Vector delimiterrules, float[][] transmatrix, int[] classcount, String alg) {
    this.classes = classes;
    this.scoredterms = scoredterms;
    this.delimiterrules = delimiterrules;
    this.transmatrix = transmatrix;
    this.classcount = classcount;
    this.alg = alg;
  }

  public String[] getClasses(){
    return classes;
  }

  public ArrayList[] getScoredterms(){
    return scoredterms;
  }

  public Vector getDelimiterrules(){
    return delimiterrules;
  }

  public float[][] getTransmatrix() {
    return transmatrix;
  }

  public int[] getClasscount() {
    return classcount;
  }

  public int getTagIndexInTransMatrix(String tag) {
    if (tag.compareTo(LearnTransition.starttag) == 0 || tag.compareTo("") == 0) {
      return 0;
    }
    if (tag.compareTo(LearnTransition.endtag) == 0) {
      return classes.length + 1;
    }
    for (int i = 0; i < classes.length; i++) {
      if (classes[i].compareTo(tag) == 0) {
        return i + 1;
      }
    }
    return 0; //will never reach here
  }



}
