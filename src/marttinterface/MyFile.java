package marttinterface;

import java.io.File;

/**
 * <p>Title: User Interface of MARTT </p>
 * <p>Description: Support training example annotation and marked-up example review.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */

public class MyFile{
  public File file = null;
  public MyFile(File f) {
    this.file = f;
  }
  public File getFile(){
    return file;
  }
  public String toString(){
    return file.getName();
  }

}