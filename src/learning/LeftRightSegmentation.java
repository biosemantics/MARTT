package learning;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.*;
import learning.Model;
import learning.LeftRightSegmentationModel;
import visitor.ElementComposite;
import visitor.Serializer;
import miner.TermSemantic;
import miner.SemanticLabel;
import jds.collection.BinarySearchTree;
import knowledgebase.Composite;

/**
 * <p>Title: LeftRigthSegmentation</p>
 * <p>Description: implements a segmentation algorithm that relies on left side, delimitor, and right side.
 * mark up a single example, after the models are learnt</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

/**
 *
 * markup algorithm:
 *
 *
 * while the starting point is not EOF
 *      read LEAD terms and caculate for each class classScore = sum of bayesScore of each term/3.
 *      pick the N classes that have highest classScores.
 *      for each class:
 *          find its delimitorScores
 *          put all non-zero delimitors into delimitor pool
 *          from the delimitor D with highest support to bottom
 *               scan the file and stop at a delimitor D in the pool
 *               left side = the block between Ds just before the stop point
 *               find the top class Cr for the first LEAD terms at the right side of D
 *               right side = the block between D and top delimitor of Cr
 *               caculate bayesScoreLeft, bayesScoreRight
 *               boundaryScore = bayesScoreLeft * delimitorScore * (bayesScoreLeft - bayesScoreRight)
 *          keep the largest boundaryScore for the class
 *      end for
 *      of all classes, pick the one has hightest boundaryScore, label the boundary as the class.
 * end while
 */
