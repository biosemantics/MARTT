package learning;
import java.lang.*;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * <p>Title: Learning</p>
 * <p>Description: Learning algorithms for marking up</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author hong cui
 * @version 0.1a
 */


public class Stemmer {
    int wordSize;
    int containsVowel;
    int endsWithCvc;
    int addAnE;
    int removeAnE;
    int replaceEnd;

    protected int pivot=0;
    protected String stemmingWord;
    protected int idWords;


    public static String[][] step1aRules=
    {
      {"101",  "sses",      "ss",      "3",  "1", "-1",  "NULL"},
      {"102",  "ies",       "i",       "2",  "0", "-1",  "NULL"},
      {"103",  "ss",        "ss",      "1",  "1", "-1",  "NULL"},
      {"104",  "s",         " ",       "0", "-1", "-1",  "NULL"},
      {"000",  "NULL",      "NULL",    "0",  "0",  "0",  "NULL"}
    };


    public static String[][] step1bRules=
    {
      {"105",  "eed",       "ee",       "2",  "1",  "0",  "NULL"},
      {"106",  "ed",        " ",        "1", "-1", "-1",  "ContainsVowel"},
      {"107",  "ing",       " ",        "2", "-1", "-1",  "ContainsVowel"},
      {"000",  "NULL",      "NULL",     "0",  "0",  "0",  "NULL"}
    };


    public static String[][] step1b1Rules =
    {
      {"108",  "at",        "ate",   "1",  "2", "-1",  "NULL"},
      {"109",  "bl",        "ble",   "1",  "2", "-1",  "NULL"},
      {"110",  "iz",        "ize",   "1",  "2", "-1",  "NULL"},
      {"111",  "bb",        "b",     "1",  "0", "-1",  "NULL"},
      {"112",  "dd",        "d",     "1",  "0", "-1",  "NULL"},
      {"113",  "ff",        "f",     "1",  "0", "-1",  "NULL"},
      {"114",  "gg",        "g",     "1",  "0", "-1",  "NULL"},
      {"115",  "mm",        "m",     "1",  "0", "-1",  "NULL"},
      {"116",  "nn",        "n",     "1",  "0", "-1",  "NULL"},
      {"117",  "pp",        "p",     "1",  "0", "-1",  "NULL"},
      {"118",  "rr",        "r",     "1",  "0", "-1",  "NULL"},
      {"119",  "tt",        "t",     "1",  "0", "-1",  "NULL"},
      {"120",  "ww",        "w",     "1",  "0", "-1",  "NULL"},
      {"121",  "xx",        "x",     "1",  "0", "-1",  "NULL"},
      {"122",  " ",         "e",    "-1",  "0", "-1",  "AddAnE"},
      {"000",  "NULL",      "NULL",  "0",  "0",  "0",  "NULL"}
    };

    public static String[][] step1cRules =
    {
      {"123",  "y",          "i",       "0",  "0", "-1",  "ContainsVowel"},
      {"000",  "NULL",       "NULL",    "0",  "0",  "0",  "NULL"}
    };


    public static String[][] step2Rules =
    {
      {"203",  "ational",   "ate",   "6",  "2",  "0",  "NULL"},
      {"204",  "tional",    "tion",  "5",  "3",  "0",  "NULL"},
      {"205",  "enci",      "ence",  "3",  "3",  "0",  "NULL"},
      {"206",  "anci",      "ance",  "3",  "3",  "0",  "NULL"},
      {"207",  "izer",      "ize",   "3",  "2",  "0",  "NULL"},
      {"208",  "abli",      "able",  "3",  "3",  "0",  "NULL"},
      {"209",  "alli",      "al",    "3",  "1",  "0",  "NULL"},
      {"210",  "entli",     "ent",   "4",  "2",  "0",  "NULL"},
      {"211",  "eli",       "e",     "2",  "0",  "0",  "NULL"},
      {"213",  "ousli",     "ous",   "4",  "2",  "0",  "NULL"},
      {"214",  "ization",   "ize",   "6",  "2",  "0",  "NULL"},
      {"215",  "ation",     "ate",   "4",  "2",  "0",  "NULL"},
      {"216",  "ator",      "ate",   "3",  "2",  "0",  "NULL"},
      {"217",  "alism",     "al",    "4",  "1",  "0",  "NULL"},
      {"218",  "iveness",   "ive",   "6",  "2",  "0",  "NULL"},
      {"219",  "fulnes",    "ful",   "5",  "2",  "0",  "NULL"},
      {"220",  "ousness",   "ous",   "6",  "2",  "0",  "NULL"},
      {"221",  "aliti",     "al",    "4",  "1",  "0",  "NULL"},
      {"222",  "iviti",     "ive",   "4",  "2",  "0",  "NULL"},
      {"223",  "biliti",    "ble",   "5",  "2",  "0",  "NULL"},
      {"000",  "NULL",      "NULL",  "0",  "0",  "0",  "NULL"}
    };

