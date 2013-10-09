package learning;

import java.util.Iterator;
/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: Thesis Project, everything in this project</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class LearnTransition {
  private String[] classes = null;
  private String[] trainingexamples = null;
  private int[] classcount = null;
  private float[][] matrix = null;
  public final static String starttag = "START";
  public final static String endtag ="END";

  public LearnTransition(String[] trainingexamples, String[] classes, int[] classcount) {
    this.classcount = classcount;
    this.classes = classes;
    this.trainingexamples = trainingexamples;
  }


  private void printMatrix(float[][] matrix, String label){
    int size = matrix[0].length;
    for(int i = 0;  i < classes.length; i++){
      System.out.print(classes[i] +" | ");
    }
    System.out.println();
    System.out.println(label+" matrix: ================================");
    for(int i = 0;  i < size; i++){
      for (int j = 0; j < size; j++) {
        System.out.print(matrix[i][j] + " | ");
      }
      System.out.println();
    }
    System.out.println("end "+label+" matrix: ================================");

  }
  /**
   * size by size matrix, row: from-tag, columne to-tag
   * @return
   */
  public int[][] learnTransCount() {
    int size = classes.length+2; //all classes + start state + end state
    int[][] trans = new int[size][size];
    //int[] classcount = new int[size];
    for(int i = 0;  i < trainingexamples.length; i++){
      Iterator it = Utilities.getTagOrder(trainingexamples[i]);
      String fromtag = starttag;
      int from = getIndex(fromtag);
      while(it.hasNext()){
        String totag = (String)it.next();
        int to = getIndex(totag);
        trans[from][to] += 1;
        //classcount[from]++;
        fromtag = totag;
        from = to;
      }
     int to = getIndex(endtag);
     trans[from][to] += 1;
     //classcount[from]++;
     //classcount[to]++;
    }
    /*System.out.println("count matrix: ================================");
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        System.out.print(trans[i][j] + " | ");
      }
      System.out.println();
    }
    System.out.println("end count matrix: ================================");
    */
    return trans;
  }
  /**
   * row by row, divided by classcount => p(current col class|previous = row class)
   * @return
   */
  public float[][] learnTransMatrix(){
    int[][] counts = learnTransCount();
    int size = classes.length+2;
    float[][] prob = new float[size][size];
    //find probability
    for(int r = 0; r < size; r++){
      for(int c = 0; c < size; c++){
        if(r == 0 || r == size -1){//start || end
          prob[r][c] = (float)counts[r][c]/trainingexamples.length;
        }else {
          prob[r][c] = (float)counts[r][c] / classcount[r - 1]; //classcount do not contain START and END
        }
      }
    }
    return prob;
  }
  /**
   * column by column, divided by classcount => p(previous = row class | current col class)
   * @return
   */
  public float[][] learnConditionMatrix(){
    int[][] counts = learnTransCount();
    int size = classes.length+2;
    float[][] prob = new float[size][size];
    //find probability
    for(int c = 0; c < size; c++){
      for(int r = 0; r < size; r++){
        if(c == 0 || c == size -1){//start || end
          prob[r][c] = (float)counts[r][c]/trainingexamples.length;
        }else {
          prob[r][c] = (float)counts[r][c]/classcount[c - 1]; //classcount do not contain START and END
        }
      }
    }
    return prob;
  }



private int getIndex(String tag){
    //find index of tag in classes
    //return index+1
    //if "START" return 0
    //if "END" return classes.length+1
    if(tag.compareTo(starttag) == 0){
      return 0;
    }
    if(tag.compareTo(endtag) == 0){
      return classes.length+1;
    }
    for(int i = 0; i < classes.length; i++){
      if(classes[i].compareTo(tag) == 0){
        return i+1;
      }
    }
    return 0;//will never reach here
  }

}