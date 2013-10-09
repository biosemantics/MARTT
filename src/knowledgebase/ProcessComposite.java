package knowledgebase;

import java.io.Serializable;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import learning.Utilities;
import miner.TermSemantic;
import miner.RelativePosition;
import org.w3c.dom.*;

/**
 * <p>Title: Composite</p>
 * <p>Description: A composite node hold other components as its children</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class ProcessComposite
    /*extends ProcessComponent*/
    implements Serializable {
  protected ProcessComposite parent = null;
  protected ArrayList children = new ArrayList();
  protected String tag = null;
  protected int n = -1;
  protected float sup = -1f;
  protected float conf = -1f;
  protected Component kbase = null;
  protected boolean useStoplist = true;
  protected boolean debug = true;


  public ProcessComposite() {
  }

  public ProcessComposite(Component cparent, String tag,
                          String[] xmls, int n,
                          float sup, float conf, boolean debug) {
    this.tag = tag;
    this.n = n;
    this.sup = sup;
    this.conf = conf;
    this.debug = debug;
    if(this.debug){
      System.out.println("gether term semantic and relative positions for [" + tag + "]");
    }
    RelativePosition rp = mineRelativePosition(xmls);
    TermSemantic ts = mineTermSemantic(xmls);
    kbase = createKbase(cparent, rp, ts);
    dispatch(xmls); //go to child nodes
  }

  public Component createKbase(Component cparent, RelativePosition rp,
                               TermSemantic ts) {
    return new Composite(tag, (Composite) cparent, rp, ts, n, sup, conf);
  }

  public RelativePosition mineRelativePosition(String[] xmls) {
    int length = xmls.length;
    for (int f = 0; f < length; f++) {
       xmls[f] = xmls[f].replaceFirst(">\\s*null\\s*<", "><");
    }
    return new RelativePosition(xmls, getAllClasses(xmls));
  }

  /**
   * extract classes from xmls
   * mine termSemantics
   * @param xml
   * @return
   */
  public TermSemantic mineTermSemantic(String[] xmls) {
    String[] classes = getAllClasses(xmls);
    String[] flats = new String[xmls.length];
    for (int i = 0; i < flats.length; i++) {
      flats[i] = learning.Utilities.getFlatXml(xmls[i]);
    }
    System.out.println("obtained flat xml instances for mining term semantics");
    return new TermSemantic(flats, classes, n, conf, sup, new String[] {".",
                            ";",
                            ":"}
                            , useStoplist);
  }

  /**
   * xmls contains 1-level deep xml documents: root-element1
   *                                                element2
   *                                                ...
   *                                                elementx
   * @param xmls
   * @return
   */
  private String[] getAllClasses(String[] xmls) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < xmls.length; i++) {
      Document document = Utilities.getDomModel(xmls[i]);
      Node root = document.getDocumentElement();
      tag = root.getNodeName();
      for (Node achild = root.getFirstChild(); achild != null;
           achild = achild.getNextSibling()) {
        //assume no pcdata in non-leaf elements
        if (achild.getNodeType() == Node.ELEMENT_NODE) {
          if (sb.toString().indexOf(" " + achild.getNodeName() + " ") < 0) {
            sb.append(" " + achild.getNodeName() + " ");
          }
        }
      }
    }
    return sb.toString().replaceFirst("^\\s+", "").trim().split("\\s+");
  }

  /**
   * xmls: nested xml instances
   * extract relevant elements from xml and dispatch those to child nodes of kbase to populate them
   * @param xml
   */
  public void dispatch(String[] xmls) {
    Hashtable elements = new Hashtable(); //tag => xmls
    for (int i = 0; i < xmls.length; i++) {
      addInstancesTo(elements, xmls[i]);
    }
    Enumeration en = elements.keys();
    while (en.hasMoreElements()) {
      String tag = (String) en.nextElement();
      Component c = kbase.getChild(tag);
      ArrayList xmlset = (ArrayList) elements.get(tag);
      if (c != null) {
        System.err.println("ProcessComponent c " + c.getTag() +
                           "exists in knowledge base");
        /**@todo implement mergable and incremental knowledge base **/
      }
      else {
        if (isNested(xmlset)) {
          c = new ProcessComposite(kbase, tag,
                                   (String[]) xmlset.toArray(new String[1]), n,
                                   sup, conf, debug).getKbase();
        }
      }
    }
  }

  private boolean isNested(ArrayList xmlset) {
    if (xmlset == null) {
      return false;
    }
    Iterator it = xmlset.iterator();
    while (it.hasNext()) {
      String xml = (String) it.next();
      Pattern p = Pattern.compile("^<(.*?)>(.*)</\\1>$");
      Matcher m = p.matcher(xml);
      if (m.lookingAt()) {
        String content = m.group(2);
        if (content.indexOf("<") >= 0) {
          return true;
        }
      }
      else {
        System.err.println("xml element is in wrong format");
      }
    }
    return false;
  }

  private void addInstancesTo(Hashtable elements, String xml) {
    //copy from ElementComposite.populateChildren
    Document document = Utilities.getDomModel(xml);
    Node root = document.getDocumentElement();
    tag = root.getNodeName();
    for (Node achild = root.getFirstChild(); achild != null;
         achild = achild.getNextSibling()) {
      //assume no pcdata in non-leaf elements
      if (achild.getNodeType() == Node.ELEMENT_NODE) {
        String childtext = achild.toString(); //content of achild "<achild>.*?</achild>"
        if (childtext != null && childtext.compareTo("") != 0) {
          if (elements.containsKey(achild.getNodeName())) {
            ( (ArrayList) elements.get(achild.getNodeName())).add(childtext);
          }
          else {
            ArrayList l = new ArrayList();
            l.add(childtext);
            elements.put(achild.getNodeName(), l);
          }
        }
      }
    }
  }

  public String getTag() {
    return tag;
  }

  public Component getKbase() {
    return kbase;
  }




}
