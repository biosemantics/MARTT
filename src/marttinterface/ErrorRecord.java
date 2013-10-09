package marttinterface;

import java.util.*;
import java.util.regex.*;
import java.io.File;

/**
 * <p>Title: User Interface of MARTT </p>
 * <p>Description: Support training example annotation and marked-up example review.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */

public class ErrorRecord {
  static final String[] errortypes = new String[] {
      "Extra Content", "Missing Content", "Classification Error",
      "Compound Error", "Errors in Originals"};
  String file;
  String cause;
  String fpclass; //false positive class
  String fnclass; //false negtive class
  String error;
  static final String errorfile = "errors.txt";

  public ErrorRecord() {

  }

  public ErrorRecord(String file, String error, String cause,
                     String fpclass, String fnclass) {
    this.file = file;
    this.error = error;
    this.cause = cause;
    this.fpclass = fpclass;
    this.fnclass = fnclass;
  }


  public void setFile(String file) {
    this.file = file;
  }

  public void setError(String error) {
    this.error = error;
  }

  public void setCause(String cause) {
    this.cause = cause;
  }

  public void setFpclass(String fpclass) {
    this.fpclass = fpclass;
  }

  public void setFnclass(String fnclass) {
    this.fnclass = fnclass;
  }

/////////////////////////////////////////

  public String getFile() {
    return file;
  }

  public String getError() {
    return error;
  }

  public String getCause() {
    return cause;
  }

  public String getFpclass() {
    return fpclass;
  }

  public String getFnclass() {
    return fnclass;
  }
  ////////////////////////////////

  public String toString(){
    StringBuffer sb = new StringBuffer();
    sb.append("["+file+"]["+error+"]["+fpclass+"/"+fnclass+"]["+cause+"]");
    return sb.toString();
  }
}
