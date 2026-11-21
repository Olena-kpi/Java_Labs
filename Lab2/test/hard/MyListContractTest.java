package hard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/* Спільні тести, які мають проходити будь-яка реалізація MyList.
    Підкласи лише створюють потрібну реалізацію. */
abstract class MyListContractTest {

    protected abstract MyList createList();

    private MyList listOf(Object... items) {
        MyList list = createList();
        for (Object item : items) {
            list.add(item);
        }
        return list;
    }

    @Test
    void newListIsEmpty() {
        MyList list = createList();
        assertEquals(0, list.size());
        assertArrayEquals(new Object[0], list.toArray());
    }

    @Test
    void addAppendsToTheEnd() {
        MyList list = listOf("a", "b", "c");
        assertEquals(3, list.size());
        assertArrayEquals(new Object[]{"a", "b", "c"}, list.toArray());
    }

    @Test
    void addAtIndexInsertsAndShifts() {
        MyList list = listOf("a", "b", "c");
        list.add(0, "start");
        list.add(2, "middle");
        list.add(list.size(), "end");
        assertArrayEquals(new Object[]{"start", "a", "middle", "b", "c", "end"}, list.toArray());
    }

    @Test
    void addAtIndexRejectsInvalidPosition() {
        MyList list = listOf("a", "b");
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, "x"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(3, "x"));
        assertEquals(2, list.size());
    }

    @Test
    void addAllAppendsArray() {
        MyList list = listOf("a");
        list.addAll(new Object[]{"b", "c"});
        assertArrayEquals(new Object[]{"a", "b", "c"}, list.toArray());
    }

    @Test
    void addAllAtIndexInsertsArray() {
        MyList list = listOf("a", "d");
        list.addAll(1, new Object[]{"b", "c"});
        assertArrayEquals(new Object[]{"a", "b", "c", "d"}, list.toArray());

        list.addAll(0, new Object[]{"x", "y"});
        list.addAll(list.size(), new Object[]{"z"});
        assertArrayEquals(new Object[]{"x", "y", "a", "b", "c", "d", "z"}, list.toArray());
    }

    @Test
    void addAllToEmptyList() {
        MyList list = createList();
        list.addAll(0, new Object[]{1, 2, 3});
        assertArrayEquals(new Object[]{1, 2, 3}, list.toArray());
    }

    @Test
    void addAllWithEmptyArrayChangesNothing() {
        MyList list = listOf("a");
        list.addAll(new Object[0]);
        list.addAll(0, new Object[0]);
        assertArrayEquals(new Object[]{"a"}, list.toArray());
    }

    @Test
    void addAllRejectsNullAndInvalidIndex() {
        MyList list = listOf("a");
        assertThrows(NullPointerException.class, () -> list.addAll(null));
        assertThrows(NullPointerException.class, () -> list.addAll(0, null));
        assertThrows(IndexOutOfBoundsException.class, () -> list.addAll(2, new Object[]{"x"}));
        assertThrows(IndexOutOfBoundsException.class, () -> list.addAll(-1, new Object[]{"x"}));
        assertEquals(1, list.size());
    }

    @Test
    void getReturnsElementByIndex() {
        MyList list = listOf("a", "b", "c");
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    void getRejectsInvalidIndex() {
        MyList list = listOf("a");
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> createList().get(0));
    }

    @Test
    void removeReturnsElementAndShiftsTheRest() {
        MyList list = listOf("a", "b", "c", "d");
        assertEquals("b", list.remove(1));
        assertEquals("a", list.remove(0));
        assertEquals("d", list.remove(list.size() - 1));
        assertArrayEquals(new Object[]{"c"}, list.toArray());
        assertEquals("c", list.remove(0));
        assertEquals(0, list.size());
    }

    @Test
    void removeRejectsInvalidIndex() {
        MyList list = listOf("a");
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
        assertEquals(1, list.size());
    }

    @Test
    void setReplacesElement() {
        MyList list = listOf("a", "b", "c");
        list.set(1, "B");
        list.set(0, "A");
        list.set(2, "C");
        assertArrayEquals(new Object[]{"A", "B", "C"}, list.toArray());
        assertEquals(3, list.size());
    }

    @Test
    void setRejectsInvalidIndex() {
        MyList list = listOf("a");
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, "x"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(1, "x"));
    }

    @Test
    void indexOfFindsFirstOccurrenceUsingEquals() {
        MyList list = listOf("a", "b", "a", new String("c"));
        assertEquals(0, list.indexOf("a"));
        assertEquals(1, list.indexOf("b"));
        assertEquals(3, list.indexOf("c")); /* інший об'єкт, але equals == true */
        assertEquals(-1, list.indexOf("zzz"));
    }

    @Test
    void supportsNullElements() {
        MyList list = listOf("a", null, "b");
        assertNull(list.get(1));
        assertEquals(1, list.indexOf(null));
        list.set(0, null);
        assertEquals(0, list.indexOf(null));
        assertNull(list.remove(0));
        assertEquals(2, list.size());
    }

    @Test
    void toArrayReturnsIndependentCopy() {
        MyList list = listOf("a", "b");
        Object[] copy = list.toArray();
        copy[0] = "changed";
        assertEquals("a", list.get(0));
        assertNotSame(list.toArray(), list.toArray());
    }

    @Test
    void keepsElementIdentity() {
        Object item = new Object();
        MyList list = listOf("x", item);
        assertSame(item, list.get(1));
    }

    @Test
    void handlesManyElements() {
        MyList list = createList();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        assertEquals(1000, list.size());
        assertEquals(0, list.get(0));
        assertEquals(500, list.get(500));
        assertEquals(999, list.get(999));

        for (int i = 0; i < 500; i++) {
            list.remove(0);
        }
        assertEquals(500, list.size());
        assertEquals(500, list.get(0));
        assertEquals(999, list.get(499));
    }

    @Test
    void toStringAndEqualsDependOnContent() {
        MyList list = listOf(1, "two", null);
        assertEquals("[1, two, null]", list.toString());
        assertEquals("[]", createList().toString());

        assertEquals(listOf(1, "two", null), list);
        assertEquals(listOf(1, "two", null).hashCode(), list.hashCode());
        assertTrue(!list.equals(listOf(1, "two")));
        assertTrue(!list.equals(listOf(1, "two", "other")));
        assertTrue(!list.equals(null));
        assertTrue(!list.equals("[1, two, null]"));
    }
}
