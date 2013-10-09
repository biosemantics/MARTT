package learning;

import miner.TermSemantic;
import miner.RelativePosition;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: Thesis Project, everything in this project</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class SemanticClassSegmentationModel
    extends Model {
  protected TermSemantic ts = null;
  private Vector delimiterrules = null;
  protected float[][] transmatrix = null;
  protected String[] classes = null;
  protected String[] delim = null;
  protected RelativePosition rp = null;
  protected CompoundPattern compoundpatterns = null;
  protected MultiplePattern multiplepatterns = null;
  //public static final String modelfile = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level2\\termsemantic.model";

  public SemanticClassSegmentationModel(){
    super();
  }
  public SemanticClassSegmentationModel(TermSemantic ts, Vector delimrules,
                            RelativePosition rp, float[][] transmatrix, String[] classes, String[] delim, CompoundPattern compoundpatterns, MultiplePattern multiplepatterns) {
    this.ts = ts;
    this.delimiterrules = delimrules;
    this.transmatrix = transmatrix;
    this.classes = classes;
    this.delim = delim;
    this.compoundpatterns = compoundpatterns;
    this.multiplepatterns = multiplepatterns;
    this.rp = rp;
  }

  public TermSemantic getTermSemantic() {
    return ts;
  }

  public String[] getClasses(){
     return classes;
     }

  public Vector getDelimiterrules() {
    return delimiterrules;
  }

  public float[][] getTransmatrix() {
    return transmatrix;
  }

  public RelativePosition getRP(){
    return rp;
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

  public String[] getDelim() {
    return delim;
  }

  public CompoundPattern getCompoundPatterns(){
    return compoundpatterns;
  }

  public MultiplePattern getMultiplePatterns() {
    return multiplepatterns;
  }


}
