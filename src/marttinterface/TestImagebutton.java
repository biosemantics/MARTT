/*
 * TestImagebutton.java
 *
 * Created on __DATE__, __TIME__
 */

package marttinterface;

/**
 *
 * @author  __USER__
 */
public class TestImagebutton extends javax.swing.JFrame {

	/** Creates new form TestImagebutton */
	public TestImagebutton() {
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
		jButton1 = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		jButton1
				.setIcon(new javax.swing.ImageIcon(
						"C:\\Documents and Settings\\hongcui\\Desktop\\iConference\\MARTTInterfaceWorkspace\\MARTTInterface\\src\\marttinterface\\images\\favicon.ico"));
		jButton1.setText("jButton1");

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().add(67, 67, 67).add(jButton1)
						.addContainerGap(234, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().add(52, 52, 52).add(jButton1)
						.addContainerGap(223, Short.MAX_VALUE)));
		pack();
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new TestImagebutton().setVisible(true);
			}
		});
	}

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton jButton1;
	// End of variables declaration//GEN-END:variables

}