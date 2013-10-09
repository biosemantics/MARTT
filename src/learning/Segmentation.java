package learning;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.*;
import miner.SemanticLabel;
import knowledgebase.Composite;
import visitor.ElementComposite;

/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: Thesis Project, everything in this project</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public abstract class Segmentation {
  protected Model model = null;
  protected ElementComposite ec = null;
  protected Hashtable trainfilenames = null;
  protected String alg = null;

  public Segmentation() {

  }

  public Segmentation(Model model, ElementComposite ec, String alg) {
    this.model = model;
    this.ec = ec;
    this.trainfilenames = ec.getTrainFileNames();
    this.alg = alg;
  }

  public abstract Vector markup(String example, String filename,
                                Composite knowledge, String order,
                                boolean debug, String kbsc, String lrp, String kblrp);

  /**
   * from filename extract genus or family name of the plant
   * @param filename
   * @return
   */
  protected String getGFName(String filename){
    String gfname = "";
    Pattern g = Pattern.compile(".*?g_(\\w+?)[\\._].*");
    Pattern f = Pattern.compile(".*?f_(\\w+?)[\\._].*");
    Matcher m = g.matcher(filename);
    if (m.lookingAt()) {
      gfname = m.group(1);
    }
    else {
      m = f.matcher(filename);
      if (m.lookingAt()) {
        gfname = m.group(1);
      }
      else {
        gfname = filename; //if filename is already f/g name
      }
    }
    return gfname;
  }

  /**
   * use fname to fetch training examples of same genus/family from trainfilenames
   * @param fname
   * @return
   */
  protected SemanticLabel instanceBasedMatch(String text, String fname){
    SemanticLabel label = new SemanticLabel();
    ArrayList train = (ArrayList)trainfilenames.get(fname);
    if(train == null){
      label.setConf(0f);
      return label;
    }
    Iterator it = train.iterator();
    float bestscore = 0f;
    String tag = null;
    while(it.hasNext()){
      String exp = (String)it.next();
      String[] result = compare(text, exp);
      float score = Float.parseFloat(result[1]);
      if(score != Float.NaN && Float.compare(score, bestscore) > 0){
        bestscore = score;
        tag = result[0];
      }
    }
    return new SemanticLabel("","", tag, bestscore, 0f, "B");
   }

   /**
    * find in training the piece that is most similiar to text
    * @param text
    * @param training
    * @return [0]: tag [1]:similarity
    * @todo : fix this problem with "2n=144" problem. 2n=144 becomes NUMBER = NUMBER
    * and eventually "=", which is reduced to "" here, resulting a NAN support.
    */
   private String[] compare(String text, String training){
     String[] result = new String[2];
     text = text.replaceFirst("^\\s+", "").trim();
     float bestscore = 0f;
     String temp = text.replaceAll("\\p{Punct}", " ").replaceAll("^\\s+", "");
     int index = temp.indexOf(" ");
     String firstword = index > 0 ? temp.substring(0, index) : temp;
     //Pattern p = Pattern.compile(".*?<(.*?)>[^>]*?\\p{Punct}?\\s*("+firstword+".*?)</\\1>(.*)");
     Pattern p = Pattern.compile(".*?<(.*?)>([^>]*\\p{Punct})?\\s*("+firstword+".*?)</\\1>(.*)");
     Matcher m = p.matcher(training);
     while(text.compareTo("") != 0){
       if(m.lookingAt()){
         String tag = m.group(1);
         String matching = m.group(3);
         training = m.group(4);
         float score = similarity(text, matching);
         m = p.matcher(training);
         if(Double.compare(score, bestscore) > 0){
           bestscore = score;
           result[0] = tag;
         }
       }else{
         break;
       }
     }
     result[1] = ""+bestscore;
     return result;
   }
   /**
    * score the similiarity between the two string
    * @param text
    * @param matching
    * @return
    */
   private float similarity(String text, String matching){
     //Pattern p = Pattern.compile("(^.{1,})\\b\\d+.*");
     Pattern p = Pattern.compile("(^.{1,})\\b\\d+\\s*");
     Matcher m = p.matcher(text);
     if(m.matches()){
       text = m.group(1);
     }
     m = p.matcher(matching);
     if(m.matches()){
       matching = m.group(1);
     }
     text = text.replaceAll("\\p{Punct}", " ");
     matching = matching.replaceAll("\\p{Punct}", " ");
     String[] tokens = text.split("\\s+");
     String[] mtokens = matching.split("\\s+");
     int n = tokens.length > mtokens.length ? mtokens.length : tokens.length;
     n = n > 5 ? 5 : n;
     int j = 0;
     int score = 0;
     int pointer = 0;
     for(int i = 0; i < n; i++){
       for(; j < mtokens.length; j++){
         int len = tokens[i].length();
         int mlen = mtokens[j].length();
         len = len > mlen ? len - len/3 : mlen - mlen/3;
         if(tokens[i].regionMatches(true, 0, mtokens[j], 0, len)){
            score += n-i;
            pointer = j;
            break;
         }
       }
       j = pointer + 1;
     }
     return (float)2*score/(n*(n+1));
   }

  /**
   * use tag and seg to update markedresult
   * @param markedresult
   * @param tag
   * @param lasttag
   * @param seg
   *
   */
  protected void updateResult(Vector markedresult, SemanticLabel label,
                              String lasttag, String seg, boolean debug,
                              boolean merge) {
    if (seg.trim().compareTo("") == 0) {
      return;
    }
    Hashtable markedtext = (Hashtable) markedresult.get(0);
    StringBuffer sb = (StringBuffer) markedresult.get(1);
    String tag = label.getTag();
    ArrayList content = null; //arraylist of MarkedSegment

    if (markedtext.containsKey(tag)) {
      content = (ArrayList) markedtext.get(tag);
      /*if (merge && tag.compareTo(lasttag) == 0) { //neighboring text with same tag, merge!
        int last = content.size() - 1;
        MarkedSegment lastseg = (MarkedSegment) content.get(last);
        lastseg.setSegment( ( (MarkedSegment) content.get(last)).getSegment() +
                           " " + seg);
        lastseg.addLabel(label);
        int len = sb.length() - tag.length() - 3;
        sb.delete(len, sb.length()); //remove last end tag
        sb.append(" " + seg + "</" + tag +
                  ">");
      }
      else {*/
        content.add(new MarkedSegment(seg, label));
        sb.append("<" + tag + ">" +
                  seg + "</" + tag +
                  ">");
      //}
    }
    else {
      content = new ArrayList();
      content.add(new MarkedSegment(seg, label));
      sb.append("<" + tag + ">" +
                seg + "</" + tag +
                ">");
    }
    markedtext.put(tag, content);
    if (debug) {
      System.out.println("<" + tag + ">" +
                         seg + "</" + tag +
                         ">");
    }
  }

}
