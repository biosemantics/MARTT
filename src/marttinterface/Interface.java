package marttinterface;

import javax.swing.UIManager;
import java.awt.*;

/**
 * <p>Title: User Interface of MARTT </p>
 * <p>Description: Support training example annotation and marked-up example review.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 1.0
 */

public class Interface {
  boolean packFrame = false;

  //Construct the application
  public Interface() {
	  IFrame frame = new IFrame();
	    //Validate frames that have preset sizes
	    //Pack frames that have useful preferred size info, e.g. from their layout
	    if (packFrame) {
	      frame.pack();
	    }
	    else {
	      frame.validate();
	    }
	    //Center the window
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension frameSize = frame.getSize();
	    if (frameSize.height > screenSize.height) {
	      frameSize.height = screenSize.height;
	    }
	    if (frameSize.width > screenSize.width) {
	      frameSize.width = screenSize.width;
	    }
	    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	    frame.setVisible(true);
  }
  //Main method
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    new Interface();
  }


}
