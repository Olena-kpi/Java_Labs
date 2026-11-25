package hard;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.LongSupplier;

/* Кеш відрізняється від Map: ключі й значення не бувають null, у запису є час життя,
    а коли місця немає, політика витіснення (EvictionPolicy) обирає запис для видалення.
    Створюється через Cache.<K, V>builder()...build(). */
public class Cache<K, V> {

    public enum RemovalCause { EXPIRED, EVICTED, REPLACED, EXPLICIT }

    /* Слухач лише приймає дані, тому його можна оголосити для надтипів K і V. */
    @FunctionalInterface
    public interface RemovalListener<K, V> {
        void onRemoval(K key, V value, RemovalCause cause);
    }

    private static final class Entry<V> {
        final V value;
        final long expiresAt;

        Entry(V value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }
    }

    public static final class Builder<K, V> {
        private int maxSize = 100;
        private long ttl;
        private EvictionPolicy<K> policy = EvictionPolicy.lru();
        private LongSupplier clock = () -> System.nanoTime() / 1_000_000;
        private RemovalListener<? super K, ? super V> listener = (k, v, cause) -> { };

        private Builder() {
        }

        public Builder<K, V> maxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        /* Час життя за замовчуванням у мс; 0 — без обмеження. */
        public Builder<K, V> ttl(long millis) {
            this.ttl = millis;
            return this;
        }

        public Builder<K, V> policy(EvictionPolicy<K> policy) {
            this.policy = Objects.requireNonNull(policy, "Політика не може бути null");
            return this;
        }

        public Builder<K, V> clock(LongSupplier clock) {
            this.clock = Objects.requireNonNull(clock, "Годинник не може бути null");
            return this;
        }

        public Builder<K, V> onRemoval(RemovalListener<? super K, ? super V> listener) {
            this.listener = Objects.requireNonNull(listener, "Слухач не може бути null");
            return this;
        }

        public Cache<K, V> build() {
            if (maxSize < 1) {
                throw new IllegalArgumentException("Розмір кешу має бути не менше 1: " + maxSize);
            }
            if (ttl < 0) {
                throw new IllegalArgumentException("Час життя не може бути від'ємним: " + ttl);
            }
            return new Cache<>(this);
        }
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    private final int maxSize;
    private final long defaultTtl;
    private final EvictionPolicy<K> policy;
    private final LongSupplier clock;
    private final RemovalListener<? super K, ? super V> listener;
    private final Map<K, Entry<V>> entries = new HashMap<>();
    private long hits;
    private long misses;
    private long evictions;

    private Cache(Builder<K, V> b) {
        maxSize = b.maxSize;
        defaultTtl = b.ttl;
        policy = b.policy;
        clock = b.clock;
        listener = b.listener;
    }

    public void put(K key, V value) {
        put(key, value, defaultTtl);
    }

    public void put(K key, V value, long ttlMillis) {
        Objects.requireNonNull(key, "Ключ не може бути null");
        Objects.requireNonNull(value, "Значення не може бути null");
        if (ttlMillis < 0) {
            throw new IllegalArgumentException("Час життя не може бути від'ємним: " + ttlMillis);
        }
        Entry<V> old = entries.remove(key);
        if (old != null) {
            policy.onRemove(key);
            listener.onRemoval(key, old.value, RemovalCause.REPLACED);
        } else if (entries.size() >= maxSize) {
            purgeExpired();
            if (entries.size() >= maxSize) {
                K victim = policy.victim();
                Entry<V> removed = entries.remove(victim);
                policy.onRemove(victim);
                evictions++;
                listener.onRemoval(victim, removed.value, RemovalCause.EVICTED);
            }
        }
        entries.put(key, new Entry<>(value, ttlMillis == 0 ? Long.MAX_VALUE : clock.getAsLong() + ttlMillis));
        policy.onInsert(key);
    }

    public Optional<V> get(K key) {
        Objects.requireNonNull(key, "Ключ не може бути null");
        Entry<V> entry = entries.get(key);
        if (entry != null && expired(entry)) {
            drop(key, RemovalCause.EXPIRED);
            entry = null;
        }
        if (entry == null) {
            misses++;
            return Optional.empty();
        }
        hits++;
        policy.onAccess(key);
        return Optional.of(entry.value);
    }

    /* Function<? super K, ? extends V>: функція для будь-якого надтипу ключа, що дає V або підтип. */
    public V computeIfAbsent(K key, Function<? super K, ? extends V> loader) {
        Optional<V> cached = get(key);
        if (cached.isPresent()) {
            return cached.get();
        }
        V value = loader.apply(key);
        put(key, value);
        return value;
    }

    /* Джерело: підходить Map<String, Integer> для кешу Cache<String, Number>. */
    public void putAll(Map<? extends K, ? extends V> source) {
        for (Map.Entry<? extends K, ? extends V> e : source.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /* Приймач: кожну пару можна передати споживачу для надтипів K і V, зокрема BiConsumer<Object, Object>. */
    public void forEach(BiConsumer<? super K, ? super V> action) {
        purgeExpired();
        for (Map.Entry<K, Entry<V>> e : entries.entrySet()) {
            action.accept(e.getKey(), e.getValue().value);
        }
    }

    public boolean remove(K key) {
        Objects.requireNonNull(key, "Ключ не може бути null");
        if (!entries.containsKey(key)) {
            return false;
        }
        drop(key, RemovalCause.EXPLICIT);
        return true;
    }

    public int size() {
        purgeExpired();
        return entries.size();
    }

    public String stats() {
        return "вдало: " + hits + ", невдало: " + misses + ", витіснено: " + evictions;
    }

    private boolean expired(Entry<V> entry) {
        return entry.expiresAt <= clock.getAsLong();
    }

    private void drop(K key, RemovalCause cause) {
        Entry<V> removed = entries.remove(key);
        policy.onRemove(key);
        listener.onRemoval(key, removed.value, cause);
    }

    private void purgeExpired() {
        MyList<K> stale = new MyArrayList<>();
        for (Map.Entry<K, Entry<V>> e : entries.entrySet()) {
            if (expired(e.getValue())) {
                stale.add(e.getKey());
            }
        }
        for (K key : stale) {
            drop(key, RemovalCause.EXPIRED);
        }
    }
}
