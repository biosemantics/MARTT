package marttinterface;

import javax.swing.SwingWorker;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;
import java.awt.*;

public class Task extends SwingWorker<Void, String> {
	protected IFrame iframe = null;
	protected String jobfolder = null;
	protected String savefolder = null;
	protected ProgressBar bar = null;
	protected JTextArea text = null;
	
	Task(String jobfolder, String savefolder, JTextArea text, IFrame iframe, ProgressBar bar){
		this.iframe = iframe;
		this.jobfolder = jobfolder;
		this.savefolder = savefolder;
		this.text = text;
		this.bar = bar;
	}
	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() {
    	if(!isCancelled()){
    		Toolkit.getDefaultToolkit().beep();
    		bar.setVisible(false);
    		iframe.showInterfaceFrame(savefolder);
    	}
    }

}
