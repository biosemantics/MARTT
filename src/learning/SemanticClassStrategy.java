package learning;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.*;
import miner.TermSemantic;
import miner.RelativePosition;
import knowledgebase.ProcessComposite;

public class SemanticClassStrategy
    extends LearningStrategy {
  protected int[] classcount = null;
  protected int size = 0;
  protected int number = 0;
  protected int n = 0;
  protected float conf = 0f;
  protected float sup = 0f;
  protected String alg = null;

  public SemanticClassStrategy(String[] trainingexamples, String[] classes,
                                   String[] delim, String alg) {
    super(trainingexamples, classes, delim, alg);
    this.size = trainingexamples.length;
    this.number = classes.length;
    this.alg = alg;
    optimizeParameters();
  }

  public Model learnModel(boolean debug) {
    TermSemantic sc = new TermSemantic(trainingexamples, classes, n, conf,
                                       sup, delim, false);
    LearnDelimiter del = new LearnDelimiter(trainingexamples, classes);
    Vector rules = del.learn();
    CompoundPattern compoundpatterns = new CompoundPattern(trainingexamples);
    //compoundpatterns.learnPatterns(debug);
    MultiplePattern multiplepatterns = new MultiplePattern(trainingexamples);
    //multiplepatterns.learnPatterns(debug);
    //sc.printTermsForClasses(0.01f, 0.8f);
    classcount = sc.getClasscount();
    LearnTransition tran = new LearnTransition(trainingexamples, classes,
                                               classcount);
    float[][] transmatrix = tran.learnTransMatrix();
    RelativePosition rp = new ProcessComposite().mineRelativePosition(addnull(trainingexamples));
    return new SemanticClassSegmentationModel(sc, rules, rp, transmatrix, classes, delim, compoundpatterns,multiplepatterns);
  }

  protected String[] addnull(String[] examples){
    String[] news = new String[examples.length];
    for(int i = 0; i < examples.length; i++){
      news[i] = "<description>null"+examples[i]+"</description>";
    }
    return news;
  }

  /**
   * use trainingexamples to decide the size of the largest n-gram, confidence and support thresholds for discounting
   * @return 0:n ; 1:confidence; 2:support
   */
  protected void optimizeParameters() {
    n = 2;
    conf = 0.8f;
    sup = 0.035f;
  }

  public static void main(String[] argv) {
    /*SemanticModelStrategy s = new SemanticModelStrategy(new String[] {
        ""}
        , new String[] {""}
        , new String[] {""});
    //System.out.println(Utilities.getOccurrence("b b c d a a e","a"));
    System.out.println(s.wildcards("petioles , rachis").toString());
    String[] p = new String[2];
    p[0] = "\\w+ , rachis";
    p[1] = "petioles , \\w+";
    String[] g = s.generalizeCP(p);
    for (int i = 0; i < g.length; i++) {
      System.out.println(g[i]);
    }

    System.out.println(s.match("cat  , dog", g));
*/
  }

}

