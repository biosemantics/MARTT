package learning;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;
import visitor.ElementComposite;

/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: Thesis Project, everything in this project</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class LeftRightContentSegmentation
    extends LeftRightSegmentation {
  public LeftRightContentSegmentation() {
    super();
  }

  public LeftRightContentSegmentation(LeftRightContentSegmentationModel m, ElementComposite ec, String alg) {
    super(m, ec, alg);
  }

  public String[] nextTaggedSegment(String text, String genusfamilyname, String lasttag,
                                    String prevtag, boolean debug) {

    String lleadwords = getLeadWords(text); //lleadwords are n tokens 0< n <=LEAD
    double[] lcscores1 = classScore(lleadwords);
    //int[] ltops = Utilities.topIndices(lcscores1, N); //top class candidate
    int[] ltops = fillCandidate(lcscores1, N); //top class candidate, may not in order
    boolean filled = false;
    if (!filled(ltops)) {
      //no top, pick N-1 most likely following tag given lasttag + lasttag
      ltops = fillTopClasses(ltops, lasttag);
      filled = true;
      //ltops[0] = 0;
      //ltops[1] = 1;
      if (debug) {
        System.err.print("fill top classes:");
        for (int c = 0; c < ltops.length; c++) {
          System.out.print(ltops[c] + " ");
        }
        System.out.println();
      }
    }
    int candid = ltops.length;
    double[] fscores = new double[candid]; //final scores for this round
    String[] ftextseg = new String[candid]; // final textsegs for this round
    for (int c = 0; c < candid; c++) { //process each class candidate
      if (debug) {
        System.out.println("\n\ncandidate class: " + classes[ltops[c]] + " " +
                           lcscores1[ltops[c]]);
      }
      TreeSet rules = (TreeSet) delimRules.get(ltops[c]);
      //collect scores for each candidate delimitor
      //segscores[0]: leftscores, 1:rightscores, 2:leftstrings
      Vector[] segscores = new Vector[3];
      segscores[0] = new Vector();
      segscores[1] = new Vector();
      segscores[2] = new Vector();
      //find scores for each delimitor
      //segscores = scoreSegment(text, segscores, rules, ltops, c, lasttag, debug);
      segscores = scoreSegment(text, segscores, rules, lasttag, debug);
      Vector leftscores = segscores[0];
      Vector rightscores = segscores[1];
      Vector leftstrings = segscores[2];

      //boundary score for each candidate delimitor
      double[] bscores = boundaryScore(leftscores, rightscores, ltops[c], rules,
                                       lasttag);
      //float[] bscores = boundaryScore1(leftscores, rightscores,ltops[c]);
      if (debug) {
        System.out.println("boundary score: " + Utilities.print(bscores));
      }
      //for by-sentence segmentation, always use the most likely delimitor
      //int[] topindex = new int[1];
      //topindex[0] = 0;
      //for broken-sentence segmentation, need to find out which delimitor scores the highest
      int num = 1;
      int[] topindex = Utilities.topIndices(bscores, num); //index of the most likely delimitor(s)
      if (topindex[0] == -1) {
        topindex[0] = 0;
        if (debug) {
          System.out.print("fill top delimiter:");
          for (int d = 0; d < num; d++) {
            System.out.print(topindex[d] + " ");
          }
          System.out.println();
        }
      }

      double bscore = bscores[topindex[0]];
      double score = bscore *
          (lcscores1[ltops[c]] == 0.0 ? 1.0 / Math.sqrt(candid) :
           lcscores1[ltops[c]] * lcscores1[ltops[c]]); //score and textseg should be saved for later comparison
      if (debug) {
        System.out.println("score: " + score);
      }
      String textseg = (String) leftstrings.get(topindex[0]);
      fscores[c] = score;
      ftextseg[c] = textseg;
    }
    String tag = null;
    int[] cindex = new int[1];
    if (candid < 2) { //one candid only occurs when only "End" is the possible next tag for lasttag.
      tag = classes[ltops[0]];
      cindex[0] = 0;
    }
    else {
      //find the indices of the two most likely class  for left string
      //cindex may have a length < 2
      cindex = Utilities.topIndices(fscores, 2);
      if (cindex[0] == -1) {
        cindex[0] = 0;
        cindex[1] = 1;
      }
      int from1, from2;
      int from = model.getTagIndexInTransMatrix(lasttag);
      from1 = from;
      from2 = from;
      int to1 = ltops[cindex[0]];
      int to2 = ltops[cindex[1]];
      if (from - 1 == to1) {
        from1 = model.getTagIndexInTransMatrix(prevtag);
      }
      if (from - 1 == to2) {
        from2 = model.getTagIndexInTransMatrix(prevtag);
      }

      float tr1 = transmatrix[from1][to1 + 1];
      float tr2 = transmatrix[from2][to2 + 1];
      if (debug) {
        System.out.println();
        System.out.println("from class index " + (from1 - 1) + " to " + to1 +
                           ": " + tr1);
        System.out.println("from class index " + (from2 - 1) + " to " + to2 +
                           ": " + tr2);
      }
      if (Double.compare(fscores[cindex[0]], fscores[cindex[1]]) >= 0 &&
          Double.compare(fscores[cindex[0]] * tr1, fscores[cindex[1]] * tr2) >=
          0) {
        tag = classes[to1]; //cindex[0] stays
      }
      else if (Double.compare(fscores[cindex[0]], fscores[cindex[1]]) >= 0 &&
               Double.compare(fscores[cindex[0]] * tr1,
                              fscores[cindex[1]] * tr2) < 0) {
        double factor = 0.5;
        if (Double.compare(tr1, 0d) == 0) {
          factor = 10d;
        }
        if (Double.compare(fscores[cindex[0]] / fscores[cindex[1]], 1 + factor) >=
            0) {
          tag = classes[to1];
        }
        else {
          tag = classes[to2];
          cindex[0] = cindex[1]; //cindex[1] takes over
        }
        //confused, fail!
        //if(debug){
        //  System.out.println("term score and order info in conflict, return null as markup result");
        //}
        //return null;
      }

      //if(lasttag.compareTo("cones") == 0) {
      //special treatment for cones in foc
      //if lasttag = cones, current tag is not in cones trans list, make current tag "cones"
      //if (transmatrix[getIndex("cones")][getIndex(tag)] == 0f) {
      //tag = "cones";
      //}
      //special treatment ends here
      //}

      if (debug) {
        if (tag.compareTo(classes[ltops[ltops.length - 1]]) == 0) {
          System.out.println("************************************************");
          System.out.println(
              "**********second candidate win!******************");
          System.out.println("************************************************");
        }
      }
    }
    String seg = ftextseg[cindex[0]];
    /*
         ArrayList content = null;
        if (markedtext.containsKey(tag)) {
        content = (ArrayList) markedtext.get(tag);
         if (tag.compareTo(lasttag) == 0) { //neighboring text with same tag, merge!
        int last = content.size() - 1;
        content.set(last, (String) content.get(last) + " " + seg);
        int len = sb.length() - tag.length() - 3;
        sb.delete(len, sb.length()); //remove last end tag
        sb.append(" " + seg + "</" + tag +
        ">");
        }
        else {
        content.add(seg);
        sb.append("<" + tag + ">" +
        seg + "</" + tag +
        ">");
        }
        }
        else {
        content = new ArrayList();
        content.add(seg);
        sb.append("<" + tag + ">" +
        seg + "</" + tag +
        ">");
        }
        markedtext.put(tag, content);
     */
    //System.out.println("<"+tag+">"+seg+"</"+tag+">");
    return new String[] {
        tag, seg};
  }

  /**
   * find the boundary score for each boundary candidate
   * @param left a vector of left class scores
   * @param right a vector of right class scores
   * @param classid candidate class
   * @param rules the sorted rules for classid, the same rules as used in scoreSegment
   * @param lasttag the immediate last tag before this round of processing
   * @return boundary score for each delimitor candidate
   */
  protected double[] boundaryScore(Vector leftscores, Vector rightscores,
                                 int classid, TreeSet rules, String lasttag) {
    Iterator rit = rules.iterator();
    double[] scores = new double[rules.size()];
    int i = 0;
    while (rit.hasNext()) {
      Rule r = (Rule) rit.next();
      float support = r.getSupport();
      double[] left = (double[]) leftscores.get(i);
      double[] right = (double[]) rightscores.get(i);
      double score = (double) Math.sqrt( (double) support) * left[classid];
      //for cases like phls, a period-ended seg may have more than one semantic components which
      //have higher score than classid. to punish this situation, devide the score by that number
      /*int num = greaterThan(left, left[classid]);
      score = score*Math.pow(score, num); didn't work*/
      //add transmatrix score
      //int[] top = Utilities.topIndices(right, 1);
      //if(top[0] == -1){
      //top = fillTopClasses(top, lasttag);
      //  top[0] = 0;
      //}
      //float transcore = classid == top[0]? 0.1f : transmatrix[classid+1][top[0]+1];
      //float transcore = transmatrix[getIndex(prevtag)][classid+1];
      //score *= transcore;

      //float score = support * left[classid] *
      //  Math.abs(left[classid] - right[classid]);//boundary score of the candidate class
      scores[i++] = score;
    }
    return scores;
  }

}