package task4;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/* Лічильник тегів із двома способами впорядкування результатів. */
public class TagStatistics {

    public record Entry(String tag, int count) {
    }

    /* TreeMap тримає назви в лексикографічному порядку */
    private final Map<String, Integer> counts = new TreeMap<>();
    private int total;

    public void add(String tag) {
        counts.merge(tag, 1, Integer::sum);
        total++;
    }

    public int total() {
        return total;
    }

    public boolean isEmpty() {
        return counts.isEmpty();
    }

    /* За назвою тегу, лексикографічно за зростанням. */
    public List<Entry> byName() {
        return entries(Comparator.comparing(Entry::tag));
    }

    /* За частотою появи за зростанням; теги з однаковою частотою - за назвою. */
    public List<Entry> byFrequency() {
        return entries(Comparator.comparingInt(Entry::count).thenComparing(Entry::tag));
    }

    private List<Entry> entries(Comparator<Entry> order) {
        List<Entry> list = new ArrayList<>();
        counts.forEach((tag, count) -> list.add(new Entry(tag, count)));
        list.sort(order);
        return list;
    }
}
