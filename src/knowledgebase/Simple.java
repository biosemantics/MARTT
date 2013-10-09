package knowledgebase;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: Thesis Project, everything in this project</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class Simple extends Component implements Serializable{
  public Simple() {
  }

  public boolean addChild(Component child){
      return false;
    }

    public Component getChild(String tag){
      return null;
    }

    public ArrayList getChildren(){
      return new ArrayList();
    }

    public void removeChild(String tag){
    }

}