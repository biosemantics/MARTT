package structure;

import org.w3c.dom.*;
/**
 * <p>Title: StructureMeasure</p>
 * <p>Description: A measure of structuredness of flora corpus</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class Utilities {
  public Utilities() {
  }

  public static int nonZero(int [] array){
    int count = 0;
    for(int i = 0; i < array.length; i++){
      if(array[i] != 0){
        count++;
      }
    }
    return count;
  }

  public static boolean exist(String[] array, String element) {
     for (int i = 0; i < array.length; i++) {
       if (array[i].compareTo(element) == 0) {
         return true;
       }
     }
     return false;
   }

  public static int combinations(int ccount){
    double combs = 0d;
    for(int c = 1; c<ccount / 2; c++){
      combs += MathFunctions.nCr(ccount, c);
    }
    if(ccount % 2 == 0){
      combs = combs*2 - MathFunctions.nCr(ccount, ccount/2) + 1;
    }else{
      combs = combs *2 + 1;
    }

    return (int)Math.ceil(combs);
  }

  /**
   * get text of a element node
   * @return
   */
  public static String getText(Node element){
    for(Node node = element.getFirstChild(); node != null; node = node.getNextSibling()){
      if(node.getNodeType() == node.TEXT_NODE){
        return node.getNodeValue();
      }
    }
    return null;
  }

  public static void main(String[] args) {
    Utilities utilities1 = new Utilities();
    System.out.println(combinations(15));
  }

}
