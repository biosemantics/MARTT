package xmltest;

import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import java.util.Vector;
import java.util.Enumeration;
/**
 * <p>Title: XMLTest</p>
 * <p>Description: Learn to use SAX and DOM interface to processing XML documents</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class UseSAX extends DefaultHandler {
  Element previous = null;
  Element next = null;
  Element e;
  Vector elements=new Vector();
  String file = null;
  int count= 0;

  public UseSAX(String file){
    this.file = file;
  }

  public void startDocument() throws SAXException{
    System.out.println("start process document");
  }

  public void endDocument() throws SAXException{
    System.out.println("process results:");
    content(elements);
    try{
      FileOutputStream out = new FileOutputStream(file);
      ObjectOutputStream s = new ObjectOutputStream(out);
      s.writeObject(elements);
      s.flush();
      s.close();
    }catch(Exception e){
      System.err.println(e.toString());
    }
  }

  public void startElement(String namespaceURI,
                           String localName, String qName, org.xml.sax.Attributes atts)
      throws SAXException{
    elements.insertElementAt(new Element(), count++);
    e = (Element)elements.elementAt(count-1);
    if(previous != null){
      e.setPrevious(previous);
      previous.setNext(e);
    }

    e.setName(qName); //qName is the tag name
    e.setAtts(atts);
    if(atts != null)
      System.out.println("Attributes:"+atts.getQName(1)+"="+atts.getValue(1));
    previous = e;
  }

  /**
   * get the content for the element
   */
  public void characters(char[] buf, int offset, int len){
    String s = new String(buf, offset, len);
    System.out.println("Text1: "+s);
    String newStr = e.getText()+s; //element content may be broken up into several chunks.
    e.setText(newStr);
  }


  public void endElement(String namespaceURI, String localName, String qName)
      throws SAXException{
    //elements.addElement(e);
    //previous = e;
  }

  public static String convertToFileURL(String filename) {
        // On JDK 1.2 and later, simplify this to:
        // "path = file.toURL().toString()".
        String path = new File(filename).getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return "file:" + path;
    }

  public static void main(String[] args) throws Exception{
    String saveAsFile = "/home/hongcui/public_html/FOCSample.object";
    String xmlFile = "/home/hongcui/public_html/FOCSample.xml";
    UseSAX useSAX1 = new UseSAX(saveAsFile);

    SAXParserFactory spf = SAXParserFactory.newInstance();
    SAXParser saxParser = spf.newSAXParser();
    XMLReader xmlReader = saxParser.getXMLReader();
    xmlReader.setContentHandler(useSAX1);
    xmlReader.setErrorHandler(new MyErrorHandler(System.err));
    //System.out.println("start parse the file");
    xmlReader.parse(convertToFileURL(xmlFile));
    //System.out.println("File parsed");
    System.out.println("Check back the data saved...");
    //read back from file saveAsFile
    FileInputStream in = new FileInputStream(saveAsFile);
    ObjectInputStream s = new ObjectInputStream(in);
    Vector v = null;
    while((v = (Vector)s.readObject())!= null){
      content(v);
    }
    System.out.println("done");
  }

  private static void content(Vector v){
    Enumeration en = v.elements();
    while(en.hasMoreElements()){
      System.out.println(en.nextElement().toString());
    }
  }

}


