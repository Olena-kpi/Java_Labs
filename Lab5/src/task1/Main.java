package task1;

import util.ConsoleInput;
import util.FileErrors;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = ConsoleInput.open();
        System.out.print("Де лежить текстовий файл (шлях та ім'я): ");
        String fileName = scanner.nextLine().trim();
        if (fileName.isBlank()) {
            System.out.println("Помилка: шлях до файлу не може бути порожнім.");
            return;
        }

        try {
            MaxWordsFinder.Result result = new MaxWordsFinder().find(Path.of(fileName));
            if (result == null) {
                System.out.println("Файл порожній, рядків немає.");
                return;
            }
            System.out.println("Рядок №" + result.lineNumber() + " містить найбільше слів: " + result.words());
            System.out.println("> " + result.line());
            if (result.otherLinesWithSameCount() > 0) {
                System.out.println("(ще рядків з такою самою кількістю слів: " + result.otherLinesWithSameCount()
                        + "; показано перший)");
            }
        } catch (IOException | InvalidPathException e) {
            System.out.println("Помилка: " + FileErrors.describe(e, fileName));
        }
    }
}
