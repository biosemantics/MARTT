package visitor;

import learning.Model;
import learning.Utilities;
import learning.MarkedSegment;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
//import miner.TermSemantic;
import knowledgebase.Composite;
/**
 * <p>Title: VisitorInsert</p>
     * <p>Description: super class to VisitorInsertMarkup and VisitorInsertAnswer</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public abstract class VisitorInsert
    extends VisitorAbstract {

  protected Vector xmlsegs = null; //a to-be-marked examples, a vector may
  //contain more than one MarkedSegment that share the same tag but not adjecent to each other
  protected Composite knowledge = null;
  protected String order = ""; //indicates the order of an example to be marked up. it is needed to pass a text segment from one node to its sibling nodes. use String instead of int because reflection needs class and object, not literal.
  protected String filename = null;
  protected String alg = null;
  protected String kbsc = null;
  protected String lrp = null;
  protected String kbrp = null;
  protected String kblrp = null;


  public VisitorInsert(Vector xmlsegs, String filename, Composite knowledge, String order, String alg, String kbsc, String lrp, String kblrp) {
    this.xmlsegs = xmlsegs;
    this.knowledge = knowledge;
    this.order = order;
    this.filename = filename;
    this.alg = alg;
    this.kbsc = kbsc;
    this.lrp = lrp;
    this.kbrp = kbrp;
    this.kblrp = kblrp;
  }

  /**
   * when this method is called, learning process has completed and ec has a non-null model member.
   * learnt model determined what markup strategy to use
   * @param ec
   * @todo hashtables[i] may be empty
   * @todo handle "none" tag
   */
  public void visitElementComposite(ElementComposite ec, String alg) {
    Vector[] markupinfo = new Vector[xmlsegs.size()]; //one element for each seg
    //Hashtable[] hashtables = new Hashtable[xmlsegs.size()]; //tag => content
    ArrayList hashtables = new ArrayList();
    Vector marked = new Vector();
    addMarkedSegs(ec, xmlsegs, order);//the segs marked as ec.label
    /**@todo should be markupinfo = ec.getMarkupInfo(xmlsegs)??*/
    markupinfo = getMarkupInfo(ec); //each info has a hashtable for tag=>content(arraylist), and a markedup stringbuffer


    for (int i = 0; i < xmlsegs.size(); i++) {
      //if(markupinfo[i] != null){
        hashtables.add((Hashtable) markupinfo[i].get(0)); //a tag => more than one segments
        marked.add( ( (StringBuffer) markupinfo[i].get(1)).toString());
        Vector[] residualinfo = (Vector[])markupinfo.clone();
        while(residualinfo[i].size() > 2){
          //markup residual
          xmlsegs.removeAllElements();
          xmlsegs.add(residualinfo[i].get(2));
          residualinfo = getMarkupInfo(ec);//only one element
          hashtables.add((Hashtable)residualinfo[0].get(0));
          marked.add(( (StringBuffer)residualinfo[0].get(1)).toString());
        }
      /*}else{
        //if mark up fails
        hashtables[i] = new Hashtable();
        marked.add(Utilities.strip((String)xmlsegs.get(i)));
      }*/
    }
    /**@todo should be ec.addMarkedSegs(marked)??*/
    addMarkeds(ec, marked, order);//add marked string produced by ec

    //merge hashtables into one hashtable, so segments sharing a tag are together, tag string => arraylist
    Hashtable table = mergeHashtables((Hashtable[])hashtables.toArray(new Hashtable[1]));

    //dispatch segments of hashtables to corresponding child of ec, and
    //put place-holder for markeds in all un-matched elements
    Vector[] divide = groupChildren(ec, table); //find matched, unmatched, and new children
    //add new children to the tree
    insertNewChildren(divide[2], ec, table);//and dispatch to new children
    //elements that are not found in this example
    addPlaceHolderForUnmatched(divide[1]);
    //dispatch to nodes that are found in this example
    Iterator matched = divide[0].iterator();
    while (matched.hasNext()) { //matched
      ElementComponent ect = (ElementComponent) matched.next();
      /**@todo why can't we make ArrayList Vector directly?**/
      Vector segswithtag = new Vector( (ArrayList) table.get(ect.getTag()));
      //ect.accept(new VisitorInsert(segswithtag));
      //reflection, call either VisitorInsertAnswer or VisitorInsertMarkup
      try {
        Constructor con = this.getClass().getConstructor(new Class[] {
            segswithtag.getClass(),"".getClass(), (new Composite()).getClass(), "".getClass(),"".getClass(),"".getClass(),"".getClass(),"".getClass()});
        ect.accept( (VisitorAbstract) con.newInstance(new Object[] {segswithtag, filename, knowledge, order, alg, kbsc, lrp, kblrp}), alg);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  protected abstract void addPlaceHolderForUnmatched(Vector unmatched);
  /**
   * when a description has only flowers, then leaves etc will have placeholders
   * and all sub elements in leaves will have placeholders as well
   * @param ec
   */
  protected abstract void addPlaceHolderForChildren(ElementComponent ec);

  /**
   * add to ec the markedsegments that belong to ec
   * markedsegments are produced by ec's parent
   * @param ec
   * @param marksegs
   */
  public abstract void addMarkedSegs(ElementComponent ec, Vector marksegs, String order);
  /**
   * add marked strings to ec
   * marked strings are produced by ec
   * @param ec
   * @param marked
   */
  public abstract void addMarkeds(ElementComponent ec, Vector markeds, String order);

  public abstract void insertNewChildren(Vector children, ElementComposite ec, Hashtable table);


  public abstract Vector[] getMarkupInfo(ElementComposite ec);
  /**
   * group children into three groups, matched, un-matched, and new children.
   * @param components child components
   * @param keys tags
   * @return
   */
  private Vector[] groupChildren(ElementComponent ec, Hashtable tagtable) {
    Vector[] groups = new Vector[3];
    groups[0] = new Vector(); //matched
    groups[1] = new Vector(); //un-matched
    groups[2] = new Vector(); //new children
    Enumeration tags = tagtable.keys();
    //find new children
    while(tags.hasMoreElements()){
      String tag = (String)tags.nextElement();
      if(ec.findChild(tag) == null){
        groups[2].add(tag);
      }
    }
    //the other two groups
    Iterator components = ec.iterator();
    if(components != null){ //==null when ec is a leaf
      while (components.hasNext()) {
        ElementComponent ec1 = (ElementComponent) components.next();
        boolean match = false;
        Enumeration keys = tagtable.keys();
        while (keys.hasMoreElements()) {
          String tag = (String) keys.nextElement();
          if (ec1.getTag().compareTo(tag) == 0) {
            groups[0].add(ec1);
            match = true;
            break;
          }
        }
        if (!match) {
          groups[1].add(ec1);
        }
      }
    }
    return groups;
  }

  /**
   * @todo test this function
   * @param hashes
   * @return
   */
  private Hashtable mergeHashtables(Hashtable[] hashes) {
    Hashtable table = hashes[0];
    for (int i = 1; i < hashes.length; i++) {
      Enumeration e = hashes[i].keys();
      while (e.hasMoreElements()) {
        String key = (String) e.nextElement();
        if (table.containsKey(key)) { //perserve order!!!
          ( (ArrayList) table.get(key)).addAll( (ArrayList) hashes[i].get(key));
        }
        else {
          table.put(key, (ArrayList) hashes[i].get(key));
        }
      }
    }
    return table;
  }

  /**
   * @todo convert leaf to composite
   * @param el
   */
  public abstract void visitElementLeaf(ElementLeaf el, String alg);

  public void visitObject(Object o, String alg) {

  }
}
