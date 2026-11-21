package simple;

import com.google.gson.Gson;

public class Main {

    public static void main(String[] args) {
        Gson gson = new Gson();

        /* a) створюємо екземпляр Person */
        Person original = new Person("Шевченко", "Тарас", 47);
        System.out.println("a) Початковий об'єкт:  " + original);

        /* b) конвертуємо в JSON */
        String json = gson.toJson(original);
        System.out.println("b) JSON:               " + json);

        /* c) конвертуємо назад в об'єкт */
        Person restored = gson.fromJson(json, Person.class);
        System.out.println("c) Відновлений об'єкт: " + restored);

        /* d) перевіряємо equals-ом */
        System.out.println("d) original.equals(restored): " + original.equals(restored));
        System.out.println("   original == restored:      " + (original == restored)
                + "  (це різні об'єкти в пам'яті)");
        System.out.println("   hashCode однакові:         "
                + (original.hashCode() == restored.hashCode()));

        Person older = new Person("Шевченко", "Тарас", 48);
        System.out.println();
        System.out.println("Для порівняння: " + older);
        System.out.println("original.equals(older): " + original.equals(older));
        System.out.println("original.equals(null):  " + original.equals(null));
    }
}
