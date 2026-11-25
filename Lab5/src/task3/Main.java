package task3;

import util.ConsoleInput;
import util.FileErrors;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = ConsoleInput.open();

        System.out.print("Вхідний файл (шлях та ім'я): ");
        String source = scanner.nextLine().trim();
        System.out.print("Вихідний файл (шлях та ім'я): ");
        String target = scanner.nextLine().trim();
        if (source.isEmpty() || target.isEmpty()) {
            System.out.println("Помилка: шлях до файлу не може бути порожнім.");
            return;
        }
        char key = readKey(scanner);
        boolean encrypt = readMode(scanner);

        try {
            TextFileCipher cipher = new TextFileCipher();
            if (encrypt) {
                cipher.encryptFile(Path.of(source), Path.of(target), key);
            } else {
                cipher.decryptFile(Path.of(source), Path.of(target), key);
            }
            System.out.println("Готово. Результат записано у файл " + target);
        } catch (IOException | InvalidPathException e) {
            System.out.println("Помилка: " + FileErrors.describe(e, source, target));
        }
    }

    /* Запитує ключ, доки не буде введено рівно один символ (пробіл теж допустимий). */
    private static char readKey(Scanner scanner) {
        while (true) {
            System.out.print("Ключовий символ: ");
            String text = scanner.nextLine();
            if (text.length() == 1 && !Character.isSurrogate(text.charAt(0))) {
                return text.charAt(0);
            }
            System.out.println("Помилка: ключ має бути одним символом.");
        }
    }

    /* true - шифрування, false - дешифрування. */
    private static boolean readMode(Scanner scanner) {
        while (true) {
            System.out.print("Режим (1 - шифрування, 2 - дешифрування): ");
            String text = scanner.nextLine().trim();
            if (text.equals("1") || text.equals("2")) {
                return text.equals("1");
            }
            System.out.println("Помилка: введіть 1 або 2.");
        }
    }
}
