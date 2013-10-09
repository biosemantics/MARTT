package visitor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import learning.Utilities;
import learning.MarkedSegment;
import learning.Model;

/**
 *
 * <p>Title: PRScore</p>
 * <p>Description: find precision and recall from a 2 by 2 table</p>
 * <pre>
 *         truth        T            F
 * decision
 *   T                  a            b
 *   F                  c            d
 * </pre>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public class PRScore
    extends Score {
  private float precision = 0f;
  private float recall = 0f;
  private boolean printdetails = false;
  public PRScore() {

  }

  public PRScore(int a, int ab, int ac) {
    precision = (float) a / ab;
    recall = (float) a / ac;
  }

  public float getPrecision() {
    return precision;
  }

  public float getRecall() {
    return recall;
  }

  public void setPrecision(float precision) {
    this.precision = precision;
  }

  public void setRecall(float recall) {
    this.recall = recall;
  }

  public boolean isZero() {
    return Float.compare(recall, 0) == 0;
  }

  public boolean isGood() {
    return (!isZero() && Float.compare(recall, Float.NaN) != 0);
  }

  public String toString() {
    String output = precision + ":(p):" + recall + ":(r)";
    return output;
  }

  public Score addition(Score score) {
    PRScore sum = new PRScore();
    sum.setPrecision(precision + ( (PRScore) score).getPrecision());
    sum.setRecall(recall + ( (PRScore) score).getRecall());
    return sum;
  }

  public void divideBy(int k) {
    precision = precision / k;
    recall = recall / k;
  }

  public void reset() {
    precision = 0f;
    recall = 0f;
  }

  /**
   * measure how well elements ecl have been marked as such.
   * @param ec1
   * @return
   */
  public Score score(ElementComponent ec1) {
    //often times when root element has non-1 precision and recall, it is due to countSegmentsInString
    //returns different count when count markedseg.getSeg and answer string. seg and string may have differnt
    //language encoding entities, for example "&#38;#38;#215;" in seg, "#215;" is answer string
    /**@todo for now, set delim as such. To fix it, need to make Model a better super class **/
    //delim is in some Models, and can be accessed through current Model interface.
    String[] delim = new String[]{".",";",":"};
    ArrayList answers = ec1.getAnswers(); //arraylist of vectors
    ArrayList markedsegs = ec1.getMarkedSegs();

    if (answers.size() != markedsegs.size() && markedsegs.size() != 0 && answers.size() !=0) {
      System.err.println("can't score for element " + ec1.getTag() +
                         " : answers and markeds are in different size");
    }
    int ac = 0, ab = 0, a = 0;
    int size = answers.size(); //number of examples
    if (markedsegs.size() == 0) {
      //when the node was added due to inserting answers
      //a and ab = 0;
      ac = nonPlaceholders(answers, delim); //all non-placeholders in answers
      if(printdetails){
        System.out.println("false negative for " + ec1.getTag() + ":");
        Iterator it = answers.iterator();
        while (it.hasNext()) {
          Vector va = (Vector) it.next();
          Enumeration en = va.elements();
          while (en.hasMoreElements()) {
            System.out.println( (String) en.nextElement());
          }
        }
      }
    }
    else {
      for (int i = 0; i < size; i++) {
        Vector va = (Vector) answers.get(i); //one vector for one example
        Vector vm = (Vector) markedsegs.get(i);
        int f1 = 0, f2 = 0;
        if (!isPlaceholder(va)) { //if v contains one element that is a placeholder
          //ac = ac + va.size();
          ac = ac + countSegmentsInVector(va, delim);
          f1 = 1;
        }
        if (!isPlaceholder(vm)) {
          //ab = ab + vm.size(); //problem: vm has two vectors which make the same content as va which has one vector,
          ab = ab + countSegmentsInVector(vm, delim);
          f2 = 1;
        }
        if (f1 == 0 && f2 != 0) {
          if(printdetails){
            System.out.println("false positives for " + ec1.getTag() + ":");
            Enumeration en = vm.elements();
            while (en.hasMoreElements()) {
              System.out.println( ( (MarkedSegment) en.nextElement()).toString());
            }
            System.out.println();
          }
        }

        if (f1 != 0 && f2 == 0) {
          if(printdetails){
            System.out.println("false negative for " + ec1.getTag() + ":");
            Enumeration en = va.elements();
            while (en.hasMoreElements()) {
              System.out.println( (String) en.nextElement());
            }
            System.out.println();
          }
        }

        if (f1 * f2 == 1) { //va and vm are not placeholders
          int count = matchContent(va, vm, ec1, delim);
          a = a + count;
        }
      }
    }
    PRScore score = new PRScore(a, ab, ac);
    return score;
  }

  /*public Score score(ElementComponent ec1) {
    //if(ec1.getTag().compareTo("sorus") == 0){
    //  System.out.println();
    //}
    ArrayList answers = ec1.getAnswers(); //arraylist of vectors
    ArrayList markeds = ec1.getMarkeds();
    if(answers.size() != markeds.size() && markeds.size() != 0){
      System.err.println("can't score for element "+ec1.getTag()+" : answers and markeds are in different size");
      //System.exit(1);
    }
    int ac = 0, ab = 0, a = 0;
    int size = answers.size();//number of examples
    if(markeds.size() == 0){
      //when the node was added due to inserting answers
      //a and ab = 0;
      ac = nonPlaceholders(answers);//all non-placeholders in answers
      System.out.println("false negative for " + ec1.getTag() + ":");
      Iterator it = answers.iterator();
      while(it.hasNext()){
        Vector va = (Vector) it.next();
        Enumeration en = va.elements();
        while (en.hasMoreElements()) {
          System.out.println( (String) en.nextElement());
        }
      }
    }
    else {
      for (int i = 0; i < size; i++) {
        Vector va = (Vector) answers.get(i); //one vector for one example
        Vector vm = (Vector) markeds.get(i);
        int f1 = 0, f2 = 0;
       if (!isPlaceholder(va)) { //if v contains one element that is a placeholder
          ac = ac + va.size();
          f1 = 1;
        }
        if (!isPlaceholder(vm)) {
          ab = ab + vm.size(); //problem: vm has two vectors which make the same content as va which has one vector,
          f2 = 1;
        }
        if(f1 == 0 && f2 != 0){
          System.out.println("false positives for "+ec1.getTag()+":");
          Enumeration en = vm.elements();
          while(en.hasMoreElements()){
            System.out.println(((MarkedSegment)en.nextElement()).toString());
          }
        }
        if (f1 != 0 && f2 == 0) {
          System.out.println("false negative for " + ec1.getTag()+":");
          Enumeration en = va.elements();
          while (en.hasMoreElements()) {
            System.out.println( (String) en.nextElement());
          }
        }
        if (f1 * f2 == 1) { //va and vm are not placeholders
          int count = matchContent(va, vm, ec1);
          a = a + count;
        }
      }
    }
    PRScore score = new PRScore(a, ab, ac);
    return score;
   }*/

  /**
   * return the number of segments marked
   * @param answers
   * @param delim
   * @return
   */
  private int nonPlaceholders(ArrayList answers, String[] delim) {
    int total = 0;
    Iterator it = answers.iterator();
    while (it.hasNext()) {
      Vector v = (Vector) it.next();
      if (!isPlaceholder(v)) {
        //total += v.size();
        total = countSegmentsInVector(v, delim);
      }
    }
    return total;
  }

  private int countSegmentsInVector(Vector v, String[] delim) {
    int total = 0;
    if (v.get(0) instanceof String) {
      Enumeration en = v.elements();
      //count every "delim"-seperated segment
      while (en.hasMoreElements()) {
        String seg = (String) en.nextElement();
        total += countSegmentsInString(seg, delim);
      }
    }
    else if (v.get(0) instanceof MarkedSegment) {
      Enumeration en = v.elements();
      while (en.hasMoreElements()) {
        MarkedSegment seg = (MarkedSegment) en.nextElement();
        total += countSegmentsInString(seg.getSegment(), delim);
      }
    }
    return total;
  }

  /**
   * count the number of delim-separated segments the str has
   * @param str
   * @param delim
   * @return
   */
  private int countSegmentsInString(String str, String[] delim) {
    int count = 0;
    str = learning.Utilities.strip(str);
    str = str.replaceAll("&\\S*;", ""); //remove entities and malformed entities such as &#35;amp;&#215;
    while(str.compareTo("") != 0){
      int index = learning.Utilities.findCutPoint(str, delim);
      if(index == -1){
        //System.err.println("annotation segments at unexpected location");
        str = "";
      }else{
        str = str.substring(index + 1).trim();
      }
      count++;
    }
    return count;
  }

  /**
   * a placeholder vector has only one string element ""
   * for vm, placeholders may just be null
   * @param v
   * @return
   */
  private static boolean isPlaceholder(Vector v) {
    if (v.size() == 0) {
      return true; //null vector
    }
    if (v.get(0)instanceof String) {
      return v.size() == 1 && ( ( (String) v.get(0))).compareTo("") == 0;
    }
    else if (v.get(0)instanceof MarkedSegment) {
      return v.size() == 1 && ( ( (MarkedSegment) v.get(0))).getSegment() == null;
    }
    return false;
  }

  /**
   * if the strings in xml2 matches that in xml1, it is possible strings are splitted at different points in two vectors
   * only care about the number of matches
   * @param xml1 the answer
   * @param xml2 that marked up by the system
   * @return the number of strings in xml1 matched those in xml2
   *
   */
  private int matchContent(Vector xml1, Vector xml2,
                                  ElementComponent ec1, String[] delim) {
    int count = 0;
    Enumeration answers = xml1.elements();
    Enumeration markedsegs = xml2.elements();
    //cancatenate all elements in markeds into a long string
    StringBuffer sb = new StringBuffer();
    while (markedsegs.hasMoreElements()) {
      MarkedSegment temp = (MarkedSegment) markedsegs.nextElement();
      String tempstring = Utilities.strip(temp.getSegment());//take away xml tags
      sb.append(tempstring);
    }
    //matching
    while (answers.hasMoreElements()) {
      String ans = (String) answers.nextElement();
      String mkcopy = sb.toString();
      ans = Utilities.strip(ans);
      while (ans.compareTo("") != 0) {
        int index = learning.Utilities.findCutPoint(ans, delim);
        String ansseg = null;
        if(index >= 0){
          ansseg = ans.substring(0, index + 1);
        }else{
          ansseg = ans;
        }
        String anscopy = ansseg;
        ans = ans.substring(ansseg.length()).trim();
        ansseg = Utilities.strip(ansseg);
        ansseg = wordString(ansseg);
        String mk = wordString(sb.toString());
        if (mk.matches(".*?" + ansseg + ".*?")) {
          count++;
          if(printdetails){
            printMatchSegment(anscopy, xml2, ec1.getTag());
          }
        }
        else {
          if (printdetails) {
            System.out.println("Markup missed: [" + anscopy + "] as " +
                               ec1.getTag());
            System.out.println();
          }
        }
      }
    }
    return count;
  }

  private String wordString(String str) {
    str = str.replaceAll("&\\S*;","");
    str = str.replaceAll("\\s", ""); //remove all spaces
    str = str.replaceAll("\\W", ""); //avoid . which can mess up reg exp match
    //str = str.replaceAll("\\d", ""); //&#234;
    return str;
  }
  /**
   * find ansseg from xml2 and print corresponding MarkedSegment
   * @param ansseg
   * @param xml2
   */
  private void printMatchSegment(String anscopy, Vector xml2, String tag){
    Enumeration en = xml2.elements();
    String ansseg = wordString(anscopy);
    while(en.hasMoreElements()){
      MarkedSegment mkseg = (MarkedSegment)en.nextElement();
      String mkstring = wordString(mkseg.getSegment());
      if(mkstring.indexOf(ansseg) >= 0){
        //System.out.println(tag+":["+anscopy+"] is matched by "+mkseg.toString());
        //if(mkseg.getLabel().toString().compareTo("bracts[bracts]:bract[0.97/0.66]S") ==0){
        //  System.out.println();
        //}
        System.out.println("good match: "+mkseg.getLabel().toString());
        System.out.println();
        return;
      }
    }
  }
  /*private static int matchContent(Vector xml1, Vector xml2, ElementComponent ec1) {
    int count = 0;
    Enumeration answers = xml1.elements();
    Enumeration markeds = xml2.elements();
    //cancatenate all elements in markeds into a long string
    StringBuffer sb = new StringBuffer();
    while (markeds.hasMoreElements()) {
      String temp = (String) markeds.nextElement();
      temp = Utilities.strip(temp);//take away xml tags
      sb.append(temp);
    }
    //matching
    while (answers.hasMoreElements()) {
      String ans = (String) answers.nextElement();
      String anscopy = ans;
      String mkcopy = sb.toString();
      //ans = Utilities.strip(ans);
      ans = Utilities.strip(ans).replaceAll("\\s","");//remove all spaces
       ans = ans.replaceAll("\\W", ""); //avoid . which can mess up reg exp match
      ans = ans.replaceAll("\\d",""); //&234;
      String mk = sb.toString().replaceAll("\\s","").replaceAll("\\W", "").replaceAll("\\d","");
      if(mk.matches(".*?"+ans+".*?")){
        count++;
      }else{
        System.out.println(ec1.getTag()+ " : ");
        System.out.println("Answer: "+anscopy);
        System.out.println("Marked: "+mkcopy);
      }
    }
    return count;
   }*/

  public static void main(String[] argv){
    PRScore s = new PRScore();
    String A = "<plant-habit-and-life-style>Shrubs epiphytic, glabrous throughout.</plant-habit-and-life-style><stems>Stems to 1.5 m.</stems><leaves>Petiole ca. 1 cm; leaf blade oblong-elliptic, 9-15 &#215; 3-5 cm, base cuneate, oblique, apex caudate with a tail ca. 1.5 cm; lateral veins 10-12 pairs, obliquely ascending, conspicuous adaxially, obscure abaxially.</leaves><flowers>Pseudumbels extra-axillary; peduncle ca. 2.5 cm. Flowers not seen.</flowers><fruits>Follicles linear-lanceolate, ca. 15.5 cm &#215; 4 mm.</fruits><seeds>Seeds ca. 5 &#215; 2.5 mm; coma ca. 4 cm.</seeds>";
    String B = "Shrubs epiphytic, glabrous throughout. Stems to 1.5 m. Petiole ca. 1 cm; leaf blade oblong-elliptic, 9-15 &#38;amp;#215; 3-5 cm, base cuneate, oblique, apex caudate with a tail ca. 1.5 cm; lateral veins 10-12 pairs, obliquely ascending, conspicuous adaxially, obscure abaxially. Pseudumbels extra-axillary; peduncle ca. 2.5 cm. Flowers not seen. Fruiting pedicel ca. 2 cm. Follicles linear-lanceolate, ca. 15.5 cm &#38;amp;#215; 4 mm. Seeds ca. 5 &#38;amp;#215; 2.5 mm; coma ca. 4 cm.";
    System.out.println(s.countSegmentsInString(A, new String[]{".",";"}));
    System.out.println(s.countSegmentsInString(B, new String[]{".",";"}));

  }
}
