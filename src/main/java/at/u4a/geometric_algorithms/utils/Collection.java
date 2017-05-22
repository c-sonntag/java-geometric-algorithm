package at.u4a.geometric_algorithms.utils;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.ListIterator;

public class Collection {

    /**
     * @see http://stackoverflow.com/a/2103037
     */
    public static class ReverseIterable<T> implements Iterable<T> {

        private static class ReverseIterator<T> implements Iterator<T> {

            private final ListIterator<T> lit;

            public ReverseIterator(ListIterator<T> lit) {
                this.lit = lit;
            }

            public boolean hasNext() {
                return lit.hasPrevious();
            }

            public T next() {
                return lit.previous();
            }

            public void remove() {
                lit.remove();
            }
        }

        private final AbstractList<T> al;

        public ReverseIterable(AbstractList<T> al) {
            this.al = al;
        }

        public Iterator<T> iterator() {
            return new ReverseIterator<T>(al.listIterator(al.size()));
        }
    }

}
