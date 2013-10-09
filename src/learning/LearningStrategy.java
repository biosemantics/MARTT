package learning;

/**
 * <p>Title: LearningStrategy</p>
 * <p>Description: the abstract class for all learning strategies</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public abstract class LearningStrategy {
  protected String[] trainingexamples = null;
  protected String[] classes = null;
  protected String[] delim = null;
  protected String alg = null;
  public LearningStrategy(String[] trainingexamples, String[] classes, String[] delim, String alg) {
    this.trainingexamples = trainingexamples;
    this.classes = classes;
    this.delim = delim;
    this.alg = alg;
  }

  public abstract Model learnModel(boolean debug);

}