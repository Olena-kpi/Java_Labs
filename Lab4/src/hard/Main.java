package hard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/* Демонстрація на прикладі бібліотеки: Book -> Novel, Textbook. */
public class Main {

    static class Book implements Comparable<Book> {
        final String title;
        final int pages;

        Book(String title, int pages) {
            this.title = title;
            this.pages = pages;
        }

        @Override
        public int compareTo(Book other) {
            return Integer.compare(pages, other.pages);
        }

        @Override
        public String toString() {
            return title + "(" + pages + ")";
        }
    }

    static class Novel extends Book {
        Novel(String title, int pages) {
            super(title, pages);
        }
    }

    static class Textbook extends Book {
        Textbook(String title, int pages) {
            super(title, pages);
        }
    }

    public static void main(String[] args) {
        lists();
        sets();
        caches();
    }

    private static void lists() {
        title("Списки: MyArrayList (кільцевий буфер) і MyLinkedList (з вартовим)");

        MyList<Book> shelf = new MyArrayList<>();
        List<Novel> novels = List.of(new Novel("Місто", 190), new Novel("Intermezzo", 120),
                new Novel("Тигролови", 240));
        shelf.addAll(novels);                       /* List<Novel> -> Collection<? extends Book> */
        shelf.add(0, new Textbook("Java", 540));
        System.out.println("Полиця: addAll(List<Novel>) і add(0, ..): " + shelf);

        shelf.sort(Comparator.comparing((Object o) -> o.toString()));   /* Comparator<Object> для Book */
        System.out.println("sort(Comparator<Object>) за назвою: " + shelf);

        System.out.println("Найбільша книга (max, T = Book): " + ListOps.max(shelf));
        System.out.println("filter(> 150 стор.): " + shelf.filter(b -> b.pages > 150));
        MyList<String> titles = shelf.map(b -> b.title.toUpperCase());
        System.out.println("map -> назви великими: " + titles);

        MyList<Number> pages = new MyLinkedList<>();
        MyList<Integer> counts = new MyArrayList<>();
        List<Object> anything = new ArrayList<>();
        shelf.map(b -> b.pages).copyTo(anything);   /* Collection<? super Integer> */
        System.out.println("copyTo(List<Object>): " + anything);
        for (Book b : shelf) {
            counts.add(b.pages);
        }
        ListOps.moveAll(counts, pages);             /* з MyList<Integer> у MyList<Number> */
        pages.add(0.5);
        System.out.println("Сторінки як Number: " + pages + ", сума = " + ListOps.sum(pages));

        ListOps.reverse(pages);                     /* MyList<?> з «захопленням» типу */
        System.out.println("Після reverse(MyList<?>): " + pages);

        MyList<Book> linked = new MyLinkedList<>();
        linked.addAll(new Book[]{new Book("Додаток", 10), new Book("Вступ", 5)});
        linked.addAll(1, new Book[]{new Book("Зміст", 3)});
        System.out.println("MyLinkedList: addAll(E[]) та addAll(1, E[]): " + linked);

        MyList<Book> same = new MyArrayList<>();
        same.addAll(new Book[]{linked.get(0), linked.get(1), linked.get(2)});
        System.out.println("Списки різних класів із однаковим вмістом рівні: " + same.equals(linked));
    }

    private static void sets() {
        title("Множина: MyLinkedHashSet (масив порядку + хеш-таблиця індексів)");

        MySet<String> mine = new MyLinkedHashSet<>();
        mine.addAll(List.of("java", "kotlin", "scala", "java", "groovy"));
        mine.add(null);
        System.out.println("Порядок додавання, без повторів, з null: " + mine);

        mine.remove("kotlin");
        mine.add("clojure");
        System.out.println("Після remove(kotlin) і add(clojure): " + mine);

        MySet<String> other = new MyLinkedHashSet<>();
        other.addAll(List.of("scala", "python", "java"));
        System.out.println("union:        " + SetOps.<Object>union(mine, other));
        System.out.println("intersection: " + SetOps.<String>intersection(mine, other));
        System.out.println("difference:   " + SetOps.<String>difference(mine, other));

        for (var it = mine.iterator(); it.hasNext(); ) {
            String s = it.next();
            if (s == null || s.startsWith("s")) {
                it.remove();
            }
        }
        System.out.println("Iterator.remove() прибрав null і scala: " + mine);
        mine.retainAll(List.of("java", "clojure", "rust"));
        System.out.println("retainAll([java, clojure, rust]): " + mine + ", розмір " + mine.size());
    }

    private static void caches() {
        title("Кеш: політики FIFO / LRU / LFU, час життя, слухач видалень");

        for (String name : new String[]{"FIFO", "LRU", "LFU"}) {
            EvictionPolicy<String> policy = switch (name) {
                case "FIFO" -> EvictionPolicy.fifo();
                case "LRU" -> EvictionPolicy.lru();
                default -> EvictionPolicy.lfu();
            };
            Cache<String, Integer> cache = Cache.<String, Integer>builder().maxSize(3).policy(policy).build();
            cache.putAll(Map.of("a", 1));
            cache.put("b", 2);
            cache.put("c", 3);
            cache.get("c");
            cache.get("a");
            cache.get("a");
            cache.get("b");
            cache.put("d", 4);
            StringBuilder alive = new StringBuilder();
            for (String key : new String[]{"a", "b", "c", "d"}) {
                alive.append(key).append(cache.get(key).isPresent() ? "+ " : "- ");
            }
            System.out.println(name + ": після звернень c,a,a,b додано d -> " + alive + "(" + cache.stats() + ")");
        }

        long[] now = {0};
        Cache<String, Number> prices = Cache.<String, Number>builder()
                .maxSize(2).ttl(100).clock(() -> now[0])
                .onRemoval((Object k, Object v, Cache.RemovalCause why) ->      /* слухач для надтипів K і V */
                        System.out.println("  видалено " + k + "=" + v + " (" + why + ")"))
                .build();
        prices.putAll(Map.of("apple", 12.5));       /* Map<String, Double> -> Map<? extends String, ? extends Number> */
        prices.put("pear", 30, 300);                /* для цього запису — власний час життя */
        now[0] = 150;
        System.out.println("t=150: apple -> " + prices.get("apple").map(Object::toString).orElse("протермінований")
                + ", pear -> " + prices.get("pear").map(Object::toString).orElse("немає"));
        System.out.println("computeIfAbsent(plum): " + prices.computeIfAbsent("plum", String::length));
        prices.put("plum", 7);
        prices.put("fig", 9);
        prices.forEach((Object k, Object v) -> System.out.println("  у кеші: " + k + " -> " + v));

        try {
            prices.put("x", null);
        } catch (NullPointerException e) {
            System.out.println("null заборонено: " + e.getMessage());
        }
    }

    private static void title(String text) {
        System.out.println();
        System.out.println("### " + text);
    }
}
