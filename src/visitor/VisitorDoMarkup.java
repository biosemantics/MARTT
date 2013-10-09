package visitor;

import learning.Model;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
//import miner.TermSemantic;
import learning.MarkedSegment;
import knowledgebase.Composite;
/**
 * <p>Title: VisitorDoMarkup</p>
 * <p>Description: Handles markup, including k-fold validation, markup based
 *                 purely on training, and markup with knowledge support</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public class VisitorDoMarkup
    extends VisitorInsert {


  public VisitorDoMarkup(Vector xmlsegs, String filename, Composite knowledge, String order, String alg, String kbsc, String lrp, String kblrp) {
    super(xmlsegs, filename, knowledge, order, alg, kbsc, lrp, kblrp); //a to-be-marked examples, a vector may
    //contain more than one string that share the same tag but not adjecent to each other
  }


  public Vector[] getMarkupInfo(ElementComposite ec){
    Model model = (Model)ec.getModel();
    Vector[] result = new Vector[xmlsegs.size()];
    if (model == null) { //should never happen
      System.err.println(
          "Markup Process can not proceed because the model is null");
      System.exit(1);
    }
    else {
      //reflection: to find out learning strategy used when learning the model
      String classname = model.getClass().getName();
      String packagename = classname.substring(0, classname.lastIndexOf('.'));
      classname = classname.substring(classname.lastIndexOf('.') + 1); //classname = "LeftRightSegmentationModel" for example.
      classname = classname.substring(0, classname.length() - 5); //classname = "LeftRightSegmentation"
      try {
        Class c = Class.forName(packagename + "." + classname);
        Class modelclass = model.getClass(); //LRSModel
        Class node = ec.getClass();
        Constructor con = c.getConstructor(new Class[] {modelclass, node, "".getClass()}); //get constructor of LeftRightSegmentation
        Object segmenter = con.newInstance(new Object[] {model, ec, alg}); //create an instance of LRS
        Method method = c.getMethod("markup", new Class[] {"".getClass(),"".getClass(), (new Composite()).getClass(), "".getClass(), boolean.class,"".getClass(),"".getClass(),"".getClass()}); //find the method "markup"
        Vector marked = new Vector(xmlsegs.size());
        for (int i = 0; i < xmlsegs.size(); i++) {
          String tobemark = ( (MarkedSegment) xmlsegs.get(i)).getSegment().trim();
          result[i] = (Vector) method.invoke(segmenter,
                                               new Object[] {tobemark, filename,
                                               knowledge, order,
                                               Boolean.FALSE, kbsc, lrp, kblrp}); //invoke the method "markup" on segmenter
        }
      }
      catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      catch (NoSuchMethodException e1) {
        e1.printStackTrace();
      }
      catch (InstantiationException e2) {
        e2.printStackTrace();
      }
      catch (IllegalAccessException e3) {
        e3.printStackTrace();
      }
      catch (IllegalArgumentException e4) {
        e4.printStackTrace();
      }
      catch (InvocationTargetException e5) {
        e5.printStackTrace();
        e5.getTargetException().printStackTrace();
      }catch(ClassCastException e6){
        e6.printStackTrace();
      }

    }
    return result;
  }

  public void visitElementLeaf(ElementLeaf el, String alg) {
    el.addMarkedSegs(xmlsegs, Integer.parseInt(order));
    //convert to string and add to markeds
    Vector markeds = new Vector();
    Enumeration en = xmlsegs.elements();
    while(en.hasMoreElements()){
      MarkedSegment seg = (MarkedSegment)en.nextElement();
      markeds.add(seg.getSegment());
    }
    el.addMarkeds(markeds, Integer.parseInt(order));
  }

  protected void addPlaceHolderForUnmatched(Vector unmatched){
    Iterator unmatcheds = unmatched.iterator();
    while (unmatcheds.hasNext()) { //unmatched
      ElementComponent ect = (ElementComponent) unmatcheds.next();
      Vector placeholder = new Vector();
      MarkedSegment holder = new MarkedSegment();
      placeholder.add(holder);
      addMarkedSegs(ect, placeholder, order);
      Vector placeholders = new Vector();
      placeholders.add("");
      addMarkeds(ect, placeholders, order);
      //need to add placeholder for all ect's offspring nodes
      addPlaceHolderForChildren(ect);
    }
  }

  protected void addPlaceHolderForChildren(ElementComponent ec){
  Vector placeholder = new Vector();
  Vector placeholders = new Vector();
  MarkedSegment holder = new MarkedSegment();
  placeholder.add(holder);
  placeholders.add("");
  Iterator children = ec.iterator();
  if(children == null){return;}
  while(children.hasNext()){
    ElementComponent c = (ElementComponent)children.next();
    addMarkedSegs(c, placeholder, order);
    addMarkeds(c, placeholders, order);
    addPlaceHolderForChildren(c);
  }
}


  public void addMarkeds(ElementComponent ec, Vector markeds, String order){
    ec.addMarkeds(markeds, Integer.parseInt(order));
  }

  public void addMarkedSegs(ElementComponent ec, Vector marksegs, String order) {
    ec.addMarkedSegs(marksegs, Integer.parseInt(order));
  }
  /**
   * when children were marked by premarkup, eg. chromosomes
   * @param children Vector
   * @param ec ElementComposite
   * @param table Hashtable
   */
  public void insertNewChildren(Vector children, ElementComposite ec,
                                  Hashtable table) {
      Iterator newones = children.iterator();
      while (newones.hasNext()) {
        String tag = (String) newones.next();
        ElementLeaf el = new ElementLeaf();
        el.setTag(tag);
        el.setParent(ec);
        ec.addChild(el);
        Vector segs = new Vector( (ArrayList) table.get(tag));
        //put placeholders-1 el.markeds

        int placeholders = ec.getMarkeds().size();

        for (int i = 0; i < placeholders - 1; i++) {
          Vector temp1 = new Vector();
          temp1.add("");
          el.addMarkeds(temp1, i);
          Vector temp2 = new Vector();
          temp2.add(new MarkedSegment());
          el.addMarkedSegs(temp2, i);
        }
        //dispatching to el
        el.accept(new VisitorDoMarkup(segs, "", knowledge, order, alg, kbsc, lrp, kblrp),alg);
      }

    }
  /*public void insertNewChildren(Vector children, ElementComposite ec, Hashtable table){
    Iterator it = children.iterator();
    while(it.hasNext()){
     String temp = (String)it.next();
      if (temp != null) {
        System.err.println(
            "New Child: "+temp+" A call to insertNewChildren is not possible for VisitorDoMarkup");
      }
    }
  }*/
}
