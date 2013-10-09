package marttinterface;

import java.io.File;
import java.util.Vector;
/**
 * <p>Title: User Interface of MARTT </p>
 * <p>Description: Support training example annotation and marked-up example review.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */

public class Setting {
  private String filepath = null;
  public static String schemapath = "xmlschema.xsd";
  private String depotpath = null;
  private boolean maketodo = false;
  private boolean diff = true; //open files that are not in depot
  private Vector todolist = null;
  private int[] listitems = null;
  public static final String todofile = "todolist.txt";


  public Setting() {
  }

  public void setFilePath(String filepath) {
    this.filepath = filepath;
  }

  public void setDiff(boolean diff) {
    this.diff = diff;
  }

  public void setSchemaPath(String schemapath) {
    this.schemapath = schemapath;
  }

  public void setDepotPath(String depotpath) {
    this.depotpath = depotpath;
  }


  public void setMakeTodo(boolean maketodo) {
    this.maketodo = maketodo;
  }

  public void setTodoList(Vector todolist){
    this.todolist = todolist;
    this.listitems = new int[todolist.size()];
  }

  public String getFilePath() {
    return filepath;
  }

  public boolean getDiff() {
    return diff;
  }

  public String getSchemaPath() {
    return schemapath;
  }

  public String getDepotPath() {
    return depotpath;
  }

  public boolean getMakeTodo() {
    return maketodo;
  }

  /**
   * return current TodoList, there may be to-be-purged items in the list.
   * @return
   */
  public Vector getTodoList(){
    return todolist;
  }

  public void setListItem(int i){
    listitems[i] = 1;
  }

  /**
   * @todo when the user remove the list and then purge list, throws list not exist.
   * @todo add short-cut for purge list
   * @todo import list not show in the initial interface
   * @return int
   */
  public int purgeTodoList(){
    int purged = 0;
    for(int i = 0; i < listitems.length; i++){
      if(listitems[i] == 1){
        todolist.remove(i-purged);
        purged++;
      }
    }
    listitems = new int[todolist.size()];
    return purged;
  }

}
