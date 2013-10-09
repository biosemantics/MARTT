package learning;

import java.io.Serializable;

import java.util.regex.*;

/**
 * <p>Title: Model</p>
 * <p>Description: The abstract class for all models learned from training data</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

public abstract class Model
    implements Serializable {
  public static final String nonspecified = "non-specified"; //for cdata
  public static final String untagged = "untagged"; //for text to which a tag can't be given with confidence

  public Model() {
  }

  /**
   * to avoid seg at ca.
   * avoid to break parenthsized text unit ()[]{}""''
   * @todo: need more robust way of handling these exceptional cases
   */
  public static int getDelimiterIndex(String delim, String text) {
    //replace () etc with equal number of spaces
    String copy = text;
    //System.out.println("1:"+text.length());
    copy = fillSpacesBetween("[","]",copy);
    copy = fillSpacesBetween("(",")",copy);
    copy = fillSpacesBetween("{","}",copy);
    copy = fillSpacesBetween("\"","\"",copy);
    copy = fillSpacesBetween("'","'",copy);
    text = copy;

    //find the index of delim
    int index = -1;
    if (delim.compareTo(".") == 0) {
      //to avoid to segment at ca., sq. mm, diam.), etc
      //look for .\s*[A-Z] or .\s+[0-9]
      StringBuffer indices = new StringBuffer();

      Pattern p = Pattern.compile(
          "([A-Z]{2,})\\.\\s*[A-Z]");//MILKWEED. Stems...

      Matcher m = p.matcher(text);
      if (m.find()) {
        index = m.start(1)+m.group(1).length();
        indices.append(index + 1).append(" ");
      }

      p = Pattern.compile(
         "[^A-Z](?<!\\ssubg)(?<![Ff]l)(?<![Ff]r)(?<!\\bvar)(?<!\\bsubsp)(?<!\\bet al)\\.\\s*[A-Z]");//avoid T. Smith

      m = p.matcher(text);
      if (m.find()) {
        index = m.start();
        indices.append(index + 1).append(" ");
      }

     /* if(index1< 0 && index2 >= 0){
        return index2;
      }else if(index1 >=0 && index2 < 0){
        return index1;
      }else if(index1 >=0 && index2 >= 0){
        return index1 < index2? index1 : index2;
      }*/

      p = Pattern.compile("\\.\\s*2*\\s*[nx]\\s*=");
      m = p.matcher(text);
      if (m.find()) {
        index = m.start();
        indices.append(index).append(" ");
      }

      p = Pattern.compile("\\.\\s*[<\\(\\[\\{]");
      m = p.matcher(text);
      if (m.find()) {
        index = m.start();
        indices.append(index).append(" ");
      }

      p = Pattern.compile("[^0-9(](?<!\\s*ca)(?<!\\s*approx)(?<!\\s*[Dd]iam)\\.\\s*[0-9]");
      m = p.matcher(text);
      if (m.find()) {
        index = m.start();
        indices.append(index + 1).append(" ");
      }

      /*p = Pattern.compile("[a-z](?<!\\sca)\\.\\s*[0-9]");
             m = p.matcher(text);
             if (m.find()) {
        index = m.start();
        return index + 1;
             }*/

      p = Pattern.compile("\\.\\s*[)\\]}]\\s*");
      m = p.matcher(text);
      if (m.find()) {
        index = m.end() - 1;
        indices.append(index).append(" ");
      }

      p = Pattern.compile("\\.\\s*\\z");
      m = p.matcher(text);
      if (m.find()) {
        index = m.start();
        indices.append(index).append(" ");
      }
      if(indices.length() < 1){
        return -1;
      }else{
        //find the smallest non-nagetiveindex
        String[] inds = indices.toString().split(" ");
        int low = Integer.MAX_VALUE;
        for (int i = 0; i < inds.length; i++) {
          int d = Integer.parseInt(inds[i]);
          if (d >= 0 && low > d) {
            low = d;
          }
        }
        return low;
      }
    }
    else if (delim.compareTo(";") == 0) {
      //take care of &#123;
      /*Pattern p = Pattern.compile("(?<!&#\\S{1,10});");
             Matcher m = p.matcher(text);
             if(m.find()){
        index = m.start();
        return index;
             }*/
      if (text.indexOf("&") >= 0) {
        Pattern p = Pattern.compile("(.*?)(&\\S*;)(.*)");
        Matcher m = p.matcher(text);
        int passed = 0;
        while (m.lookingAt()) {
          //System.err.println("$$$$$$$$$$ "+m.group(2));
          if (m.group(1).indexOf(";") > 0) {
            return passed + m.group(1).indexOf(";");
          }
          else {
            passed += (m.group(1) + m.group(2)).length();
            text = m.group(3);
            m = p.matcher(text);
          }
        }
        return text.indexOf(";") > 0 ? passed + text.indexOf(";") : -1;
      }

      Pattern p = Pattern.compile(";\\s*[)\\]}]\\s*");
      Matcher m = p.matcher(text);
      if (m.find()) {
        index = m.end() - 1;
        return index;
      }

    }
    else {
      Pattern p = Pattern.compile(delim+"\\s*[)\\]}]\\s*");
      Matcher m = p.matcher(text);
      if (m.find()) {
        index = m.end() - 1;
        return index;
      }

    }
    return text.indexOf(delim) ;
  }

  public static String fillSpacesBetween(String left, String right, String text){
    StringBuffer newtext = new StringBuffer();
    Pattern p = Pattern.compile("(.*?)\\"+left+"(.*?)\\"+right+"(.*)");
    Matcher m = p.matcher(text);
    while(m.lookingAt()){
      newtext.append(m.group(1));
      String replace = m.group(2);
      text = m.group(3);
      replace = replace.replaceAll("."," ");
      newtext.append(left+replace+right);
      m = p.matcher(text);
    }
    newtext.append(text);
    //System.out.println(newtext.length());
    return newtext.toString();
  }

  public static void main(String[] argv) {
    //int i = getDelimitorIndex(".", " Smith ( 1996) recognized var. longispicatus and var. pungens in TX and separated the two as follows:");
    //System.out.println(i);
    String text = "pappus awns absent or slightly developed and with erect-hispid teeth. Low moist areas; Fannin and Lamar cos. in Red River drainage; mainly se and e TX. A";
    //String text ="L., var. dog (flesh-colored), SWAMP MILKWEED. Stems erect, usually (40-)70-150(- 250) cm tall; leaf blades linear-lanceolate to lanceolate or ovate-elliptic, (3-)5-15 cm long, to ca. 4 cm wide; petioles 3-10(-17) mm long; corolla lobes bright pink (rarely white); gynostegium pale pink (rarely white); hoods with horns.";
    //String text ="May-Jun. [ E. pallida var. angustifolia (DC.) Cronquist] Barkley (1986) indicated that this species grades into E. pallida to the e; Cronquist (1980) and Gandhi and Thomas (1989) treated it as a variety of E. pallida. We are following";
    int i = getDelimiterIndex(".", text);
    System.out.println(text.substring(0, i+1));
  }

}
