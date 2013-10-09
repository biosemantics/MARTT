package structure;

import java.io.File;
import java.util.*;
import visitor.ElementComposite;
import visitor.ElementComponent;

/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: derive the dtd tree structure from well-formed text</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author not attributable
 * @version 0.1
 */

public class XMLDTDExtractor {
  private Hashtable DTD = null; // root element => [level 2 element]+ => [level 3 element]+
  private ElementComposite ec = null;

  public XMLDTDExtractor(File[] files) {
  //derive dtd
  //1. build a tree from the files
  ec = new ElementComposite();
  for (int i = 0; i < files.length; i++) {
    String text = learning.Utilities.readFile(files[i]);
    ec.addTrain(text, files[i].getName());//text along with filename
  }
  //2. read off the structure
  DTD = new Hashtable();
  buildDTD(DTD, ec);
  }

  /**
   * recursive
   * @param DTD
   * @param ec
   */
  private void buildDTD(Hashtable table, ElementComponent ec){
    Hashtable level = new Hashtable();
    table.put(ec.getTag(), level);
    Iterator it = ec.iterator();//get children of ec
    while(it !=null && it.hasNext()){
      buildDTD(level, (ElementComponent)it.next());
    }
  }
   /**
   * access method of dtd tree
   * @param parent. eg. root/level1/level2
   * @return the list of names of child element
   */
  public String[] getChildElements(String parent){
    String [] nodes = parent.split("/");
    Hashtable tree = DTD;
    for(int i = 0; i < nodes.length; i++){
      tree =(Hashtable) tree.get(nodes[i]);
    }
    return (String[])tree.keySet().toArray(new String[]{});
  }
  /**
   * print out the DTD structure
   * @return string
   */
  public String toString(){
    StringBuffer sb = new StringBuffer();
    sb = buildString(sb, DTD, "");
    return sb.toString();
  }
  /**
   * recursive. build string from dtd
   * @param sb
   */
  private StringBuffer buildString(StringBuffer sb, Hashtable table, String path){
    if(table != null){
      Enumeration en = table.keys();
      while (en != null && en.hasMoreElements()) {
        String tag = (String) en.nextElement();
        String full = path + tag;
        sb.append(full + "\n");
        sb = buildString(sb, (Hashtable) table.get(tag), full + "/");
      }
    }
    return sb;
  }

  public ElementComposite getXMLTree(){
    return ec;
  }

  public static void main(String[] args) {
  String srcData = "C:\\Documents and Settings\\hong cui\\Research-Exp\\jasist\\trainingdir-fna133\\";

  File dir = new File(srcData);
  File[] files = dir.listFiles();
  XMLDTDExtractor xde = new XMLDTDExtractor(files);
  System.out.println(xde.toString());
  System.out.println("child nodes of flowers: ");
  String[] nodes = xde.getChildElements("description/flowers");
  for(int i = 0; i < nodes.length; i++){
    System.out.println(nodes[i]);
  }
}



}
