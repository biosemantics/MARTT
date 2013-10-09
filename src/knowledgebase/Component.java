package knowledgebase;

import miner.RelativePosition;
import miner.TermSemantic;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.*;
/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: Thesis Project, everything in this project</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public abstract class Component implements Serializable{
  protected RelativePosition rp = null;
  protected TermSemantic ts = null;
  protected Composite parent = null;
  protected ArrayList children = new ArrayList();
  protected String tag = null;
  //protected int n = 3;
  //protected float sup = 0.01d;
  //protected float conf = 0.8d;

  public Component()  {
  }

  public Component(String tag, Composite parent, RelativePosition rp, TermSemantic ts,  int n,
                     float sup, float conf){
      this.tag = tag;
      this.parent = parent;
      //this.n = n;
      //this.sup = sup;
      //this.conf = conf;
      this.rp = rp;
      this.ts = ts;
      if(parent!=null){parent.addChild(this);}
 }

 /**
  * xpath: /description/leaves/petiole
  * @param xpath
  * @return
  */
 public TermSemantic getTermSemanticFor(String xpath){
   Component com = getComponent(xpath, this);
   return com== null? null : com.getTermSemantic();
 }

 /**
 * xpath: /description/leaves/petiole
 * @param xpath
 * @return
 */
public RelativePosition getRelativePositionFor(String xpath){
  Component com = getComponent(xpath, this);
  return com== null? null : com.getRelativePosition();
}

 /**
  * xpath: /description/leaves/petiole
  * recursive
  * @param xpath
  * @return
  */
 public Component getComponent(String xpath, Component kbase){
   Pattern p = Pattern.compile("/(.*?)(/.*)?$");
   Matcher m = p.matcher(xpath.trim());
   String node = "";
   String remain = "";
   if(m.lookingAt()){
      node = m.group(1);
      remain = m.group(2) == null ? "" : m.group(2).trim();
   }
   if(kbase.getTag().compareToIgnoreCase(node) != 0){
     System.err.println("xpath ["+xpath+"] is not compatable with the structure of the knowledge base");
     return null;
   }
   if(remain.compareTo("") != 0){
     m = p.matcher(remain);
     if(m.lookingAt()){
       node = m.group(1);
       Iterator it = kbase.getChildren().iterator();
       while(it.hasNext()){
         Component com = (Component)it.next();
         if(com.getTag().compareToIgnoreCase(node)==0){
           return getComponent(remain, com);
         }
       }
     }
   }else{
     return kbase;
   }
   return null;
 }

 /**
   * recursion
   * @param kb
   */
  public void printKnowledgeBase(Component kb){
    //termsemantic
    TermSemantic ts = kb.getTermSemantic();
    if (ts != null) {
      System.out.println();
      System.out.println(">>>>>>>>>>>>>KB-" + kb.getTag() + ":");
      ts.printTermsForClasses(0.01f, 0.7f);
      System.out.println("<<<<<<<<<<<<<end of KB-" + kb.getTag());
      System.out.println();
    }

    //relativeposition
    RelativePosition rp = kb.getRelativePosition();
    if (rp != null) {
      System.out.println();
      System.out.println(">>>>>>>>>>>>>KB-" + kb.getTag() + ":");
      rp.printPositions(rp.getPositions());
      System.out.println("<<<<<<<<<<<<<end of KB-" + kb.getTag());
      System.out.println();
    }

    Iterator it = kb.getChildren().iterator();
    while(it.hasNext()){
      Component child = (Component)it.next();
      printKnowledgeBase(child);
    }

  }

 public abstract ArrayList getChildren();
 public abstract boolean addChild(Component child);
 public abstract Component getChild(String tag);
 public abstract void removeChild(String tag);

 public void setTag(String tag){
     this.tag = tag;
   }

   public void setRelativePosition (RelativePosition rp){
     this.rp = rp;
   }

   public void setTermSemantic(TermSemantic ts){
     this.ts = ts;
   }

   public void setParent(Composite parent){
     this.parent = parent;
   }

   public String getTag(){
     return tag;
   }

   public RelativePosition getRelativePosition(){
     return rp;
   }

   public TermSemantic getTermSemantic(){
     return ts;
   }

   public Composite getParent(){
     return parent;
   }

}