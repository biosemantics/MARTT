package marttinterface;

import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
//import com.borland.jbcl.layout.*;
import java.awt.event.*;

/**
 * <p>Title: User Interface of MARTT </p>
 * <p>Description: Support training example annotation and marked-up example review.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */

public class OpenDialog
    extends JDialog {
  protected JPanel jPanelFile = new JPanel();
  protected JPanel jPanelFile1 = new JPanel();
  protected JPanel jPanelFile2 = new JPanel();
  protected JPanel jPanelFile4 = new JPanel();
  protected JLabel jLabelFile = new JLabel();
  protected JTextField jTextField = new JTextField();
  protected JButton jButtonBrowseFile = new JButton();
  protected JLabel jLabelDTD = new JLabel();
  protected JTextField jTextFieldDTD = new JTextField();
  protected JButton jButtonBrowseDTD = new JButton();
  protected JCheckBox jCheckBox1 = new JCheckBox();
  protected JPanel jPanelSaveInner = new JPanel();
  protected JPanel jPanel1 = new JPanel();
  protected JButton jButtonOK = new JButton();
  protected JFileChooser jFileChooser1 = new JFileChooser("C:\\Documents and Settings\\hongcui\\Desktop\\WorkFeb2008\\Projects");
  protected JLabel jLabelDepot = new JLabel();
  protected JTextField jTextFieldDepot = new JTextField();
  protected JButton jButtonDepotPath = new JButton();
  protected JLabel jCheckBoxDiffDepotOnly = new JLabel();
  protected JFileChooser jFileChooser2 = new JFileChooser("C:\\Documents and Settings\\hongcui\\Desktop\\WorkFeb2008\\Projects");
  protected Setting setting = null;

  public OpenDialog(InterfaceFrame frame) throws HeadlessException {
    try {
      jbInit(frame);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected void jbInit(InterfaceFrame frame) throws Exception {
    setting = frame.getSetting();
    init(frame);
  }

  protected void init(InterfaceFrame frame) throws Exception {
    this.setResizable(false);
    this.setTitle("Open Folder");
    this.setSize(400, 260);
    this.getContentPane().setSize(400, 260);
    jButtonOK.setText("OK");
    jButtonOK.setEnabled(false);
    jButtonOK.addActionListener(new OpenDialog_jButtonOK_actionAdapter(this,
        frame));
    jPanelFile.setBorder(null);
    jPanelFile.setBounds(new Rectangle(31, 8, 333, 28));
    jPanelFile1.setBorder(null);
    jPanelFile1.setBounds(new Rectangle(31, 44, 333, 26));
    jPanelFile2.setBorder(null);
    jPanelFile2.setBounds(new Rectangle(31, 70, 333, 20));
    jPanel1.setBounds(new Rectangle(31, 180, 333, 34));
    jPanelFile4.setBounds(new Rectangle(31, 98, 333, 87));
    jButtonBrowseFile.addActionListener(new
        OpenDialog_jButtonBrowseFile_actionAdapter(this));
    jButtonBrowseDTD.addActionListener(new
        OpenDialog_jButtonBrowseDTD_actionAdapter(this));
    jPanelFile1.add(jTextFieldDTD, null);
    jPanelFile1.add(jButtonBrowseDTD, null);
    jPanelFile1.add(jLabelDTD, null);
    jPanelSaveInner.add(jLabelDepot, null);
    jPanelSaveInner.add(jTextFieldDepot, null);
    jPanelSaveInner.add(jButtonDepotPath, null);
    jPanelSaveInner.add(jCheckBoxDiffDepotOnly, null);

    this.getContentPane().add(jPanelFile2, null);
    jPanelFile2.add(jCheckBox1, null);
    this.getContentPane().add(jPanelFile1, null);
    this.getContentPane().add(jPanel1, null);
    jPanel1.add(jButtonOK, null);
    this.getContentPane().add(jPanelFile4, null);
    jPanelFile4.add(jPanelSaveInner, null);
    this.getContentPane().add(jPanelFile, null);
    jPanelFile4.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
        createTitledBorder("What files do you want to use"),
        BorderFactory.createEmptyBorder(2, 2, 2, 2)));
    jPanelFile4.setLayout(null);
    jPanelFile2.setLayout(null);
    jPanelFile1.setLayout(null);
    this.getContentPane().setLayout(null);
    jPanelFile.setFont(new java.awt.Font("Dialog", 0, 9));
    jPanelFile.setForeground(Color.black);
    jPanelFile.setLayout(null);
    jLabelFile.setText("Folder");
    jLabelFile.setBounds(new Rectangle(9, 6, 58, 17));
    String path = setting.getFilePath();
    jTextField.setText(path==null? "" : path);
    jTextField.getDocument().addDocumentListener(new OpenDialog_jTextField_documentAdapter(this));
    jTextField.setBounds(new Rectangle(52, 4, 209, 21));
    java.net.URL url2 = OpenDialog.class.getResource(
        "toolbarButtonGraphics/general/Open.gif");
    if (url2 != null) {
      jButtonBrowseFile.setSelectedIcon(new ImageIcon());
    }
    else {
      jButtonBrowseFile.setText("Browse");
      jButtonBrowseFile.setBounds(new Rectangle(269, 3, 50, 20));
      jButtonBrowseFile.setFont(new java.awt.Font("Dialog", 0, 9));
      jButtonBrowseFile.setMargin(new Insets(2, 2, 2, 2));
    }

    jLabelDTD.setText("XML Schema");
    jLabelDTD.setBounds(new Rectangle(9, 6, 80, 17));
    jTextFieldDTD.setText("");
    jTextFieldDTD.getDocument().addDocumentListener(new OpenDialog_jTextFieldDTD_documentAdapter(this));
    jTextFieldDTD.setBounds(new Rectangle(85, 3, 176, 21));

    java.net.URL url1 = OpenDialog.class.getResource(
        "toolbarButtonGraphics/general/Open.gif");
    if (url1 != null) {
      jButtonBrowseDTD.setSelectedIcon(new ImageIcon());
    }
    else {
      jButtonBrowseDTD.setText("Browse");
      jButtonBrowseDTD.setBounds(new Rectangle(269, 3, 50, 20));
      jButtonBrowseDTD.setFont(new java.awt.Font("Dialog", 0, 9));
      jButtonBrowseDTD.setMargin(new Insets(2, 2, 2, 2));
    }
    jCheckBox1.setActionCommand("jCheckBoxYesToDoList");
    jCheckBox1.setText("Manage to-do list");
    jCheckBox1.setBounds(new Rectangle(9, 1, 132, 23));
    jCheckBox1.setSelected(true);//by default
    jPanelSaveInner.setBorder(null);
    jPanelSaveInner.setBounds(new Rectangle(10, 19, 310, 63));
    jPanelSaveInner.setLayout(null);

    jPanelFile.add(jTextField, null);
    jPanelFile.add(jLabelFile, null);
    jPanelFile.add(jButtonBrowseFile, null);
    jFileChooser1.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    //put two check boxes and a file chooser in jPanelModeInner
    jCheckBoxDiffDepotOnly.setBounds(4, 5, 300, 15);
    jCheckBoxDiffDepotOnly.setText(
        "Only open the files not in the depot");
    //jCheckBoxDiffDepotOnly.setSelected(true);

    jLabelDepot.setText("Depot");
    jLabelDepot.setBounds(new Rectangle(4, 26, 35, 15));
    jTextFieldDepot.setText("");
    jTextFieldDepot.setBounds(new Rectangle(43, 24, 195, 21));

    ImageIcon ii = new ImageIcon("C:/Documents and Settings/hongcui/Desktop/iConference/MARTTInterfaceWorkspace/MARTTInterface/src/marttinterface/cup.gif");
 //       "toolbarButtonGraphics/general/Open.gif");
    if (ii != null) {
      jButtonDepotPath.setIcon(ii);
      jButtonDepotPath.setLocation(243, 24);
    }
    else {
      jButtonDepotPath.setText("Browse");
      jButtonDepotPath.setBounds(new Rectangle(243, 24, 50, 20));
      jButtonDepotPath.setFont(new java.awt.Font("Dialog", 0, 9));
      jButtonDepotPath.setMargin(new Insets(2, 2, 2, 2));
    }
    jButtonDepotPath.addActionListener(new
        SaveToDepotDialog_jButtonDepotPath_actionAdapter(this));

    jFileChooser2.setBounds(new Rectangle(0, 0, 464, 245));
    jFileChooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    jFileChooser2.setCurrentDirectory(new File("C:\\Documents and Settings\\hongcui\\Desktop\\iConference"));

  }

  protected void jTextField_update(DocumentEvent e) {
    if (jTextFieldDTD.getText().trim().compareTo("") != 0 &&
        jTextField.getText().trim().compareTo("") != 0) {
      jButtonOK.setEnabled(true);
    }else{
      jButtonOK.setEnabled(false);
    }
  }

  protected void jButtonBrowseFile_actionPerformed(ActionEvent e) {
    if (jFileChooser1.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String path = jFileChooser1.getSelectedFile().getPath();
      jTextField.setText(path);
      setting.setFilePath(path);
      if (jTextFieldDTD.getText().trim().compareTo("") != 0 &&
        jTextField.getText().trim().compareTo("") != 0) {
        jButtonOK.setEnabled(true);
      }else{
      jButtonOK.setEnabled(false);
    }
    }
  }

  protected void jTextFieldDTD_update(DocumentEvent e) {
    if (jTextFieldDTD.getText().trim().compareTo("") != 0 &&
        jTextField.getText().trim().compareTo("") != 0) {
      jButtonOK.setEnabled(true);
    }else{
      jButtonOK.setEnabled(false);
    }

  }

  protected void jButtonBrowseDTD_actionPerformed(ActionEvent e) {
    if (jFileChooser1.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String schemapath = jFileChooser1.getSelectedFile().getPath();
      jTextFieldDTD.setText(schemapath);
      setting.setSchemaPath(schemapath);
      if (jTextField.getText().trim().compareTo("") != 0 &&
        jTextFieldDTD.getText().trim().compareTo("") != 0) {
        jButtonOK.setEnabled(true);
      }else{
      jButtonOK.setEnabled(false);
    }


    }

  }


  protected void jButtonDepotPath_actionPerformed(ActionEvent e) {
    if (jFileChooser2.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String depotpath = jFileChooser2.getSelectedFile().getPath();
      jTextFieldDepot.setText(depotpath);
      setting.setDepotPath(depotpath);
    }
  }

}

