package marttinterface;

import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import java.util.Enumeration;
import java.net.URL;
import java.io.*;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;



/**
 * <p>Title: User Interface of MARTT </p>
 * <p>Description: Support training example annotation and marked-up example review.
 * this class builds tree structure of elements based on an xml schema</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */

public class SchemaPopupMenu{
   private String text = null;
   private File file = null;
   private ActionListener al = null;
   private MouseAdapter ma = null;
   private Hashtable erefhash = null; //hash stores element "ref"s
   private Hashtable grefhash = null; //hash stores group "ref"s
   private Hashtable trefhash = null; //hash stores types (without namespace)
   private Hashtable elemhash = null; //hash stores elements (corresponding to "ref"s) [element name => hashtable]
   private Hashtable grphash = null; //hash stores group elements (corresponding to "ref"s) [element name => hashtable]
   private Hashtable typehash = null; //hash stores complextype definition
   private Hashtable elemindxhash = null;
   private Hashtable[] components = null;//first level elements under root

   /**
    * constructor
    */
   public SchemaPopupMenu(String filename, ActionListener al, MouseAdapter ma) {
     file = new File(filename);
     this.al = al;
     this.ma = ma;
     erefhash = new Hashtable();
     grefhash = new Hashtable();
     trefhash = new Hashtable();
     elemhash = new Hashtable();
     grphash = new Hashtable();
     typehash = new Hashtable();
     elemindxhash = new Hashtable(); //tell the index of a value in elemhash in components
   }

   /**
    * parse xml schema and construct the menu
    */
   public JPopupMenu constructPopupMenu() {
     JPopupMenu popup = null;
     try {
       DocumentBuilderFactory factory =
           DocumentBuilderFactory.newInstance();
       DocumentBuilder builder = factory.newDocumentBuilder();
       Document doc = null;
       doc = builder.parse(file);
       Hashtable hierarchy = getHierarchy(doc); //build the hierarchy of elements, keys are the nodes
       Hashtable names = new Hashtable();
       convert(hierarchy, names); //convert nodes to strings (element name)
       String root =(String)names.keys().nextElement();
       JMenu menu = new JMenu(root);
       constructMenu((Hashtable)names.get(root), menu);
       popup = new JPopupMenu();
       popup.add(menu);
       return popup;
     }
     catch (FileNotFoundException e1) {
       e1.printStackTrace();
       return null;
     }
     catch (Exception e2) {
       e2.printStackTrace();
       return null;
     }
   }

   public void actionPerformed(ActionEvent en){
     if(en.getSource() instanceof JMenuItem || en.getSource() instanceof JMenu){
       JMenuItem item = (JMenuItem)en.getSource();
       System.out.println(item.getText());
     }
   }
   /**
    * parse doc into element hierarchy
    */
   public Hashtable getHierarchy(Document doc) {
     Node root = doc.getDocumentElement(); //root: schema
     //get first level components
     NodeList nList = root.getChildNodes();
     int l = nList.getLength();
     components = new Hashtable[l];
     for (int i = 0; i < l; i++) {
       if (nList.item(i).getNodeType() != Node.ELEMENT_NODE) {
         components[i] = null; //do not process non-element nodes (e.g comments)
       }
       else {
         components[i] = new Hashtable();
         buildHash(components[i], nList.item(i));
         //if element just parsed is refed somewhere, use parsed element to populate ref's hash
         if (components[i].keys().hasMoreElements()) {
           Node firstnode = (Node) components[i].keys().nextElement();
           if (nList.item(i).getNodeName().toLowerCase().indexOf("element") >=
               0) { //handle element refs
             handleElementRef(firstnode, i, components);
           }
           else if (nList.item(i).getNodeName().toLowerCase().indexOf("group") >=
                    0) { //handle group refs
             handleGroup(firstnode, i, components);
           }
           else if (nList.item(i).getNodeName().toLowerCase().indexOf(
               "complextype") >=
                    0) { //first level element. handle complexttype refs
             handleComplexType(firstnode, i, components);
           }
         }
       }
     }
     int rootindex = -1;
     int numroot = 0;
     for (int i = 0; i < l; i++) {
       if (components[i] != null &&
           nList.item(i).getNodeName().toLowerCase().indexOf("element") >=
           0) { //check for non null "element" components
         rootindex = i;
         numroot++;
         break;
       }
     }
     if (numroot > 1 || grefhash.size() > 0) {
       System.out.println("Incomplete parsing\n");
     }
     return components[rootindex];
   }

