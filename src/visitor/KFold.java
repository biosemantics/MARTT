package visitor;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.*;
import java.util.Random;
import java.util.Vector;
import java.util.Enumeration;
import java.text.DecimalFormat;
import learning.Utilities;
import learning.Model;
import learning.MarkedSegment;
import miner.SemanticLabel;
import knowledgebase.Composite;
import xmlsimilarity.WordBasedCosineSimilarity;

/**
 * <p>Title: KFold</p>
 * <p>Description: KFold validation for an algorithm, K >=1</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class KFold {
  private String trainingfolder = null; //dir to plain file
  private int K = 1;
  private String[][] taxons = null; //two sets, taxons[0] saves taxons for training examples, 1 for test.
  private String[][] filenames = null; //two sets, [0] for training examples, [1] for test examples
  private ArrayList[] scores = null;
  private Hashtable taxonhash = null;
  private Composite knowledge = null;
  private String alg = null;
  private String kbsc = null;
  private String lrp = null;
  private String kblrp = null;
  private String evaluationmode = null;
  private int trainsizetotal = 0;
  private float simtotal = 0f;

  public KFold() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  public KFold(String trainingfolder, int K, Composite knowledge, String alg,
               String kbsc, String lrp, String kblrp, String evaluationmode) {
    this.trainingfolder = trainingfolder;
    this.filenames = new String[2][];
    //this.classes = classes;
    this.K = K;
    //this.algorithm = algorithm;
    this.taxons = new String[2][];
    this.scores = new ArrayList[K];
    this.taxonhash = new Hashtable();
    this.knowledge = knowledge;
    this.alg = alg;
    this.kbsc = kbsc;
    this.lrp = lrp;
    this.kblrp = kblrp;
    this.evaluationmode = evaluationmode;
  }

  /*public KFold(String tobemarkedfolder, String serializedcomposite) {
    this.trainingfolder = trainingfolder;
    //this.classes = classes;
    this.serializedcomposite = serializedcomposite;
    //this.algorithm = algorithm;
     }*/

  /**
   * k fold cross validation
   * print out score, the average of n runs
   */
  public void crossValid() {
    String[] instances = kEvenSets();
    if(instances == null){
      System.err.println("less than "+K+" examples for "+K+" fold validation");
      return;
    }
    ElementComposite ec = new ElementComposite(); //declared here. Need to be shared by different runs.
    //prepare folders to save k-fold marked examples
    File ftemp = new File(trainingfolder);
    String folder = trainingfolder;
    String afolder = folder+"-answers";
    String rfolder = folder+"-results";
    File bf = new File(folder);
    File af = new File(afolder);
    File rf = new File(rfolder);
    try {
      if (!bf.exists()) {
        bf.mkdir();
      }
      if (!af.exists()) {
        af.mkdir();
      }

      if (!rf.exists()) {
        rf.mkdir();
      }
    }
    catch (Exception ioe) {
      ioe.printStackTrace();
    }

    for (int f = 0; f < K; f++) {
      String[][] split = getTrainTestSets(instances, f);
      String[] train = split[0];
      String[] test = split[1];
      String[] answers = new String[test.length];
      //train[0] = "<?xml version=\"1.0\" encoding=\"ISO8859-1\"?> <description><plant-habit-and-life-style>Herbs, annual or perennial, stout, to 70 cm; rhizomes present.</plant-habit-and-life-style><leaves>Leaves emersed or submersed; </leaves><inflorescences>Inflorescences racemes, rarely panicles, 8 cm.</inflorescences><flowers>Flowers 6 mm wide; sepals spreading to recurved, veins not papillate;petals clawed; stamens 9; anthers versatile; pistils 45.</flowers><fruits>Fruits oblanceolate; glands beak terminal, 1.3 mm.</fruits><chromosomes>2n = 22.</chromosomes> </description> "
      //    ;
      //train[1] = "<?xml version=\"1.0\" encoding=\"ISO8859-1\"?> <description><leaves>Leaves basally white with pink or red;single midvein prominently raised, other veins barely or not raised; cross section rhomboid.</leaves><inflorescences>Spadix at anthesis, post-anthesis spadix mm.</inflorescences><flowers>Flowers pollen grains not staining in aniline blue.</flowers><fruits>Fruits not produced in North America.</fruits><chromosomes>2n = 36.</chromosomes> </description>"
      //    ;
      if (K > 1) {
        answers = (String[]) test.clone();
        test = Utilities.stripOffTags(test); //make unmarked test examples
        //training
        //ec = new ElementComposite();
        for (int i = 0; i < train.length; i++) {
          ec.addTrain(train[i], filenames[0][i]); //text along with filename
        }
        //switch: VisitorLearnLessStructured or VisitorLearnSemiStructured
        if (alg.compareTo("CT") == 0 || alg.compareTo("NB") == 0 ||
            alg.compareTo("LW") == 0 || alg.compareTo("LWI") == 0) {
          ec.accept(new VisitorLearnLessStructured(), alg);
        }
        if (alg.compareTo("SC") == 0 || alg.compareTo("SCCP") == 0 ||
            alg.compareTo("SCCPI") == 0 || alg.compareTo("SMCP") == 0 ||
            alg.compareTo("SMCPI") == 0) {
          ec.accept(new VisitorLearnSemiStructured(), alg);
        }

        //markup and save marked up segs in ec
        float total = 0;
        for (int t = 0; t < test.length; t++) {
          //System.out.println(filenames[1][t]);
          Vector exp = new Vector();
          String text = Premarkup.markup(test[t]);
          MarkedSegment temp = new MarkedSegment(text,
                                                 new SemanticLabel("", "", "",
              0f, 0f, ""));
          exp.add(temp);
          ec.accept(new VisitorDoMarkup(exp, filenames[1][t], knowledge,
                                        "" + t + "", alg, kbsc, lrp, kblrp),
                    alg);
          if (evaluationmode.indexOf("sim") >= 0) {
            //use xmlsimilarity
            String xml2 = ec.getMarked(t)[0];
            String xml1 = answers[t];
            //for the creation of confusion matrix in perl
            write2Disk(learning.Utilities.removeTaxon(xml1),
                       learning.Utilities.removeTaxon(xml2),
                       new File(af, filenames[1][t]),
                       new File(rf, filenames[1][t]));
            WordBasedCosineSimilarity wbcs = new WordBasedCosineSimilarity(
                WordBasedCosineSimilarity.VERIFICATION);
            float sim = wbcs.compute(learning.Utilities.removeTaxon(xml1),
                                     learning.Utilities.removeTaxon(xml2));
            total += sim;

            System.out.println("CosineSimilarity: " + sim + " on " +
                               filenames[1][t]);


          }
        }

        if (evaluationmode.indexOf("sim") >= 0) {
          float s = total/test.length;
          System.out.println("CosinSimilarity["+train.length+"]: " + s);
          scoresim(train.length, s, f);
        }
        if (evaluationmode.indexOf("pr") >= 0) {
          //validation
          for (int i = 0; i < answers.length; i++) {
            //ec.addAnswer(answers[i]); //load in answers after finishing markup
            Vector answer = new Vector();
            answer.add(answers[i].replaceAll("<\\?.*?\\?>", ""));
            ec.accept(new VisitorInsertAnswer(answer, "", null, "" + i + "",
                                              alg, kbsc,
                                              lrp, kblrp), alg);
          }
          ec.accept(new VisitorScore(new PRSScore()), alg); //score this round and set the score for ec
          score(ec, "allnodes", f); /**@todo make allnodes variable */
          //printMarkedExamples(ec, size);
          ec.resetAnswers(); //reset for next round
          ec.resetScores();
        }
        ec.resetMarkeds();
        ec.resetMarkedSegs();
        ec.resetTrains();
      }

      //markup: read in learned model
      if (K <= 1) {
        System.err.println(
            "K must be greater than 1. Use ProduceMarkups instead for K = 1");
      }

    }
  }

  private void scoresim(int trainsize, float similarity, int run){

      this.trainsizetotal += trainsize;
      this.simtotal += similarity;
      if(run == K-1){
        System.out.println("Averaged CosinSimilarity["+(float)trainsizetotal/K+"]: " + simtotal/K);
        this.trainsizetotal = 0;
        this.simtotal = 0;
      }
  }
  /**
   * write to
   * @param xml1 String
   * @param xml2 String
   * @param filename String
   */
  private void write2Disk(String xml1, String xml2, File answer, File result) {
    try {
      FileWriter fw = new FileWriter(answer);
      fw.write(xml1);
      fw.flush();
      fw = new FileWriter(result);
      fw.write(xml2);
      fw.flush();
    }
    catch (Exception ioe) {
      ioe.printStackTrace();
    }
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

  /**
   * calculate score for K-fold validation and print it
   * @param ec scored hierarchy
   * @param mode one of "toplevel" and "allnodes". toplevel gives top level score, allnodes gives all node scores
   * @param round add to the score the round-th run
   */

  public void score(ElementComposite ec, String mode, int round) {
    if (mode.compareTo("toplevel") == 0) {
      ArrayList top = ec.getChildScores();
      scores[round] = top;
      //classes = ec.getChildClasses();
    }
    else {
      ArrayList top = ec.getAllScores();
      scores[round] = top;
      //classes = ec.getAllClasses();
    }
    printScore(scores[round]);
    if (round == K - 1) {
      average(scores);
    }
  }

  public void average(ArrayList[] scores) {
    ArrayList avg = new ArrayList();
    Hashtable byclass = new Hashtable();
    ArrayList classlist = new ArrayList();

    for (int j = 0; j < K; j++) {
      ArrayList arun = scores[j];
      Iterator it = arun.iterator();
      while (it.hasNext()) {
        MapEntry aclass = (MapEntry) it.next();
        String tag = (String) aclass.getKey();
        Score score = (Score) aclass.getValue();
        int trainsize = Integer.parseInt( (String) aclass.getInfo());
        Object[] allinfo = (Object[]) byclass.get(tag);
        if (allinfo == null) {
          if (score.isGood()) {
            allinfo = new Object[3];
            allinfo[0] = new Integer(trainsize);
            allinfo[1] = score;
            allinfo[2] = new Integer(1); //count of this class
            byclass.put(tag, allinfo);
            classlist.add(tag);
          }
        }
        else {
          if (score.isGood()) {
            allinfo[0] = new Integer( ( (Integer) allinfo[0]).intValue() +
                                     trainsize);
            allinfo[1] = ( (Score) allinfo[1]).addition(score);
            allinfo[2] = new Integer( ( (Integer) allinfo[2]).intValue() + 1);
          }
        }
      }
    }
    Iterator it = classlist.iterator();
    while (it.hasNext()) {
      String tag = (String) it.next();
      Object[] allinfo = (Object[]) byclass.get(tag);
      int n = ( (Integer) allinfo[2]).intValue();
      float avgtrsize = ( (Integer) allinfo[0]).intValue() / (float) n;
      ( (Score) allinfo[1]).divideBy(n);
      DecimalFormat df = new DecimalFormat("###.#");
      String trsize = df.format(avgtrsize);
      avg.add(new MapEntry(tag, allinfo[1], trsize));
    }

    /*int size = scores[0].size(); //number of classes
         for (int j = 0; j < size; j++) {
      //Score sum = (Score)((MapEntry)scores[0].get(j)).getValue();
      //sum.reset();
      int good = 0;
      Score sum = new PRScore();
      int trainingsize = 0;
      String tag = (String) ( (MapEntry) scores[0].get(j)).getKey();
      for (int i = 0; i < K; i++) {
        String info = (String) ( (MapEntry) scores[i].get(j)).getInfo();
        trainingsize += Integer.parseInt(info);
        Score score = (Score) ( (MapEntry) scores[i].get(j)).getValue();
        if (score.isZero()) {
          good++;
        }
        else if (score.isGood()) {
          sum = sum.addition(score);
          good++;
        }
      }
      sum.divideBy(good);
      DecimalFormat df = new DecimalFormat("###.#");
      String trsize = df.format( (float) trainingsize / K);
      avg.add(new MapEntry(tag, sum, trsize));
         }*/
    //print avg
    printScore(avg);
  }

  public void printScore(ArrayList avg) {
    Iterator it = avg.iterator();
    while (it.hasNext()) {
      MapEntry me = (MapEntry) it.next();
      System.out.println( (String) me.getKey() + "[" + (String) me.getInfo() +
                         "] " +
                         ( (Score) me.getValue()).toString());
    }
  }

  /*public void printScore(ArrayList avg) {
    printScoreFor( "/description/plant-habit-and-life-style", avg);
    printScoreFor( "/description/roots", avg);
    printScoreFor( "/description/buds", avg);
    printScoreFor( "/description/stems", avg);
    printScoreFor( "/description/leaves", avg);
    printScoreFor( "/description/flowers", avg);
    printScoreFor( "/description/pollen", avg);
    printScoreFor( "/description/fruits", avg);
    printScoreFor( "/description/cones", avg);
    printScoreFor( "/description/seeds", avg);
    printScoreFor( "/description/spore-related-structures", avg);
    printScoreFor( "/description/gametophytes", avg);
    printScoreFor( "/description/chromosomes", avg);
    printScoreFor( "/description/phenology", avg);
    printScoreFor( "/description/compound", avg);
    printScoreFor( "/description/other-features", avg);
    printScoreFor( "/description/other-information", avg);
     }*/


  private void printScoreFor(String element, ArrayList avg) {
    Iterator it = avg.iterator();
    while (it.hasNext()) {
      MapEntry me = (MapEntry) it.next();
      String key = (String) me.getKey();
      if (key.compareTo(element) == 0) {
        System.out.println(key + "[" + (String) me.getInfo() +
                           "]: " +
                           ( (Score) me.getValue()).toString());
      }
    }
  }

  public String[] getMarkedExamples(ElementComposite ec, int size) {
    //obtain marked up examples
    String[] markedexamples = new String[size];
    Pattern p = Pattern.compile("(<description>)(.*)");
    for (int i = 0; i < size; i++) {
      String markedtext = ec.getMarked(i)[0]; //the root element ec should have exactly 1 element in markeds.
      markedexamples[i] = markedtext;
      Matcher m = p.matcher(markedexamples[i]);
      if (m.lookingAt()) {
        //piece back taxons
        markedexamples[i] = m.group(1) + taxons[1][i] + m.group(2);
      }
      markedexamples[i].replaceAll("<" + Model.nonspecified + ">", "");
      markedexamples[i].replaceAll("</" + Model.nonspecified + ">", "");
    }
    return markedexamples;
  }

  public void printMarkedExamples(ElementComposite ec, int size) {
    //obtain marked up examples
    String[] markedexamples = getMarkedExamples(ec, size);
    for (int i = 0; i < size; i++) {
      System.out.println("\n" + markedexamples[i] + "\n");
    }
  }

  private String[][] getTrainTestSets(String[] instances, int k) {
    String[][] split = new String[2][];
    //collect files for training (0) and test (1)
    if (K == 1) {
      split[1] = instances[0].split("##"); //test only
      split[0] = null;
    }
    else {
      split[1] = instances[k].split("##"); //an array of filenames for test
      StringBuffer sb = new StringBuffer();
      for (int tr = 0; tr < K; tr++) { //collect all training
        if (tr != k) {
          sb.append(instances[tr] + "##");
        }
      }
      split[0] = sb.toString().trim().split("##"); //an array of filenames for training
    }
    //read file content, in split[][], replace filename with its content
    for (int i = 0; i < 2; i++) {
      if (split[i] != null) {
        taxons[i] = new String[split[i].length];
        //if(i == 1){
        filenames[i] = new String[split[i].length];
        //}
        for (int j = 0; j < split[i].length; j++) {
          //if (i == 1) {
          filenames[i][j] = new File(split[i][j]).getName();
          //}
          split[i][j] = learning.Utilities.readFile(new File(split[i][j]));
          split[i][j] = removeTaxon(split[i][j], i, j); //save taxons section
          split[i][j] = new learning.TextPreprocessing(split[i][j]).
              replaceSpecialChar();
        }
      }
    }
    return split;
  }

  public String removeTaxon(String xml, int i, int j) {
    String pattern = "<taxon>.*?</taxon>";
    Pattern p = Pattern.compile("(.*?)(" + pattern + ")(.*)");
    Matcher m = p.matcher(xml);
    if (m.lookingAt()) {
      taxons[i][j] = m.group(2);
      xml = m.group(1) + m.group(3);
    }
    return xml;
  }

  /**
   * devides all the files in trainingfolder to K even shares
   * @return array of size K, each element is a concated file names of a share, separated by a space
   */
  private String[] kEvenSets() {
    StringBuffer[] sharesb = new StringBuffer[K];
    for (int f = 0; f < K; f++) {
      sharesb[f] = new StringBuffer();
    }
    String[] shares = new String[K];
    File folder = new File(trainingfolder);
    File[] temp = folder.listFiles();
    if (temp == null || temp.length < K) {
      return null;
    }
    //File[] instances = randomize(temp);
    File[] instances = temp; /**@todo change back to randomize*/
    int total = instances.length;
    for (int f = 0; f < total; f++) {
      sharesb[f % K].append(instances[f].getPath() + "##");
    }
    for (int f = 0; f < K; f++) {
      shares[f] = sharesb[f].toString().trim().replaceFirst("##$", "");
    }
    return shares;
  }

  /**
   * randomize the order of the files in files.
   */
  private File[] randomize(File[] files) {
    int size = files.length;
    File[] rand = new File[size];
    int count = 0;
    Random rg = new Random();
    while (count < size) {
      int i = rg.nextInt(size);
      if (rand[i] == null) {
        rand[i] = files[count++];
      }
    }
    return rand;
  }

  public static void main(String[] args) {
    String dir = args[0];
    String alg = args[1];
    String kbpath = args[2]; //kb
    String kbsc = args[3]; //lsc,kbsc, kb-l, l-kb, mix
    String lrp = args[4]; //lrp0, lrp1, lrp2, lrp3, lrp4, lrp5
    String kblrp = args[5]; //kblrp0, kblrp1, kblrp2, kblrp3, kblrp4, kblrp5
    String mode = null;
    if (args.length < 7) {
      mode = "pr";
    }
    else {
      mode = args[6];
    }
    String K = args[7];
    int folds = Integer.parseInt(K);

    Composite rkb = kbpath.compareTo("null") == 0 ? null :
        (Composite) Serializer.readback(kbpath);

      KFold test = new KFold(dir, folds, rkb, alg, kbsc, lrp, kblrp, mode);
      test.crossValid();

  }

  private void jbInit() throws Exception {
  }

}
