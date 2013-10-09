package xmltest;

import java.io.Serializable;
/**
 * <p>Title: XMLTest</p>
 * <p>Description: Learn to use SAX and DOM interface to processing XML documents</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class Attributes implements Serializable{
  String[] localNames;
  String[] values;
  String[] qualifiedNames;

  public Attributes(String[] qualifiedNames, String[] values) {
    this.qualifiedNames = qualifiedNames;
    this.values = values;
    this.localNames = null;
  }

  public Attributes(String[] localNames, String[] qualifiedNames, String[] values) {
      this.localNames = localNames;
      this.values = values;
      this.qualifiedNames = qualifiedNames;
    }

  public int getLength(){
    return values.length;
  }

  public String getLocalName(int index){
    if(localNames != null){
      return localNames[index];
    }else{
      return null;
    }
  }

  public String getQName(int index){
    if(qualifiedNames != null){
      return qualifiedNames[index];
    }else{
      return null;
    }
  }


  public String getValue(int index){
    return values[index];
  }
}