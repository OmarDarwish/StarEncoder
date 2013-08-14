
package encoding;

import java.io.File;
import java.util.*;

/**
 *
 * @author Omar Darwish
 * oxd120030
 * CS 2336.002 
 */
public interface Dictionary<E>{
    public boolean add(E e); //retrun true if added successfully
    public boolean add(File f);//add from file
    public boolean add(Dictionary d);//add from dictionary
    public boolean contains(E element); //return true if contains, false if not
    public boolean remove(E element); //return true if removed successfuly
    public E first();//returns first element
    public E last();//returnsn last element
    public int getIndex(E element); // return x < 0 if not found;
    public Iterator<E> iterator() throws EmptyDictionaryException; // return an iterator for this dictionary
    public int size();

}
