package marttinterface;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.tree.*;

// For creating borders
import javax.swing.border.EmptyBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
// regular exp
import java.util.regex.*;

import org.xml.sax.*;
import org.w3c.dom.*;

/**
 * <p>Title: User Interface of MARTT </p>
 * <p>Description: Support training example annotation and marked-up example review.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */

public class ListController {
  InterfaceFrame frame = null;
  JScrollPane treeScrollPane;
  JSplitPane splitPane;
  JTextPane textPane; //where an xml file is displayed
  DefaultListModel listmodel = new DefaultListModel();
  JList list = new JList(listmodel);
  /*JLabel editableLabel = new JLabel();
     JLabel uneditableLabel = new JLabel();
     JButton refreshButton = new JButton();*/
  JLabel dirtyLabel = new JLabel();
  boolean dirty = false;
  int start; //the text selection range on xmlPane
  int end;
  JTree tree; //xml structure tree
  DomToTreeModelAdapter dom2tree;
  TreePath treepath; //the treepath of the node currently displayed
  String selectedText;
  String defaultnewline;
  static String newline = "\n";
  JPopupMenu classPopup = null;
  Vector records = new Vector();
  boolean adderror = true;
  /*JMenuItem jMenuItemPurgeList = new JMenuItem();
     JMenuItem jMenuItemShowList = new JMenuItem();
     JMenuItem jMenuItemImportList = new JMenuItem();
     JMenuItem jMenuItemRemoveListFile = new JMenuItem();
     JMenu jMenuToDoList = new JMenu();*/
  static final String[] stylecode = {
      "regular", "bold"};

  public ListController(InterfaceFrame frame) {
    this.frame = frame;
    /*java.net.URL url = InterfaceFrame.class.getResource(
        "toolbarButtonGraphics/general/Edit24.gif");
         if (url != null) {
      editableLabel.setIcon(new ImageIcon(url));
      editableLabel.setToolTipText("Editable");
         }
         else {
      editableLabel.setText("Editable");
         }

         url = InterfaceFrame.class.getResource(
        "toolbarButtonGraphics/general/Stop24.gif");
         if (url != null) {
      uneditableLabel.setIcon(new ImageIcon(url));
      uneditableLabel.setToolTipText("Uneditable");
         }
         else {
      uneditableLabel.setText("Uneditable");
         }*/

    //refreshButton = getRefreshButton();
    dirtyLabel = new JLabel("*");

    // Build right-side view:textPane
    textPane = new JTextPane();
    textPane.setContentType("text/plain; charset=Cp1252");
    textPane.setEditable(true);
    textPane.setMargin(new Insets(10, 10, 10, 10));
    textPane.addMouseListener(new ListController_xmlPane_mouseAdapter(this));
    //save system newline
    //defaultnewline = (String) textPane.getStyledDocument().getProperty(
    //    DefaultEditorKit.EndOfLineStringProperty);
    //use "\n" for newline for in memory text

    textPane.getStyledDocument().putProperty(DefaultEditorKit.
                                             EndOfLineStringProperty, "\n");

    frame.getSplitPane().setRightComponent(new JPanel());
    classPopup = new SchemaPopupMenu(Setting.schemapath,
                                     new
                                     ListController_classPopup_actionAdapter(this),
                                     new
                                     ListController_classPopup_mouseAdapter(this)).
        constructPopupMenu();
    if (classPopup == null) {
      JOptionPane.showMessageDialog(frame,
                                    "Please check and make sure DTD or Schema file exist and are valid");
    }

    //add Edit and Format menu
    //if ( ( (JMenu) frame.jMenuBar1.getComponent(1)).getText().compareTo("Edit") !=
    //    0) {
    //if (frame.jMenuBar1.getComponentCount() == 3) {
    //TextPaneController tpc = new TextPaneController(frame, textPane);
    /*JMenu jMenuEdit = tpc.createEditMenu();
           JMenu jMenuFormat = tpc.createFormatMenu();
           JMenu jMenuError = new JMenu("Error Record");
           JMenuItem jMenuItemAddRecord = new JMenuItem("Add record");
           jMenuItemAddRecord.addActionListener(new
     ListController_jMenuItemAddRecord_actionAdapter(this));
           JMenuItem jMenuItemShowStat = new JMenuItem("Show statistics");
           jMenuItemShowStat.addActionListener(new
     ListController_jMenuItemShowStat_actionAdapter(this));
           jMenuError.add(jMenuItemAddRecord);
           jMenuError.add(jMenuItemShowStat);

           jMenuToDoList.setText("To Do List");
           jMenuItemPurgeList.setEnabled(false);
           jMenuItemShowList.setEnabled(false);
           jMenuItemImportList.setEnabled(true);
           jMenuItemImportList.setActionCommand("Review");
           jMenuItemImportList.addActionListener(new
        InterfaceFrame_jMenuItemImportList_actionAdapter(frame));
           jMenuItemShowList.setText("Show list");
           jMenuItemImportList.setText("Import list");
           jMenuItemPurgeList.setText("Purge list");
           jMenuItemRemoveListFile.setActionCommand("RemoveListFile");
           jMenuItemRemoveListFile.setText("Remove list file");
           jMenuItemRemoveListFile.addActionListener(new
        InterfaceFrame_jMenuItemRemoveListFile_actionAdapter(frame));
           jMenuItemShowList.addActionListener(new
     InterfaceFrame_jMenuItemShowList_actionAdapter(
        frame));
           jMenuItemPurgeList.addActionListener(new
     InterfaceFrame_jMenuItemPurgeList_actionAdapter(
        frame));
           jMenuToDoList.add(jMenuItemRemoveListFile);
           jMenuToDoList.addSeparator();
           jMenuToDoList.add(jMenuItemPurgeList);
           jMenuToDoList.add(jMenuItemShowList);
           jMenuToDoList.addSeparator();
           jMenuToDoList.add(jMenuItemImportList);
           frame.jMenuBar1.add(jMenuEdit, 1);
           frame.jMenuBar1.add(jMenuFormat, 2);
           frame.jMenuBar1.add(jMenuError, 3);
           frame.jMenuBar1.add(jMenuToDoList, 4);*/
    //}

  }

