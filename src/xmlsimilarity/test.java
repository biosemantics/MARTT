package xmlsimilarity;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
public class test {
    public test() {
    }

    public static void main(String[] argv){
    Pattern p = Pattern.compile("(\\d+)\\.(\\d+)");
    Matcher m = p.matcher("aaa5.5bbb");
    if(m.find()){
        System.out.println("matched");
    }
    }
}
