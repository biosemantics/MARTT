package learning;

import java.util.regex.*;
import java.util.Vector;

/**
 * <p>Title: Learning</p>
 * <p>Description: Learning algorithms for marking up</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */

/**
 * tokenize a long string to words
 */

public class Tokenizer {
  //final static String stop = "a an at the or on for as to of with in cm mm m ca.";
  public static String stop = " µm a able about above according accordingly across actually after afterwards again against ago ain't all allow allows almost alone along already also although always am among amongst amoungst amount an and another any anybody anyhow anyone anything anyway anyways anywhere apart appear appreciate appropriate are aren aren't around as at a's aside ask asking associated at available away awfully back became because become becomes becoming been before beforehand begin beginning behind being believe below beside besides best better between beyond both bottom brief broadly but buy by ca call came can cannot cant can't caption cause causes certain certainly changes clearly click cm c'mon come comes commonly computer concerning consequently consider considering conspicuously contain containing contains copy corresponding could couldn couldnt couldn't course cry currently deeply definitely densely describe described despite detail diam did didn didn't different do does doesn doesn't doing don done don't down downwards due during each edu eight eighty either eleven else elsewhere empty end ending enough entirely especially essentially etc even ever every everybody everyone everything everywhere exactly example except extremely far few fifteen fifth fifty fify fill find fire first five followed following follows for former formerly forth forty found four free from front full further furthermore get gets getting give given gives gmt go goes going gone got gotten gov greetings had hadn't happens hardly has hasn hasnt hasn't have haven haven't having he he'd he'll hello help hence her here hereafter hereby herein here's hereupon hers herself he's hi him himself his hither home homepage hopefully how howbeit however htm html http hundred i.e. i'd if ignored i'll i'm immediate in inasmuch inc inc. indeed indicate indicated indicates information initially inner insofar instead int interest into inward is isn isn't it it'd it'll its it's itself i've join just keep keeps kept know known knows last lately later latter latterly least left less lest let let's like liked likely little longer look looking looks loosely ltd m made mainly make makes many may maybe me mean meantime meanwhile merely microsoft might mil mill million mine miss mm moderately more moreover most mostly move mrs msie much must my myself name namely narrowly near nearly necessary need needs neither net netscape never nevertheless new next nine ninety no nobody non none nonetheless noone nor normally not nothing novel now nowhere NULL NUMBER obviously occasionally of off often oh ok okay old on once one ones one's only onto or org other others otherwise ought our ours ourselves out outside over overall own page part particular particularly per perhaps placed please plus possible presumably probably provides proximally put putting que quite rarely rather readily really reasonably recent recently regarding regardless regards relatively reserved respectively right ring roughly said same saw say saying says seasonally second secondly see seeing seem seemed seeming seems seen self selves sensible sent serious seriously seven seventy several shall she she'd she'll she's shorter should shouldn shouldn't show side since sincere site six sixty slightly so some somebody somehow someone something sometime sometimes somewhat somewhere soon sorry sparingly sparsely specified specify specifying stand still stop strongly sub such sup sure system take taken taking tardily tell ten tends test text than thank thanks thanx that that'll thats that's the their theirs them themselves then thence there thereafter thereby therefore therein there'll theres there's thereupon these they they'd they'll they're they've thick thin think third thirty this thorough thoroughly those though thousand three through throughout thru thus till to together too took top toward towards tried tries trillion truly try trying t's twelve twenty twice two under unfortunately unless unlike unlikely until unto upon use used useful uses using usually value various very via viz want wants was wasn wasn't way we web webpage website we'd welcome well we'll went were we're weren weren't we've what whatever what'll what's when whence whenever where whereafter whereas whereby wherein where's whereupon wherever whether which while whither who who'd whoever whole who'll whom whomever who's whose why will willing wish with within without won wonder won't would wouldn wouldn't www yes yet you you'd you'll your you're yours yourself yourselves you've zero"; //leave a space at end
  public Tokenizer() {
  }
  /**
   * @todo when replace numbers to NUMBER, why do we want to make "2n" "NUMBER"?
   * @param longString
   * @param useStopList
   * @return
   */
  public static String[] tokenize(String longString, boolean useStopList) {
    //System.out.println("tokenizer got string:"+longString);
    //String new1 = longString.toLowerCase();
    String new1 = longString;
    Pattern delim = Pattern.compile("\\s+");
    //replace numerical numbers to token
    new1 = replacement(new1, "[0-9]+.?[0-9]*", " NUMBER ");
    //It seems that NUMERICALNUMBER should be include in stoplist.
    new1 = replacement(new1, "d.b.h.", "DBH");
    //new1 = replacement(new1, "[0-9]+.?[0-9]*", "");
    //new1 = replacement(new1,"NUMERICALNUMBER","");
    /*new1 = replacement(new1, "Jan\\W", "MONTH");
         new1 = replacement(new1, "Feb\\W", "MONTH");
         new1 = replacement(new1, "Mar\\W", "MONTH");
         new1 = replacement(new1, "Apr\\W", "MONTH");
         new1 = replacement(new1, "May\\W", "MONTH");
         new1 = replacement(new1, "Jun\\W", "MONTH");
         new1 = replacement(new1, "Jul\\W", "MONTH");
         new1 = replacement(new1, "Aug\\W", "MONTH");
         new1 = replacement(new1, "Sep\\W", "MONTH");
         new1 = replacement(new1, "Oct\\W", "MONTH");
         new1 = replacement(new1, "Nov\\W", "MONTH");
         new1 = replacement(new1, "Dec\\W", "MONTH");*/
    //remove puncturations
    new1 = removePuncs(new1);
    String tokens[] = delim.split(new1);
    //for(int i = 0; i<tokens.length; i++)
    //System.out.println("="+tokens[i]+"=");
    if (tokens.length == 0) {
      return null;
    }
    Vector results = new Vector();
    Pattern p = null;
    Matcher m = null;
    //Stemmer stemmer = new Stemmer();
    int add = 0;
    //remove stop words and stemming

    for (int i = 0; i < tokens.length; i++) {
      if (tokens[i].length() == 0) {
        continue;
      }
      //\u00A0 looks like ' ', but it not.
      tokens[i] = tokens[i].replace('\u00A0', ' ').replaceAll("\\s+", "").
          trim();
      p = Pattern.compile(" " + tokens[i] + " "); //match whole word
      m = p.matcher(stop);
      if (!useStopList) {
        results.addElement(tokens[i]);
        add = 1;
      }
      else if(useStopList && !m.find()) {
        results.addElement(tokens[i]);
        add = 1;
      }
    }

    if (add == 1) {
      return (String[]) results.toArray(new String[1]);
    }
    else {
      return null;
    }
  }

  public static String removePuncs(String new1) {
    new1 = replacement(new1, "[\\]\\[!/%$#@~`^&*(),.;\"'´×±{}\\+\\-\\?]", " ");
    new1 = new1.replaceAll("=", " = ");
    return new1;
  }

  public static String replacement(String original, String pattern,
                                   String replacement) {
    // Create a pattern to match cat
    Pattern p = Pattern.compile(pattern);
    // Create a matcher with an input string
    Matcher m = p.matcher(original);
    StringBuffer sb = new StringBuffer();
    boolean result = m.find();
    // Loop through and create a new String
    // with the replacements
    while (result) {
      m.appendReplacement(sb, replacement);
      result = m.find();
    }
    // Add the last segment of input to
    // the new String
    m.appendTail(sb);
    return sb.toString();
  }

  public static void main(String[] args) {
    Tokenizer tokenizer1 = new Tokenizer();
    String[] str = tokenizer1.tokenize("A B C D", true);
    for (int i = 0; i < str.length; i++) {
      System.out.println("*" + str[i] + "*");
    }
  }

}
