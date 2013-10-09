package knowledgebase;

import miner.RelativePosition;
import miner.TermSemantic;
import learning.Term;
import miner.SCScore;
import jds.collection.BinarySearchTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: Thesis Project, everything in this project</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class Merger {
  private static boolean avgscore = false;

  public Merger() {
  }

  public static void addToKB(Component kb, Component akb, float sup, float conf) {
    TermSemantic ts = kb.getTermSemantic();
    mergeTS(ts, akb.getTermSemantic(), sup, conf); //update the ts with a new ts
    akb.setTermSemantic(null);
    RelativePosition rp = kb.getRelativePosition();
    mergeRP(rp, akb.getRelativePosition()); //update the rp with a new rp
    akb.setRelativePosition(null);
    //take care of children
    ArrayList achildren = akb.getChildren();
    ArrayList pool = new ArrayList();
    pool.addAll(achildren);
    pool.addAll(kb.getChildren());
    Iterator it = pool.iterator();
    StringBuffer sb = new StringBuffer();
    while (it.hasNext()) {
      String tag = ((Component) it.next()).getTag();
      //String tag = pcom.getTag();
      if(sb.toString().indexOf(" "+tag+" ") < 0){
        Component acom = akb.getChild(tag);
        Component com = kb.getChild(tag);
        if (acom != null && com != null) {
          addToKB(com, acom, sup, conf);
        }
        else if (acom != null && com == null) {
          //com = acom;
          com = new Composite();
          com.setTag(acom.getTag());
          com.setParent((Composite)kb);
          com.setRelativePosition(new RelativePosition(acom.getRelativePosition()));
          com.setTermSemantic(new TermSemantic(acom.getTermSemantic()));
          kb.addChild(com);
        }
        akb.removeChild(tag);
        sb.append(" "+tag+" ");
      }
    }
  }

  /**
   * classcount, classes, and semantics are members of TermSemantic that are accessible
   * for the public
   * so merge need to take care these members only.
   * @param ts
   * @param update
   */
  private static void mergeTS(TermSemantic ts, TermSemantic update, float sup, float conf) {
    if (ts == null && update != null) {
      ts = new TermSemantic(update);
      return;
    }
    if (update == null) {
      return;
    }
    int[] classcount = ts.getClasscount();
    String[] classes = ts.getClasses();
    Vector updated = updateClassCount(classcount, classes, update.getClasscount(),
                                      update.getClasses());
    ts.setClassCount( (int[]) updated.get(0), (String[]) updated.get(1));
    ts.setClasses( (String[]) updated.get(1));
    BinarySearchTree[] semantics = ts.getSemantics();
    updateSemantics(semantics, classes, update.getSemantics(),
                    update.getClasses(), avgscore);
    ts.setSemantics(semantics, (String[]) updated.get(1));
    update = null;
    ts.scoreTerms(false, sup, conf);
  }

  /**
   * classes and classcount are a pair, take care to make sure update count and its classes
   * is still paired-up.
   * @param classcount
   * @param classes
   * @param update
   * @param classes1
   * @return vector, 1st element is classcount, 2nd element is classes
   */
  private static Vector updateClassCount(int[] classcount, String[] classes,
                                 int[] update, String[] classes1) {
    StringBuffer sbvalue = new StringBuffer();
    StringBuffer sbclass = new StringBuffer();
    //read the update pair into a hashtable
    Hashtable updt = new Hashtable();
    for (int i = 0; i < classes1.length; i++) {
      updt.put(classes1[i], new Integer(update[i]));
    }

    for (int i = 0; i < classes.length; i++) {
      Integer I = (Integer) updt.get(classes[i]);
      int value = I == null ? 0 : I.intValue();
      classcount[i] += value;
      sbvalue.append(classcount[i]).append(" ");
      sbclass.append(classes[i]).append(" ");
      updt.remove(classes[i]);
    }
    //check to see if updt has any element remaining
    if (updt.size() != 0) {
      Enumeration en = updt.keys();
      while (en.hasMoreElements()) {
        String key = (String) en.nextElement();
        sbclass.append(key).append(" ");
        sbvalue.append( ( (Integer) updt.get(key)).intValue()).append(" ");
      }
      classes = sbclass.toString().trim().split(" ");
      String[] values = sbvalue.toString().trim().split(" ");
      classcount = new int[values.length];
      for (int i = 0; i < values.length; i++) {
        classcount[i] = Integer.parseInt(values[i]);
      }
    }
    Vector updated = new Vector();
    updated.add(classcount);
    updated.add(classes);
    update = null;
    classes1 = null;
    return updated;
  }

  /**
   * akb's terms + this' tmers
       * just put the terms together, if scores are not equal, pick the greater ones
   *
   * @param akb
   */
  public static void merge(Component kb, Component akb) {
    //merge this with akb
    TermSemantic ts1 = akb.getTermSemantic();
    BinarySearchTree[] trees1 = ts1.getSemantics();
    String[] classes1 = ts1.getClasses();
    TermSemantic ts = kb.getTermSemantic();
    BinarySearchTree[] trees = ts.getSemantics();
    String[] classes = ts.getClasses();
    updateSemantics(trees, classes, trees1, classes1, true);
    RelativePosition rp = kb.getRelativePosition();
    mergeRP(rp, akb.getRelativePosition()); //update the rp with a new rp
    //take care of children
    ArrayList achildren = akb.getChildren();
    ArrayList pool = new ArrayList();
    pool.addAll(achildren);
    pool.addAll(kb.getChildren());
    Iterator it = pool.iterator();
    StringBuffer sb = new StringBuffer();

    while (it.hasNext()) {
      Component pcom = (Component) it.next();
      String tag = pcom.getTag();
      if(sb.toString().indexOf(" "+tag+" ") < 0){
        Component acom = akb.getChild(tag);
        Component com = kb.getChild(tag);
        if (acom != null && com != null) {
          merge(com, acom);
        }
        else if (acom != null && com == null) {
          //com = acom;
          com = new Composite();
          com.setTag(acom.getTag());
          com.setParent( (Composite) kb);
          com.setRelativePosition(acom.getRelativePosition());
          com.setTermSemantic(acom.getTermSemantic());
          kb.addChild(com);
        }
        sb.append(" "+tag+" ");
      }
    }
    akb = null;
    //return kb;
  }

  /**
   * update the BT "semantics" with a BT "update"
   * @param semantics
   * @param update
   */
  private static void updateSemantics(BinarySearchTree[] semantics, String[] classes,
                              BinarySearchTree[] update, String[] classes1,
                              boolean score) {
    int sizes = semantics.length;
    for (int i = 0; i < sizes; i++) {
      mergeTrees(semantics[i], classes, update[i], classes1, score);
      update[i] = null;
    }
  }

  /**
   * use "update" to update btree
   * @param btree
   * @param update
   */
  private static void mergeTrees(BinarySearchTree btree, String[] classes,
                          BinarySearchTree update, String[] classes1,
                          boolean score) {
    if (update == null) {
      return;
    }
    StringBuffer csb = new StringBuffer();
    StringBuffer ucsb = new StringBuffer();
    for (int i = 0; i < classes.length; i++) {
      csb.append(" ").append(classes[i]).append(" ");
    }
    for (int i = 0; i < classes1.length; i++) {
      ucsb.append(" ").append(classes1[i]).append(" ");
    }
    String existclasses = csb.toString();
    String updateclasses = ucsb.toString();
    StringBuffer checked = new StringBuffer();
    Enumeration uen = update.elements(); //"update" en
    while (uen.hasMoreElements()) {
      Term uterm = (Term) uen.nextElement();
      if (btree.containsElement(uterm)) {
        Term bterm = (Term) btree.findElement(uterm);
        updateTerm(bterm, classes, uterm, classes1, score);
        checked.append(" ").append(bterm.getTerm()).append(" ");
        uterm = null;
      }
      else {
        //not only add uterm, but need to config its memebers like classes and classcount
        /*Term newterm = (Term)uterm.clone();
                   for(int i = 0; i < classes.length; i++){
          newterm.setCountForClass(0, classes[i]);
                   }
                   updateTerm(newterm, classes, uterm, classes1);
                   btree.addElement(newterm);*/
        adjustClasses(uterm, updateclasses, existclasses);
        btree.addElement(uterm.clone());
        checked.append(" ").append(uterm.getTerm()).append(" ");
        uterm = null;
      }
    }
    //update unchecked terms in btree, update their classes and classcount info
    String ck = checked.toString();
    Enumeration en = btree.elements();
    while (en.hasMoreElements()) {
      Term t = (Term) en.nextElement();
      if (ck.indexOf(" " + t.getTerm() + " ") < 0) {
        adjustClasses(t, existclasses, updateclasses);
      }
    }
  }

  private static void adjustClasses(Term t, String existclasses, String updateclasses) {
    updateclasses = updateclasses.replaceFirst("^ ", "").trim();
    String[] uclasses = updateclasses.split(" ");

    for (int i = 0; i < uclasses.length; i++) {
      if (existclasses.indexOf(" " + uclasses[i] + " ") < 0) {
        t.setCountForClass(0, uclasses[i]);
      }
    }
  }

  /**
   * term occurrences in different classes need to be updated using "update"
   *
   * @param term
   * @param update
   */
  private static void updateTerm(Term term, String[] classes, Term update,
                          String[] classes1, boolean score) {
    if (term.getTerm().compareTo(update.getTerm()) != 0) {
      System.err.println("Try to merge two different terms!");
    }
    Hashtable updt = new Hashtable();
    for (int i = 0; i < classes1.length; i++) {
      updt.put(classes1[i], "");
    }
    //update counts for each class
    for (int i = 0; i < classes.length; i++) {
      String clabel = classes[i];
      if (updt.containsKey(clabel)) {
        int upcount = update.getOccuranceIn(clabel);
        int count = term.getOccuranceIn(clabel);
        term.setCountForClass(upcount + count, clabel);
        if (score) {
          //update score for the class
          SCScore s = (SCScore) ( (Hashtable) term.getScore()).get(classes[i]);
          SCScore s1 = (SCScore) ( (Hashtable) update.getScore()).get(classes[i]);
          if (s == null && s1 != null) {
            s = new SCScore(s1);
            s1 = null;
          }
          else if (s != null && s1 != null) {
            s = updateScore(s, s1);
            s1 = null;
          }
        }
        updt.remove(clabel);
      }
    }

    Enumeration en = updt.keys();
    while (en.hasMoreElements()) {
      String ulabel = (String) en.nextElement();
      int upcount = update.getOccuranceIn(ulabel);
      term.setCountForClass(upcount, ulabel);
      if (score) {
        //update score
        SCScore s1 = (SCScore) ( (Hashtable) update.getScore()).get(ulabel);
        if (s1 != null) {
          ( (Hashtable) term.getScore()).put(ulabel, new SCScore(s1));
          s1 = null;
        }
      }
    }
  }

  /**
   * if s.conf = s1.conf = 0, do nothing
   * if s.conf = 0 s1.conf!=0, return s1
   * if s.conf != 0 s1.conf=0, return s
   * if s.conf != 0 s1.conf!=0, return avg(s, s1)
   * @param s
   * @param s1
   */
  private static SCScore updateScore(SCScore s, SCScore s1) {
    float conf = s.getConfidence();
    float conf1 = s1.getConfidence();
    if (Float.compare(conf, 0f) == 0 && Float.compare(conf1, 0f) == 0) {
      return s;
    }
    else if (Float.compare(conf, 0f) == 0 && Float.compare(conf1, 0f) != 0) {
      return new SCScore(s1);
    }
    else if (Float.compare(conf, 0f) != 0 && Float.compare(conf1, 0f) == 0) {
      return s;
    }
    else {
      float sup = s.getSupport();
      float sup1 = s1.getSupport();
      s.setConfidence( (conf + conf1) / 2);
      s.setSupport( (sup + sup1) / 2);
      return s;
    }
  }

  /**
   * use "update" to update rp
   * @param rp
   * @param update
   */

  private static void mergeRP(RelativePosition rp, RelativePosition update) {
    //update classcount and classes
    if (rp == null && update != null) {
      rp = new RelativePosition(update);
      update = null;
      return;
    }
    if (update == null) {
      return;
    }
    int[] classcount = rp.getClassCount();
    int[] updateccount = update.getClassCount();
    Vector updated = updateClassCount(classcount, rp.getClasses(), updateccount,
                                      update.getClasses());
    classcount = (int[]) updated.get(0);
    String[] classes = (String[]) updated.get(1);
    //update positioncounts
    Hashtable[] positioncounts = rp.getPositionCounts();
    Hashtable[] updatepcounts = update.getPositionCounts();
    positioncounts = updatePositionCounts(positioncounts, rp.getClasses(),
                                          updatepcounts, update.getClasses());
    //update selftransfer
    Hashtable self = rp.getSelfTransfer();
    Hashtable updateself = update.getSelfTransfer();
    updateSelfTransfer(self, updateself);
    //update rp
    rp.setClassCount(classcount);
    rp.setClasses(classes);
    rp.setPositionCounts(positioncounts);
    rp.setSelfTransfer(self);
    //rp.getProbabilities();
    update = null;
  }

  private static void updateSelfTransfer(Hashtable self, Hashtable update){
    Enumeration en = self.keys();
    StringBuffer checked = new StringBuffer();
    while(en.hasMoreElements()){
      String tag = (String)en.nextElement();
      Hashtable updateprob =(Hashtable) update.get(tag);
      if(updateprob != null){
        updateProb((Hashtable)self.get(tag), updateprob);
        checked.append(" "+tag+" ");
      }
    }
    String checkedtags = checked.toString();
    en = update.keys();
    while(en.hasMoreElements()){
      String tag = (String)en.nextElement();
      if(checkedtags.indexOf(tag) < 0){
        self.put(tag, update.get(tag));
      }
    }
  }
  /**
   * position[Integer] => prob [Float]
   * pick the greater prob of the two
   * @param prob Hashtable
   * @param update Hashtable
   */
  private static void updateProb(Hashtable prob, Hashtable update) {
    Enumeration en = prob.keys();
    StringBuffer checked = new StringBuffer();
    while (en.hasMoreElements()) {
      Integer position = (Integer) en.nextElement();
      Float pro = (Float) update.get(position);
      if (pro != null) {
        if (pro.compareTo( (Float) prob.get(position)) > 0) {
          prob.put(position, pro);
        }
        checked.append(" " + position.toString() + " ");
      }
    }
    String checkedpos = checked.toString();
    en = update.keys();
    while (en.hasMoreElements()) {
      Integer position = (Integer) en.nextElement();
      if (checkedpos.indexOf(position.toString()) < 0) {
        prob.put(position, update.get(position));
      }
    }

  }
  private static Hashtable[] updatePositionCounts(Hashtable[] pc, String[] classes,
                                          Hashtable[] updatepc,
                                          String[] classes1) {
    ArrayList result = new ArrayList();
    Hashtable updt = new Hashtable();
    for (int i = 0; i < classes1.length; i++) {
      updt.put(classes1[i], updatepc[i]);
    }
    //update
    for (int i = 0; i < classes.length; i++) {
      updateHash(pc[i], (Hashtable) updt.get(classes[i]));
      updt.remove(classes[i]);
      result.add(pc[i]);
    }
    //add remaining of updt
    Enumeration en = updt.keys();
    while (en.hasMoreElements()) {
      String label = (String) en.nextElement();
      result.add(((Hashtable)updt.get(label)).clone());
    }
    updatepc = null;
    //return
    return (Hashtable[]) result.toArray(new Hashtable[0]);
  }

  /**
   *
   * @param elementpc position => hash [element => count]
   * @param update position => hash [element => count]
   */
  private static void updateHash(Hashtable elementpc, Hashtable update) {
    if (update == null) {
      return;
    }
    Enumeration en = update.keys();
    while (en.hasMoreElements()) {
      Object key = en.nextElement();
      if (elementpc.containsKey(key)) {
        Hashtable epc = (Hashtable) elementpc.get(key);
        Hashtable upc = (Hashtable) update.get(key);
        updateHashCount(epc, upc); //epc and upc: element => count
      }
      else {
        elementpc.put(key, ((Hashtable)update.get(key)).clone());
      }
    }
    update = null;
  }

  /**
   *
   * @param epc element => count
   * @param upc element => count
   */
  private static void updateHashCount(Hashtable epc, Hashtable upc) {
    Enumeration en = upc.keys();
    while (en.hasMoreElements()) {
      Object key = en.nextElement();
      if (epc.containsKey(key)) {
        //sum up
        Integer count = (Integer) epc.get(key);
        Integer update = (Integer) upc.get(key);
        epc.put(key, new Integer(count.intValue() + update.intValue()));
      }
      else {
        Integer i =(Integer) upc.get(key);
        epc.put(key, i);
      }
    }
    upc = null;
  }

}
