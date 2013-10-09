package xmltest;

import javax.xml.parsers.*;
import java.util.*;
import org.w3c.dom.*;
/**
 * <p>Title: XMLTest</p>
 * <p>Description: Learn to use SAX and DOM interface to processing XML documents</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class UseDOM {
  Document document;

  public UseDOM(String file) {
    try{
      DocumentBuilderFactory factory =
          DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse(file);
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  public Document getDocument(){
    return document;
  }

  public Hashtable getElementDic(){
    Hashtable elemDictionary = new Hashtable();
    Node root = document.getDocumentElement();
    System.out.println("Root = "+root.getNodeName());
    NodeList nList = root.getChildNodes();
    Node aNode;
    for(int i = 0; i<nList.getLength(); i++){
      aNode = nList.item(i);
      String name = aNode.getNodeName();
      System.out.println("NodeName = "+ name);
      System.out.println("NodeValue = "+aNode.getNodeValue());
      NodeList sList = aNode.getChildNodes();
      if(sList.getLength() != 0){
        for(int j = 0; j<sList.getLength(); j++){
          if(sList.item(j) instanceof org.w3c.dom.Text){
            elemDictionary.put(name, sList.item(j).getNodeValue());
          }
          System.out.println("sNodeName = "+sList.item(j).getNodeName());
          System.out.println("sNodeValue = "+sList.item(j).getNodeValue());
        }
      }
    }
    return elemDictionary;
  }
  public static void main(String[] args) {
    String file = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level1\\trainingdir-fna500\\f_ALISMATACEAE_g_SAGITTARIA_s_cristata.descrpt1";
    UseDOM useDOM1 = new UseDOM(file);
    Hashtable ht = useDOM1.getElementDic();
    System.out.println(ht.toString());

  }
}