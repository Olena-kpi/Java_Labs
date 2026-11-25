package hard;

/* Узагальнені методи над списками. Кожна сигнатура демонструє своє правило wildcard. */
public final class ListOps {

    private ListOps() {
    }

    /* ? extends: список лише віддає елементи, тому підходить MyList<Integer> для T = Number. */
    public static double sum(MyList<? extends Number> numbers) {
        double total = 0;
        for (Number n : numbers) {
            total += n.doubleValue();
        }
        return total;
    }

    /* Найбільший елемент: T порівнюється з собою або з надтипом (Comparable<? super T>),
        тому працює й для Novel, який успадкував порівняння від Book. */
    public static <T extends Comparable<? super T>> T max(MyList<? extends T> items) {
        if (items.isEmpty()) {
            throw new java.util.NoSuchElementException("Список порожній");
        }
        T best = null;
        for (T item : items) {
            if (best == null || item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }

    /* PECS в одному методі: з from читаємо (extends), у to записуємо (super). */
    public static <T> int moveAll(MyList<? extends T> from, MyList<? super T> to) {
        int moved = 0;
        for (T item : from) {
            to.add(item);
            moved++;
        }
        return moved;
    }

    /* MyList<?>: тип елементів не важливий. Метод-обгортка «захоплює» невідомий тип у змінну T. */
    public static void reverse(MyList<?> list) {
        reverseCaptured(list);
    }

    private static <T> void reverseCaptured(MyList<T> list) {
        for (int i = 0, j = list.size() - 1; i < j; i++, j--) {
            T tmp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, tmp);
        }
    }
}
