package hard;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.LongSupplier;

/* Кеш: як Map, але без null, із часом життя записів і витісненням при заповненні (LRU). */
public class Cache {

    private static final class Entry {
        final Object key;
        Object value;
        long expiresAt; /* момент втрати чинності (Long.MAX_VALUE — без обмеження) */
        Entry newer; /* запис, до якого зверталися пізніше */
        Entry older; /* запис, до якого зверталися раніше */

        Entry(Object key) {
            this.key = key;
        }
    }

    private final int maxSize;
    private final long ttlMillis; /* 0 — записи не втрачають чинність */
    private final LongSupplier clock; /* джерело часу в мілісекундах (підміняється в тестах) */
    private final Map<Object, Entry> entries = new HashMap<>();

    private Entry newest; /* останній використаний запис */
    private Entry oldest; /* кандидат на витіснення */
    private int evictions;

    public Cache(int maxSize) {
        this(maxSize, 0);
    }

    public Cache(int maxSize, long ttlMillis) {
        this(maxSize, ttlMillis, () -> System.nanoTime() / 1_000_000);
    }

    public Cache(int maxSize, long ttlMillis, LongSupplier clock) {
        if (maxSize < 1) {
            throw new IllegalArgumentException("Розмір кешу має бути не менше 1: " + maxSize);
        }
        if (ttlMillis < 0) {
            throw new IllegalArgumentException("Час життя не може бути від'ємним: " + ttlMillis);
        }
        this.maxSize = maxSize;
        this.ttlMillis = ttlMillis;
        this.clock = Objects.requireNonNull(clock, "Джерело часу не може бути null");
    }

    public void put(Object key, Object value) {
        Objects.requireNonNull(key, "Ключ не може бути null");
        Objects.requireNonNull(value, "Значення не може бути null");

        Entry entry = entries.get(key);
        if (entry != null) {
            entry.value = value;
            entry.expiresAt = expirationTime();
            moveToNewest(entry);
            return;
        }

        if (entries.size() >= maxSize) {
            removeExpired();
        }
        if (entries.size() >= maxSize) {
            evictOldest();
        }

        entry = new Entry(key);
        entry.value = value;
        entry.expiresAt = expirationTime();
        entries.put(key, entry);
        linkNewest(entry);
    }

    public Object get(Object key) {
        Objects.requireNonNull(key, "Ключ не може бути null");
        Entry entry = entries.get(key);
        if (entry == null) {
            return null;
        }
        if (isExpired(entry)) {
            removeEntry(entry);
            return null;
        }
        moveToNewest(entry);
        return entry.value;
    }

    public boolean containsKey(Object key) {
        Objects.requireNonNull(key, "Ключ не може бути null");
        Entry entry = entries.get(key);
        if (entry == null) {
            return false;
        }
        if (isExpired(entry)) {
            removeEntry(entry);
            return false;
        }
        return true;
    }

    public Object remove(Object key) {
        Objects.requireNonNull(key, "Ключ не може бути null");
        Entry entry = entries.get(key);
        if (entry == null) {
            return null;
        }
        removeEntry(entry);
        return isExpired(entry) ? null : entry.value;
    }

    public void clear() {
        entries.clear();
        newest = null;
        oldest = null;
    }

    public int size() {
        removeExpired();
        return entries.size();
    }

    public int maxSize() {
        return maxSize;
    }

    public int evictionCount() {
        return evictions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Entry x = newest; x != null; x = x.older) {
            if (isExpired(x)) {
                continue;
            }
            if (!first) {
                sb.append(", ");
            }
            sb.append(x.key).append('=').append(x.value);
            first = false;
        }
        return sb.append(']').toString();
    }


    private long expirationTime() {
        return ttlMillis == 0 ? Long.MAX_VALUE : clock.getAsLong() + ttlMillis;
    }

    private boolean isExpired(Entry entry) {
        return entry.expiresAt <= clock.getAsLong();
    }

    private void removeExpired() {
        if (ttlMillis == 0) {
            return;
        }
        Entry x = oldest;
        while (x != null) {
            Entry next = x.newer; /* зберігаємо посилання до можливого видалення x */
            if (isExpired(x)) {
                removeEntry(x);
            }
            x = next;
        }
    }

    private void evictOldest() {
        if (oldest != null) {
            removeEntry(oldest);
            evictions++;
        }
    }

    private void removeEntry(Entry entry) {
        entries.remove(entry.key);
        unlink(entry);
    }

    private void linkNewest(Entry entry) {
        entry.older = newest;
        entry.newer = null;
        if (newest == null) {
            oldest = entry;
        } else {
            newest.newer = entry;
        }
        newest = entry;
    }

    private void unlink(Entry entry) {
        if (entry.older == null) {
            oldest = entry.newer;
        } else {
            entry.older.newer = entry.newer;
        }
        if (entry.newer == null) {
            newest = entry.older;
        } else {
            entry.newer.older = entry.older;
        }
        entry.older = null;
        entry.newer = null;
    }

    private void moveToNewest(Entry entry) {
        if (entry != newest) {
            unlink(entry);
            linkNewest(entry);
        }
    }
}
