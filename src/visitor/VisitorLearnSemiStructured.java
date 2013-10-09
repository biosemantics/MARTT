package visitor;

import learning.BayesScore;
import learning.InformationExtractionStrategy;
import learning.SemanticClassStrategy;
import learning.SemanticModelStrategy;
import learning.Model;
import learning.Utilities;
import java.util.regex.*;

/**
 * <p>Title: VisitorLearnSemiStructured</p>
 * <p>Description: learn from FOC and FNA examples.
 * the semistructuredness of FOC and FNA allows to use the punctuation markes
 * to segment text </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class VisitorLearnSemiStructured
    extends VisitorLearn {

  public VisitorLearnSemiStructured() {
    super();
  }

  public Model learnModel(String[] trainingexamples, String[] classes,
                          String alg) {
    //String stdpunc = getStandardPunc(trainingexamples);
    //String stdpunc = getLowestPunc(trainingexamples); with exceptions
    Model model = null;

    if (alg.compareTo("SC") == 0 ||alg.compareTo("SCCP") == 0 || alg.compareTo("SCCPI") == 0) {
            model = (new SemanticClassStrategy(trainingexamples, classes,
                                               new String[] {".",";"}, alg)).
                learnModel(false);
    }
    /*if (stdpunc == null || stdpunc.compareTo(".") == 0) {
      if (alg.compareTo("SMCP") == 0 || alg.compareTo("SMCPI") == 0) {
        model = (new SemanticModelStrategy(trainingexamples, classes,
                                           new String[] {"."}, alg)).
            learnModel(false);
      }
      else if (alg.compareTo("SC") == 0 ||alg.compareTo("SCCP") == 0 || alg.compareTo("SCCPI") == 0) {
        model = (new SemanticClassStrategy(trainingexamples, classes,
                                           new String[] {"."}, alg)).
            learnModel(false);
      }
      else {
        System.err.println("model algorithm " + alg + " is not implemented");
        System.exit(1);
      }
    }
    else if (stdpunc.compareTo(";") == 0) {
      if (alg.compareTo("SMCP") == 0 || alg.compareTo("SMCPI") == 0) {

        model = (new SemanticModelStrategy(trainingexamples, classes,
                                           new String[] {".", ";", ":"}, alg)).
            learnModel(false);
      }
      else if (alg.compareTo("SC") == 0 ||alg.compareTo("SCCP") == 0 || alg.compareTo("SCCPI") == 0) {
        model = (new SemanticClassStrategy(trainingexamples, classes,
                                           new String[] {".", ";", ":"}, alg)).
            learnModel(false);
      }
      else {
        System.err.println("model algorithm " + alg + " is not implemented");
        System.exit(1);
      }
    }
    else if (stdpunc.compareTo(",") == 0) {
      if (alg.compareTo("SMCP") == 0 || alg.compareTo("SMCPI") == 0) {

        model = (new SemanticModelStrategy(trainingexamples, classes,
                                           new String[] {".", ";", ",", ":"}, alg)).
            learnModel(false);
      }
      else if (alg.compareTo("SC") == 0 ||alg.compareTo("SCCP") == 0 || alg.compareTo("SCCPI") == 0) {

        model = (new SemanticClassStrategy(trainingexamples, classes,
                                           new String[] {".", ",", ";", ":"},alg)).
            learnModel(false);
      }
      else {
        System.err.println("model algorithm " + alg + " is not implemented");
        System.exit(1);
      }

    }
    else if (stdpunc.compareTo(" ") == 0) {
      System.exit(1);
      //extraction
      //model = (new InformationExtractionStrategy(trainingexamples, classes,
      //   new String[] {})).learnModel(true);
    }*/
    return model;
  }

  /**
   * .;,(space)is the order
   * @param trainingexamples
   * @return
   */
  private String getLowestPunc(String[] trainingexamples) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < trainingexamples.length; i++) {
      String exp = trainingexamples[i];
      Pattern p = Pattern.compile("<.*?>(.*?)</.*?>(.*)");
      Matcher m = p.matcher(exp);
      while (m.lookingAt()) {
        String cont = m.group(1).trim();
        exp = m.group(2);
        m = p.matcher(exp);
        //collect last non-space char
        char last = cont.charAt(cont.length() - 1);
        sb.append(last);
      }
    }
    String puncstring = sb.toString();
    float total = puncstring.length();
    float period = Utilities.getOccurrence(puncstring, ".") / total;
    float comma = Utilities.getOccurrence(puncstring, ",") / total;
    float semicollon = Utilities.getOccurrence(puncstring, ";") / total;
    float collon = Utilities.getOccurrence(puncstring, ":") / total;
    float others = 1 - period - comma - semicollon - collon;

    String lowest = ".";
    float threshold = 0.05f;
    if (others > threshold) {
      lowest = " ";
    }
    else if (comma > threshold) {
      lowest = ",";
    }
    else if (semicollon > threshold) {
      lowest = ";";
    }
    else if (period > threshold) {
      lowest = ".";
    }
    return lowest;
  }

  /**
   * .;,(space)is the order
   * @param trainingexamples
   * @return
   */
  private String getStandardPunc(String[] trainingexamples) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < trainingexamples.length; i++) {
      String exp = trainingexamples[i];
      Pattern p = Pattern.compile("<.*?>(.*?)</.*?>(.*)");
      Matcher m = p.matcher(exp);
      while (m.lookingAt()) {
        String cont = m.group(1).trim();
        exp = m.group(2);
        m = p.matcher(exp);
        //collect last non-space char
        char last = cont.charAt(cont.length() - 1);
        sb.append(last);
      }
    }
    String puncstring = sb.toString();
    float total = puncstring.length();
    float period = Utilities.getOccurrence(puncstring, ".") / total;
    float comma = Utilities.getOccurrence(puncstring, ",") / total;
    float semicollon = Utilities.getOccurrence(puncstring, ";") / total;
    float collon = Utilities.getOccurrence(puncstring, ":") / total;
    float others = 1 - period - comma - semicollon - collon;
    /*this is flawed: 0.5 . 0.45 ; 0.2 , and 0.3 others => ";" is the lowest.
     but a "."-ended sentence does not neccessarily contain ";", and "." are
     skipped to find the next ";". for example "xxx xxx x xx. xxx xxx xx;"*/
    String lowest = ".";
    float threshold = 0.05f;
    if (others > threshold) {
      lowest = " ";
    }
    else if (comma > threshold) {
      lowest = ",";
    }
    else if (semicollon > threshold) {
      lowest = ";";
    }
    else if (period > threshold) {
      lowest = ".";
    }
    return lowest;

    //change to pick the punc mark that > 95%, otherwise, use learnDelimitor to
    //decide seg. punc. mark on the fly
    /*String stdpunc = "."; //standard punctuation mark used for segmentation
         float threshold = 0.95f;
         if (others > threshold) {
      stdpunc = " ";
         }
         else if (comma > threshold) {
      stdpunc = ",";
         }
         else if (semicollon > threshold) {
      stdpunc = ";";
         }
         else if (period > threshold) {
      stdpunc = ".";
         }
         else {
      stdpunc = null;
         }
         return stdpunc;*/
  }

  public static void main(String[] args) {
    VisitorLearnSemiStructured v = new VisitorLearnSemiStructured();
    System.out.println("loaded");
  }
}
