package encoding;

/**
 *
 * @author Omar Darwish
 */
import java.util.Comparator;

public class LengthComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        if(o1 instanceof String && o2 instanceof String){
            String s1 = (String)o1;
            String s2 = (String)o2;
            if(s1.length() > s2.length())//first string longer
                return s1.length();
            if(s1.length() == s2.length())//strings equal in length
                if(s1.equals(s2))
                    return 0;
                else
                    return s1.compareTo(s2);
            else
                return -s2.length();//second string longer
        }else
            throw new UnsupportedOperationException("Comparison type not supported.");
    }

}