class OpenDialog_jButtonOK_actionAdapter
    implements java.awt.event.ActionListener {
  OpenDialog target;
  InterfaceFrame adaptee;

  OpenDialog_jButtonOK_actionAdapter(OpenDialog dialog,
                                     InterfaceFrame adaptee) {
    this.adaptee = adaptee;
    this.target = dialog;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.openDialog_jButtonOK_actionPerformed(target, e);
  }

}

class OpenDialog_jButtonBrowseFile_actionAdapter
    implements java.awt.event.ActionListener {
  OpenDialog adaptee;

  OpenDialog_jButtonBrowseFile_actionAdapter(OpenDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonBrowseFile_actionPerformed(e);
  }
}

class OpenDialog_jButtonBrowseDTD_actionAdapter
    implements java.awt.event.ActionListener {
  OpenDialog adaptee;

  OpenDialog_jButtonBrowseDTD_actionAdapter(OpenDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonBrowseDTD_actionPerformed(e);
  }
}


class OpenDialog_jTextFieldDTD_documentAdapter
    implements DocumentListener {
  OpenDialog adaptee;

  OpenDialog_jTextFieldDTD_documentAdapter(OpenDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void insertUpdate(DocumentEvent e) {
    adaptee.jTextFieldDTD_update(e);
  }

  public void removeUpdate(DocumentEvent e) {
  adaptee.jTextFieldDTD_update(e);
}

public void changedUpdate(DocumentEvent e) {
  adaptee.jTextFieldDTD_update(e);
}


}

class OpenDialog_jTextField_documentAdapter
    implements javax.swing.event.DocumentListener {
  OpenDialog adaptee;

  OpenDialog_jTextField_documentAdapter(OpenDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void insertUpdate(DocumentEvent e) {
    adaptee.jTextField_update(e);
  }

  public void removeUpdate(DocumentEvent e) {
  adaptee.jTextField_update(e);
}

public void changedUpdate(DocumentEvent e) {
  adaptee.jTextField_update(e);
}

}

class SaveToDepotDialog_jButtonDepotPath_actionAdapter
    implements java.awt.event.ActionListener {
  OpenDialog adaptee;

  SaveToDepotDialog_jButtonDepotPath_actionAdapter(OpenDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonDepotPath_actionPerformed(e);
  }
}
