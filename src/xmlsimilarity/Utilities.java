package xmlsimilarity;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * <p>Title: XML Similarity Measures</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public class Utilities {
  public Utilities() {
  }
  public static ArrayList getClauses(String text){
    ArrayList segs = new ArrayList();
    text = text.trim();
    while(text.compareTo("") != 0){
      int i = learning.Utilities.findCutPoint(text, new String[]{".",";"});
      String temp = i > 0 ? text.substring(0, i+1) : text;
      segs.add(temp);
      text = text.substring(temp.length());
    }
    return segs;
  }
/**
   * remove extra white spaces, remove xml markups
   * @param str
   * @return
   */
 public static String normalize(String str){
    Pattern p = Pattern.compile("(.*?)(\\d+)\\.(\\d+)(.*)");
    Matcher m = p.matcher(str);
    while(m.find()){
        String replacement = m.group(2)+"a"+m.group(3);
        str = m.group(1)+replacement+m.group(4);
        m = p.matcher(str);
    }
    return str.replaceAll("<[^<]+?>", " ").replaceAll("\\p{Punct}", " ").replaceAll("\\s+", " ").replaceFirst("^\\s+","").trim();
  }

  public static String[] normalizedTokens(String text, int unit) {
  String[] tokens = null;
  if (unit == WordBasedCosineSimilarity.WORD) {
    tokens = text.split("\\s+");
  }
  else if (unit == WordBasedCosineSimilarity.CLAUS) {
    ArrayList temp = getClauses(text);
    tokens = new String[temp.size()];
    Iterator it = temp.iterator();
    int i = 0;
    while (it.hasNext()) {
      tokens[i++] = normalize( (String) it.next());
    }
  }
  return tokens;
}

  public static Document getDocModel(String xmlstring){
    DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
    Document doc = null;
    try{
      DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
      String xmlin = "<?xml version=\"1.0\" encoding=\"iso8859-1\" ?>";
      xmlstring = xmlstring.indexOf("<?") < 0 ? xmlin+xmlstring : xmlstring;
      doc = dbuilder.parse(new InputSource(new StringReader(xmlstring)));
    }catch(SAXException saxe){
      saxe.printStackTrace();
    }catch(ParserConfigurationException pce){
      pce.printStackTrace();
    }catch(IOException ioe){
      ioe.printStackTrace();
    }
    return doc;
  }
}
