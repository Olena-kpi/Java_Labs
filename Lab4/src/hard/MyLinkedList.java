package hard;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/* Двозв'язний список із вартовим вузлом: вартовий замикає ланцюжок в кільце, тому
    вставка й видалення не мають особливих випадків для початку та кінця. */
public class MyLinkedList<E> implements MyList<E> {

    private static final class Node<E> {
        E item;
        Node<E> before;
        Node<E> after;

        Node(E item) {
            this.item = item;
        }
    }

    private final Node<E> guard = new Node<>(null);
    private int size;
    private int modCount;

    public MyLinkedList() {
        guard.before = guard;
        guard.after = guard;
    }

    @Override
    public void add(E e) {
        linkBefore(guard, e);
    }

    @Override
    public void add(int index, E element) {
        checkPosition(index);
        linkBefore(index == size ? guard : nodeAt(index), element);
    }

    @Override
    public void addAll(E[] c) {
        addAll(size, c);
    }

    @Override
    public void addAll(int index, E[] c) {
        Objects.requireNonNull(c, "Масив не може бути null");
        checkPosition(index);
        Node<E> successor = index == size ? guard : nodeAt(index);   /* позицію шукаємо один раз */
        for (E e : c) {
            linkBefore(successor, e);
        }
    }

    @Override
    public E get(int index) {
        checkElement(index);
        return nodeAt(index).item;
    }

    @Override
    public E remove(int index) {
        checkElement(index);
        return unlink(nodeAt(index));
    }

    @Override
    public void set(int index, E element) {
        checkElement(index);
        nodeAt(index).item = element;
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        for (Node<E> n = guard.after; n != guard; n = n.after, index++) {
            if (Objects.equals(o, n.item)) {
                return index;
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
        int i = 0;
        for (Node<E> n = guard.after; n != guard; n = n.after) {
            result[i++] = n.item;
        }
        return result;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private Node<E> next = guard.after;
            private Node<E> last;
            private int expected = modCount;

            @Override
            public boolean hasNext() {
                return next != guard;
            }

            @Override
            public E next() {
                verify();
                if (next == guard) {
                    throw new NoSuchElementException();
                }
                last = next;
                next = next.after;
                return last.item;
            }

            @Override
            public void remove() {
                if (last == null) {
                    throw new IllegalStateException();
                }
                verify();
                unlink(last);
                last = null;
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

    private void linkBefore(Node<E> successor, E item) {
        Node<E> node = new Node<>(item);
        node.after = successor;
        node.before = successor.before;
        successor.before.after = node;
        successor.before = node;
        size++;
        modCount++;
    }

    private E unlink(Node<E> node) {
        node.before.after = node.after;
        node.after.before = node.before;
        E item = node.item;
        node.item = null;
        node.before = null;
        node.after = null;
        size--;
        modCount++;
        return item;
    }

    /* іде з того кінця, який ближче до індексу */
    private Node<E> nodeAt(int index) {
        Node<E> n = guard;
        if (index < size / 2) {
            for (int i = 0; i <= index; i++) {
                n = n.after;
            }
        } else {
            for (int i = size; i > index; i--) {
                n = n.before;
            }
        }
        return n;
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
