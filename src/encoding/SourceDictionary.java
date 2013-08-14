
package encoding;

import java.util.*;
import java.io.*;

/**
 *
 * @author Omar Darwish
 * oxd120030
 * CS 2336.002
 * Dictionary that contains all original words in lowercase
 */
public class SourceDictionary implements Dictionary<String>{
    //tree that holds all words
    private TreeSet<String> dict;
    
    //empty dictionary
    public SourceDictionary(){
        dict = new TreeSet<String>();
    }
    
    //empty dictionary with comparator
    public SourceDictionary(Comparator c){
        dict = new TreeSet<String>(c);
    }
    
    //dictionary from array
    public SourceDictionary(String...wordList){
        dict = new TreeSet<String>();
        int i = 0;
        for(String s: wordList){
            dict.add(s.toLowerCase());
            i++;
        }
    }    
    
    //dictionary from array wtih comparator
    public SourceDictionary(String[] wordList, Comparator c){
        dict = new TreeSet<String>(c);
        for(String s: wordList){
            dict.add(s.toLowerCase());
        }        
    }
    
    //dictionary from text file path
    public SourceDictionary(File f){
        dict = new TreeSet<String>();
        add(f);
    }
    
    //dictionary from file path with comparator
    public SourceDictionary(File f, Comparator c){
        dict = new TreeSet<String>(c);
        add(f);
    }
    
    //dictionary from dictionary
    public SourceDictionary(Dictionary d){
        dict = new TreeSet<String>();
        add(d);        
    }
    
    //dictionary from dictionary with comparator
    public SourceDictionary(Dictionary d, Comparator c){
        dict = new TreeSet<String>(c);
        add(d);        
    }
    
    @Override
    //add single element
    public boolean add(String element) {
        try{
            dict.add(element.toLowerCase());
            return true;
        }catch(Exception ex){
            ex.printStackTrace();            
        }
        return false;
    }
    
    @Override
    //add from file
    public boolean add(File f){
        if (f.exists() && f.length() > 0) {
            if(f.getName().substring(f.getName().lastIndexOf('.')+1).equals("txt")){//get and check file extension
                //file is a text
                //text file must be ANSI encoded if run form Windows platform
                try {
                    int init = dict.size();
                    BufferedReader in = new BufferedReader(new FileReader(f));
                    String line;
                    //add to tree
                    while((line = in.readLine()) != null){
                        dict.add(line.toLowerCase());                       
                    }
                    in.close();
                    return true;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else{
                //file is binary
                try {                                       
                    DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));                    
                    int init = dict.size();
                    
                    //add to tree
                    while(input.available() > 0){
                        String s = input.readUTF();
                        dict.add(s.toLowerCase());
                    }                    
                    input.close();
                    return true;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }             
            }
        }
        return false; // file does not exist or is empty
           
    }
    
    @Override
    public boolean add(Dictionary d){
        try{
            Iterator<String> iter = d.iterator();
            //iterate through the dictionary and add to this dictionary
            while(iter.hasNext()){
                dict.add(iter.next().toLowerCase());
            }
        }catch(EmptyDictionaryException ex){
            ex.printStackTrace();
        }finally{
            return false;
        }
    }

    @Override
    public boolean contains(String element) {
        return dict.contains(element.toLowerCase());
    }

    @Override
    public boolean remove(String element) {
        try{
            dict.remove(element);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public int getIndex(String element) {
        if(contains(element))
            return dict.headSet(element).size();
        else
            return -1; //element not found
    }

    @Override
    public Iterator<String> iterator() throws EmptyDictionaryException {
        //return an iterator only if set is not empty
        if(dict.size() > 0)
            return dict.iterator();
        else
            throw new EmptyDictionaryException();
    }

    @Override
    public int size() {
        return dict.size();
    }
    
    @Override
    public String first(){
        return dict.first();
    }
    
    @Override
    public String last(){
        return dict.last();
    }    
    
    //create text file from dictionary
    public void toTextFile(String path) throws EmptyDictionaryException{
        if (dict.size() > 0) {
            try {
                File file = new File(path);
                BufferedWriter output = new BufferedWriter(new FileWriter(file));
                for(String s:dict){
                    if(!s.equals(""));
                        output.write(s);
                        output.newLine();
                        output.flush();
                }
                output.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new EmptyDictionaryException("File not written");
        }
        
    }
    
    //create binary file from dictionary
    public void toBinaryFile(String path) throws EmptyDictionaryException{
        if(dict.size() > 0){
            try{
                File file = new File(path);
                
                //create the file if it does not exist
                if(!file.exists())
                    file.createNewFile();
                DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                Iterator<String> iter = dict.iterator();
                //write all values in tree
                while(iter.hasNext()){
                    String temp = iter.next();
                    output.writeUTF(temp);
                }
                output.flush();
                output.close();
            } catch(IOException ex){
                ex.printStackTrace();
            }
        }else{
            throw new EmptyDictionaryException("File not written.");
        }
    }
}