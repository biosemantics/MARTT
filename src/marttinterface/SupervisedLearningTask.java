package marttinterface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javax.swing.SwingWorker;
import javax.swing.JTextArea;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import learning.MarkedSegment;
import miner.SemanticLabel;

import knowledgebase.Composite;
import visitor.ElementComposite;
import visitor.Premarkup;
import visitor.ProduceMarkups;
import visitor.VisitorDoMarkup;
import visitor.VisitorLearnSemiStructured;

public class SupervisedLearningTask extends Task{
	private String trainfolder = null;
	private String knowledgesource = null;
	
    SupervisedLearningTask(String trainfolder, String jobfolder,
        		String savefolder, String knowledgesource, JTextArea text, IFrame iframe, ProgressBar bar) {
        super(jobfolder, savefolder, text, iframe, bar);	
    	this.trainfolder = trainfolder;
       	this.knowledgesource = knowledgesource;
        }
    		
		/*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
            publish("start supervised learning at: "+System.currentTimeMillis());
    		ElementComposite ec = new ElementComposite();
    		int i = trainfolder.lastIndexOf('\\');
    		String tdir = trainfolder.substring(0, i);
    		String fname = trainfolder.substring(i+1);
    		String alg = "SCCP";
    	    File srcdir = new File(trainfolder);
    	    File[] filelist = srcdir.listFiles();
    	    for (i = 0; i < filelist.length; i++) {
    	      String xml = learning.Utilities.readFile(filelist[i]);
    	      xml = learning.Utilities.removeTaxon(xml);
    	      if (xml.trim().compareTo("") != 0) {
    	        ec.addTrain(xml, filelist[i].getName());
    	      }
    	    }
    		publish("read in "+i+" training examples at: "+System.currentTimeMillis());
    		progress = 1;
    		setProgress(progress);
    		ec.accept(new VisitorLearnSemiStructured(), alg);
            System.out.println("learned the model at: "+System.currentTimeMillis());
            progress +=15;
            setProgress(progress);
          
          //Produce Markups
           boolean doscore = true;
           Composite kb = null;
           try{
        	   kb = knowledgesource ==null ? kb :
        		   (Composite) visitor.Serializer.readback(knowledgesource);
           }catch(SecurityException se){
        	   se.printStackTrace();
           }catch(IllegalArgumentException iae){
        	   iae.printStackTrace();
           }
           String kbsc = kb ==null? "lsc": "kbsc";
          String lrp="lrp0";
          String kblrp = "kblrp0";
          ProduceMarkups pm = new ProduceMarkups(jobfolder,savefolder, ec, kb, doscore, false, "SCCP", kbsc, lrp, kblrp);
          File[] files = new File(jobfolder).listFiles();
          int size =files.length;
          String[] filenames = new String[size];
          //for (i = 0; i < files.length; i++) {
          //    filenames[i] = files[i].getName();
          //}
          String[] answer = null;
          String[] markedups = new String[size];
          String [] tests = null;
          if (doscore) {
              answer = pm.getAnswerInstances();
            }
          tests = pm.getTestInstances();
          filenames = pm.getFileNames();//must be called after getTestInstances();
          String markedupfolder = savefolder.endsWith("/") ? savefolder : savefolder+"/";
          File sdir = new File(markedupfolder);
          sdir.mkdir();

          //mark up one by one, print and save one by one
          for (int t = 0; t < size; t++) {
            String instance = tests[t];
            if(instance.trim().compareTo("") == 0){
              continue;
            }
            Vector exp = new Vector();
            instance = Premarkup.markup(instance);
            MarkedSegment temp = new MarkedSegment(instance, new SemanticLabel("","","",0f,0f,""));
            exp.add(temp);
            ec.accept(new VisitorDoMarkup(exp, filenames[t], kb, ""+t+"",alg, kbsc, lrp, kblrp), alg); //mark up

            String example = pm.getMarkedExample(ec, t); //t is the index for taxon
            markedups[t] = example;
            ec.resetMarkeds();
            ec.resetMarkedSegs();
            boolean goodorder = true;
            if (goodorder) {
              String filename = filenames[t];
              File file = new File(markedupfolder + filename);
              String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                  example;
              try {
                //OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(file), "iso8859-1");
                FileWriter output = new FileWriter(file);
                output.write(xml);
                output.close();
              }
              catch (IOException ex) {
                ex.printStackTrace();
              }
            }
            if(t%4 == 0){
            	progress+=1;
            	setProgress(progress);
            	publish("Mark up "+t+" examples at: "+System.currentTimeMillis());
            }
            
          }
    	  publish("done at: "+System.currentTimeMillis());
    	  publish("scoring...");
    	  if (doscore) {
    	        pm.compare(ec, markedups, answer);
    	  }
    	  publish("performance score saved to: "+System.getProperty("user.dir")+"\\performance");
    	  return null;
      	}

        @Override
        public void process(List<String> chunks) {
        	for (String state : chunks) {
              text.append(state + "\n");
        	}
        }
    }


