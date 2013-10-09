package visitor;

import learning.Model;
import learning.LeftRightLeadWordsStrategy;
import learning.LeftRightContentStrategy;
/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: Thesis Project, everything in this project</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class VisitorLearnLessStructured extends VisitorLearn {
  public VisitorLearnLessStructured() {
  }
  public Model learnModel(String[] trainingexamples, String[] classes, String alg) {
    Model model = null;
    if(alg.compareTo("LW") == 0 || alg.compareTo("LWI") == 0){
      model = (new LeftRightLeadWordsStrategy(trainingexamples, classes, new String[]{""}, alg)).learnModel(false);
    }else if(alg.compareTo("CT") == 0 || alg.compareTo("NB") == 0){
      model = (new LeftRightContentStrategy(trainingexamples, classes,
                                                  new String[] {""}, alg)).
          learnModel(false);
    }else{
      System.err.println("Algorithm "+alg+" is not implemented");
    }
    return model;
  }

  public static void main(String[] argv){
  }

}