    public static String[][] step3Rules =
    {
      {"301",  "icate",     "ic",      "4",  "1",  "0",  "NULL"},
      {"302",  "ative",     " ",       "4", "-1",  "0",  "NULL"},
      {"303",  "alize",     "al",      "4",  "1",  "0",  "NULL"},
      {"304",  "iciti",     "ic",      "4",  "1",  "0",  "NULL"},
      {"305",  "ical",      "ic",      "3",  "1",  "0",  "NULL"},
      {"308",  "ful",       " ",       "2", "-1",  "0",  "NULL"},
      {"309",  "ness",      " ",       "3", "-1",  "0",  "NULL"},
      {"000",  "NULL",      "NULL",    "0",  "0",  "0",  "NULL"}
    };

    public static String[][] step4Rules =
    {
      {"401",  "al",        " ",  "1", "-1",  "1",  "NULL"},
      {"402",  "ance",      " ",  "3", "-1",  "1",  "NULL"},
      {"403",  "ence",      " ",  "3", "-1",  "1",  "NULL"},
      {"405",  "er",        " ",  "1", "-1",  "1",  "NULL"},
      {"406",  "ic",        " ",  "1", "-1",  "1",  "NULL"},
      {"407",  "able",      " ",  "3", "-1",  "1",  "NULL"},
      {"408",  "ible",      " ",  "3", "-1",  "1",  "NULL"},
      {"409",  "ant",       " ",  "2", "-1",  "1",  "NULL"},
      {"410",  "ement",     " ",  "4", "-1",  "1",  "NULL"},
      {"411",  "ment",      " ",  "3", "-1",  "1",  "NULL"},
      {"412",  "ent",       " ",  "2", "-1",  "1",  "NULL"},
      {"423",  "sion",      "s",  "3",  "0",  "1",  "NULL"},
      {"424",  "tion",      "t",  "3",  "0",  "1",  "NULL"},
      {"415",  "ou",        " ",  "1", "-1",  "1",  "NULL"},
      {"416",  "ism",       " ",  "2", "-1",  "1",  "NULL"},
      {"417",  "ate",       " ",  "2", "-1",  "1",  "NULL"},
      {"418",  "iti",       " ",  "2", "-1",  "1",  "NULL"},
      {"419",  "ous",       " ",  "2", "-1",  "1",  "NULL"},
      {"420",  "ive",       " ",  "2", "-1",  "1",  "NULL"},
      {"421",  "ize",       " ",  "2", "-1",  "1",  "NULL"},
      {"000",  "NULL",      "NULL",    "0",  "0",  "0",  "NULL"}
    };


    public static String[][] step5aRules =
    {
      {"501",  "e",         " ",       "0", "-1",  "1",  "NULL"},
      {"502",  "e",         " ",       "0", "-1", "-1",  "RemoveAnE"},
      {"000",  "NULL",      "NULL",    "0",  "0",  "0",  "NULL"}
    };

    public static String[][] step5bRules =
    {
      {"503",  "ll",        "l",     "1",  "0",  "1",  "NULL"},
      {"000",  "NULL",    "NULL",    "0",  "0",  "0",  "NULL"}
    };


    public Stemmer() {

    }


    /**
     * Test si le caractere lu est-il du type vowel ??
     * @param   c   caractere lu.
     * @return   <code> true or false </code>
     */
    public boolean isVowel(char lettre) {
      char c;
      c = Character.toUpperCase(lettre);
      if ((c=='A') || (c=='E') || (c=='I') || (c=='O') || (c=='U')) {
        return true;
      }
      else {
        return false;
      }
    }


   /**
     * Test si le caractere lu est-il du type vowel ??
     * @param   c   caractere lu.
     * @return   <code> true or false </code>
     */
    public boolean isVowelPlusY(char lettre) {
      char c;
      c = Character.toUpperCase(lettre);
      if ((c=='A') || (c=='E') || (c=='I')
           || (c=='O') || (c=='U') || (c=='Y')) {
        return true;
      }
      else {
        return false;
      }
    }

