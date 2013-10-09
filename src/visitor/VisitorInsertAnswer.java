package visitor;

import learning.Model;
import learning.Utilities;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.*;
import org.w3c.dom.*;
//import miner.TermSemantic;
import knowledgebase.Composite;

/**
 * <p>Title: VisitorInsertAnswer</p>
 * <p>Description: add answer keys to learning and markup hierachy</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public class VisitorInsertAnswer
    extends VisitorInsert {
  //inherit type: "answer" or "result"

  public VisitorInsertAnswer(Vector answer, String filename, Composite knowledge, String order, String alg, String kbsc, String lrp, String kblrp) {
    super(answer, "", knowledge, order, alg, kbsc, lrp, kblrp); //this.xmlsegs = answer, which contains nonflat xmls
  }


  public Vector[] getMarkupInfo(ElementComposite ec) {
    String currenttag = ec.getTag();
    Pattern p = Pattern.compile("\\A<" + currenttag + ">.*?</" + currenttag +
                                ">\\z");
    Vector[] result = new Vector[xmlsegs.size()];
    for (int i = 0; i < xmlsegs.size(); i++) {
      String content = ( (String) xmlsegs.get(i)).trim().replaceAll("\\A\\s+",
          "");
      Matcher m = p.matcher(content);
      if (!m.lookingAt()) {
        content = "<none>" + content + "</none>"; //make it well-formed xml
      }
      result[i] = new Vector(2);
      String flat = Utilities.getFlatXml(content);
      result[i].add(getHash(content)); //keep non-flat xml for dispatching
      result[i].add(new StringBuffer(flat));
    }
    return result;
  }

  /**
   * tag => content list
   * @param xml
   * @return
   */
  private Hashtable getHash(String xml) {
    Hashtable table = new Hashtable();
    Document doc = Utilities.getDomModel(xml);
    Node root = doc.getDocumentElement();
    for (Node content = root.getFirstChild(); content != null;
         content = content.getNextSibling()) {
      if (content.getNodeType() == Node.ELEMENT_NODE) {
        String cont = content.toString();
        String tag = content.getNodeName();
        Pattern p = Pattern.compile("\\A<" + tag + ">(.*?)</" + tag +
                                    ">\\z");
        Matcher m = p.matcher(cont);
        m.lookingAt();
        addToHash(tag, m.group(1), table);
      }
      else if (content.getNodeType() == Node.TEXT_NODE &&
               content.getNodeValue().trim().compareTo("") != 0) {
        String tag = Model.nonspecified;
        String cont = content.getNodeValue();
        addToHash(tag, cont, table);
      }
    }
    return table;
  }

  private void addToHash(String key, String value, Hashtable table) {
    if (!table.containsKey(key)) {
      ArrayList list = new ArrayList();
      list.add(value);
      table.put(key, list);
    }
    else {
      ( (ArrayList) table.get(key)).add(value);
    }
  }

  public void addMarkedSegs(ElementComponent ec, Vector markedsegs, String order) {
    //do nothing, prscore do not use markeds, but markedsegs
  }
  /**
   * anwsers are alway add at the end of the arraylist, so "order" is not used.
   * arraylist.add(int, object) does not go beyond current size of the arraylist
   * @param ec
   * @param markeds
   * @param order
   */
  public void addMarkeds(ElementComponent ec, Vector markeds, String order) {
    String currenttag = ec.getTag();
    Pattern p = Pattern.compile("\\A<" + currenttag + ">(.*?)</" + currenttag +
                                ">\\z");
    //flatten elements in marksegs before add to ec
    Vector flats = new Vector();
    Enumeration segs = markeds.elements();
    while (segs.hasMoreElements()) {
      String content = ( (String) segs.nextElement()).trim().replaceAll(
          "^\\s+",
          "");
      Matcher m = p.matcher(content);
      if (!m.lookingAt()) {
        content = "<none>" + content + "</none>";
      }
      flats.add(Utilities.getFlatXml(content));
    }
    ec.addAnswer(flats);
  }

  public void visitElementLeaf(ElementLeaf el, String alg) {

    //if xmlsegs is not simple, convert el to ElementComposite and dispatch xml to el
    boolean complex = false;
    Enumeration en = xmlsegs.elements();
    while (en.hasMoreElements()) {
      String xml = (String) en.nextElement(); //<leaves>...</leaves>
      Pattern p = Pattern.compile("(.*?)<(.*?)>(.*?)</\\2>(.*)");
      Matcher m = p.matcher(xml);
      if (m.lookingAt()) {
        String remain = m.group(1) + m.group(3) + m.group(4);
        complex = remain.matches(".*?<(.*?)>.*?</\\1>.*");
      }
    }
    if (complex) {
      //convert ec to ElementComposite and dispatch xmlsegs to it
      ElementComposite sub = new ElementComposite(el);
      //create "non-specified" child for sub and copy sub.markeds to "non-specified" child
      ElementLeaf nonspec = new ElementLeaf();
      nonspec.setTag(Model.nonspecified);
      nonspec.setParent(sub);
      sub.addChild(nonspec);
      nonspec.setMarkeds(sub.getMarkeds());
      sub.accept(new VisitorInsertAnswer(xmlsegs, "", knowledge, order, alg, kbsc, lrp, kblrp), alg);
    }
    else {
      addMarkeds(el, xmlsegs, order);
    }
  }

  protected void addPlaceHolderForUnmatched(Vector unmatched) {
    Iterator unmatcheds = unmatched.iterator();
    while (unmatcheds.hasNext()) { //unmatched
      ElementComponent ect = (ElementComponent) unmatcheds.next();
      Vector placeholder = new Vector();
      placeholder.add("");
      addMarkeds(ect, placeholder, order);
      //need to add placeholder for all ect's offspring nodes
      addPlaceHolderForChildren(ect);
    }
  }

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
        //put placeholders-1 in el.answers [and placeholders-1+1 in el.markeds]
        //no need to place placeholder in markeds, markeds will contain just null vectors
        int placeholders = ec.getAnswers().size();
        Vector temp = new Vector();
        for (int i = 0; i < placeholders - 1; i++) {
          temp = new Vector();
          temp.add("");
          el.addAnswer(temp);
        }
        //dispatching to el
        el.accept(new VisitorInsertAnswer(segs, "", knowledge, order, alg, kbsc, lrp, kblrp),alg);
      }

    }

  protected void addPlaceHolderForChildren(ElementComponent ec) {
    Vector placeholder = new Vector();
    placeholder.add("");
    Iterator children = ec.iterator();
    if (children == null) {
      return;
    }
    while (children.hasNext()) {
      ElementComponent c = (ElementComponent) children.next();
      addMarkeds(c, placeholder, order);
      addPlaceHolderForChildren(c);
    }
  }

}
