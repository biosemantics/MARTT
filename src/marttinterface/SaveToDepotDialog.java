package marttinterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.io.IOException;
import java.io.FileWriter;
import java.text.DateFormat;


/**
 * <p>Title: User Interface of MARTT </p>
 * <p>Description: Support training example annotation and marked-up example review.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */

public class SaveToDepotDialog
    extends JDialog {
  JLabel jLabelDepot = new JLabel();
  JTextField jTextFieldDepot = new JTextField();
  JButton jButtonOpenFile = new JButton();
  JButton jButtonSaveToDepot = new JButton();
  JFileChooser jFileChooser1 = new JFileChooser();
  JCheckBox jCheckBox2DepotOnly = new JCheckBox();
  InterfaceFrame frame = null;
  ListController listcontroller = null;

  public SaveToDepotDialog(InterfaceFrame frame, ListController listcontroller) throws HeadlessException {
    try {
      jbInit(frame, listcontroller);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit(InterfaceFrame frame, ListController listcontroller) throws Exception {
    this.frame = frame;
    this.listcontroller = listcontroller;
    jLabelDepot.setText("Location of Training Depot");
    jLabelDepot.setBounds(new Rectangle(19, 17, 170, 15));
    this.setSize(350, 140);
    this.setResizable(false);
    this.setTitle("Save to training example depot");
    this.getContentPane().setLayout(null);
    if (frame.getSetting().getDepotPath() == null) {
      jTextFieldDepot.setText("");
    }
    else {
      jTextFieldDepot.setText(frame.getSetting().getDepotPath());
    }
    jTextFieldDepot.setBounds(new Rectangle(19, 35, 259, 21));

    java.net.URL url = OpenDialog.class.getResource(
        "toolbarButtonGraphics/general/Open.gif");
    if (url != null) {
      jButtonOpenFile.setSelectedIcon(new ImageIcon());
    }
    else {
      jButtonOpenFile.setText("Browse");
      jButtonOpenFile.setBounds(new Rectangle(292, 35, 50, 20));
      jButtonOpenFile.setFont(new java.awt.Font("Dialog", 0, 9));
      jButtonOpenFile.setMargin(new Insets(2, 2, 2, 2));
    }
    jButtonOpenFile.addActionListener(new
        SaveToDepotDialog_jButtonOpenFile_actionAdapter(this));

    jCheckBox2DepotOnly.setBounds(15, 65, 200, 20);
    jCheckBox2DepotOnly.setText("Do not over-write the original");
    //jCheckBox2DepotOnly.setSelected(true);

    jButtonSaveToDepot.setBounds(new Rectangle(122, 87, 107, 25));
    jButtonSaveToDepot.setText("Save to Depot");
    jButtonSaveToDepot.addActionListener(new
        SaveToDepotDialog_jButtonSaveToDepot_actionAdapter(this));
    jFileChooser1.setBounds(new Rectangle(0, 0, 464, 245));
    jFileChooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    this.getContentPane().add(jLabelDepot, null);
    this.getContentPane().add(jTextFieldDepot, null);
    this.getContentPane().add(jButtonOpenFile, null);
    this.getContentPane().add(jCheckBox2DepotOnly, null);
    this.getContentPane().add(jButtonSaveToDepot, null);
  }

  void jButtonOpenFile_actionPerformed(ActionEvent e) {
    if (jFileChooser1.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String depotpath = jFileChooser1.getSelectedFile().getPath();
      jTextFieldDepot.setText(depotpath);
      frame.getSetting().setDepotPath(depotpath);
    }
  }

  void jButtonSaveToDepot_actionPerformed(ActionEvent e) {
    saveToDepot();
  }

  boolean saveToDepot(){
    int index = listcontroller.list.getSelectedIndex();
    if (index >= 0) {
      File file = ( (MyFile) frame.getSetting().getTodoList().get(index)).
          getFile();
      String filename = file.getName();
      File fpath = new File(frame.getSetting().getDepotPath(), filename);
      //listcontroller.refreshTree();
      //String content = DomToTreeModelAdapter.xmlout + listcontroller.dom2tree.document.getDocumentElement().toString();
      boolean saved = frame.saveSelectedFile(fpath);
      if (!saved) {
        JOptionPane.showMessageDialog(this,
                                      "Failed to save to the Depot [ " +
                                      fpath.getPath() + " ]");
        return false;
      }
      /**
       * @todo: over-write the original
       */
       /*if(!jCheckBox2DepotOnly.isSelected()){
        saved = frame.saveSelectedFile(file);
        if(!saved){
        JOptionPane.showMessageDialog(this,
                                      "Failed to save to file [ " +
                                      file.getPath() + " ]");

         return false;
         }
        }*/

      if (!jCheckBox2DepotOnly.isSelected() &&
          !saved) {
        JOptionPane.showMessageDialog(this,
                                      "Failed to save to file [ " +
                                      file.getPath() + " ]");
        return false;
      }

    }
    this.setVisible(false);
    return true;

  }
}

class SaveToDepotDialog_jButtonOpenFile_actionAdapter
    implements java.awt.event.ActionListener {
  SaveToDepotDialog adaptee;

  SaveToDepotDialog_jButtonOpenFile_actionAdapter(SaveToDepotDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonOpenFile_actionPerformed(e);
  }
}

class SaveToDepotDialog_jButtonSaveToDepot_actionAdapter
    implements java.awt.event.ActionListener {
  SaveToDepotDialog adaptee;

  SaveToDepotDialog_jButtonSaveToDepot_actionAdapter(SaveToDepotDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonSaveToDepot_actionPerformed(e);
  }
}
