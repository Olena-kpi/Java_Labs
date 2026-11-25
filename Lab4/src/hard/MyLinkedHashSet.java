package hard;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/* Множина, що пам'ятає порядок додавання. Замість вузлів зі зв'язками тут «компактна»
    схема: елементи лежать підряд у масиві keys (це і є порядок додавання), а хеш-таблиця
    table з відкритою адресацією зберігає лише номери комірок масиву keys.
    Видалений елемент лишає в keys позначку REMOVED; вона зникає при перебудові. */
public class MyLinkedHashSet<E> implements MySet<E> {

    private static final Object REMOVED = new Object();
    private static final int EMPTY = -1;
    private static final int DELETED = -2;
    private static final int MIN_CAPACITY = 8;

    private Object[] keys = new Object[MIN_CAPACITY];
    private int[] hashes = new int[MIN_CAPACITY];
    private int[] table = newTable(MIN_CAPACITY);
    private int used;      /* зайнято комірок keys, разом із видаленими */
    private int size;
    private int modCount;

    public MyLinkedHashSet() {
    }

    @Override
    public boolean add(E e) {
        int hash = hash(e);
        if (find(e, hash) >= 0) {
            return false;
        }
        if (used == keys.length) {
            rebuild();
        }
        int slot = used++;
        keys[slot] = e;
        hashes[slot] = hash;
        int mask = table.length - 1;
        int i = hash & mask;
        while (table[i] >= 0) {
            i = (i + 1) & mask;
        }
        table[i] = slot;
        size++;
        modCount++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int hash = hash(o);
        int mask = table.length - 1;
        for (int i = hash & mask; table[i] != EMPTY; i = (i + 1) & mask) {
            int slot = table[i];
            if (slot >= 0 && hashes[slot] == hash && Objects.equals(keys[slot], o)) {
                table[i] = DELETED;
                keys[slot] = REMOVED;
                size--;
                modCount++;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return find(o, hash(o)) >= 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        keys = new Object[MIN_CAPACITY];
        hashes = new int[MIN_CAPACITY];
        table = newTable(MIN_CAPACITY);
        used = 0;
        size = 0;
        modCount++;
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int n = 0;
        for (int i = 0; i < used; i++) {
            if (keys[i] != REMOVED) {
                result[n++] = keys[i];
            }
        }
        return result;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int next = skip(0);
            private int last = -1;
            private int expected = modCount;

            @Override
            public boolean hasNext() {
                return next < used;
            }

            /* у keys лежать лише елементи типу E (і позначка REMOVED, яку ми пропускаємо) */
            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                if (expected != modCount) {
                    throw new ConcurrentModificationException();
                }
                if (next >= used) {
                    throw new NoSuchElementException();
                }
                last = next;
                next = skip(next + 1);
                return (E) keys[last];
            }

            @Override
            public void remove() {
                if (last < 0) {
                    throw new IllegalStateException();
                }
                if (expected != modCount) {
                    throw new ConcurrentModificationException();
                }
                MyLinkedHashSet.this.remove(keys[last]);
                last = -1;
                expected = modCount;
            }

            private int skip(int from) {
                while (from < used && keys[from] == REMOVED) {
                    from++;
                }
                return from;
            }
        };
    }

    @Override
    public String toString() {
        return ListSupport.format(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MySet<?> that) || that.size() != size) {
            return false;
        }
        for (Object item : that) {
            if (!contains(item)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int sum = 0;
        for (int i = 0; i < used; i++) {
            if (keys[i] != REMOVED) {
                sum += hashes[i];
            }
        }
        return sum;
    }

    private int find(Object o, int hash) {
        int mask = table.length - 1;
        for (int i = hash & mask; table[i] != EMPTY; i = (i + 1) & mask) {
            int slot = table[i];
            if (slot >= 0 && hashes[slot] == hash && Objects.equals(keys[slot], o)) {
                return slot;
            }
        }
        return -1;
    }

    /* Масив keys заповнено: або стискаємо його (якщо багато видалених), або збільшуємо вдвічі. */
    private void rebuild() {
        int capacity = size <= keys.length / 2 ? keys.length : keys.length * 2;
        Object[] oldKeys = keys;
        int[] oldHashes = hashes;
        int oldUsed = used;
        keys = new Object[capacity];
        hashes = new int[capacity];
        table = newTable(capacity);
        used = 0;
        int mask = table.length - 1;
        for (int s = 0; s < oldUsed; s++) {
            if (oldKeys[s] == REMOVED) {
                continue;
            }
            keys[used] = oldKeys[s];
            hashes[used] = oldHashes[s];
            int i = oldHashes[s] & mask;
            while (table[i] != EMPTY) {
                i = (i + 1) & mask;
            }
            table[i] = used++;
        }
    }

    /* таблиця вдвічі більша за keys, тому вільні комірки завжди є */
    private static int[] newTable(int capacity) {
        int[] t = new int[capacity * 2];
        java.util.Arrays.fill(t, EMPTY);
        return t;
    }

    private static int hash(Object o) {
        int h = Objects.hashCode(o);
        return (h ^ (h >>> 16)) & 0x7fffffff;
    }
}
