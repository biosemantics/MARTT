package learning;

import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.TreeSet;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import xmltest.Element;
import javax.swing.border.*;

/**
 * <p>Title: BDLearner</p>
 * <p>Description: Learn to mark up biological descriptions</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class LearnDelimiter {
  private String[] trainingexamples;
  private Hashtable datatable = null;
  private Hashtable classcount = null;
  private String[] classes = null;
  public static final String[] puncs = {
      ",", ";", "."};
  public static final String puncstring = ",;.:";
  /**
   * @param trainingExamples file path to serialized training files
   * @param classes an array of class labels
   */
  public LearnDelimiter(String[] trainingexamples, String[] classes) {
    this.classes = classes;
    datatable = new Hashtable();
    classcount = new Hashtable();
    for (int i = 0; i < classes.length; i++) {
      Hashtable puncmark = new Hashtable();
      for (int p = 0; p < puncs.length; p++) {
        puncmark.put(puncs[p], new Integer(0)); //punctuation mark and its count
      }
      datatable.put(classes[i], puncmark);
      classcount.put(classes[i], new Integer(0));
    }
    this.trainingexamples = trainingexamples; //training text
  }

  /**
   * learn the Support for punctuation marks as element delimitors
   * that is, for each class, find the probablity of its instances ends with
   * certain punctuation mark.
   */
  public Vector learn() {
    Vector rules = new Vector();
    //collect statistics
    createDataTable();
    //find rules
    for (int i = 0; i < classes.length; i++) { //use classes to perserve the order of classes
      String c = classes[i];
      TreeSet ruleSet = new TreeSet(new Rule()); //sorted in decending order
      int total = ( (Integer) classcount.get(c)).intValue();
      Hashtable puncs = (Hashtable) datatable.get(c);
      Enumeration pmarks = puncs.keys();
      while (pmarks.hasMoreElements()) {
        String pmark = (String) pmarks.nextElement();
        float support = ( (Integer) puncs.get(pmark)).floatValue() / total;
        //System.out.println("class="+c+"  mark="+pmark+"  support="+support);
        Rule r = new Rule(pmark, support, -1f); //ignore confidence score
        ruleSet.add(r);
      }
      rules.add(ruleSet);
    }
    return rules;
  }

  /**
   * collect data so the rules can be derived from it
   * dataTable: classname -> puncs -> counts
   *
   */
  private void createDataTable() {
    int size = trainingexamples.length; //size of training set
    for (int i = 0; i < size; i++) {
      String example = trainingexamples[i].replaceFirst("^\\s+", "").trim(); //remove leading/ending spaces
      while (example.compareTo("") != 0) {
        String classl = Utilities.getFirstTag(example);
        String text = Utilities.getFirstText(example).replaceFirst("^\\s+", "").
            trim();
        example = Utilities.removeFirstElement(example);

        if (text.compareTo("") == 0) { //<description>
          continue;
        }
        //update class count for classl
        //System.out.println("Class: "+classl);
        int oldValue = ( (Integer) classcount.get(classl)).intValue();
        //System.out.println("class count: "+oldValue);
        classcount.put(classl, new Integer(oldValue + 1));
        //find the delimitor and update dataTable
        //System.out.println("Text: "+text);
        int match = 0;
        for (int p = 0; p < puncs.length; p++) {
          if (text.endsWith(puncs[p])) {//check point
            //System.out.println("Punc Mark Matched: "+puncs[p]);
            Hashtable h = (Hashtable) datatable.get(classl);
            int value = ( (Integer) h.get(puncs[p])).intValue();
            //System.out.println("Punc Mark Count: "+value);
            ( (Hashtable) datatable.get(classl)).put(puncs[p],
                new Integer(value + 1));
            match = 1;
            break;
          }
        }
        if (match == 0) { //very special cases, for example fr. Aug-Oct(Specimens not seen)
          /**@to do: if no match, check the following element for the starting char.*/
          //System.out.println("Text: "+text);
          //System.out.println("Class: "+classl);
        }
      }
    }
  }

  public static Vector main(String[] args) {
    String[] classes = {
        "PlantHabitAndLifeStyle", "Stems", "Leaves", "Flowers",
        "Fruits", "Cones", "Seeds", "Roots", "SporeRelatedStructures",
        "Flowering", "Fruiting",
        "SeedMaturity", "Compound", "Chromosomes", "Others"};
    String[] trainingexamples = args; //each element is a training example

    LearnDelimiter ld = new LearnDelimiter(trainingexamples, classes);
    Vector rules = ld.learn();
    //System.out.println(Utilities.printRules(rules, classes));
    return rules;
  }

}
