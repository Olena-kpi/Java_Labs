package hard;

import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyLinkedHashSetTest {

    /* Ключ, у якого всі екземпляри мають однаковий хеш-код: гарантує колізії. */
    private record CollidingKey(String name) {
        @Override
        public int hashCode() {
            return 42;
        }
    }

    private final MySet set = new MyLinkedHashSet();

    @Test
    void newSetIsEmpty() {
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());
        assertEquals("[]", set.toString());
        assertFalse(set.iterator().hasNext());
    }

    @Test
    void addRejectsDuplicates() {
        assertTrue(set.add("a"));
        assertFalse(set.add("a"));
        assertTrue(set.add(new String("a").concat("b")));
        assertFalse(set.add("ab"));
        assertEquals(2, set.size());
    }

    @Test
    void preservesInsertionOrder() {
        /* хеш-коди цих рядків не відповідають порядку додавання */
        String[] items = {"zebra", "apple", "mango", "banana", "cherry"};
        for (String item : items) {
            set.add(item);
        }
        assertArrayEquals(items, set.toArray());
        assertEquals("[zebra, apple, mango, banana, cherry]", set.toString());
    }

    @Test
    void reAddingDoesNotChangeOrder() {
        set.add("a");
        set.add("b");
        set.add("c");
        set.add("a");
        assertArrayEquals(new Object[]{"a", "b", "c"}, set.toArray());
    }

    @Test
    void removeWorksAndKeepsOrderOfTheRest() {
        set.add("a");
        set.add("b");
        set.add("c");
        assertTrue(set.remove("b"));
        assertFalse(set.remove("b"));
        assertFalse(set.remove("missing"));
        assertArrayEquals(new Object[]{"a", "c"}, set.toArray());

        assertTrue(set.remove("a"));
        assertTrue(set.remove("c"));
        assertTrue(set.isEmpty());

        set.add("again");
        assertArrayEquals(new Object[]{"again"}, set.toArray());
    }

    @Test
    void containsUsesEqualsAndHashCode() {
        set.add("hello");
        assertTrue(set.contains(new String("hello")));
        assertFalse(set.contains("world"));
    }

    @Test
    void allowsSingleNullElement() {
        assertTrue(set.add(null));
        assertFalse(set.add(null));
        assertTrue(set.contains(null));
        assertEquals(1, set.size());
        assertTrue(set.remove(null));
        assertFalse(set.contains(null));
    }

    @Test
    void handlesHashCollisions() {
        CollidingKey a = new CollidingKey("a");
        CollidingKey b = new CollidingKey("b");
        CollidingKey c = new CollidingKey("c");
        assertTrue(set.add(a));
        assertTrue(set.add(b));
        assertTrue(set.add(c));
        assertFalse(set.add(new CollidingKey("b")));

        assertTrue(set.remove(b)); /* елемент із середини ланцюжка колізій */
        assertTrue(set.contains(a));
        assertFalse(set.contains(b));
        assertTrue(set.contains(c));
        assertArrayEquals(new Object[]{a, c}, set.toArray());
    }

    @Test
    void growsAndKeepsOrderAndContent() {
        int count = 5000;
        for (int i = 0; i < count; i++) {
            set.add(count - i); /* від 5000 до 1 */
        }
        assertEquals(count, set.size());

        Object[] array = set.toArray();
        for (int i = 0; i < count; i++) {
            assertEquals(count - i, array[i]);
            assertTrue(set.contains(count - i));
        }
        assertFalse(set.contains(0));
        assertFalse(set.contains(count + 1));
    }

    @Test
    void clearRemovesEverything() {
        set.add("a");
        set.add("b");
        set.clear();
        assertTrue(set.isEmpty());
        assertFalse(set.contains("a"));
        set.add("c");
        assertArrayEquals(new Object[]{"c"}, set.toArray());
    }

    @Test
    void iteratorVisitsElementsInInsertionOrder() {
        set.add("x");
        set.add("y");
        set.add("z");
        StringBuilder sb = new StringBuilder();
        for (Object o : set) {
            sb.append(o);
        }
        assertEquals("xyz", sb.toString());
    }

    @Test
    void iteratorRemoveDeletesCurrentElement() {
        for (int i = 1; i <= 6; i++) {
            set.add(i);
        }
        Iterator<Object> it = set.iterator();
        while (it.hasNext()) {
            if ((Integer) it.next() % 2 == 0) {
                it.remove();
            }
        }
        assertArrayEquals(new Object[]{1, 3, 5}, set.toArray());
        assertEquals(3, set.size());
    }

    @Test
    void iteratorContractViolationsAreReported() {
        set.add("a");
        Iterator<Object> it = set.iterator();
        assertThrows(IllegalStateException.class, it::remove);
        it.next();
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void iteratorIsFailFast() {
        set.add("a");
        set.add("b");
        Iterator<Object> it = set.iterator();
        it.next();
        set.add("c"); /* структурна зміна поза ітератором */
        assertThrows(ConcurrentModificationException.class, it::next);
    }
}