  /*public JButton getRefreshButton() {
    java.net.URL url = InterfaceFrame.class.getResource(
        "toolbarButtonGraphics/general/Refresh24.gif");
    if (url != null) {
      refreshButton.setIcon(new ImageIcon(url));
      refreshButton.setToolTipText("Refresh");
    }
    else {
      refreshButton.setText("Refresh");
    }
    refreshButton.addActionListener(new
   ListController_refreshButton_actionAdapter(this));

    return refreshButton;
     }*/

  /**
   * perform certain functions according to the given setting
   * @param null
   * @return null
   */
  public void setUp() {
    //set up the list
    if (frame.getSetting().getTodoList() == null) {
      File file = new File(frame.getSetting().getFilePath());
      ArrayList mflist = new ArrayList();
      if (file.isDirectory()) { //from a directory
        File[] flist = file.listFiles();
        int count = 0;
        for (int i = 0; i < flist.length; i++) {
          if (addToList(flist[i])) {
            mflist.add(new MyFile(flist[i]));
          }
        }
      }
      else {
        if (addToList(file)) {
          mflist.add(new MyFile(file));
        }
      }
      frame.getSetting().setTodoList(new Vector(mflist));
      //if there is only 1 file in mflist, display the content in the textarea
      if (mflist.size() == 1) {
        list.setSelectedIndex(0);
        display( ( (MyFile) mflist.get(0)).getFile());
      }
    }
    //display list pane in frame
    //frame.jMenuItemSave.setEnabled(true);
    //frame.jMenuItemSaveAs.setEnabled(true);
    //frame.jMenuItemSave2Depot.setEnabled(true);
    //jMenuItemPurgeList.setEnabled(true);
    //jMenuItemShowList.setEnabled(true);
    frame.setDisplayed(-1); //to avoid to prompt for saving files from last session
    showList();

    //create popup menu from the schema
    /*frame.jSplitPane.setRightComponent(new JPanel());
         classPopup = new SchemaPopupMenu(frame.setting.getSchemaPath(),
                                     new
     ListController_classPopup_actionAdapter(this),
                                     new
     ListController_classPopup_mouseAdapter(this)).
        constructPopupMenu();
         if (classPopup == null) {
      JOptionPane.showMessageDialog(frame,
     "Please check and make sure DTD or Schema file exist and are valid");
         }*/
    //frame.toolBar.setListController(this);
    //frame.jToolBar = frame.toolBar.setDefaultToolBar(ToolBar.ANNOTE_MODE);
    //remove menuBar?
    //frame.menuBar.setListController(this);
    //frame.setJMenuBar(frame.menuBar.setDefaultMenuBar(MenuBar.ANNOTE_MODE));
  }

