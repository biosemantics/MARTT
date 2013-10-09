package visitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * <p>Title: BDLearner</p>
 * <p>Description: Learn to mark up biological descriptions</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class Serializer {
  public Serializer() {
  }

  public static void serialization(String filename, Object object) {
    try {
      FileOutputStream out = new FileOutputStream(new File(filename));
      ObjectOutputStream s = new ObjectOutputStream(out);
      s.writeObject(object);
      s.flush();
      s.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Object readback(String filename) {
    Object obj = null;
    try {
      FileInputStream in = new FileInputStream(new File(filename));
      ObjectInputStream s = new ObjectInputStream(in);
      obj = (Object) s.readObject();
      s.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return obj;
  }

  public static void main(String[] args) {
    Serializer serializer1 = new Serializer();
  }

}