package simple;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

/* Консольний застосунок:
підрахунок кількості надпростих чисел у натуральному ряді до n (n <= 1000). */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            int n = readNatural(scanner);

            int[] superPrimes = SuperPrimes.findSuperPrimes(n);
            System.out.println("Надпрості числа від 1 до " + n + ": "
                    + Arrays.toString(superPrimes));
            System.out.println("Кількість надпростих чисел: "
                    + SuperPrimes.countSuperPrimes(n));
        } catch (NoSuchElementException e) {
            System.out.println("Введення завершено без отримання числа.");
        }
    }

    /* Зчитує з клавіатури натуральне число n (1..1000). Якщо введено не число
    або число поза допустимим діапазоном, пропонує повторити введення. */
    private static int readNatural(Scanner scanner) {
        while (true) {
            System.out.print("Введіть натуральне число n (1.."
                    + SuperPrimes.MAX_N + "): ");
            String line = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(line);
                if (n >= 1 && n <= SuperPrimes.MAX_N) {
                    return n;
                }
                System.out.println("Помилка: число має бути в межах від 1 до "
                        + SuperPrimes.MAX_N + ".");
            } catch (NumberFormatException e) {
                System.out.println("Помилка: \"" + line + "\" не є цілим числом.");
            }
        }
    }
}
