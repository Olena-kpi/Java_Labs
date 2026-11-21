package hard;

import org.junit.jupiter.api.Test;

import java.util.RandomAccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyArrayListTest extends MyListContractTest {

    @Override
    protected MyList createList() {
        return new MyArrayList();
    }

    @Test
    void implementsRandomAccessMarker() {
        assertTrue(createList() instanceof RandomAccess);
    }

    @Test
    void worksWithZeroInitialCapacity() {
        MyArrayList list = new MyArrayList(0);
        for (int i = 0; i < 50; i++) {
            list.add(i);
        }
        assertEquals(50, list.size());
        assertEquals(49, list.get(49));
    }

    @Test
    void rejectsNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new MyArrayList(-1));
    }

    @Test
    void addAllLargerThanCurrentCapacityGrowsArray() {
        MyArrayList list = new MyArrayList(2);
        Object[] many = new Object[100];
        for (int i = 0; i < many.length; i++) {
            many[i] = i;
        }
        list.addAll(many);
        list.addAll(50, many);
        /* [0..49] + [0..99] (вставлено з позиції 50) + [50..99] */
        assertEquals(200, list.size());
        assertEquals(49, list.get(49));
        assertEquals(0, list.get(50));
        assertEquals(99, list.get(149));
        assertEquals(50, list.get(150));
        assertEquals(99, list.get(199));
    }
}
