package encoding;

/**
 *
 * @author Omar Darwish
 * Converts dictionaries into star encoding
 * Provides functions for encoding and decoding strings and files
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class StarEncoder {
   private final char[] ENCODING_CHARS;
   private SourceDictionary source;   
   private HashMap[] encodeMap; //encodeMap[i]  maps codes to strings for all words length i
   private HashMap[] decodeMap; //decodeMap[i]  maps strings to codes for all words length i
   SourceDictionary[] subDict;
   
   public StarEncoder(SourceDictionary source, char[] encodeChars){
        this.source = source;

        //setup maps
        SourceDictionary lengthDict = new SourceDictionary(this.source, new LengthComparator());
        subDict = new SourceDictionary[lengthDict.last().length()];
        encodeMap = new HashMap[lengthDict.last().length()];
        decodeMap = new HashMap[lengthDict.last().length()];

        //setup encoding character array
        if (encodeChars == null) {
            //use default encoding
            ENCODING_CHARS = new char[52];
            for (int i = 97; i < 123; i++)//a-z
            {
                ENCODING_CHARS[i - 97] = Character.toChars(i)[0];
            }
            for (int i = 65; i < 91; i++)//A-Z
            {
                ENCODING_CHARS[i - 39] = Character.toChars(i)[0];
            }
        } else {
            ENCODING_CHARS = encodeChars;
        }

        //create temporary sub dictionaries
        try {
            Iterator<String> iter = lengthDict.iterator();
            int lastLength = 0;
            while (iter.hasNext()) {
                String current = iter.next();
                //initialize subdictionaries
                if (current.length() > lastLength) {
                    subDict[current.length() - 1] = new SourceDictionary();
                    lastLength = current.length();
                }
                //fill subdictionaries
                subDict[current.length() - 1].add(current);
            }
        } catch (EmptyDictionaryException ex) {
            ex.printStackTrace();
        }

        //create maps
        for (int i = 0; i < encodeMap.length; i++) {
            if (subDict[i] != null) {
                encodeMap[i] = getEncodeMap(subDict[i]);
                decodeMap[i] = getDecodeMap(subDict[i]);
            }
        }
    }

    public String decode(String s) {
        StringBuilder sb = new StringBuilder();
        String[] words = getWords(s);
        int[] spacing;
        if (s.length() > 0) {
            spacing = getSpacing(s);

            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if (!word.equals("")) {
                    //check for punctuation
                    char[] punct = new char[2];//empty by default
                    //check for punctaion at end of word
                    if (isPunctuation(word.charAt(word.length() - 1))) {
                        punct[1] = word.charAt(word.length() - 1);
                        word = word.substring(0, word.length() - 1);
                    }
                    //check for punctuation at beginning of word
                    if (isPunctuation(word.charAt(0))) {
                        punct[0] = word.charAt(0);
                        word = word.substring(1);
                    }
                    //check for and remove cap mask
                    int[] caps = null;
                    if (word.contains("^")) {
                        ArrayList<Integer> capList = new ArrayList<Integer>(word.length() / 2);//max number of caps per word
                        for (int j = 0; j < word.length(); j++) {
                            if (word.charAt(j) == '^') {
                                capList.add(j);
                            }
                        }
                        //setup caps array
                        caps = new int[capList.size()];
                        int count = capList.size() - 1;
                        ListIterator<Integer> iter = capList.listIterator(capList.size());
                        for (int j = caps.length - 1; j >= 0; j--) {
                            caps[j] = iter.previous() - count;
                            count--;
                        }

                        //remove all prefixes
                        word = word.replace("^", "");
                    }

                    //check if code is in map
                    int wordLength = word.length() - 1;
                    if (wordLength >= 1 && wordLength <= decodeMap.length && decodeMap[wordLength] != null) {
                        if (decodeMap[word.length() - 1].containsKey(word)) {
                            //decode word
                            word = (String) decodeMap[word.length() - 1].get(word);

                            //apply cap mask
                            if (caps != null) {
                                char[] letters = word.toCharArray();
                                for (int j : caps) {
                                    letters[j] = Character.toUpperCase(letters[j]);
                                }
                                word = new String(letters);
                            }
                            StringBuilder temp = null;
                            temp = new StringBuilder(word);
                            temp.insert(0, punct[0]);
                            temp.append(punct[1]);
                            sb.append(createSpacing(spacing[i]) + temp.toString().trim());
                        } else //code is not in map
                        {
                            sb.append(createSpacing(spacing[i]) + words[i].trim());
                        }
                    } else {
                        sb.append(createSpacing(spacing[i]) + words[i]);
                    }
                }
            }
        }

        return sb.toString();
    }
   
   public void decode(File toDecode, File output){
       try{
           if(!toDecode.exists())
               toDecode.createNewFile();
           BufferedReader in = new BufferedReader(new FileReader(toDecode));  
           BufferedWriter out = new BufferedWriter(new FileWriter(output));
           String line = null;
           while((line = in.readLine()) != null){
               out.write(decode(line) + System.getProperty("line.separator"));
           }
           out.flush();
           in.close();
           out.close();
       }catch(IOException ex){
           
       }
   }
   
   public String encode(String s){
       StringBuilder sb = new StringBuilder();
       String[] words = getWords(s);
       if (s.length() > 0) {
           int[] spacing = getSpacing(s);
           
           for (int i = 0; i < words.length; i++) {

               String word = words[i];
               if (!word.equals("")) {
                   //check for punctuation
                   char[] punct = new char[2];//empty by default
                   //check for punctuation at end of word
                   if (isPunctuation(word.charAt(word.length() - 1))) {
                       punct[1] = word.charAt(word.length() - 1);
                       word = word.substring(0, word.length() - 1);
                   }
                   //check for punctuation at beginning of word
                   if (isPunctuation(word.charAt(0))) {
                       punct[0] = word.charAt(0);
                       word = word.substring(1);
                   }

                   if (source.contains(word.toLowerCase())) {
                       ArrayList<Integer> caps = new ArrayList<Integer>();

                       //check for capitals
                       for (int j = 0; j < word.length(); j++) {
                           if (Character.isUpperCase(word.charAt(j))) {
                               caps.add(j);
                           }
                       }

                       //get code, apply prefix cap mask
                       if (caps.size() > 0) {
                           StringBuilder temp = new StringBuilder((String) encodeMap[word.length() - 1].get(word.toLowerCase()));
                           int count = 0;
                           for (int j : caps) {
                               temp.insert(j + count, '^');
                               count++;
                           }
                           temp.insert(0, punct[0]);
                           temp.append(punct[1]);
                           sb.append(createSpacing(spacing[i]) + temp.toString().trim());
                       } else {
                           //string has no caps
                           StringBuilder temp = new StringBuilder(punct[0] + (String) encodeMap[word.length() - 1].get(word) + punct[1]);
                           sb.append(createSpacing(spacing[i]) + temp.toString().trim());
                       }

                   } else //if not in the dictionary, return the string as is.
                   {
                       sb.append(createSpacing(spacing[i]) + words[i]);
                   }
               }
           }
       }
       return sb.toString();
       
   }
   
   public void encode(File toEncode, File output){
       try{
           if(!toEncode.exists())
               toEncode.createNewFile();
           BufferedReader in = new BufferedReader(new FileReader(toEncode));  
           BufferedWriter out = new BufferedWriter(new FileWriter(output));
           String line = null;
           while((line = in.readLine()) != null){
               out.write(encode(line) + System.getProperty("line.separator"));
           }
           out.flush();
           out.close();
           in.close();
       }catch(IOException ex){
           
       }
   }
   
   public static boolean isPunctuation(char c) {
        return c == ','
            || c == '.'
            || c == '!'
            || c == '?'
            || c == ':'
            || c == ';'
            || c == '\''
            || c == '\"'
            ;
    }
   
   public int[] getSpacing(String s){
       //returns integers of the number of spaces before each word in the string
       int temp[] = new int[getWords(s).length];
       int remaining = s.length();
       int word = 0;
       int spacing = 0;
       int i = 0;
       while(remaining > 0 && word < temp.length){
           //count whitespace
           while(s.charAt(i) == ' '){
               spacing++;
               i++;
               remaining--;
           }
           //save result
           temp[word] = spacing;
           word++;           
           spacing = 0;
           //count space till next whitespace
           while(i < s.length() - 1 && s.charAt(i) != ' '){
               i++;
               remaining--;
           }
       }
       return temp;
   }
   
   public String createSpacing(int length){
       StringBuilder temp = new StringBuilder();
       for(int i = 0; i < length; i++){
           temp.append(" ");
       }
       return temp.toString();
   }
   
   public String[] getWords(String s){
       ArrayList<String>  cleaner = new ArrayList<String>();
       //clean s from empty words
       for(String str:s.split("\\s+")){
           if(!str.equals(""))
               cleaner.add(str);
       }
       String[] temp = new String[cleaner.size()];
       return cleaner.toArray(temp);
   }
   
   private HashMap<String,String> getEncodeMap(SourceDictionary source){
       //map with string as key, code as value
       HashMap temp = new HashMap<String, String>();
       try {
           Iterator<String> iter = source.iterator();
           String[] codes = getCodes(source);

           for (int i = 0; i < codes.length; i++) {
               temp.put(iter.next(), codes[i]);
           }
       } catch (EmptyDictionaryException ex) {
           ex.printStackTrace();
       }
       return temp;
   }
   
   private HashMap<String,String> getDecodeMap(SourceDictionary source){
       //map with code hash as key, string as value
       HashMap temp = new HashMap<String, String>();
       try {
           Iterator<String> iter = source.iterator();
           String[] codes = getCodes(source);

           for (int i = 0; i < codes.length; i++) {
               temp.put(codes[i], iter.next());
           }
       } catch (EmptyDictionaryException ex) {
           ex.printStackTrace();
       }
       return temp;
   }   
   
   private String[] getCodes(SourceDictionary source) {
        ArrayList<String> codes = new ArrayList<String>(source.size());
        int usedChars = -1;
        int castNum = 0;
        int currentChar = 0;
        int wordLength = source.first().length();;
        int remaining = 0; //counter for words that still need to be encoded after the word limit for the current word length has been raeched.
        
        //generate first code
        char[] temp = new char[wordLength];
        for(int i = 0; i < temp.length; i++){
            temp[i] = '*';
        }
        codes.add(new String(temp));
        
        //generate rest of codes
        for (int i = 0; i < source.size() - 1; i++) {
            char[] word = new char[wordLength];

            //increase used characters for first time
            if (i % ENCODING_CHARS.length == 0 && currentChar == wordLength - 1 && usedChars < 0) {
                usedChars++;
            }

            //check if static characters needs to change
            if (usedChars > -1 && currentChar == wordLength - 1 && i % ENCODING_CHARS.length == 0) {//reached last code for current number of static chars
                int firstTime = ENCODING_CHARS.length * wordLength;
                if (i > firstTime) {//ignore first change, castNum already set for first round
                    castNum++;
                    castNum = castNum % ENCODING_CHARS.length;
                }
                currentChar = usedChars;
                if (castNum == 0 && usedChars < wordLength - 1 && i > firstTime) {//out of encode chars for current static char
                    usedChars++;//move over one static char
                    currentChar = usedChars;//start generating permutations from here
                }
            }

            //set static chars
            if (usedChars > -1) {
                word[usedChars] = ENCODING_CHARS[castNum];
                //fill the already used static chars with the last encode character
                for (int j = 0; j < usedChars; j++) {
                    word[j] = ENCODING_CHARS[ENCODING_CHARS.length - 1];
                }
            }

            //move to the next character if out of encoding chars
            if (i % ENCODING_CHARS.length == 0 && currentChar < wordLength && i > 0) {
                currentChar++;
            }

            //set changing character
            try {
                word[currentChar] = ENCODING_CHARS[i % ENCODING_CHARS.length];
            } catch (ArrayIndexOutOfBoundsException ex) {
                //Reached code limit for word length
                remaining = source.size() - i;
                break;
            }

            //characters before changing character
            if (usedChars > -1) {
                if (currentChar > usedChars + 1) {
                    for (int j = usedChars + 1; j < currentChar; j++) {
                        word[j] = '*';
                    }
                }
            } else if (currentChar > 0) {
                for (int j = 0; j < currentChar; j++) {
                    word[j] = '*';
                }
            }

            //characters after changing character
            for (int k = currentChar + 1; k < wordLength; k++) {
                word[k] = '*';
            }

            //add generated word to list
            codes.add(new String(word));
        }

        //ecnode remaining words if there are any
        if (remaining > 0) {
           try {
               Iterator<String> iter = source.iterator();
               int count = 0;
               for (; count < source.size() - remaining + 1; count++) {
                   iter.next();
               }
               for (; count < source.size(); count++) {
                   codes.add(iter.next());
               }
           } catch (EmptyDictionaryException ex) {
               ex.printStackTrace();
           }
       }
       codes.trimToSize();
       return codes.toArray(new String[codes.size()]);
    }
}