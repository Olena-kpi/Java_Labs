package hard;

import java.util.Arrays;
import java.util.RandomAccess;

public class Main {

    public static void main(String[] args) {
        demoLists();
        demoLinkedHashSet();
        demoCache();
    }

    /* п. 1: MyList */

    private static void demoLists() {
        title("1. MyArrayList та MyLinkedList (інтерфейс MyList)");

        MyList arrayList = new MyArrayList();
        MyList linkedList = new MyLinkedList();
        System.out.println("MyArrayList реалізує RandomAccess:  " + (arrayList instanceof RandomAccess));
        System.out.println("MyLinkedList реалізує RandomAccess: " + (linkedList instanceof RandomAccess));

        for (MyList list : new MyList[]{arrayList, linkedList}) {
            System.out.println();
            System.out.println("--- " + list.getClass().getSimpleName() + " ---");

            list.add("A");
            list.add("B");
            list.add("C");
            System.out.println("add(A), add(B), add(C)        -> " + list);

            list.add(1, "X");
            System.out.println("add(1, X)                     -> " + list);

            list.addAll(new Object[]{"D", "E"});
            System.out.println("addAll([D, E])                -> " + list);

            list.addAll(2, new Object[]{"Y", "Z"});
            System.out.println("addAll(2, [Y, Z])             -> " + list);

            System.out.println("get(3)                        -> " + list.get(3));

            list.set(0, "a");
            System.out.println("set(0, a)                     -> " + list);

            System.out.println("indexOf(Z)                    -> " + list.indexOf("Z"));
            System.out.println("indexOf(Q)                    -> " + list.indexOf("Q"));

            System.out.println("remove(1)                     -> видалено " + list.remove(1)
                    + ", список " + list);
            System.out.println("size()                        -> " + list.size());
            System.out.println("toArray()                     -> " + Arrays.toString(list.toArray()));

            try {
                list.get(100);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("get(100)                      -> IndexOutOfBoundsException: "
                        + e.getMessage());
            }
        }
        System.out.println();
        System.out.println("Списки рівні за вмістом (equals): " + arrayList.equals(linkedList));
    }

    /* п. 2: LinkedHashSet */

    private static void demoLinkedHashSet() {
        title("2. MyLinkedHashSet");

        MySet set = new MyLinkedHashSet();
        for (String s : new String[]{"banana", "apple", "cherry", "apple", "date"}) {
            boolean added = set.add(s);
            System.out.println("add(" + s + ") -> " + added + "   " + set);
        }
        set.add(null);
        System.out.println("add(null)       -> допускається: " + set);

        System.out.println("contains(cherry): " + set.contains("cherry"));
        System.out.println("remove(apple):    " + set.remove("apple") + "   " + set);
        System.out.println("size():           " + set.size());

        System.out.print("Обхід (for-each): ");
        for (Object o : set) {
            System.out.print(o + " ");
        }
        System.out.println();

        /* багато елементів: таблиця збільшується, а порядок вставки зберігається */
        MySet numbers = new MyLinkedHashSet();
        for (int i = 20; i >= 1; i--) {
            numbers.add(i * 7 % 23);
        }
        System.out.println("Порядок вставки зберігається після збільшення таблиці:");
        System.out.println(numbers);
    }

    /* п. 3: кеш */

    private static void demoCache() {
        title("3. Кеш (відмінності від Map)");

        /* 3.1 обмеження null */
        Cache cache = new Cache(3);
        try {
            cache.put("key", null);
        } catch (NullPointerException e) {
            System.out.println("put(key, null)   -> NullPointerException: " + e.getMessage());
        }
        try {
            cache.get(null);
        } catch (NullPointerException e) {
            System.out.println("get(null)        -> NullPointerException: " + e.getMessage());
        }

        /* 3.2 витіснення (eviction): політика LRU */
        System.out.println();
        System.out.println("Витіснення, місткість = " + cache.maxSize() + " (вивід: від найновішого):");
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        System.out.println("put a, b, c      -> " + cache);
        cache.get("a");
        System.out.println("get(a)           -> " + cache + "   (a став найновішим)");
        cache.put("d", 4);
        System.out.println("put(d)           -> " + cache + "   (витіснено b)");
        System.out.println("containsKey(b):     " + cache.containsKey("b"));
        System.out.println("Кількість витіснень: " + cache.evictionCount());

        /* 3.3 спливання часу життя (expiry) — з керованим годинником, без реального очікування */
        System.out.println();
        System.out.println("Час життя записів = 100 мс:");
        long[] now = {0};
        Cache expiring = new Cache(10, 100, () -> now[0]);
        expiring.put("token", "abc123");
        System.out.println("t = 0 мс,   get(token) -> " + expiring.get("token"));
        now[0] = 99;
        System.out.println("t = 99 мс,  get(token) -> " + expiring.get("token"));
        now[0] = 100;
        System.out.println("t = 100 мс, get(token) -> " + expiring.get("token")
                + "   (запис втратив чинність), size() = " + expiring.size());
    }

    private static void title(String text) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println(text);
        System.out.println("==================================================");
    }
}
