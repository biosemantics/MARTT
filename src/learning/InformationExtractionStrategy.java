package learning;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.*;
import miner.TermSemantic;
import miner.RelativePosition;
/**
 * <p>Title: InformationExtractionStrategy</p>
 * <p>Description: Learn extraction patterns and other hints to extract small text blocks</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class InformationExtractionStrategy extends LearningStrategy {
  private int size = 0;
  private int number = 0;
  private float conf = 0f;
  private float sup = 0f;
  private int n = 0;

  public InformationExtractionStrategy(String[] trainingexamples, String[] classes, String[] delim, String alg) {
    super(trainingexamples, classes, delim, alg);
    size = trainingexamples.length;
    number = classes.length;
    optimizeParameters();
  }
  /**
   * for each candidate element, learn:
   * extraction patterns = boundry pattern + content pattern
   * term semantics
   * length by words (mean, min, max)
   * element order
   * @param debug
   * @return InformationExtractionSegmentationModel
   */
  public Model learnModel(boolean debug) {
    TermSemantic ts = new TermSemantic(trainingexamples, classes, n, conf,
                                       sup, delim, false);
    Hashtable ld = new LengthDistribution(trainingexamples, classes).getDistribution();
    RelativePosition order = new RelativePosition(trainingexamples, classes);
    Hashtable patterns = new ExtractionPattern(trainingexamples, classes, 0.1).extractPatterns();
    //Hashtable patterns = null;
    return new InformationExtractionSegmentationModel(patterns, ts, ld, order, classes);
  }

  private void optimizeParameters(){
    n = 2;
    conf = 0.8f;
    sup = 0.035f;
  }


  private class LengthDistribution{
    private String[] classes = null;
    private String[] trainingexamples = null;
    private int size = 0;
    private int number = 0;
    private Hashtable distribution = null;

    public LengthDistribution(String[] trainingexamples, String[] classes){
      this.classes = classes;
      this.trainingexamples = trainingexamples;
      size = trainingexamples.length;
      number = classes.length;
      distribution = computeDistribution();
    }
    /**
     * 1. collect counts as stringbuffer, using hashtable count
     * 2. calculate min, max, and mean from count
     * @return
     */
    private Hashtable computeDistribution(){
      Hashtable count = new Hashtable();
      Pattern p = Pattern.compile(".*?<(.*?)>([^<]*?)</\1>(.*)");
      //1:
      for(int i = 0; i < size; i++){
        String example = trainingexamples[i];
        Matcher m = p.matcher(example);
        while(m.lookingAt()){
          String tag = m.group(1);
          int c = m.group(2).split("\\s+").length;
          updateCount(count, tag, c);
          example = m.group(3);
          m = p.matcher(example);
        }
      }
      //2:
      Enumeration en = count.keys();
      while(en.hasMoreElements()){
        String tag = (String)en.nextElement();
        StringBuffer sb = (StringBuffer)count.get(tag);
        MeanMinMax mmm = getMeanMinMax(sb.toString());
        count.put(tag, mmm);
      }

      return count;
    }

    private MeanMinMax getMeanMinMax(String countstring){
      String[] counts = countstring.split("\\s+");
      int min = Integer.MAX_VALUE;
      int max = Integer.MIN_VALUE;
      int sum = 0;
      for(int i = 0; i < counts.length; i++){
        int c = Integer.parseInt(counts[i]);
        sum += c;
        if(min > c){
          min = c;
        }
        if(max < c){
          max = c;
        }
      }
      return new MeanMinMax((float)sum/counts.length, min, max);
    }
    /**
     *
     * @param count
     * @param tag
     * @param c
     */
    private void updateCount(Hashtable count, String tag, int c){
      StringBuffer sb = new StringBuffer(c).append(" ");
      if(count.containsKey(tag)){
        StringBuffer temp = (StringBuffer)count.get(tag);
        count.put(tag, temp.append(sb));
      }else{
        count.put(tag, sb);
      }
    }

    public Hashtable getDistribution(){
      return distribution;
    }

  }

  private class MeanMinMax{
    private float mean = 0f;
    private float min = 0f;
    private float max = 0f;

    public MeanMinMax(float mean, float min, float max){
      this.mean = mean;
      this.max = max;
      this.min = min;
    }

    public float getMean(){
      return mean;
    }

    public float getMax(){
      return max;
    }

    public float getMin(){
      return min;
    }

  }
}