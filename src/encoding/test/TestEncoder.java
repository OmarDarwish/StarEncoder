
package encoding.test;

/**
 *
 * @author Omar Darwish
 * oxd120030
 * CS 2336.002
 */
import encoding.Dictionary;
import encoding.StarEncoder;
import encoding.SourceDictionary;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestEncoder {

    public TestEncoder(){
        StarEncoder english, french, german, spanish;
        SourceDictionary eng, frn, ger, spa;
        
        eng = new SourceDictionary(new File("wordlists\\englishCln.txt"));
        frn = new SourceDictionary(new File("wordlists\\frenchCln.txt"));
        ger = new SourceDictionary(new File("wordlists\\germanCln.txt"));
        spa = new SourceDictionary(new File("wordlists\\spanishCln.txt"));
        
        english = new StarEncoder(eng,null);
        french = new StarEncoder(frn,null);
        german = new StarEncoder(ger,null);
        spanish = new StarEncoder(spa,null);
        
        encode(new File("corpora\\english"),english);
        encode(new File("corpora\\french"),french);
        encode(new File("corpora\\german"),german);
        encode(new File("corpora\\spanish"),spanish);
              
    }
    
    public static void clean(File f){
        if(f.isDirectory()){
            for(File i:f.listFiles()){
                clean(i);
            }                
        }else{
            if(!f.getName().contains("Cln"))
            Cleaner.clean(f, new File(f.getAbsolutePath().replaceAll(".txt", "Cln.txt")));
        }
    }
    
    public void encode(File f, StarEncoder dict){
        if(f.isDirectory()){
            for(File i:f.listFiles()){
                encode(i,dict);
            }                
        }else{
            if(f.getName().contains("Cln"))
            dict.encode(f, new File(f.getAbsolutePath().replaceAll(".txt", "Star.txt")));
        }
    }
    
    public static void main(String[] args) {
        new TestEncoder();       
    }
}
