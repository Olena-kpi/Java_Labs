package hard;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/* Множина на основі хеш-таблиці, яка пам'ятає порядок додавання елементів. */
public class MyLinkedHashSet implements MySet {

    private static final int DEFAULT_CAPACITY = 16; /* завжди степінь двійки */
    private static final int MAX_CAPACITY = 1 << 30;
    private static final float LOAD_FACTOR = 0.75f;

    private static final class Entry {
        final Object key;
        final int hash;
        Entry next; /* наступний елемент у ланцюжку бакета */
        Entry before; /* попередній елемент у порядку вставки */
        Entry after; /* наступний елемент у порядку вставки */

        Entry(Object key, int hash) {
            this.key = key;
            this.hash = hash;
        }
    }

    private Entry[] table;
    private Entry head; /* найдавніший елемент */
    private Entry tail; /* найновіший елемент */
    private int size;
    private int threshold; /* при перевищенні розміру таблиця збільшується */
    private int modCount; /* лічильник структурних змін для fail-fast ітератора */

    public MyLinkedHashSet() {
        table = new Entry[DEFAULT_CAPACITY];
        threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
    }

    @Override
    public boolean add(Object e) {
        int hash = hash(e);
        if (findEntry(e, hash) != null) {
            return false;
        }
        Entry created = new Entry(e, hash);
        int index = indexFor(hash, table.length);
        created.next = table[index];
        table[index] = created;
        linkLast(created);

        size++;
        modCount++;
        if (size > threshold) {
            resize();
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Entry entry = findEntry(o, hash(o));
        if (entry == null) {
            return false;
        }
        removeEntry(entry);
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return findEntry(o, hash(o)) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        table = new Entry[table.length];
        head = null;
        tail = null;
        size = 0;
        modCount++;
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Entry x = head; x != null; x = x.after) {
            result[i++] = x.key;
        }
        return result;
    }

    @Override
    public Iterator<Object> iterator() {
        return new InsertionOrderIterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Entry x = head; x != null; x = x.after) {
            if (x != head) {
                sb.append(", ");
            }
            sb.append(x.key);
        }
        return sb.append(']').toString();
    }


    /* Хеш-код з «перемішуванням» старших бітів у молодші, щоб було менше колізій. */
    private static int hash(Object key) {
        int h = (key == null) ? 0 : key.hashCode();
        return h ^ (h >>> 16);
    }

    private static int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    private Entry findEntry(Object key, int hash) {
        for (Entry x = table[indexFor(hash, table.length)]; x != null; x = x.next) {
            if (x.hash == hash && Objects.equals(key, x.key)) {
                return x;
            }
        }
        return null;
    }

    private void linkLast(Entry entry) {
        Entry oldTail = tail;
        tail = entry;
        if (oldTail == null) {
            head = entry;
        } else {
            entry.before = oldTail;
            oldTail.after = entry;
        }
    }

    /* Видаляє запис і з ланцюжка бакета, і зі списку порядку вставки. */
    private void removeEntry(Entry target) {
        int index = indexFor(target.hash, table.length);
        Entry prev = null;
        for (Entry x = table[index]; x != null; prev = x, x = x.next) {
            if (x == target) {
                if (prev == null) {
                    table[index] = x.next;
                } else {
                    prev.next = x.next;
                }
                break;
            }
        }

        Entry before = target.before;
        Entry after = target.after;
        if (before == null) {
            head = after;
        } else {
            before.after = after;
        }
        if (after == null) {
            tail = before;
        } else {
            after.before = before;
        }
        target.before = null;
        target.after = null;
        target.next = null;

        size--;
        modCount++;
    }

    /* Подвоює таблицю та заново розподіляє записи по бакетах. */
    private void resize() {
        if (table.length >= MAX_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }
        Entry[] bigger = new Entry[table.length * 2];
        for (Entry x = head; x != null; x = x.after) {
            int index = indexFor(x.hash, bigger.length);
            x.next = bigger[index];
            bigger[index] = x;
        }
        table = bigger;
        threshold = (int) (bigger.length * LOAD_FACTOR);
    }

    private final class InsertionOrderIterator implements Iterator<Object> {
        private Entry nextEntry = head;
        private Entry lastReturned;
        private int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return nextEntry != null;
        }

        @Override
        public Object next() {
            checkForComodification();
            if (nextEntry == null) {
                throw new NoSuchElementException();
            }
            lastReturned = nextEntry;
            nextEntry = nextEntry.after;
            return lastReturned.key;
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException("Спочатку потрібно викликати next()");
            }
            checkForComodification();
            removeEntry(lastReturned);
            lastReturned = null;
            expectedModCount = modCount;
        }

        private void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
