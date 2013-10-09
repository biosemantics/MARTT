package jds.sort;

import java.util.Enumeration;
import jds.SortAlgorithm;
import jds.Indexed;
import java.util.Comparator;

public class ShellSort implements SortAlgorithm {
	public ShellSort (Comparator t) { test = t; }
	private Comparator test;

	public void sort (Indexed v) {
		int n = v.size();
		for (int gap = n/2; gap >= 1; gap = gap / 2) {
			for (int i = gap; i < n; i++) {
				Object element = v.elementAt(i);
				int j = i - gap;
				while (j >= 0 && 
				  (test.compare(element,v.elementAt(j)) < 0)) {
					Object val = v.elementAt(j);
					v.setElementAt(val, j+gap);
					j = j - gap;
				}
			v.setElementAt(element, j+gap);
			}
		}
	}
}

