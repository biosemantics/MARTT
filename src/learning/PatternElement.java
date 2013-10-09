package learning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.*;

/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: Thesis Project, everything in this project</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public abstract class PatternElement implements Serializable{
  protected String[] trainingexamples = null;
  protected int size = 0;

  public PatternElement(String[] trainingexamples) {
    this.trainingexamples = trainingexamples;
    size = trainingexamples.length;
  }

  public abstract void learnPatterns(boolean debug);
  public abstract ArrayList getPatterns();
  public abstract String matchPatterns(String text);


  protected ArrayList getElements(String tag, boolean debug) {
    ArrayList pool = new ArrayList();
    StringBuffer stopwordstring = new StringBuffer();
    for (int i = 0; i < size; i++) {
      if (trainingexamples[i].indexOf("<" + tag + ">") >= 0) {
        Pattern p = Pattern.compile(".*?<" + tag + ">(.*?)</" + tag + ">.*");
        Matcher m = p.matcher(trainingexamples[i]);
        if (m.lookingAt()) {
          //String pt = generatePattern(Tokenizer.removePuncs(m.group(1)), sc, debug);
          String example = m.group(1).trim();
          //remove spaces around punctuation marks
          String[] puncs = LearnDelimiter.puncs;
          for (int t = 0; t < puncs.length; t++) {
            example = example.replaceAll("\\s*\\" + puncs[t], puncs[t]); //remove spaces before puncs
          }
          example = example.replaceAll("\\s+", " ").trim();
          pool.add(example);
          if (debug) {
            System.out.println("compound: " + example);
          }
        }
      }
    }
    return pool;
  }

  protected ArrayList removeCovered(ArrayList pool, String[] goodptn) {
    for (int i = 0; i < pool.size(); i++) {
      String example = (String) pool.get(i);
      if (match(example, goodptn)) {
        pool.remove(i);
        i--;
      }
    }
    return pool;
  }

  private boolean match(String example, String[] patterns) {
    for (int i = 0; i < patterns.length; i++) {
      for (int t = 0; t < LearnDelimiter.puncs.length; t++) {
        patterns[i] = patterns[i].replaceAll(" " + LearnDelimiter.puncs[t] +
                                             " ",
                                             "\\\\s*" + LearnDelimiter.puncs[t] +
                                             "\\\\s*");
      }
      Pattern p = Pattern.compile(patterns[i].replaceAll(" ", "\\\\s+"));
      Matcher m = p.matcher(example);
      if (m.lookingAt()) {
        return true;
      }
    }
    return false;
  }

  /**
   * for each candidate, test and see if it covers any examples that is not annotated with "element"
   * return those candidates that covers only "elements".
   * @param candidates
   * @param element
   * @return
   */
  protected String[] goodPattern(String[] candidates, String element) {
    StringBuffer goodindex = new StringBuffer();
    for (int j = 0; j < candidates.length; j++) {
      String candidate = candidates[j];
      boolean good = true;
      for (int i = 0; i < size; i++) {
        if (!good) {
          break;
        }
        String example = trainingexamples[i];
        while (example.matches(".*?" + candidate + ".*")) {
          //match the begining of an element or a clause
          Pattern p = Pattern.compile(".*?<(.*?)>([^<]*?[.;:])?\\s*" + candidate +
                                      "[^<]*?</\\1>(.*)");
          Matcher m = p.matcher(example);
          if (m.lookingAt()) {
            String tag = m.group(1);
            example = m.group(3);
            if (tag.compareTo(element) != 0) {
              good = false;
              break;
            }
          }
          else {
            break;
          }
        }
      }
      if (good) {
        goodindex.append(j).append(" ");
      }
    }
    String[] goodpatterns = null;
    if (goodindex.length() > 0) {
      String[] indexstr = goodindex.toString().trim().split(" ");
      goodpatterns = new String[indexstr.length];
      for (int i = 0; i < indexstr.length; i++) {
        goodpatterns[i] = candidates[Integer.parseInt(indexstr[i])];
      }
    }
    return goodpatterns;
  }

}
