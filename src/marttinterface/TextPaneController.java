package marttinterface;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

/**
 * <p>Title: User Interface of MARTT </p>
 * <p>Description: Support training example annotation and marked-up example review.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */

public class TextPaneController {
  InterfaceFrame frame;
  JTextPane textPane;
  AbstractDocument doc;
  String newline = "\n";
  HashMap actions;

  //undo helpers
  protected UndoAction undoAction;
  protected RedoAction redoAction;
  protected UndoManager undo = new UndoManager();

  public TextPaneController(InterfaceFrame frame, JTextPane textPane) {
    this.frame = frame;
    this.textPane = textPane;

    //Set up the menu bar.
    createActionTable(textPane);

    //Add some key bindings.
    addBindings();

    //Start watching for undoable edits and caret changes.
    doc = (AbstractDocument)textPane.getStyledDocument();
    doc.addUndoableEditListener(new MyUndoableEditListener());
  }

  //This listens for and reports caret movements.

  //This one listens for edits that can be undone.
  protected class MyUndoableEditListener
      implements UndoableEditListener {
    public void undoableEditHappened(UndoableEditEvent e) {
      //Remember the edit and update the menus.
      UndoableEdit ue = e.getEdit();
      undo.addEdit(ue);
      undoAction.updateUndoState();
      redoAction.updateRedoState();
    }
  }

  //And this one listens for any changes to the document.

  //Add a couple of emacs key bindings for navigation.
  protected void addBindings() {
    InputMap inputMap = textPane.getInputMap();

    //Ctrl-b to go backward one character
    KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
    inputMap.put(key, DefaultEditorKit.backwardAction);

    //Ctrl-f to go forward one character
    key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);
    inputMap.put(key, DefaultEditorKit.forwardAction);

    //Ctrl-p to go up one line
    key = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK);
    inputMap.put(key, DefaultEditorKit.upAction);

    //Ctrl-n to go down one line
    key = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
    inputMap.put(key, DefaultEditorKit.downAction);
  }

  //Create the edit menu.
  public JMenu createEditMenu() {
    JMenu menu = new JMenu("Edit");

    //Undo and redo are actions of our own creation.
    undoAction = new UndoAction();
    menu.add(undoAction);

    redoAction = new RedoAction();
    menu.add(redoAction);

    menu.addSeparator();

    //These actions come from the default editor kit.
    //Get the ones we want and stick them in the menu.
    menu.add(getActionByName(DefaultEditorKit.cutAction));
    menu.add(getActionByName(DefaultEditorKit.copyAction));
    menu.add(getActionByName(DefaultEditorKit.pasteAction));

    menu.addSeparator();

    menu.add(getActionByName(DefaultEditorKit.selectAllAction));
    return menu;
  }

  //Create the style menu.
  public JMenu createFormatMenu() {
    JMenu menu = new JMenu("Format");
    //font weigths submenu
    JMenu wMenu = new JMenu("Font decoration");
    Action action = new StyledEditorKit.BoldAction();
    action.putValue(Action.NAME, "Bold");
    wMenu.add(action);

    action = new StyledEditorKit.ItalicAction();
    action.putValue(Action.NAME, "Italic");
    wMenu.add(action);

    action = new StyledEditorKit.UnderlineAction();
    action.putValue(Action.NAME, "Underline");
    wMenu.add(action);
    menu.add(wMenu);
    //font size submenu
    JMenu sMenu = new JMenu("Font size");
    sMenu.add(new StyledEditorKit.FontSizeAction("10", 10));
    sMenu.add(new StyledEditorKit.FontSizeAction("11", 11));
    sMenu.add(new StyledEditorKit.FontSizeAction("12", 12));
    sMenu.add(new StyledEditorKit.FontSizeAction("13", 13));
    sMenu.add(new StyledEditorKit.FontSizeAction("14", 14));
    sMenu.add(new StyledEditorKit.FontSizeAction("15", 15));
    sMenu.add(new StyledEditorKit.FontSizeAction("16", 16));
    menu.add(sMenu);

    //font family submenu
    JMenu fMenu = new JMenu("Font family");
    fMenu.add(new StyledEditorKit.FontFamilyAction("Arial",
        "Arial"));
    fMenu.add(new StyledEditorKit.FontFamilyAction("Courier",
        "Courier"));
    fMenu.add(new StyledEditorKit.FontFamilyAction("Serif",
        "Serif"));
    fMenu.add(new StyledEditorKit.FontFamilyAction("SansSerif",
        "SansSerif"));
    menu.add(fMenu);
    //font color
    JMenu cMenu = new JMenu("Font color");
    cMenu.add(new StyledEditorKit.ForegroundAction("Black",
        Color.black));
    cMenu.add(new StyledEditorKit.ForegroundAction("Dark grey",
        Color.darkGray));
    cMenu.add(new StyledEditorKit.ForegroundAction("Blue",
        Color.blue));
    cMenu.add(new StyledEditorKit.ForegroundAction("Red",
        Color.red));
    cMenu.add(new StyledEditorKit.ForegroundAction("Green",
        Color.green));
    menu.add(cMenu);
    return menu;
  }

  //The following two methods allow us to find an
  //action provided by the editor kit by its name.
  private void createActionTable(JTextComponent textComponent) {
    actions = new HashMap();
    Action[] actionsArray = textComponent.getActions();
    for (int i = 0; i < actionsArray.length; i++) {
      Action a = actionsArray[i];
      actions.put(a.getValue(Action.NAME), a);
    }
  }

  private Action getActionByName(String name) {
    return (Action) (actions.get(name));
  }

  class UndoAction
      extends AbstractAction {
    public UndoAction() {
      super("Undo");
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
      try {
        if(textPane.isEditable()){
          undo.undo();
        }
      }
      catch (CannotUndoException ex) {
        System.out.println("Unable to undo: " + ex);
        ex.printStackTrace();
      }
      updateUndoState();
      redoAction.updateRedoState();
    }

    protected void updateUndoState() {
      if (undo.canUndo()) {
        if(textPane.isEditable()){
          setEnabled(true);
        }
        putValue(Action.NAME, undo.getUndoPresentationName());
      }
      else {
        setEnabled(false);
        putValue(Action.NAME, "Undo");
      }
    }
  }

  class RedoAction
      extends AbstractAction {
    public RedoAction() {
      super("Redo");
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
      try {
        undo.redo();
      }
      catch (CannotRedoException ex) {
        System.out.println("Unable to redo: " + ex);
        ex.printStackTrace();
      }
      updateRedoState();
      undoAction.updateUndoState();
    }

    protected void updateRedoState() {
      if (undo.canRedo()) {
         if(textPane.isEditable()){
           setEnabled(true);
         }
         putValue(Action.NAME, undo.getRedoPresentationName());
      }
      else {
        setEnabled(false);
        putValue(Action.NAME, "Redo");
      }
    }
  }
}