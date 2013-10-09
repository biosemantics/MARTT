package learning;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.*;
import miner.TermSemantic;
import miner.RelativePosition;
import miner.Pair;
import miner.SemanticLabel;
import visitor.ElementComposite;
import knowledgebase.Composite;

/**
 * <p>Title: SemanticClassSegmentation</p>
 * <p>Description:segment using semantic classes </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class SemanticClassSegmentation
    extends Segmentation {
  private SemanticClassSegmentationModel model = null;
  protected TermSemantic semantics = null;
  private Vector delimrules = null;
  protected String[] classes = null;
  private String classstring = null;
  protected float[][] transmatrix = null;
  protected String[] delim = null;
  protected CompoundPattern compoundpatterns = null;
  protected MultiplePattern multiplepatterns = null;
  protected RelativePosition rp = null;

  public SemanticClassSegmentation(SemanticClassSegmentationModel model,
                                   ElementComposite ec, String alg) {
    super(model, ec, alg);
    this.model = model;
    this.semantics = ( (SemanticClassSegmentationModel) model).getTermSemantic();
    this.classes = ( (SemanticClassSegmentationModel) model).getClasses();
    this.delimrules = ( (SemanticClassSegmentationModel) model).
        getDelimiterrules();
    this.transmatrix = ( (SemanticClassSegmentationModel) model).getTransmatrix();
    this.delim = ( (SemanticClassSegmentationModel) model).getDelim();
    this.compoundpatterns = ( (SemanticClassSegmentationModel) model).
        getCompoundPatterns();
    this.multiplepatterns = ( (SemanticClassSegmentationModel) model).
        getMultiplePatterns();
    this.rp = ( (SemanticClassSegmentationModel) model).
        getRP();

    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < classes.length; i++) {
      sb.append(classes[i] + " ");
    }
    this.classstring = sb.toString().trim();
  }

  /**
   *
   * @param example to be marked-up string
   * @param mode false: normal, true:debug
   * @return a vector who first element is a tag=>content hashtable, 2nd element is marked-up string
   */
  public Vector markup(String example, String filename, Composite knowledge,
                       String order, boolean debug, String kbsc, String lrp,
                       String kblrp) {

    StringBuffer elements = new StringBuffer(RelativePosition.START + " "); //used to save tags marked up so far
    //markedresult contains two elements, one is hashtable of tags and contents, the other is the flat-marked string
    Vector markedresult = new Vector();
    markedresult.add(new Hashtable());
    markedresult.add(new StringBuffer());

    String gfname = getGFName(filename);
    String text = example;
    String lasttag = "";
    String result[] = null;
    boolean merge = true;

    while (text.trim().compareTo("") != 0) { //segment successively
      String pretags = elements.toString().replaceFirst("^\\s+", " ").trim();
      result = markupASegment(text, gfname, markedresult, lasttag, order, debug,
                              knowledge, pretags, kbsc, lrp, kblrp, merge); //update markedresult
      text = result[0];
      /*
             //check for sandwich
             boolean sandwich = false;
             boolean interlaced = false;*/

      /* all sandwich found in FNCT is other-information xxx other-information
             if (lrp.indexOf("5") > 0 || kblrp.indexOf("5") > 0) {
               String[] ptags = elements.toString().trim().split(" ");
               int p = ptags.length - 2;
               String ptag = p >= 0 ? ptags[p] : null;
               if (ptag != null && ptag.compareTo(result[1]) == 0) {
                 if (sandwich(ptag, lasttag, knowledge)) {
        sandwich = true;
        markedresult = fixSandwich(lasttag, ptag, markedresult);
                 }
               }
               /*interlaced other-information: flowers other-information fruits
                if (lasttag.compareTo("other-information") == 0 &&
         result[1].compareTo("other-information") != 0) {
        if (!sandwich && interlaced(ptag, lasttag, result[1], knowledge)) {
         interlaced = true;
         markedresult = fixInterlace(ptag, lasttag, result[1], markedresult);
                  }
                }
              }*/
              if (result[1].compareTo(lasttag) != 0) {
                lasttag = result[1];
                /*if (sandwich) {
                  int cut = elements.toString().trim().lastIndexOf(" ");
                  String temp = elements.substring(0, cut + 1);
                  elements = new StringBuffer(temp);
                }
                else if (interlaced) {
                  int cut = elements.toString().trim().lastIndexOf(" ");
                  String temp = elements.substring(0, cut + 1);
        elements = new StringBuffer(temp).append(lasttag).append(" ");
                }
                else {
                  elements.append(lasttag).append(" ");
                }*/
              }
    }
    String xpath = ec.getParentTags() + "/" + ec.getTag();
    markedresult = new Verifier(markedresult, semantics, rp, knowledge, xpath,
                                classes, merge, kbsc).verify();
    //markedresult = new Verifier().verify();

    return markedresult;
  }

  /**
   * if prob(pretag, lasttag, tag) < 0.001, true
   * @param pretag
   * @param lasttag other-information
   * @param tag
   * @param knowledge
   * @return
   */
  private boolean interlaced(String pretag, String lasttag, String tag,
                             Composite knowledge) {
    RelativePosition rpn = rp;
    RelativePosition krpn = knowledge == null ? null :
        knowledge.getRelativePosition();
    float lp = rpn.probability(pretag, 1, lasttag);
    float kp = krpn == null ? -1f : krpn.probability(pretag, 1, lasttag);
    float p = 1f;
    p *= Float.compare(kp, 0f) <= 0 ? lp : kp;
    lp = rpn.probability(lasttag, 1, tag);
    kp = krpn == null ? -1f : krpn.probability(lasttag, 1, tag);
    p *= Float.compare(kp, 0f) <= 0 ? lp : kp;

    if (Float.compare(p, 0.01f) <= 0) {
      System.out.println("Found a interlace: " + pretag + " " + lasttag + " " +
                         tag);
      return true;
    }
    return false;
  }

  /**
   * make lasttag pretag, update markedresult
   * @param pretag
   * @param lasttag
   * @param markedresult
   * @return
   */
  private Vector fixInterlace(String breadtag, String hamtag, String tag,
                              Vector markedresult) {
    if (breadtag.compareTo(RelativePosition.START) == 0) {
      return markedresult;
    }
    Hashtable table = (Hashtable) markedresult.get(0);
    StringBuffer sb = (StringBuffer) markedresult.get(1);

    ArrayList hamsegs = (ArrayList) table.get(hamtag);
    ArrayList breadsegs = (ArrayList) table.get(breadtag);
    //move last segs from the end of ham list and insert it to breadsegs before the last bread
    MarkedSegment ham = (MarkedSegment) hamsegs.remove(hamsegs.size() - 1);
    //insert ham in breadsegs
    MarkedSegment bread = (MarkedSegment) breadsegs.remove(breadsegs.size() -
        1);
    String newbread = bread.getSegment() + " " + ham.getSegment();
    ArrayList hamlabels = ham.getLabel();
    Iterator it = hamlabels.iterator();
    while (it.hasNext()) {
      SemanticLabel lb = (SemanticLabel) it.next();
      lb.setTag(breadtag);
      lb.setType("X");
    }
    ArrayList labels = bread.getLabel();
    labels.addAll(hamlabels);
    MarkedSegment fixed = new MarkedSegment();
    fixed.setSegment(newbread);
    fixed.setLabels(labels);
    breadsegs.add(fixed);
    if (hamsegs.size() == 0) {
      table.remove(hamtag);
    }
    else {
      table.put(hamtag, hamsegs);
    }
    table.put(breadtag, breadsegs);

//now fix sb:
    String marked = sb.toString().trim();
    Pattern p = Pattern.compile("(.*?)</" + breadtag + "><" + hamtag +
                                ">(.*?)</" + hamtag + ">(<" + tag +
                                ">.*</" + tag + ">)\\z");
    Matcher m = p.matcher(marked);
    if (m.lookingAt()) {
      marked = m.group(1) + " " + m.group(2) + "</" + breadtag + ">" + " " +
          m.group(3);
    }
    else {
      System.err.println("Impossible");
    }
    markedresult.set(1, new StringBuffer(marked));
    return markedresult;

  }

  /**
   * check to see if "pretag lasttag pretag" is a rare sequence
   * @param pretag
   * @param tag
   * @return
   */
  private boolean sandwich(String pretag, String lasttag, Composite knowledge) {
    if (pretag.compareTo("other-information") == 0) {
      return false;
    }
    RelativePosition rpn = rp;
    RelativePosition krpn = knowledge == null ? null :
        knowledge.getRelativePosition();
    float lp = rpn.probability(pretag, 1, lasttag);
    float kp = krpn == null ? -1f : krpn.probability(pretag, 1, lasttag);
    float p = 1f;
    p *= Float.compare(kp, 0f) <= 0 ? lp : kp;
    lp = rpn.probability(lasttag, 1, pretag);
    kp = krpn == null ? -1f : krpn.probability(lasttag, 1, pretag);
    p *= Float.compare(kp, 0f) <= 0 ? lp : kp;

    if (Float.compare(p, 0.01f) <= 0) {
      System.out.println("Found a sandwich: " + pretag + " " + lasttag + " " +
                         pretag);
      return true;
    }
    return false;
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
    MarkedSegment ham = (MarkedSegment) hamsegs.remove(hamsegs.size() - 1);
    //insert ham in breadsegs
    MarkedSegment bread2 = (MarkedSegment) breadsegs.remove(breadsegs.size() -
        1); //last bread
    MarkedSegment bread1 = (MarkedSegment) breadsegs.remove(breadsegs.size() -
        1);
    String newbread = bread1.getSegment() + " " + ham.getSegment() + " " +
        bread2.getSegment();
    ArrayList hamlabels = ham.getLabel();
    Iterator it = hamlabels.iterator();
    while (it.hasNext()) {
      SemanticLabel lb = (SemanticLabel) it.next();
      lb.setTag(breadtag);
      lb.setType("X");
    }
    ArrayList labels = bread1.getLabel();
    labels.addAll(hamlabels);
    labels.addAll(bread2.getLabel());
    MarkedSegment fixed = new MarkedSegment();
    fixed.setSegment(newbread);
    fixed.setLabels(labels);
    breadsegs.add(fixed);
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

  /**
   * updated to allow multiple delimter candidates
   * mark up the first segment of text and populate markedtext hashtable
   * @param text to be marked-up text
   * @param markedresult to be updated in this function
   * @param lasttag the tag of previous segment
   * @param debug run in debug mode or not
   * @param knowlegeon turn on the access to knowledge base or not
   * @return [0]the remaining text after the first markedup segment is removed
   *         [1]the last tag
   */
  protected String[] markupASegment(String text, String genusfamilyname,
                                    Vector markedresult,
                                    String lasttag, String order, boolean debug,
                                    Composite knowledge, String pretags,
                                    String kbsc, String lrp, String kblrp,
                                    boolean merge) {
    String first = null; //the start of the segment to be processed
    String shortest = null; //the segment
    SemanticLabel tag = null;

    if (text.indexOf("<") >= 0) { //when text is already partially marked up
      Pattern p = Pattern.compile("\\s*<(.*?)>(.*)</\\1>(.*)");
      Matcher m = p.matcher(text);
      if (m.matches()) {//to be marked is marked already
        first = m.group(2);
        tag = new SemanticLabel(first, first, m.group(1), 0f, 0f, "P");
        shortest = first;
        text = m.group(3);
        if (debug) {
          System.out.println("first :*" + first + "*");
          System.out.println("rest of text :*" + text + "*");
        }
        shortest = shortest.replaceAll("^\\s*", "").trim();
        updateResult(markedresult, tag, lasttag, shortest, debug, true);
        return new String[] {
            text, tag.getTag()};
      }
      else {//
        p = Pattern.compile("(.+)(<(.*?)>.*</\\3>.*)");
        m = p.matcher(text);
        if (m.matches()) {
          String temp = m.group(1);
          int findex = learning.Utilities.findCutPoint(temp, new String[] {",",
              ".",
              ";", ":"});
          first = findex >= 0 ? temp.substring(0, findex + 1) : temp;
          if (debug) {
            System.out.println("first :*" + first + "*");
          }
          shortest = null;
          int nearest = learning.Utilities.findCutPoint(temp, delim);
          shortest = nearest < 0 ? temp : temp.substring(0, nearest + 1);
          text = text.substring(shortest.length()).trim();
        }
      }
    }
    else {
      //avoid to include leading tokens of next sentence
      int findex = learning.Utilities.findCutPoint(text, new String[] {",", ".",
          ";", ":"});
      //int findex = model.getDelimiterIndex(".", text);//. for main-structure-level
      first = findex >= 0 ? text.substring(0, findex + 1) : text;
      if (debug) {
        System.out.println("first :*" + first + "*");
      }
      //find the segment and the remaining text
      shortest = null;
      int nearest = learning.Utilities.findCutPoint(text, delim);
      shortest = nearest < 0 ? text : text.substring(0, nearest + 1);
      text = text.substring(shortest.length()).trim();
    }

    //decide on the tag
    if (alg.compareTo("SCCPI") == 0) {
      tag = instanceBasedMatch(first, genusfamilyname);
    }
    else {
      tag = new SemanticLabel();
      tag.setConf(0f);
    }

    if (Float.compare(tag.getConf(), 0.7f) < 0) {
      boolean pmatch = false;
      if (alg.indexOf("SCCP") >= 0 && !isForeignTag("compound")) {
        //see if it is a compound
        //1. collect items
        String items = "";
        Pattern p1 = Pattern.compile("^((?:\\s*\\w+\\s*,){1,})\\s+and\\s+(\\w+).*");
        Pattern p2 = Pattern.compile("^\\s*(\\w+)\\s+and\\s+(\\w+)(.*)");
        Matcher m = p1.matcher(shortest+" "+text);
        if(m.lookingAt()){
          items += " "+m.group(1).replaceAll(",", " ");
          items += " "+m.group(2);
        }else{
          m = p2.matcher(shortest+" "+text);
          if(m.lookingAt()){
            items += " "+m.group(1)+" "+m.group(2);
          }
        }
        //2. determine if items belong to the same element
        items = items.replaceFirst("^\\s*", "").trim();
        if(items.compareTo("") != 0){
        String[] item = items.split("\\s+");
        String tagstr ="";
        for(int i = 0; i <item.length; i++){
          SemanticLabel[] tags1 = query4Labels(item[i], knowledge, 1, kbsc);
          String tagtxt = tags1 != null && tags1[0] != null? tags1[0].getTag() : "";
          tagstr += tagstr.indexOf(tagtxt) >=0 ? "" : " "+tagtxt+" ";
        }
        if(tagstr.replaceFirst("^\\s*", "").trim().split("\\s+").length > 1){
          //multiple tags, compound
          tag = new SemanticLabel(first, items, "compound", 0f, 0f, "P");
          String temp = shortest+" "+text;
          int nearest = learning.Utilities.findCutPoint(temp, delim);
          shortest = nearest < 0 ? temp : temp.substring(0, nearest + 1);
          text = temp.substring(shortest.length()).trim();
          pmatch = true;
        }
        //not to learn patterns for compound and multiple, regexp are too time comsuming
        /*String pstring = compoundpatterns.matchPatterns(first);
        if (pstring != null) {
          tag = new SemanticLabel(first, pstring, "compound", 0f, 0f, "P");
          pmatch = true;
        }
        else if ( (pstring = multiplepatterns.matchPatterns(first)) != null) {
          tag = new SemanticLabel(first, pstring, "multiple", 0f, 0f, "P");
          pmatch = true;
        }*/
        }
      }
      if (!pmatch) {
          String[] tokens = Tokenizer.tokenize(first, semantics.getUseStopList());
        if (tokens == null || tokens.length < 1) {
          tag = defaultTag(first, lasttag, knowledge, markedresult, debug);
        }
        else {
          StringBuffer ngram = new StringBuffer();
          int n = semantics.getN() > tokens.length ? tokens.length :
              semantics.getN();
          for (int i = 0; i < n; i++) {
            ngram.append(tokens[i] + " ");
          }

          SemanticLabel[] tags = null;
          SemanticLabel[] etags = null;
          //make changes only to "kbsc" to test function "verify"
          //save tags, and not tag in the markedresult 8/8/05
          //if (kbsc.compareTo("kbsc") == 0) {
            tags = query4Labels(ngram.toString(), knowledge, n, kbsc);
            if (debug && tags != null) {
              for (int i = 0; i < tags.length; i++) {
                System.out.println(ngram.toString() + " IS " +
                                   (tags[i] == null ? "null" : tags[i].toString()));
              }
            }

            /*8/8/05 tag = tags == null || tags[0] == null ?
                defaultTag(first, lasttag, knowledge, markedresult, debug) :
                tags[0];

                         tag = isForeignTag(tag.getTag()) ?
             defaultTag(first, lasttag, knowledge, markedresult, debug) : tag;*/
            /**/
            if (tags == null || tags[0] == null || isForeignTag(tags[0].getTag())) {
              tag = defaultTag(first, lasttag, knowledge, markedresult, debug);
            }
            else {
              tag = tags[0];
              String alts = "";
              for (int i = 0; i < tags.length; i++) {
                String alt = tags[i] == null ? " " + tags[i].getTag() : "";
                alts += alt;
              }
              //type contains info on the source of primary tag and a list of sorted alternative tags
              tag.setType(tag.getType() + alts);
            }
            /**/
          //}
          /*else if (kbsc.compareTo("lsc") == 0) {
            etags = semantics.semanticClassFor(ngram.toString().
                                               trim(), 0.8f, 0f, n); //0.01
            if (lrp.indexOf("0") > 0 || lrp.indexOf("5") > 0 ||
                kblrp.indexOf("5") > 0) {
              tag = etags == null || etags[0] == null ?
                  defaultTag(ngram.toString(), lasttag, null, null, debug) :
                  etags[0];
            }
            else {
              String tagstring = null;
              if (kblrp.indexOf("0") < 0 && lrp.indexOf("0") < 0) {
                tagstring = chooseOneTag(tags, etags,
                                         knowledge.getRelativePosition(),
                                         rp,
                                         pretags, lrp, kblrp);
              }
              else if (lrp.indexOf("0") < 0) {
                tagstring = chooseOneTag(tags, etags,
                                         null,
                                         rp,
                                         pretags, lrp, kblrp);
              }
              else {
                System.err.println("Illegal combination of " + kblrp + " " +
                                   lrp +
                                   " " + kbsc);
                System.exit(1);
              }
              tag = tagstring == null ?
                  defaultTag(ngram.toString(), lasttag, null, null, debug) :
                  new SemanticLabel("", "", tagstring, 0f, 0f, "R");

            }
          }*/
          /*else if (kbsc.compareTo("kb-l") == 0) {
            TermSemantic ts = knowledge != null ?
                knowledge.getTermSemanticFor(ec.getParentTags() + "/" +
                                             ec.getTag()) : null;
            tags = ts != null ?
                ts.semanticClassFor(ngram.toString().trim(),
                                    0.8f,
                                    0.0035f, n) : null;
            if (tags == null || tags[0] == null || isForeignTag(tags[0].getTag())) {
              etags = semantics.semanticClassFor(ngram.toString().
                                                 trim(), 0.8f, 0f, n); //0.01
              tag = etags == null || etags[0] == null ?
                  defaultTag(ngram.toString(), lasttag, null, null, debug) :
                  etags[0];
            }
            else {
              tag = tags[0];
            }
          }
          else if (kbsc.compareTo("l-kb") == 0) {
            etags = semantics.semanticClassFor(ngram.toString().
                                               trim(), 0.8f, 0f, n); //0.01
            if (etags == null || etags[0] == null) {
              TermSemantic ts = knowledge != null ?
                  knowledge.getTermSemanticFor(ec.getParentTags() + "/" +
                                               ec.getTag()) : null;
              tags = ts != null ?
                  ts.semanticClassFor(ngram.toString().trim(),
                                      0.8f,
                                      0.0035f, n) : null;
              if (tags == null || tags[0] == null) {
                tag = defaultTag(ngram.toString(), lasttag, null, null, debug);
              }
              else {
                tag = isForeignTag(tags[0].getTag()) ?
                    defaultTag(ngram.toString(), lasttag, null, null, debug) :
                    tags[0];
              }
            }
            else {
              tag = etags[0];
            }
          }
          else if (kbsc.compareTo("mix") == 0) {
            TermSemantic ts = knowledge != null ?
                knowledge.getTermSemanticFor(ec.getParentTags() + "/" +
                                             ec.getTag()) : null;
            tags = ts != null ?
                ts.semanticClassFor(ngram.toString().trim(),
                                    0.8f,
                                    0.0035f, n) : null;
            etags = semantics.semanticClassFor(ngram.toString().
                                               trim(), 0.8f, 0f, n); //0.01
            if ( (lrp.indexOf("0") > 0 && kblrp.indexOf("0") > 0) ||
                lrp.indexOf("5") > 0 || kblrp.indexOf("5") > 0) { //do not use rps
              tag = chooseOneTag(tags, etags);
              tag = tag == null ?
                  defaultTag(ngram.toString(), lasttag, null, null, debug) :
                  tag;
            }
            else { //use lrp and/or kbrp
              String tagstring = null;
              if (kblrp.indexOf("0") < 0 && lrp.indexOf("0") < 0) {
                tagstring = chooseOneTag(tags, etags,
                                         knowledge.getRelativePosition(),
                                         rp,
                                         pretags, lrp, kblrp);
              }
              else if (lrp.indexOf("0") < 0) {
                tagstring = chooseOneTag(tags, etags,
                                         null,
                                         rp,
                                         pretags, lrp, kblrp);
              }
              else {
                System.err.println("Illegal combination of " + kblrp + " " +
                                   lrp +
                                   " " + kbsc);
                System.exit(1);
              }
              tag = tagstring == null ?
                  defaultTag(ngram.toString(), lasttag, null, null, debug) :
                  new SemanticLabel("", "", tagstring, 0f, 0f, "R");
            }
          }
          else {
            System.out.println("unexpected combination methods: " + kbsc);
            System.exit(1);
          }*/
        }
      }
    }

    if (debug) {
      System.out.println("rest of text :*" + text + "*");
    }
    /**@todo replace true with parameter merge */
    shortest = shortest.replaceAll("^\\s*", "").trim();
    updateResult(markedresult, tag, lasttag, shortest, debug, merge);
    return new String[] {
        text, tag.getTag()};
  }

  private SemanticLabel[] query4Labels(String words, Composite knowledge, int n, String kbsc){
  TermSemantic ts = null;
    if(kbsc.compareTo("kbsc") == 0){
       ts = knowledge != null ?
                 knowledge.getTermSemanticFor(ec.getParentTags() + "/" +
                                              ec.getTag()) : null;

    }else if(kbsc.compareTo("lsc") == 0){
      ts = semantics;
    }
    SemanticLabel[] tags = ts != null ?
        ts.semanticClassFor(words.trim(),
                            0.8f,
                            0.0035f, n) : null;

  return tags;
  }
  /**
   * each tag gethers votes, top voted tag is chosen
   * @param kbtags
   * @param ltags
   * @return
   */
  private SemanticLabel chooseOneTag(SemanticLabel[] kbtags,
                                     SemanticLabel[] ltags) {
    SemanticLabel chosen = null;
    if (kbtags == null) {
      return ltags[0];
    }
    if (kbtags[0] == null && ltags[0] == null) {
      return null;
    }
    else if (kbtags[0] == null) {
      chosen = ltags[0];
    }
    else if (ltags[0] == null) {
      chosen = kbtags[0];
    }
    else {
      int lkb = kbtags.length;
      int ll = ltags.length;
      if (ll >= 2 && ltags[0].getTag().compareTo(ltags[1].getTag()) == 0) {
        chosen = ltags[0];
      }
      else if (lkb >= 2 &&
               kbtags[0].getTag().compareTo(kbtags[1].getTag()) == 0) {
        chosen = kbtags[0];
      }
      else {
        int weight = lkb > ll ? lkb : ll;
        Hashtable kbts = new Hashtable();
        for (int i = 0; i < lkb; i++) {
          if (kbtags[i] != null) {
            if (kbts.containsKey(kbtags[i])) {
              kbts.put(kbtags[i],
                       new Integer(weight - i +
                                   ( (Integer) kbts.get(kbtags[i])).intValue()));
            }
            else {
              kbts.put(kbtags[i], new Integer(weight - i));
            }
          }
        }

        for (int i = 0; i < ll; i++) {
          if (ltags[i] != null) {
            if (kbts.containsKey(ltags[i])) {
              kbts.put(ltags[i],
                       new Integer(weight - i +
                                   ( (Integer) kbts.get(ltags[i])).intValue()));
            }
            else {
              kbts.put(ltags[i], new Integer(weight - i));
            }
          }
        }

        int max = 0;
        Enumeration en = kbts.keys();
        while (en.hasMoreElements()) {
          SemanticLabel l = (SemanticLabel) en.nextElement();
          if (!isForeignTag(l.getTag())) {
            int v = ( (Integer) kbts.get(l)).intValue();
            if (v > max) {
              chosen = l;
              max = v;
            }
            else if (v == max) {
              if (Double.compare(l.getConf(), chosen.getConf()) > 0) {
                chosen = l;
              }
            }
          }
        }
      }
    }

    return isForeignTag(chosen.getTag()) ? null : chosen;
  }

  /**
   * use rps to select position position
   * @param tags
   * @param etags
   * @param rp
   * @param kbrp
   * @param pretags
   * @return
   */
  protected String chooseOneTag(SemanticLabel[] kbtags,
                                SemanticLabel[] ltags,
                                RelativePosition kbrp,
                                RelativePosition lrp, String pretags,
                                String lrpstr, String kblrpstr) {
    int kbl = kbtags == null ? 0 : kbtags.length;
    int ll = ltags == null ? 0 : ltags.length;
    Hashtable scoredtags = readInHash(kbtags, ltags);
    String[] previoustags = pretags.split(" ");
    Vector sorted = new Vector();
    Enumeration en = scoredtags.keys();
    while (en.hasMoreElements()) {
      String tag = (String) en.nextElement();
      int votes = ( (Integer) scoredtags.get(tag)).intValue();
      //int strong = kbl > ll ? 2 * kbl - 1 : 2 * ll - 1;
      if (lrpstr.indexOf("4") > 0 || kblrpstr.indexOf("4") > 0) {
        int strong = kbl > ll ? 2 * kbl + 1 : 2 * ll + 1;
        if ( (kbl > 1 || ll > 1) && votes >= strong) {
          return isForeignTag(tag) ? null : tag;
        }
      }
      //else {
      float score = 0f;
      if (lrpstr.indexOf("3") > 0 || lrpstr.indexOf("4") > 0 ||
          kblrpstr.indexOf("3") > 0 || kblrpstr.indexOf("4") > 0) {
        score = sequenceScore(tag, kbrp, lrp, previoustags, lrpstr, kblrpstr) *
            votes;
      }
      else {
        score = sequenceScore(tag, kbrp, lrp, previoustags, lrpstr, kblrpstr);
      }
      Pair p = new Pair(tag, score);
      insertElementByProb(sorted, p);
      //}
    }
    System.out.println("Tag candidates sorted");
    /*Enumeration enu = sorted.elements();
    while (enu.hasMoreElements()) {
      Pair p = (Pair) enu.nextElement();
      System.out.println(p.toString());
    }
    System.out.println();*/

    Pair p = sorted.size() == 0 ? null : (Pair) sorted.get(0); //may be null
    if (p == null) {
      return null;
    }
    else if (Float.compare(p.getProb(), 0.00f) <= 0) {
      return null;
    }
    else {
      String t = (String) p.getTag();
      return isForeignTag(t) ? null : t;
    }
  }

  /**
   * geological mean of the probability of three previous tags leads to label,
   * not counting other-information and not counting the immediate tag that is the same as
   * label.
   * @param label
   * @param kbrp
   * @param lrp
   * @param pretags
   * @return
   */
  private float sequenceScore(String tag, RelativePosition kbrp,
                              RelativePosition lrp, String[] previoustags,
                              String lrpstr, String kblrpstr) {
    //if (lrpstr.indexOf("1") > 0 || kblrpstr.indexOf("1") > 0) {
    if (kblrpstr.indexOf("0") < 0 || lrpstr.indexOf("1") > 0) { // if use kblrp
      //1st option is p(previous, this)
      int l = previoustags.length;
      for (int i = l - 1; i >= 0; i--) {
        String ptag = previoustags[i];
        if (ptag.compareTo(tag) != 0) {
          float lp = lrp != null ? lrp.probability(ptag, 1, tag) : -1f;
          float kp = kbrp != null ? kbrp.probability(ptag, 1, tag) : -1f;
          return Float.compare(kp, 0f) <= 0 ? lp : kp;
        }
      }
      return 0f;
    }
    else {
      int l = previoustags.length;
      int count = 0;
      float product = 1;
      int dis = l;
      for (int i = l - 1; i >= 0; i--) {
        String ptag = previoustags[i];
        if (ptag.compareTo(tag) == 0 && count == 0) {
          dis--;
          continue;
        }
        if (ptag.compareTo("other-information") != 0 && count < 3) {
          float lp = lrp != null ? lrp.probabilityInRange(ptag, dis - i, tag) :
              -1;
          float kp = kbrp != null ? kbrp.probabilityInRange(ptag, dis - i, tag) :
              -1;
          product *= Float.compare(kp, 0f) <= 0 ? lp : kp;
          count++;
        }
        //else {
        //  dis--;
        //}
      }
      if (count == 0) {
        System.err.println("sequenceScore reached unreachable count = 0 ");
      }
      return (float) Math.pow(product, 1d / (float) count);
    }
  }

  private void insertElementByProb(Vector elements, Pair pair) {
    int size = elements.size();
    if (size == 0) {
      elements.add(pair);
      return;
    }
    for (int i = 0; i < size; i++) {
      Pair p = (Pair) elements.get(i);
      if (Double.compare(p.getProb(), pair.getProb()) < 0) {
        elements.insertElementAt(pair, i);
        return;
      }
    }
    elements.add(pair);
  }

  /**
   * read all tags in kbtags and ltags in a hashtable tag=>votes
   * top tags in both arrays get more votes than the ones towards the end.
   * @param kbtags
   * @param ltags
   * @return
   */
  private Hashtable readInHash(SemanticLabel[] kbtags,
                               SemanticLabel[] ltags) {
    int lkb = 0;
    int ll = 0;
    if (kbtags != null) {
      lkb = kbtags.length;
    }
    if (ltags != null) {
      ll = ltags.length;
    }
    int weight = lkb > ll ? lkb : ll;
    Hashtable hashts = new Hashtable();
    if (kbtags != null) {
      for (int i = 0; i < lkb; i++) {
        if (kbtags[i] != null) {
          String t = kbtags[i].getTag();
          if (hashts.containsKey(t)) {
            hashts.put(t,
                       new Integer(weight - i +
                                   ( (Integer) hashts.get(t)).intValue()));
          }
          else {
            hashts.put(t, new Integer(weight - i));
          }
        }
      }
    }
    if (ltags != null) {
      for (int i = 0; i < ll; i++) {
        if (ltags[i] != null) {
          String t = ltags[i].getTag();
          if (hashts.containsKey(t)) {
            hashts.put(t,
                       new Integer(weight - i +
                                   ( (Integer) hashts.get(t)).intValue()));
          }
          else {
            hashts.put(t, new Integer(weight - i));
          }
        }
      }
    }
    return hashts;
  }

  /**
   * use rp to pick tag
   * @param rp
   * @param tags
   * @param pretags
   * @return
   */
  protected SemanticLabel likelyTagByPosition(RelativePosition kbrp,
                                              RelativePosition rp,
                                              SemanticLabel[] tags,
                                              String pretags) {
    String[] previoustags = pretags.split(" ");
    String lasttag = previoustags[previoustags.length - 1];
       if (tags == null || tags[0] == null) {
      System.out.println("[tags is null, no decision]");
      return null;
    }
    if (lasttag.compareTo("") == 0) {
      lasttag = RelativePosition.START;
    }
    Vector pairs = rp.getNeighborElements(lasttag, 1); //find all elements that appeared right after lasttag
    if (pairs == null) {
      System.out.println("[rp is null, pick tags[0]:" + tags[0] + "]");
      return tags[0];
    }
    pairs.add(0, new Pair(lasttag, 0f)); //it is most likely to continue with the lasttag.
    //
    System.out.print("semantic tag list:");
    for (int i = 0; i < tags.length; i++) {
      System.out.print(tags[0].getTag() + " ");
    }
    System.out.println();
    System.out.print("rp list:");
    Enumeration e = pairs.elements();
    while (e.hasMoreElements()) {
      System.out.print( ( (Pair) e.nextElement()).toString());
    }
    System.out.println();
    ///
    String t = null;
    //pick the tag is likely by order and by semantics
    /*Enumeration en = pairs.elements();
          while(en.hasMoreElements()){
        RelativePosition.Pair p = (RelativePosition.Pair) en.nextElement();
        String tag = p.getTag();
        if(t==null && tag.compareTo(RelativePosition.START) != 0 && tag.compareTo(RelativePosition.END) != 0){
          t = tag;
        }
        for(int i = 0; i < tags.length; i++){
          if(tag.compareTo(tags[i].getTag()) == 0){
            return tags[i];
          }
        }
          }*/
    /*for(int i = 0; i < tags.length; i++){
      if(possible(tags[i].getTag(), rp, previoustags)){
         Enumeration en = pairs.elements(); while (en.hasMoreElements()) {
        Pair p = (Pair) en.nextElement();
        String tag = p.getTag();
        float prob = p.getProb();
        if (t == null && tag.compareTo(RelativePosition.START) != 0 &&
            tag.compareTo(RelativePosition.END) != 0) {
          t = tag;
        }
        if (tag.compareTo(tags[i].getTag()) == 0 &&
            Double.compare(prob, 0d) != 0) {
          System.out.println("[likely: " + tags[i] + "]");
          return tags[i];
        }
      }
      }
        }*/
    //find most likely non-start, non-end element
    Enumeration en = pairs.elements();
    while (en.hasMoreElements()) {
      Pair p = (Pair) en.nextElement();
      String tag = (String) p.getTag();
      if (t == null && tag.compareTo(RelativePosition.START) != 0 &&
          tag.compareTo(RelativePosition.END) != 0) {
        t = tag;
      }
    }
    for (int i = 0; i < tags.length; i++) {
      if (possible(tags[i].getTag(), kbrp, rp, previoustags)) {
        return tags[i];
      }
    }
    //System.out.println("[no overlap, pick tags[0]: "+tags[0]+"]");
    //return tags[0];
    //not to return END and START
    return t == null ? tags[0] : new SemanticLabel("", "", t, 0f, 0f, "O");
  }

  /**
   * if it is possible tag appear after previoustags, return true, otherwise, return false
   * @param tag
   * @param rp
   * @param previoustags
   * @return
   */
  protected boolean possible(String tag, RelativePosition kbrp,
                             RelativePosition rp, String[] previoustags) {
    int range = previoustags.length;
    //if tag is continuing with the lasttag, return true.
    if (tag.compareTo(previoustags[previoustags.length - 1]) == 0) {
      return true;
    }
    if (tag.compareTo("other-information") != 0) { //kb.rp do not have other-information
      for (int i = 0; i < previoustags.length; i++) {
        float p = kbrp.probabilityInRange(previoustags[i], 20, tag);
        //float p = rp.probabilityInRange(previoustags[i], range, tag);//early mistakes causes more mistakes, reduce range to avoid early mistakes
        if (previoustags[i].compareTo("other-information") != 0 &&
            previoustags[i].compareTo(tag) != 0) {
          if (Double.compare(p, 0.01d) < 0) {
            return false;
          }
        }
      }
    }
    else {
      for (int i = 0; i < previoustags.length; i++) {
        float p = rp.probabilityInRange(previoustags[i], 20, tag);
        //float p = rp.probabilityInRange(previoustags[i], range, tag);//early mistakes causes more mistakes, reduce range to avoid early mistakes
        if (previoustags[i].compareTo("other-information") != 0 &&
            previoustags[i].compareTo(tag) != 0) {
          if (Double.compare(p, 0.01d) < 0) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * if all other measure failed to decide on a tag for a segment,
   * previously make it either "other-information" or the starting tag
   * 8/4/05: make it "flowers"
   * @param start
   * @param lasttag
   * @return
   */
  protected SemanticLabel defaultTag(String first, String lasttag,
                                     Composite knowledge, Vector markedresult,
                                     boolean debug) {
    SemanticLabel tag = null;
    /*first = first.replaceFirst("^\\s*", "");
    if (lasttag.compareTo("") != 0 &&
        lasttag.compareTo("other-information") != 0) {
      if (lasttag.compareTo("flowers") == 0 && first.charAt(0) >= 97) {
        tag = new SemanticLabel(first, "", "flowers", 0f, 0f,
                                "D");
      }
      else {
        //RelativePosition rp = knowledge.getRelativePosition();
        Vector candi = rp.getNeighborElements(lasttag, 1);
        if ( ( (String) candi.get(0)).compareTo("flowers") == 0 &&
            first.charAt(0) >= 97) {
          tag = new SemanticLabel(first, "", "flowers", 0f, 0f,
                                  "D");
        }
        else {
          if (first.charAt(0) >= 97) {
            tag = new SemanticLabel(first, "", lasttag, 0f, 0f,
                                    "D");
          }
          else {
            tag = new SemanticLabel(first, "", "other-information", 0f, 0f,
                                    "D");
          }
        }
      }
    }
    else if (lasttag.compareTo("") == 0) {
      int[] firsttag = Utilities.topIndices(transmatrix[0], 1);
      tag = new SemanticLabel(first, "", classes[firsttag[0] - 1], 0f,
                              0f, "D");

    }
    else if (lasttag.compareTo("other-information") == 0) {
      tag = new SemanticLabel(first, "", "other-information", 0f, 0f,
                              "D");
    }*/

    String start = first;
     if (lasttag.compareTo("") == 0) {
     int[] firsttag = Utilities.topIndices(transmatrix[0], 1);
     tag = new SemanticLabel(start, "", classes[firsttag[0] - 1], 0f,
                             0f, "D");
     }
     else {
     tag = isForeignTag("other-information") ?
         new SemanticLabel(start, "", lasttag, 0f, 0f, "D") :
         new SemanticLabel(start, "", "other-information", 0f, 0f,
                           "D");
     }
    if (debug) {
      System.out.println("default tag IS:" + tag.getTag());
    }

    return tag;
  }

  /**
   * mark up the first segment of text and populate markedtext hashtable
   * @param text to be marked-up text
   * @param markedresult to be updated in this function
   * @param lasttag the tag of previous segment
   * @param debug run in debug mode or not
   * @param knowlegeon turn on the access to knowledge base or not
   * @return [0]the remaining text after the first markedup segment is removed
   *         [1]the last tag
   */
  /*protected String[] markupASegment(String text, String filename, Vector markedresult,
       String lasttag, String order, boolean debug,
                                  Composite knowledge) {
    //avoid to include leading tokens of next sentence
    int findex = learning.Utilities.findCutPoint(text, delim);
    //int findex = model.getDelimiterIndex(".", text);//. for main-structure-level
    String first = findex >=0? text.substring(0, findex+1): text;
    String[] tokens = Tokenizer.tokenize(first, true);
    SemanticLabel tag = null;
    if (tokens == null || tokens.length < 1) {
      tag = new SemanticLabel(first, "", lasttag, 0d, 0d, "D");
    }
    else {
      StringBuffer ngram = new StringBuffer();
      int n = miner.TermSemantic.n > tokens.length? tokens.length : miner.TermSemantic.n;
      for (int i = 0; i < n; i++) {
        ngram.append(tokens[i]+ " ");
      }
      //use semantic info: pick the first tag
      SemanticLabel[] fulltags = semantics.semanticClassFor(ngram.toString().trim(), 0.8, 0);
      //ElementComposite parent = ec.getParent();
      //SemanticLabel[] partags = null;
      //if(parent != null){
      //  TermSemantic parsemantics = ( (SemanticClassSegmentationModel) parent.
      //                               getModel()).getTermSemantic();
      //  partags = parsemantics.semanticClassFor(ngram.toString().trim(),
      //      0.8, 0);
      //}
      //SemanticLabel[] tags = leadingTags(fulltags, partags, ngram.toString());
      SemanticLabel[] tags = fulltags;
      //if(tags[0] == null){
      //  tags = semantics.semanticClassFor(ngram.toString().trim().toLowerCase(), 0.8, 0);
      //}
      SemanticLabel[] etags = null;
      if (tags[0] == null) { //not seen ngram in training data: no semantic class found for ngram
        //System.err.println("");
        //etags = knowledge != null ? leadingTags(knowledge.semanticClassFor(ngram.toString().trim(), 0.9, 0.01), null) : null;
        TermSemantic ts = knowledge != null ? knowledge.getTermSemanticFor(ec.getParentTags()+"/"+ec.getTag()): null;
        etags = ts != null ? ts.semanticClassFor(ngram.toString().trim(),
                                           0.9,
                                           0.01) : null;
        if (etags != null && etags[0] != null) {
          tag = etags[0];
        }
        else {
          if (lasttag.compareTo("") == 0) {
            int[] firsttag = Utilities.topIndices(transmatrix[0], 1);
            //tag = classes[firsttag[0] - 1];
            tag = new SemanticLabel(ngram.toString(),"", classes[firsttag[0] - 1], 0d, 0d, "F");
          }
          else {
            //tag = lasttag;
       tag = new SemanticLabel(ngram.toString(),"", lasttag, 0d, 0d, "D");
          }
        }
      }
      //else if (hasDifferentTags(tags)) { //tags contains different labels
      //  etags = knowledge!= null ? knowledge.semanticClassFor(ngram.toString().trim(), 0.9, 0.01, true) : null;
      //  if (etags != null && etags[0] != null) {
      //    tag = findAgreed(tags, etags);
      //    tag = tag == null ? tags[0] : tag;
      //  }
      //  else {
      //    tag = tags[0];
      //  }
      //}
      else {
        tag = tags[0];
      }
      if (isForeignTag(tag.getTag())) {
        if (lasttag.compareTo("") == 0) {
       String label = classes[Utilities.topIndices(transmatrix[0], 1)[0] - 1];
          tag = new SemanticLabel(ngram.toString(), "", label, 0d, 0d, "F");
        }
        else {
          String label = lasttag;
       tag = new SemanticLabel(ngram.toString(), "", lasttag, 0d, 0d, "D");
        }
      }
    }
    //if(model.getTagIndexInTransMatrix(tag) == 0){
    //  System.out.println();
    //}
    //@todo allow other candidates!!!
    //seg using most likely delimiter for tag
    TreeSet rules = (TreeSet) delimrules.get(model.getTagIndexInTransMatrix(tag.getTag()) -
                                             1);
    Rule rule = (Rule) rules.first();
    int index = model.getDelimiterIndex(rule.body, text);
    String seg = "";
    if (index != -1) {
      seg = text.substring(0, 1 + model.getDelimiterIndex(rule.body, text));
    }
    else {
      seg = text;
    }
    String newstart = text.substring(seg.length());
    text = newstart.trim();
    if (debug) {
      System.out.println("rest of text :*" + text + "*");
    }
    updateResult(markedresult, tag, lasttag, seg, debug, true);
    return new String[] {
    text, tag.getTag()};
     }*/
  /**
   * find most likely semantic classes from semantic classes from self and parent
   * ignore semantic classes of self from parent,
   * for example, if self is leaves and parent semantic class is also leaves, ignore it
   * @param self
   * @param parent
   * @return
   */
  protected SemanticLabel[] leadingTags(SemanticLabel[] self,
                                        SemanticLabel[] parent, String ngram) {
    return self;
    /**@todo for now just return self**/
  }

  /*protected String[] leadingTags(String[] self, String[] parent){
    ArrayList tags = new ArrayList();
    String selftag = ec.getTag();
    if(parent == null){
      for(int i = 0; i < self.length; i++){
        if (self[i].compareTo("") != 0) {
          tags.add(self[i]);
        }
      }
    }else{
      int slen = self.length;
      int plen = parent.length;
      if (slen != plen) {
        System.err.println(
            "the numbers of self and parent semantic classes are not equal");
        System.exit(1);
      }
      for (int i = 0; i < slen; i++) {
        if (self[i].compareTo("") != 0) {
          tags.add(self[i]);
        }
       if (parent[i].compareTo("") != 0 && parent[i].compareTo(selftag) != 0) {
          tags.add(parent[i]);
        }
      }
    }
    return (String[])tags.toArray(new String[1]);
     }*/


  protected boolean isForeignTag(String tag) {
    if (tag.compareTo("") == 0) {
      return true;
    }
    if (classstring.indexOf(tag) >= 0) {
      return false;
    }
    return true;
  }

  private boolean hasDifferentTags(String[] tags) {
    String tag = tags[0];
    for (int i = 1; i < tags.length; i++) {
      if (tags[i].compareTo(tag) != 0) {
        return true;
      }
    }
    return false;
  }

  private String findAgreed(String[] tags1, String[] tags2) {
    for (int i = 0; i < tags1.length; i++) {
      for (int j = 0; j < tags2.length; j++) {
        if (tags1[i].compareTo(tags2[j]) == 0) {
          return tags1[i];
        }
      }
    }
    return null;
  }

  public static void main(String[] args) {
    /*ElementComposite ec = (ElementComposite) Serializer.readback(
        SemanticClassSegmentationModel.modelfile);
         SemanticClassSegmentation lrs = new SemanticClassSegmentation( (
        SemanticClassSegmentationModel) ec.getModel(), ec, "SC");
         String example = "Plants perennial, terrestrial, on rock, or often ";
         Vector result = lrs.markup(example, "", null, "0", false, "", "", "");
         System.out.println( ( (StringBuffer) result.get(1)).toString());*/
  }

}
