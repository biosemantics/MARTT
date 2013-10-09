package visitor;

/**
 * <p>Title: Hierarchy</p>
 * <p>Description: A learning hierarchy for marking up text with xml</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public interface Visitable {
  public void accept(ReflectiveVisitor v, String alg) ;
}