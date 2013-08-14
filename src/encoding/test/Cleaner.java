package encoding.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 *
 * @author Omar Darwish
 * Removes all capital letters
 * Removes all non-alpha numeric characters
 * removes all numbers
 */
public class Cleaner {
    
    public static String clean(String toClean){
        String temp = toClean;
        temp = deAccent(temp);
        temp = temp.toLowerCase();
        temp = temp.replaceAll("[^a-zA-z ]", "");
        return temp;
    }
    
    public static void clean(File toDecode, File output){
       try{
           if(!toDecode.exists())
               toDecode.createNewFile();
           if(!output.exists())
               output.createNewFile();
           BufferedReader in = new BufferedReader(new FileReader(toDecode));  
           BufferedWriter out = new BufferedWriter(new FileWriter(output));
           String line = null;
           while((line = in.readLine()) != null){
               line = deAccent(line);
               line = line.toLowerCase();
               line = line.replaceAll("[^a-zA-Z ]", "");
               out.write(line + "\r\n");
           }
           out.flush();
           in.close();
           out.close();
       }catch(IOException ex){
           
       }
   }
    
    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
  

}
