package hard;

import java.util.Objects;

/* Двозв'язний список. Доступ за індексом потребує обходу вузлів. */
public class MyLinkedList extends AbstractMyList {

    private static final class Node {
        Object item;
        Node prev;
        Node next;

        Node(Node prev, Object item, Node next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }

    private Node first;
    private Node last;
    private int size;

    @Override
    public void add(Object e) {
        linkLast(e);
    }

    @Override
    public void add(int index, Object element) {
        checkPositionIndex(index);
        if (index == size) {
            linkLast(element);
        } else {
            linkBefore(element, node(index));
        }
    }

    @Override
    public void addAll(int index, Object[] c) {
        Objects.requireNonNull(c, "Масив елементів не може бути null");
        checkPositionIndex(index);
        if (c.length == 0) {
            return;
        }
        /* succ — вузол, перед яким вставляємо (null, якщо вставка в кінець) */
        Node succ = (index == size) ? null : node(index);
        Node pred = (succ == null) ? last : succ.prev;

        for (Object item : c) {
            Node created = new Node(pred, item, null);
            if (pred == null) {
                first = created;
            } else {
                pred.next = created;
            }
            pred = created;
        }

        if (succ == null) {
            last = pred;
        } else {
            pred.next = succ;
            succ.prev = pred;
        }
        size += c.length;
    }

    @Override
    public Object get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }

    @Override
    public Object remove(int index) {
        checkElementIndex(index);
        return unlink(node(index));
    }

    @Override
    public void set(int index, Object element) {
        checkElementIndex(index);
        node(index).item = element;
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        for (Node x = first; x != null; x = x.next, index++) {
            if (Objects.equals(o, x.item)) {
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
        for (Node x = first; x != null; x = x.next) {
            result[i++] = x.item;
        }
        return result;
    }

    /* Повертає вузол за індексом, рухаючись від найближчого кінця. */
    private Node node(int index) {
        Node x;
        if (index < (size >> 1)) {
            x = first;
            for (int i = 0; i < index; i++) {
                x = x.next;
            }
        } else {
            x = last;
            for (int i = size - 1; i > index; i--) {
                x = x.prev;
            }
        }
        return x;
    }

    private void linkLast(Object item) {
        Node oldLast = last;
        Node created = new Node(oldLast, item, null);
        last = created;
        if (oldLast == null) {
            first = created;
        } else {
            oldLast.next = created;
        }
        size++;
    }

    private void linkBefore(Object item, Node succ) {
        Node pred = succ.prev;
        Node created = new Node(pred, item, succ);
        succ.prev = created;
        if (pred == null) {
            first = created;
        } else {
            pred.next = created;
        }
        size++;
    }

    private Object unlink(Node x) {
        Object item = x.item;
        Node prev = x.prev;
        Node next = x.next;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }
        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }
        x.item = null;
        size--;
        return item;
    }
}
