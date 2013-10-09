package visitor;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * <p>Title: VisitorScore</p>
 * <p>Description: Score the performance of the markup system</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public class VisitorScore
    extends VisitorAbstract {
  private Score scoreobject = null;

  public VisitorScore(Score scoremethod) {
    this.scoreobject = scoremethod;
  }


  /**
   * compare ec.markeds against ec.answers to score the performance of markup
   * save the score in ec.score
   *
   * each node scores for the elements of its child nodes
   * @param ec
   * @todo score "non-specified" elements: markeds and answers are not in the same node.
   */
  public void visitElementComposite(ElementComposite ec, String alg) {
    Iterator components = ec.iterator();
    ec.setScore(scoreobject.score(ec));

    while (components.hasNext()) {
      ElementComponent ec1 = (ElementComponent) components.next();
      //can be made into a template method to accomondate other scoring schema
      ec1.accept(new VisitorScore(scoreobject), alg); //dispatch to child nodes
    }
  }

  public void visitElementLeaf(ElementLeaf el, String alg) {
    Score pr = scoreobject.score(el);
    el.setScore(pr);
  }

  public void visitObject(Object o, String alg) {
    /**do nothing*/
  }



  public static void  main(String args[]){

  }
}