  public void showList() {
    list = new JList(frame.getSetting().getTodoList());
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setLayoutOrientation(JList.VERTICAL);
    list.setVisibleRowCount( -1);
    list.addListSelectionListener(new
                                  ListController_list_listSelectionListenerAdapter(this));
    //frame.listScrollPane = new JScrollPane(list);
    JScrollPane listScrollPane = new JScrollPane(list);
    //frame.getSplitPane().setLeftComponent(frame.listScrollPane);
    frame.getSplitPane().setLeftComponent(listScrollPane);
    frame.getSplitPane().setDividerLocation(200);
  }

  /**
   *  put the files that are not in the depot in the list
   * @param f
   * @return
   */
  private boolean addToList(File f) {
    boolean add = false;
    if (frame.getSetting().getDiff()) {
      File thisfile = new File(frame.getSetting().getDepotPath(), f.getName());
      add = thisfile.exists() ? false : true;
    }
    else {
      add = true;
    }
    return add;
  }

  public void valueChanged(ListSelectionEvent e) {
    if (!e.getValueIsAdjusting()) {
      if (textPane != null) {
        textPane.setText("");
      }
      if (tree != null) {
        tree.setVisible(false);
      }
      frame.setDisplayed(list.getSelectedIndex());
      System.out.println("new displayed: " + frame.getDisplayed());
      File file = ( (MyFile) frame.getSetting().getTodoList().get(frame.getDisplayed())).
          getFile();
      display(file);
      dirty = false;
    }
  }

  /**
   * display the content of the file in the textarea
   * @param file
   */
  private void display(File file) {
    String content = readFileContent(file);
    displayText(content);
    frame.getStatus().setText(file.getName() + " [" +
                            (list.getSelectedIndex() + 1) + "/" +
                            frame.getSetting().getTodoList().size() + "]");

  }

