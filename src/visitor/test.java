package visitor;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import jds.collection.BinarySearchTree;

/**
 * <p>Title: XML Hierarchy Using Visitor Pattern</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class test {
  public test() {
  }
  public void testReplaceAll(String xml){
    String s = xml.replaceAll("^\\s+","").replaceAll("\\s+$","");
    System.out.println("*"+xml.replaceAll("^\\s+","").replaceAll("\\s+$","")+"*");
  }
  public void testparse(String xml){
    Document document = null;
    try {
    	DocumentBuilderFactory factory =
       DocumentBuilderFactory.newInstance();
   DocumentBuilder builder = factory.newDocumentBuilder();
   document = builder.parse(new ByteArrayInputStream(xml.getBytes()));
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  private Document getDomModel(String xml) {
      Document document = null;
      String xmltext = xml;
      if (xml.indexOf("<?") < 0) {
        xmltext = "<?xml version=\"1.0\" encoding=\"iso8859-1\"?>" + xml;
      }
      //build dom for xml
      try {
        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(new ByteArrayInputStream(xmltext.getBytes(
            "ISO-8859-1")));
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      return document;
    }

  public void addTrainingExample(String xml) {
      StringBuffer example = new StringBuffer();
      Document doc = getDomModel(xml);
      Node root = doc.getDocumentElement();
      for (Node n = root.getFirstChild(); n != null; n = n.getNextSibling()) {
        if (n.getNodeType() == Node.ELEMENT_NODE) {
          String text = getNodeContent(n, new String());
          example.append("<" + n.getNodeName() + ">" + text + "</" +
                         n.getNodeName() + ">");
        }
      }
      System.out.println(example.toString());
    }

    /**
     * get all the textual content in the node n, use recursion
     * @param n
     * @return a plain string without tags
     */
    private String getNodeContent(Node n, String text) {
      Node first = n.getFirstChild();
      if (first.getNodeType() == Node.TEXT_NODE && first.getNodeValue() != null) { //base
        text += first.getNodeValue()+" ";
        return text.trim();
      }
      for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
        if (d.getNodeType() == Node.ELEMENT_NODE) {
          text = getNodeContent(d, text)+" ";
        }
      }
      return text.trim();
    }

  public static void main(String[] args){
    String xml="<?xml version=\"1.0\" encoding=\"ISO8859-1\"?>  <description><taxon><family>ARACEAE</family><genus>CALLA</genus></taxon><plant-habit-and-life-style>Herbs, wetland.</plant-habit-and-life-style><stems>Rhizomes horizontal.</stems><leaves>Leaves several, emergent, appearing before flowers, arising along rhizome, also clustered terminally; petiole 1.5�2 or more times as long as blade; blade bright green, not peltate, ovate to nearly round, base cordate, apex short-acuminate to apiculate; lateral veins parallel.</leaves><inflorescences>Inflorescences: peduncle, as long as or longer than petiole; spathe white, often green or partially green abaxially, open at maturity, not enclosing spadix; spadix cylindric.</inflorescences><flowers>Flowers all bisexual or distal ones staminate; perianth absent.</flowers><fruits>Fruits not embedded in spadix, red.</fruits><seeds>Seeds 4�9(�11), embedded in mucilage.</seeds><chromosomes>x = 18.</chromosomes> </description> ";
    //String xml ="  something is not right   ";
    //new test().addTrainingExample(xml);
    //BinarySearchTree bt = new BinarySearchTree(new learning.Term());
    //System.out.println(bt.toString());
    System.out.println(System.getProperty("file.encoding"));
  }
}
