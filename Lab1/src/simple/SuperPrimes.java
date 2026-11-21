package simple;

/* Завдання варіанту №8 (Simple Task).

Надпростим називається число, якщо воно є простим та число, яке отримане
з нього записом цифр у зворотному порядку, теж є простим.
Клас містить функцію, що визначає кількість надпростих чисел
у натуральному ряді від 1 до n (де n <= 1000). */
public final class SuperPrimes {

    /* Максимально допустиме значення вхідного параметра згідно з умовою завдання. */
    public static final int MAX_N = 1000;

    private SuperPrimes() {
        /* утилітарний клас: екземпляри не створюються */
    }

    /* Визначає кількість надпростих чисел у натуральному ряді від 1 до n.
    Якщо n виходить за допустимі межі, кидається IllegalArgumentException. */
    public static int countSuperPrimes(int n) {
        checkRange(n);
        int count = 0;
        for (int number = 2; number <= n; number++) {
            if (isSuperPrime(number)) {
                count++;
            }
        }
        return count;
    }

    /* Повертає масив усіх надпростих чисел від 1 до n (у порядку зростання).
    Якщо n виходить за допустимі межі, кидається IllegalArgumentException. */
    public static int[] findSuperPrimes(int n) {
        int[] result = new int[countSuperPrimes(n)];
        int index = 0;
        for (int number = 2; number <= n; number++) {
            if (isSuperPrime(number)) {
                result[index++] = number;
            }
        }
        return result;
    }

    /* Перевіряє, чи є число надпростим: воно саме і число, записане тими самими
    цифрами у зворотному порядку, мають бути простими. */
    public static boolean isSuperPrime(int number) {
        return isPrime(number) && isPrime(reverse(number));
    }

    /* Перевіряє, чи є натуральне число простим, тобто таким, що ділиться
    лише на 1 та на себе. Одиниця простим числом не вважається. */
    public static boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }
        if (number % 2 == 0) {
            return number == 2;
        }
        /* достатньо перевірити непарні дільники до кореня з number */
        for (int divisor = 3; (long) divisor * divisor <= number; divisor += 2) {
            if (number % divisor == 0) {
                return false;
            }
        }
        return true;
    }

    /* Повертає число, отримане записом цифр заданого натурального числа
    у зворотному порядку (наприклад, 137 -> 731, 120 -> 21). */
    public static int reverse(int number) {
        int reversed = 0;
        while (number > 0) {
            reversed = reversed * 10 + number % 10;
            number /= 10;
        }
        return reversed;
    }

    private static void checkRange(int n) {
        if (n < 1 || n > MAX_N) {
            throw new IllegalArgumentException(
                    "Число n має бути натуральним і не більшим за " + MAX_N
                            + ", отримано: " + n);
        }
    }
}
