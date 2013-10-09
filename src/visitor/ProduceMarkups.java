package visitor;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.*;
import java.util.Vector;
import learning.Utilities;
import learning.MarkedSegment;
import learning.Model;
import knowledgebase.Composite;
import miner.SemanticLabel;
import xmlsimilarity.WordBasedCosineSimilarity;


/**
 * <p>Title: ProduceMarkups</p>
 * <p>Description: after markup system is trained, this class use trained system to mark up examples
 *                 run ElementComposite first to serialize trained tree</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class ProduceMarkups {
  private ElementComposite  ec= null; //the learned model which will be used for markup
  private String[] taxons = null;
  private String outputdir = null;
  private String[] filenames = null;
  private File[] files = null;
  private Hashtable taxonhash = null;
  private String testfolder = null;
  private boolean doscore = false;//score performance
  private boolean checkorder = false; //if a marked up example is not in right order, do not output to disk
  private int size = 0;
  private Composite knowledge = null;
  private String alg = null;
  private String kbsc = null;
  private String lrp = null;
  private String kblrp = null;
  //private String level = null;


  public ProduceMarkups() {

  }

  public ProduceMarkups(String testfolder, String outputdir, ElementComposite ec, Composite knowledge, boolean doscore, boolean checkorder, String alg, String kbsc, String lrp, String kblrp) {
    this.ec = ec;
    this.testfolder = testfolder;
    this.outputdir = outputdir;
    this.size = new File(testfolder).listFiles().length;
    this.taxons = new String[size];
    this.filenames = new String[size];
    File dir = new File(testfolder);
    this.files = dir.listFiles();

    this.taxonhash = new Hashtable();
    this.doscore = doscore;
    this.checkorder = checkorder;
    this.knowledge = knowledge;
    this.alg = alg;
    //this.level = level;
    this.kbsc = kbsc;
    this.lrp = lrp;
    this.kblrp = kblrp;

    /**@todo if doscore, make sure files are marked up ones **/
  }

  /**
   * produce marked ups
   * if test files are marked up ones, also do performance scoring
   */
  public void produce() {
    
    String[] answer = null;
    String[] markedups = new String[size];
    String [] tests = null;
    if (doscore) {
      answer = getAnswerInstances();
    }
    tests = getTestInstances();

 
    String markedupfolder = outputdir.endsWith("/") ? outputdir : outputdir+"/";
    File dir = new File(markedupfolder);
    dir.mkdir();

    //mark up one by one, print and save one by one
    for (int t = 0; t < size; t++) {
      //String instance = getAnInstance(t);???
      String instance = tests[t];
      if(instance.trim().compareTo("") == 0){
        continue;
      }
      Vector exp = new Vector();
      instance = Premarkup.markup(instance);
      MarkedSegment temp = new MarkedSegment(instance, new SemanticLabel("","","",0f,0f,""));
      exp.add(temp);
      ec.accept(new VisitorDoMarkup(exp, filenames[t], knowledge, ""+t+"",alg, kbsc, lrp, kblrp), alg); //mark up

      //printMarkedExamples(ec, t); //t is the index for taxon
      //System.out.println(t);
      //markedups[t] = ec.getMarked(0)[0].replaceAll("<" + Model.nonspecified +
      //    ">", "").replaceAll("</" + Model.nonspecified + ">", "");
      String example = getMarkedExample(ec, t); //t is the index for taxon
      markedups[t] = example;
      ec.resetMarkeds();
      ec.resetMarkedSegs();
      boolean goodorder = true;
      /*if (checkorder) {
        LeftRightSegmentationModel model = ( (LeftRightSegmentationModel) ec.
                                            getModel());
        goodorder = goodOrder(example, model.getTransmatrix(), model.getClasses());
      }*/
      if (goodorder) {
        //String filename = getFileName(taxons[1][t]);
        String filename = filenames[t];
        File file = new File(markedupfolder + filename);
        //String xml = "<?xml version=\"1.0\" encoding=\"ISO8859-1\"?>" +
        //    example;
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            example;
        try {
          //OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(file), "iso8859-1");
          FileWriter output = new FileWriter(file);
          output.write(xml);
          output.close();
        }
        catch (IOException ex) {
          ex.printStackTrace();
        }
      }
      
    }
    System.out.println("marked "+size+" examples at: "+System.currentTimeMillis());
    if (doscore) {
      compare(ec, markedups, answer);
    }
    
	

  }

  /**
       * check to see if xml contains impossible transition from one class to another
   * @param xml
   * @param transmatrix
   * @return
   */
  private boolean goodOrder(String xml, float[][] transmatrix, String[] classes) {
    //remove descriptions and taxon
    Pattern p = Pattern.compile(".*?</taxon>(.*?)</description>");
    Matcher m = p.matcher(xml);
    if (m.lookingAt()) {
      xml = m.group(1);
    }
    String fromtag = "";
    while (xml.trim().compareTo("") != 0) {
      p = Pattern.compile("\\s*<(.*?)>.*?</\\1>(.*)");
      m = p.matcher(xml);
      if (m.lookingAt()) {
        String totag = m.group(1);
        xml = m.group(2);
        if (Float.compare(0, findProb(fromtag, totag, transmatrix, classes)) ==
            0) {
          return false;
        }
        fromtag = totag;
      }
      else {
        System.err.println("ProduceMarkups: impossible");
      }
    }
    //no need to check tag => end, because of big variation
    return false;
  }

  private float findProb(String fromtag, String totag, float[][] matrix,
                         String[] classes) {
    if (fromtag.compareTo("") == 0 || fromtag.compareTo(totag) == 0 ) {
      return 0.1f; //any float > 0: starting from any tag is possible, repeat any tag is possible
    }
    int from = 0;
    int to = 0;
    for (int i = 0; i < classes.length; i++) {
      if (classes[i].compareTo(fromtag) == 0) {
       from = i + 1;
      }
      if(classes[i].compareTo(totag) == 0) {
       to = i + 1;
      }
    }
    return matrix[from][to];
  }

  private String getFileName(String taxon) {
    int i = 0;
    String name = null;
    name = taxon.replaceAll("<.*?>", "-");
    name = name.replaceAll("\\s+", "-");
    name = name.replaceAll("-+", "-");
    name = name.replaceFirst("\\A-", "").replaceFirst("-\\z", "");
    if (taxonhash.containsKey(name)) {
      i = ( (Integer) taxonhash.get(name)).intValue() + 1;
    }
    taxonhash.put(name, new Integer(i));
    return name + i + ".xml";
  }

  public void compare(ElementComposite ec, String[] marked, String[] answers) {
	StringBuffer sb = new StringBuffer();  
    WordBasedCosineSimilarity wbcs = new WordBasedCosineSimilarity(
    WordBasedCosineSimilarity.VERIFICATION);
    float total = 0f;
    for (int i = 0; i < size; i++) {
      Vector v1 = new Vector();
      marked[i] = marked[i].replaceFirst("null", "");
      marked[i] = learning.Utilities.removeTaxon(marked[i]);
      answers[i] = answers[i].replaceFirst("null", "");
      answers[i] = learning.Utilities.removeTaxon(answers[i]);
      MarkedSegment temp = new MarkedSegment(marked[i].replaceFirst("<description>", "").replaceFirst("</description>", ""),
                                             new SemanticLabel("", "", "",
          0f, 0f, ""));
      v1.add(temp);
      //v1.add(marked[i]);
      ec.accept(new VisitorInsertMarkup(v1, "", knowledge, ""+i+"", alg, kbsc, lrp, kblrp), alg);

      Vector v2 = new Vector();
      v2.add(answers[i]);
      ec.accept(new VisitorInsertAnswer(v2, "", knowledge, ""+i+"", alg, kbsc, lrp, kblrp),alg);

      float sim = wbcs.compute(answers[i], marked[i]);
      total += sim;
      sb.append("CosineSimilarity: " + sim + " on " + filenames[i] +"\n");
     }
     //precision-recall commentout to save time
     //ec.accept(new VisitorScore(new PRSScore()), alg);
     //score(ec, "allnodes");
     sb.append("Averaged CosinSimilarity: " + total / size);
     try {
         FileWriter output = new FileWriter(System.getProperty("user.dir")+"\\performance.txt");
         output.write(sb.toString());
         output.close();
       }
       catch (IOException ex) {
         ex.printStackTrace();
       }
  }

  /**
   *
   * @param ec scored hierarchy
   * @param mode one of "toplevel" and "allnodes". toplevel gives top level score, allnodes gives all node scores
   */

  public void score(ElementComposite ec, String mode) {
    ArrayList top = null;
    if (mode.compareTo("toplevel") == 0) {
      top = ec.getChildScores();
    }
    else {
      top = ec.getAllScores();
    }
    printScore(top);
  }

  public void printScore(ArrayList avg) {
    Iterator it = avg.iterator();
    while (it.hasNext()) {
      MapEntry me = (MapEntry) it.next();
      System.out.println( (String) me.getKey() + " " +
                         ( (Score) me.getValue()).toString());
    }
  }

  /**
   * index is the index for taxon
   * @param ec
   * @param size
   * @return
   */
  public String getMarkedExample(ElementComposite ec, int index) {
    //obtain marked up examples
    Pattern p = Pattern.compile("(<description>)(.*)");

    String markedexample = ec.getMarked(0)[0]; //the root element ec should have exactly 1 element in markeds.
    Matcher m = p.matcher(markedexample);
    if (m.lookingAt()) {
      //piece back taxons
      markedexample = m.group(1) + taxons[index] + m.group(2);
    }
    markedexample.replaceAll("<" + Model.nonspecified + ">", "");
    markedexample.replaceAll("</" + Model.nonspecified + ">", "");
    return markedexample;
  }

  public void printMarkedExamples(ElementComposite ec, int index) {
    //obtain marked up examples
    String markedexample = getMarkedExample(ec, index);
    System.out.println("\n" + markedexample + "\n");
  }

  private String getAnInstance(int i){
    filenames[i] = files[i].getName();
    String instance = learning.Utilities.readFile(files[i]);
    instance = removeTaxon(instance, i); //save taxons section
    instance = new learning.TextPreprocessing(instance).replaceSpecialChar();
    return instance;
  }
  
  public String[] getFileNames(){
	  return filenames;
  }

  public String[] getAnswerInstances() {
    File dir = new File(testfolder);
    File[] files = dir.listFiles();
    String[] insts = new String[files.length];
    for (int i = 0; i < files.length; i++) {
      filenames[i] = files[i].getName();
      insts[i] = learning.Utilities.readFile(files[i]);
      insts[i] = removeTaxon(insts[i], i); //save taxons section
      insts[i] = new learning.TextPreprocessing(insts[i]).
          replaceSpecialChar();
    }
    return insts;
  }

  public String[] getTestInstances() {
    String[] insts = getAnswerInstances();
    insts = learning.Utilities.stripOffTags(insts);
    return insts;
  }

  public String removeTaxon(String xml, int i) {
    String pattern = "<taxon>.*?</taxon>";
    Pattern p = Pattern.compile("(.*?)(" + pattern + ")(.*)");
    Matcher m = p.matcher(xml);
    if (m.lookingAt()) {
      taxons[i] = m.group(2);
      xml = m.group(1) + m.group(3);
    }
    return xml;
  }

  public static void main(String[] args) {
    String dir = args[0];
    String outputdir = args[1];
    String modelfile =args[2];
    String knowledge = args[3];
    String alg = args[4];
    //String level = args[5];
    String scoring = args[5];
    boolean score = Boolean.valueOf(scoring).booleanValue();
    String kbsc = args[6];
    //String dir =
    //    "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level1\\foc-test-4";
    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level1\\trainingdir-foc500-merged";
    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level1\\trainingdir-fna500-merged";
    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\FNA\\descriptionsWithoutHTML";
    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\FOC\\descriptions2-standardized";
    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\FOC\\test";

    //String dir = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level1\\focleafstem";
    /*use knowledge base
    String kbpath = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level2\\kb-fna";
    Composite rkb = (Composite) Serializer.readback(kbpath);
    //TermSemantic ts = rkb.getTermSemanticFor("/description/cones/seed-cones");
    ProduceMarkups pm = new ProduceMarkups(dir, new SemanticClassSegmentationModel(), rkb, true, false);
    */
   Composite kb = null;
   try{
     kb = knowledge.compareTo("null") == 0 ? kb :
         (Composite) visitor.Serializer.readback(knowledge);
   }catch(SecurityException se){
     se.printStackTrace();
   }catch(IllegalArgumentException iae){
     iae.printStackTrace();
   }
    /* create empty models only to retrieve the modelfile
    Model model = null;
    if(alg.compareTo("NB") == 0){
      model = new LeftRightContentSegmentationModel();
    }else if(alg.compareTo("CT") == 0){
      model = new LeftRightContentSegmentationModel();
    }else if(alg.compareTo("LW") == 0){
      model = new LeftRightLeadWordsSegmentationModel();
    }else if(alg.compareTo("LWI") == 0){
      model = new LeftRightLeadWordsSegmentationModel();
    }else if(alg.compareTo("SC") == 0){
      model = new SemanticClassSegmentationModel();
    }else if(alg.compareTo("SCCP") == 0){
      model = new SemanticClassSegmentationModel();
    }else if(alg.compareTo("SCCPI") == 0){
      model = new SemanticClassSegmentationModel();
    }else if(alg.compareTo("SMCP") == 0){
      model = new SemanticModelSegmentationModel();
    }else if(alg.compareTo("SMCPI") == 0){
      model = new SemanticModelSegmentationModel();
    }else{
      System.err.println("markup method "+alg+" is not implemented");
      System.exit(1);
    }*/
    String lrp="lrp0";
    String kblrp = "kblrp0";
    ElementComposite ec = null;
    //markup: read in learned model
    try {
      ec = (ElementComposite) Serializer.readback(modelfile);
    }
    catch (SecurityException ex1) {
    }
     catch (IllegalArgumentException ex1) {
    }
    if (ec == null) { //fails to readback
      System.err.println("fails to read the model, program exits");
    }
    
    ProduceMarkups pm = new ProduceMarkups(dir, outputdir, ec, kb, score, false, alg, kbsc, lrp, kblrp);
    pm.produce();
  }

}
