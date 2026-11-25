package hard;

import java.util.Objects;

/* Допоміжні методи, спільні для обох списків. */
final class ListSupport {

    private ListSupport() {
    }

    static IndexOutOfBoundsException outOfBounds(int index, int size) {
        return new IndexOutOfBoundsException("Позиція " + index + " поза межами списку з " + size + " елементів");
    }

    static String format(Iterable<?> items) {
        StringBuilder sb = new StringBuilder("[");
        String separator = "";
        for (Object item : items) {
            sb.append(separator).append(item);
            separator = ", ";
        }
        return sb.append(']').toString();
    }

    static boolean sameContent(MyList<?> list, Object other) {
        if (list == other) {
            return true;
        }
        if (!(other instanceof MyList<?> that) || that.size() != list.size()) {
            return false;
        }
        var a = list.iterator();
        var b = that.iterator();
        while (a.hasNext()) {
            if (!Objects.equals(a.next(), b.next())) {
                return false;
            }
        }
        return true;
    }

    static int contentHash(MyList<?> list) {
        int hash = 1;
        for (Object item : list) {
            hash = 31 * hash + Objects.hashCode(item);
        }
        return hash;
    }
}
