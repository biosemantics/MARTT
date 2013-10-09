package structure;

import learning.Term;
import learning.InfoGainScore;
//import bdlearner.ScoredTerm;
import learning.TermClassTree;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.*;
import jds.collection.BinarySearchTree;

/**
 * <p>Title: StructureMeasure</p>
 * <p>Description: A measure of structuredness of flora corpus</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

/**
 * scoring the structuredness of a collection in terms of term distribution for a level
 *
 * 2. find all the terms with infogain score above a threshold => term list
 * 1. create a BTerm binary search tree
 * 3. for each term in term list, find (the number of classes it occurs in - 1)
 * 4. sum freq of all terms
 * 5. return 1 - (sum / number of terms) / number of classes
 *
 * this algorithm does  not punish the cases where a class has a good number of term with single occurance,
 * it awards this situation, instead. We changed the algorithm to punish these cases.
 */

public class TermDistribution
    extends MeasureAlgorithm {
  private float threshold = 0f;
  public TermDistribution(String[] instances, String[] classes, float threshold) {
    super(instances, classes);
    this.threshold = threshold;
  }

  public float score() {
      TermClassTree tct = new TermClassTree(classes, true, 1);
      String[] xmls = new String[instances.length];
      for (int f = 0; f < instances.length; f++) {
        String xml = instances[f].replaceFirst("^\\s*", "").trim();
        Pattern p = Pattern.compile("^<(.*?)>(.*?)</\\1>");
        Matcher m = p.matcher(xml);
        if (m.lookingAt()) {
          xmls[f] = m.group(2);
        }

        p = Pattern.compile("(.*?)<taxon>.*?</taxon>(.*)");
        m = p.matcher(xmls[f]);
        if(m.lookingAt()){
          xmls[f] = m.group(1)+m.group(2);
        }
      }

      BinarySearchTree termtree = tct.classTermMatrix(xmls, 200);
      //int sum = 0;
      int termcount = 0;
      int nonzeroterms = 0;
      float totalpercentile = 0f;
      int[] classCount = tct.getClassCount();
      InfoGainScore score1 = new InfoGainScore(termtree, classes, classCount);
      BinarySearchTree scored = score1.scoredTerms();
      Enumeration e = scored.elements();
      while (e.hasMoreElements()) {
        Term st = (Term) e.nextElement();
        if (Float.compare( ( (Float) st.getScore(st.getTerm())).floatValue(),
                          threshold) >= 0) {
          termcount++;
          Term bterm = (Term) termtree.findElement(new Term(st.getTerm()));
          int sum = 0;
          int max = 0;
          for(int i = 0; i < classes.length; i++){
            int ccount = bterm.getOccuranceIn(classes[i]) - 1;
            sum += ccount >= 0? ccount: 0;
            max = max < ccount ? ccount : max;
            //System.out.println(bterm.getTerm()+" count for class "+classes[i] +" is "+ ccount +". now sum="+sum+" and max="+max);
          }
          if(sum > 0 && max > 0){
            nonzeroterms++;
            totalpercentile += (float)max / sum;
            //System.out.println("==>count in. nonzeroterms="+nonzeroterms+" percentile="+(float)max/sum);
          }else{
            //System.out.println("==>ignore");
          }
          //int classfreq = Utilities.nonZero(bterm.getClassCount());
          //sum += classfreq - 1;
          //System.out.print("[" + classfreq + "] " + bterm.toString());
        }
      }
      if (termcount == 0) {
        System.err.println("\nThreshold set too high");
        //System.exit(1);
      }
      int n = classes.length;
      System.out.println("total term: " + termcount +" nonzero terms: "+nonzeroterms+ " total percentile: "+totalpercentile+" , class number: " + n);
      float t1 = (float)nonzeroterms/termcount;
      float t2 = totalpercentile/nonzeroterms;
      System.out.println("nonzero terms:"+ t1);
      System.out.println("average percentile of the most signaling term:"+t2);
      return t1 * t2;
    }

  /*public float score() {
    TermClassTree tct = new TermClassTree(classes);
    String[] xmls = new String[files.length];
    for (int f = 0; f < files.length; f++) {
      String xml = learning.Utilities.readFile(files[f]);
      Pattern p = Pattern.compile(".*?<description>(.*?)</description>.*");
      Matcher m = p.matcher(xml);
      if (m.lookingAt()) {
        xmls[f] = m.group(1);
      }

      p = Pattern.compile("(.*?)<taxon>.*?</taxon>(.*)");
      m = p.matcher(xmls[f]);
      if(m.lookingAt()){
        xmls[f] = m.group(1)+m.group(2);
      }
    }

    BinarySearchTree termtree = tct.classTermMatrix(xmls, 200);
    int sum = 0;
    int termcount = 0;
    int[] classCount = tct.getClassCount();
    InfoGainScore score1 = new InfoGainScore(termtree, classes, classCount);
    BinarySearchTree scored = score1.scoredTerms();
    Enumeration e = scored.elements();
    while (e.hasMoreElements()) {
      Term st = (Term) e.nextElement();
      if (Float.compare( ( (Float) st.getScore(st.getTerm())).floatValue(),
                        threshold) >= 0) {
        termcount++;
        Term bterm = (Term) termtree.findElement(new Term(st.getTerm()));
        int classfreq = Utilities.nonZero(bterm.getClassCount());
        sum += classfreq - 1;
        System.out.print("[" + classfreq + "] " + bterm.toString());
      }
    }
    if (termcount == 0) {
      System.err.println("Threshold set too high");
      //System.exit(1);
    }
    int n = classes.length;
    System.out.println("total term: " + termcount + " , class number: " + n);
    return 1 - ( (float) sum / (termcount * n));
  }*/

  public static void main(String[] args) {
    //String srcData =
    //    "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\Level1\\trainingdir-fnct300-level1\\";

    String srcData = "C:\\Documents and Settings\\hong cui\\Research-Exp\\jasist\\trainingdir-fna133\\";
    String[] classes = {
        "description", "taxon", "plant-habit-and-life-style", "roots", "stems",
        "buds", "leaves", "inflorescences", "flowers", "pollen", "fruits",
        "cones", "seeds",
        "spore-related-structures", "gametophytes", "chromosomes", "phenology",
        "compound", "other-features", "other-information"};

    File dir = new File(srcData);
    File[] files = dir.listFiles();

    String[] instances = new String[files.length];
    for (int f = 0; f < files.length; f++) {
      instances[f] = learning.Utilities.readFile(files[f]);
    }

    TermDistribution termDistribution1 = new TermDistribution(instances, classes,
        0.0f);
    try {
      System.out.println("term distribution score: " + termDistribution1.score());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

}
