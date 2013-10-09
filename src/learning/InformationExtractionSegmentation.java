package learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.*;
import knowledgebase.Composite;
import miner.RelativePosition;
import miner.SemanticLabel;
import miner.TermSemantic;
import visitor.ElementComposite;

/**
 * <p>Title: InformationExtractionSegmentation</p>
 * <p>Description: mark up using information extraction regular expression</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class InformationExtractionSegmentation
    extends Segmentation {
  private TermSemantic semantics = null;
  private Hashtable extractionrules = null;
  private Hashtable lengthdistribution = null;
  private RelativePosition relativeposition = null;
  private String[] classes = null;

  public InformationExtractionSegmentation(
      InformationExtractionSegmentationModel model,
      ElementComposite ec, String alg) {
    super(model, ec, alg);
    this.semantics = ( (InformationExtractionSegmentationModel) model).
        getTermSemantic();
    this.classes = ( (InformationExtractionSegmentationModel) model).getClasses();
    this.extractionrules = ( (InformationExtractionSegmentationModel) model).
        getPatterns();
    //String pfile = "C:\\Documents and Settings\\hong cui\\ThesisProject\\Exp\\level2\\patternfile";
    //this.extractionrules = (Hashtable)visitor.Serializer.readback(pfile);
    this.lengthdistribution = ( (InformationExtractionSegmentationModel) model).
        getLengthDistribution();
    this.relativeposition = ( (InformationExtractionSegmentationModel) model).
        getOrder();
    /*StringBuffer sb = new StringBuffer();
         for (int i = 0; i < classes.length; i++) {
      sb.append(classes[i] + " ");
         }*/
  }

  /**
   * why do we need this "order" here?
   * @param example
   * @param knowledge
   * @param order
   * @param debug
   * @return vector has 2 elements, first is a hashtable tag=>content, second is the marked stringbuffer
   */
  public Vector markup(String example, String filename, Composite knowledge, String order,
                       boolean debug, String kbsc, String lrp, String kblrp) {
    Vector markedresult = new Vector();

    String examplecopy = example;
    ExtractionPattern.RegExpExtraction[] record = new ExtractionPattern.
        RegExpExtraction[example.length()]; //record mark result and check for overlaps

    //for now, use extractionrules + term semantics
    //use term semantics to verify extracted segments
    Enumeration en = extractionrules.keys();
    while (en.hasMoreElements()) {
      String tag = (String) en.nextElement();
      if (tag.compareTo("text") != 0) {
        ArrayList rules = (ArrayList) extractionrules.get(tag);
        Iterator it = rules.iterator();
        while (it.hasNext()) {
          example = examplecopy;
          ExtractionPattern.RegExpExtraction rule = (ExtractionPattern.
              RegExpExtraction) it.next();
          Pattern p = Pattern.compile(ExtractionPattern.
                                      expandClasses(rule.getPattern()));
          Matcher m = p.matcher(example);
          int past = 0;
          while (m.find()) {
            String captured = m.group(1);
            int start = m.start(1); //index of the first captured character
            int end = m.end(1); //1+index of the last captured character
            ExtractionPattern.RegExpExtraction applied = element(record, past+start, past+end);
            if (applied == null) {
              Arrays.fill(record, past+start, past+end, rule);
            }
            else { //some other rule has extracted a part that is overlapping with this range
              //give up on this one
            }
            past += end;
            example = example.substring(end);
            m = p.matcher(example);
          }
        }
      }
    }
    //at this point, all marked parts have registered in record
    //now use record to update markedresult
    StringBuffer sb = new StringBuffer();
    Hashtable table = new Hashtable();
    String previous = record[0] == null? "text" : record[0].getTag();
    int start = 0;
    int end = 0;
    int i = 1;
    while(i < record.length){
      String tag = record[i] == null? "text" : record[i].getTag();
      if(tag.compareTo(previous) == 0 ){
        end++;
      }else{
        String seg = examplecopy.substring(start, end+1);
        updateTable(table, seg, previous, record[i-1]);
        sb.append("<"+previous+">"+seg+"</"+previous+">");
        previous = tag;
        start = i;
        end = i;
      }
      i++;
    }
    //when 1 = length
    String seg = examplecopy.substring(start, end + 1);
    updateTable(table, seg, previous, record[i - 1]);
    sb.append("<" + previous + ">" + seg + "</" + previous + ">");

    markedresult.add(table);
    markedresult.add(sb);
    return markedresult;
  }

  private void updateTable(Hashtable table, String seg, String tag, ExtractionPattern.RegExpExtraction rule){
    MarkedSegment segment = new MarkedSegment(seg, rule);
    if(table.containsKey(tag)){
      ((ArrayList)table.get(tag)).add(segment);
    }else{
      ArrayList segments = new ArrayList();
      segments.add(segment);
      table.put(tag, segments);
    }
  }

  private ExtractionPattern.RegExpExtraction element(ExtractionPattern.
      RegExpExtraction[] record, int start, int end) {
    for (int i = start; i < end; i++) {
      if (record[i] != null) {
        return record[i];
      }
    }
    return null;
  }
}