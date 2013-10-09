package marttinterface;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

/**
 * <p>Title: User Interface of MARTT </p>
 *
 * <p>Description: Support training example annotation and marked-up example
 * review.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: hong cui</p>
 *
 * @author hong cui
 * @version 1.0
 */
public class MenuBar {
  JMenuBar jMenuBar = null;

  JMenu jMenuFile = new JMenu("File");
  //JMenuItem jMenuItemNew = new JMenuItem("New");
   JMenuItem jMenuItemOpenFile = new JMenuItem("Open file");
   JMenuItem jMenuItemSave = new JMenuItem("Save");
   JMenuItem jMenuItemSaveAs = new JMenuItem("Save as");
   JMenuItem jMenuItemSave2Depot = new JMenuItem("Save to depot");
   JMenuItem jMenuItemExit = new JMenuItem("Exit");

   JMenu jMenuEdit = null;
   JMenu jMenuFormat = null;

   JMenu jMenuCompose = new JMenu("Compose");
   JMenuItem jMenuItemCompose = new JMenuItem("Compose");
   JMenuItem jMenuItemMarkup = new JMenuItem("Mark up");

   JMenu jMenuToDoList = new JMenu("List");
   JMenuItem jMenuItemPurgeList = new JMenuItem("Purge list");
   JMenuItem jMenuItemShowList = new JMenuItem("Show list");
   JMenuItem jMenuItemImportList = new JMenuItem("Import list");
   JMenuItem jMenuItemRemoveListFile = new JMenuItem("Remove list file");

   JMenu jMenuError = new JMenu("Error Record");
   JMenuItem jMenuItemAddRecord = new JMenuItem("Add record");
   JMenuItem jMenuItemShowStat = new JMenuItem("Show records");

   JMenu jMenuHelp = new JMenu("Help");
   JMenuItem jMenuHelpAbout = new JMenuItem("About");

   InterfaceFrame frame = null;
   ListController listcontroller = null;
  static int ANNOTE_MODE = 0;
  static int COMPOSE_MODE = 1;
  private static boolean frameReg = false;
  private static boolean controllerReg = false;

  public MenuBar() {
      //file
  jMenuItemOpenFile.setActionCommand("Open File");
  //jMenuItemOpenFile.setSelected(false);
  //jMenuItemSave.setEnabled(false);
  //jMenuItemSaveAs.setEnabled(false);
  //jMenuItemSave2Depot.setEnabled(false);
  jMenuFile.add(jMenuItemOpenFile);
  jMenuFile.addSeparator();
  jMenuFile.add(jMenuItemSave);
  jMenuFile.add(jMenuItemSaveAs);
  jMenuFile.add(jMenuItemSave2Depot);
  jMenuFile.addSeparator();
  jMenuFile.add(jMenuItemExit);
  //compose
  jMenuCompose.add(jMenuItemCompose);
  jMenuCompose.add(jMenuItemMarkup);
  //list
  //jMenuItemPurgeList.setEnabled(false);
  //jMenuItemShowList.setEnabled(false);
  //jMenuItemImportList.setEnabled(true);
  jMenuItemImportList.setActionCommand("Review");
  jMenuItemRemoveListFile.setActionCommand("RemoveListFile");
  jMenuToDoList.add(jMenuItemRemoveListFile);
  jMenuToDoList.addSeparator();
  jMenuToDoList.add(jMenuItemPurgeList);
  jMenuToDoList.add(jMenuItemShowList);
  jMenuToDoList.addSeparator();
  jMenuToDoList.add(jMenuItemImportList);
  //help
  jMenuHelp.add(jMenuHelpAbout);
  //edit and format
  //error record
  jMenuError.add(jMenuItemAddRecord);
  jMenuError.add(jMenuItemShowStat);

  }

  public void setFrame(InterfaceFrame frame) {
    this.frame = frame;
    createMenu();
  }

  public void setListController(ListController listcontroller) {
    this.listcontroller = listcontroller;
    createMenu();
  }

  private void createMenu() {
    if (frame != null && !frameReg) {
      registerFrameListeners();
      frameReg = true;
    }
    if (listcontroller != null && !controllerReg) {
      registerListControllerListeners();
      controllerReg = true;
    }
  }

  private void registerFrameListeners() {
    jMenuItemImportList.addActionListener(new
                                          InterfaceFrame_jMenuItemImportList_actionAdapter(
        frame));

    jMenuItemRemoveListFile.addActionListener(new
                                              InterfaceFrame_jMenuItemRemoveListFile_actionAdapter(
        frame));
    jMenuItemShowList.addActionListener(new
                                        InterfaceFrame_jMenuItemShowList_actionAdapter(
        frame));
    jMenuItemPurgeList.addActionListener(new
                                         InterfaceFrame_jMenuItemPurgeList_actionAdapter(
        frame));

    jMenuItemCompose.addActionListener(new
                                       Composer_jMenuItemCompose_actionAdapter(
        frame.getComposer()));
    jMenuItemMarkup.addActionListener(new
                                      Composer_jMenuItemMarkup_actionAdapter(
        frame.getComposer()));

    jMenuItemSave.addActionListener(new
                                    InterfaceFrame_jMenuItemSave_actionAdapter(
        frame));
    jMenuItemSaveAs.addActionListener(new
                                      InterfaceFrame_jMenuItemSaveAs_actionAdapter(frame));
    jMenuItemSave2Depot.addActionListener(new
                                          InterfaceFrame_jMenuItemSave2Depot_actionAdapter(
        frame));

    jMenuItemOpenFile.addActionListener(new
                                        InterfaceFrame_jMenuItemOpenFile_actionAdapter(
        frame));

    jMenuItemExit.addActionListener(new
                                    InterfaceFrame_jMenuFileExit_ActionAdapter(
        frame));

    jMenuHelpAbout.addActionListener(new
                                     InterfaceFrame_jMenuHelpAbout_ActionAdapter(
        frame));

  }

  private void registerListControllerListeners() {
    TextPaneController tpc = new TextPaneController(listcontroller.frame,
        listcontroller.textPane);
    jMenuEdit = tpc.createEditMenu();
    jMenuFormat = tpc.createFormatMenu();

    /*jMenuItemAddRecord.addActionListener(new
                                         ListController_jMenuItemAddRecord_actionAdapter(
                                             listcontroller));
    jMenuItemShowStat.addActionListener(new
                                        ListController_jMenuItemShowStat_actionAdapter(
                                            listcontroller));*/

  }

  public JMenuBar setDefaultMenuBar(int mode) {
    jMenuBar = new JMenuBar();
    if (jMenuFile.getItemCount() != 0) {
      jMenuBar.add(jMenuFile);
    }
    if (jMenuEdit != null && jMenuEdit.getItemCount() != 0) {
      jMenuBar.add(jMenuEdit);
    }
    if (jMenuFormat != null && jMenuFormat.getItemCount() != 0) {
      jMenuBar.add(jMenuFormat);
    }
    if (jMenuToDoList.getItemCount() != 0) {
      jMenuBar.add(jMenuToDoList);
    }
    if (jMenuCompose.getItemCount() != 0) {
      jMenuBar.add(jMenuCompose);
    }
    if (jMenuError.getItemCount() != 0) {
      jMenuBar.add(jMenuError);
    }
    if (jMenuHelp.getItemCount() != 0) {
      jMenuBar.add(jMenuHelp);
    }
    if (mode == 0) { //annotation

    }
    else { //compose

    }
    return jMenuBar;

  }

}
