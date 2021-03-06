/*
 * ProgressBar.java
 *
 * Created on __DATE__, __TIME__
 */

package marttinterface;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JTextArea;
import javax.swing.JFrame;
import java.beans.*;

;

/**
 *
 * @author  __USER__
 */
public class ProgressBar extends javax.swing.JFrame implements java.awt.event.ActionListener, 
PropertyChangeListener {

	private String trainfolder = null;
	private String jobfolder = null;
	private String savefolder = null;
	private String knowledgesource = null;
	private String mode = null;
	private IFrame iframe = null;
	private Task task = null;

	/** Creates new form ProgressBar */
	public ProgressBar(String trainfolder, String jobfolder, String savefolder,
			String knowledgesource, String mode, IFrame iframe) {
		this.trainfolder = trainfolder;
		this.jobfolder = jobfolder;
		this.savefolder = savefolder;
		this.knowledgesource = knowledgesource;
		this.mode = mode;
		this.iframe = iframe;
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		jProgressBar1 = new javax.swing.JProgressBar();
		jLabel1 = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTextArea1 = new javax.swing.JTextArea();
		cancel = new javax.swing.JButton();
		back = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jLabel1
				.setText("Learning is in progress. It may take minutes to half a hour to complete. Please stand by.");

		jTextArea1.setColumns(20);
		jTextArea1.setRows(5);
		jScrollPane1.setViewportView(jTextArea1);

		cancel.setText("Cancel and Go to Main Interface");
		cancel.addActionListener(this);

		back.setText("Cancel and Back to Learning Setting");
		back.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				backActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
getContentPane().setLayout(layout);
layout.setHorizontalGroup(
layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
.add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
.add(28, 28, 28)
.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
.add(layout.createSequentialGroup()
.add(back, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 250, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 101, Short.MAX_VALUE)
.add(cancel))
.add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
.add(org.jdesktop.layout.GroupLayout.LEADING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
.add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 548, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
.add(jProgressBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)))
.add(37, 37, 37))
);
layout.setVerticalGroup(
layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
.add(layout.createSequentialGroup()
.addContainerGap()
.add(jLabel1)
.add(33, 33, 33)
.add(jProgressBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
.add(37, 37, 37)
.add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 122, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 22, Short.MAX_VALUE)
.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
.add(cancel)
.add(back))
.addContainerGap())
);							
		pack();
	}// </editor-fold>//GEN-END:initComponents
	
	public void start(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension frameSize = getSize();
	    if (frameSize.height > screenSize.height) {
	      frameSize.height = screenSize.height;
	    }
	    if (frameSize.width > screenSize.width) {
	      frameSize.width = screenSize.width;
	    }
	    setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	    setTitle(mode+" learning");
	    setVisible(true);
		task = mode.compareTo("supervised") == 0? new SupervisedLearningTask(trainfolder, jobfolder,
        		savefolder, knowledgesource, jTextArea1, iframe, this): new UnsupervisedLearningTask(jobfolder, savefolder, jTextArea1, iframe, this);
		task.addPropertyChangeListener(this);
		task.execute();
	}
	


	public void propertyChange(PropertyChangeEvent evt) {
	    int progress = task.getProgress();
	    if (progress == 0 || progress <= 100) {
	    	jProgressBar1.setIndeterminate(true);
	    }else if(progress > 0 && progress <= 100) {
	    	jProgressBar1.setIndeterminate(false); 
	    	jProgressBar1.setString(null);
	    	jProgressBar1.setValue(progress);
	    }
	}
	
	//GEN-FIRST:event_cancelActionPerformed
	public void actionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		setVisible(false);
		task.cancel(true);
		iframe.showInterfaceFrame(null);
		iframe.setVisible(false);
		
		
	}//GEN-LAST:event_cancelActionPerformed

	//GEN-FIRST:event_backActionPerformed
	private void backActionPerformed(java.awt.event.ActionEvent evt) {
		task.cancel(true);
		setVisible(false);
		iframe.getLearnButton().setEnabled(true);
	}//GEN-LAST:event_backActionPerformed
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton back;
	private javax.swing.JButton cancel;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JProgressBar jProgressBar1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextArea jTextArea1;
	// End of variables declaration//GEN-END:variables

}