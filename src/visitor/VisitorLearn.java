package visitor;

import java.util.ArrayList;
import java.util.Iterator;
import learning.Model;

/**
 * <p>Title: VisitorLearn</p>
 * <p>Description: abstract class. super class for all learning visitors</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public abstract class VisitorLearn
    extends VisitorAbstract
    implements ReflectiveVisitor {
  public VisitorLearn() {
    super();
  }

  public void visitElementComposite(ElementComposite ec, String alg) {
    String[] trainingexamples = this.getAllTrainingExamples(ec);
    String[] classes = this.getAllSubElements(ec);
    Model model = learnModel(trainingexamples, classes, alg);
    ec.setModel(model);
    Iterator children = ec.iterator();
    while (children.hasNext()) {
      this.dispatch(children.next(), alg);
    }
  }

  public abstract Model learnModel(String[] trainingexamples, String[] classes, String alg);

  public void visitElementLeaf(ElementLeaf el, String alg) {
    //do nothing
  }

  public void visitObject(Object o, String alg) {
    //do nothing
  }

  /**
   * obtain all training data of the component
   *
   * @param ec
   * @return an array of training data as strings
   */
  private String[] getAllTrainingExamples(ElementComponent ec) {
    String[] trainingexamples = new String[ec.getTrainingExamples().size()];
    trainingexamples = (String[]) ec.getTrainingExamples().toArray(
        trainingexamples);
    return trainingexamples;
  }

  /**
   * obtain all possible sub-elements, except for "taxon"
   * @param ec
   * @return an array of class labels as strings
   */
  private String[] getAllSubElements(ElementComponent ec) {
    ArrayList classlist = new ArrayList();
    Iterator children = ec.iterator();
    while (children.hasNext()) {
      ElementComponent comp = (ElementComponent) children.next();
      if (comp.getTrainingExamples().size() != 0) { //comp may exist without any training example when
        String tag = (comp.getTag()); //it is created in a different run of k-fold validation
        classlist.add(tag);
      }
    }
    //if(classlist.size() == 0){
    //  classlist.add(Model.nonspecified);
    //}
    return (String[]) classlist.toArray(new String[classlist.size()]);
  }

  public void dispatch(Object o, String alg) {
    super.dispatch(o, alg);
  }

}