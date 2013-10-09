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
import learning.MarkedSegment;
import miner.SemanticLabel;
/**
 * <p>Title: VisitorInsertMarkup</p>
 * <p>Description: insert marked-up examples to hierachy. used when comparing machine marked examples with answer keys</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public class VisitorInsertMarkup extends VisitorDoMarkup {
  public VisitorInsertMarkup(Vector xmlsegs, String filename, Composite knowledge, String order, String alg, String kbsc, String lrp, String kblrp) {
    super(xmlsegs,"", knowledge, order, alg, kbsc, lrp, kblrp);//no need for filename, so make it ""
  }
  /**
   * originally taken from VisitorInsertAnswer, but it should be producing MarkedSegment & not string.
   * modified on 8/27/2005
   * @param ec
   * @return
   */
  public Vector[] getMarkupInfo(ElementComposite ec){
    String currenttag = ec.getTag();
    Pattern p = Pattern.compile("\\A<" + currenttag + ">.*?</" + currenttag +
                                ">\\z");
    Vector[] result = new Vector[xmlsegs.size()];
    for (int i = 0; i < xmlsegs.size(); i++) {
      String content = ( (MarkedSegment) xmlsegs.get(i)).getSegment().trim().replaceAll("\\A\\s+",
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
/**
 * modified on 8/27/2005
 * @param key String
 * @param value String
 * @param table Hashtable
 */
private void addToHash(String key, String value, Hashtable table) {
  MarkedSegment ms = new MarkedSegment(value, new SemanticLabel("", "", key, 0f, 0f, ""));
  if (!table.containsKey(key)) {
    ArrayList list = new ArrayList();
    //list.add(value);
    list.add(ms);
    table.put(key, list);
  }
  else {
    //( (ArrayList) table.get(key)).add(value);
    ( (ArrayList) table.get(key)).add(ms);
  }
}

}
