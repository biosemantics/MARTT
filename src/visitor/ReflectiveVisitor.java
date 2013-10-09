package visitor;

/**
 * <p>Title: ReflectiveVisitor</p>
 * <p>Description: by using reflection, classes that implements RV interface is
 *                 able to dispatch visit method calls to appropriate class. This
 *                 reflection makes up the weakness of visitor pattern, namely,
 *                 difficulites in adding new class in class strucutre.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public interface ReflectiveVisitor {
  public void dispatch(Object o, String alg);
}