package learning;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 *
 * <p>Description: Thesis Project, everything in this project</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: UIUC</p>
 *
 * @author not attributable
 * @version 0.1
 */
public class test {
  public test() {
  }

  public static Vector getTagList(String xml, String[] delim) {
    Vector result = new Vector();
    ArrayList tags = new ArrayList();
    Hashtable repeat = new Hashtable();
    Pattern p = Pattern.compile("(.*?)<(.*?)>(.*?)</\\2>");
    Matcher m = p.matcher(xml);
    if (!m.lookingAt()) {
      result.add(tags);
      result.add(repeat);
      return result;
    }

    while (xml.compareTo("") != 0) {
      String tag = null;
      String seg = null;
      m = p.matcher(xml);
      if (m.lookingAt()) {
        if (m.group(1).trim().compareTo("") != 0) {
          int rep = repCount(m.group(1), delim);
          tag = Model.nonspecified;
          repeat.put(tag,
                     repeat.get(tag) == null ? Integer.toString(rep) :
                     ( (String) repeat.get(tag)) + " " + rep);
          tags.add(tag);
        }
        tag = m.group(2);
        seg = m.group(3);
        int rep = repCount(seg, delim);
        repeat.put(tag,
                   repeat.get(tag) == null ? Integer.toString(rep) :
                   ( (String) repeat.get(tag)) + " " + rep);
        tags.add(tag);
      }
      else {
        int rep = repCount(xml, delim);
        tag = Model.nonspecified;
        repeat.put(tag,
                   repeat.get(tag) == null ? Integer.toString(rep) :
                   ( (String) repeat.get(tag)) + " " + rep);
        tags.add(tag);
        break;
      }
      //remove first element
      int index = xml.indexOf("</" + tag + ">");
      xml = xml.substring(index + 3 + tag.length()).trim();
    }
    result.add(tags);
    result.add(repeat);
    return result;
  }
  /**
   * count the number of segments, seperated by delim, the text has.
   * @param text String
   * @return int
   */
  private  static int repCount(String text, String[] delim){
    text = text.trim();
    int count = 0;
    while(text.compareTo("") != 0){
      int index = learning.Utilities.findCutPoint(text, delim);
      text = index >=0 && text.length() >= index+1 ? text.substring(index+1) : "";
      count++;
    }
  return count;
}

private static StringBuffer wildcards(String str) {
    StringBuffer sb = new StringBuffer();
    String[] tokens = str.split("\\s+");
    for (int i = 0; i < tokens.length; i++) {
      String[] copy = (String[]) tokens.clone();
      if (copy[i].compareTo("\\w+") != 0){ //&&
          //LearnDelimiter.puncstring.indexOf(copy[i]) < 0) {
        if(copy[i].matches("\\w+")){
          copy[i] = "\\w+";
        }else if(copy[i].matches(".*?\\p{Punct}$")){
          char punct = copy[i].charAt(copy[i].length()-1);
          copy[i] = "\\w+\\s*"+punct;
        }
        //copy[i] = "\\w+";
        sb.append("===");
        for (int j = 0; j < copy.length; j++) {
          String punct = copy[j];

          if(punct.matches("\\p{Alnum}+\\p{Punct}$")){
          char p = punct.charAt(punct.length()-1);
          punct = punct.replaceFirst(p+"","")+"\\s*"+p;
        }
          if (j != copy.length - 1 ) {
            sb.append(punct).append(" ");
          }
          else {
            sb.append(punct);
          }
        }
      }
    }
    return sb;
  }
  public static void main(String[] argv) {
    /*String xml = "<plant-habit-and-life-style>Shrubs, evergreen, 1-2 m.</plant-habit-and-life-style><stems>Stems monomorphic, without short axillary shoots. Bark of 2d-year stems tan, glabrous)</stems><buds>Bud scales 11-13 mm, persistent.</buds><stems>Spines absent.</stems><leaves>Leaves 5-9-foliolate; petioles 2-8 cm. Leaflet blades thick and rigid; surfaces abaxially smooth, shiny, adaxially dull, gray-green; terminal leaflet stalked, blade 6.5-9.3 × 4-7 cm, 1.3-2.3 times as long as wide; lateral leaflet blades ovate or lance-ovate, 4-6-veined from base, base truncate or weakly cordate, margins plane, toothed, with 2-7 teeth 3-8 mm tipped with spines to 1.4-4 × 0.3-0.6 mm, apex acuminate.</leaves><flowers>Inflorescences racemose, dense, 70-150-flowered, 5-17 cm; bracteoles ± corky, apex rounded to acute.</flowers><fruits>Berries dark blue, glaucous, oblong-ovoid, 9-12 mm, juicy, solid</fruits>";
    String[] delim = new String[]{";",":","."};
    Vector v = getTagList(xml, delim);
    ArrayList l = (ArrayList)v.get(0);
    Hashtable h = (Hashtable)v.get(1);
    Iterator i = l.iterator();
    while(i.hasNext()){
      System.out.println((String)i.next());
    }

    Enumeration en = h.keys();
    while(en.hasMoreElements()){
      String tag = (String)en.nextElement();
      System.out.print(tag+">>");
      System.out.println((String)h.get(tag));
    }*/
    /*Pattern p = Pattern.compile("(.*)<phenology>May\\-Oct\\.</phenology>(.*)");
    String marked = "<other-information>L., (flesh-colored), SWAMP MILKWEED.</other-information><stems>Stems erect, usually (40-)70-150(- 250) cm tall;</stems><leaves>leaf blades linear-lanceolate to lanceolate or ovate-elliptic, (3-)5-15 cm long, to ca. 4 cm wide;</leaves><leaves>petioles 3-10(-17) mm long;</leaves><flowers>corolla lobes bright pink (rarely white);</flowers><flowers>gynostegium pale pink (rarely white);</flowers><other-information>hoods with horns.</other-information><other-information>Wet ground.</other-information><phenology>May-Oct.</phenology>";
    Matcher m = p.matcher(marked);
    if(m.lookingAt()){
      System.out.println("matched");
    }*/
    //System.out.println(Math.pow(0.44, ((double)1 / 3)));
    /*String text = "Diam.4cm";
    Pattern p = Pattern.compile("[^0-9(](?<!\\s*ca)(?<!\\s*[Dd]iam)\\.\\s*[0-9]");
    Matcher m = p.matcher(text);
    if (m.find()) {
      System.out.println(m.start());

    }*/

    //System.out.println(wildcards("Branchlets, leaves"));
    String shortest = "Dallas, Denton, Jack, Parker, Tarrant, and Young cos., also Erath, Grayson, and Palo Pinto cos. (Reed 1969a);";
    String text = "";
    Pattern p = Pattern.compile("((?:\\s*\\w+\\s*,){1,})\\s+and\\s+(\\w+)(.*)");
    //Pattern p = Pattern.compile("\\s*(\\w+)\\s+and\\s+(\\w+)(.*)");
    String temp = shortest+" "+text;
    Matcher m = p.matcher(temp);
    if(m.lookingAt()){
      System.out.println(m.group(1));
      System.out.println(m.group(2));
      System.out.println(m.group(3));
    }
  }
}
