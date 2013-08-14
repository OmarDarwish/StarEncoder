
package encoding;

/**
 *
 * @author Omar Darwish
 * oxd120030
 * CS 2336.002
 */
public class EmptyDictionaryException extends Exception{
    
    public EmptyDictionaryException(){
        super();
    }
    
    public EmptyDictionaryException(String error){
        super(error);
    }
}
