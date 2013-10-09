package knowledgebase;

import java.io.File;
import java.util.ArrayList;
import learning.Utilities;
import miner.TermSemantic;
import miner.RelativePosition;
import visitor.Serializer;

/**
 * <p>Title: Knowledge Base</p>
 * <p>Description: Constructe a knowledge base from marked up xml document</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class KnowledgeBase {
  private Composite kb = null;
  public static int batchsize = 20000; //the size of a subset

  public KnowledgeBase() {
  }

  /**
   * for a large set of files, we may run into out of memory problems
   * so divide the large set of n equal-sized subsets
       * mine knowledge from a subset at a time, and then add them to the final tree
   *
   * @param filepath
   * @param n
   * @param sup
   * @param conf
   */
  public KnowledgeBase(String filepath, int n, float sup, float conf,
                       String kbpath, boolean debug) {

    File srcdir = new File(filepath);
    File[] files = srcdir.listFiles();
    File[][] filesets = divideIntoSets(files);

    for (int s = 0; s < filesets.length; s++) {
      int len = filesets[s].length;
      String[] xmls = new String[len];
      for (int f = 0; f < len; f++) {
        xmls[f] = Utilities.readFile(filesets[s][f]);
      }
      ProcessComposite apc = new ProcessComposite(null, "description", xmls, n,
                                                  sup, conf, debug);
      Composite akb = (Composite) apc.getKbase();
      apc = null;
      xmls = null;

      if(filesets.length == 1){
        kb = akb;
      }else{
        akb.pruneTS(sup * 0.8f, conf * 0.8f);
        Serializer.serialization(kbpath + s, akb);
        TermSemantic ts = akb.getTermSemanticFor("/description");
        System.out.println("content of "+kbpath+s+" [confidence 0.7, support 0.001]");
        ts.printTermsForClasses(0.001f, 0.7f);
        kb = null;
        }
      /*if (kb == null) {
        kb = akb;
      }
      else {
        Merger.addToKB(kb, akb, sup, conf); //add to kb a newly mined kb
        akb = null;
        }*/
    }
    if(kb!=null){
    kb.pruneTS(sup, conf);
    }
  }

  public Composite getKnowledgeBase() {
    return kb;
  }

  /**
   * divide files into a number of sets, each of which containing "batchsize" of files
   * @param files
   * @return
   */
  private File[][] divideIntoSets(File[] files) {
    int total = files.length;
    int n = 1 + total / batchsize;
    File[][] sets = new File[n][];
    ArrayList[] lists = new ArrayList[n]; //each set may have variable number of files

    for (int s = 0; s < n; s++) {
      lists[s] = new ArrayList();
    }

    for (int i = 0; i < total; ) {
      for (int s = 0; s < n; s++) {
        if (i < total) {
          lists[s].add(files[i]);
          i++;
        }
        else {
          break;
        }
      }
    }

    //add lists to sets
    for (int s = 0; s < n; s++) {
      sets[s] = (File[]) lists[s].toArray(new File[0]);
    }

    return sets;
  }

  /**
   *
   * @param args args[0]:action--create, add, or merge
   *        create:     args[1]: filepath
   *                    args[2]: kbpath
   *                    args[3]: confidence
   *                    args[4]: support
   *                    args[5]: n (max. size of n-grams)
   *                    args[6]: debug
   *        addto/merge args[1]: kbpath1
   *                    args[2]: kbpath2
   *                    args[3]: kbpath
   */
  public static void main(String[] args) {
    String action = args[0];
    if (action.compareTo("create") == 0) {
      String filepath = args[1];
      String kbpath = args[2];
      float conf = Float.parseFloat(args[3]);
      float sup = Float.parseFloat(args[4]);
      int n = Integer.parseInt(args[5]);
      boolean debug = (Boolean.valueOf(args[6])).booleanValue();
      KnowledgeBase base = new KnowledgeBase(filepath, n, sup, conf, kbpath, debug);
      System.out.println("kb is learned");
      Composite kb = base.getKnowledgeBase();
      if(kb != null){
        Serializer.serialization(kbpath, kb);
        System.out.println("kb is written to disk");
      }
      kb = null;
      base = null;
      //if >1 batch, kbpath will not be there, instead kbpath0 and kbpath1 were created
      File kbpathf = new File(kbpath);
      if(kbpathf.exists() && debug){
        kb = (Composite) Serializer.readback(kbpath);
        TermSemantic ts = kb.getTermSemanticFor("/description");
        RelativePosition rp = kb.getRelativePosition();
        System.out.println("stem-2 prob " +rp.selfProbability("stems", 2));
        ts.printTermsForClasses(sup, conf);
        System.out.println("DONE");
      }
    }
    if (action.compareTo("create") != 0) {
      String kbpath1 = args[1];
      String kbpath2 = args[2];
      String kbpath = args[3];
      Composite rkb1 = (Composite) Serializer.readback(kbpath1);
      System.out.println("read in kb1");
      Composite rkb2 = (Composite) Serializer.readback(kbpath2);
      System.out.println("read in kb2");
      if (action.compareTo("add") == 0) {
        Merger.addToKB(rkb1, rkb2, 0.001f, 0.7f);
      }
      else {
        Merger.merge(rkb1, rkb2);
      }
      rkb2 = null;
      rkb1.pruneTS(0.001f, 0.7f);
      Serializer.serialization(kbpath, rkb1);
      System.out.println("written!");
      //run into out of memory here. why don't print from rkb1 directly? 5/25/05
      //outofmemory problem was fixed by setting the initial and maximum heap size of the java virtual machine
      //java -Xms512m -Xmx512m -classpath ...
      rkb1 = null;
      Composite rkb = (Composite) Serializer.readback(kbpath);
      System.out.println("content of "+kbpath+" [confidence 0.7, support 0.001]");
      TermSemantic ts = rkb.getTermSemanticFor("/description");
      ts.printTermsForClasses(0.001f, 0.7f);
    }

    /*String kbpath = "C:\\Docume~1\\hongcu~1\\ThesisProject\\Exp\\level2\\level2\\resultskb\\leaves\\kb-foc+fna-nostop-merge";
    Composite kb = (Composite) Serializer.readback(kbpath);
    TermSemantic ts = kb.getTermSemanticFor("/description");
    ts.printTermsForClasses(0.001f, 0.7f);*/





  }
}
