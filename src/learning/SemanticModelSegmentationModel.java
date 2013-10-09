package learning;

import java.util.ArrayList;
import java.util.Hashtable;
import miner.TermSemantic;
import miner.RelativePosition;

/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: Thesis Project, everything in this project</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class SemanticModelSegmentationModel extends SemanticClassSegmentationModel {
  private Hashtable modelclasses = null;
  private RelativePosition rp = null;
  //public static final String modelfile = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level2\\termsemanticleaf.model";

  public SemanticModelSegmentationModel(){
    super();
  }
  public SemanticModelSegmentationModel(TermSemantic ts, Hashtable modelclasses, CompoundPattern compoundpatterns, MultiplePattern multiplepatterns, RelativePosition rp, float[][] transmatrix, String[] classes, String[] delim) {
    this.ts = ts;
    this.transmatrix = transmatrix;
    this.rp = rp;
    this.modelclasses = modelclasses;
    this.compoundpatterns = compoundpatterns;
    this.multiplepatterns = multiplepatterns;
    this.classes = classes;
    this.delim = delim;
  }

  public Hashtable getModelClasses(){
    return modelclasses;
  }

  public RelativePosition getRP(){
    return rp;
  }

}
