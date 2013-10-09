package marttinterface;

import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Dimension;

/**
 * <p>Title: User Interface of MARTT </p>
 *
 * <p>Description: Support training example annotation and marked-up example
 * review. This class is in charge of the toolbar on the interface </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: hong cui</p>
 *
 * @author hong cui
 * @version 1.0
 */
public class ToolBar {
  InterfaceFrame frame = null;
  ListController listcontroller = null;
  JToolBar jToolBar = new JToolBar();
  JButton jButtonOpen = new JButton();
  JButton jButtonSaveToDepot = new JButton();
  JButton jButtonHelp = new JButton();
  JButton jButtonPurgeList = new JButton();
  JButton jButtonCompose = new JButton();
  JButton jButtonMarkup = new JButton();
  JLabel editableLabel = new JLabel();
  JLabel uneditableLabel = new JLabel();
  JButton refreshButton = new JButton();
  private String status = null;
  static int ANNOTE_MODE = 0; //0: annotation 1: compose
  static int COMPOSE_MODE = 1;
  private static boolean frameReg = false;
  private static boolean controllerReg = false;

  /**
   * tool bar components are created here, but the controls are distributed to
   * appropriate classes
   */

  public ToolBar() {
    //open
    java.net.URL url = InterfaceFrame.class.getResource(
        "toolbarButtonGraphics/general/Open24.gif");
    if (url != null) {
      jButtonOpen.setIcon(new ImageIcon(url));
      jButtonOpen.setToolTipText("Open file");
    }
    else {
      jButtonOpen.setText("Open");
    }

    //save to depot
    url = InterfaceFrame.class.getResource(
        "toolbarButtonGraphics/general/Save24.gif");
    if (url != null) {
      jButtonSaveToDepot.setIcon(new ImageIcon(url));
      jButtonSaveToDepot.setToolTipText("Save file to the depot");
    }
    else {
      jButtonSaveToDepot.setText("Save to Depot");
    }
    //help
    url = InterfaceFrame.class.getResource(
        "toolbarButtonGraphics/general/Help24.gif");
    if (url != null) {
      jButtonHelp.setIcon(new ImageIcon(url));
      jButtonHelp.setToolTipText("Help");
    }
    else {
      jButtonHelp.setText("Help");
    }
    //purge
    url = InterfaceFrame.class.getResource(
        "toolbarButtonGraphics/general/Purge24.gif");
    if (url != null) {
      jButtonPurgeList.setIcon(new ImageIcon(url));
      jButtonPurgeList.setToolTipText("Purge List");
    }
    else {
      jButtonPurgeList.setText("Purge List");
    }

    //compose
    url = InterfaceFrame.class.getResource(
        "toolbarButtonGraphics/general/Compose24.gif");
    if (url != null) {
      jButtonCompose.setIcon(new ImageIcon(url));
      jButtonCompose.setToolTipText("Compose");
    }
    else {
      jButtonCompose.setText("Compose");
    }

    //Markup
    url = InterfaceFrame.class.getResource(
        "toolbarButtonGraphics/general/Markup24.gif");
    if (url != null) {
      jButtonMarkup.setIcon(new ImageIcon(url));
      jButtonMarkup.setToolTipText("Mark up");
    }
    else {
      jButtonMarkup.setText("Mark up");
    }

//editable
    url = InterfaceFrame.class.getResource(
        "toolbarButtonGraphics/general/Edit24.gif");
    if (url != null) {
      editableLabel.setIcon(new ImageIcon(url));
      editableLabel.setToolTipText("Editable");
    }
    else {
      editableLabel.setText("Editable");
    }
//uneditable
    url = InterfaceFrame.class.getResource(
        "toolbarButtonGraphics/general/Stop24.gif");
    if (url != null) {
      uneditableLabel.setIcon(new ImageIcon(url));
      uneditableLabel.setToolTipText("Uneditable");
    }
    else {
      uneditableLabel.setText("Uneditable");
    }

    //refresh tree
    url = InterfaceFrame.class.getResource(
        "toolbarButtonGraphics/general/Refresh24.gif");
    if (url != null) {
      refreshButton.setIcon(new ImageIcon(url));
      refreshButton.setToolTipText("Refresh");
    }
    else {
      refreshButton.setText("Refresh");
    }

  }

