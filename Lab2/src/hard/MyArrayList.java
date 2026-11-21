package hard;

import java.util.Objects;
import java.util.RandomAccess;

/* Список на основі масиву, що збільшується автоматично. Реалізує RandomAccess. */
public class MyArrayList extends AbstractMyList implements RandomAccess {

    private static final int DEFAULT_CAPACITY = 10;

    private Object[] elements;
    private int size;

    public MyArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public MyArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Від'ємна місткість: " + initialCapacity);
        }
        this.elements = new Object[initialCapacity];
    }

    @Override
    public void add(Object e) {
        ensureCapacity(size + 1);
        elements[size++] = e;
    }

    @Override
    public void add(int index, Object element) {
        checkPositionIndex(index);
        ensureCapacity(size + 1);
        /* зсуваємо хвіст на одну позицію праворуч */
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
    }

    @Override
    public void addAll(int index, Object[] c) {
        Objects.requireNonNull(c, "Масив елементів не може бути null");
        checkPositionIndex(index);
        if (c.length == 0) {
            return;
        }
        ensureCapacity(size + c.length);
        /* зсуваємо хвіст на c.length позицій праворуч і вставляємо новий фрагмент */
        System.arraycopy(elements, index, elements, index + c.length, size - index);
        System.arraycopy(c, 0, elements, index, c.length);
        size += c.length;
    }

    @Override
    public Object get(int index) {
        checkElementIndex(index);
        return elements[index];
    }

    @Override
    public Object remove(int index) {
        checkElementIndex(index);
        Object removed = elements[index];
        int moved = size - index - 1;
        if (moved > 0) {
            System.arraycopy(elements, index + 1, elements, index, moved);
        }
        elements[--size] = null; /* щоб збирач сміття міг звільнити об'єкт */
        return removed;
    }

    @Override
    public void set(int index, Object element) {
        checkElementIndex(index);
        elements[index] = element;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(o, elements[i])) {
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
        Object[] copy = new Object[size];
        System.arraycopy(elements, 0, copy, 0, size);
        return copy;
    }

    /* Збільшує масив, якщо в ньому бракує місця. */
    private void ensureCapacity(int minCapacity) {
        if (minCapacity < 0) {
            throw new OutOfMemoryError("Занадто великий розмір списку");
        }
        if (minCapacity > elements.length) {
            int newCapacity = Math.max(minCapacity, elements.length + (elements.length >> 1));
            Object[] bigger = new Object[newCapacity];
            System.arraycopy(elements, 0, bigger, 0, size);
            elements = bigger;
        }
    }
}