    /**
     * Compter le syllable dans une facon particuliere, compter le nombre
     * de vowel-consonant par paire dans un mot, sans regarder l'initiale
     * du consonant et le final du vowel, le lettre "y" compte comme un
     * consoant dans un debut du mot ou bien il y a un vowel devant lui.
     * par example : <code> "cat" egale 1, "any" egale 1, "amount" egale 2
     * "anything" egale 3 </code>.
     * Note: la facon plus facile et plus rapide de comptage est de traiter
     * avec un automate a l'etat fini. l'etat 0 test sur la premier lettre
     * si ceci est un vowel, alors on va dans l'etat 1, qui est "la derniere
     * mot est un vowel" etat, si le premier lettre est un consonant ou "y"
     * on va dans l'etat 2, qui est "la derniere lettreest un consonant" etat
     * dans etat 1, le "a" et "y" est traitee comme consonant,( parce que ceci
     * est suivi par un vowel), mais dans l'etat 2, "y" est traite comme un
     * vowel, (parce qu'il est suivi par un consonant, le resultat est
     * incrementdans le transition d'un etat 1 a l'etat 2 , parce que c'est
     * le seul occurrant apres un pair de vowel-consonant qu'on a compte.
     * @param  word    le mot qu'on va traiter.
     * @return  un entier.
     */
    public int wordSize(String word){
      int etat=0;
      int resultat=0;
      char c;
      for (int i=0; i< word.length(); i++) {
        c = word.charAt(i);
        switch(etat) {
        case 0: etat = isVowel(c) ? 1 : 2;
          break;
        case 1: etat = isVowel(c) ? 1 : 2;
          if ( etat == 2) { resultat ++;}
          break;
        case 2: etat = (isVowel(c) || ('y'==c )) ? 1 : 2;
          break;
        }
      }
      return resultat;
    }


    /**
     * il y a des regles de reecriture s'appui seulement sur un root qui
     * contenant un vowel, la ou un vowel est une des "aeiou" ou bien "y"
     * avec consonant devant lui.
     * Note: evidament, sous la definition de Vowel, un mot contenant a vowel
     * "iff" ou bien sa premiere lettre est une des "aeiou" ou bien n'import
     * laquel des autres lettres sont "aeiouy",le but est de tester ce
     * condition.
     */

    public int containsVowel(String word) {
      int flag=0;
      if (word.length() ==0 ) {
        return 0;
      }
      else {
        // System.out.println("contains vowel");
        for (int i= 1; i< word.length() ; i++) {
          if (isVowel(word.charAt(0)) || isVowelPlusY(word.charAt(i))) {
            flag = 1; break;
          }
        }
        if ( flag == 1) {
          return 1;
        }
        else {
          return 0;
        }
      }
    }



    /**
     * Voir les 3 dernieres lettres, si le mot fini avec un consonant
     * -vowel-consonant combinasion, et le deuxieme consonant n'est pas
     * "w" ou "x" ou "y" , on return true(1).
     * sinon on return false(0).
     * @param  word String, le mot analyse.
     * @return   <code> 1 si condition saitisfait sinon 0 </code>
     */

    public int endsWithCVC(String word){
      int flag =0;
      if (word.length() < 2) {
        return 0;
      }
      else {
        char firstC = Character.toUpperCase(word.charAt(word.length()-1));
        char secondV= Character.toUpperCase(word.charAt(word.length()-2));
        char thirdC = Character.toUpperCase(word.charAt(word.length()-3));

        if ((!isVowel(firstC) && (firstC != 'W')
             && (firstC != 'X') && (firstC != 'Y'))
            && (isVowelPlusY(secondV))
            && (!isVowel(thirdC))) {
          return 1;
        }
        else {
          return 0;
        }
      }
    }

    /**
     * Regle 122.
     * si le mot courant rencontre un condition special pour ajouter un "e".
     * Note: Tester wordSize = 1 et a mot fini par consonant-vowel-consonant.
     *
     *
     */
    public int addAnE(String word) {
      if  ((wordSize(word) == 1) && (endsWithCVC(word)==1)){
        return 1;
      }
      else {
        return 0;
      }
    }

    /**
     * Regle 502.
     * si le mot courant rencontre un condition special pour enlever un "e"
     * Note: Test wordSize = 1 et a mot non fini par consonant-vowel-consonant
     *
     */

    public int removeAnE(String word){
      if  ((wordSize(word) == 1) && (endsWithCVC(word)!=1)){
        return 1;
      }
      else {
        return 0;
      }
    }


