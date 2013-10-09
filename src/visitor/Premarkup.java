package visitor;
import java.util.regex.*;
import java.io.File;

/**
 * <p>Title: Premarkup</p>
 *
 * <p>Description: markup the patterns, such as fr. fl. and x=, 2n= using reg exp </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: UIUC</p>
 *
 * @author not attributable
 * @version 0.1
 */
public class Premarkup {
  //fnct
  //static Pattern chromosome = Pattern.compile("(.*?)(2?\\s*[XxNn]\\s*=\\s*[\\d+*?\\s*,]+\\.)(.*)");
  //foc fna , greedy to the last .
  static Pattern chromosome = Pattern.compile("(.*?)(2?\\s*[XxNn]\\s*=\\s*[\\d+*?\\s*,]+[^<]*\\.)(.*)");

  //fnct
  /*static String month = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)";
  static String season ="([Ss]pring|[Ss]ummer|[Ff]all|[Ww]inter)";
  static String modifier = "([Ll]ate|[Ee]arly|[Mm]id)";
  static String time = modifier+"?"+"\\-?\\s*"+"("+month+"|"+season+")";
  static String p ="(\\(\\s*"+time+"\\s*\\))?\\s*"+time+"\\s*\\-\\s*"+time+"\\s*(\\(\\s*"+time+"\\s*\\))?";
  static Pattern phenology = Pattern.compile("(.*?\\.)([^\\.]{0,10}"+p+"[^\\.]{0,30}\\.)(.*)");
  */
  //foc
  static Pattern phenology = Pattern.compile("(.*?[,\\.]\\s*)((?:[Ff][lr]\\..*?([,\\.]|and)\\s*)+)(.*)");//foc
  public Premarkup() {
  }

  public static String markup(String text){
    if(text.indexOf("The chromosome number") >=0){
      System.out.print("");
    }
    Matcher m = phenology.matcher(text);
    if(m.lookingAt()){
      String temp = m.group(2);
      if(temp.trim().endsWith(".")){
        text = m.group(1) + "<phenology>"+temp+"</phenology>"+m.group(m.groupCount());
      }else{
        String remain = m.group(m.groupCount());
        int i = learning.Model.getDelimiterIndex(".", remain);
        i = i>0? i : remain.length()-1;
        temp += remain.substring(0, i+1);
        text = m.group(1) + "<phenology>"+temp+"</phenology>"+remain.substring(i+1);
      }
      //System.out.println("PHENOLOGY: "+temp);
    }

    m = chromosome.matcher(text);
    if(m.lookingAt()){
       if(m.group(1).trim().endsWith(".") || m.group(1).trim().endsWith(";") ||m.group(1).trim().endsWith(">")){
        text = m.group(1) + "<chromosomes>"+m.group(2)+"</chromosomes>"+m.group(m.groupCount());
      }else{
        String ch = m.group(2);
        ch  = ch.replaceAll("\\)", "\\\\)").replaceAll("\\(", "\\\\(");
        Pattern p = Pattern.compile("(.*?)([A-Z][^A-Z]*?" + ch + ")(.*)");
        m = p.matcher(text);
        if(m.lookingAt()){
          text = m.group(1) + "<chromosomes>" + m.group(2).replaceAll("\\\\", "") +
              "</chromosomes>" +
              m.group(m.groupCount());
        }else{
          //do nothing
        }
      }
      //System.out.println("CHROMOSOMES: "+m.group(2));
    }
    return text;
  }
  public static void main(String[] args) {
    File f = new File("U:\\Research\\projects\\trainingexamplereduction\\samples\\fna500\\");
    //File f = new File("U:\\Research\\FloraData\\FNA\\descriptionsWithoutHTML\\");
    File[] list = f.listFiles();
    for(int i = 0; i < list.length; i++){
      String text = learning.Utilities.readFile(list[i]);
      Premarkup.markup(text);
    }
  }
}
