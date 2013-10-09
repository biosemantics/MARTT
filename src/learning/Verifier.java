package learning;

import miner.SemanticLabel;
import miner.RelativePosition;
import miner.TermSemantic;
import knowledgebase.Composite;
import java.util.*;
import java.util.regex.*;

/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 *
 * <p>Description: Thesis Project, everything in this project</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: UIUC</p>
 *
 * @author not attributable
 * @version 0.1
 */
public class Verifier {
  private Vector markedresult = null;
  private TermSemantic semantics = null;
  private RelativePosition rp = null;
  private TermSemantic kbsc = null;
  private RelativePosition kbrp = null;
  private String[] classes = null;
  private boolean merge = false;
  private static final Float threshold = new Float(0.20);
  private boolean action = false;
  private String kbsrc = null;


  public Verifier(Vector markedresult, TermSemantic semantics,
                  RelativePosition rp, Composite knowledge, String xpath, String[] classes, boolean merge, String kbtype) {
    this.markedresult = markedresult;
    this.semantics = semantics; //locally learned
    this.rp = rp; //locally learned
    if(knowledge !=null){
      this.kbsc = knowledge.getTermSemanticFor(xpath); //knowlegebase
      this.kbrp = knowledge.getRelativePositionFor(xpath);
    }
    this.classes = classes;
    this.merge = merge;
    this.kbsrc = kbtype;
  }

  /**
   * 1. use tag to classify the tagging of segments as "certain", "marked" and "default".
   *    certain= n-gram rule (n>=2) or two 1-gram rule
   *    marked = 1-gram rule
   *    default = default
   * 2. upgrade "marked" to "certain" if high prob. by RP to "certain" elements,
   *    or check the prob. for the alternative tags [from term semantics]: if high, change the tag to the alternative one and marked as "certain"
   *    otherwise, degrade to "default".
   * 3. for "default", find candidates by RP to "certain" elements, select candidate by voting (intersection).
   * @param markedresult Vector: [0]: tag=>arraylist of content; [1]:marked up string as string buffer
   * @return Vector
   */
  public Vector verify() {
    String marked = ( (StringBuffer) markedresult.get(1)).toString();
    /*if(marked.indexOf("(Michx.) Britton, (bearded),") >= 0){
      System.out.print("");
    }*/
    marked = marked.replaceAll(">\\s*", ">").replaceAll("\\s*<", "<");
    Hashtable tags = (Hashtable) markedresult.get(0);
    Hashtable segs = new Hashtable();//"code" => semanticlabel, text
    String encoded = encodeSegments(tags, marked, segs);

    Hashtable certains = new Hashtable();
    Hashtable taggeds = new Hashtable();
    Hashtable defaults = new Hashtable();
    Hashtable textsegs = new Hashtable();

    classify(segs, encoded, certains, taggeds, defaults, textsegs);
    if(action){
      adjustment(certains, taggeds, defaults);
      taggeds = null;
      processDefaults(certains, defaults);
      defaults = null;
    }
    updateMarkedResult(certains, textsegs);
    return markedresult;
  }

  private void updateMarkedResult(Hashtable certains, Hashtable textsegs){
    int size = certains.size();
    Hashtable content = new Hashtable();
    if(textsegs.size() != size){
      System.err.println("different numbers of text segs and tags");
    }
    String[] marked = new String[size];
    for (int i = 0; i < size; i++) {
      SemanticLabel tag = (SemanticLabel) certains.get(new Integer(i));
      String text = (String) textsegs.get(new Integer(i));
      if (content.get(tag.getTag()) == null) {
        ArrayList list = new ArrayList();
        MarkedSegment ms = new MarkedSegment(text, tag);
        list.add(ms);
        content.put(tag.getTag(), list);
      }
      else {
        ArrayList list = (ArrayList) content.get(tag.getTag());
        if (merge && i > 0 &&
            ( (SemanticLabel) certains.get(new Integer(i - 1))).getTag().
            compareTo(tag.getTag()) == 0) {
          int last = list.size() - 1;
          MarkedSegment lastseg = (MarkedSegment) list.get(last);
          lastseg.setSegment( ( (MarkedSegment) list.get(last)).getSegment() +
                             " " + text);
          lastseg.addLabel(tag);
        }else{
          MarkedSegment ms = new MarkedSegment(text, tag);
          list.add(ms);
        }
      }
      marked[i] = "<" + tag.getTag() + ">" + text + "</" + tag.getTag() + ">";
    }
    StringBuffer markedtext = new StringBuffer();
    for (int i = 0; i < marked.length; i++) {
      markedtext.append(marked[i]);
    }
    if (merge) {
      markedtext = new StringBuffer(markedtext.toString().replaceAll("</(.*?)><\\1>", " "));
    }
    markedresult.set(0, content);
    markedresult.set(1, markedtext);
  }
  private void processDefaults(Hashtable certains, Hashtable defaults){
    Enumeration en = defaults.keys();
    while(en.hasMoreElements()){
      Object[] key = (Object[])en.nextElement();
      //Float prob = (Float)defaults.get(key);
      SemanticLabel tag = (SemanticLabel)key[0];
      //ArrayList candidates = new ArrayList();
      Float max = new Float(0f);
      String label = null;
      int position = Integer.parseInt(((String)key[1]).replaceAll(" ",""));
      for(int i = 0; i < classes.length; i++){
        String temp = classes[i];
        Float prob = geoMeanProb(certains, temp, position);
        if(prob.compareTo(max) > 0){
          max = prob;
          label = classes[i];
        }
      }
      tag.setTag(label);
      certains.put(new Integer(position), tag);
    }
  }

