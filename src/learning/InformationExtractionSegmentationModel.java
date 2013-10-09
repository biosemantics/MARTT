package learning;

import java.util.Hashtable;
import miner.RelativePosition;
import miner.TermSemantic;

/**
 * <p>Title: InformationExtractionSegmentationModel</p>
 * <p>Description: learned model for information extraction</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UIUC</p>
 * @author Hong Cui
 * @version 0.1
 */

public class InformationExtractionSegmentationModel
    extends Model {
  private TermSemantic termsemantic = null;
  private Hashtable lengthdistribution = null;
  private RelativePosition order = null;
  private Hashtable patterns = null;
  private String[] classes = null;

  public InformationExtractionSegmentationModel() {
    super();
  }

  public InformationExtractionSegmentationModel(Hashtable patterns,
                                                TermSemantic termsemantic,
                                                Hashtable lengthdistribution,
                                                RelativePosition order, String[] classes) {
    this.patterns = patterns;
    this.termsemantic = termsemantic;
    this.lengthdistribution = lengthdistribution;
    this.order = order;
    this.classes = classes;
  }

  public Hashtable getPatterns(){
    return patterns;
  }

  public TermSemantic getTermSemantic(){
    return termsemantic;
  }

  public Hashtable getLengthDistribution(){
    return lengthdistribution;
  }

  public RelativePosition getOrder(){
    return order;
  }

  public String[] getClasses() {
    return classes;
  }

}