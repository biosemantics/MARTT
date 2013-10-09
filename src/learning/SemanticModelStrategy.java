package learning;

import java.util.regex.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Enumeration;
import miner.TermSemantic;
import miner.RelativePosition;
import knowledgebase.ProcessComposite;

/**
 * <p>Title: LeafSemanticModelStrategy</p>
 * <p>Description: use term semantic class information and "model classes" to mark up
 *                 1. if ., check elements separated by ; using term semantics to build a model
 *                    for the sentence to see if the model matches any model classes learnt from
 *                    training examples: this is not effective, removed.
 *                 2. if no match, check ;-separated clauses
 *                 3. learn semantic patterns for compound</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class SemanticModelStrategy
    extends SemanticClassStrategy {

  public SemanticModelStrategy(String[] trainingexamples, String[] classes,
                                   String[] delim, String alg) {
    super(trainingexamples, classes, delim, alg);
  }

  public Model learnModel(boolean debug) {
    TermSemantic sc = new TermSemantic(trainingexamples, classes, n, conf,
                                       sup, delim, false);
    CompoundPattern compoundpatterns = new CompoundPattern(trainingexamples);
    compoundpatterns.learnPatterns(debug);
    MultiplePattern multiplepatterns = new MultiplePattern(trainingexamples);
    multiplepatterns.learnPatterns(debug);
    Hashtable modelclasses = learnModelClasses(sc);
    //sc.printTermsForClasses(0.01f, 0.8f);
    if (debug) {
      //sc.printTermsForClasses(0.01, 0.8);
      Enumeration en = modelclasses.keys();
      while (en.hasMoreElements()) {
        String label = (String) en.nextElement();
        System.out.println(label + " CONTAINS " +
                           (String) modelclasses.get(label));
      }
      System.out.println();
      System.out.println();
    }
    classcount = sc.getClasscount();
    LearnTransition tran = new LearnTransition(trainingexamples, classes,
                                               classcount);
    float[][] transmatrix = tran.learnTransMatrix();
    RelativePosition rp = new ProcessComposite().mineRelativePosition(addnull(trainingexamples));
    return new SemanticModelSegmentationModel(sc, modelclasses,
                                                  compoundpatterns,
                                                  multiplepatterns, rp, transmatrix,
                                                  classes, delim);
  }

  /**
   * analyze training examples to build model classes for each element
   *
   * @return hashtable where a key is class label, and a value is a set of sub-elements decided by semantic class of leading n-gram
   * @todo should make this method an public class
   */
  private Hashtable learnModelClasses(TermSemantic sc) {
    Hashtable models = new Hashtable();
    for (int i = 0; i < number; i++) {
      models.put(classes[i], new StringBuffer());
    }
    StringBuffer classstring = new StringBuffer();
    for (int i = 0; i < size; i++) {
      String example = trainingexamples[i].replaceFirst("^\\s+", "").trim();
      while (example.compareTo("") != 0) {
        String classl = Utilities.getFirstTag(example);
        String text = Utilities.getFirstText(example).replaceFirst("^\\s+", "").
            trim();
        example = Utilities.removeFirstElement(example);
        if (hasMultipleClauses(text)) {
          classstring.append(classl + " ");
          updateModels(models, classl, text, sc);
        }
      }
    }
    finalizeModels(models, classstring.toString());
    return models;
  }

  /**
   * clauses seperated by ; or .
   * @param text
   * @return
   */
  private boolean hasMultipleClauses(String text) {
    int i1 = Model.getDelimiterIndex(";", text);
    int i2 = Model.getDelimiterIndex(";", text);
    int i3 = Model.getDelimiterIndex(".", text);

    if (i1 >= 0 && i2 >= 0 && i1 != i2) {
      return true;
    }
    else if (i1 >= 0 && i3 >= 0) {
      return true;
    }
    return false;
  }

  /**
   * analyze "text" to update models for the class "label"
   * use "sc" to determine class for each ;-separated clause
   * update model "label" -contains- labelstring by appending clause labels to the labelstring
   * in "models"
   *
   *
   * @param models
   * @param label
   * @param text
   * @param sc
   * @param n
   */
  private void updateModels(Hashtable models, String label, String text,
                            TermSemantic sc) {
    //StringBuffer occured = new StringBuffer();
    while (text.compareTo("") != 0) {
      int d = text.indexOf(';');
      int period = Model.getDelimiterIndex(".", text);
      d = d >= 0 ? d : (period == -1? text.length()-1 : period); //index for delimitor
      String clause = text.substring(0, d + 1);
      text = text.substring(d + 1);
      //decide class label for clause
      //String clabel = Utilities.classFor(clause, sc, 0.8, 0.01);
      String clabel = Utilities.getFirstMWords(clause, 3, "-", true);
      //if(clabel != null && occured.toString().indexOf(clabel) < 0){
      StringBuffer labelstring = (StringBuffer) models.get(label);
      d = labelstring.indexOf(clabel + " ");
      if (d >= 0) {
        //labelstring : label 111 label 11111
        labelstring.replace(d, d + clabel.length() + 1, clabel + " 1");
      }
      else {
        labelstring.append(clabel + " 1 ");
      }
      models.put(label, labelstring);
      //occured.append(clabel).append(" ");
      //}
    }
  }

  /**
   * change model from
   * "label"=>"label1 1111 label2 11111 label1 1111111 label3 etc"
   * to
   * "label"=>"label1 xx label2 xx label3 xx" xx is a two digit number representing the percentage of the label before the number
   * @param models
   * @param classcount
   */
  private void finalizeModels(Hashtable models, String classcount) {
    Enumeration labels = models.keys();
    while (labels.hasMoreElements()) {
      String label = (String) labels.nextElement();
      StringBuffer percstring = new StringBuffer();
      String labelstring = ( (StringBuffer) models.get(label)).toString().trim();
      if (labelstring.compareTo("") != 0) {
        int ccount = Utilities.getOccurrence(classcount, label);
        String[] seg = labelstring.split(" ");
        for (int i = 0; i < seg.length; i++) {
          String node = seg[i++];
          int occ = seg[i].length();
          float perc = (float) occ / ccount;
          java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
          percstring.append(node).append(" ").append(df.format(perc)).
              append(" ");
        }
      }
      models.put(label, percstring.toString().trim());
    }
  }

  /**
   * change model from
   * "label"=>"label1 label2 label1 label3 etc"
   * to
   * "label"=>"label1 xx label2 xx label3 xx" xx is a two digit number representing the percentage of the label before the number
   * @param models
   * @param classcount
   */
  /*private void finalizeModels(Hashtable models, String classcount){
    Enumeration labels = models.keys();
    while(labels.hasMoreElements()){
      String label = (String) labels.nextElement();
      StringBuffer percstring = new StringBuffer();
      String labelstring = ((StringBuffer)models.get(label)).toString();
      int ccount = Utilities.getOccurrence(classcount, label);
      for(int i = 0; i < number; i++){
        int occ = Utilities.getOccurrence(labelstring, classes[i]);
        if(occ != 0){
          float perc = (float)occ/ccount;
          java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
       percstring.append(classes[i]).append(" ").append(df.format(perc)).append(" ");
        }
      }
      models.put(label, percstring.toString().trim());
    }
     }*/

  public static void main(String[] argv) {
    /*SemanticModelStrategy s = new SemanticModelStrategy(new String[] {
        ""}
        , new String[] {""}
        , new String[] {""});
    //System.out.println(Utilities.getOccurrence("b b c d a a e","a"));
    System.out.println(s.wildcards("petioles , rachis").toString());
    String[] p = new String[2];
    p[0] = "\\w+ , rachis";
    p[1] = "petioles , \\w+";
    String[] g = s.generalizeCP(p);
    for (int i = 0; i < g.length; i++) {
      System.out.println(g[i]);
    }

    System.out.println(s.match("cat  , dog", g));
*/
  }

  /**
   * translate words/phrases in compound to their semantic class
   * return pattern with semantic classes as literals
   * "and" is in the stop list, but here each word is examined, so that "and" would be included in the patterns
   *
       * split compound using stop words, check for semantic classes for each component
   *
   * @param training
   * @param sc
   * @return
   */
  /*private String generatePattern(String compound, TermSemantic sc, boolean debug){
    String[][] splits = splitAtStopWords(compound);
    String[] components = splits[0];
    String[] stops = splits[1];
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < components.length; i++){
      String[] semantics = checkSemanticsFor(components[i], sc, debug);
      for(int j = 0; j < semantics.length; j++){
        sb.append(semantics[j]).append(" ");
      }
      sb.append(stops[i]).append(" ");
    }
    return sb.toString().trim();
   }*/
  /**
   * it is possible a component contains two or more sub components that have a semantic class defined
   * 1. get n-gram (n = length of the component) and all its sub-grams
   * 2. check class labels for each n-gram starting with the longest gram.
   * 3. from left to right, check for the longest ngram that has a label for component
   *
   * labels index = n(n+1)/2 -(i+1)n + i(i+1)/2 + j, where n is tokens.length, i is ngrams index, j is ngrams[i] index
   * @param components
   * @return
   */
  /*private String[] checkSemanticsFor(String component, TermSemantic sc, boolean debug){
    StringBuffer sb = new StringBuffer();
    String[] tokens = Tokenizer.tokenize(component);
    int l = tokens.length;
    ArrayList[] ngrams = sc.getLENGrams(tokens, l);
    String[] labels = sc.getClassForNGrams(ngrams, conf, sup, true);//true = check other forms
    for (int j = 0; j < l - 1; j++) {
      boolean found = false;
      for (int i = l - 1 - j; i >= 0; i--) {
        int index = l*(l+1)/2 - (i+1)*l + i*(i+1)/2 +j;
        if(labels[index].compareTo("")!= 0){
          sb.append(labels[index]).append(" ");
          int icp = i;
          i = l-(i+j+2);
          j = icp+j+1;
          found = true;
          continue;
        }
      }
      if(!found){
        sb.append("unknown").append(" ");
      }
    }
       return sb.toString().replaceAll("(unknown )+", "unknown ").trim().split(" ");
   }*/
  /**
   * split string compound at stopwords, remember the stopwords that split the string
   * @param compound
   * @return [0] components [1] stopwords
   */
  /*private String[][] splitAtStopWords(String compound){
    StringBuffer sb = new StringBuffer().append("(");
    String[] stoplist = Tokenizer.stop.split(" ");
    for(int i = 0; i<stoplist.length; i++){
      sb.append(stoplist[i]).append("|");
    }
    String stoppattern = sb.deleteCharAt(sb.length()-1).append(")").toString();
    String[] components = compound.split(stoppattern);
    String[] stops = new String[components.length];
    for(int i = 0; i < components.length; i++){
      Pattern comp = Pattern.compile(components[i]+"\\s*(\\S+)\\s*(.*)");
      Matcher m = comp.matcher(compound);
      if(m.lookingAt()){
        stops[i] = m.group(1);
        compound = m.group(2);
      }else{
        stops[i]="";
      }
    }
    String[][] results = new String[2][];
    results[0] = components;
    results[1] = stops;
    return results;
   }*/

}
