package learning;

/**
 * <p>Title: Learning</p>
 * <p>Description: Learning algorithms for marking up</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public class TermScoreComparator implements java.util.Comparator{
  private int classindex = -1;
  public TermScoreComparator(int c) {
    this.classindex = c;
  }

  /**
   * compare functions in two different ways:
   * 1. if there is a meaningful classindex, assume scores is a hash accessable with the classindex
   * 2. if classindex is -1, then term and its score is just a pair. score is accessable using the term.
   * @param o1
   * @param o2
   * @return
   */
  public int compare(Object o1, Object o2){
    Term term1 = (Term)o1;
    Term term2 = (Term)o2;
    if(classindex == -1 ){
      float v2 = ((Float)term2.getScore(term2.getTerm())).floatValue() * 10000;
      float v1 = ((Float)term1.getScore(term1.getTerm())).floatValue() * 10000;
      /*if(v2 - v1 < 0){
        System.out.println("left:("+v1+","+v2+")");
      }else{
        System.out.println("right:("+v1+","+v2+")");
      }*/
      return (int) ( v2 - v1);
    }else{
      return (int) ( (term2).getScore(classindex) * 10000 -
                    ( (term1).getScore(classindex)) * 10000);
    }
  }
}