package visitor;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.*;
import java.util.Vector;
import org.w3c.dom.*;
import learning.Model;
import learning.Utilities;

/**
 * <p>Title: XML Hierarchy Using Visitor Pattern</p>
 * <p>Description: The composite class in the Composite Pattern</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 * @todo placeholders for answers???
 */

public class ElementComposite
    extends ElementComponent {
  protected int type = 0;
  protected Model learnedmodel = null;
  /**
   * assume xml is at least well formed
   * @param xml
   */
  public ElementComposite(String xml, String fname, boolean train) {
    super(xml, fname, train);
  }

  public ElementComposite() {
    super();
  }

  /**
   * convert a ElementLeaf to a ElementComposite
   * @param el
   */
  public ElementComposite(ElementComponent el) {
    this.children = new ArrayList();
    this.trainingexamples = el.getTrainingExamples();
    this.markeds = el.getMarkeds();
    this.answers = el.getAnswers();
    this.tag = el.getTag();
    this.parent = el.getParent();
    el.getParent().addChild(this);
    el.getParent().removeChild(el);
  }

  /**
   * Add an ElementComponent to the children
   * @param e An ElementComponent to be added to the children
   * @return ture, if e is added, otherwise false
   */
  public boolean addChild(ElementComponent e) {
    if (e != null) {
      children.add(e);
      return true;
    }
    return false;
  }

  public Iterator iterator() {
    return children.iterator();
  }

  /**
   * Remove an ElementComponent from the children
   * @param e An ElementComponent to be removed from the children
   * @return ture, if e is removed, otherwise false
   */
  public boolean removeChild(ElementComponent e) {
    if (e != null) {
      children.remove(e);
      return true;
    }
    return false;
  }

  /**
   * find in the children an elementcomponent with tag
   * @param tag
   * @return null if not found, otherwise return the elementcomponent
   */
  public ElementComponent findChild(String tag) {
    ElementComponent ec = null;
    Iterator it = children.iterator();
    while (it.hasNext()) {
      ec = (ElementComponent) it.next();
      if (ec.getTag().toLowerCase().compareTo(tag.toLowerCase()) == 0) {
        return ec;
      }
    }
    return null;
  }

  /**
   * after learning strategy is applied to learn a model for the component, save it to the component
   * @param m
   */
  public void setModel(Model m) {
    learnedmodel = m;
  }

  public Object getModel() {
    return learnedmodel;
  }

  /**
   * piece up to return complete marked up examples from children
   * recursion, each node collects the marked up pieces from its children and fixes them in original order
   * because it's possible to have one elements occurring multiple times in xml, it's possible for a node to
   * have more than one pieced-ups. that's the reason for return an array.
   *
   * @return
   */
  public String[] getMarked(int index) {
    Hashtable count = new Hashtable();
    Vector ordered = (Vector) markeds.get(index);
    Enumeration en = ordered.elements();
    StringBuffer[] marked = new StringBuffer[ordered.size()];
    int c = 0;
    while (en.hasMoreElements()) {
      marked[c] = new StringBuffer();
      marked[c].append("<" + tag + ">");
      String seg = (String) en.nextElement();
      boolean succeed = false; //if sub-elements are marked successfully
      //when false, tagorder is empty
      Iterator tagorder = Utilities.getTagOrder(seg); //a list of subelem tags in orig. order
      while (tagorder.hasNext()) {
        succeed = true;
        String tag = (String) tagorder.next();
        if (count.containsKey(tag)) {
          count.put(tag,
                    new Integer( ( (Integer) count.get(tag)).intValue() + 1));
        }
        else {
          count.put(tag, new Integer(1));
        }
        Iterator it = children.iterator(); //child nodes
        while (it.hasNext()) {
          ElementComponent ec = (ElementComponent) it.next();
          if (tag.compareTo(ec.getTag()) == 0) {
            marked[c].append(ec.getMarked(index)[ ( (Integer) count.get(tag)).
                             intValue() - 1]);
            break;
          }
        }
      }
      if (succeed == false) {
        marked[c].append(seg); //seg is not marked further, just return seg itself
      }
      marked[c].append("</" + tag + ">");
      c++;
    }

    String[] result = new String[marked.length];
    for (int i = 0; i < marked.length; i++) {
      result[i] = marked[i].toString();
    }
    return result;
  }

  /**
   * adjust markeds to reflect the correction/insertion to children's markeds
   * @param markeds
   * @param order
   */
  public void adjustMarkeds(Vector markeds, int order, String tag) {
    Vector p = (Vector)this.markeds.get(order);
    Enumeration men = markeds.elements();
    while (men.hasMoreElements()) {
      String mstring = (String) men.nextElement();
      Enumeration pen = p.elements();
      int count = 0;
      while (pen.hasMoreElements()) {
        String pstring = (String) pen.nextElement();
        if (pstring.replaceAll("\\s+",
                               "").indexOf(mstring.replaceAll("\\s+", "")) >= 0) {
          pstring = adjust(pstring, mstring, tag);
          p.set(count, pstring);
          break;
        }
        count++;
      }
    }
  }

  /**
   * not need to adjust markedsegs, they are merely the entire content of the element
   * @param markedsegs
   * @param order
   */
  /* public void adjustMarkedSegs(Vector markedsegs, int order, String tag) {
     Vector p = (Vector)this.markedsegs.get(order);
     Enumeration men = markedsegs.elements();
     while (men.hasMoreElements()) {
       String mstring = ( (MarkedSegment) men.nextElement()).getSegment();
       Enumeration pen = p.elements();
       while (pen.hasMoreElements()) {
         MarkedSegment pseg = (MarkedSegment) pen.nextElement();
         String pstring = pseg.getSegment();
         if (pstring.replaceAll("\\s+",
       "").indexOf(mstring.replaceAll("\\s+", "")) >= 0) {
           pstring = adjust(pstring, mstring, tag);
           pseg.setSegment(pstring);
           break;
         }
       }
     }
   }*/

  /**
   * replace mstring in pstring with <tag>mstring</tag>, and close/start neighboring elements
   *
   *
   * @param pstring
   * @param mstring
   * @return
   */
  private String adjust(String pstring, String mstring, String tag) {
    String newstring = "";
    String mstr = mstring.replaceAll("\\s+", " ").replaceAll("\\s*,\\s*", ", ").
        replaceAll("\\s*\\.\\s*", "\\\\. ").replaceAll("\\s*;\\s*", "; ").
        replaceAll("\\s*\\(\\s", " \\\\(").replaceAll("\\s*\\)\\s*", "\\\\) ").
        replaceAll("\\s*\\[\\s*",
                   " \\\\[").
        replaceAll("\\s*\\]\\s*", "\\\\] ").replaceAll("\\s*\\?\\s*", "\\\\? ").
        replaceAll("\\s*=\\s*",
                   "=").replaceAll("\\s*-\\s*", "\\\\-");
    String pstr = pstring.replaceAll("\\s+", " ").replaceAll("\\s*,\\s*", ", ").
        replaceAll("\\s*\\.\\s*", ". ").replaceAll("\\s*;\\s*", "; ").
        replaceAll("\\s*\\(\\s", " (").replaceAll("\\s*\\)\\s*", ") ").
        replaceAll("\\s*\\[\\s*",
                   " [").
        replaceAll("\\s*\\]\\s*", "] ").replaceAll("\\s*\\?\\s*", "? ").
        replaceAll("\\s*=\\s*",
                   "=").replaceAll("\\s*-\\s*", "-");

    Pattern p = Pattern.compile("(.*?)" + mstr + "([^<]*?</(.*?)>.*)");
    Matcher m = p.matcher(pstr);
    while (m.lookingAt()) {
      String etag = m.group(3);
      pstr = m.group(2);
      newstring = m.group(1) + "</" + etag + ">" + "<" + tag + ">" +
          mstr.replaceAll("\\\\", "") +
          "</" + tag + "><" + etag + ">";
      m = p.matcher(pstr);
    }
    newstring += pstr;
    if (newstring.compareTo(pstr) == 0) {
      System.err.println("adjust parent's marked failed");
    }
    newstring = newstring.replaceAll("<(.*?)>\\s*</\\1>", "");
    return newstring;
  }

  public void addAnswerExample(String xml) {
    answers.add(Utilities.getFlatXml(xml).toString());
  }

  /**
   * collaps xml into a flat well-formed xml 1 level deep, and add it to trainingexamples
   * @param xml
   */
  public void addTrainingExample(String xml, String filename) {
    String gfname = "";
    Pattern g = Pattern.compile(".*?g_(\\w+?)[\\._].*");
    Pattern f = Pattern.compile(".*?f_(\\w+?)[\\._].*");
    Matcher m = g.matcher(filename);
    if (m.lookingAt()) {
      gfname = m.group(1);
    }
    else {
      m = f.matcher(filename);
      if (m.lookingAt()) {
        gfname = m.group(1);
      }
      else {
        gfname = filename; //if filename is already f/g name
      }
    }
    System.out.println("111 :: " + Utilities.getFlatXml(xml));
    String flat = markNonspecified(Utilities.getFlatXml(xml));//non-specified may be tagged to a leaf element of an xml instance, because the element was earlier created as a composite element


    trainingexamples.add(flat);
    if (trainfilenames.containsKey(gfname)) {
      ( (ArrayList) trainfilenames.get(gfname)).add(flat);
    }
    else {
      ArrayList exps = new ArrayList();
      exps.add(flat);
      trainfilenames.put(gfname, exps);
    }
  }

  /**
   * extract immediate child elements and add them to the arraylist children
   * @param xml the xml string to be added
   * @param train is this xml training data(true) or test data/answer(false)
   */
  public void populateChildren(String xml, String fname, boolean train) {
    Document document = Utilities.getDomModel(xml);
    //extract elements at current level, make each elements a child ElementComponent
    Node root = document.getDocumentElement();
    tag = root.getNodeName();
    for (Node achild = root.getFirstChild(); achild != null;
         achild = achild.getNextSibling()) {
      if (achild.getNodeType() == Node.ELEMENT_NODE) {
        String childtext = achild.toString(); //content of achild "<achild>.*?</achild>"
        //String childtext = achild.getTextContent();
        if (childtext != null && childtext.compareTo("") != 0) {
          //if achild exists as a child comp to THIS, addTrainingExample, otherwise, create a new child
          ElementComponent ec = this.findChild(achild.getNodeName());
          if (ec == null) { //create new
            ec = hasGrandChildren(achild) ?
                (ElementComponent)new ElementComposite(childtext, fname, train) :
                new ElementLeaf(childtext, fname, train);
            //ec = (ElementComponent)new ElementComposite(childtext, train);
            ec.setParent(this);
            this.addChild(ec);
          }
          else { //add to trainingexamples
            if (train) {
              ec.addTrainingExample(childtext, fname);
            }
            else {
              ec.addAnswerExample(childtext);
            }
            if (ec instanceof ElementComposite) { //if ec is a composite
              ec.populateChildren(childtext, fname, train);
            }
            else { //if ec is a leaf
              //if achild embeds other elements, need to
              //convert ec from leaf to composite
              if (hasGrandChildren(achild)) {
                ElementComponent newec = new ElementComposite(ec);
                newec.populateChildren(childtext, fname, train);
              }
            }
          }
          //ec.populateChildren(childtext, train);
        }
      }
      else { //non-Element node
        String nonspecified = achild.getNodeValue();
        if (nonspecified != null && nonspecified.trim().compareTo("") != 0) {
          //make a non-empty non-specified element for non-specified or pcdata
          ElementLeaf et = (ElementLeaf)this.findChild(Model.nonspecified);
          nonspecified = "<" + Model.nonspecified + ">" +
              nonspecified.replaceAll("^\\s+", "").replaceAll("\\s+$", "") +
              "</" + Model.nonspecified + ">";
          if (et == null) {
            et = new ElementLeaf(nonspecified, fname, train);
            et.setParent(this);
            this.addChild(et);
          }
          else {
            if (train) {
              et.addTrainingExample(nonspecified, fname);
            }
            /*else {
              et.addAnswer(nonspecified);
                         }*/
          }
        }
      }
    }

  }


  public static String markNonspecified(String example) {
    String leftangel = "<";
    String rightangel = ">";
    String leftangelend = "</";

    StringBuffer sb = new StringBuffer();
    Pattern p = Pattern.compile("(.*?)(<(.*?)>.*?</\\3>)(.*)");
    Matcher m = p.matcher(example);
    while (m.lookingAt()) {
      /*sb.append(m.group(1).trim().compareTo("") != 0 ?
                "<" + Model.nonspecified + ">" + m.group(1).trim() + "</" +
                Model.nonspecified + ">" : "");*/
      if(m.group(1).trim().compareTo("") != 0){
        sb.append(leftangel).append(Model.nonspecified).append(rightangel).
            append(m.group(1).trim()).append(leftangelend).
            append(Model.nonspecified).append(rightangel);
      }
      sb.append(m.group(2));
      example = m.group(4);
      m = p.matcher(example);
    }
    if (example.trim().compareTo("") != 0) {
      sb.append(leftangel).append(Model.nonspecified).append(rightangel).append(example).append(leftangelend).
         append(Model.nonspecified).append(rightangel);
    }
    return sb.toString();
  }

  /**
   * test to see if any of anode's children has children
   * @param anode
   * @return true or false
   */
  private boolean hasGrandChildren(Node anode) {
    for (Node n = anode.getFirstChild(); n != null; n = n.getNextSibling()) {
      if (n.getChildNodes().getLength() > 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * print the component and all its children
   */
  public void print() {
    System.out.println("<" + tag + ">");
    Iterator it = children.iterator();
    while (it.hasNext()) {
      ElementComponent ec = (ElementComponent) it.next();
      ec.print();
    }
    System.out.println("</" + tag + ">");
  }

  public void accept(ReflectiveVisitor visitor, String alg) {
    visitor.dispatch(this, alg);
  }

  /**
   * child classes of this
   * @return
   */
  public ArrayList getChildClasses() {
    ArrayList classes = new ArrayList();
    Iterator it = children.iterator();
    while (it.hasNext()) {
      classes.add(getParentTags() + "/" +
                  ( (ElementComponent) it.next()).getTag());
    }
    return classes;
  }

  public ArrayList getChildSimpleClasses() {
    ArrayList classes = new ArrayList();
    Iterator it = children.iterator();
    while (it.hasNext()) {
      classes.add(((ElementComponent) it.next()).getTag());
    }
    return classes;
  }

  /**
   * get all tags of all children nodes, plus the tag of this node
   * @return
   */
  public ArrayList getAllClasses() {
    ArrayList classes = new ArrayList();
    Iterator it = children.iterator();
    classes.add(getParentTags() + "/" + tag);
    while (it.hasNext()) {
      classes.addAll( ( (ElementComponent) it.next()).getAllClasses());
    }
    return classes;
  }

  /**
   * get scores of immediate children
   * @return
   */
  public ArrayList getChildScores() {
    ArrayList scores = new ArrayList();
    Iterator it = children.iterator();
    while (it.hasNext()) {
      ElementComponent ec = (ElementComponent) it.next();
      String size = "" + ec.getTrainingExamples().size();
      scores.add(new MapEntry(ec.getTag(), ec.getScore(), size));
    }
    return scores;
  }

  /**
   * get scores of all child nodes of this branch
   * @return
   */
  public ArrayList getAllScores() {
    ArrayList scores = new ArrayList();
    String size = "" + trainingexamples.size();
    scores.add(new MapEntry(getParentTags() + "/" + tag, score, size));
    Iterator it = children.iterator();
    while (it.hasNext()) {
      ElementComponent ec = (ElementComponent) it.next();
      scores.addAll(ec.getAllScores());
    }
    return scores;
  }

  /**
   * reset scores of all children and this
   */
  public void resetScores() {
    Iterator it = children.iterator();
    while (it.hasNext()) {
      ElementComponent child = (ElementComponent) it.next();
      child.resetScores();
    }
    this.score = null;
  }

  /**
   * reset  of all children and this
   */
  public void resetAnswers() {
    Iterator it = children.iterator();
    while (it.hasNext()) {
      ElementComponent child = (ElementComponent) it.next();
      child.resetAnswers();
    }
    this.answers = new ArrayList();
  }

  public void resetTrains() {
    Iterator it = children.iterator();
    while (it.hasNext()) {
      ElementComponent child = (ElementComponent) it.next();
      child.resetTrains();
    }
    this.trainingexamples = new ArrayList();
    this.trainfilenames = new Hashtable();
  }

  /**
   * reset  of all children and this
   */
  public void resetMarkeds() {
    Iterator it = children.iterator();
    while (it.hasNext()) {
      ElementComponent child = (ElementComponent) it.next();
      child.resetMarkeds();
    }
    this.markeds = new ArrayList();
  }

  public void resetMarkedSegs() {
    Iterator it = children.iterator();
    while (it.hasNext()) {
      ElementComponent child = (ElementComponent) it.next();
      child.resetMarkedSegs();
    }
    this.markedsegs = new ArrayList();
  }

  /**
   *
   * @param args args[0]=dir--the source where the model will be learned
   *             args[1]=algorithm to be used. choose from NB, CT, LW, LWI, SC,
   *                     SCCP, SCCPI, SMCP, SMCPI
   */
  public static void main(String[] args) {
    String string = "";
    System.out.print(markNonspecified(string));
    ElementComposite ec = new ElementComposite();
    String dir = args[0];
    String modelfile = args[1];
    String alg = args[2];

    System.out.println(dir);
    System.out.println(modelfile);
    System.out.println(alg);
    
    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level1\\trainingdir-fna630-merged-bysent-level1-stratified\\";
    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level1\\trainingdir-fnct300\\";
    //String dir = "/home/hongcui/ThesisProject/Exp/level1/trainingdir-foc500-merged-bysent-level1-standardized/";
    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level2\\trainingdir-fna630-merged-bysent-level2-stratified\\";
    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level2\\trainingdir-foc500-merged-bysent-level2-standardized\\";
    //String alg = "SCCP";
    
    File srcdir = new File(dir);
    File[] filelist = srcdir.listFiles();
    for (int i = 0; i < filelist.length; i++) {
    	//System.out.println(filelist[i]);
    	String xml = learning.Utilities.readFile(filelist[i]);
    	xml = learning.Utilities.removeTaxon(xml);
    	//System.out.println(xml);
    	if (xml.trim().compareTo("") != 0) {
    		System.out.println(filelist[i].getName());
    		ec.addTrain(xml, filelist[i].getName());
    	}
	}
	if (alg.compareTo("NB") == 0 || alg.compareTo("CT") == 0 ||
		alg.compareTo("LW") == 0 || alg.compareTo("LWI") == 0) {
		//switch: VisitorLearnLessStructured or VisitorLearnSemiStructured
		ec.accept(new VisitorLearnLessStructured(), alg);
	}
    else {
      ec.accept(new VisitorLearnSemiStructured(), alg);
    }
    //serialize ec
    try {
      /*Serializer.serialization( (String) (ec.getModel().
                                          getClass().getDeclaredField(
          "modelfile")).get(null), ec);*/
      Serializer.serialization(modelfile, ec);
    }
    catch (SecurityException ex1) {
    }
    /*catch (NoSuchFieldException ex1) {
    }
    catch (IllegalAccessException ex1) {
    }*/
    catch (IllegalArgumentException ex1) {
    }
  }

}
