package hard;

import java.util.Collection;
import java.util.Iterator;

public interface MySet<E> extends Iterable<E> {

    boolean add(E e);

    boolean remove(Object o);

    boolean contains(Object o);

    int size();

    void clear();

    Object[] toArray();

    default boolean isEmpty() {
        return size() == 0;
    }

    /* Джерело: колекція віддає елементи E або підтипів. */
    default boolean addAll(Collection<? extends E> source) {
        boolean changed = false;
        for (E e : source) {
            changed |= add(e);
        }
        return changed;
    }

    /* Тут тип елементів колекції не важливий (contains приймає Object), тому Collection<?>. */
    default boolean containsAll(Collection<?> items) {
        for (Object o : items) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    default boolean removeAll(Collection<?> items) {
        boolean changed = false;
        for (Object o : items) {
            changed |= remove(o);
        }
        return changed;
    }

    default boolean retainAll(Collection<?> keep) {
        boolean changed = false;
        for (Iterator<E> it = iterator(); it.hasNext(); ) {
            if (!keep.contains(it.next())) {
                it.remove();
                changed = true;
            }
        }
        return changed;
    }

    /* Приймач: елементи множини можна скласти в колекцію E або будь-якого надтипу. */
    default void copyTo(Collection<? super E> target) {
        for (E e : this) {
            target.add(e);
        }
    }
}