  public void setListController(ListController listcontroller) {
    this.listcontroller = listcontroller;
    createButtons();
  }

  public void setFrame(InterfaceFrame frame) {
    this.frame = frame;
    createButtons();
  }

  public JToolBar setDefaultToolBar(int mode) {
    jToolBar.removeAll();
    if (mode == 0) {
      jToolBar.add(jButtonOpen);
      jToolBar.add(jButtonPurgeList);
    }
    else {
      jToolBar.add(jButtonCompose);
      jToolBar.add(jButtonMarkup);
    }
    jToolBar.add(jButtonSaveToDepot);
    jToolBar.add(jButtonHelp);
    status = "default";
    return jToolBar;
  }

  private void createButtons() {
    if (frame != null && !frameReg) {
      jButtonOpen.addActionListener(new
                                    InterfaceFrame_jMenuItemOpenFile_actionAdapter(
                                        frame));

      jButtonSaveToDepot.addActionListener(new
    		  InterfaceFrame_jMenuItemSave2Depot_actionAdapter(
                                               frame)); ;

      jButtonHelp.addActionListener(new
                                    InterfaceFrame_jMenuHelpAbout_ActionAdapter(
                                        frame));

      jButtonPurgeList.addActionListener(new
                                         InterfaceFrame_jMenuItemPurgeList_actionAdapter(
                                             frame));

      jButtonCompose.addActionListener(new
                                       Composer_jMenuItemCompose_actionAdapter(
                                           frame.getComposer()));
      jButtonMarkup.addActionListener(new
                                      Composer_jMenuItemMarkup_actionAdapter(
                                          frame.getComposer()));
      frameReg = true;
    }
    if (listcontroller != null && !controllerReg) {
      refreshButton.addActionListener(new
                                      ListController_refreshButton_actionAdapter(
                                          listcontroller));
      controllerReg = true;
    }

  }

  public void adjustToolBarEditButtons(boolean editable) {
    jToolBar.repaint(); //make any previous changes take effects
    int editableindex = jToolBar.getComponentIndex(editableLabel);
    int uneditableindex = jToolBar.getComponentIndex(
        uneditableLabel); //current status
    if (editable) {
      //textPane.setEditable(true);
      //frame.jMenuBar1.getComponent(1).setEnabled(true); //enable edit menu
      if (uneditableindex < 0 && editableindex < 0) {
        jToolBar.add(refreshButton);
        jToolBar.addSeparator(new Dimension(10, 34));
        jToolBar.add(editableLabel);
        jToolBar.repaint();
      }
      else if (uneditableindex >= 0 &&
               jToolBar.getComponentAtIndex(uneditableindex - 1) instanceof
               JToolBar.Separator) {
        jToolBar.remove(uneditableLabel);
        jToolBar.add(refreshButton, uneditableindex - 1);
        jToolBar.add(editableLabel, uneditableindex + 1);
        jToolBar.repaint();
      }
    }
    else {
      //textPane.setEditable(false);
      //frame.jMenuBar1.getComponent(1).setEnabled(false); //disable edit menu
      if (uneditableindex < 0 && editableindex < 0) {
        jToolBar.addSeparator(new Dimension(10, 34));
        jToolBar.add(uneditableLabel);
        jToolBar.repaint();
      }
      else if (editableindex >= 0 &&
               jToolBar.getComponentAtIndex(editableindex - 1) instanceof
               JToolBar.Separator) { //change from editable to uneditable
        jToolBar.remove(refreshButton);
        jToolBar.remove(editableLabel);
        jToolBar.remove(editableindex - 2); //remove the seperator
        //for some reason the following two lines have no effect on the toolbar
        jToolBar.addSeparator(new Dimension(10, 34));
        jToolBar.add(uneditableLabel, editableindex - 1);
      }
    }
    status = "editability";
  }

  public void addPurgeListButton() {
    if (status.compareTo("default") == 0) {
      jToolBar.add(jButtonPurgeList);
    }
    else if (status.compareTo("default") == 0) {

    }

  }
}