   /**
    * index element definitions.
    * solve element ref: when an element definition is parsed, find its ref point and populate the hash
    * note the interaction with group refs.
    * @param components[i] the element definition (parsed)
    *        firstnode the element
    */
   private void handleElementRef(Node firstnode, int i, Hashtable[] components) {
     elemhash.put(firstnode.getNodeValue(),
                  components[i].get(firstnode)); //index parsed elem hash
     elemindxhash.put(components[i].get(firstnode), new Integer(i)); //remember it's index in components
     //check to see if there is a registered "ref" to components[i].
     if (firstnode.getNodeName().toLowerCase() == "name") { //not a ref
       Hashtable ereghash = (Hashtable) erefhash.get(firstnode.
           getNodeValue());
       if (ereghash != null) { //registered
         Object o = components[i].get(firstnode);
         if (o instanceof Hashtable) { //not ""
           ereghash.putAll( (Hashtable) o);
           //if o is registered in grefhash, replace o with ereghash in grefhash
           Enumeration e = grefhash.keys();
           ArrayList keys = new ArrayList();
           while (e.hasMoreElements()) {
             String key = (String) e.nextElement();
             if ( ( (ArrayList) grefhash.get(key)).contains( (Hashtable)
                 o)) {
               keys.add(key);
             }
           }
           if (keys.size() > 0) {
             Iterator it = keys.iterator();
             while (it.hasNext()) {
               String key = (String) it.next();
               ArrayList list = (ArrayList) grefhash.get(key);
               list.remove( (Hashtable) o);
               list.add(ereghash);
               grefhash.put(key, list);
             }
           }
         }
         components[i] = null; //resolved element ref. Parsing is done when only 1 "element" left in components.
       }
     }

   }

   /**
    * index a group definition.
    * solve group refs.
    * @param components[i] the group defintion (parsed)
    *        firstnode the group element
    */
   private void handleGroup(Node firstnode, int i, Hashtable[] components) {
     grphash.put(firstnode.getNodeValue(), components[i].get(firstnode)); //index parsed group hash
     if (firstnode.getNodeName().toLowerCase() == "name") {
       ArrayList greghashlist = (ArrayList) grefhash.get(firstnode.
           getNodeValue());
       if (greghashlist != null) { //registered
         Iterator it = greghashlist.iterator();
         while (it.hasNext()) {
           Hashtable greghash = (Hashtable) it.next();
           greghash.putAll( (Hashtable) components[i].get(firstnode));
         }
         grefhash.remove(firstnode.getNodeValue()); //solved group ref. grefhash should be empty when parsing is done
       }
     }

   }

   /**
    * index a complextype definition.
    * solve complextype refs.
    * @param components[i] the complextype defintion (parsed)
    *        firstnode the complextype element
    */
   private void handleComplexType(Node firstnode, int i, Hashtable[] components) {
     typehash.put(firstnode.getNodeValue(), components[i].get(firstnode)); //index parsed type hash
     //check to see if there is registered type refs
     if (firstnode.getNodeName().toLowerCase() == "name") {
       ArrayList treghashlist = (ArrayList) trefhash.get(firstnode.
           getNodeValue());
       if (treghashlist != null) { //registered
         Iterator it = treghashlist.iterator();
         while (it.hasNext()) {
           Hashtable treghash = (Hashtable) it.next();
           treghash.putAll( (Hashtable) components[i].get(firstnode));
         }
         trefhash.remove(firstnode.getNodeValue()); //solved type ref. trefhash should be empty when parsing is done
       }
     }

   }

