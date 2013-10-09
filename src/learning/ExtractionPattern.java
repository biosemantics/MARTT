package learning;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.regex.*;

/**
 * <p>Title: ExtractionPattern</p>
 * <p>Description: learn extraction patterns from training examples. a training example is a text block marked with tags</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class ExtractionPattern implements Serializable{
  private String[] trainingexamples = null;
  private String[] classes = null;
  private int size = 0;
  private int number = 0;
  private double laplacian = 1d; //lap = (errors+1)/(extractions+1)
  private Hashtable patterns = null;
  private static Hashtable SCLASS = new Hashtable();

  public ExtractionPattern(String[] trainingexamples, String[] classes,
                           double laplacian)  {
    this.trainingexamples = trainingexamples;
    this.classes = classes;
    this.size = trainingexamples.length;
    this.number = classes.length;
    this.laplacian = laplacian;
    //this.patterns = extractPatterns();
  }

  public static void populateSCLASS() {
    SCLASS.put("MoNTh", "\\bJan|Janurary|Feb|February|Mar|March|Apr|April|May|Jun|June|Jul|July|Aug|August|Sep|September|Oct|October|Nov|Novemeber|Dec|December\\b");
    SCLASS.put("UnIt", "\\b(?:cm|mm|dm|µm|m)\\b");
    //SCLASS.put("UnIt", "\\b(?i:cm|mm|dm|µm|m)\\b");
    SCLASS.put("SeAsOn", "\\b(?:[Ss]pring|[Ss]ummer|[Ff]all|[Aa]utume|[Ww]inter)\\b");
    //SCLASS.put("SeAsOn", "\\b(?i:spring|summer|fall|autume|winter)\\b");
    SCLASS.put("DiAm", "\\b(?:diam\\.|diameter\\b|[Dd]\\.[Bb]\\.[Hh]\\.)");
    //SCLASS.put("DiAm", "\\b(?i:diam\\.|diameter\\b|d\\.b\\.h\\.)");
    SCLASS.put("NuMbEr", "\\b\\d+(?:\\.\\d+)?\\b");
    SCLASS.put("NuMaLpH", "\\b\\d+\\p{Alpha}+\\b");
    SCLASS.put("HtMlEnTiTy", "\\b&\\S+;");
  }

  public static Hashtable getSCLASS() {
    populateSCLASS();
    return SCLASS;
  }

  /**
   * 1. collect instances for each class: a hashtable of arraylist
   *    an instance contains the target element as well as the elements
   *    immediately before and after it
   * 2. derive patterns from the arraylist of instances
   * @return
   */
  public Hashtable extractPatterns() {
    Hashtable instances = new Hashtable();
    collectInstances(instances);
    instances = learnPatterns(instances);
    printAllRules(instances);
    return instances;
  }

  private void printAllRules(Hashtable instances){
    Enumeration en = instances.keys();
    while(en.hasMoreElements()){
      String tag = (String) en.nextElement();
      if(tag.compareTo("text") != 0){
        System.out.println("Rules for : " + tag);
        ArrayList rules = (ArrayList) instances.get(tag);
        Iterator it = rules.iterator();
        while (it.hasNext()) {
          RegExpExtraction rule = (RegExpExtraction) it.next();
          System.out.println(rule.toString());
        }
        System.out.println();
      }
    }
  }
  /**
   * 1. collect instances for each class: a hashtable of arraylist
   *    an instance contains the target element as well as the elements
   *    immediately before and after it
   * @param patterns
   */
  private void collectInstances(Hashtable instances) {
    for (int i = 0; i < size; i++) {
      String example = trainingexamples[i].trim().replaceFirst("^\\s+", "");
      Pattern p = Pattern.compile(
          "^(<(.*)>[^>]*?</\\2>)\\s*(((<(.*)>[^>]*?</\\6>).*|$))");
      Matcher m = p.matcher(example);
      if (m.lookingAt()) {
        String inst = ("^" + m.group(1) + m.group(5)).replaceFirst("null",
            "\\$");
        String tag = m.group(2);
        updateHashtable(instances, tag, inst);
        //System.out.println("inst: " + inst);
      }
      p = Pattern.compile(
          "(<(.*)>[^>]*?</\\2>)\\s*(<(.*)>[^>]*?</\\4>)\\s*(((<(.*)>[^>]*?</\\8>).*|$))");
      m = p.matcher(example);
      while (m.lookingAt()) {
        String inst = (m.group(1) + m.group(3) +
                       m.group(7)).replaceFirst("null", "\\$");
        example = m.group(3) + m.group(5);
        String tag = m.group(4);
        updateHashtable(instances, tag, inst);
        m = p.matcher(example);
        //System.out.println("inst: " + inst);
      }
    }
  }

  /**
   * 2. derive patterns from the hashtable of arraylists of instances
   * @param patterns
   * @return
   */
  private Hashtable learnPatterns(Hashtable instances) {
    if (instances == null) {
      return instances;
    }
    Enumeration en = instances.keys();
    while (en.hasMoreElements()) {
      String tag = (String) en.nextElement();
      if (tag.compareTo("text") != 0) {
        ArrayList instlist = (ArrayList) instances.get(tag);
        ArrayList ptns = learnPatternsOfAClass(instlist, tag);
        ptns = sortBySize(ptns); //decides the order in which rules will be applied
        instances.put(tag, ptns);
      }
    }
    return instances;
  }

  /**
   * rules are sorted by 1. # of constrains 2. laplacian score
   * @param rules
   * @return
   */
  private ArrayList sortBySize(ArrayList rules) {
    Hashtable sort = new Hashtable();
    Iterator it = rules.iterator();
    int maxlength = 0;
    //group rules by the number of constrains
    while (it.hasNext()) {
      RegExpExtraction rule = (RegExpExtraction) it.next();
      int size = rule.getTokenindices().split("\\s+").length;
      if (size > maxlength) {
        maxlength = size;
      }
      if (sort.containsKey("" + size)) {
        ( (ArrayList) sort.get("" + size)).add(rule);
      }
      else {
        ArrayList newlist = new ArrayList();
        newlist.add(rule);
        sort.put("" + size, newlist);
      }
    }
    //sort by laplacian score, low -> high
    ArrayList sorted = new ArrayList();
    while (maxlength > 0) {
      if (sort.containsKey("" + maxlength)) {
        ArrayList rulelist = (ArrayList) sort.get("" + maxlength);
        rulelist = sortByLaplacian(rulelist);
        sorted.addAll(rulelist);
      }
      maxlength--;
    }
    return sorted;
  }

  private ArrayList sortByLaplacian(ArrayList rules) {
    Object rulelist[] = rules.toArray();
    Arrays.sort(rulelist, new RegExpExtraction());
    return new ArrayList(Arrays.asList(rulelist));
  }

  /**
   * sort instances by the length of the segments marked by tag in terms of # of tokens
   * @param instances
   * @param tag
   * @return
   */
  private ArrayList sortByLength(ArrayList instances, String tag) {
    int maxlength = 0;
    Hashtable sort = new Hashtable(); //each cell contains instances of same length
    Iterator it = instances.iterator();
    Pattern p = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">");
    while (it.hasNext()) {
      String inst = (String) it.next();
      Matcher m = p.matcher(inst);
      if (m.find()) {
        String seg = m.group(1);
        int len = spacePunctuations(seg).split("\\s+").length;
        if (maxlength < len) {
          maxlength = len;
        }
        if (sort.containsKey("" + len)) {
          ( (ArrayList) sort.get("" + len)).add(inst);
        }
        else {
          ArrayList segs = new ArrayList();
          segs.add(inst);
          sort.put("" + len, segs);
        }
      }
    }

    ArrayList sorted = new ArrayList();
    while (maxlength > 0) {
      if (sort.containsKey("" + maxlength)) {
        sorted.addAll( (ArrayList) sort.get("" + maxlength));
      }
      maxlength--;
    }
    return sorted;
  }

  /**
   *
   * learn patterns that cover all the instances, but not other elements in training example
   * candidate patterns are to be generalized as much as possible but without covering any false positive instances
   * each generalization step should be as minimal as possible
   * @param instances
   * @return
   */
  private ArrayList learnPatternsOfAClass(ArrayList instances, String tag) {
    boolean debug = true;

    ArrayList rules = new ArrayList();
    if (instances == null) {
      return null;
    }
    instances = sortByLength(instances, tag);

    while (!instances.isEmpty()) {
      String inst = (String) instances.get(0);

      if (debug) {
        System.out.println("derive rule from:[" + inst + "]");
      }

      inst = matchClass(inst);

      RegExpExtraction[] bases = getBases(inst, tag); //see Soderland 99 whisk rule learning
      RegExpExtraction base1 = bases[0];
      RegExpExtraction base2 = bases[1];

      int removed = 0;
      double lap1 = base1.getLaplacian();
      double lap2 = 0d;
      if (Double.compare(lap1, this.laplacian) < 0) {
        if (debug) {
          System.out.println("Base1 beats laplacian threshold(" + lap1 + "):[" +
                             base1.getPatternString() + "]");
        }
        rules.add(base1);
        removed = removeCovered(base1, instances);
        continue;
      }
      else if (Double.compare( (lap2 = base2.getLaplacian()), this.laplacian) <
               0) {
        if (debug) {
          System.out.println("Base2 beats laplacian threshold(" + lap2 + "):[" +
                             base2.getPatternString() + "]");
        }
        rules.add(base2);
        removed = removeCovered(base2, instances);
        continue;
      }

      if (debug) {
        System.out.println("Base1 is not good enough, specialize base1(" + lap1 +
                           ")[" + base1.getPatternString() + "]");
      }
      base1 = specialize(base1, inst, lap1);

      if (debug) {
        System.out.println("Specialized Base1(" + base1.getLaplacian() +
                           ")[" + base1.getPatternString() + "]");
      }

      if (debug) {
        System.out.println("Base2 is not good enough, specialize base2(" + lap2 +
                           ")[" + base2.getPatternString() + "]");
      }

      base2 = specialize(base2, inst, lap2);

      if (debug) {
        System.out.println("Specialized Base2(" + base2.getLaplacian() +
                           ")[" + base2.getPatternString() + "]");
      }

      if (Double.compare(base1.getLaplacian(), base2.getLaplacian()) > 0) {
        if (debug) {
          System.out.println("Specialized Base2(" + base2.getLaplacian() +
                             ") is better than specialized base1(" +
                             base1.getLaplacian() + "):[" +
                             base2.getPatternString() + "]<=>[" +
                             base1.getPatternString() + "]");
        }

        rules.add(base2);
        removed = removeCovered(base2, instances);
      }
      else {
        if (debug) {
          System.out.println("Specialized Base1(" + base1.getLaplacian() +
                             ") is better than specialized base2(" +
                             base2.getLaplacian() + "):[" +
                             base1.getPatternString() + "]<=>[" +
                             base2.getPatternString() + "]");
        }

        rules.add(base1);
        removed = removeCovered(base1, instances);
      }

      if(removed == 0){
        System.err.println("rules do not cover:["+instances.get(0)+"]");
        System.err.println("remove:["+instances.get(0)+"]");
        instances.remove(0);
      }
    }
    return rules;
  }

  /**
   * generate an extraction pattern from instance by anchoring the
   * to-be-extracted part using the words just inside the boundary,
   * if possible, use pre-defined class rather than the words themselves.
   * @param instance
   * @param tag
   * @return
   */
  private RegExpExtraction[] getBases(String instance, String tag) {
    RegExpExtraction[] bases = new RegExpExtraction[2]; //0: base1, 1: base2;
    Pattern p = Pattern.compile("^\\s*(.*?)\\s*<" + tag + ">\\s*(.*?)\\s*</" +
                                tag + ">\\s*(.*)\\s*$");
    Matcher m = p.matcher(instance);

    if (m.lookingAt()) {
      String[] left = spacePunctuations(Utilities.strip(m.group(1))).split(
          "\\s+"); //# of tokens before the target
      String[] body = spacePunctuations(m.group(2)).split("\\s+");
      String[] right = spacePunctuations(Utilities.strip(m.group(3))).split(
          "\\s+");
      int lenl = left.length;
      int lenb = body.length;
      int lenr = right.length;
      int length = lenl + lenb + lenr;

      if (body.length < 2) {
        String boundl = decorate(body[0]);
        //String pt = ".*?("+left+").*(?i)";//make case insensitive
        RegExpExtraction base1 = new RegExpExtraction("(" + boundl + ")", tag,
            "" + lenl, length);
        base1.calculateLaplacian();
        bases[0] = base1;
      }
      else {
        String boundl = decorate(body[0]);
        String boundr = decorate(body[lenb - 1]);
        //String pt = ".*?(" + left + ".*?" + right + ").*(?i)";//make case insensitive
        RegExpExtraction base1 = new RegExpExtraction("(" + boundl + " " +
            boundr + ")", tag, "" + lenl + " " + (lenl + lenb - 1), length);
        base1.calculateLaplacian();
        bases[0] = base1;
      }
      String boundl = decorate(left[lenl - 1]);
      String boundr = decorate(right[0]);
      RegExpExtraction base2 = new RegExpExtraction(boundl + "( )" + boundr,
          tag, "" + (lenl - 1) + " " + (lenl + lenb), length);
      base2.calculateLaplacian();
      bases[1] = base2;
      return bases;
    }
    return null;
  }

  /**
   * replace instances of semantic class in str with their class label
   * the order in which classes are applied may be important
   * @param str
   * @return
   */

  public static String matchClass(String str) {
    populateSCLASS();
    StringBuffer sb = new StringBuffer();
    Pattern p = Pattern.compile("(.*?)<(.*?)>([^>]*?)</\\2>(.*)");
    Matcher m = p.matcher(str);
    boolean tagged = false;
    while(m.lookingAt()){
      tagged = true;
      sb.append(m.group(1));
      String tag = m.group(2);
      String cont = m.group(3);
      str = m.group(4);
      m = p.matcher(str);
      String pt = (String) SCLASS.get("HtMlEnTiTy");
      cont = cont.replaceAll(pt, " HtMlEnTiTy ");
      pt = (String) SCLASS.get("NuMaLpH");
      cont = cont.replaceAll(pt, " NuMaLpH ");
      pt = (String) SCLASS.get("NuMbEr");
      cont = cont.replaceAll(pt, " NuMbEr ");
      pt = (String) SCLASS.get("DiAm");
      cont = cont.replaceAll(pt, " DiAm ");
      pt = (String) SCLASS.get("SeAsOn");
      cont = cont.replaceAll(pt, " SeAsOn ");
      pt = (String) SCLASS.get("UnIt");
      cont = cont.replaceAll(pt, " UnIt ");
      pt = (String) SCLASS.get("MoNTh");
      cont = cont.replaceAll(pt, " MoNTh ");
      sb.append("<"+tag+">"+cont+"</"+tag+">");
    }
    if(tagged){
      sb.append(str);
    }
    if(!tagged){
      String pt = (String) SCLASS.get("HtMlEnTiTy");
      str = str.replaceAll(pt, " HtMlEnTiTy ");
      pt = (String) SCLASS.get("NuMaLpH");
      str = str.replaceAll(pt, " NuMaLpH ");
      pt = (String) SCLASS.get("NuMbEr");
      str = str.replaceAll(pt, " NuMbEr ");
      pt = (String) SCLASS.get("DiAm");
      str = str.replaceAll(pt, " DiAm ");
      pt = (String) SCLASS.get("SeAsOn");
      str = str.replaceAll(pt, " SeAsOn ");
      pt = (String) SCLASS.get("UnIt");
      str = str.replaceAll(pt, " UnIt ");
      pt = (String) SCLASS.get("MoNTh");
      str = str.replaceAll(pt, " MoNTh ");
      return str;
    }
    return sb.toString();
  }

  /**
       * if str matches any class, return class name, otherwise return non-caputuring
   * group case-insensitive matching pattern of str.
   * @param str
   * @return
   */
  /*private String matchClass(String str) {
    Enumeration en = SCLASS.keys();
    while (en.hasMoreElements()) {
      String label = (String) en.nextElement();
      String pt = (String) SCLASS.get(label);
      Pattern p = Pattern.compile(pt);
      Matcher m = p.matcher(str);
      if (m.lookingAt()) {
        return "\\s*"+label+"\\s*";
      }
    }
    if (str.matches("(\\p{Alpha})+")) { //what about :o?
      return "\\s*\\b(?i:" + str + ")\\b\\s*";
    }else if(str.compareTo("^") == 0 ||str.compareTo("$") == 0  ){
      return str;
    }
    else {
      return "\\s*"+escape(str)+"\\s*";
    }
     }*/
  /**
   * put around token possible spaces for reg exp
   * @param token
   * @return
   */
  private String decorate(String token) {
    if (SCLASS.containsKey(token)) {
      return "\\s*" + token + "\\s*";
    }
    if (token.matches("(\\p{Alpha})+")) { //what about :o?
      return "\\s*\\b(?:" + cases(token) + ")\\b\\s*";
      //return "\\s*\\b(?i:" + token + ")\\b\\s*";
    }
    if (token.compareTo("^") == 0 || token.compareTo("$") == 0) {
      return token;
    }
    else {
      return "\\s*" + learning.Utilities.escape(token) + "\\s*";
    }
  }
  /**
   * "abcd" => "[aA][bB][cC][dD]"
   * "abcd" => "[aA]bcd"
   * @param token
   * @return
   */
  private String cases(String token){
    StringBuffer sb = new StringBuffer();
    /*char[] chars = token.toCharArray();
    for(int i = 0; i < chars.length; i++){
      String upper = (chars[i]+"").toUpperCase();
      String lower = (chars[i]+"").toLowerCase();
      sb.append("[").append(upper).append(lower).append("]");
    }
    return sb.toString();*/
    String first = ""+token.charAt(0);
    sb.append("[").append(first.toLowerCase()).append(first.toUpperCase()).append("]").append(token.substring(1));
    return sb.toString();
  }


  /**
   * put spaces around any punctuation marks
   * return a string that has not leading and trailing spaces, and
   * has exactly 1 space between tokens, words or puncturation marks
   * @param str
   * @return
   */
  public static String spacePunctuations(String str) {
    char[] array = str.toCharArray();
    Pattern p = Pattern.compile("\\p{Punct}");
    StringBuffer sb = new StringBuffer();

    for (int i = 0; i < array.length; i++) {
      Matcher m = p.matcher("" + array[i]);
      if (m.matches()) {
        sb.append(" ").append(array[i]).append(" ");
      }
      else {
        sb.append(array[i]);
      }
    }
    return sb.toString().trim().replaceAll("\\s+", " ").replaceFirst("^\\s+",
        "");
  }

  /**
   * remove from instances that have been covered by rule
   * @param rule
   * @param instances
   * @return
   */
  private int removeCovered(RegExpExtraction rule, ArrayList instances) {
    boolean debug = true;
    if (debug) {
      System.out.println("[" + rule.getPatternString() + "] removes");
    }
    int count = 0;
    Iterator it = instances.iterator();
    while (it.hasNext()) {
      String xml = (String) it.next();
      String text = Utilities.strip(xml);
      text = text.replaceFirst("^\\s*\\^\\s*", "").replaceFirst("\\s*\\$\\s*$",
          "");
      ArrayList extracted = rule.extractUseRule(text);
      if (!extracted.isEmpty()) {
        if (xml.replaceAll("\\s+",
                           "").indexOf( ( (String) extracted.get(0)).replaceAll(
            "\\s+", "")) >=
            0) {
          it.remove();
          count++;
          if (debug) {
            System.out.println(text);
          }
        }
      }
    }
    return count;
  }

  /**
   * adding words from seed instance to rule to decrease its laplacian value
   * @param rule
   * @param instances
   * @return
   */
  private RegExpExtraction specialize(RegExpExtraction rule, String seed,
                                      double lap) {
    boolean debug = true;

    String text = spacePunctuations(Utilities.strip(seed));
    String[] words = text.split("\\s+");
    double obestlap = lap;
    RegExpExtraction orule = (RegExpExtraction) rule.clone();
    do {
      double bestlap = obestlap;
      RegExpExtraction bestrule = (RegExpExtraction) orule.clone();
      for (int i = 0; i < words.length; i++) {
        String indices = orule.getTokenindices();
        if (!inString(indices, "" + i)) {
          RegExpExtraction newrule = addAWord(orule, words[i], i);
          double newlap = newrule.getLaplacian(); //laplacian should be a member of the rule
          if (debug) {
            System.out.println("[new pattern(" + newlap + ")]:" +
                               newrule.getPatternString());
          }
          if (Double.compare(newlap, bestlap) < 0) {
            bestlap = newlap;
            bestrule = newrule;
            if (debug) {
              System.out.println("[current inner best(" + bestlap + ")]:" +
                                 bestrule.getPatternString());
            }
          }
          else if (Double.compare(newlap, bestlap) == 0) {
            bestrule = pickPrefered(newrule, bestrule);
            bestlap = bestrule.getLaplacian();
          }
        }
      }
      //what about ==0 ?
      if (Double.compare(bestlap, obestlap) < 0) {
        orule = bestrule;
        obestlap = bestlap;
        if (debug) {
          System.out.println("[current out best(" + obestlap + ")]:" +
                             orule.getPatternString());
        }
      }
      else if (Double.compare(bestlap, obestlap) == 0) {
        orule = pickPrefered(bestrule, orule);
        obestlap = orule.getLaplacian();
        if (debug) {
          System.out.println("no improvment on laplacian, ends here");
        }
        break; //no improvement
      }
      else {
        if (debug) {
          System.out.println("no improvment on laplacian, ends here");
        }
        break; //no improvement
      }
    }
    while (Double.compare(orule.getLaplacian(), this.laplacian) > 0);
    return orule;
  }

  /**
   * use heuristics to pick one rule out of two with same laplacian
   *
   * 1. prefer rules with constrains near the boundaries
   * 2. prefer semantic class over words
   * 3. prefer more general rules
   *
   * @param rule1
   * @param rule2
   * @return
   */
  private RegExpExtraction pickPrefered(RegExpExtraction rule1,
                                        RegExpExtraction rule2) {
    int score1 = scoreRule(rule1);
    int score2 = scoreRule(rule2);
    return score1 > score2 ? rule2 : rule1;//pick lower score
  }

  /**
   * score = sum(min-distance-to-boundary-of-each-constrain) - 2*#-of-semantic-classes
   * that is, if a semantic class appears 3 tokens away from boundary, its net-effects is 1,
   * equivalent to a word near by the boundary
   * @param rule1
   * @return
   */
  private int scoreRule(RegExpExtraction rule) {
    String[] components = rule.getPatternComponents();
    int leftboundary = 0;
    int rightboundary = 0;
    //find two boundaries
    for (int i = 0; i < components.length; i++) {
      if (components[i].matches("^\\(.*")) {
        leftboundary = i;
      }
      else if (components[i].matches(".*?\\)$")) {
        rightboundary = i;
      }
      else if (components[i].matches(".*?\\($")) {
        leftboundary = i;
      }
      else if (components[i].matches("^\\).*")) {
        rightboundary = i;
      }
    }
    //score
    int score = 0;
    for (int i = 0; i < components.length; i++) {
      if (components[i].compareTo(".*?") != 0) {
        if (components[i].matches(".*?\\s*([A-Z][a-z][A-Z])+\\s*.*")) {
          score = score - 2;
        }
        int dist = 0;
        if (i == leftboundary && i == rightboundary) {
          dist = 0;
        }
        else {
          int distl = Math.abs(i - leftboundary);
          int distr = Math.abs(i - rightboundary);
          dist = Math.min(distl, distr);
        }
        score = score + dist;
      }
    }
    return score;
  }

  /**
       * insert "word" to rule at a location indicated by "index" to make a new rule
   * @param rule
   * @param word
   * @param index
   * @return
   */
  private RegExpExtraction addAWord(RegExpExtraction rule, String word,
                                    int index) {
    RegExpExtraction newrule = (RegExpExtraction) rule.clone();
    String[] components = newrule.getPatternComponents();
    if (index < components.length) {
      components[index] = decorate(matchClass(word));
      newrule.updatePatternComponents(components);
      return newrule;
    }
    else {
      //throw indexOUTofBound
    }
    return null;
  }

  /**
   * <a>aaa</a><b>bbb</b><c>ccc</c> => aaa(bbb)
   * ^<a>aaa</a><b>bbb</b> => ^(aaa)bbb
   * ^<a>aaa</a>$ => ^(aaa)$
   * @param instance
   * @return
   */
  /*private String instanceToPattern(String instance){
    instance = instance.replaceAll("<.*?>", "####").replaceAll("#\\s+#", "##");
    String[] parts = instance.split("#+");
    if(parts.length != 3){
      System.err.println("pattern does not have three parts");
    }
    StringBuffer sb = new StringBuffer();
       sb.append(parts[0]).append("(").append(parts[1]).append(")").append(parts[2]);
    String ptn = sb.toString();
    ptn = patternProcess(ptn);
    return ptn;
     }*/
  /**
   * pattern and text to be matched use this same processing methods
   * @param ptn
   * @return
   */
  /*private String patternProcess(String ptn) {
    ptn = ptn.toLowerCase();
    //take care of spaces around punctuation marks
    ptn = ptn.replaceAll("\\s*\\p{Punct}\\s*", " PUNCT ");
    ptn = ptn.replaceAll("\\d+\\.\\d+", " FLOAT "); //take care of floats
    ptn = ptn.replaceAll("\\d+", " NUMBER "); //take care of numbers, must do it after floats
    ptn = ptn.replaceAll("\\b" + MONTH + "\\b", " MONTH "); //Month
    //ptn = ptn.replaceAll("\\b"+UNIT+"\\b", " UNIT "); //UNIT
    ptn = ptn.replaceAll("\\s+", "\\s+"); //take care of space
    return ptn;
     }*/
  /**
   * expand the classes in a rule to their corresponding reg exp strings
   * @param rulestring
   * @return
   */
  public static String expandClasses(String rulestring) {
    populateSCLASS();
    Enumeration en = SCLASS.keys();
    while (en.hasMoreElements()) {
      String label = (String) en.nextElement();
      if (rulestring.indexOf(label) >= 0) {
        String pt = (String) SCLASS.get(label);
        pt = pt.replaceAll("\\\\", "\\\\\\\\");
        rulestring = rulestring.replaceAll(label, pt);
      }
    }
    return rulestring;
  }

  private void updateHashtable(Hashtable patterns, String tag, String instance) {
    if (tag.compareTo("") != 0) {
      ArrayList instances = null;
      if (patterns.containsKey(tag)) {
        instances = (ArrayList) patterns.get(tag);
      }
      else {
        instances = new ArrayList();
      }
      instances.add(instance);
      patterns.put(tag, instances);
    }
  }

  public Hashtable getPatterns() {
    return patterns;
  }

  /**
   *
   * @param string
   * @param token
   * @return ture if token is in string as a word
   */
  private boolean inString(String string, String token) {
    String[] tokens = string.split("\\s+");
    for (int i = 0; i < tokens.length; i++) {
      if (tokens[i].compareTo(token) == 0) {
        return true;
      }
    }
    return false;
  }

  /**
   *
   * <p>Title: RegExpExtraction</p>
   * <p>Description: data structure of single slot extraction pattern</p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: UIUC</p>
   * @author Hong Cui
   * @version 0.1
   */
  public class RegExpExtraction extends Tag
      implements Comparator, Serializable {
    private String[] pattern = null; //hold pieces of patterns
    private String patternstring = null;
    private double laplacian = 1d;
    private String tag = null;
    private String tokenindices = null; //indices of tokens in (standardized)seed instance in the pattern

    public RegExpExtraction() {

    }

    public RegExpExtraction(String anchors, String tag, String tokenindices,
                            int length) {
      this.tokenindices = tokenindices; // space-seperated numbers
      String[] tokens = anchors.split("\\s+");
      String[] indices = tokenindices.split("\\s+");
      if (tokens.length != indices.length) {
        //throw new Exception();
      }
      pattern = new String[length];
      int l1 = -1;
      int r1 = -1;
      int l2 = -1;
      int r2 = -1;
      int j = 0;
      for (int i = 0; i < length; i++) {
        if (inString(tokenindices, "" + i)) {
          pattern[i] = tokens[j];
          j++;
        }
        else {
          pattern[i] = ".*?";
        }
      }
      this.patternstring = getPatternString();
      this.tag = tag;
      //this.laplacian = laplacian;
    }

    /**
     * test rule on entire training examples to calcuate laplacian score
     * laplacian = [(number of errors)+1] / [(number of extractions)+1]
     * @param rules
     * @return
     */
    public void calculateLaplacian() {
      int error = 0;
      int extract = 0;
      for (int i = 0; i < trainingexamples.length; i++) {
        String xml = trainingexamples[i];
        String text = Utilities.strip(xml);
        ArrayList extracted = this.extractUseRule(text); //extracted: <tag>extracted</tag>
        Iterator it = extracted.iterator();
        while (it.hasNext()) {
          if (xml.replaceAll("\\s+",
                             "").indexOf( ( (String) it.next()).replaceAll(
              "\\s+", "")) >= 0) {
            extract++;
          }
          else {
            error++;
          }
        }
      }
      this.laplacian = (double) (error + 0.5) / (extract + 0.5);
    }

    /**
     * use rule to extract from plain text, return the extracted in a pair of tag of the rule
     * <tag>extracted text</tag>, may extract more than one target.
     * if nothing is extracted, return empty arraylist
     * @param rule
     * @param text
     * @return
     */
    private ArrayList extractUseRule(String text) {
      ArrayList extracted = new ArrayList();
      String pt = expandClasses(this.getPattern());
      String tag = this.getTag();

      //extract
      //text = spacePunctuations(text);//spacing tokens is not needed because of \\s* used in patterns
      Pattern p = Pattern.compile(pt);
      Matcher m = p.matcher(text);
      while (m.find() && text.trim().compareTo("") != 0) {
        extracted.add("<" + tag + ">" + m.group(1) + "</" + tag + ">");
        if (m.end(1) + 1 >= text.length()) {
          text = "";
        }
        else {
          text = text.substring(m.end(1) + 1);
        }
        m = p.matcher(text);
      }
      return extracted;
    }


    private String getPatternString() {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < pattern.length; i++) {
        sb.append(pattern[i]);
      }
      return sb.toString().replaceAll("(\\.\\*\\?)+", ".*?")
          .replaceAll("(\\\\s\\*)+", "\\\\s*")
          .replaceFirst("\\\\s\\*$","")
          .replaceFirst("^\\\\s\\*","")
          .replaceAll("\\\\s\\*\\.\\*\\?",".*?")
          .replaceAll("\\.\\*\\?\\\\s\\*",".*?")
          .replaceAll("\\\\s\\*\\(\\.\\*\\?", "(.*?")
          .replaceAll("\\\\s\\*\\)\\.\\*\\?", ").*?")
          .replaceAll("\\.\\*\\?\\(\\\\s\\*",".*?(")
          .replaceAll("\\.\\*\\?\\)\\\\s\\*",".*?)")
          .replaceAll("\\\\s\\*\\)\\\\s\\*",")\\\\s*")
          .replaceAll("\\\\s\\*\\(\\\\s\\*","\\\\s*(")
          .replaceFirst("\\.\\*\\?$","");

    }

    public String getPattern() {
      return patternstring;
    }

    public String getTag() {
      return tag;
    }

    public String getTokenindices() {
      return tokenindices;
    }

    public String[] getPatternComponents() {
      return pattern;
    }

    public void setPatternComponents(String[] pattern) {
      this.pattern = pattern;
    }

    public void updatePatternComponents(String[] pattern) {
      this.pattern = pattern;
      this.patternstring = getPatternString();
      //update laplacian and tokenindices
      calculateLaplacian();

      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < pattern.length; i++) {
        if (pattern[i].compareTo(".*?") != 0) {
          sb.append(i).append(" ");
        }
      }
      tokenindices = sb.toString().trim();
    }

    public double getLaplacian() {
      return laplacian;
    }

    public void setLaplacian(double laplacian) {
      this.laplacian = laplacian;
    }

    public void setPattern(String patternstring) {
      this.patternstring = patternstring;
    }

    public void setTag(String tag) {
      this.tag = tag;
    }

    public void setTokenindices(String indices) {
      this.tokenindices = indices;
    }

    public Object clone() {
      RegExpExtraction clone = new RegExpExtraction();
      clone.setLaplacian(this.laplacian);
      clone.setPattern(this.patternstring);
      clone.setTag(this.tag);
      clone.setPatternComponents( (String[])this.pattern.clone());
      clone.setTokenindices(this.tokenindices);
      return clone;
    }

    public String toString(){
      StringBuffer sb = new StringBuffer();
      sb.append("["+laplacian+" : "+patternstring+"]");
      return sb.toString();
    }

    public int compare(Object o1, Object o2) {
      return (Double.compare( ( (RegExpExtraction) o1).getLaplacian(),
                             ( (RegExpExtraction) o2).getLaplacian()));
    }

    public boolean equal(Object o1) {
      return patternstring.compareTo( ( ( (RegExpExtraction) o1).getPattern())) ==
          0 &&
          Double.compare(laplacian, ( (RegExpExtraction) o1).getLaplacian()) == 0;
    }
  }

  public static void main(String[] argv) {

    String[] classes = new String[] {
        "growth-habit", "life-span", "height-when-mature", "stems",
        "persistence", "crown", "habitat", "hairness", "sex", "roots", "sap",
        "woodiness", "diameter", "color", "text"};
    String filepath = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level2\\fna500-phls-test";
    File file = new File(filepath);
    File[] files = file.listFiles();
    String[] examples = new String[files.length];
    for (int i = 0; i < examples.length; i++) {
      examples[i] = Utilities.readFile(files[i]);
      Pattern p = Pattern.compile(
          ".*?<phls-general>(.*?)</phls-general>.*");
      Matcher m = p.matcher(examples[i]);
      if (m.lookingAt()) {
        examples[i] = m.group(1);
      }
    }
    ExtractionPattern exp = new ExtractionPattern(examples, classes, 0.1);
    Hashtable patterns = exp.extractPatterns();
    String pfile = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level2\\patternfile";
    visitor.Serializer.serialization(pfile, patterns);
  }
}