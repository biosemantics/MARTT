package structure;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * <p>Title: StructureMeasure</p>
 * <p>Description: A measure of structuredness of flora corpus</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public abstract class MeasureAlgorithm {
  protected String[] instances=null;
  protected String[] classes=null;

  public MeasureAlgorithm(String[] instances, String[] classes) {
    this.instances = instances;
    this.classes = classes;
  }

  public abstract float score();

  public void printPatternHash(Hashtable fields) {
    for (int i = 0; i < classes.length; i++) {
      Hashtable pattern = (Hashtable) fields.get(classes[i]);
      if (pattern != null) {
        Enumeration en = pattern.keys();
        System.out.println("\n\nDelimitor Patterns for Class " + classes[i]);
        int count1 = 0;
        int count10 = 0;
        while (en.hasMoreElements()) {
          String p = (String) en.nextElement();
          float value = ( (Float) pattern.get(p)).floatValue();
          if (Float.compare( value,0f) != 0) {
            if(Float.compare(value, 1f) == 0){
              count1++;
            }else if(Float.compare(value, 10f) <= 0){
              count10++;
            }else{
              System.out.println(value + "  " + p);
            }
          }
        }
        System.out.println("There are ["+count1+"] patterns occurred once, ["+(count10+count1)+"] patterns occurred less then 10 times");
      }
    }
  }


  public static void main(String[] args) {
  //  MeasureAlgorithm measureAlgorithm1 = new MeasureAlgorithm();
  }

}
