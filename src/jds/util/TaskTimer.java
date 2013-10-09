package jds.util;

import jds.util.Reporter;

public class TaskTimer
{

    public TaskTimer(Reporter ioutput) { output = ioutput; }

    private Reporter output;

    public void initialize(int i) { } // default is nothing

    public void doTask(int i) { } // override this method

    public void run(int start, int stop, int step)
    {
	for(int i = start; i <= stop; i += step) {
	    initialize(i);
	    System.gc();
	    long starttime = System.currentTimeMillis();
	    doTask(i);
	    long stoptime = System.currentTimeMillis();
	    int time = (int) (stoptime - starttime);
	    output.addPoint(i, time);
	}
    }
}
