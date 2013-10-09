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

public class LeftRightLeadWordsSegmentationModel extends LeftRightSegmentationModel {
  public LeftRightLeadWordsSegmentationModel(){

  }
  public LeftRightLeadWordsSegmentationModel(String[] classes, ArrayList[] terms, Vector rules, float[][] matrix, int[] classcount, String alg) {
    super(classes, terms, rules, matrix, classcount, alg);
  }

}