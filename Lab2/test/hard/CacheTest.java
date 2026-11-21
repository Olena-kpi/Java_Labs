package hard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CacheTest {

    /* Годинник, яким керує тест: час змінюється вручну, без реального очікування. */
    private long now = 0;

    private Cache cacheWithTtl(int maxSize, long ttl) {
        return new Cache(maxSize, ttl, () -> now);
    }

    /* спільне з Map: пошук за ключем */

    @Test
    void storesAndReturnsValuesByKey() {
        Cache cache = new Cache(5);
        cache.put("a", 1);
        cache.put("b", 2);
        assertEquals(1, cache.get("a"));
        assertEquals(2, cache.get("b"));
        assertNull(cache.get("missing"));
        assertEquals(2, cache.size());
    }

    @Test
    void keyHasSingleValueAndPutReplacesIt() {
        Cache cache = new Cache(5);
        cache.put("a", 1);
        cache.put("a", 2);
        assertEquals(2, cache.get("a"));
        assertEquals(1, cache.size());
    }

    @Test
    void keysAreComparedWithEqualsAndHashCode() {
        Cache cache = new Cache(5);
        cache.put(new String("key"), "value");
        assertEquals("value", cache.get("key"));
        assertTrue(cache.containsKey("key"));
    }

    @Test
    void removeReturnsRemovedValue() {
        Cache cache = new Cache(5);
        cache.put("a", 1);
        assertEquals(1, cache.remove("a"));
        assertNull(cache.remove("a"));
        assertFalse(cache.containsKey("a"));
        assertEquals(0, cache.size());
    }

    @Test
    void clearRemovesAllEntries() {
        Cache cache = new Cache(5);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.clear();
        assertEquals(0, cache.size());
        assertNull(cache.get("a"));
        cache.put("c", 3);
        assertEquals("[c=3]", cache.toString());
    }

    /* відмінність 1: null заборонений */

    @Test
    void nullKeysAndValuesAreRejectedEverywhere() {
        Cache cache = new Cache(5);
        assertThrows(NullPointerException.class, () -> cache.put(null, "v"));
        assertThrows(NullPointerException.class, () -> cache.put("k", null));
        assertThrows(NullPointerException.class, () -> cache.put(null, null));
        assertThrows(NullPointerException.class, () -> cache.get(null));
        assertThrows(NullPointerException.class, () -> cache.containsKey(null));
        assertThrows(NullPointerException.class, () -> cache.remove(null));
        assertEquals(0, cache.size());
    }

    /* відмінність 2: час життя (expiry) */

    @Test
    void entryExpiresAfterTimeToLive() {
        Cache cache = cacheWithTtl(5, 100);
        cache.put("a", 1);

        now = 99;
        assertEquals(1, cache.get("a"));
        assertTrue(cache.containsKey("a"));

        now = 100;
        assertNull(cache.get("a"));
        assertFalse(cache.containsKey("a"));
        assertEquals(0, cache.size());
    }

    @Test
    void putRestartsTimeToLive() {
        Cache cache = cacheWithTtl(5, 100);
        cache.put("a", 1);
        now = 80;
        cache.put("a", 2);
        now = 150; /* від першого запису минуло 150, від другого — 70 */
        assertEquals(2, cache.get("a"));
        now = 180;
        assertNull(cache.get("a"));
    }

    @Test
    void expiredEntriesAreNotCountedOrPrinted() {
        Cache cache = cacheWithTtl(5, 100);
        cache.put("old", 1);
        now = 60;
        cache.put("new", 2);
        now = 120; /* "old" вже не чинний, "new" ще чинний */
        assertEquals("[new=2]", cache.toString());
        assertEquals(1, cache.size());
        assertNull(cache.remove("old"));
    }

    @Test
    void zeroTimeToLiveMeansEntriesNeverExpire() {
        Cache cache = cacheWithTtl(5, 0);
        cache.put("a", 1);
        now = Long.MAX_VALUE / 2;
        assertEquals(1, cache.get("a"));
    }

    /* відмінність 3: витіснення (eviction) */

    @Test
    void evictsLeastRecentlyUsedEntryWhenFull() {
        Cache cache = new Cache(3);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.put("d", 4); /* витісняється a — найдавніший */

        assertNull(cache.get("a"));
        assertEquals(2, cache.get("b"));
        assertEquals(3, cache.get("c"));
        assertEquals(4, cache.get("d"));
        assertEquals(3, cache.size());
        assertEquals(1, cache.evictionCount());
    }

    @Test
    void getMakesEntryRecentlyUsed() {
        Cache cache = new Cache(3);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.get("a"); /* тепер найдавніший — b */
        cache.put("d", 4);

        assertEquals("[d=4, a=1, c=3]", cache.toString());
        assertFalse(cache.containsKey("b"));
    }

    @Test
    void replacingValueMakesEntryRecentlyUsed() {
        Cache cache = new Cache(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("a", 10);
        cache.put("c", 3); /* витісняється b */
        assertEquals("[c=3, a=10]", cache.toString());
        assertEquals(1, cache.evictionCount());
    }

    @Test
    void containsKeyDoesNotChangeEvictionOrder() {
        Cache cache = new Cache(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.containsKey("a");
        cache.put("c", 3); /* a все ще найдавніший */
        assertFalse(cache.containsKey("a"));
        assertTrue(cache.containsKey("b"));
    }

    @Test
    void expiredEntriesAreRemovedBeforeEvictingLiveOnes() {
        Cache cache = cacheWithTtl(2, 100);
        cache.put("a", 1);
        now = 50;
        cache.put("b", 2);
        cache.get("a"); /* a — найновіший за використанням, але закінчиться першим */
        now = 100; /* a втратив чинність, b — ще ні */
        cache.put("c", 3); /* має видалити a (застарілий), а не b */

        assertEquals("[c=3, b=2]", cache.toString());
        assertEquals(0, cache.evictionCount());
    }

    @Test
    void cacheOfSizeOneKeepsOnlyLastEntry() {
        Cache cache = new Cache(1);
        cache.put("a", 1);
        cache.put("b", 2);
        assertNull(cache.get("a"));
        assertEquals(2, cache.get("b"));
        assertEquals(1, cache.evictionCount());
    }

    @Test
    void neverExceedsMaximumSize() {
        Cache cache = new Cache(10);
        for (int i = 0; i < 1000; i++) {
            cache.put(i, "v" + i);
            assertTrue(cache.size() <= 10);
        }
        assertEquals(990, cache.evictionCount());
        assertEquals("v999", cache.get(999));
        assertNull(cache.get(989));
    }

    /* параметри конструктора */

    @Test
    void rejectsInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> new Cache(0));
        assertThrows(IllegalArgumentException.class, () -> new Cache(-5));
        assertThrows(IllegalArgumentException.class, () -> new Cache(5, -1));
        assertThrows(NullPointerException.class, () -> new Cache(5, 100, null));
    }
}
