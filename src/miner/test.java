package miner;

import jds.collection.BinarySearchTree;
import java.io.File;
import java.io.FileWriter;
import java.util.regex.*;
import learning.Term;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * <p>Title: Markuper for Taxonomic Treatment</p>
 * <p>Description: Thesis Project, everything in this project</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class test {
  public test() {
  }

  public static float similarity(String text, String matching){
    Pattern p = Pattern.compile("(^.{1,})\\b\\d+.*");
    Matcher m = p.matcher(text);
    if(m.lookingAt()){
      text = m.group(1);
    }
    m = p.matcher(matching);
    if(m.lookingAt()){
      matching = m.group(1);
    }
    String[] tokens = text.split("\\s+");
    String[] mtokens = matching.split("\\s+");
    int n = tokens.length > mtokens.length ? mtokens.length : tokens.length;
    n = n > 5 ? 5 : n;
    int j = 0;
    int score = 0;
    int pointer = 0;
    for(int i = 0; i < n; i++){
      for(; j < mtokens.length; j++){
        int len = tokens[i].length();
        int mlen = mtokens[j].length();
        len = len > mlen ? len - len/3 : mlen - mlen/3;
        if(tokens[i].regionMatches(true, 0, mtokens[j], 0, len)){
           score += n-i;
           pointer = j;
           break;
        }
      }
      j = pointer + 1;
    }
    return (float)2*score/(n*(n+1));
  }

  public static void main(String[] argv){
    /*BinarySearchTree tree = new BinarySearchTree(new Term());
    tree.addElement(new Term("a"));
    tree.addElement(new Term("c"));
    tree.addElement(new Term("f"));
    tree.addElement(new Term("b"));
    tree.removeElement(tree.findElement(new Term("a")));
    Enumeration e = tree.elements();
    while(e.hasMoreElements()){
       Object t = e.nextElement();
      System.out.println();
    }*/
    /*System.out.println('Z'+1);
    String test = " , ";
    System.out.println(test.replaceAll(" (,) ","A\\\\s+B"));*/
    /*Pattern p = Pattern.compile(".*?<(.*?)>([^<]*?[.;])?\\s*home[^<]*?</\\1>(.*)");
    Matcher m = p.matcher("<tag>school;home</tag>");
    if(m.lookingAt()){
      System.out.println(m.group(1));
      System.out.println(m.group(2));
    }*/
    //System.out.println(0d/0d);
    /*String text = "abc.";
    //Pattern p = Pattern.compile("\\s*ovary[ ,.:;].*?,\\s*style[ ,.:;].*?,\\s*stigma[ ,.:;].*?,\\s*decurrent[ ,.:;].*?,\\s*abaxially[ ,.:;].*?,\\s*groove[ ,.:;].*?[,.;:]?");
    Pattern p = Pattern.compile("\\w+[^.]");
    Matcher m = p.matcher(text);
    if(m.lookingAt()){
      System.out.println("match");
    }*/
    /*String str = "&#38;amp;#215; what";
    str = str.replaceAll("&#\\S*;", "");
    System.out.println(str);*/
    /*String t = "<a>aaa</a><b>bbb</b>";
    Pattern p = Pattern.compile("^(<(.*)>[^>]*?</\\2>)(((<(.*)>[^>]*?</\\6>).*|$))");
    Matcher m = p.matcher(t);
    if(m.lookingAt()){
      String inst = m.group(1) + m.group(5);
      System.out.println("inst: " + inst);
    }
    p = Pattern.compile("(<(.*)>[^>]*?</\\2>)(<(.*)>[^>]*?</\\4>)(((<(.*)>[^>]*?</\\8>).*|$))");
    m = p.matcher(t);
    while(m.lookingAt()){
      String inst = m.group(1)+m.group(3)+m.group(7);
      t = m.group(3) + m.group(5);
      m = p.matcher(t);
      System.out.println("inst: "+inst);
    }*/
    /*String ts = "This TESTs (?i) OpTion may<";
    Pattern pt = Pattern.compile("(\\b(?i:feb|jan|may)\\b)");
    Matcher m = pt.matcher(ts);
    if(m.find()){
      System.out.println(m.group(1));
      //System.out.println(m.group(2));
    }*/
    /*String ts = "null";
    ts = ts.replaceFirst("null", "\\$");
    System.out.println(ts);*/

    /*Hashtable SCLASS = new Hashtable();
    SCLASS.put("MoNTh", "\\bJan|Janurary|Feb|February|Mar|March|Apr|April|May|Jun|June|Jul|July|Aug|August|Sep|September|Oct|October|Nov|Novemeber|Dec|December\\b");
    SCLASS.put("UnIt", "\\b(?i:cm|mm|dm|µm|m)\\b");
    SCLASS.put("SeAsOn", "\\b(?i:spring|summer|fall|autume|winter)\\b");
    SCLASS.put("DiAm", "\\b(?i:diam.|diameter|d.b.h.)\\b");
    SCLASS.put("NuMbEr", "\\d+");
    SCLASS.put("VaLuE", "\\d+\\.\\d+");

    String test = "diam.";
    String pt = "\\b(?i:diam\\.|diameter\\b|d\\.b\\.h\\.)";
    System.out.println(pt);
    Pattern p = Pattern.compile(pt);
    Matcher m = p.matcher(test);
    if(m.find()){
      System.out.println("find");
    }*/
    //read serialized files back
    /*String input = "c:\\documents and settings\\hong cui\\thesisproject\\exp\\level2\\trainingdir-foc500-merged-bysent-phls.outputObjects";
    String output = "c:\\documents and settings\\hong cui\\thesisproject\\exp\\level2\\trainingdir-foc500-merged-bysent-phls\\";
    File in = new File(input);
    File[] inlist = in.listFiles();
    for(int i = 0; i < inlist.length; i++){
      String path = inlist[i].getAbsolutePath();
      Vector elems = (Vector) visitor.Serializer.readback(path);
      Enumeration en = elems.elements();
      StringBuffer sb = new StringBuffer("<plant-habit-and-life-style>");
      while(en.hasMoreElements()){
        xmltest.Element elem = (xmltest.Element)en.nextElement();
        String tag = elem.getName();
        String cont = elem.getText();
        if(cont.compareTo("") == 0){
          continue;
        }
        sb.append("<"+tag+">"+cont+"</"+tag+">");
      }
      sb.append("</plant-habit-and-life-style>");
      String xml = sb.toString();
      File out = new File(output+inlist[i].getName());
      try{
        FileWriter fout = new FileWriter(out);
        fout.write(xml);
        fout.close();
      }catch(Exception e){
          e.printStackTrace();
      }
    }*/
    /*String text = "^ sslls $";
    /*text = text.replaceFirst("^\\s*\\^\\s*", "").replaceFirst("\\s*\\$\\s*$", "");
    System.out.println("*"+text+"*");*/

   /* String text= ",(0.5-)1-3(-5, 10?) m,";
    //String pt = ".*?(\\s*\\b(?i:Trees)\\b\\s*)\\s*\\b,\\b\\s*";

    String pt = ".*?(\\(.*?,.*?\\b(?i:cm|mm|dm|µm|m)\\b)";
    Pattern p = Pattern.compile(pt);
    Matcher m = p.matcher(text);
    if(m.find()){
      System.out.println("match "+m.group(1));
    }*/

   /*String a = "test this";
   ArrayList list = new ArrayList();
   list.add(a);
   Hashtable table = new Hashtable();
   table.put("1", list);
   ((ArrayList)table.get("1")).add(0, "test that");
   System.out.println(((ArrayList)table.get("1")).get(0));
         */
   /*String filename = "tt_f_ggg_g_kkk.xml";
   Pattern f = Pattern.compile(".*?f_(\\w+?)[\\._].*");
   Matcher m = f.matcher(filename);
   if(m.lookingAt()){
     System.out.println(m.group(1) );
   }*/

   /*float score = similarity("Inflorescences racemes or panicles, of 1?15","Inflorescences of 1?15 whorls, ");
   System.out.println(score);*/
   String t = "what \\( do you do";
   System.out.println(t.replaceAll("\\\\\\\\",""));

   /*String firstword= "Inflorescences";
   String text = "<inflorescence-general>Inflorescences of 1?15 whorls, floating or emersed;</inflorescence-general><bract>bracts distinct.</bract>";
   Pattern p = Pattern.compile(".*?<(.*?)>.*?\\p{Punct}?\\s*("+firstword+".*?)</\\1>(.*)");
   Matcher m = p.matcher(text);
   if(m.lookingAt()){
     System.out.println(m.group(1));
     System.out.println(m.group(2));

     System.out.println(m.group(3));

   }*/


  }
}
