package hard;

import java.util.Objects;

/* Спільна частина реалізацій MyList: перевірка індексів, toString, equals і hashCode. */
public abstract class AbstractMyList implements MyList {

    @Override
    public void addAll(Object[] c) {
        addAll(size(), c);
    }

    protected final void checkElementIndex(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(message(index));
        }
    }

    protected final void checkPositionIndex(int index) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException(message(index));
        }
    }

    private String message(int index) {
        return "Індекс: " + index + ", розмір списку: " + size();
    }

    @Override
    public String toString() {
        Object[] items = toArray();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(items[i]);
        }
        return sb.append(']').toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MyList)) {
            return false;
        }
        Object[] mine = toArray();
        Object[] theirs = ((MyList) o).toArray();
        if (mine.length != theirs.length) {
            return false;
        }
        for (int i = 0; i < mine.length; i++) {
            if (!Objects.equals(mine[i], theirs[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (Object item : toArray()) {
            result = 31 * result + Objects.hashCode(item);
        }
        return result;
    }
}
