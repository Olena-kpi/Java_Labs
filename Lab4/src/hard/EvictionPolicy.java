package hard;

import java.util.HashMap;
import java.util.Map;

/* Правило витіснення записів кешу: кеш повідомляє про події, а політика називає жертву. */
public interface EvictionPolicy<K> {

    void onInsert(K key);

    void onAccess(K key);

    void onRemove(K key);

    /* Ключ, який слід витіснити (політика не порожня, коли кеш викликає цей метод). */
    K victim();

    /* FIFO: витісняється той, що живе в кеші найдовше, звернення порядок не змінюють. */
    static <K> EvictionPolicy<K> fifo() {
        return new OrderBased<>(false);
    }

    /* LRU: витісняється той, до якого найдавніше зверталися. */
    static <K> EvictionPolicy<K> lru() {
        return new OrderBased<>(true);
    }

    /* LFU: витісняється той, до якого зверталися найрідше (при рівності — старіший). */
    static <K> EvictionPolicy<K> lfu() {
        return new Frequency<>();
    }

    final class OrderBased<K> implements EvictionPolicy<K> {
        private final boolean refreshOnAccess;
        private final MySet<K> order = new MyLinkedHashSet<>();

        private OrderBased(boolean refreshOnAccess) {
            this.refreshOnAccess = refreshOnAccess;
        }

        @Override
        public void onInsert(K key) {
            order.remove(key);
            order.add(key);
        }

        @Override
        public void onAccess(K key) {
            if (refreshOnAccess) {
                order.remove(key);
                order.add(key);
            }
        }

        @Override
        public void onRemove(K key) {
            order.remove(key);
        }

        @Override
        public K victim() {
            return order.iterator().next();
        }
    }

    final class Frequency<K> implements EvictionPolicy<K> {
        private final Map<K, Integer> uses = new HashMap<>();
        private final MySet<K> order = new MyLinkedHashSet<>();

        private Frequency() {
        }

        @Override
        public void onInsert(K key) {
            uses.put(key, 1);
            order.add(key);
        }

        @Override
        public void onAccess(K key) {
            uses.merge(key, 1, Integer::sum);
        }

        @Override
        public void onRemove(K key) {
            uses.remove(key);
            order.remove(key);
        }

        @Override
        public K victim() {
            K best = null;
            int fewest = Integer.MAX_VALUE;
            for (K key : order) {          /* порядок додавання розв'язує рівність */
                int count = uses.get(key);
                if (count < fewest) {
                    fewest = count;
                    best = key;
                }
            }
            return best;
        }
    }
}
