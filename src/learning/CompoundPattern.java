package learning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.*;

/**
 * <p>Title: CompoundPattern</p>
 * <p>Description: learn patterns from training examples for marking up compound elements</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class CompoundPattern extends PatternElement implements Serializable{
  private ArrayList compoundpatterns = null;
  public CompoundPattern(String[] trainingexamples) {
    super(trainingexamples);
  }

  /**
   * for each example
   *    generate pattern
   *    update pattern pool: make sure not false positives
   * return pattern pool
   * @param sc
   * @return
   */

  public void learnPatterns(boolean debug) {
    ArrayList pool = getElements("compound", debug);
    pool = generateCompoundPatterns(pool, debug);
    if (debug) {
      System.out.println("patterns for compound :");
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
    }
    compoundpatterns = pool;
  }

  /**
   * from the pool, pick one pattern,
   *     use the first token as a candiate pattern, try to match the patterns in the pool and other negative examples
   *     if the c-p matches any negative examples, it is too general
   *     add the second token to c-p, so on and so forth, until there is no negative examples covered.
   *     save the c-p, remove all patterns covered by c-p from the pool
   *
   * @param pool
   * @param stopwordstring
   */
  private ArrayList generateCompoundPatterns(ArrayList pool, boolean debug) {
    ArrayList goodpatterns = new ArrayList();
    while (!pool.isEmpty()) {
      String pstring = (String) pool.get(0);
      if(pstring.compareTo("Branchlets, leaves, inflorescences, calyces, and corollas glabrous.") == 0){
        System.out.print("");
      }
      pstring = pstring.replaceAll("^\\s+", "").replaceAll("\\s+", " ").trim();
      String next = pstring.substring(0, pstring.indexOf(" "));
      StringBuffer cp = new StringBuffer(next);
      pstring = pstring.substring(pstring.indexOf(next) + next.length() + 1);
      String[] goodptn = null;
      if (debug) {
        System.out.println("try to generate pattern from [" + next + "]");
      }
      boolean fail = false;
      int grow = 0;
      while ( (goodptn = goodPatternsFrom(cp.toString(), debug)) == null) {
        if (grow > 5) {
          System.err.println("[" + cp.toString() +
              "] still recovers false negative examples, it is likely an annotation mistake");
        }
        if (fail) {
          break;
        }
        //too general, add one more word to the pattern
        int nindx = pstring.indexOf(" ");
        if (nindx < 0) {
          nindx = pstring.length() - 1;
          fail = true; //all words in pstring are used, if goodPatternsFrom is still empty, then fail.
        }
        next = pstring.substring(0, nindx);
        cp = cp.append(" ").append(next);
        grow++;
        pstring = pstring.substring(pstring.indexOf(next) + next.length() + 1);
        if (debug) {
          System.out.println("try to generate pattern from [" + cp.toString() +
                             "]");
        }
      }

      if (goodptn != null && goodptn.length > 0) {
        if (debug) {
          System.out.println("found good patterns from [" + cp.toString() +
                             "]:");
          for (int i = 0; i < goodptn.length; i++) {
            System.out.println("[" + goodptn[i] + "]");
          }
        }
      }
      if (goodptn != null && goodptn.length > 0) {
        //remove patterns covered by cp, including its origin pattern
        pool = removeCovered(pool, goodptn);
        goodpatterns.add(goodptn);
      }
      else {
        //remove from the pool the bad example
        pool = removeCovered(pool, new String[] {cp.toString()});
        if (debug) {
          System.out.println("fail to find a good pattern from [" + cp.toString() +
                             "]");
        }
      }
    }
    return goodpatterns;
  }



  /**
   * find a pattern from candiate string that is generalize to the maximum
   * degree without causing any false positives
   * 1. check to see if candiate covers more than one patterns but not false positives
       * 2. try to generalize by replace specific semantic class to more generic class:
   *
   *
   * @param candidate
   * @param sc
   * @return
   */
  private String[] goodPatternsFrom(String candidate, boolean debug) {
    String[] candidates = new String[1];
    candidates[0] = candidate;
    String[] good = goodCompoundPattern(candidates);
    if (good == null) {
      //too general, candidates contain no good pattern
      if (debug) {
        System.out.println("[" + candidates[0] + "]" +
                           " covers false positive examples");
      }
      return null;
    }
    //candidate is good
    String[] copy = null;
    while (good != null) {
      copy = (String[]) good.clone();
      good = generalizeCP(good);
      if (debug) {
        System.out.println("generalized [" + candidates[0] + "] : ");
        for (int i = 0; i < good.length; i++) {
          System.out.println(good[i]);
        }
      }
      good = goodCompoundPattern(good);
      if (good != null) {
        if (debug) {
          System.out.println("good patterns among generalized : ");
          for (int i = 0; i < good.length; i++) {
            System.out.println(good[i]);
          }
        }
        copy = good;
      }
    }
    return copy;
  }


  private String[] goodCompoundPattern(String[] candidates) {
    if (candidates == null || candidates[0].compareTo("") == 0) {
      return null;
    }
    return goodPattern(candidates, "compound");
  }


  /**
   * replace each word with wildcards(\w+) and generate a set of generalized patterns
   * remove duplicates
   * returned patterns look like "petioles\\s+and\\s+\\w+" for "petioles and *"
   * @param good
   * @return
   */
  protected String[] generalizeCP(String[] good) {
    //could it be made more general?
    //"petioles and" => "petioles \w+", "\w+ and",
    StringBuffer generalized = new StringBuffer();
    for (int i = 0; i < good.length; i++) {
      StringBuffer one = wildcards(good[i]);
      String[] ones = one.toString().substring(3).split("===");
      for (int j = 0; j < ones.length; j++) {
        if (generalized.toString().indexOf(ones[j]) < 0) {
          generalized.append("===").append(ones[j]);
        }
      }
    }
    String[] patterns = generalized.toString().substring(3).split("===");
    for (int i = 0; i < patterns.length; i++) {
      patterns[i] = patterns[i].replaceAll("\\s+", " ");
      for (int t = 0; t < LearnDelimiter.puncs.length; t++) {
        patterns[i] = patterns[i].replaceAll(" " + LearnDelimiter.puncs[t] +
                                             " ",
                                             "\\\\s*" + LearnDelimiter.puncs[t] +
                                             "\\\\s*");
      }
      patterns[i] = patterns[i].replaceAll(" ", "\\\\s+");
    }
    return patterns;
  }

  /**
   * if str is A * C,
   * return {"* * C", "A * *"}
   * where * is \\w+
   * take care of A, C cases. Assume no spaces between punct and words.
   * @param str
   * @return
   */
  private static StringBuffer wildcards(String str) {
    StringBuffer sb = new StringBuffer();
    String[] tokens = str.split("\\s+");
    for (int i = 0; i < tokens.length; i++) {
      String[] copy = (String[]) tokens.clone();
      if (copy[i].compareTo("\\w+") != 0){ //&&
          //LearnDelimiter.puncstring.indexOf(copy[i]) < 0) {
        if(copy[i].matches("\\w+")){
          copy[i] = "\\w+";
        }else if(copy[i].matches(".*?\\p{Punct}$")){
          char punct = copy[i].charAt(copy[i].length()-1);
          copy[i] = "\\w+\\s*"+punct;
        }
        //copy[i] = "\\w+";
        sb.append("===");
        for (int j = 0; j < copy.length; j++) {
          String punct = copy[j];

          if(punct.matches("\\p{Alnum}+\\p{Punct}$")){
          char p = punct.charAt(punct.length()-1);
          punct = punct.replaceFirst(p+"","")+"\\s*"+p;
        }
          if (j != copy.length - 1 ) {
            sb.append(punct).append(" ");
          }
          else {
            sb.append(punct);
          }
        }
      }
    }
    return sb;
  }


  public String matchPatterns(String text){
    Iterator it = compoundpatterns.iterator();
    while(it.hasNext()){
      String[] patterns = (String[])it.next();
      for(int i = 0; i < patterns.length; i++){
        Pattern p = Pattern.compile("\\s*"+patterns[i]+".*");
        Matcher m = p.matcher(text);
        if(m.lookingAt()){
          return p.pattern();
        }
      }
    }
    return null;
  }

  public ArrayList getPatterns(){
    return compoundpatterns;
  }



}
