package hard;

/* Операції над множинами. Результат має тип T, а аргументи можуть бути множинами підтипів. */
public final class SetOps {

    private SetOps() {
    }

    public static <T> MySet<T> union(MySet<? extends T> a, MySet<? extends T> b) {
        MySet<T> result = new MyLinkedHashSet<>();
        for (T item : a) {
            result.add(item);
        }
        for (T item : b) {
            result.add(item);
        }
        return result;
    }

    /* Другий аргумент лише перевіряється через contains(Object), тому MySet<?>. */
    public static <T> MySet<T> intersection(MySet<? extends T> a, MySet<?> b) {
        MySet<T> result = new MyLinkedHashSet<>();
        for (T item : a) {
            if (b.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public static <T> MySet<T> difference(MySet<? extends T> a, MySet<?> b) {
        MySet<T> result = new MyLinkedHashSet<>();
        for (T item : a) {
            if (!b.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }
}