  /**
   * adjust the placement of tags in the three catagories
   * @param certains Hashtable
   * @param taggeds Hashtable
   * @param defaults Hashtable
   */
  private void adjustment(Hashtable certains, Hashtable taggeds, Hashtable defaults){
    Enumeration en = taggeds.keys();
    while(en.hasMoreElements()){
      Object[] key = (Object[])en.nextElement();
      Float prob = (Float)taggeds.get(key);
      if(prob.compareTo(threshold) > 0){
        //upgrade to certain: position => tag
        //taggeds.put(key, null);
        int pos = Integer.parseInt(((String)(key[1])).replaceAll(" ",""));
        certains.put(new Integer(pos), (SemanticLabel)key[0]);
      }else{
        //check for alternatives
        if(!replaceByAlternative(key, taggeds, certains)){
          //downgrade to default
          //taggeds.put(key, null);
          defaults.put(key, prob);
        }
      }
    }
  }
  /**
   * if alternatives fit better in the position, add alternatives to certains
   * @param tag SemanticLabel
   * @param type String[]
   * @param taggeds Hashtable
   * @param certains Hashtable
   */
  private boolean replaceByAlternative(Object[] key, Hashtable taggeds, Hashtable certains){
    SemanticLabel tag = (SemanticLabel)key[0];
    int pos = Integer.parseInt(((String)(key[1])).replaceAll(" ",""));
    String[] type = tag.getType().split("\\s+");
    Float greatest = new Float(0);
    String label = "";
    for(int i = 1; i < type.length; i++){ //type[0] is the type itself
      Float mean = geoMeanProb(certains, type[i], pos);
      if(mean.compareTo(greatest) > 0){
        greatest = mean;
        label = type[i];
      }
    }
    if(greatest.compareTo(threshold) > 0){
      tag.setTag(label);
      certains.put(new Integer(pos), tag);
      //taggeds.put(key, null);
      return true;
    }
    return false;
  }

  /**
   * classify the labels in segs into three catagories
   * collect rp info for taggeds and defaults
   *
   * @param segs Hashtable
   * @param encoded String
   * @param certains Hashtable
   * @param taggeds Hashtable
   * @param defaults Hashtable
   */
  private void classify(Hashtable segs, String encoded, Hashtable certains,
                        Hashtable taggeds, Hashtable defaults, Hashtable textsegs) {
    String[] codes = encoded.replaceFirst("^\\s*", "").split("\\s+");
    Enumeration en = segs.keys();
    //ArrayList ce = new ArrayList();
    ArrayList ta = new ArrayList();
    ArrayList de = new ArrayList();
    //classify
    while (en.hasMoreElements()) {
      String code = (String) en.nextElement();
      Object[] obj = (Object[])segs.get(code);
      SemanticLabel tag = (SemanticLabel)obj[0];
      int position = arrayIndex(codes, code);
      textsegs.put(new Integer(position), (String)obj[1]);
      if(!action){
        certains.put(new Integer(position), tag);
      }else{
        if (rank(tag).compareTo("certain") == 0) {
          certains.put(new Integer(position), tag);
        }
        else if (rank(tag).compareTo("tagged") == 0) {
          ta.add(new Object[] {tag, Integer.toString(position)});
        }
        else if (rank(tag).compareTo("default") == 0) {
          de.add(new Object[] {tag, Integer.toString(position)});
        }
      }
    }
    //rp
    if(action){
      rpProbability(ta, certains, taggeds);
      rpProbability(de, certains, defaults);
    }
  }