    /**
     * Applique sur une ensemble de regles pour remplacer le suffix de mot.
     * return <id> de regles trouvee. si pas de regles trouvee , return 0.
     * Note: Passer dans l'ensemble de regle jusqu'a on rencontre tous soit
     * applique, si on a trouve une regle, return <id> de ceci.
     * ceci est le coeur de algorithm de stemmer, il passe un ensemble de
     * suffix remplacement regle pour trouver un match dans un courant suffix
     * quand il trouve un, si le racine de ce mot est assez long et il
     * rencontre autres conditions est necessaire, le fonction returne.
     */

    public synchronized String replaceEnd(String word, String[][] rule){
      int flag =0; int leng=0;
      int ending=0;
      for (int i=0; i<rule.length-1; i++) {
        if (rule[i][6].equals("ContainsVowel"))
          flag = containsVowel(word);
        if (rule[i][6].equals("AddAnE"))
          flag = addAnE(word);
        if (rule[i][6].equals("RemoveAnE"))
          flag = removeAnE(word);
        leng = word.length();
        if (leng == 2) {
          return word;
        }
        ending = leng - Integer.valueOf(rule[i][3]).intValue()-1;

        // System.out.println( "Word= "+word+"  ID= "+ rule[i][0]+"  Deplacement= "+ rule[i][3]+
        //            "  longeur= "+leng+"  ending= "+ending+" flag="+ flag);
        if ( ending >= 0){
          if (word.substring(ending).equals(rule[i][1])) {
            //  System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            if (Integer.valueOf(rule[i][5]).intValue() < wordSize(word))
               if (rule[i][6].equals("NULL") || (flag == 1)) {
                 stemmingWord= word.substring(0,ending) + rule[i][2];
                 idWords = Integer.valueOf(rule[i][0]).intValue();
                 //   System.out.println("StemmingWord="+stemmingWord+" et son id="+idWords);
                 pivot=1;
                 return stemmingWord;
               }
          }
          stemmingWord=word;
        }
        else if ((ending < 0 ) && (flag ==0)){
          stemmingWord=word;
          //System.out.println("Stemming Word 1: "+stemmingWord);
          return stemmingWord;
        }
      }

      stemmingWord=word;
    //System.out.println("Stemming Word 2: "+stemmingWord);
      return stemmingWord;
    }


    public synchronized int toGetPivot(){
      return pivot;
    }


    public synchronized String toGetStemmingWord(){
      return stemmingWord;
    }

    public synchronized int toGetIdWords() {
      return idWords;
    }

    /**
     * return false si le mot contenant un non-alphabetic caractere
     * donc il n'est forcement pas un stemmer mot, return 1 sinon.
     * Plan :<p>
     * part 1: Assurer que tous les mots sont alphabetic.<p>
     * part 2: Passer Porter algorithm. ( suffix stripping ). <p>
     * part 3: Return une indication is le "stemming" est succes.
     */

    public synchronized String stem(String word) {
      int flag =0; String stemmer="";
      /** part 1 */
      for (int i =0; i< word.length(); i++) {
        if (!Character.isLetter(word.charAt(i))){
          flag = 1; break;
        }
      }
      if ( flag != 1) {
       /** part 2 */
        stemmer=replaceEnd(word,step1aRules);

        if (toGetPivot() != 1) {
          stemmer=replaceEnd(word,step1bRules);
        }
        int id = toGetIdWords();
        if (((id == 106) || (id == 107)) && (toGetPivot() != 1)) {
          stemmer=replaceEnd(word,step1b1Rules);
        }

        if (toGetPivot() != 1) {
          stemmer=replaceEnd(word, step1cRules);
        }
        if (toGetPivot() != 1) {
          stemmer=replaceEnd(word, step2Rules);
        }
        if (toGetPivot() != 1) {
          stemmer=replaceEnd(word, step3Rules);
        }
        if (toGetPivot()!= 1) {
          stemmer=replaceEnd(word, step4Rules);
        }
        if (toGetPivot() != 1) {
          stemmer=replaceEnd(word, step5aRules);
        }
        if (toGetPivot() != 1) {
          stemmer=replaceEnd(word, step5bRules);
        }
        if (toGetPivot() != 1) {
          stemmer=replaceEnd(word, step5aRules);
        }
        word = stemmer;
      }

      return word.trim();
    }


  public static void main(String[] args) {
    Stemmer stemmer1 = new Stemmer();
    System.out.println("leaves stemmed to: *"+stemmer1.stem("leaves")+"*");
    System.out.println("stems stemmed to: *"+stemmer1.stem("stems")+"*");
  }

}
