package at.u4a.geometric_algorithms.utils;

import java.util.AbstractList;

public class Mutable {

    /** @see http://stackoverflow.com/a/5606799 */
    public static <E> int getHashCode(AbstractList<E> l) {
        int hashCode = 1;
        for (E obj : l) {
            //System.out.print(obj+"-"+obj.hashCode()+" ");
            hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
        }
        //System.out.println();
        return hashCode;
    }

}
