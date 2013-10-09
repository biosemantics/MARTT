package learning;

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

public class LeftRightContentStrategy extends LeftRightStrategy {
  public LeftRightContentStrategy(String[] trainingexamples, String[] classes, String[] delim, String alg) {
    super(trainingexamples, classes, delim, alg);
  }
  public Model returnModel(String[] classes, ArrayList[] terms, Vector rules, float[][] matrix, int[] classcount){
    return new LeftRightContentSegmentationModel(classes, terms, rules, matrix,
                                          classcount, alg);
  }

}