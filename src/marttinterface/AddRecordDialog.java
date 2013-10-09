package marttinterface;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.text.*;

/**
 * <p>Title: User Interface of MARTT </p>
 * <p>Description: Support training example annotation and marked-up example review.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */

public class AddRecordDialog extends JDialog {
  JPanel jPanel1 = new JPanel();
  TitledBorder titledBorder1;
  JLabel jLabel1 = new JLabel();
  JTextField jTextFieldFileName = new JTextField();
  JLabel jLabel2 = new JLabel();
  JComboBox jComboBoxErrorType = new JComboBox(ErrorRecord.errortypes);
  JLabel jLabel3 = new JLabel();
  JTextField jTextFieldCause = new JTextField();
  JLabel jLabel4 = new JLabel();
  JTextField jTextFieldFPClass = new JTextField();
  JLabel jLabel5 = new JLabel();
  JTextField jTextFieldFNClass = new JTextField();
  JButton jButtonAdd = new JButton();
  JButton jButtonCancel = new JButton();
  ListController listcontroller;

  public AddRecordDialog(ListController listcontroller) throws HeadlessException {
    try {
      jbInit(listcontroller);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit(ListController listcontroller) throws Exception {
    this.setModal(true);
    this.listcontroller = listcontroller;
    this.setTitle("Add a new error record");
    this.setSize(new Dimension(400, 230));
    jLabel1.setText("File involved");
    jLabel1.setBounds(new Rectangle(13, 26, 71, 15));
    jPanel1.setOpaque(true);
    jPanel1.setBounds(new Rectangle(0, 0, 400, 210));
    jPanel1.setLayout(null);
    this.getContentPane().setLayout(null);
    try{
      File fpath = ( (MyFile) listcontroller.frame.getSetting().getTodoList().get(
          listcontroller.frame.getDisplayed())).
          getFile();
      jTextFieldFileName.setText(fpath.getName());
    }catch(NullPointerException e){
      jTextFieldFileName.setText("");
    }
    jTextFieldFileName.setBounds(new Rectangle(90, 24, 292, 21));
    jLabel2.setText("Error type");
    jLabel2.setBounds(new Rectangle(13, 57, 69, 15));
    //Vector items = new Vector(Arrays.asList(ErrorRecord.errortypes));
    //items.add(0, "Select Type");
    //jComboBoxErrorType = new JComboBox(items);
    jComboBoxErrorType.setBounds(new Rectangle(90, 55, 180, 21));
    jLabel3.setText("Possible causes");
    jLabel3.setBounds(new Rectangle(13, 92, 92, 15));
    jTextFieldCause.setText("");
    jTextFieldCause.setBounds(new Rectangle(114, 89, 267, 21));
    jLabel4.setText("False positive class");
    jLabel4.setBounds(new Rectangle(12, 128, 109, 15));
    jTextFieldFPClass.setText("");
    jTextFieldFPClass.setBounds(new Rectangle(127, 125, 104, 21));
    jLabel5.setText("False negative class");
    jLabel5.setBounds(new Rectangle(12, 162, 121, 15));
    jTextFieldFNClass.setText("");
    jTextFieldFNClass.setBounds(new Rectangle(127, 159, 104, 21));
    jButtonAdd.setBounds(new Rectangle(287, 130, 85, 25));
    jButtonAdd.setText(" Add ");
    jButtonAdd.addActionListener(new AddRecordDialog_jButtonAdd_actionAdapter(this));
    jButtonCancel.setBounds(new Rectangle(287, 157, 85, 25));
    jButtonCancel.setText("Cancel");
    jButtonCancel.addActionListener(new AddRecordDialog_jButtonCancel_actionAdapter(this));

    this.getContentPane().add(jPanel1, null);
    jPanel1.add(jTextFieldFileName, null);
    jPanel1.add(jLabel4, null);
    jPanel1.add(jLabel5, null);
    jPanel1.add(jTextFieldFNClass, null);
    jPanel1.add(jTextFieldFPClass, null);
    jPanel1.add(jLabel3, null);
    jPanel1.add(jTextFieldCause, null);
    jPanel1.add(jComboBoxErrorType, null);
    jPanel1.add(jLabel2, null);
    jPanel1.add(jLabel1, null);
    jPanel1.add(jButtonAdd, null);
    jPanel1.add(jButtonCancel, null);
  }

  void jComboBoxErrorType_actionPerformed(ActionEvent e) {
  }

  /**
   * add a record to the error list in interfaceframe
   * also write the record to disk
   * so that the interfaceframe keeps a complete list of newly created error records
   * but each record is write to the disk immediately after it is added.
   * @param e
   */
  void jButtonAdd_actionPerformed(ActionEvent e) {
    String file = jTextFieldFileName.getText();
    String type = (String)jComboBoxErrorType.getSelectedItem();
    String cause = jTextFieldCause.getText();
    String fpclass = jTextFieldFPClass.getText();
    String fnclass = jTextFieldFNClass.getText();
    ErrorRecord er = new ErrorRecord(file, type, cause, fpclass, fnclass);
    listcontroller.frame.getErrorList().add(er);
    saveErrorRecord(er);
    this.setVisible(false);
  }

  void jButtonCancel_actionPerformed(ActionEvent e) {
      this.setVisible(false);
    }


  private void saveErrorRecord(ErrorRecord er){
  if (listcontroller != null) {
    try {
      File error = new File(System.getProperty("user.dir") + "/" +
                            ErrorRecord.errorfile);
      FileWriter fw = new FileWriter(error, true); //append
      Calendar now = Calendar.getInstance();
      fw.write(System.getProperty("line.separator"));
      fw.write(System.getProperty("line.separator"));
      fw.write(DateFormat.getDateInstance().format(now.getTime()));
      fw.write(System.getProperty("line.separator"));
      fw.write(listcontroller.frame.getSetting().getFilePath());
      fw.write(System.getProperty("line.separator"));
      fw.write(er.toString());
      fw.flush();
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
}

}


}

class AddRecordDialog_jButtonCancel_actionAdapter implements java.awt.event.ActionListener {
  AddRecordDialog adaptee;

  AddRecordDialog_jButtonCancel_actionAdapter(AddRecordDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonCancel_actionPerformed(e);
  }
}

class AddRecordDialog_jButtonAdd_actionAdapter implements java.awt.event.ActionListener {
  AddRecordDialog adaptee;

  AddRecordDialog_jButtonAdd_actionAdapter(AddRecordDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonAdd_actionPerformed(e);
  }
}
