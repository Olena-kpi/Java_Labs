package hard;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

/* Список на кільцевому буфері: елементи лежать у масиві, що «обертається», тому вставка
    й видалення на початку, як і в кінці, не потребують зсуву всього масиву —
    зсувається коротша половина. Місткість завжди степінь двійки. Реалізує RandomAccess. */
public class MyArrayList<E> implements MyList<E>, RandomAccess {

    private static final int MIN_CAPACITY = 8;

    /* Object[], бо масив параметра типу (new E[n]) створити не можна */
    private Object[] ring = new Object[MIN_CAPACITY];
    private int head;      /* фізична позиція елемента з індексом 0 */
    private int size;
    private int modCount;

    public MyArrayList() {
    }

    public MyArrayList(int expectedSize) {
        if (expectedSize < 0) {
            throw new IllegalArgumentException("Очікуваний розмір не може бути від'ємним: " + expectedSize);
        }
        ring = new Object[capacityFor(expectedSize)];
    }

    @Override
    public void add(E e) {
        add(size, e);
    }

    @Override
    public void add(int index, E element) {
        checkPosition(index);
        reserve(size + 1);
        if (index < size / 2) {
            head = slot(-1);                       /* звільняємо місце спереду, зсуваючи початок на крок */
            for (int i = 0; i < index; i++) {
                ring[slot(i)] = ring[slot(i + 1)];
            }
        } else {
            for (int i = size; i > index; i--) {
                ring[slot(i)] = ring[slot(i - 1)];
            }
        }
        ring[slot(index)] = element;
        size++;
        modCount++;
    }

    @Override
    public void addAll(E[] c) {
        addAll(size, c);
    }

    @Override
    public void addAll(int index, E[] c) {
        Objects.requireNonNull(c, "Масив не може бути null");
        checkPosition(index);
        if (c.length == 0) {
            return;
        }
        reserve(size + c.length);
        for (int i = size - 1; i >= index; i--) {  /* хвіст зсувається один раз на всю довжину масиву */
            ring[slot(i + c.length)] = ring[slot(i)];
        }
        for (int i = 0; i < c.length; i++) {
            ring[slot(index + i)] = c[i];
        }
        size += c.length;
        modCount++;
    }

    @Override
    public E get(int index) {
        checkElement(index);
        return at(index);
    }

    @Override
    public E remove(int index) {
        checkElement(index);
        E removed = at(index);
        if (index < size / 2) {
            for (int i = index; i > 0; i--) {
                ring[slot(i)] = ring[slot(i - 1)];
            }
            ring[head] = null;
            head = slot(1);
        } else {
            for (int i = index; i < size - 1; i++) {
                ring[slot(i)] = ring[slot(i + 1)];
            }
            ring[slot(size - 1)] = null;
        }
        size--;
        modCount++;
        return removed;
    }

    @Override
    public void set(int index, E element) {
        checkElement(index);
        ring[slot(index)] = element;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(o, ring[slot(i)])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = ring[slot(i)];
        }
        return result;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int next;
            private int last = -1;
            private int expected = modCount;

            @Override
            public boolean hasNext() {
                return next < size;
            }

            @Override
            public E next() {
                verify();
                if (next >= size) {
                    throw new NoSuchElementException();
                }
                last = next++;
                return at(last);
            }

            @Override
            public void remove() {
                if (last < 0) {
                    throw new IllegalStateException();
                }
                verify();
                MyArrayList.this.remove(last);
                next = last;
                last = -1;
                expected = modCount;
            }

            private void verify() {
                if (expected != modCount) {
                    throw new ConcurrentModificationException();
                }
            }
        };
    }

    @Override
    public String toString() {
        return ListSupport.format(this);
    }

    @Override
    public boolean equals(Object o) {
        return ListSupport.sameContent(this, o);
    }

    @Override
    public int hashCode() {
        return ListSupport.contentHash(this);
    }

    /* фізична позиція логічного індексу (індекс може виходити на крок за межі) */
    private int slot(int index) {
        return (head + index) & (ring.length - 1);
    }

    /* у буфері лежать лише елементи типу E, тому приведення безпечне */
    @SuppressWarnings("unchecked")
    private E at(int index) {
        return (E) ring[slot(index)];
    }

    private void reserve(int needed) {
        if (needed <= ring.length) {
            return;
        }
        Object[] bigger = new Object[capacityFor(needed)];
        for (int i = 0; i < size; i++) {
            bigger[i] = ring[slot(i)];
        }
        ring = bigger;
        head = 0;
    }

    private static int capacityFor(int needed) {
        int capacity = MIN_CAPACITY;
        while (capacity < needed) {
            capacity <<= 1;
        }
        return capacity;
    }

    private void checkElement(int index) {
        if (index < 0 || index >= size) {
            throw ListSupport.outOfBounds(index, size);
        }
    }

    private void checkPosition(int index) {
        if (index < 0 || index > size) {
            throw ListSupport.outOfBounds(index, size);
        }
    }
}