   /**
    * dump element hierarchy into multi-layer menu
    * @param names element hierarchy
    *        menu a multi-layer menu
    */
   public void constructMenu(Hashtable names, JMenu menu) {
     if (names == null) { //base
       return;
     }
     Enumeration en = names.keys();
     String[] sorted = (String[])names.keySet().toArray(new String[1]);
     Arrays.sort(sorted);
     for(int i = 0; i < sorted.length; i++) {
       String tag = sorted[i];
       Hashtable sub = (Hashtable) names.get(tag);
       if ( sub.size() == 0) {
         //leaf node
         JMenuItem item = new JMenuItem(tag);
         item.addActionListener(al);
         menu.add(item);
       }
       else {
         //internal node
         JMenu submenu = new JMenu(tag);
         submenu.addMouseListener(ma);//so submenu text may also be selected
         menu.add(submenu);
         constructMenu(sub, submenu);
       }
     }
   }

   /**
    * itaratively build a hashtable mapping elements and subelements relations.
    * @param elements a hashtable where the node may be put in (if node is an element). elements is the value to a higher level key.
    * @param node a node obtained from DOM
    *
    * each iteration processes one node and branches other processes for each of its childnode
    *
    */
   public void buildHash(Hashtable elements, Node node) {
     NodeList nList = node.getChildNodes();
     int size = nList.getLength();
     if (size == 0) { //base case: leaf node
       //if element, add it to the hash as keys.
       if (node.getNodeName().toLowerCase().indexOf("element") >= 0) {
         regElementRef(node, elements);
       }
       else if (node.getNodeName().toLowerCase().indexOf("group") >= 0) {
         regGroupRef(node, elements);
       }
       return;
     } //base case: leaf node

     //1. if node is an element
     if ( (node.getNodeName().toLowerCase().indexOf("element") >= 0 ||
           node.getNodeName().toLowerCase().indexOf("group") >= 0 ||
           node.getNodeName().toLowerCase().indexOf("complextype") >= 0) &&
         node.getAttributes().getNamedItem("name") != null) {
       Hashtable sub = new Hashtable();
       NamedNodeMap map = node.getAttributes();
       //Node bnode = null;
       Node bnode = map.getNamedItem("name");
       //if ( (bnode = map.getNamedItem("name")) != null) {
       elements.put(bnode, sub); //add node to the hash with a empty hash as its value.
       //The empty hash will be populated in next iterations using child nodes of the node
       //}
       Node anode = null;
       for (int i = 0; i < size; i++) {
         anode = nList.item(i);
         buildHash(sub, anode);
       }
     }
     else {
       //2. if node is not an element:skip and continue with child nodes
       Node anode = null;
       for (int i = 0; i < size; i++) {
         anode = nList.item(i);
         buildHash(elements, anode);
       }
     }
   }

   /**
    * regester element refs and types
    * if ref/type definition parsed, expand on ref/type
    */
   private void regElementRef(Node node, Hashtable elements) {
     NamedNodeMap map = node.getAttributes();
     Node bnode = null;
     if ( (bnode = map.getNamedItem("name")) != null) {
       Node anode = map.getNamedItem("type");
       if (anode != null && anode.getNodeValue().indexOf(":") < 0) { //expand on type
         //check to see if the parsed type exists
         Hashtable treghash = (Hashtable) typehash.get(anode.getNodeValue());
         if (treghash != null) {
           elements.put(bnode, treghash);
         }
         if (treghash == null) { // the same type can be used at many places in a doc, save all the context elements
           Object list = trefhash.get(anode.getNodeValue());
           Hashtable h = new Hashtable();
           if (list == null) {
             ArrayList alist = new ArrayList();
             alist.add(h);
             trefhash.put(anode.getNodeValue(), alist); //register type ref
           }
           else {
             ( (ArrayList) list).add(h);
             trefhash.put(anode.getNodeValue(), list); //register type ref
           }
           elements.put(bnode, h);
         }
       }
       else {
         elements.put(bnode, "");
       }

     }
     else if ( (bnode = map.getNamedItem("ref")) != null) {
       //check to see if the corresponding element exists
       Hashtable refhash = (Hashtable) elemhash.get(bnode.getNodeValue());
       if (refhash != null) {
         /*Enumeration enu = elemindxhash.keys();
                      System.out.println("keys are");
                      while(enu.hasMoreElements()){
           Hashtable k = (Hashtable)enu.nextElement();
           System.out.println(k.toString());
           System.out.println("This key equals to refhash? "+ (k == refhash));
              System.out.println("value to the key is "+ elemindxhash.containsKey(k));
                      }
                      System.out.println("elemindxhash contains the key refhash? "+elemindxhash.containsKey(refhash));
                      Object I = elemindxhash.get(refhash);
                      int i = ((Integer)I).intValue();
          */
           components[ ( (Integer) elemindxhash.get(refhash)).intValue()] = null;
       }
       if (refhash == null) {
         refhash = new Hashtable();
         erefhash.put(bnode.getNodeValue(), refhash); //register "ref": element ref is unique in a document
       }
       elements.put(bnode, refhash);
     }

   }

