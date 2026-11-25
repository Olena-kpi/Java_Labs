package hard;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

/* Список із десятьма базовими методами завдання (тепер узагальненими) та кількома
    методами за принципом PECS: wildcard стоїть там, де аргумент віддає елементи
    (? extends E) або приймає їх (? super E). */
public interface MyList<E> extends Iterable<E> {

    void add(E e);

    void add(int index, E element);

    void addAll(E[] c);

    void addAll(int index, E[] c);

    E get(int index);

    E remove(int index);

    void set(int index, E element);

    int indexOf(Object o);

    int size();

    Object[] toArray();

    /* ---------- методи з wildcard (реалізовані один раз, для обох списків) ---------- */

    /* Джерело: колекція віддає елементи, тому підходить будь-яка з елементами E або підтипів. */
    default boolean addAll(Collection<? extends E> source) {
        return addAll(size(), source);
    }

    default boolean addAll(int index, Collection<? extends E> source) {
        if (index < 0 || index > size()) {
            throw ListSupport.outOfBounds(index, size());
        }
        /* спершу копія: так безпечно навіть list.addAll(list) */
        MyArrayList<E> copy = new MyArrayList<>();
        for (E e : source) {
            copy.add(e);
        }
        for (E e : copy) {
            add(index++, e);
        }
        return !copy.isEmpty();
    }

    /* Приймач: у Collection<Object> чи Collection<Book> можна скласти елементи MyList<Novel>. */
    default void copyTo(Collection<? super E> target) {
        for (E e : this) {
            target.add(e);
        }
    }

    /* Компаратор лише споживає елементи, тому компаратор для Book годиться для списку Novel. */
    default void sort(Comparator<? super E> order) {
        MyArrayList<E> sorted = new MyArrayList<>();
        for (E e : this) {
            int low = 0;
            int high = sorted.size();
            while (low < high) {                       /* бінарна вставка зберігає стабільність */
                int mid = (low + high) >>> 1;
                if (order.compare(sorted.get(mid), e) <= 0) {
                    low = mid + 1;
                } else {
                    high = mid;
                }
            }
            sorted.add(low, e);
        }
        int i = 0;
        for (E e : sorted) {
            set(i++, e);
        }
    }

    default MyList<E> filter(Predicate<? super E> condition) {
        MyList<E> result = new MyArrayList<>();
        for (E e : this) {
            if (condition.test(e)) {
                result.add(e);
            }
        }
        return result;
    }

    /* Function<? super E, ? extends R>: приймає E (або надтип) і повертає R (або підтип). */
    default <R> MyList<R> map(Function<? super E, ? extends R> mapper) {
        MyList<R> result = new MyLinkedList<>();
        for (E e : this) {
            result.add(mapper.apply(e));
        }
        return result;
    }

    default boolean isEmpty() {
        return size() == 0;
    }

    default boolean contains(Object o) {
        return indexOf(o) >= 0;
    }
}
