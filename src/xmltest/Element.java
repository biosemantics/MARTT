package xmltest;

import org.xml.sax.*;
import java.io.Serializable;
/**
 * <p>Title: XMLTest</p>
 * <p>Description: Learn to use SAX and DOM interface to processing XML documents</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class Element implements Serializable{
  Element previous;
  Element next;
  String text;
  String name;
  Attributes atts; //make a serializable Attributes

  public Element() {
    this.previous = null;
    this.next = null;
    this.text ="";
    this.name ="";
    this.atts = null;
  }

  public Element(String name) {
    this.previous = null;
    this.next = null;
    this.text ="";
    this.name =name;
    this.atts = null;
  }

  public void setName(String name){
    this.name = name;
  }

  public void setText(String text){
    this.text = text;
  }

  public void setPrevious(Element prev){
    this.previous = prev;
  }

  public void setNext(Element next){
    this.next = next;
  }

  public void setAtts(org.xml.sax.Attributes atts){
    int n = atts.getLength();
    String[] name = new String[n];
    String[] values = new String[n];
    for (int i = 0; i < n; i++) {
      name[i] = atts.getQName(i);
      values[i] = atts.getValue(i);
    }

    this.atts = new Attributes(name, values);
  }

  public String getName(){
    return name;
  }

  public String getText(){
      return text;
    }

  public String toString(){
    StringBuffer b = new StringBuffer("");
    b.append("Element Name:");
    b.append(this.name);
    b.append("\n\n");
    b.append("Element Text:");
    b.append(this.text);
    b.append("\n\n");
    b.append("Element Attributes:");
    int n = atts.getLength();
    if(n==0){
      b.append("None");
    }else{
      for (int i = 0; i < n; i++) {
          b.append("\nAttrName:" + atts.getQName(i));
          b.append("\nAttrValue:" + atts.getValue(i));
      }
    }
    b.append("\n\n");
    b.append("Previous Element Name:");
    if(previous != null){
      b.append(previous.getName());
    }else{
      b.append("NULL");
    }
    b.append("\n\n");
    b.append("Next Element Name:");
    if(next != null){
      b.append(next.getName());
    }else{
      b.append("NULL");
    }
    b.append("\n\n");

    return b.toString();

  }


  public static void main(String[] args) {
    Element element1 = new Element();
  }

}