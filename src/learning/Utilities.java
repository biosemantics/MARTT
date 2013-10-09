package learning;

import java.util.regex.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Iterator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.LineNumberReader;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import jds.collection.BinarySearchTree;
import jds.collection.SparseMatrix;
import java.util.Hashtable;
import structure.MathFunctions;
//import xmlsimilarity.WordBasedCosineSimilarity;

/**
 * <p>Title: Learning</p>
 * <p>Description: Learning algorithms for marking up</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public class Utilities {
//final static String sentDelim = "[.;,]+\\s*";
  final static String sentDelim = "[.;]+\\s*";
  public Utilities() {
  }

  public static String[] getSentences(String longString) {
    Pattern p = Pattern.compile(sentDelim);
    String[] sents = p.split(longString);
    return sents;
  }

  /**
 * if there is no taxon, return xml
 * @param xml
 * @return
 */
public static String removeTaxon(String xml) {
  String pattern = "<taxon>.*?</taxon>";
  Pattern p = Pattern.compile("(.*?)(" + pattern + ")(.*)");
  Matcher m = p.matcher(xml);
  if (m.lookingAt()) {
    xml = m.group(1) + m.group(3);
  }
  return xml;
}





  /**
   * find the indices of top class from score array
   * @param scores an array of score, each element represents a class
   * @param N the number of candidate score kept
   * @return an array of class indices, sorted in decending order.
   */
  public static int[] topIndices(double[] scores, int N) {
    if (scores.length < N) {
      N = scores.length;
    }
    int[] indices = new int[N];
    for (int i = 0; i < N; i++) {
      indices[i] = -1;
    }
    double[] sorted = (double[]) scores.clone();
    int size = scores.length;
    int count = 0;
    Arrays.sort(sorted);
    boolean equal = true;
    for (int i = 1; i < size; i++) {
      if (Double.compare(sorted[0], sorted[i]) != 0) {
        equal = false;
      }
    }
    if (equal) {
      return indices; //there is no "top" indices
    }
    for (int j = size - 1; j > size - N - 1; j--) {
      for (int k = 0; k < size; k++) {
        if (Double.compare(sorted[j], scores[k]) == 0) {
          if (!exist(indices, k)) {
            indices[count++] = k;
            break;
          }
          else {
            continue;
          }
        }
      }
    }
    return indices;
  }

  public static int[] topIndices(float[] scores, int N) {
    if (scores.length < N) {
      N = scores.length;
    }
    int[] indices = new int[N];
    for (int i = 0; i < N; i++) {
      indices[i] = -1;
    }
    float[] sorted = (float[]) scores.clone();
    int size = scores.length;
    int count = 0;
    Arrays.sort(sorted);
    boolean equal = true;
    for (int i = sorted.length - 1; i > 0; i--) {
      if (Float.compare(sorted[0], sorted[i]) != 0) {
        equal = false;
      }
    }
    if (equal) {
      return indices; //there is no "top" indices
    }
    for (int j = size - 1; j > size - N - 1; j--) {
      for (int k = 0; k < size; k++) {
        if (Float.compare(sorted[j], scores[k]) == 0) {
          if (!exist(indices, k)) {
            indices[count++] = k;
            break;
          }
          else {
            continue;
          }
        }
      }
    }
    return indices;
  }

  private static boolean exist(int[] array, int element) {
    for (int i = 0; i < array.length; i++) {
      if (array[i] == element) {
        return true;
      }
    }
    return false;
  }

  /**
   * mode chooses the method:
   * mode = 0, default; mode =1, diff > 1*standard deviation;
   */
  public static int maxInArray(float[] array, int mode) {
    int index = 0;
    float max = array[0];
    float diff = 0f;

    if (mode > 0) {
      //compute standard deviation
      diff = stdDev(array);
    }

    boolean test = false;

    for (int i = 1; i < array.length; i++) {
      switch (mode) {
        case 0:
          test = array[i] > max;
          break;
        case 1:
          test = (array[i] - max) > diff;
          break;
      }
      if (test) {
        index = i;
        max = array[i];
      }
    }
    return index;
  }

  public static float stdDev(float[] array) {
    int l = array.length;

    double sqr = 0d;
    float average = average(array);

    for (int i = 0; i < l; i++) {
      sqr += Math.pow(array[i] - average, 2.0d);
    }
    return (float) Math.sqrt(sqr / (l - 1));
  }

  public static float average(float[] array) {
    float sum = 0f;
    int l = array.length;
    for (int i = 0; i < l; i++) {
      sum += array[i];
    }
    return sum / l;
  }

  /**
   * find out if the index pointing to the max value of the array
   *
   * @para index and an array
   * @return -1 if yes, else the index whoes value is the max
   */
  public static int isMaxInArray(int index, float[] array) {
    int max = index;
    for (int i = 0; i < array.length; i++) {
      if (array[max] < array[i]) {
        max = i;
      }
    }
    if (max == index) {
      return -1;
    }
    return max;
  }

  public static float[] diffOfTwoArray(float[] score1, float[] score2) {
    int number = score1.length;
    float[] increase = new float[number];
    for (int i = 0; i < number; i++) {
      increase[i] = (score1[i] - score2[i]); // / (0.001f+score1[i]);
    }
    return increase;
  }

  public static String print(double[] array) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < array.length; i++) {
      sb.append(array[i] + " ");
    }
    return sb.toString();
  }

  //copy from float[] array, fix
  public static String print(int[] array) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < array.length; i++) {
      sb.append(array[i] + " ");
    }
    return sb.toString();
  }

  /**
   *
   * @param flatxml a well-formed xml segments like <>...</><>...</>
   * @return the first tag
   */
  public static String getFirstTag(String flatxml) {
    int start = flatxml.indexOf("<");
    int end = flatxml.indexOf(">");
    return flatxml.substring(start+1, end);
  }

  /**
   *
   * @param flatxml a well-formed xml segments like <>...</><>...</>
   * @return the first text segment enclosed by the first tag pair.
   */
  public static String getFirstText(String flatxml) {
    String tag = getFirstTag(flatxml);
    Pattern p = Pattern.compile(".*?<" + tag + ">(.*?)</" + tag + ">.*");
    Matcher m = p.matcher(flatxml);
    String text = null;
    if (m.lookingAt()) {
      text = m.group(1);
    }
    return text;
  }

  /**
   *
   * @param flatxml a well-formed xml segments like <>...</><>...</>
   * @return the remaining of flatxml after removing the first element.
   */
  public static String removeFirstElement(String flatxml) {
    String tag = getFirstTag(flatxml);
    String text = getFirstText(flatxml);
    text = escape(text);
    Pattern p = Pattern.compile(".*?<"+tag+">"+text+"</"+tag+">(.*)");
    Matcher m = p.matcher(flatxml);
    if(m.lookingAt()){
      return m.group(1);
    }
    return null;
  }

  /**
   * strip off xml tags
   * @param test
   * @return
   */
  public static String[] stripOffTags(String[] test) {
    String[] plain = new String[test.length];
    for (int i = 0; i < test.length; i++) {
      plain[i] = strip(test[i]);
    }
    return plain;
  }

  public static String strip(String xml) {
    String copy = xml.replaceAll("<\\?.*?\\?>", "");
    Pattern p = Pattern.compile("(.*?)<(.*?)>(.*?)</\\2>(.*)");
    Matcher m = p.matcher(copy);
    while (m.lookingAt()) {
      String text = m.group(1) + " " + m.group(3) + " " + m.group(4);
      copy = text.toString().trim();
      m = p.matcher(copy);
    }
    return copy.replaceAll("^\\s+", "").replaceAll("\\s+", " ");
  }

  public static Document getDomModel(String xml) {
    Document document = null;
    String xmltext = xml;
    if (xml.indexOf("<?") < 0) {
      //xmltext = "<?xml version=\"1.0\" encoding=\"iso8859-1\"?>" + xml;
      xmltext = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
    }
    //build dom for xml
    try {
      DocumentBuilderFactory factory =
          DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      //document = builder.parse(new ByteArrayInputStream(xmltext.getBytes(
      //    "ISO-8859-1")));
      document = builder.parse(new ByteArrayInputStream(xmltext.getBytes(
          "UTF-8")));
    }
    catch (Exception e) {
      System.err.println(xmltext);
      e.printStackTrace();
    }
    return document;
  }

  public static ArrayList[] getLENGrams(String[] tokens, int len, String symbol) {
      ArrayList[] ngrams = new ArrayList[len];
      for (int i = 0; i < len; i++) {
        ngrams[i] = new ArrayList();
      }
      for (int i = 1; i <= len; i++) { //n gram
        for (int j = 0; j < len - i + 1; j++) {
          String token = "";
          for (int k = j; k < j + i; k++) {
            //for (int k = len - i - j; k < len - j; k++) {
            token += symbol + tokens[k];
          }
          token = token.substring(1); //remove the first symbol
          //System.out.println(i - 1 + " " + token);
          //token = token.toLowerCase();
          token = token.replaceFirst("[\\p{Punct}&&[^=]]$","").trim();
          if(token.compareTo("") != 0){
            ngrams[i - 1].add(token);
          }
        }
      }
      return ngrams;
    }

  /**
   * <></><></> => <root><></><></></root>
   * @param flatxml String
   * @return String
   */
  public static String wellForm(String flatxml){
    Pattern p = Pattern.compile("^\\s*<([^<]*)>.*?</\\1>\\s*$");
    Matcher m = p.matcher(flatxml);
    if(!m.matches()){
      flatxml = "<root>"+flatxml+"</root>";
    }
    return flatxml;
  }

  /**
   * make xml a well-formed xml with depth=1 and containing all text in original xml
   * @param xml
   * @return
   */
  public static String getFlatXml(String xml) {
    String leftangel = "<";
    String leftangelend = "</";
    String rightangel = ">";
    String space = " ";
    StringBuffer example = new StringBuffer();
    Document doc = Utilities.getDomModel(xml);
    Node root = doc.getDocumentElement();
    for (Node n = root.getFirstChild(); n != null; n = n.getNextSibling()) {
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        String text = getNodeContent(n, new StringBuffer());
        String tag  = n.getNodeName();
        example.append(leftangel).append(tag).append(rightangel).append(text).
            append(leftangelend).append(tag).append(rightangel);
      }
      else if (n.getNodeType() == Node.TEXT_NODE) {
        String tag = n.getParentNode().getNodeName(); //use parent tag for cdata data
        String text = n.getNodeValue().trim();
        if(text.compareTo("") != 0){
          example.append(leftangel).append(tag).append(rightangel).append(text).
              append(leftangelend).append(tag).append(rightangel);
        }
        //example.append(n.getNodeValue()).append(space);



      }
    }
    return example.toString();
  }

  /**
   * get all the textual content in the node n, use recursion
   * @param n
   * @return a plain string without tags
   */
  public static String getNodeContent(Node n, StringBuffer text) {
    String space = " ";
    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
      if (d.getNodeType() == Node.ELEMENT_NODE) {
        //text = getNodeContent(d, text) + space;
        text = new StringBuffer(getNodeContent(d, text)).append(space);
      }
      else if (n.getFirstChild().getNodeType() == Node.TEXT_NODE) { //base
        //text += n.getFirstChild().getNodeValue() + space;
        text.append(n.getFirstChild().getNodeValue()).append(space);
      }
    }
    return text.toString().trim();
  }

  /* public static String printRules(Vector rules, String[] classes) {
     int hasLabel = 0;
     StringBuffer sb = new StringBuffer();
     if (classes != null) {
       hasLabel = 1;
     }
     Enumeration en = rules.elements();
     int count = 0;
     while (en.hasMoreElements()) {
       sb.append("\n");
       if (hasLabel == 1) {
         sb.append(classes[count]);
       }
       else {
         sb.append(count);
       }
       TreeSet ruleSet = (TreeSet) en.nextElement();
       Iterator it = ruleSet.iterator();
       while (it.hasNext()) {
         Rule r = (Rule) it.next();
         sb.append("\n" + r.toString());
       }
       count++;
     }
     return sb.toString();
   }*/

  /**
   * fix the length to be length, if less then length, append padchars to make it to the fixed length
   */
  public static String fixLength(String classlabel, int length, char padchar) {
    int pad = length - classlabel.length();
    StringBuffer sb = new StringBuffer();
    if (pad < 0) {
      for (int i = 0; i < length; i++) {
        sb.append(classlabel.charAt(i));
      }
      return sb.toString();
    }
    else {
      for (int i = 0; i < pad; i++) {
        sb.append(padchar);
      }
      return (new StringBuffer(classlabel).append(sb)).toString();
    }
  }

  public static String printBTree(BinarySearchTree bt) {
    StringBuffer sb = new StringBuffer();
    Enumeration objects = bt.elements();
    while (objects.hasMoreElements()) {
      sb.append(objects.nextElement().toString());
    }
    return sb.toString();
  }

  public static String printScoredBTree(BinarySearchTree bt) {
    StringBuffer sb = new StringBuffer();
    Enumeration objects = bt.elements();
    while (objects.hasMoreElements()) {
      sb.append(((Term)objects.nextElement()).printScore());
    }
    return sb.toString();
  }

  public static int minDistance(float[] point, float[][] points) {
    float min = distance(point, points[0]);
    float dist = 0f;
    int index = 0;
    for (int i = 1; i < point.length; i++) {
      dist = distance(point, points[i]);
      if (min > dist) {
        min = dist;
        index = i;
      }
    }
    return index;
  }

  public static float distance(float[] point1, float[] point2) {
    double dist = 0d;
    for (int i = 0; i < point1.length; i++) {
      dist += Math.pow(point1[i] - point2[i], 2.0d);
    }
    return (float) Math.sqrt(dist);
  }

  /**
   * read in a text file
   * @param a text file
   * @return a string of file content
   */
  public static String readFile(File f) {
    StringBuffer sb = new StringBuffer();
    try {
      FileReader s = new FileReader(f);
      LineNumberReader r = new LineNumberReader(s);
      String line = "";
      while ( (line = r.readLine()) != null) {
        sb.append(line);
        sb.append(' ');
      }
    }
    catch (Exception ex) {
      System.err.println("In Utilities.readFile: ");
      ex.printStackTrace();
    }
    String xml = sb.toString(); //apply xml encoding
    xml = new TextPreprocessing(xml).replaceSpecialChar();
    if (xml.matches("<\\?.*?\\?>.*")) { //xml
      Document doc = getDomModel(xml);
      Node root = doc.getDocumentElement();
      xml = root.toString();
      //xml = xml.replaceFirst("<\\?.*?\\?>\\s*","");
    }
    return xml;
  }

  public static float getEntropy(int[] classCount) {
    //find entropy for entire collection
    float entroC = 0f;
    int totalExps = 0;
    for (int c = 0; c < classCount.length; c++) {
      totalExps += classCount[c];
    }
    for (int c = 0; c < classCount.length; c++) {
      float p = (float) classCount[c] / totalExps; //propotion
      if (Float.compare(p, 0f) != 0) {
        entroC += -1f * p * Math.log(p) / Math.log(2d);
      }
    }
    return entroC;
  }

  public static float getEntropy(float[] classCount) {
    //find entropy for entire collection
    float entroC = 0f;
    float totalExps = 0;
    for (int c = 0; c < classCount.length; c++) {
      totalExps += classCount[c];
    }
    for (int c = 0; c < classCount.length; c++) {
      float p = (float) classCount[c] / totalExps; //propotion
      if (Float.compare(p, 0f) != 0) {
        entroC += -1f * p * Math.log(p) / Math.log(2d);
      }
    }
    return entroC;
  }

  /**
   * read in serialized xml files
   * @param an seriazed xml file
   * @return an Enumeration of elements in xml file
   */
  public static Enumeration readXMLElements(File file) {
    try {
      ObjectInputStream s = new ObjectInputStream(new FileInputStream(file));
      Vector v = null;
      if ( (v = (Vector) s.readObject()) != null) { //a file contains one elements vector
        return v.elements();
      }
    }
    catch (Exception e) {
      System.out.println("In Utilities.readXMLElements: " + e.toString());
    }
    return null;
  }

  /**
   * read in a serialized <code>SparseMatrix</code> file
   * @param a serialized SparseMatrix
   * @return a serialized SparseMatrix
   */
  public static Object readObject(File f) {
    File file = f;
    SparseMatrix sm;
    try {
      FileInputStream in = new FileInputStream(file);
      ObjectInputStream s = new ObjectInputStream(in);
      sm = (SparseMatrix) s.readObject();
      return sm;
    }
    catch (Exception exc) {
      System.out.println(exc.toString());
    }
    return null;
  }

  public static Vector getTagList(String xml, String[] delim) {
    Vector result = new Vector();
    ArrayList tags = new ArrayList();
    Hashtable repeat = new Hashtable();
    Pattern p = Pattern.compile("(.*?)<(.*?)>(.*?)</\\2>");
    Matcher m = p.matcher(xml);
    if (!m.lookingAt()) {
      result.add(tags);
      result.add(repeat);
      return result;
    }

    while (xml.compareTo("") != 0) {
      String tag = null;
      String seg = null;
      m = p.matcher(xml);
      if (m.lookingAt()) {
        if (m.group(1).trim().compareTo("") != 0) {
          int rep = repCount(m.group(1), delim);
          tag = Model.nonspecified;
          System.out.print("");
          repeat.put(tag,
                     repeat.get(tag) == null ? Integer.toString(rep) :
                     ( (String) repeat.get(tag)) + " " + rep);
          tags.add(tag);
        }
        tag = m.group(2);
        seg = m.group(3);
        int rep = repCount(seg, delim);
        repeat.put(tag,
                   repeat.get(tag) == null ? Integer.toString(rep) :
                   ( (String) repeat.get(tag)) + " " + rep);
        tags.add(tag);
      }
      else {
        int rep = repCount(xml, delim);
        tag = Model.nonspecified;
        System.out.print("");
        repeat.put(tag,
                   repeat.get(tag) == null ? Integer.toString(rep) :
                   ( (String) repeat.get(tag)) + " " + rep);
        tags.add(tag);
        break;
      }
      //remove first element
      int index = xml.indexOf("</" + tag + ">");
      xml = xml.substring(index + 3 + tag.length()).trim();
    }
    result.add(tags);
    result.add(repeat);
    return result;
  }
  /**
   * count the number of segments, seperated by delim, the text has.
   * @param text String
   * @return int
   */
  private  static int repCount(String text, String[] delim){
    text = text.trim();
    int count = 0;
    while(text.compareTo("") != 0){
      int index = learning.Utilities.findCutPoint(text, delim);
      text = index >=0 && text.length() >= index+1 ? text.substring(index+1) : "";
      count++;
    }
  return count;
}
  /*public static ArrayList getTagList(String xml) {
    ArrayList tags = new ArrayList();
    Pattern p = Pattern.compile("(.*?)<(.*?)>.*?</\\2>");
    Matcher m = p.matcher(xml);
    if (!m.lookingAt()) {
      return tags;
    }

    while (xml.compareTo("") != 0) {
      String tag = null;
      m = p.matcher(xml);
      if (m.lookingAt()) {
        if (m.group(1).trim().compareTo("") != 0) {
          tags.add(Model.nonspecified);
        }
        tag = m.group(2);
        tags.add(tag);
      }
      else {
        tag = Model.nonspecified; //@todo is this right?
        tags.add(tag); //xml is plain text string
        break;
      }
      //remove first element
      int index = xml.indexOf("</" + tag + ">");
      xml = xml.substring(index + 3 + tag.length()).trim();
    }
    return tags;

  }*/

  /**
   * use semantics to decide the class for text
   * @param text
   * @param semantics
   * @return class label
   * @todo if subelements are marked up, should just go ahead and fetch class labels.
   */
  /*public static String classFor(String text, TermSemantic semantics, double conf, double sup){
    String[] tokens = Tokenizer.tokenize(text);
    if (tokens == null || tokens.length < 1) {
      return "";
    }
    StringBuffer ngram = new StringBuffer();
    int n = miner.TermSemantic.n > tokens.length ? tokens.length :
        miner.TermSemantic.n;
    for (int i = 0; i < n; i++) {
      ngram.append(tokens[i] + " ");
    }
    SemanticLabel[] fulltags = semantics.semanticClassFor(ngram.toString().trim(), conf,
                                               sup);

    return fulltags[0] == null? null : fulltags[0].getTag();
  }*/
  /**
   *
   * @param longstring
   * @param shortstring
   * @return the occurrence of shortstring in the longstring
   */
  public static int getOccurrence(String longstring, String shortstring){
    int i = longstring.indexOf(shortstring);
    if(i < 0){
      return 0;
    }
    return 1+getOccurrence(longstring.substring(i+shortstring.length()), shortstring);
  }

  /**
   *
   * @param xml flat xml string
   * @return tags in the xml string in order
   */
  public static Iterator getTagOrder(String xml) {
    ArrayList tags = new ArrayList();
    Pattern p = Pattern.compile("(.*?)<(.*?)>.*?</\\2>");
    Matcher m = p.matcher(xml);
    if (!m.lookingAt()) {
      return tags.iterator();
    }

    while (xml.compareTo("") != 0) {
      String tag = null;
      m = p.matcher(xml);
      if (m.lookingAt()) {
        if (m.group(1).trim().compareTo("") != 0) {
          tags.add(Model.nonspecified);
          System.out.print("");
        }
        tag = m.group(2);
        tags.add(tag);
      }
      else {
        tag = Model.nonspecified; /**@todo is this right?**/
        System.out.print("");
        tags.add(tag); //xml is plain text string
        break;
      }
      //remove first element
      int index = xml.indexOf("</" + tag + ">");
      xml = xml.substring(index + 3 + tag.length()).trim();
    }
    return tags.iterator();
  }
  /**
   *
   * @param text
   * @return word1-word2-word3
   */
  public static String getFirstMWords(String text, int m, String separator, boolean stop){
    String[] words = Tokenizer.tokenize(text, stop);
    StringBuffer sb = new StringBuffer();
    if(words == null){
      return "";
    }
    int n = words.length > m-1 ? m-1 : words.length;
    for(int i = 0; i < n; i++){
      sb.append(words[i]).append(separator);
    }
    sb.setCharAt(sb.length()-1, ' ');
    return sb.toString().trim();
  }

  public static String getFirstThreeWords(String text){
    String[] words = Tokenizer.tokenize(text, true);
    StringBuffer sb = new StringBuffer();
    if (words == null) {
      return "";
    }
    int n = words.length > 2 ? 2 : words.length;
    for (int i = 0; i < n; i++) {
      sb.append(words[i]).append("-");
    }
    sb.setCharAt(sb.length() - 1, ' ');
    return sb.toString().trim();

  }
  /**
   * tokenizer should stop at any punctuation mark before the end of the text
   * for example , ( [, etc
   * @param text
   * @return
   */
  public static int stopAt(String text) {
    /*Pattern p = Pattern.compile("[;,\\(\\[]");
    Matcher m = p.matcher(text);
    if (m.find()) {
      return m.start();
    }
    return text.length();*/
    //String[] punct = new String[]{"!","\"","#","$","%","&","'","(",")","*","+",",","-",".","/",":",";","?","@","[","\\","]","^","_","`","{","|","}","~"};
    String[] punct1 = new String[]{"!","'",",",".",":",";"};
    int d = text.length();
    for(int i = 0; i < punct1.length; i++){
      int t = Model.getDelimiterIndex(punct1[i], text);
      d = t >= 0 &&t < d ? t: d;
    }
    Pattern p = Pattern.compile("[?{\\(\\[]");//cause problem in getDelimiterIndex
    Matcher m = p.matcher(text);
    if (m.find()) {
      int t = m.start();
      d = t < d ? t: d;
    }
    return d;
  }

  /**
   * find the delim that is closest to the beginning of content, return it's index
   * @param content
   * @return
   */
  public static int findCutPoint(String content, String[] delim) {

      int p = Integer.MAX_VALUE;
      for (int i = 0; i < delim.length; i++) {
        int c = learning.SemanticClassSegmentationModel.getDelimiterIndex(
            delim[i], content);
        if (c >= 0 && c < p) {
          p = c;
        }
      }
      if (p == Integer.MAX_VALUE) {
        return -1;
      }
      else {
        return p;
      }

  }

  /**
   * escape special chars to convert str to reg exp
   * @param str
   * @return
   */
  public static String escape(String str) {
    /* () => /( /), [] => /[ /], {} => /{ /}, . ? * + - !=> /. /? /* /+ /- /!*/
    /* ^ $ no change */
    return str.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)")
        .replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]")
        .replaceAll("\\{", "\\\\{").replaceAll("\\}", "\\\\}")
        .replaceAll("\\.", "\\\\.").replaceAll("\\*", "\\\\*")
        .replaceAll("\\+", "\\\\+").replaceAll("\\?", "\\\\?")
        .replaceAll("\\-", "\\\\-").replaceAll("\\!", "\\\\!");
  }

  public static void main(String[] args) {

    Utilities utilities1 = new Utilities();
    /*System.out.println(findCutPoint("[try this.]", new String[]{";","."}));*/
    System.out.println(getFlatXml("<morphology><gyrogonite> Gyrogonite of Characeae with undivided basal plate, except Aclistochara.</gyrogonite><extant-species><extant-species> In extant species, 5 large noncalcified coronula cells in 1 tier;</extant-species><thallus-corticated> thallus corticated or not corticated.</thallus-corticated></extant-species></morphology>"));

    int ccount = 4;
    double combs = 0d;

    for(int c = 1; c<ccount / 2; c++){
      combs += MathFunctions.nCr(ccount, c);
    }
    if(ccount % 2 == 0){
      combs = combs*2 - MathFunctions.nCr(ccount, ccount/2) + 1;
    }else{
      combs = combs *2 + 1;
    }
    System.out.print( (int)Math.ceil(combs));


    /*double x = Math.log(0.05d)/Math.log(2d);

    double y = Math.log(0.25d)/Math.log(2d);
    double z = 1-y/x;
    System.out.println(z);

    y = (1/4d)*(Math.log(1/20d)/Math.log(2d)) + (3/4d)*(Math.log(1/4d)/Math.log(2d));
    z = 1-y/x;
    System.out.println(z);

    y = (1/2d)*(Math.log(1/20d)/Math.log(2d)) + (1/2d)*(Math.log(1/4d)/Math.log(2d));
    z = 1-y/x;
    System.out.println(z);*/






    /*Utilities utilities1 = new Utilities();
    System.out.println(getFirstMWords("what about this time?", 3, " ", false));*/
    //System.out.println(findCutPoint("[try this.]", new String[]{";","."}));

    /*SparseMatrix sm;
         File f = new File("/home/hongcui/ThesisProject/Exp/Exp1/Models/colloc0");
         try{
      FileInputStream in = new FileInputStream(f);
      ObjectInputStream s = new ObjectInputStream(in);
      sm = (SparseMatrix)s.readObject();
      sm.elementAt(1,3);
         }catch(Exception exc){
      System.out.println(exc.toString());
         }*/
    /*double[] a = {
        1.0f, 1.0f, 1.0f, 1.0f};
         int[] in = topIndices(a, 3);
         for (int i = 0; i < 3; i++) {
      System.out.println(in[i]);
         }*/
    //Pattern p = Pattern.compile("(?<!(?:subg))\\.\\s*[A-Z]");
    /*Pattern p = Pattern.compile("\\.\\s*\\)\\s*[A-Z]");
         Matcher m = p.matcher("cm. ) Another");
         System.out.println(m.find());
         System.out.println(m.end());
     */
    /*double s = 1;
         double p = 0;
         System.out.println(Double.compare(p, s));*/
    /*String xml = "555";
    Iterator it = getTagOrder(xml);
    while (it.hasNext()) {
      System.out.println( (String) it.next());
    }*/

    /*String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level1\\trainingdir-fna500";
         File dirfile = new File(dir);
         File[] files = dirfile.listFiles();
         int count = 0;
         for(int i = 0; i < files.length; i++){
      String xml = readFile(files[i]);
      String flatxml = getFlatXml(xml);
      String flat = strip(flatxml).replaceAll("\\W", "");
      String text = strip(xml).replaceAll("\\W", "");
      if(flat.compareTo(text) != 0){
        System.out.println(count+" "+files[i].getName()+":"+xml);
        System.out.println();
        System.out.println(flatxml);
        System.out.println();
        count++;
      }
         }*/
  }
}
