package xmlsimilarity;

/**
 * <p>Title: XML Similarity</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Hong Cui
 * @version 1.0
 */
public class VectorElementVerification extends VectorElement {
    public VectorElementVerification(String word, int location, String xpath) {
        super(word, location, xpath);
    }

    public float similarity(VectorElement ve) {
        if (this.word.compareToIgnoreCase(ve.getWord()) == 0 &&
            this.location == ve.getLocation()) {
            String[] tokens = this.xpath.replaceFirst("^/", "").split("/");
            String[] tokens2 = ve.xpath.replaceFirst("^/", "").split("/");
            int divisor = tokens.length > tokens2.length ? tokens.length :
                          tokens2.length;
            int shorter = tokens.length < tokens2.length ? tokens.length :
                          tokens2.length;
            int divident = 0;
            for (int i = 0; i < shorter; i++) {
                if (tokens[i].compareTo(tokens2[i]) == 0) {
                    divident++;
                }else{
                    break;
                }
            }
            //return (divident - 1) /((float) divisor -1);
            /*if (ve.getWord().compareTo("distal") == 0) {
                System.out.println();
            }*/
            return divident >= 2 ? 1f : 0f;

        } else {
            return 0f;
        }
    }

}