  private int arrayIndex(String[] array, String elem){
    for(int i = 0; i < array.length; i++){
      if(elem.compareTo(array[i]) == 0){
        return i;
      }
    }
    return -1;
  }

  private void rpProbability(ArrayList tags, Hashtable certains, Hashtable prob){
    Iterator it = tags.iterator();
    while(it.hasNext()){
      Object[] obj = (Object[])it.next();
      SemanticLabel tag =(SemanticLabel)obj[0];
      String pos = ((String)obj[1]);
      int position = Integer.parseInt(pos.replaceAll(" ",""));
      Float meanprob = geoMeanProb(certains, tag.getTag(), position);
      prob.put(new Object[]{tag, pos}, meanprob);
    }
  }

  private Float geoMeanProb(Hashtable certains, String tag, int position) {
    Enumeration en = certains.keys();
    //float product = 1f;
    float sum = 0;
    int count = 0;
    while (en.hasMoreElements()) {
      Integer pos = (Integer) en.nextElement();
      String tag2 = ( (SemanticLabel) certains.get(pos)).getTag();
      int position2 = pos.intValue();
      float pr = 0f;
      if(tag2 == null || tag == null){
        System.out.print("");
      }
      if (tag2.compareTo(tag) == 0) {//self-transfer?
        pr = kbsrc.compareTo("kbsc")==0? kbrp.selfProbability(tag, Math.abs(position-position2)) :rp.selfProbability(tag, Math.abs(position-position2)) ;
        count++;
      }
      else {
        pr = kbsrc.compareTo("kbsc")==0? kbrp.probabilityInRange(tag2,
                                     (position - position2),
                                     tag) : rp.probabilityInRange(tag2,
                                     (position - position2),
                                     tag); //prob. of tag appear before/after tag2
        count++;
      }
      //product *= pr;
      sum += pr;
    }
    //return new Float(Math.pow(product, ((double) 1 / count)));
    return new Float((float)sum / count);
  }

  private String rank(SemanticLabel tag) {
    if(tag.getType().compareTo("S") == 0){// && Float.compare(tag.getConf(), 0.9f) >=0 && Float.compare(tag.getSup(), 0.01f) >=0){
      return "certain";
    }
    if(tag.getNgram().split(TermSemantic.symbol).length > 1 && tag.getType().compareTo("S") == 0){
      return "certain";
    }
    if(tag.getType().matches("[BMP].*?")){
      return "certain";
    }
    if(tag.getType().indexOf(tag.getTag()) >= 0){
      return "certain";
    }
    if(tag.getType().matches("D.*?")){
      return "default";
    }
    return "tagged";
    }

  /**
   * turn marked to an encoded string where each number is associated with
   * a semantic label
   *
   * @param tags Hashtable
   * @param marked String <>...</><>...</><>...</>
   * @param segs Hashtable code=>label
   * @return "1 4 2 3"
   */

  private String encodeSegments(Hashtable tags, String marked, Hashtable segs) {
    Enumeration en = tags.keys();
    int index = 0;
    while (en.hasMoreElements()) {
      String tag = (String) en.nextElement();
      ArrayList content = (ArrayList) tags.get(tag);
      for (int i = 0; i < content.size(); i++) {
        MarkedSegment seg= (MarkedSegment) content.get(i);
        String text = seg.getSegment();
        SemanticLabel label = (SemanticLabel)seg.getLabel().get(0);
        String pstring = "(.*)<" + tag + ">" + learning.Utilities.escape(text) + "</" + tag + ">(.*)";
        Pattern p = Pattern.compile(pstring);
        Matcher m = p.matcher(marked);
        if(m.lookingAt()){
          marked = m.group(1) + " "+index+" "+m.group(2);
          segs.put("" + index, new Object[]{label, text});
          index++;
        }else{
          System.err.println("Unrecognized content: "+ pstring);
        }
      }
    }
    if(marked.matches(".*?[a-zA-Z]+.*?")){
      System.err.println("Incomplete encoding");
    }
    return marked;
  }

  public static void main(String[] args) {
    Verifier verifier = new Verifier(null, null, null, null, null, null, false,null);
  }
}
