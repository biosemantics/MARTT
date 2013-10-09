package marttinterface;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

// Basic GUI components
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

// GUI components for right-hand side
import javax.swing.JSplitPane;
import javax.swing.JEditorPane;

// GUI support classes
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;



// For creating a TreeModel
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


  // An array of names for DOM node-types
  // (Array indexes = nodeType() values.)


  // This class wraps a DOM node and returns the text we want to
  // display in the tree. It also returns children, index values,
  // and child counts.
  public class AdapterNode {
    org.w3c.dom.Node domNode;
    boolean elementOnly  = true;

    static final String[] typeName = {
     "none",
     "Element",
     "Attr",
     "Text",
     "CDATA",
     "EntityRef",
     "Entity",
     "ProcInstr",
     "Comment",
     "Document",
     "DocType",
     "DocFragment",
     "Notation",
 };


    // Construct an Adapter node from a DOM node
    public AdapterNode(org.w3c.dom.Node node) {
      domNode = node;
    }
    public Node getNode(){
      return domNode;
    }
    // Return a string that identifies this node in the tree
    // *** Refer to table at top of org.w3c.dom.Node ***
    public String toString() {
      String s = typeName[domNode.getNodeType()];
      String nodeName = domNode.getNodeName();
      if (!nodeName.startsWith("#")) {
        s += ": " + nodeName;
      }
      if (domNode.getNodeValue() != null) {
        if (s.startsWith("ProcInstr")) {
          s += ", ";
        }
        else {
          s += ": ";
        }
        // Trim the value to get rid of NL's at the front
        String t = domNode.getNodeValue().trim();
        int x = t.indexOf("\n");//get rid of extra characters
        if (x >= 0) {
          t = t.substring(0, x);
        }
        s += t;
      }
      return s;
    }

    /*
     * Return children, index, and count values
     */
    public int index(AdapterNode child) {
      //System.err.println("Looking for index of " + child);
      int count = childCount();
      for (int i = 0; i < count; i++) {
        AdapterNode n = this.child(i);
        if (child.domNode == n.domNode) {
          return i;
        }
      }
      return -1; // Should never get here.
    }

    public AdapterNode child(int searchIndex) {
      Node node = null;
      if (!elementOnly) {
        //Note: JTree index is zero-based.
        node =
            domNode.getChildNodes().item(searchIndex);
      }else{
        int n = domNode.getChildNodes().getLength();
        int count = 0;
        for(int i = 0; i < n; i++){
           node = domNode.getChildNodes().item(i);
          if(node.getNodeType() == Node.ELEMENT_NODE && count++ ==searchIndex){
            break;
          }
        }
      }
      return new AdapterNode(node);
    }

    public int childCount() {
      if(!elementOnly){
        return domNode.getChildNodes().getLength();
      }else{
        int count = 0;
        int n = domNode.getChildNodes().getLength();
        for (int i = 0; i < n; i++) {
          Node node = domNode.getChildNodes().item(i);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            count++;
          }
        }
        return count;
      }
    }
  }
