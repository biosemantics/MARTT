package marttinterface;

import visitor.VisitorDoMarkup;
import visitor.Serializer;
import miner.SemanticLabel;
import java.util.Vector;
import visitor.ElementComposite;
import learning.MarkedSegment;
import knowledgebase.Composite;
import learning.Model;
import java.util.regex.Matcher;

/**
 * <p>Title: User Interface of MARTT </p>
 *
 * <p>Description: Support training example annotation and marked-up example
 * review.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Martt {
  String modelfile = "U:\\research\\projects\\TDWGDemo\\FNA-model";
  final String alg = "SCCP";
  final String kbsc = "kb-l";
  String lrp = "lrp0";
  String kblrp = "kblrp0";
  String knowledgefile = "U:\\research\\projects\\TDWGDemo\\KB";

  public Martt() {

  }

  public String markup(String text) {
    Vector exp = new Vector();
    MarkedSegment temp = new MarkedSegment(text,
                                           new SemanticLabel("", "", "", 0f, 0f,
        ""));
    exp.add(temp);
    ElementComposite ec = null;
    Composite kb = null;
    try {
      ec = (ElementComposite) Serializer.readback(modelfile);
      kb = (Composite) Serializer.readback(knowledgefile);
    }
    catch (SecurityException ex1) {
    }
    catch (IllegalArgumentException ex1) {
    }
    if (ec == null) { //fails to readback
      System.err.println("fails to read the model, program exits");
    }
    int t = 0;
    //the second parameter should be filename, but it seems not to be useful. use "".
    ec.accept(new VisitorDoMarkup(exp, "", kb, "" + t + "", alg, kbsc,
                                  lrp, kblrp), alg); //mark up
    String markedexample = ec.getMarked(0)[0]; //the root element ec should have exactly 1 element in markeds.
    markedexample.replaceAll("<" + Model.nonspecified + ">", "");
    markedexample.replaceAll("</" + Model.nonspecified + ">", "");
    ec.resetMarkeds();
    ec.resetMarkedSegs();
    return markedexample;

  }
}