  public void displayText(String content) {
    //build and show the tree
    try {
      if (content.indexOf(DomToTreeModelAdapter.xmlin) < 0 &&
          content.indexOf("<") < 0) {
        //insert a fake element dooo to ensure the well-formedness of annotation
        //annotation
        content = content.replaceAll(System.getProperty("line.separator"), "");
        String ctext = "<description><dooo>" + content +
            "</dooo></description>";
        dom2tree = new DomToTreeModelAdapter(ctext);
      }
      else {
        dom2tree = new DomToTreeModelAdapter(content); //review
      }
      tree = new JTree(dom2tree);
      treeListener(tree);
      JScrollPane xmlScrollPane = new JScrollPane(textPane,
                                                  JScrollPane.
                                                  VERTICAL_SCROLLBAR_AS_NEEDED,
                                                  JScrollPane.
                                                  HORIZONTAL_SCROLLBAR_NEVER);
      treeScrollPane = new JScrollPane(tree);

      splitPane =
          new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                         treeScrollPane,
                         xmlScrollPane);
      splitPane.setContinuousLayout(true);
      splitPane.setDividerLocation(200);
      frame.getSplitPane().setDividerLocation(200);
      frame.getSplitPane().setRightComponent(splitPane);
    }
    catch (SAXException saxe) {
      JOptionPane.showMessageDialog(frame,
                                    "File is not well-formed or valid XML");
      frame.setDisplayed(-1);
      saxe.printStackTrace();
    }

  }

  public void treeListener(JTree tree) {
    tree.addTreeSelectionListener(
        new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        treepath = e.getNewLeadSelectionPath();
        if (treepath != null) {
          AdapterNode adpnode = (AdapterNode) treepath.getLastPathComponent();
          if (adpnode.getNode() == dom2tree.document.getDocumentElement()) {
            textPane.setEditable(true);
            //frame.toolBar.setListController(frame.listcontroller);
            //frame.toolBar.adjustToolBarEditButtons(true);
          }
          else {
            textPane.setEditable(false);
            //frame.toolBar.setListController(frame.listcontroller);
            //frame.toolBar.adjustToolBarEditButtons(false);
            //adjustToolBarEditButtons(false);
          }
          style4XmlPane(adpnode.getNode().toString());
        }
      }
    }
    );

  }

  /* public void adjustToolBarEditButtons(boolean editable) {
     frame.jToolBar.repaint(); //make any previous changes take effects
     int editableindex = frame.jToolBar.getComponentIndex(editableLabel);
     int uneditableindex = frame.jToolBar.getComponentIndex(
         uneditableLabel); //current status
     if (editable) {
       textPane.setEditable(true);
       frame.jMenuBar1.getComponent(1).setEnabled(true); //enable edit menu
       if (uneditableindex < 0 && editableindex < 0) {
         frame.jToolBar.add(refreshButton);
         frame.jToolBar.addSeparator(new Dimension(10, 34));
         frame.jToolBar.add(editableLabel);
         frame.jToolBar.repaint();
       }
       else if (uneditableindex >= 0 &&
   frame.jToolBar.getComponentAtIndex(uneditableindex - 1) instanceof
                JToolBar.Separator) {
         frame.jToolBar.remove(uneditableLabel);
         frame.jToolBar.add(refreshButton, uneditableindex - 1);
         frame.jToolBar.add(editableLabel, uneditableindex + 1);
         frame.jToolBar.repaint();
       }
     }
     else {
       textPane.setEditable(false);
       frame.jMenuBar1.getComponent(1).setEnabled(false); //disable edit menu
       if (uneditableindex < 0 && editableindex < 0) {
         frame.jToolBar.addSeparator(new Dimension(10, 34));
         frame.jToolBar.add(uneditableLabel);
         frame.jToolBar.repaint();
       }
       else if (editableindex >= 0 &&
   frame.jToolBar.getComponentAtIndex(editableindex - 1) instanceof
                JToolBar.Separator) { //change from editable to uneditable
         frame.jToolBar.remove(refreshButton);
         frame.jToolBar.remove(editableLabel);
         frame.jToolBar.remove(editableindex - 2); //remove the seperator
         //for some reason the following two lines have no effect on the toolbar
         frame.jToolBar.addSeparator(new Dimension(10, 34));
         frame.jToolBar.add(uneditableLabel, editableindex - 1);

       }
     }

   }*/

  /**
   * display tags in bold
   * so far could not get indentions work.
   * @param xmlseg
   */
  public void style4XmlPane(String xmlseg) {

      StyledDocument doc = textPane.getStyledDocument();

      try {
        doc.remove(0, doc.getLength()); //clean up whatever is left on the pane
        Style deflt = StyleContext.getDefaultStyleContext().getStyle(
            StyleContext.
            DEFAULT_STYLE);
        LinkedList[] styledlists = new LinkedList[] {
            new LinkedList(), new LinkedList()};
        try {
          AdapterNode root = new AdapterNode(new DomToTreeModelAdapter(xmlseg).
                                             document);
          styledLists(root, styledlists, 0, doc, deflt);
          LinkedList stylelist = styledlists[0];
          LinkedList textlist = styledlists[1];
          Iterator it = stylelist.iterator();
          int i = 0;
          while (it.hasNext()) {
            String s = (String) it.next();
            doc.insertString(doc.getLength(), (String) textlist.get(i++),
                             doc.getStyle(s));
          }
        }
        catch (SAXException saxe) {
          saxe.printStackTrace();
        }

      }
      catch (Exception e) {
        e.printStackTrace();
      }

  }

  /**
   *
   * @param doc StyledDocument
   * @param deflt Style
   * @param xmlseg String
   * @param stylentext LinkedList[]
   * @param insert int
   * @param indent int
   * @param insertednouse int
   * @return int
   */
  private void styledLists(AdapterNode node, LinkedList[] stylentext,
                           int indent, StyledDocument doc, Style deflt) {
    Style regular = doc.addStyle("regular" + indent, deflt);
    StyleConstants.setLeftIndent(deflt, 10);
    Style boldindent = doc.addStyle("boldindent" + indent, deflt);
    StyleConstants.setBold(boldindent, true);
    StyleConstants.setLeftIndent(boldindent, indent);
    StyleConstants.setFirstLineIndent(boldindent, indent);
    Style bold = doc.addStyle("bold" + indent, deflt);
    StyleConstants.setBold(bold, true);

    String btag = "<" + node.getNode().getNodeName() + ">";
    String etag = "</" + node.getNode().getNodeName() + ">";
    if (btag.indexOf("#") < 0) { //not to print doc root
      stylentext[0].add("boldindent" + indent); //beginning tag
      stylentext[1].add(btag);
      stylentext[0].add("regular" + indent); //new line
      stylentext[1].add(newline);
    }
    //do the children
    int count = node.childCount();
    if (count == 0) { //leaf node
      String content = node.getNode().toString().replaceAll("<.*?>", "");
      if ( (String) stylentext[1].getLast() == newline) {
        stylentext[1].removeLast();
        stylentext[0].removeLast();
      }
      stylentext[0].add("regular" + indent); //content
      stylentext[1].add(content);
    }
    else {
      for (int i = 0; i < count; i++) {
        styledLists(node.child(i), stylentext, indent + 3, doc, deflt);
      }
    }
    //close tag
    if (etag.indexOf("#") < 0) {
      stylentext[0].add("boldindent" + indent); //beginning tag
      stylentext[1].add(etag);
      stylentext[0].add("regular" + indent); //new line
      stylentext[1].add(newline);
    }
  }

  /**
   * recursive
   * @param doc document model of the textpane
   * @param deflt default style
   * @param xmlseg xmlseg for this round of recursion
   * @param stylentext array of two linkedlist, one for style, one for text
   * @param insert the number of elements inserted in front of the current element so far
   * @param indent the amount of indent to be used for this round of recursion
   * @param inserted the total number of elements inserted in this round
   * @return number of elements inserted so far
   */
  /*private int styledLists(StyledDocument doc, Style deflt, String xmlseg,
                          LinkedList[] stylentext, int insert, int indent,
                          int insertednouse) {
    Style regular = doc.addStyle("regular" + indent, deflt);
    StyleConstants.setLeftIndent(deflt, 40);

    Style boldindent = doc.addStyle("boldindent" + indent, deflt);
    StyleConstants.setBold(boldindent, true);
    StyleConstants.setLeftIndent(boldindent, indent);
    StyleConstants.setFirstLineIndent(boldindent, indent);
    Style bold = doc.addStyle("bold" + indent, deflt);
    StyleConstants.setBold(bold, true);

    Pattern p = Pattern.compile("(<([-\\w]+)>)(.*?)(</\\2>)(.*)");
    Matcher m = p.matcher(xmlseg.trim());
    int inserted = 0; //# of elements inserted in this round
    while (m.matches()) {
      xmlseg = m.group(5);
      String content = m.group(3);
      String btag = m.group(1);
      String etag = m.group(4);

      if (content.indexOf("<") < 0) {
        //base:
   stylentext[0].add(inserted + insert, "boldindent" + indent); //beginning tag
        stylentext[1].add(inserted + insert, btag);
        stylentext[0].add(inserted + insert + 1, "regular" + indent); //content
        stylentext[1].add(inserted + insert + 1, content);
        stylentext[0].add(inserted + insert + 2, "bold" + indent); //end tag
        stylentext[1].add(inserted + insert + 2, etag);
   stylentext[0].add(inserted + insert + 3, "regular" + indent); //new line
        stylentext[1].add(inserted + insert + 3, newline);
        inserted += 4;
      }
      else {
        //add new lines
   stylentext[0].add(inserted + insert, "boldindent" + indent); //beginning tag
        stylentext[1].add(inserted + insert, btag);
   stylentext[0].add(inserted + insert + 1, "regular" + indent); //new line
        stylentext[1].add(inserted + insert + 1, newline);
   stylentext[0].add(inserted + insert + 2, "boldindent" + indent); //end tag
        stylentext[1].add(inserted + insert + 2, etag);
   stylentext[0].add(inserted + insert + 3, "regular" + indent); //new line
        stylentext[1].add(inserted + insert + 3, newline);

        inserted +=
            styledLists(doc, deflt, content, stylentext, insert + 2,
                        indent + 50, inserted + 2) + 4;
      }
      m = p.matcher(xmlseg);
    }
    if (xmlseg.compareTo("") != 0) {
      System.err.println("Remaining segment: " + xmlseg);
    }
    return inserted;
     }*/

  private String readFileContent(File file) {
    int size = (int) file.length();
    int chars_read = 0;
    String content = null;
    try {
      FileReader in = new FileReader(file);
      char[] data = new char[size];
      while (in.ready()) {
        chars_read += in.read(data, chars_read, size - chars_read);
      }
      in.close();
      content = new String(data, 0, chars_read);
    }
    catch (FileNotFoundException ex) {
      JOptionPane.showMessageDialog(frame, "Can not open a non-regular file",
                                    "Information", JOptionPane.ERROR_MESSAGE);
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    return content;
  }

  /**
   * reflect the updates made in textPane to the tree structure
   */
  public void refreshTree() {
    //the element to which the text displayed belong
    Node node = dom2tree.document.getDocumentElement();
    //update dom model
    try {
      String text = this.textPane.getText().replaceAll(newline, "").trim();
      if (text.compareTo("") != 0 /*&&
                     frame.jToolBar.getComponentIndex(refreshButton) >= 0*/) {
        dom2tree = new DomToTreeModelAdapter(text);
        tree = new JTree(dom2tree);
        treeListener(tree);
      }
    }
    catch (SAXException saxe) {
      saxe.printStackTrace();
    }
    //update structure tree
    treeScrollPane = new JScrollPane(tree);
    splitPane.setDividerLocation(200);
    splitPane.setLeftComponent(treeScrollPane);
  }

  public void popupMenu_actionPerformed(ActionEvent e) {
    if (e.getSource() instanceof JMenuItem) {
      String classname = ( (JMenuItem) e.getSource()).getText();
      frame.getStatus().setText(classname);
      if(textPane.isEditable()){
        editMarkup(classname);
      }
      classPopup.setVisible(false);
    }
  }

  private void editMarkup(String classname) {
    String beforetag = getTagBefore(); //the start tag that enbraces the selected text
    String aftertag = getTagAfter(); //the end tag that enbraces the selected text
    String text = selectedText.replaceAll("<[-\\w]+?>",
                                          "").replaceAll("</[-\\w]+?>", "");
    String newstr = beforetag == null ? "" : "</" + beforetag + ">";
    //String oldstr = newstr + selectedText + aftertag == null ? "" : "<" + aftertag + ">";
    newstr += "<" + classname + ">" + text + "</" +
        classname + ">";
    newstr += aftertag == null ? "" : "<" + aftertag + ">";
    if(adderror){
      String fname = ( (MyFile) frame.getSetting().getTodoList().get(
          frame.getDisplayed())).getFile().getName();
      generateRecords(selectedText,
                               "<" + classname + ">" + text + "</" +
                               classname + ">", classname, fname, beforetag, aftertag); //generate error record automatically
    }
    String start = textPane.getText().substring(0, this.start);
    String end = textPane.getText().substring(this.end);
    System.out.println("end string:" + end);
    newstr = start + newstr + end;
    newstr = newstr.replaceAll(newline, " ").replaceAll("<([-\\w]+?)>\\s*</\\1>",
                               " ").replaceAll("\\s+", " ");
    try {
      new DomToTreeModelAdapter(newstr);
      style4XmlPane(newstr);
      dirty = true;
    }
    catch (SAXException saxe) {
      JOptionPane.showMessageDialog(frame, "Edit results in malformed XML");
      records.removeAllElements();
      System.out.println(newstr);
    }
  }

  /**
   * compare wrong against correct and generate error record
   * @param wrong String  .... ||  ...</> [<> ....</>]* <>...
   * @param correct String <tag>....</tag>
   */
  private void generateRecords(String wrong, String correct, String tag, String filename, String btag, String atag){
    wrong = wrong.replaceFirst("^\\s*", "").replaceAll(newline, " ").trim();
    correct = correct.replaceFirst("^\\s*", "").replaceAll(newline, " ").trim();
    Pattern p = Pattern.compile("(.*?)</([-\\w]+?)>(.*)");
    //Pattern p = Pattern.compile("<([-\\w]+?)>(.*?)</\\1>\\s*(.*)");
    Matcher m = p.matcher(wrong);
    //if wrong is the same as correct, ignore
    if(wrong.compareTo(correct) != 0){
      //process segment by segment
      while(m.matches()){
        String textseg = m.group(1);
        String lab = m.group(2);
        wrong = m.group(3);
        m = p.matcher(wrong);
        if(lab.compareTo(tag) != 0){
          if(textseg.indexOf("<") >= 0){
            textseg = textseg.replaceFirst("<[-\\w]+?>", "");
            records.add(new ErrorRecord(filename, "Classification Error", textseg.substring(0,textseg.length() > 20? 20 : textseg.length()), lab,
                                        tag));
          }else{
            records.add(new ErrorRecord(filename, "Extra Content", textseg.substring(0,textseg.length() > 20? 20 : textseg.length()), lab,
                                        tag));
          }
        }else{
          /*if(textseg.indexOf("<") >= 0){
          textseg = textseg.replaceFirst("<[-\\w]+?>","");
            records.add(new ErrorRecord(filename, "Segmentation", wrong.substring(0, wrong.length() > 20? 20 : wrong.length()), lab, tag));
          }else{
            records.add(new ErrorRecord(filename, "Segmentation", wrong.substring(0, wrong.length() > 20? 20 : wrong.length()), lab, tag));
          }*/
        }
      }
      //reminding
      if(wrong.trim().compareTo("") != 0){
        p = Pattern.compile("\\s*<([-\\w]+?)>(.*)");
        m = p.matcher(wrong);
        if(m.matches()){
          String lab = m.group(1);
          String textseg = m.group(2);
          if(lab.compareTo(tag) == 0){
            //records.add(new ErrorRecord(filename, "Segmentation", textseg.substring(0, textseg.length() > 20? 20 : textseg.length()), lab, tag));
          }else{
            records.add(new ErrorRecord(filename, "Multiple Errors", textseg.substring(0, textseg.length() > 20? 20 : textseg.length()), lab, tag));
          }
        }
      }
    }
  }

  private String getTagBefore() {
    if(selectedText != null){
      String stext = selectedText.replaceAll(newline, "");
      String text = textPane.getText().replaceAll(newline, "");
      Pattern p = Pattern.compile("<([-\\w]+?)>[^<>]*?" + escape(stext));
      Matcher m = p.matcher(text);
      if (m.find()) {
        String tag = m.group(1);
        if (selectedText.indexOf("</" + tag + ">") >= 0 ||
            selectedText.indexOf("<") < 0) {
          return tag;
        }
        return null;
      }
    }
    return null;
  }

  private String getTagAfter() {
    if(selectedText != null){
      String stext = selectedText.replaceAll(newline, "");
      String text = textPane.getText().replaceAll(newline, "");

      Pattern p = Pattern.compile(escape(stext) + "[^<>]*?</([-\\w]+?)>");
      Matcher m = p.matcher(text);
      if (m.find()) {
        String tag = m.group(1);
        if (selectedText.indexOf("<" + tag + ">") >= 0 ||
            selectedText.indexOf("<") < 0) {
          return tag;
        }
        return null;
      }
    }
    return null;
  }

  public static String escape(String str) {
    /* () => /( /), [] => /[ /], {} => /{ /}, . ? * + - !=> /. /? /* /+ /- /!*/
    /* ^ $ no change */
    return str.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)")
        .replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]")
        .replaceAll("\\{", "\\\\{").replaceAll("\\}", "\\\\}")
        .replaceAll("\\.", "\\\\.").replaceAll("\\*", "\\\\*")
        .replaceAll("\\+", "\\\\+").replaceAll("\\?", "\\\\?")
        .replaceAll("\\-", "\\\\-").replaceAll("\\!", "\\\\!");
  }

  public void xmlPane_mousePressed(MouseEvent e) {
    start = textPane.getCaretPosition();
    if(textPane.isEditable()){
      dirty = true;
    }
  }

  public void xmlPane_mouseReleased(MouseEvent e) {
    if(textPane.isEditable()){
      dirty = true;
    }
    end = textPane.getCaretPosition();
    if(start == end){return;}
    if (start > end) {
      int temp = start;
      start = end;
      end = temp;
    }
    textPane.setSelectionStart(start);
    textPane.setSelectionEnd(end);
    selectedText = textPane.getSelectedText();
    selectedText = selectedText.replaceFirst("^\\s*", "").trim();
    frame.getStatus().setText(selectedText);
  }

  public void xmlPane_maybeShowPopup(MouseEvent e) {
    if (e.isPopupTrigger() && classPopup != null) {
      classPopup.show(e.getComponent(), e.getX(), e.getY());
    }
  }

  public void popupMenu_mousePressed(MouseEvent e) {
    String classname = ( (JMenu) e.getSource()).getText();
    frame.getStatus().setText("mouse pressed class: " + classname);
    if(textPane.isEditable()){
      editMarkup(classname);
    }
    classPopup.setVisible(false);
  }

  public void refreshButton_actionPerformed(ActionEvent e) {
    refreshTree();
    frame.getStatus().setText("refreshed the tree");
  }

  /*public void jMenuItemAddRecord_actionPerformed(ActionEvent e) {
    AddRecordDialog addrecord = new AddRecordDialog(this);
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension dialogSize = addrecord.getSize();
    if (dialogSize.height > screenSize.height) {
      dialogSize.height = screenSize.height;
    }
    if (dialogSize.width > screenSize.width) {
      dialogSize.width = screenSize.width;
    }
    addrecord.setLocation( (screenSize.width - dialogSize.width) / 2,
                          (screenSize.height - dialogSize.height) / 2);
    addrecord.setVisible(true);
  } copied to InterfaceFrame*/

  /**
   * @todo generate error statistics
   * @param e
   */
  /*public void jMenuItemShowStat_actionPerformed(ActionEvent e) {
    //process errorlist
    String message = compiledErrors();
    JOptionPane.showMessageDialog(frame, message);
  } copied to InterfaceFrame*/

  public String compiledErrors() {
    StringBuffer sb = new StringBuffer();
    Iterator it = (frame.getErrorList()).iterator();
    while (it.hasNext()) {
      ErrorRecord er = (ErrorRecord) it.next();
      sb.append(er.toString());
      sb.append(System.getProperty("line.separator"));
    }
    return sb.toString();
  }
}

