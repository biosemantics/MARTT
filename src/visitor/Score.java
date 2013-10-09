package visitor;

/**
 * <p>Title: Hierarchy</p>
 * <p>Description: A learning hierarchy for marking up text with xml</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public abstract class Score {
  public Score() {
  }

  public abstract String toString();
  public abstract void reset();
  public abstract Score addition(Score s);
  public abstract void divideBy(int k);
  public abstract Score score(ElementComponent ec);
  public abstract boolean isZero();
  public abstract boolean isGood();

}