   /**
    * regester group refs
    * if ref definition parsed, expand on ref/type
    */

   private void regGroupRef(Node node, Hashtable elements) {
     NamedNodeMap map = node.getAttributes();
     Node bnode = null;
     if ( (bnode = map.getNamedItem("ref")) != null) {
       //check to see if the parsed group exists
       Hashtable greghash = (Hashtable) grphash.get(bnode.getNodeValue());
       if (greghash != null) {
         elements.putAll(greghash);
       }
       if (greghash == null) { // the same group ref can be used at many places in a doc, save all the context elements
         Object list = grefhash.get(bnode.getNodeValue());
         if (list == null) {
           ArrayList alist = new ArrayList();
           alist.add(elements);
           grefhash.put(bnode.getNodeValue(), alist); //register group "ref"
         }
         else {
           ( (ArrayList) list).add(elements);
           grefhash.put(bnode.getNodeValue(), list); //register group "ref"
         }
       }
     }

   }

   /**
    * depth first traverse of the element hierarchy with nodes
    * @param elements a hashtable/string in the hierarchy
    */
   private void print(Object elements) {
     if (elements instanceof String) {
       return;
     }
     if (elements instanceof Hashtable) {
       Enumeration en = ( (Hashtable) elements).keys();
       while (en.hasMoreElements()) {
         Node n = (Node) en.nextElement();
         System.out.println(n.getNodeValue());
         Object sub = ( (Hashtable) elements).get(n);
         print(sub);
       }
     }
   }

   /**
    * node => string
    */
   private void convert(Object nodes, Hashtable names) {
     if (nodes instanceof String) {
       return;
     }
     if (nodes instanceof Hashtable) {
       Enumeration en = ( (Hashtable) nodes).keys();
       while (en.hasMoreElements()) {
         Node n = (Node) en.nextElement();
         Object subnodes = ( (Hashtable) nodes).get(n);
         Hashtable subnames = new Hashtable();
         names.put(n.getNodeValue(), subnames);
         convert(subnodes, subnames);
       }
     }
   }

   //without actionlistener, main doesn't work
   public static void main(String[] args) {
     //ClassMenu classMenu1 = new ClassMenu(null,
     //                         "/home/hongcui/ThesisProject/DTD/test2.xsd"); //test2 have run-time problems
     //ClassMenu classMenu1 = new ClassMenu(null, "/home/hongcui/ThesisProject/DTD/test.xsd");
     //ClassMenu classMenu1 = new ClassMenu(
     //    "C:\\Documents and Settings\\hong cui\\ThesisProject\\DTD\\plant-working.xsd", null, null);
     SchemaPopupMenu classMenu1 = new SchemaPopupMenu(
         "U:\\Research\\AlgeaData\\algea-data\\schema.xsd", null, null);
     final JPopupMenu popup = classMenu1.constructPopupMenu();
     JFrame f = new JFrame();
     JTextArea t = new JTextArea(20, 20);
     t.setText("select a chunk of text and mark up it!");
     t.addMouseListener(new MouseAdapter() {
       public void mousePressed(MouseEvent e) {
         if (e.getButton() == MouseEvent.BUTTON3) {
           popup.show(e.getComponent(), e.getX(), e.getY());
         }
       }
     });
     f.getContentPane().add(t);
     f.pack();
     f.setVisible(true);
   }

 }