///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////
class ListController_list_listSelectionListenerAdapter
    implements javax.swing.event.ListSelectionListener {
  ListController adaptee;

  ListController_list_listSelectionListenerAdapter(ListController adaptee) {
    this.adaptee = adaptee;
  }

  public void valueChanged(ListSelectionEvent e) {
    adaptee.valueChanged(e);
  }
}

class ListController_classPopup_actionAdapter
    implements ActionListener {
  ListController adaptee;

  ListController_classPopup_actionAdapter(ListController adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.popupMenu_actionPerformed(e);
  }

}

class ListController_classPopup_mouseAdapter
    extends java.awt.event.MouseAdapter {
  ListController adaptee;

  ListController_classPopup_mouseAdapter(ListController adaptee) {
    this.adaptee = adaptee;
  }

  public void mousePressed(MouseEvent e) {
    adaptee.popupMenu_mousePressed(e);
  }

}

class ListController_xmlPane_mouseAdapter
    extends java.awt.event.MouseAdapter {
  ListController adaptee;

  ListController_xmlPane_mouseAdapter(ListController adaptee) {
    this.adaptee = adaptee;
  }

  public void mousePressed(MouseEvent e) {
    if (e.getModifiers() != e.BUTTON1_MASK) { //if press left button
      adaptee.xmlPane_maybeShowPopup(e);
    }
    else {
      adaptee.xmlPane_mousePressed(e);
    }
  }

  public void mouseReleased(MouseEvent e) {
    if (e.isPopupTrigger()) {
      adaptee.xmlPane_maybeShowPopup(e);
    }
    else {
      adaptee.xmlPane_mouseReleased(e);
    }
  }

}

class ListController_refreshButton_actionAdapter
    implements ActionListener {
  ListController adaptee;

  ListController_refreshButton_actionAdapter(ListController adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.refreshButton_actionPerformed(e);
  }

}

/*class ListController_jMenuItemAddRecord_actionAdapter
    implements ActionListener {
  ListController adaptee;

  ListController_jMenuItemAddRecord_actionAdapter(ListController adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemAddRecord_actionPerformed(e);
  }

}*/

/*class ListController_jMenuItemShowStat_actionAdapter
    implements ActionListener {
  ListController adaptee;

  ListController_jMenuItemShowStat_actionAdapter(ListController adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemShowStat_actionPerformed(e);
  }

}*/

