package xmlsimilarity;

/**
 * <p>Title: XML Similarity Measures</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class VectorElement {
  protected String word = null;
  protected int location = -1;
  protected String xpath = null; // "/" seperated

  public VectorElement(String word, int location, String xpath) {
    this.word = word;
    this.location = location;
    this.xpath = xpath;
  }

  public String getWord(){
    return this.word;
  }

  public String getXPath(){
    return this.xpath;
  }

  public int getLocation(){
    return this.location;
  }
  /**
   * how similar this is with ve?
   * @param <any>
   * @return
   */
  public float similarity (VectorElement ve){
    if(this.word.compareToIgnoreCase(ve.getWord()) == 0){
      String[] tokens = this.xpath.replaceFirst("^/","").split("/");
      String[] tokens2 = ve.xpath.replaceFirst("^/","").split("/");
      int shorter = tokens.length < tokens2.length ? tokens.length : tokens2.length;
      int divident = 0;
      for(int i = 0; i < shorter; i++){
        if(tokens[i].compareTo(tokens2[i]) == 0){
          divident++;
        }
      }
     return 2*(divident - 1) /(tokens.length + tokens2.length-2);
    }else{
      return 0f;
    }
  }

}
