package marttinterface;


import java.io.*;
import javax.swing.*;
import java.util.*;
	
	
public class UnsupervisedLearningTask extends Task{
		
	    UnsupervisedLearningTask(String jobfolder,
	        		String savefolder, JTextArea text, IFrame iframe, ProgressBar bar) {
	        super(jobfolder, savefolder, text, iframe, bar);	
        }
	    
		/*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            int progress = 0;
            //Initialize progress property.
            setProgress(progress);
            publish("start unsupervised learning at: "+System.currentTimeMillis());
            int i = jobfolder.lastIndexOf('\\');
    		String workdir = jobfolder.substring(0, i)+"\\";
    		String todofoldername = jobfolder.substring(i+1);
    		String databasenameprefix = todofoldername;
    		i = savefolder.lastIndexOf('\\');
    		String savefoldername = savefolder.substring(i+1);
    		databasenameprefix = databasenameprefix.replace("-", "_");
    		String[] args = {"..\\..\\unsupervised\\unsupervised.pl",workdir,todofoldername,savefoldername,"seednouns.txt", "learntnouns.txt", "graphml.xml", databasenameprefix};
    		try{
    			Process p = Runtime.getRuntime().exec("perl", args);
    			if(p.waitFor()!= 0){
    				System.err.println("Unsupervised learning failed");
    				
    			}
    			BufferedReader stdInput = new BufferedReader(new 
    	                 InputStreamReader(p.getInputStream()));

    	        BufferedReader stdError = new BufferedReader(new 
    	                 InputStreamReader(p.getErrorStream()));

    	        // read the output from the command
    	        String s = "";
    	        System.out.println("Here is the standard output of the command:\n");
    	        while ((s = stdInput.readLine()) != null) {
    	        	publish(s+" at "+System.currentTimeMillis());
    	        }
    	        // read any errors from the attempted command
    	        System.out.println("Here is the standard error of the command (if any):\n");
    	        while ((s = stdError.readLine()) != null) {
    	        	publish(s+" at "+System.currentTimeMillis());
    	        }
    		}catch (Exception e){
    			e.printStackTrace();
    		}
    	  return null;
    	}

        @Override
        public void process(List<String> chunks) {
        	for (String state : chunks) {
              text.append(state + "\n");
        	}
        }
    }




