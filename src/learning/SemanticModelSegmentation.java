package learning;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.*;
import miner.SemanticLabel;
import miner.TermSemantic;
import miner.RelativePosition;
import miner.Pair;
import visitor.ElementComposite;
import visitor.ElementComponent;
import visitor.VisitorDoMarkup;
import knowledgebase.Composite;

/**
 * <p>Title: LeafSemanticModelSegmentation</p>
 * <p>Description: segmentation using LeafSemanticModelSegmentationModel</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

/**
 * when markup, first look at the entire sentence (ends with .) to see if the sentence match any learned model class
 * if not, look at its clauses(ends with ;) one by one
 *
 */
public class SemanticModelSegmentation
    extends SemanticClassSegmentation {
  //private SemanticClassSegmentationModel model = null;
  private Hashtable modelclasses = null;
  private RelativePosition rp = null;
  //private CompoundPattern compoundpatterns = null;
  //private MultiplePattern multiplepatterns = null;
  //private String[] delim = null;

  public SemanticModelSegmentation(SemanticModelSegmentationModel model,
                                   ElementComposite ec, String alg) {
    super(model, ec, alg);
    //this.model = model;
    this.semantics = ( (SemanticModelSegmentationModel) model).
        getTermSemantic();
    this.classes = ( (SemanticModelSegmentationModel) model).getClasses();
    this.modelclasses = ( (SemanticModelSegmentationModel) model).
        getModelClasses();
    this.compoundpatterns = ( (SemanticModelSegmentationModel) model).
        getCompoundPatterns();
    this.multiplepatterns = ( (SemanticModelSegmentationModel) model).
        getMultiplePatterns();
    this.transmatrix = ( (SemanticModelSegmentationModel) model).
        getTransmatrix();
    this.delim = ( (SemanticModelSegmentationModel) model).getDelim();
    this.rp = ( (SemanticModelSegmentationModel) model).getRP();

    /*StringBuffer sb = new StringBuffer();
         for (int i = 0; i < classes.length; i++) {
      sb.append(classes[i] + " ");
         }*/
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

  protected String[] markupASegment(String text, String genusfamilyname,
                                    Vector markedresult,
                                    String lasttag, String order, boolean debug,
                                    Composite knowledge, String pretags, String kbsc, String lrp, String kblrp) {
    if (debug) {
      System.out.println("\n\nTo mark up: " + text);
    }
    //String[] results = byPeriod(text, markedresult, lasttag, debug, knowledge);
    //if (results[0] == null) {
    //String punc = ";";
    //results = byPuncMark(text, markedresult, lasttag, debug, knowledge, punc);
    String[] results = byPuncMark(text, genusfamilyname, markedresult, lasttag,
                                  order, debug, knowledge, pretags, kbsc, lrp, kblrp);
    //}
    return results;
  }

  /**
   * 1.use word-based model, use learned semantics, may then also access to
   * knowledge base: semantic class and relative position
   * @param text
   * @param markedresult
   * @param lasttag
   * @param debug
   * @param knowledge
   * @return
   */
  private String[] byPuncMark(String text, String fname, Vector markedresult,
                              String lasttag, String order, boolean debug,
                              Composite knowledge, String pretags, String kbsc, String lrp, String kblrp) {

    //seg to find the shortest seg: first
    int findex = learning.Utilities.findCutPoint(text, delim);
    findex = findex >= 0 ? findex : text.length() - 1;
    String first = text.substring(0, findex + 1);
    String newstart = text.substring(findex + 1);
    text = newstart.trim();
    boolean attach = false; //attach newly marked to the end of previous one with the same tag?

    SemanticLabel tag = null;
    //decide tag for "first" by looking for similar text in training example
    if(alg.compareTo("SMCPI")==0){
      tag = instanceBasedMatch(first, fname);
    }else{
      tag = new SemanticLabel();
      tag.setConf(0f);
    }
    if (Float.compare(tag.getConf(), 0.7f) < 0) {
    //if instancebased method is not certain then,
    //decide tag for "first", try pattern match first, then term semantic class.
    String pstring = compoundpatterns.matchPatterns(first);
    if (pstring != null) {
      tag = new SemanticLabel(first, pstring, "compound", 0f, 0f, "P");
    }
    else if ( (pstring = multiplepatterns.matchPatterns(first)) != null) {
      tag = new SemanticLabel(first, pstring, "multiple", 0f, 0f, "P");
    }
    else {
      int stop = Utilities.stopAt(first);
      String start = first.substring(0, stop).trim();
      String[] tokens = Tokenizer.tokenize(start, true);
      if (tokens == null || tokens.length < 1) {
        tag = defaultTag(start, lasttag, null, null, debug);
        attach = true;
      }
      else {
        StringBuffer ngram = new StringBuffer();
        int n = semantics.getN() > tokens.length ? tokens.length :
            semantics.getN();
        for (int i = 0; i < n; i++) {
          ngram.append(tokens[i] + " ");
        }
        //use semantic info: find possible tags
        //TermSemantic ts = knowledge != null ?
        //  knowledge.getTermSemanticFor(ec.getParentTags() + "/" +
        //                                 ec.getTag()) : null;
        //SemanticLabel[] tags = ts != null ? ts.semanticClassFor(ngram.toString().trim(),
        //                                         0.8,
        //                                         0.0035) : null;

        SemanticLabel[] tags = semantics.semanticClassFor(ngram.toString().
            trim(),
            0.8f,
            0.0f, n);//0.01

        if (debug) {
          System.out.print("[" + ngram.toString() + "] has semantic classes:");
          for (int i = 0; i < tags.length; i++) {
            System.out.println(tags[i] == null ? "no classes" :
                               tags[i].toString());
          }
          System.out.println();
        }

        SemanticLabel[] etags = null;
        if (tags[0] == null) { //no semantic class found for ngram
          //TermSemantic ts = knowledge != null ?
          //    knowledge.getTermSemanticFor(ec.getParentTags() + "/" +
          //                                 ec.getTag()) : null;
          //etags = ts != null ? ts.semanticClassFor(ngram.toString().trim(),
          //    0.8,
          //    0.0035) : null;

          //etags = semantics.semanticClassFor(ngram.toString().
          //  trim(),
          //  0.8,
          //  0.01);

          //if (etags != null && etags[0] != null) {
            //tag = etags[0];
            //tag = likelyTagByPosition(rp, etags,
            //                           pretags);
            //if (debug) {
            //  System.out.println("knowledge base: " + tag.getTag());
            //}
            //if (isForeignTag(tag.getTag())) {
            //  tag = defaultTag(start, lasttag);
            //  attach = true;
            //}
          //}
          //else {
            //System.out.println("[" + ngram.toString() +
            //                   "]in knowledge base: null");
            tag = defaultTag(start, lasttag, null, null, debug);
            attach = true;
          //}
        }
        else { //tags[0] != null
          //boolean foreign = false;
          //if (isForeignTag(tags[0].getTag())) {
          //  tag = defaultTag(start, lasttag);
          //  attach = true;
          //  foreign = true;
          //}

          //if (first.trim().endsWith(".") || foreign) {
          //  if(!foreign){
              //tag = tags[0];
          //    tag = likelyTagByPosition(rp, tags,
          //                            pretags);
          //  }
          //}

          if (first.trim().endsWith(".")) {
            tag = tags[0];
            //tag = likelyTagByPosition(knowledge.getRelativePosition(), rp, tags,
            //                          lasttag);
          }
          else {
            Object[] match = matchModels(tags, text, modelclasses, debug); //text is the remaining text
            tag = (SemanticLabel) match[0];
            tag.setType("M");
            first = first + " " + match[1];
            text = (String) match[2];
            if (debug) {
              System.out.println("[model match]: " + tag.getTag() + " [" +
                                 first + "]");
            }
          }
          } //instanceBasedMatch
        }
      }

      if (tag.getTag().indexOf("blade") >= 0) {
        if (lasttag.indexOf("leaf-") >= 0 &&
            first.toLowerCase().indexOf("leaflet") < 0) {
          tag.setTag("leaf-blade");
        }
        else if (lasttag.indexOf("leaflet-") >= 0) {
          tag.setTag("leaflet-blade");
        }
      }
    }
    updateResult(markedresult, tag, lasttag, first, debug, attach);
    return new String[] {
        text, tag.getTag()};
  }



  /**
   * 1.use word-based model, use learned semantics, may then also access to knowledge base
   * @param text
   * @param markedresult
   * @param lasttag
   * @param debug
   * @param knowledge
   * @return
   */
  /*private String[] byPuncMark(String text, String fname, Vector markedresult,
                              String lasttag, String order, boolean debug,
                              Composite knowledge) {
    //seg to find the shortest seg: first
    int findex = learning.Utilities.findCutPoint(text, delim);
    findex = findex >= 0 ? findex : text.length() - 1;
    String first = text.substring(0, findex + 1);
    String newstart = text.substring(findex + 1);
    text = newstart.trim();
    boolean attach = false; //attach newly marked to the end of previous one with the same tag?
    SemanticLabel tag = null;
    //decide tag for "first" by looking for similar text in training example
    //tag = instanceBasedMatch(first, fname);
    //if (Double.compare(tag.getConf(), 0.7d) < 0) {
      //if instancebased method is not certain then,
      //decide tag for "first", try pattern match first, then term semantic class.
      String pstring = compoundpatterns.matchPatterns(first);
      if (pstring != null) {
        tag = new SemanticLabel(first, pstring, "compound", 0d, 0d, "P");
      }
      else if ( (pstring = multiplepatterns.matchPatterns(first)) != null) {
        tag = new SemanticLabel(first, pstring, "multiple", 0d, 0d, "P");
      }
      else {
        int stop = Utilities.stopAt(first);
        String start = first.substring(0, stop).trim();
        String[] tokens = Tokenizer.tokenize(start, true);
        if (tokens == null || tokens.length < 1) {
          tag = defaultTag(start, lasttag);
          attach = true;
        }
        else {
          StringBuffer ngram = new StringBuffer();
          int n = miner.TermSemantic.n > tokens.length ? tokens.length :
              miner.TermSemantic.n;
          for (int i = 0; i < n; i++) {
            ngram.append(tokens[i] + " ");
          }
          //use semantic info: find possible tags
          //TermSemantic ts = knowledge != null ?
          //    knowledge.getTermSemanticFor(ec.getParentTags() + "/" +
          //                                 ec.getTag()) : null;
          //SemanticLabel[] tags = ts != null ? ts.semanticClassFor(ngram.toString().trim(),
          //                                         0.8,
          //                                         0.0035) : null;
          SemanticLabel[] tags = semantics.semanticClassFor(ngram.toString().
              trim(),
              0.8,
              0.01);
           if (debug) {
       System.out.print("[" + ngram.toString() + "] has semantic classes:");
            for (int i = 0; i < tags.length; i++) {
              System.out.println(tags[i] == null ? "no classes" :
                                 tags[i].toString());
            }
            System.out.println();
          }
          SemanticLabel[] etags = null;
          if (tags[0] == null) { //no semantic class found for ngram
            TermSemantic ts = knowledge != null ?
                knowledge.getTermSemanticFor(ec.getParentTags() + "/" +
                                             ec.getTag()) : null;
            etags = ts != null ? ts.semanticClassFor(ngram.toString().trim(),
                0.8,
                0.0035) : null;
            //etags = semantics.semanticClassFor(ngram.toString().
            //  trim(),
            //  0.8,
            //  0.01);
            if (etags != null && etags[0] != null) {
              tag = etags[0];
              if (debug) {
                System.out.println("knowledge base: " + tag.getTag());
              }
              if (isForeignTag(tag.getTag())) {
                tag = defaultTag(start, lasttag);
                attach = true;
              }
            }
            else {
              System.out.println("[" + ngram.toString() +
                                 "]in knowledge base: null");
              tag = defaultTag(start, lasttag);
              attach = true;
          }
        }
        else {//tags[0] != null
          //boolean foreign = false;
          //if (isForeignTag(tags[0].getTag())) {
          //  tag = defaultTag(start, lasttag);
          //  attach = true;
          //  foreign = true;
          //}
          //if (first.trim().endsWith(".") || foreign) {
          //  if(!foreign){
          //    tag = tags[0];
          //  }
          //}
          if (first.trim().endsWith(".")) {
            tag = tags[0];
          }
          else {
            Object[] match = matchModels(tags, text, modelclasses, debug); //text is the remaining text
            tag = (SemanticLabel) match[0];
            tag.setType("M");
            first = first + " " + match[1];
            text = (String) match[2];
            if (debug) {
              System.out.println("[model match]: " + tag.getTag() + " [" +
                                 first + "]");
            }
          }
        //} //instanceBasedMatch
      }
    }
    if (tag.getTag().indexOf("blade") >= 0) {
      if (lasttag.indexOf("leaf-") >= 0 &&
          first.toLowerCase().indexOf("leaflet") < 0) {
        tag.setTag("leaf-blade");
      }
      else if (lasttag.indexOf("leaflet-") >= 0) {
        tag.setTag("leaflet-blade");
      }
    }
     }
     updateResult(markedresult, tag, lasttag, first, debug, attach);
     return new String[] {
      text, tag.getTag()};
   }*/

  /*private String[] byPeriod(String text, Vector markedresult,
                            String lasttag, boolean debug,
                            TermSemantic knowledge
                            ) {
    String punc = ".";
    int findex = model.getDelimitorIndex(punc, text);
    String first = findex >= 0 ? text.substring(0, findex + 1) : text;
    if (first.indexOf(';') < 0) { //no clause
      return byPuncMark(text, markedresult, lasttag, debug, knowledge, punc);
    }
    else { //model matching
      String tag = matchModelClass(first, debug);
      tag = null;
      if (tag == null) {
        return new String[] {
            null};
      }
      else {
        updateResult(markedresult, tag, lasttag, first, debug);
        String newstart = text.substring(first.length());
        text = newstart.trim();
        return new String[] {
            text, tag};
      }
    }
     }*/

  /**
   *
   * @param sentence
   * @return null: no match, otherwise matching tag
   */
  /* private String matchModelClass(String sentence, boolean debug) {
     String clabel = Utilities.classFor(sentence, semantics, 0.8, 0.01);
     if (clabel == null) {
       if (debug) {
         System.out.println(
             "fail to match model:no semantic class for leading ngram");
       }
       return null;
     }
     StringBuffer labels = new StringBuffer();
     while (sentence.compareTo("") != 0) {
       int i = sentence.indexOf(';');
       String clause = i >= 0 ? sentence.substring(0, i + 1) : sentence;
       sentence = sentence.substring(sentence.indexOf(clause) + clause.length()).
           trim();
       String label = Utilities.classFor(clause, semantics, 0.8, 0.01);
       if (label != null && labels.toString().indexOf(label) < 0) {
         labels.append(label).append(" ");
       }
     }
     String model = (String) modelclasses.get(clabel);
     if (debug) {
       System.out.println("match models: [" + labels.toString() + "]=>[" + model +
                          "]");
     }
     int score = scoreModel(labels.toString(), model);
     if (score > 0) {
       if (debug) {
         System.out.println("score = " + score + " match model for " + clabel);
       }
       return clabel;
     }
     else {
       if (debug) {
         System.out.println("score = " + score + " fail to match model for " +
                            clabel);
       }
       return null;
     }
   }
   private int scoreModel(String labelstring, String model) {
     String[] labels = labelstring.split(" ");
     int total = 0;
     for (int i = 0; i < labels.length; i++) {
       int indx = model.indexOf(labels[i]);
       if (indx < 0) {
         total -= 2;
       }
       else {
         indx += labels[i].length() + 1;
         String score = model.substring(indx, indx + 4);
         total += new Float(score).floatValue() > 0.1 ? 1 : -1;
       }
     }
     return total;
   }*/

  /**
   * 2.use word-based model, use knowledge base and not use learned semantics
   * @param text
   * @param markedresult
   * @param lasttag
   * @param debug
   * @param knowledge
   * @return
   */
  /*private String[] byPuncMark(String text, String fname, Vector markedresult,
                              String lasttag, String order, boolean debug,
                              Composite knowledge) {
    //seg to find the shortest seg: first
    int findex = learning.Utilities.findCutPoint(text, delim);
    findex = findex >= 0 ? findex : text.length() - 1;
    String first = text.substring(0, findex + 1);
    String newstart = text.substring(findex + 1);
    text = newstart.trim();
    boolean attach = false; //attach newly marked to the end of previous one with the same tag?
    SemanticLabel tag = null;
    if (first.compareTo("fruits with lateral wings.") == 0) {
      System.out.println();
    }
    //decide tag for "first" by looking for similar text in training example
    //tag = instanceBasedMatch(first, fname);
    //     if(Double.compare(tag.getConf(), 0.7d) < 0){
    //if instancebased method is not certain then,
    //decide tag for "first", try pattern match first, then term semantic class.
    String pstring = compoundpatterns.matchPatterns(first);
    if (pstring != null) {
      tag = new SemanticLabel(first, pstring, "compound", 0d, 0d, "P");
    }
    else if ( (pstring = multiplepatterns.matchPatterns(first)) != null) {
      tag = new SemanticLabel(first, pstring, "multiple", 0d, 0d, "P");
    }
    else {
      int stop = Utilities.stopAt(first);
      String start = first.substring(0, stop).trim();
      String[] tokens = Tokenizer.tokenize(start, true);
      if (tokens == null || tokens.length < 1) {
        tag = defaultTag(start, lasttag);
        attach = true;
      }
      else {
        StringBuffer ngram = new StringBuffer();
        int n = miner.TermSemantic.n > tokens.length ? tokens.length :
            miner.TermSemantic.n;
        for (int i = 0; i < n; i++) {
          ngram.append(tokens[i] + " ");
        }
        //query knowledge for semantic tags
        TermSemantic ts = knowledge != null ?
            knowledge.getTermSemanticFor(ec.getParentTags() + "/" +
                                         ec.getTag()) : null;
        SemanticLabel[] tags = ts != null ?
            ts.semanticClassFor(ngram.toString().trim(),
                                0.8,
                                0.0035) : null;
        if (debug) {
          System.out.print("[" + ngram.toString() + "] has semantic classes:");
          for (int i = 0; i < tags.length; i++) {
            System.out.println(tags[i] == null ? "no classes" :
                               tags[i].toString());
          }
          System.out.println();
        }
        boolean usedefault = false;
        if (tags[0] == null) { //no semantic class found for ngram
          if (debug) {
            System.out.println("[" + ngram.toString() +
                               "]in knowledge base: null");
          }
          tag = defaultTag(start, lasttag);
          attach = true;
          usedefault = true;
        }
        else { //pick the most likely tag
          tag = tags[0];
        }
        //check model? if ends with . or kb provides a foreign tag, do not check model
       if (usedefault ||first.trim().endsWith(".") || isForeignTag(tag.getTag())) {
          if (isForeignTag(tag.getTag())) {
            tag = defaultTag(start, lasttag);
            attach = true;
          }
        }
        else { //check model
          Object[] match = matchModels(tags, text, modelclasses, debug); //text is the remaining text
          tag = (SemanticLabel) match[0];
          tag.setType("M");
          //tag = new SemanticLabel(ngram.toString(), );
          first = first + " " + match[1];
          text = (String) match[2];
          if (debug) {
            System.out.println("[model match]: " + tag.getTag() + " [" +
                               first + "]");
          }
        }
      //}//instanceBasedMatch
      }
      //special case
      if (tag.getTag().indexOf("blade") >= 0) {
        if (lasttag.indexOf("leaf-") >= 0 &&
            first.toLowerCase().indexOf("leaflet") < 0) {
          tag.setTag("leaf-blade");
        }
        else if (lasttag.indexOf("leaflet-") >= 0) {
          tag.setTag("leaflet-blade");
        }
      }
    }
    updateResult(markedresult, tag, lasttag, first, debug, attach);
    return new String[] {
        text, tag.getTag()};
     }*/



  /**
   * @todo
   * @param tags
   * @param parentTags
   * @return
   */
  private SemanticLabel[] pickParentTag(SemanticLabel[] tags,
                                        SemanticLabel[] parentTags) {
    if (tags == null || tags[0] == null) {
      if (parentTags == null || parentTags[0] == null) {
        return null;
      }
      else {
        //filter current tag
        ArrayList ptags = new ArrayList();
        for (int i = 0; i < parentTags.length; i++) {
          if (parentTags[i].getTag().compareToIgnoreCase(ec.getTag()) != 0) {
            ptags.add(parentTags[i]);
          }
        }
        if (ptags.size() > 0) {
          return (SemanticLabel[]) ptags.toArray(new SemanticLabel[1]);
        }
        else {
          return null;
        }
      }
    }
    if (tags.length > 1) {
      return null;
    }
    if (parentTags == null || parentTags[0] == null) {
      return null;
    }
    return null;
  }

  /**
   * grab the chuncks of text that matches the model of a tag in tags
   * bigger chuncks win
   * @param tags
   * @param text
   * @return [0]: tag [1]: seg [2]:rest
   */
  private Object[] matchModels(SemanticLabel[] tags, String text,
                               Hashtable modelclasses, boolean debug) {
    int[] result = new int[tags.length];
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < tags.length; i++) {
      String tag = tags[i].getTag();
      if (sb.indexOf(tag + " ") < 0) {
        sb.append(tag).append(" ");
        if (tag != null && tag.compareTo("") != 0) {
          String model = (String) modelclasses.get(tag);
          if (model != null) {
            String[] nodes = model.split(" ");
            if (nodes.length <= 2) {
              //single, return tags[0]
              result[i] = 0;
            }
            else {
              //get more
              int inext = Model.getDelimiterIndex(".", text);
              String chunks = text.substring(0, inext + 1);
              String skull = getSkull(chunks, debug); //skull: word1-word2-word3(;) word1-word2-word3
              int extend = matches(nodes, skull.split(" "), debug);
              result[i] = extend;
            }
          }
          else { //no model learned, the tag
            /*int d = learning.Utilities.findCutPoint(text, delim);
                         String seg = text.substring(0,d+1);
                 return new Object[]{tags[0], seg, text.substring(seg.length()) };*/
            result[i] = 0;
          }
        }
      }
    }
    //larger extend, win
    int extend = 0;
    int tindex = 0;
    for (int i = 0; i < result.length; i++) {
      int n = result[i];
      if (debug) {
        System.out.println(tags[i].getTag() + " matched " + n + " nodes");
      }
      if (n > extend) {
        extend = n;
        tindex = i;
      }
    }
    //find the extend chunks of text
    sb = new StringBuffer();
    for (int i = 0; i < extend; i++) {
      int semicolon = Model.getDelimiterIndex(";", text);
      int period = Model.getDelimiterIndex(".", text);
      int inext = semicolon > period ? period : semicolon;
      if (inext < 0) {
        sb.append(text);
        text = "";
        break;
      }
      else {
        sb.append(text.substring(0, inext + 1));
        text = text.substring(inext + 1);
      }
    }
    return new Object[] {
        tags[tindex], sb.toString().trim(), text};
  }

  /**
   * for every ; seperated text, pick first 3 words
   * @param text
   * @return
   */
  private String getSkull(String text, boolean debug) {
    StringBuffer sb = new StringBuffer();
    String textcopy = text;
    int i = Model.getDelimiterIndex(";", text);
    while (i >= 0) {
      String seg = text.substring(0, i + 1);
      sb.append(learning.Utilities.getFirstMWords(seg, 3,"-", true) + " ");
      text = text.substring(i + 1);
      i = Model.getDelimiterIndex(";", text);
    }
    if (text.compareTo("") != 0) {
      sb.append(learning.Utilities.getFirstMWords(text, 3,"-", true) + " "); //between ; and .
    }
    if (debug) {
      System.out.println("[" + textcopy + "] has skull " + sb.toString().trim());
    }
    return sb.toString().trim();
  }

  /**
   * find out how many nodes in skull matches the model
   * @param model
   * @param skull
   * @return
   */
  private int matches(String[] model, String[] skull, boolean debug) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < skull.length; i++) {
      if (fuzzyMatch(skull[i], model, debug)) {
        sb.append(1);
        //if(debug){
        //  System.out.println(skull[i] + " matches the model");
        //}
      }
      else {
        sb.append(0);
        if (debug) {
          System.out.println(skull[i] + " does not match the model");
        }
      }
    }
    return sb.indexOf("0") < 0 ? sb.length() : sb.indexOf("0");
  }

  /**
   * match str to any element in strings
   * str: xxx-xxxxx-xxxx
   * @param str
   * @param strings
   * @return
   */
  private boolean fuzzyMatch(String str, String[] strings, boolean debug) {
    //the first word in str <=> the first word in strings[i]
    for (int i = 0; i < strings.length - 1; i = i + 2) {
      if (strings[i].toLowerCase().startsWith(str.toLowerCase())) {
        if (debug) {
          System.out.println(str + " fuzzy matched " + strings[i]);
        }
        return true;
      }
    }
    return false;
  }

  public static void main(String[] argv) {
    //System.out.println(stopAt("what( a good day."));
  }
}