public abstract class LeftRightSegmentation
    extends Segmentation {
  protected LeftRightSegmentationModel model = null;
  protected String[] classes = null;
  protected int lead = 3;
  protected ArrayList[] termScores = null;
  protected Vector delimRules = null;
  protected int N = 1; //# of candidate classes under consideration
  protected float[][] transmatrix = null;
  protected int[] classcount = null;
  protected float[] classprob = null;
  protected String alg= null;
  /**
   * @todo accept arguments for lead and n.
   * @param m
   */

  public LeftRightSegmentation() {

  }

  public LeftRightSegmentation(LeftRightSegmentationModel m,
                               ElementComposite ec, String alg) {
    super(m, ec, alg);
    this.model = m;
    this.classes = ( (LeftRightSegmentationModel) m).getClasses();
    this.lead = 3; //fix for now
    this.N = 2;
    /**@todo make N a variable, candidate class may be less than N*/
    this.N = N > classes.length ? classes.length : N;
    this.termScores = ( (LeftRightSegmentationModel) m).getScoredterms();
    this.delimRules = ( (LeftRightSegmentationModel) m).getDelimiterrules();
    this.transmatrix = ( (LeftRightSegmentationModel) m).getTransmatrix();
    int[] classcount = ( (LeftRightSegmentationModel) m).getClasscount();
    this.classprob = new float[classcount.length];
    int sum = 0;
    for (int i = 0; i < classcount.length; i++) {
      sum += classcount[i];
    }
    //System.out.println("class prob:");
    for (int i = 0; i < classcount.length; i++) {
      classprob[i] = (float) classcount[i] / sum;
      //System.out.println(classprob[i]);
    }
    this.alg = alg;
  }

  public LeftRightSegmentation(String[] classes, int lead, int N,
                               ArrayList[] termScores, Vector delimRules,
                               float[][] transmatrix, int[] classcount, String alg) {

    this.lead = lead;
    this.classes = classes;
    this.N = N > classes.length ? classes.length : N;
    this.termScores = termScores;
    this.delimRules = delimRules;
    this.transmatrix = transmatrix;
    float[] classprob = new float[classcount.length];
    int sum = 0;
    for (int i = 0; i < classcount.length; i++) {
      sum += classcount[i];
    }
    for (int i = 0; i < classcount.length; i++) {
      classprob[i] = (float) classcount[i] / sum;
    }
    this.alg = alg;
  }

  /**
   *
   * @param example to be marked-up string
   * @param mode false: normal, true:debug
   * @return a vector who first element is a tag=>content hashtable, 2nd element is marked-up string
   */
  public Vector markup(String example, String filename, Composite knowledge,
                       String order, boolean debug, String kbsc, String lrp, String kblrp) {
    //markedresult contains two elements, one is hashtable of tags and contents, the other is the flat-marked string
    Vector markedresult = new Vector();
    markedresult.add(new Hashtable());
    markedresult.add(new StringBuffer());

    //do whatever was done to preprocess annotated examples, see Frame1.openFile
    //String text = new TextPreprocessing(example).replaceSpecialChar();
    String text = example;
    String lasttag = "";
    String backuptag = "";
    String prevtag = "";
    //String lasttag = "START";
    String result[] = null;
    /**@todo after mark up need to replace back original char*/

    while (text.trim().compareTo("") != 0) { //segment successively
      String lleadwords = getLeadWords(text); //lleadwords are n tokens 0< n <=LEAD
      if (lleadwords == null) {
        break; //return an empty Hashtable
      }
      result = markupASegment(text, getGFName(filename), markedresult, prevtag,
                              lasttag, order,
                              debug, kbsc, lrp, kblrp); //update markedresult
      //result = markupBSegment(text, markedresult, lasttag, debug);
      /*if(result == null){
        return null;
             }*/
      /**@todo integrate transmatrix **/
      //result = markupBSegment(text, markedresult, lasttag, debug); //update markedresult
      text = result[0];
      if (result[1].compareTo(lasttag) != 0) {
        //when LRScore
        /*if (result[1].compareTo(prevtag) == 0 &&
            transmatrix[model.getTagIndexInTransMatrix(prevtag)][model.getTagIndexInTransMatrix(lasttag)] == 0f) {
          fixSanwich(lasttag, prevtag, markedresult); //markedresult is updated
          prevtag = backuptag;
          lasttag = prevtag;
                 }*/
        //else {
        backuptag = prevtag;
        prevtag = lasttag;
        lasttag = result[1];
        //}
        //when leadwords
        //lasttag = result[1];
      }
    }
    return markedresult;
  }

  /**
   * make hamtag breadtag, update markedresult accordingly
   * @param hamtag
   * @param breadtag
   * @param hamnum
   * @param markedresult
   */
  protected Vector fixSandwich(String hamtag, String breadtag,
                               Vector markedresult) {
    Hashtable table = (Hashtable) markedresult.get(0);
    StringBuffer sb = (StringBuffer) markedresult.get(1);

    ArrayList hamsegs = (ArrayList) table.get(hamtag);
    ArrayList breadsegs = (ArrayList) table.get(breadtag);
    //move last segs from the end of ham list and insert it to breadsegs before the last bread
    String ham = (String) hamsegs.remove(hamsegs.size() - 1);
    //insert ham in breadsegs
    String bread2 = (String) breadsegs.remove(breadsegs.size() - 1); //last bread
    String bread1 = (String) breadsegs.remove(breadsegs.size() - 1);
    String newbread = bread1 + " " + ham + " " + bread2;
    breadsegs.add(newbread);
    if (hamsegs.size() == 0) {
      table.remove(hamtag);
    }
    else {
      table.put(hamtag, hamsegs);
    }
    table.put(breadtag, breadsegs);

    //now fix sb: remove last </breadtag><hamtag> and </hamtag><breadtag>.
    String marked = sb.toString().trim();
    Pattern p = Pattern.compile("(.*?)</" + breadtag + "><" + hamtag +
                                ">(.*?)</" + hamtag + "><" + breadtag +
                                ">(.*</" + breadtag + ">)\\z");
    Matcher m = p.matcher(marked);
    if (m.lookingAt()) {
      marked = m.group(1) + " " + m.group(2) + " " + m.group(3);
    }
    else {
      System.err.println("Impossible");
    }
    markedresult.set(1, new StringBuffer(marked));
    return markedresult;
  }

  protected void printTable(Hashtable table) {
    Enumeration keys = table.keys();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      System.out.print(key + ": ");
      ArrayList text = (ArrayList) table.get(key);
      Iterator it = text.iterator();
      while (it.hasNext()) {
        System.out.println( (String) it.next());
      }
      System.out.println();
    }
  }

  /**
   * mark up a segment and return the rest of the text.
   *
   * still follow delimitor order to obtain candidate fragment
   * but final score = classscore * class tranmatrix score
   * grow current segment if
   * 1. scoring of the new block dosen't yield a candidate class, or yield the same candidate class of the current seg
   * 2. after merge, class candidate doesn't change and difference to the seconder runner doesn't decrease
   *
   * the difference not decrease => (newscore1-newscore2)/newscore2 >= (oldscore1 - oldscore2)/oldscore2
   * @param text
   * @param markedresult
   * @param lasttag
   * @param debug
   * @return
   */
  protected String[] markupBSegment(String text, Vector markedresult,
                                    String lasttag, boolean debug) {
    if (debug) {
      System.out.println();
      System.out.println();
      System.out.println("IN MARKUPBSEGMENT (text=" + text + ")");
    }
    int trials = LearnDelimiter.puncs.length;
    //save each trial's data
    Vector[] result = new Vector[trials];
    int count = 0;
    for (int t = 0; t < trials; t++) {
      String delim = LearnDelimiter.puncs[t];
      if (debug) {
        System.out.println("for delimitor " + delim);
        System.out.print("classes:");
        for (int i = 0; i < classes.length; i++) {
          System.out.print(classes[i] + " | ");
        }
        System.out.println();
      }
      result[t] = growByDelim(delim, text, lasttag, debug); //result = tag, content, score
    }
    int top = topScoreIndex(result);
    String tag = (String) result[top].get(0);
    String content = (String) result[top].get(1);
    String[] seg = saveResult(tag, content, lasttag, text, markedresult, debug);
    return seg;
  }

  protected String[] saveResult(String tag, String textseg, String lasttag,
                                String text, Vector markedresult, boolean debug) {
    Hashtable markedtext = (Hashtable) markedresult.get(0);
    StringBuffer sb = (StringBuffer) markedresult.get(1);

    ArrayList content = null;
    if (markedtext.containsKey(tag)) {
      content = (ArrayList) markedtext.get(tag);
      if (tag.compareTo(lasttag) == 0) { //neighboring text with same tag, merge!
        int last = content.size() - 1;
        content.set(last, (String) content.get(last) + " " + textseg);
        int len = sb.length() - tag.length() - 3;
        sb.delete(len, sb.length()); //remove last end tag
        sb.append(" " + textseg + "</" + tag +
                  ">");
      }
      else {
        content.add(textseg);
        sb.append("<" + tag + ">" +
                  textseg + "</" + tag +
                  ">");
      }
    }
    else {
      content = new ArrayList();
      content.add(textseg);
      sb.append("<" + tag + ">" +
                textseg + "</" + tag +
                ">");
    }
    markedtext.put(tag, content);
    text = text.substring(textseg.length());
    if (debug) {
      System.out.println("markup: " + sb.toString());
      System.out.println("remaining: " + text);
    }
    return new String[] {
        text, tag};
  }

  /**
   * using delim to seg and grow a segment
   * @param delim
   * @param text
   * @param lasttag
   * @return tag, segment, score in vector in that order
   */
  protected Vector growByDelim(String delim, String text, String lasttag,
                               boolean debug) {
    Vector result = null;
    int index = model.getDelimiterIndex(delim, text);
    String left = index >= 0 ?
        text.substring(0, index + 1) : text;
    double[] leftscore = getScore(left, lasttag);
    String leftclass = classes[Utilities.topIndices(leftscore, 1)[0]];
    String tag = leftclass;
    text = text.substring(left.length());
    boolean merge = false;
    if (text.length() > 0) {
      result = grow(left, leftscore, lasttag, tag, text, delim, debug); //result: tag, segement, score
    }
    else {
      result = new Vector();
      result.add(tag);
      result.add(left);
      Arrays.sort(leftscore);
      result.add(new Double(leftscore[leftscore.length - 1]));
    }
    return result;
  }

  protected Vector grow(String left, double[] leftscore, String lasttag,
                        String lefttag, String remaintext, String delim,
                        boolean debug) {
    //remaintext is not ""
    Vector result = new Vector();
    boolean merge = false;
    do {
      if (remaintext.trim().length() == 0) {
        break;
      }
      String right = nextSegment(remaintext, delim);
      double[] rightscore = getScore(right, lefttag);
      String rightclass = classes[Utilities.topIndices(rightscore, 1)[0]];
      double[] mergescore = getScore(left + right, lasttag);
      String mergeclass = classes[Utilities.topIndices(mergescore, 1)[0]];
      if (debug) {
        System.out.println("left:" + left);
        System.out.println(lefttag + ":" +
                           Utilities.print(leftscore));
        System.out.println("right:" + right);
        System.out.println(rightclass + ":" +
                           Utilities.print(rightscore));
        System.out.println("merge:" + left + right);
        System.out.println(mergeclass + ":" +
                           Utilities.print(mergescore));
      }

      if ( (nocandidate(rightscore) || lefttag.compareTo(rightclass) == 0) &&
          lefttag.compareTo(mergeclass) == 0) {
        merge = true;
        remaintext = remaintext.substring(right.length());
        left = left + right;
        leftscore = mergescore;
        if (debug) {
          System.out.println("Merge!");
        }
      }
      else {
        merge = false;
        if (debug) {
          System.out.println("Don't Merge!");
        }
      }
    }
    while (merge);

    result.add(lefttag);
    result.add(left);
    Arrays.sort(leftscore);
    result.add(new Double(leftscore[leftscore.length - 1]));

    return result;
  }

  protected int topScoreIndex(Vector[] result) {
    double top = -1.0;
    double value = -1.0;
    int index = -1;

    for (int i = 0; i < result.length; i++) {
      value = ( (Double) result[i].get(2)).doubleValue();
      if (top < value) {
        top = value;
        index = i;
      }
    }
    return index;
  }

  protected String nextSegment(String text, String delim) {
    if (text == null || text.trim().compareTo("") == 0) {
      return null;
    }
    int dindex = model.getDelimiterIndex(delim, text);
    if (dindex < 0) {
      return text;
    }
    return text.substring(0, dindex + 1);
  }

  /**
   * if scores are uniformly distributed, return true
   * @param score
   * @return
   */
  protected boolean nocandidate(double[] score) {
    double value = score[0];
    for (int i = 1; i < score.length; i++) {
      if (Double.compare(value, score[i]) != 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * compute finalscore from text
   * compute classscore first, then call finalScore()
   * @param text
   * @param fromtag
   * @return
   */
  protected double[] getScore(String text, String fromtag) {
    double[] classscore = classScore(text);
    String temp = text.trim();
    if (temp.length() == 0) {
      return classscore;
    }
    //String delim = temp.substring(temp.length() - 1);
    //classscore = finalScore(classscore, fromtag, delim);
    //classscore = finalScore(classscore, fromtag);
    return classscore;
  }

  /**
   * compute finalscore given classscore
   * finalscore = classscore * transmatrix * delim.support
   * @param score
   * @param fromtag
   * @param candidates
   * @return
   */
  protected double[] finalScore(double[] score, String fromtag, String delim) {
    //protected double[] finalScore(double[] score, String fromtag) {
    double[] classscore = score;
    int index = model.getTagIndexInTransMatrix(fromtag);
    float[] transscore = transmatrix[index];
    for (int i = 0; i < classscore.length; i++) {
      classscore[i] *= transscore[i + 1];
      TreeSet rules = (TreeSet) delimRules.get(i);
      float support = getSupport(rules, delim);
      classscore[i] *= support;
    }
    return classscore;
  }

  protected float getSupport(TreeSet rules, String body) {
    Iterator it = rules.iterator();
    float support = -1f;
    while (it.hasNext()) {
      Rule r = (Rule) it.next();
      if (r.body.compareTo(body) == 0) {
        support = r.support;
      }
    }
    return support;
  }

  /**
   * mark up the first segment of text and populate markedtext hashtable
   * @param text to be marked-up text
   * @param debug run in debug mode or not
   * @return [0]the remaining text after the first markedup segment is removed
   *         [1]the last tag
   */
  protected String[] markupASegment(String text, String gfname,
                                    Vector markedresult,
                                    String prevtag, String lasttag,
                                    String order, boolean debug, String kbsc, String lrp, String kblrp) {
    Hashtable markedtext = (Hashtable) markedresult.get(0);
    StringBuffer sb = (StringBuffer) markedresult.get(1);
    /**lead words only**/
    String[] nexttaggedsegment = nextTaggedSegment(text, gfname, lasttag,
        prevtag,
        debug);
    String tag = nexttaggedsegment[0];
    String seg = nexttaggedsegment[1];
    /**/
    /**lead words + content words
     */
    lasttag = tag;
    SemanticLabel label = new SemanticLabel("", "", tag, 0f, 0f, "R");
    updateResult(markedresult, label, lasttag, seg, debug, true);

    if (debug) {
      System.out.println("<" + tag + ">" +
                         seg + "</" + tag +
                         ">");
    }
    String newstart = text.substring(seg.length());
    text = newstart.trim();
    if (debug) {
      System.out.println("rest of text :*" + text + "*");
    }
    return new String[] {
        text, lasttag};
  }

  protected abstract String[] nextTaggedSegment(String text, String gfname,
                                                String lasttag,
                                                String prevtag, boolean debug);

  protected boolean filled(int[] c) {
    for (int i = 0; i < c.length; i++) {
      if (c[i] == -1) {
        return false;
      }
    }
    return true;
  }

  /**
   * find num of scores that are greater than average,
   * if there is no num of scores, fill -1
   * @param score
   * @param num
   * @return
   */

  public int[] fillCandidate(double[] scores, int num) {
    int[] cindex = new int[num];
    int[] index = new int[scores.length];
    float sum = 0f;
    for (int i = 0; i < scores.length; i++) {
      sum += scores[i];
      index[i] = i;
    }
    float avg = sum / scores.length;
    //bulble sort index
    for (int i = 0; i < scores.length; i++) {
      for (int j = i + 1; j < scores.length; j++) {
        if (scores[index[i]] < scores[index[j]]) {
          int temp = index[j];
          index[j] = index[i];
          index[i] = temp;
        }
      }
    }
    //gether scores > avg
    for (int i = 0; i < num; i++) {
      if (scores[index[i]] > avg) {
        cindex[i] = index[i];
      }
      else {
        cindex[i] = -1;
      }
    }
    return cindex;
  }

  /**
   * for -1s in tops, replace -1 with some possible class index
   * @param tops
   * @param lasttag
   * @return
   */
  protected int[] fillTopClasses(int[] tops, String lasttag) {
    int index = model.getTagIndexInTransMatrix(lasttag) - 1; //may repeat lasttag
    float[] totags = transmatrix[index + 1]; //possible to-tags from lasttag
    totags[totags.length - 1] = -1; //set "END" to -1, so it will not show in toptoindex
    int[] toptoindex = Utilities.topIndices(totags, totags.length - 2);
    int j;
    if (index == -1) { //if not from ""
      j = tops.length - 1; //fill tops from back to front
    }
    else {
      j = tops.length - 2;
      tops[tops.length - 1] = index;
    }
    int cut = 0;
    for (int i = j, k = 0; i >= 0; i--, k++) {
      if (tops[i] == -1 && totags[toptoindex[k]] > 0f) {
        tops[i] = toptoindex[k] - 1;
      }
      else if (tops[i] == -1 && totags[toptoindex[k]] <= 0f) {
        tops[i] = -1;
        cut++;
      }
    }
    int[] filled = new int[tops.length - cut];

    for (int i = 0, k = 0; i < tops.length; i++) {
      if (tops[i] != -1) {
        filled[k++] = tops[i];
      }
    }
    return filled;
    /* int index = getIndex(lasttag) - 1;
     int num = tops.length;
     if (num == 1 && index != -1) {
       tops[0] = index;
       return tops;
     }
     float[] totags = transmatrix[index + 1];
     if (index == -1) { //lasttag = ""
       //add num totag
       //return
       int[] toptoindex = Utilities.topIndices(totags, num);
       if(toptoindex[0] == -1){
         for(int i = 0; i < num; i++){
           toptoindex[i] = i+1;
         }
       }
       int neg = 0;
       for (int i = 0; i < num; i++) {
         if(toptoindex[i] - 1 < classes.length){
           tops[i] = toptoindex[i] - 1;
         }else{
           neg++;
         }
       }
       int[] candid = new int[num-neg];
       for (int i = 0, j = 0; i < num; i++) {
         if(tops[i] >= 0){
           candid[j++] = tops[i];
         }
       }
       return candid;
     }
     tops[0] = index;
     int[] toptoindex = Utilities.topIndices(totags, num - 1);
     if (toptoindex[0] == -1) {
       for (int i = 0; i < num; i++) {
         toptoindex[i] = i + 1;
       }
     }
     int neg = 0;
     for (int i = 1; i < num; i++) {
       //if the totag is "End", do not add
       if (toptoindex[i - 1] - 1 < classes.length) {
         tops[i]=toptoindex[i - 1] - 1;
       }else{
         neg++;
       }
     }
     int [] candid = new int[num-neg];
     for (int i = 0, j = 0; i < num; i++) {
       if(tops[i] >= 0){
         candid[j++]=tops[i];
       }
     }
     return candid;*/
  }

  /**
   * in the decendent order of support, for each delimiter,
   * find the classscores for its left and right segments.
   *
   * @param text
   * @param scores
   * @param rules
   * @param ltops
   * @param c
   * @param lasttag
   * @param debug
   * @return array of vectors with elements leftscores (one for each delim),
   *         rightscores, and left segments..
   */
      /*protected Vector[] scoreSegment(String text, Vector[] scores, TreeSet rules,
      int[] ltops, int c, String lasttag, boolean debug) {*/
  protected Vector[] scoreSegment(String text, Vector[] scores, TreeSet rules,
                                  String lasttag, boolean debug) {

    Iterator rit = rules.iterator();

    while (rit.hasNext()) { //each delimitor candidates
      Rule rule = (Rule) rit.next();
      if (debug) {
        System.out.println("candidate delimitor: " + rule.body + "(" +
                           rule.support + ")");
      }
      String[] threepieces = segment(rule.body, text, lasttag); //left, right, and rest, all contain some words if the end of text is not reached
      String left = threepieces[0];
      String right = threepieces[1];
      String rest = threepieces[2];
      if (debug) {
        System.out.println("left: " + left);
        System.out.println("right: " + right);
        System.out.println("rest: " + rest);
      }
      //if(left == null){//EOF is reached
      //  continue;
      //}
      double[] lcscores2 = classScore(left);
      double[] rcscores2 = classScore(right);
      if (debug) {
        for (int i = 0; i < classes.length; i++) {
          System.out.print(classes[i] + " | ");
        }
        System.out.println();
        System.out.println("left score: " + Utilities.print(lcscores2));
        System.out.println("right score: " + Utilities.print(rcscores2));
      }
      scores[0].add(lcscores2);
      scores[1].add(rcscores2);
      scores[2].add(left);

    }
    return scores;
  }

  /**
   * when segmenting text, avoiding cuts that break up a parenthesised()[] unit
   * this is done in getDelimitorIndex method
   * @param delim delimitor,
   * @param text string to be segmented
   * @return string[0] left segement (text before and include delim),
   *         string[1] rigth segment (text between a delim and another),
   *         string[2] rest (the rest of the text after taking away left)
   */
  protected String[] segment(String delim, String text, String lasttag) {
    String[] segments = new String[3];
    int dindex = model.getDelimiterIndex(delim, text);

    if (dindex < 0) { //only have left side,
      segments[0] = text;
      segments[1] = null;
      segments[2] = "";
      return segments;
    }

    segments[0] = text.substring(0, dindex + 1);
    segments[2] = text.substring(dindex + 1);
    //first lead terms from rest
    //find the most likely delimitor for the most likely class of "rest"
    String rleadwords = getLeadWords(segments[2]);
    double[] rcscores1 = classScore(rleadwords);
    int[] rtops = Utilities.topIndices(rcscores1, 1);
    if (rtops[0] == -1) {
      //rtops = fillTopClasses(rtops, lasttag);
      rtops[0] = 0;
    }
    String rdel = ( (Rule) ( (TreeSet) delimRules.get(rtops[0])).first()).body;
    dindex = model.getDelimiterIndex(rdel, segments[2]);

    if (dindex < 0) {
      segments[1] = segments[2];
    }
    else {
      segments[1] = segments[2].substring(0, dindex + 1);
    }
    return segments;
  }

  /**
   * get a string containing the first n tokens, 0<n<LEAD
   * @param a string
       * @return a string containing some tokens or null when no token exists in text
   */
  protected String getLeadWords(String text) {
    //avoid cross boundary leadwords
    int b = learning.Utilities.findCutPoint(text, new String[] {",", ".", ";",
                                            ":"});
    String first = b >= 0 ? text.substring(0, b) : text;
    String[] ltokens = Tokenizer.tokenize(first, true);
    if (ltokens == null) {
      return null;
    }
    String lleadwords = "";
    for (int i = 0; i < (lead < ltokens.length ? lead : ltokens.length); i++) {
      lleadwords = lleadwords + " " + ltokens[i];
    }
    return lleadwords;
  }

  /*
    protected int greaterThan(double [] scores, double score){
      int count = 0;
      for(int i = 0; i < scores.length; i++){
        if(Double.compare(scores[i], score) > 0){
          count++;
        }
      }
      return count;
    }*/
  /**
   * a different-based scoring
   */
  protected float[] boundaryScore1(Vector leftscores, Vector rightscores,
                                   int classid) {
    int rulesize = leftscores.size();
    float[] scores = new float[rulesize];
    for (int i = 0; i < rulesize; i++) {
      float[] left = (float[]) leftscores.get(i);
      float[] right = (float[]) rightscores.get(i);
      float max = -1f;
      int cindex = -1;
      for (int c = 0; c < left.length; c++) {
        float diff = Math.abs(left[c] - right[c]);
        if (Float.compare(diff, max) >= 0) {
          max = diff;
          cindex = c;
        }
      }
      if (cindex != classid) {
        System.out.println("Candidate " + classes[classid] + " but found " +
                           classes[cindex]);
      }
      scores[i] = max;
    }
    return scores;
  }

  /**
   * find out if an operator is a delimitor in the pool
   * @param ruleSet a sorted set of rules
   * @param operator a possible delimitor
   * @return ture or false
   */
  protected boolean existIn(TreeSet ruleSet, char operator) {
    Iterator it = ruleSet.iterator();
    while (it.hasNext()) {
      Rule r = (Rule) it.next();
      if (r.body.charAt(0) == operator) {
        return true;
      }
    }
    return false;
  }

  /**
   *
   * find class score for the string for each class
   * @param tokens is an array of words
   * @return an array of class score
   */
  protected double[] classScore(String tokenstr) {
    double[] scores = new double[classes.length];
    if (tokenstr == null) {
      return scores;
    }
    String[] tokens = Tokenizer.tokenize(tokenstr, true);
    if (tokens == null) {
      return scores;
    }
    if (alg.compareTo("NB") == 0) {
      //initialize scores
      for (int i = 0; i < classes.length; i++) {
        scores[i] = 1f;
      }
      //find p(t1, t2, ..., tn | c) = product(p(ti | c))
      for (int j = 0; j < tokens.length; j++) {
        //Term tTerm = new Term(tokens[j], 0, classes);
        Term tTerm = new Term(tokens[j]);
        for (int i = 0; i < classes.length; i++) {
          double value = 0f;
          if (termScores[i].contains(tTerm)) {
            Term term = (Term) termScores[i].get(termScores[i].indexOf(tTerm));
            value = term.getScore(i);
          }
          else { //for unseen term, estimate a value
            value = 1.0 / 1000;
          }
          scores[i] *= value;
        }
      }
      //find class score p = product(p(ti|c))*p(c)
      for (int i = 0; i < classes.length; i++) {
        scores[i] *= classprob[i];
      }
      scores = normalize(scores);
    }else {
      int scored = 0;
      float normalizer = 0f;
      for (int j = 0; j < tokens.length; j++) {
        for (int i = 0; i < classes.length; i++) {
          //Term tTerm = new Term(tokens[j], 0, classes);
          Term tTerm = new Term(tokens[j]);
          if (termScores[i].contains(tTerm)) {
            Term term = (Term) termScores[i].get(termScores[i].indexOf(tTerm));
            float value = term.getScore(i);
            if (value != 0.0f) {
              scores[i] += value;
              //normalizer += Math.pow(value,2);
              //scored++;
            }
          }
        }
      }
      for (int i = 0; i < classes.length; i++) {
        normalizer += Math.pow(scores[i], 2);
      }
      for (int i = 0; i < classes.length; i++) {
        if (normalizer != 0) {
          scores[i] = scores[i] / (float) Math.sqrt(normalizer);
        }
        else {
          scores[i] = 0;
        }
      }
    }
    return scores;
  }

  protected double[] normalize(double scores[]) {
    double normalizer = 0.0;
    for (int i = 0; i < classes.length; i++) {
      normalizer += Math.pow(scores[i], 2);
    }
    for (int i = 0; i < classes.length; i++) {
      if (normalizer != 0) {
        scores[i] = scores[i] / (float) Math.sqrt(normalizer);
      }
      else {
        scores[i] = 0;
      }
    }
    return scores;
  }

  public static void main(String[] args) {
    /*  ElementComposite ec = (ElementComposite) Serializer.readback(LeftRightSegmentationModel.modelfile);
     LeftRightSegmentation lrs = new LeftRightSegmentation((LeftRightSegmentationModel)ec.getModel(), ec);
     String example = "(Michx.) Britton, (bearded), BEARDED BEGGAR-TICKS, AWNLESS BEGGAR-TICKS,TICKSEED-SUNFLOWER. Annual or biennial, 0.3-1(-1.5) m tall;leaves 1-2-pinnate, the segments linear to lanceolate or narrowly ovate;petioles to 25 mm long;outer phyllaries 12-20, linear, 7- 25 mm long, conspicuously hispid-ciliate;ray flowers ca. 8, golden yellow;achenes flat, 5.5-7.5 mm long, 3-3.5 mm wide; pappus awns absent or slightly developed and with erect-hispid teeth. Low moist areas; Fannin and Lamar cos. in Red River drainage; mainly se and e TX. Apr-Oct.[B. polylepis S.F. Blake]";
     //String example = "Trees to 60 m tall; trunk to 2 m d.b.h.; bark dark gray, longitudinally flaking; crown conical; branchlets yellow, brownish yellow, or yellow-gray, turning gray or gray-brown in 2nd or 3rd year, initially glabrous or puberulent. Leaves ascending on upper side of branchlets, pectinately arranged in 2 lateral sets on lower side, bright green adaxially, linear, falcate or straight, 1-7 cm × 2-2.5 mm, stomatal lines in 2 light green or pale bands abaxially, rarely present adaxially when 2-4, incomplete, and almost to apex, resin canals 2, marginal, apex emarginate or acute. Seed cones initially green, yellowish green, or brownish green, brown-yellow or brown at maturity, cylindric or ovoid-cylindric, 4-14 × 3-3.5 cm.Seed scales at middle of cones broadly obtriangular- or trapeziform-flabellate, 1.7-3 × 2.2-3.5 cm, exposed part densely pubescent, margin strongly auriculate at base, constricted at middle, thin and incurved toward apex. Bracts included, 1/3-1/2 as long as seed scales, ridged adaxially, apex cuspidate. Seeds obliquely triangular, 7-9 mm; wing brown or purple-brown, 0.8-1.8 cm, margin denticulate. Pollination Apr-May, seed maturity Oct.";
     //String example = "Stems multiple, usually ascending, to 8 m, diam.10–15 cm.Fruits ripening from green through orange to reddish brown, ellipsoid, length 12–18 mm, diam. 7–8 mm. 2n = 36. ";
     //String example ="Shrubs, open spreading, to (4--)6 m.Bark light brown, smooth.Branches ascending; twigs glabrous to sparsely pubescent,  without glandular hairs.Winter buds containing inflorescences  ovoid, 3--5 × 3--4 mm, apex acute.Leaves: petiole  glabrous to moderately pubescent, without glandular hairs.Leaf  blade ovate to obovate or narrowly elliptic, often nearly  angular and lobulate near apex, 5--12 × 3.5--9 cm, base  narrowly cordate to narrowly rounded, margins coarsely and often  irregularly doubly serrate, apex usually distinctly acuminate;  surfaces abaxially glabrous to moderately pubescent, usually pubescent  on major veins and in vein axils.Inflorescences: staminate  catkins usually in clusters of 2--3, 4.5--6 × 0.5--0.8 cm;  peduncles mostly 0.5--2 mm.Nuts in clusters of 2--6;  involucral tubular beak long, narrow, 2--3(--4) times length of  nuts, densely bristly.2n = 22, 28.";
     lrs.markup(example,"", null,"0", true);
     //System.out.println(lrs.getDelimitorIndex(".","curved. x=10."));
     */
    /*int[] c = lrs.fillCandidate(new double[]{0.0, 2.0, 1.0, 0.5}, 2);
       for(int i = 0; i < c.length; i++){
      System.out.println(c[i]);
       }*/
  }
}