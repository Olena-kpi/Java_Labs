package hard;

import org.junit.jupiter.api.Test;

import java.util.RandomAccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MyLinkedListTest extends MyListContractTest {

    @Override
    protected MyList createList() {
        return new MyLinkedList();
    }

    @Test
    void doesNotImplementRandomAccessMarker() {
        assertFalse(createList() instanceof RandomAccess);
    }

    /* Доступ з обох кінців списку (метод node обирає найближчий кінець). */
    @Test
    void accessesElementsFromBothEnds() {
        MyList list = createList();
        for (int i = 0; i < 101; i++) {
            list.add(i);
        }
        for (int i = 0; i < 101; i++) {
            assertEquals(i, list.get(i));
        }
        list.add(100, "middle-near-end");
        list.add(1, "near-start");
        assertEquals("near-start", list.get(1));
        assertEquals("middle-near-end", list.get(101));
        assertEquals(103, list.size());
    }

    @Test
    void removingAllElementsLeavesWorkingList() {
        MyList list = createList();
        list.addAll(new Object[]{1, 2, 3});
        list.remove(1);
        list.remove(1);
        list.remove(0);
        assertEquals(0, list.size());
        list.add("again");
        list.add(0, "first");
        assertEquals("[first, again]", list.toString());
    }
}
