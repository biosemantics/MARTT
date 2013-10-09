package marttinterface;

import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import visitor.VisitorDoMarkup;
import miner.SemanticLabel;
import java.util.Vector;
import learning.MarkedSegment;
import visitor.ElementComposite;
import visitor.Serializer;
import javax.swing.JTree;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.xml.sax.SAXException;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import knowledgebase.Composite;

/**
 * <p>Title: User Interface of MARTT </p>
 * <p>Description: Support training example annotation and marked-up example review.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */

public class Composer {
  /*public Composer() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
     }*/

  InterfaceFrame frame = null;
  JTextPane jtextpane = null;
  JPopupMenu classpopup = null;
  ListController listcontroller = null;
  static Composite kb = null;
  static ElementComposite ec = null;
  public Composer(InterfaceFrame frame, Composite kb, ElementComposite ec) {
    this.frame = frame;
    this.kb = kb;
    this.ec = ec;
  }

  /**
   * @todo: save, save as
   * @param e ActionEvent
   */
  void jMenuItemCompose_actionPerformed(ActionEvent e) {
    this.listcontroller = frame.getListController() == null ?
        new ListController(frame) : frame.getListController();
    if (frame.getListController() == null) {
    	frame.setListController(this.listcontroller);
    }
    jtextpane = listcontroller.textPane;
    if (listcontroller.treeScrollPane != null) { //clear tree view
      listcontroller.treeScrollPane.removeAll();
    }
    jtextpane.setText("");
    jtextpane.setEditable(true);
    jtextpane.grabFocus();
    frame.getSplitPane().setRightComponent(jtextpane);
    frame.getSplitPane().setDividerLocation(0);
    //listcontroller.jMenuToDoList.setEnabled(false);
    //frame.toolBar.setFrame(frame);
    //frame.toolBar.setListController(listcontroller);
   //frame.jToolBar = frame.toolBar.setDefaultToolBar(ToolBar.COMPOSE_MODE);
    //Remove menuBar?
    //frame.menuBar.setFrame(frame);
    //frame.menuBar.setListController(listcontroller);
    //frame.setJMenuBar(frame.menuBar.setDefaultMenuBar(MenuBar.COMPOSE_MODE));

    /*int count = frame.jToolBar.getComponentCount();
         for(int i = 3; i < count; i++){
      frame.jToolBar.remove(3);
         }*/
  }

  /**
   * @param e
   */
  public void jMenuItemMarkup_actionPerformed(ActionEvent e) {
    //lc creates textpane and edit, format, and error menu items.
    if (jtextpane == null) {
      return;
    }
    String text = jtextpane.getText();
    /*@todo: convert <>/& to xml entities */

    System.out.println("to be marked up: " + text);
    //mark up text
    Vector exp = new Vector();
    MarkedSegment temp = new MarkedSegment(text,
                                           new SemanticLabel("", "", "", 0f, 0f,
        ""));
    exp.add(temp);
    ec.accept(new VisitorDoMarkup(exp, "", kb, "" + 0 + "", "SCCP", "kbsc",
                                  "lrp0", "kblrp0"), "SCCP"); //mark up
    String markedup = ec.getMarked(0)[0]; ; //t is the index for taxon
    ec.resetMarkeds();
    ec.resetMarkedSegs();

    //String markedup = "<description><taxon><genus>Scrophularia</genus><specific-epithet>modesta</specific-epithet></taxon><plant-habit-and-life-style><phls-general>Herbs, to 60 cm tall.</phls-general></plant-habit-and-life-style><stems><stem-general>Stems densely glandular hairy, white pithed or sometimes hollow.</stem-general></stems><leaves><leaf-general>Leaves ovate, ovate-oblong, or oblong-lanceolate, to 9 X 5 cm, pubescent, base often asymmetrical and rounded, subtruncate, subcordate, or rarely broadly cuneate, margin variously toothed.</leaf-general></leaves><flowers><inflorescence-general>Thyrses terminal or sometimes on lateral branches, to 30 cm; </inflorescence-general><inflorescence-general>cymes widely spaced, 3-7-flowered; </inflorescence-general><peduncle>peduncle to 1.5 cm, glandular hairy;</peduncle> <bract>bracts small.</bract><pedicel>Pedicel usually 4(-10) mm, glandular hairy. </pedicel><calyx>Calyx ca. 4 mm, subglabrous; lobes ovate, apex obtuse.</calyx><corolla>Corolla green to yellow-green, ca. 8 mm; tube oblong-globose, ca. 4 mm; lower lip middle lobe slightly shorter than lateral lobes; upper lip ca. 1 mm longer than lower lip, lobes ovate, with overlapping margins.</corolla><stamen>Stamens slightly shorter than lower lip;</stamen> <staminode>staminode fanlike to transversely oblong, wider than long.</staminode><ovary>Ovary ca. 2.5 mm.</ovary><style>Style 3-4 mm.</style></flowers><fruits><fruit-general>Capsule ovoid, 5-9 mm.</fruit-general></fruits><phenology>Fl. May-Jul, fr. Jul-Sep.</phenology> </description>"
    //    ;
    listcontroller.style4XmlPane(markedup);
    listcontroller.displayText(markedup);
    //frame.toolBar.adjustToolBarEditButtons(true);
    frame.getSplitPane().setDividerLocation(0);

  }

  private void jbInit() throws Exception {
  }
}

class Composer_jMenuItemMarkup_actionAdapter
    implements ActionListener {
  Composer adaptee = null;
  Composer_jMenuItemMarkup_actionAdapter(Composer com) {
    adaptee = com;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemMarkup_actionPerformed(e);
  }
}

class Composer_jMenuItemCompose_actionAdapter
    implements java.awt.event.ActionListener {
  Composer adaptee;

  Composer_jMenuItemCompose_actionAdapter(Composer adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemCompose_actionPerformed(e);
  }
}
