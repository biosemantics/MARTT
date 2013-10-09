package marttinterface;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.*;

import java.io.*;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

// Basic GUI components
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.*;

/**
 * <p>Title: User Interface of MARTT </p>
 * <p>Description: Support training example annotation and marked-up example review.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */


  // This adapter converts the current Document (a DOM) into
  // a JTree model.
  public class DomToTreeModelAdapter
      implements javax.swing.tree.TreeModel {
    Document document = null;
    static String xmlout = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
    static String xmlin = "<?xml version=\"1.0\" encoding=\"Cp1252\"?>";
    //static String xml = "<?xml version=\"1.0\"?>";

    public DomToTreeModelAdapter(Object file) throws SAXException{
      try{
        //file may be a File or the content of the file represented as a string
        this.document = createDocument(file);
      }catch(SAXException saxe){
        throw saxe;
      }
    }
    public Object getRoot() {
      //System.err.println("Returning root: " +document);
      return new AdapterNode(document);
    }

    public boolean isLeaf(Object aNode) {
      // Determines whether the icon shows up to the left.
      // Return true for any node with no children
      AdapterNode node = (AdapterNode) aNode;
      if (node.childCount() > 0) {
        return false;
      }
      return true;
    }

    public int getChildCount(Object parent) {
      AdapterNode node = (AdapterNode) parent;
      return node.childCount();
    }

    public Object getChild(Object parent, int index) {
      AdapterNode node = (AdapterNode) parent;
      return node.child(index);
    }

    public int getIndexOfChild(Object parent, Object child) {
      AdapterNode node = (AdapterNode) parent;
      return node.index( (AdapterNode) child);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
      // Null. We won't be making changes in the GUI
      // If we did, we would ensure the new value was really new,
      // adjust the model, and then fire a TreeNodesChanged event.
    }

    /*
     * Use these methods to add and remove event listeners.
     * (Needed to satisfy TreeModel interface, but not used.)
     */
    private List listenerList = new LinkedList();
    public void addTreeModelListener(TreeModelListener listener) {
      if (listener != null
          && !listenerList.contains(listener)) {
        listenerList.add(listener);
      }
    }

    public void removeTreeModelListener(TreeModelListener listener) {
      if (listener != null) {
        listenerList.remove(listener);
      }
    }

    // Note: Since XML works with 1.1, this example uses Vector.
    // If coding for 1.2 or later, though, I'd use this instead:
    //   private List listenerList = new LinkedList();
    // The operations on the List are then add(), remove() and
    // iteration, via:
    //  Iterator it = listenerList.iterator();
    //  while ( it.hasNext() ) {
    //    TreeModelListener listener = (TreeModelListener) it.next();
    //    ...
    //  }

    /*
     * Invoke these methods to inform listeners of changes.
     * (Not needed for this example.)
     * Methods taken from TreeModelSupport class described at
     *   http://java.sun.com/products/jfc/tsc/articles/jtree/index.html
     * That architecture (produced by Tom Santos and Steve Wilson)
     * is more elegant. I just hacked 'em in here so they are
     * immediately at hand.
     */
    public void fireTreeNodesChanged(TreeModelEvent e) {
      Iterator listeners = listenerList.iterator();
      while (listeners.hasNext()) {
        TreeModelListener listener =
            (TreeModelListener) listeners.next();
        listener.treeNodesChanged(e);
      }
    }

    public void fireTreeNodesInserted(TreeModelEvent e) {
      Iterator listeners = listenerList.iterator();
      while (listeners.hasNext()) {
        TreeModelListener listener =
            (TreeModelListener) listeners.next();
        listener.treeNodesInserted(e);
      }
    }

    public void fireTreeNodesRemoved(TreeModelEvent e) {
      Iterator listeners = listenerList.iterator();
      while (listeners.hasNext()) {
        TreeModelListener listener =
            (TreeModelListener) listeners.next();
        listener.treeNodesRemoved(e);
      }
    }

    public void fireTreeStructureChanged(TreeModelEvent e) {
      Iterator listeners = listenerList.iterator();
      while (listeners.hasNext()) {
        TreeModelListener listener =
            (TreeModelListener) listeners.next();
        listener.treeStructureChanged(e);
      }
    }

    private Document createDocument(Object fpath) throws SAXException{
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.
        newInstance();
    try {
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
      if(fpath instanceof File){
      document =  docBuilder.parse((File)fpath);
      }else{
        String xmltext = (String)fpath;
        xmltext = xmltext.indexOf("<?") < 0 ? xmlin+xmltext : xmltext;
        document = docBuilder.parse(new InputSource(new StringReader(xmltext)));
      }
    }
    catch (SAXException sxe) {
      // Error generated during parsing
      /*Exception x = sxe;
      if (sxe.getException() != null) {
        x = sxe.getException();
      }
      x.printStackTrace();*/
      throw sxe;
    }
    catch (ParserConfigurationException pce) {
      // Parser with specified options can't be built
      pce.printStackTrace();
    }
    catch (IOException ioe) {
      // I/O error
      ioe.printStackTrace();
    }
    return document;
  }

  }

