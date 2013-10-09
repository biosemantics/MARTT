package learning;

import java.util.Iterator;
import java.util.TreeSet;
import miner.SemanticLabel;
import visitor.ElementComposite;

/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: Thesis Project, everything in this project</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class LeftRightLeadWordsSegmentation
    extends LeftRightSegmentation {
  public LeftRightLeadWordsSegmentation() {
    super();
  }

  public LeftRightLeadWordsSegmentation(LeftRightLeadWordsSegmentationModel m,
                                        ElementComposite ec, String alg) {
    super(m, ec, alg);
  }

  public String[] nextTaggedSegment(String text, String genusfamilyname,
                                    String lasttag, String prevtag,
                                    boolean debug) {
    String lleadwords = getLeadWords(text); //lleadwords are n tokens 0< n <=LEAD
    String tag = null;
    SemanticLabel label =null;
    if(text.indexOf("=") > 0 ){
      System.out.println();
    }
    if(alg.compareTo("LWI")==0){
      label= instanceBasedMatch(lleadwords, genusfamilyname);
    }else if(alg.compareTo("LW")==0){
      label = new SemanticLabel();
      label.setConf(0f);
    }else{
      System.err.println("expected markup algorithms LW or LWI");
      System.exit(1);
    }
    if (Float.compare(label.getConf(), 0.7f) < 0) {
      double[] lcscores1 = classScore(lleadwords);
      int[] ltops = Utilities.topIndices(lcscores1, 1);
      if (ltops[0] != -1) {
        tag = classes[ltops[0]];
      }
      else {
        if (lasttag.compareTo("") != 0) {
          tag = lasttag;
        }
        else {
          int[] firsttag = Utilities.topIndices(transmatrix[0], 1);
          tag = classes[firsttag[0] - 1];
        }
      }
    }
    else {
     tag = label.getTag();
    }
    /*seg using the shortest segement*/
    int shortest = Integer.MAX_VALUE;
    if (model.getTagIndexInTransMatrix(tag) == 0) {//rare element may appear in test examples, but not training examples
      shortest = learning.Utilities.findCutPoint(text, new String[] {".",
                                                 ";"});
    }
    else {
      TreeSet rules = (TreeSet) delimRules.get(model.getTagIndexInTransMatrix(
          tag) - 1);
      Iterator it = rules.iterator();
      while (it.hasNext()) {
        Rule rule = (Rule) it.next();
        if (Float.compare(rule.getSupport(), 0f) > 0) {
          int index = model.getDelimiterIndex(rule.body, text);
          shortest = index > 0 && index < shortest ? index : shortest;
        }
      }
    }
    shortest = shortest == Integer.MAX_VALUE ? text.length() - 1 : shortest;
    String seg = text.substring(0, 1 + shortest);

    /*seg using most likely delimitor for tag
         TreeSet rules = (TreeSet) delimRules.get(model.getTagIndexInTransMatrix(tag) -
                                             1);
         Rule rule = (Rule) rules.first();
         int index = model.getDelimiterIndex(rule.body, text);
         String seg = "";
         if (index != -1) {
      seg = text.substring(0, 1 + index);
         }
         else {
      seg = text;
         }*/
    return new String[] {
        tag, seg};
  }
}