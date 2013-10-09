package learning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.*;

/**
 * <p>Title: MultiplePattern</p>
 * <p>Description: learn patterns from training examples for marking up multiple elements</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class MultiplePattern
    extends PatternElement implements Serializable{
  private ArrayList multiplepatterns = null;
  public MultiplePattern(String[] trainingexamples) {
    super(trainingexamples);
  }

  public void learnPatterns(boolean debug) {
    ArrayList pool = getElements("multiple", debug);
    pool = generateMultiplePatterns(pool, debug);
    /*if (debug) {
      System.out.println("patterns for multiple :");
         }
         Iterator it = pool.iterator();
         while (it.hasNext()) {
      String[] pts = (String[]) it.next();
      for (int i = 0; i < pts.length; i++) {
        pts[i] = pts[i].replaceAll(" ", "\\\\s+");
        if (debug) {
          System.out.println(pts[i]);
        }
      }
         }*/
    multiplepatterns = pool;
  }

  /**
   * @todo
   * @param finals
   * @return
   */
  protected ArrayList generalizeMP(ArrayList finals) {
    return finals;
  }

  /**
 * make pattern out of frequent leading words
 * @param pool
 * @param debug
 * @return
 */
/*private ArrayList generateMultiplePatterns(ArrayList pool, boolean debug) {
  ArrayList finals = new ArrayList();
  StringBuffer sb = new StringBuffer();
  for (int i = 0; i < pool.size(); i++) {
    String example = (String) pool.get(i);
    while (example.compareTo("") != 0) {
      int index = Utilities.findCutPoint(example, new String[] {",", ".", ":",
                                         ";"});
      String seg = example.substring(0, index + 1);
      example = example.substring(index + 1).replaceAll("^\\s*", "");
      int stop = Utilities.stopAt(seg);
      seg = seg.substring(0, stop);
      String[] words = Tokenizer.tokenize(seg);
      sb.append(words[0] + " ");
      //if(words.length >= 2) {sb.append(words[1]+" ");}
      //if(sb.toString().indexOf(words[0]) < 0){
      //  sb.append("\\s*").append(words[0]).append(".*?").append("[,.;]");
      //}
      //if(sb.toString().indexOf(words[0]) < 0){
      //  sb.append("\\s*").append(words[0]).append(".*?").append("[,.;]");
      //}
    }
  }
  String wordstring = sb.toString().trim();
  sb = new StringBuffer();
  String[] words = Tokenizer.tokenize(wordstring);
  for (int i = 0; i < words.length; i++) {
    if (wordstring.indexOf(words[i]) != wordstring.lastIndexOf(words[i]) &&
        sb.toString().indexOf(words[i]) < 0) { //occurs at least 2 times
      sb.append("\\s*").append(words[i]).append(".*?").append("[,.;]");
    }
  }
  String temp = sb.toString();
  finals.add(new String[] {sb.toString()});
  return finals;
}*/


  /**
   * this is better than the other version
   * comma-separated subpatterns
   * grow pattern: pick one word from each subsection, if covers false positive, add more words
   * generalized by removing subpatterns
   * remove covered examples from the pool
   * @param pool
   * @param debug
   * @return
   */
  private ArrayList generateMultiplePatterns(ArrayList pool, boolean debug) {
    ArrayList finals = new ArrayList();
    while (pool.size() != 0) {
      String example = (String) pool.get(0);
      String[] pt = new String[] {
          ""};
      String[] ptcopy = null;
      //while((pt = goodMultiplePattern(pt)) == null){
      //ptcopy = (String[])pt.clone();
      pt = specializeMP(pt, example); //specialize multiple pattern
      //if(pt == ptcopy){//can't specialize
      //  break;
      //}
      //}
      ptcopy = (String[]) pt.clone();
      if ( (pt = goodMultiplePattern(pt)) == null) {
        System.err.println("The most specialized pattern from this example still covers false positives. Annotation may be wrong:[" +
                           example + "]");
      }
      //while((pt = goodMultiplePattern(pt)) != null){
      //  ptcopy = (String[])pt.clone();
      //  pt = generalizeMP(pt);
      //}
      if (pt != null) {
        finals.add(pt);
        int sz = pool.size();
        pool = removeCovered(pool, pt);
        if (pool.size() == sz) {
          System.err.println(
              "size of pool is not reducing, causing infinite loop");
        }
      }
      else {
        pool = removeCovered(pool, ptcopy);
      }
    }
    finals = generalizeMP(finals);
    return finals;
  }

  /**
   * for each candidate
   * @param candidates
   * @param example
   * @return
   */
  private String[] specializeMP(String[] candidates, String example) {
    StringBuffer sb = new StringBuffer();

    while (example.compareTo("") != 0) {
      int index = learning.Utilities.findCutPoint(example, new String[] {",",
                                                  ".", ";"});
      if (index < 0) {
        System.err.println("element ends without a punctuation mark: [" +
                           example + "]");
      }
      //String word = Tokenizer.tokenize(example)[0];
      Pattern p = Pattern.compile("\\s*(\\w+)\\s+[()0-9a-zA-Z]+.*"); //at least 2 words in the clause
      Matcher m = p.matcher(example);
      String word = null;
      if (m.lookingAt()) {
        word = m.group(1).replaceAll("\\d", "");
      }
      if (word != null && word.compareTo("") != 0 &&
          Tokenizer.stop.indexOf(word) < 0) {
        sb.append("\\s*").append(word).append("\\s.*?").append(example.charAt(
            index));
      }
      example = example.substring(index + 1).replaceFirst("^\\s*", "").trim();
    }
    sb.replace(sb.length() - 1, sb.length(), "[,.;:]");
    return new String[] {
        sb.toString()};
  }

  /**
   * if the pattern covers false positive cases
   * @param pattern look like "xxx\s+.*?,\s+xxx\s+.*?."
   * @return
   */
  private String[] goodMultiplePattern(String[] candidates) {
    if (candidates == null || candidates[0].compareTo("") == 0) {
      return null;
    }
    return goodPattern(candidates, "multiple");
  }

  /**
   * match at least 2 subpattern is good enough
   * @param text
   * @return
   */
  public String matchPatterns(String text) {
    Iterator it = multiplepatterns.iterator();
    while (it.hasNext()) {
      String[] patterns = (String[]) it.next();
      for (int i = 0; i < patterns.length; i++) {
        String pstring = patterns[i];
        int count = 0;
        Pattern p = null;
        Matcher m = null;
        while(pstring.compareTo("") !=0){
          int index = pstring.lastIndexOf("\\s*");
          String subp = pstring.substring(index);
          pstring = pstring.substring(0, index).trim();
          p = Pattern.compile("(^|.*?[,.:;])" + subp + ".*");
          m = p.matcher(text);
          if (m.lookingAt()) {
            count++;
          }
        }
        if(count >= 2){
          return p.pattern();
        }
      }
    }
    return null;
  }

  public ArrayList getPatterns(){
    return multiplepatterns;
  }

}