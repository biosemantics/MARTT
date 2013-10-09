package learning;

import java.util.regex.*;
/**
 * <p>Title: Learning</p>
 * <p>Description: Learning algorithms for marking up</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public class TextPreprocessing {
  private String text = null;
  //public static final String string = "htmltag";
  public static final String string = "";
  public TextPreprocessing(String text) {
    this.text = text;
  }

  //string htmltag will be ignored for statistics
  public String replaceSpecialChar() {
    String st = text.replaceAll("<I>", "");
    st = st.replaceAll("</I>", "");
    st = st.replaceAll("<i>", "");
    st = st.replaceAll("</i>", "");
    st = st.replaceAll("\n", "");
    st = st.replaceAll("\\\\", "");
    st = st.replace('\u00A0',' ');
    st = st.replaceAll("&", "&#38;"); /**@todo replace all special char for XML */
    st = st.replaceAll("&acute;", "&#180;"); //Entity => Decimal
    st = st.replaceAll("&nbsp;", "&#160;"); //Entity => Decimal
    st = st.replaceAll("&micro;", "&#181;"); //Entity => Decimal
    st = st.replaceAll("&plusmn;", "&#177;"); //Entity => Decimal
    st = st.replaceAll("&deg;", "&#176;"); //Entity => Decimal
    st = st.replaceAll("&times;", "&#215;"); //Entity => Decimal
    st = st.replaceAll("&szlig;", "&#223;"); //Entity => Decimal
    st = st.replaceAll("&lt;", 	"&#60;");
    st = st.replaceAll("&gt;","&#62;");

    /**
     * @todo
     */
    //c.c => c. c
    Pattern p = Pattern.compile("(.*?[a-zA-Z])\\.([a-zA-Z].*)");
    Matcher m = p.matcher(st);
    while(m.lookingAt()){
      st = m.group(1)+". "+m.group(2);
      m = p.matcher(st);
    }
    return st;
  }
  /**
   * reverse the effects of replaceSpecialChar
   * @return
   */
  /*public String replaceBackSpecialChar(){
    String st = text.replaceAll("\\[\\[[I|i]\\]\\]", "");
    st = st.replaceAll("\\[\\[/[I|i]\\]\\]","");
    st = st.replaceAll("\\[\\[backwardslash\\]\\]","&#92;"); //for backslash
    return st;
  }*/

  public static void main(String[] args) {
    //TextPreprocessing textPreprocessing1 = new TextPreprocessing();
  }

}
