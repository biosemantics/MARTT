package xmlsimilarity;

import java.util.SortedSet;
import java.util.*;
import java.io.*;
import learning.Utilities;
/**
 * <p>Title: XML Similarity</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Hong Cui
 * @version 1.0
 */
//this class finds the family names for FoC records by matching the FoC records with FNA records.
public class NameMatcher {
    String basedir = null;
    String tododir = null;
    Hashtable hash = null;;
    public NameMatcher(String basedir, String tododir) {
        this.basedir = basedir;
        this.tododir = tododir;
    }

    public void match(){
        hash = new Hashtable();
        File base = new File(basedir);
        File todo = new File(tododir);
        File[] baselist = base.listFiles();
        File[] todolist = todo.listFiles();
        int tsize = todolist.length;
        int bsize = baselist.length;
        WordBasedCosineSimilarity wbcs = new WordBasedCosineSimilarity(WordBasedCosineSimilarity.SIMILARITY);
        for(int i = 0; i < tsize; i++){
            String todoxml = learning.Utilities.readFile(todolist[i]);
            TreeSet sorted = new TreeSet(new Pair());
            for(int j = 0; j < bsize; j++){
                String basexml = learning.Utilities.readFile(baselist[j]);
                float score = wbcs.compute(basexml, todoxml);
                sorted.add(new Pair(score, baselist[j].getName()));
            }
            sorted = topten(sorted);
            this.hash.put(todolist[i].getName(), sorted);
        }
    }

    private TreeSet topten(TreeSet sorted){
        TreeSet top = new TreeSet(new Pair());
        for(int i = 0; i < 10; i++){
            Object o = sorted.last();
            top.add(o);
            sorted.remove(o);
        }
        sorted = null;
        System.gc();
        return top;
    }

    /**
     * find out how well the matches were made
     */
    public void score(){
        if(hash != null){
            Enumeration en = hash.keys();
            while (en.hasMoreElements()) {
                String todoname = (String) en.nextElement();
                System.out.println(todoname + " may be: ");
                TreeSet sortednames = (TreeSet) hash.get(todoname);
                Iterator it = sortednames.iterator();
                while (it.hasNext()) {
                    Pair p = (Pair) it.next();
                    System.out.println(p);
                }
                System.out.println();
            }
        }
    }

    public static void main(String[] argv){
        //String fna = "U:\\Research\\dissertation\\ThesisProject\\FNA\\descriptionsWithoutHTML-markedup-level2\\";
        String fna = "U:\\Research\\dissertation\\ThesisProject\\Exp\\level2\\trainingdata\\namematcher\\";
        String foc = "U:\\Research\\dissertation\\ThesisProject\\Exp\\level2\\trainingdata\\namematcher\\";
        NameMatcher nm = new NameMatcher(fna, foc);
        nm.match();
        nm.score();
    }

    class Pair implements Comparator{
        private float score = -1f;
        private String name = null;

        public Pair(){

        }
        public Pair(float score, String name){
            this.score = score;
            this.name = name;
        }
        public float getScore(){
            return this.score;
        }
        public String getName(){
            return this.name;
        }
        public int compare(Object o1, Object o2){
            int diff = Float.compare(((Pair)o1).getScore(),((Pair)o2).getScore());
            return diff != 0? diff : ((Pair)o1).getName().compareTo(((Pair)o2).getName());
        }

        public boolean equal(Object o1, Object o2){
            if(Float.compare(((Pair)o1).score, ((Pair)o2).score)==0 && ((Pair)o1).getName().compareTo(((Pair)o2).getName()) == 0){
                return true;
            }else{
                return false;
            }
        }

        public String toString(){
            return this.name + " ["+this.score+"]";
        }


    }
}
