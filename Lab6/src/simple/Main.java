package simple;

import util.ConsoleInput;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        PhraseTranslator translator = new PhraseTranslator();
        fillDictionary(translator);
        Scanner scanner = ConsoleInput.open();

        try {
            System.out.println("Слів у словнику: " + translator.size() + ".");
            System.out.println("Додайте власні пари слів (порожнє англійське слово - завершити).");
            while (true) {
                System.out.print("Англійське слово: ");
                String english = scanner.nextLine().trim();
                if (english.isEmpty()) {
                    break;
                }
                if (!english.matches("[A-Za-z]+")) {
                    System.out.println("Помилка: слово має складатися лише з латинських літер.");
                    continue;
                }
                System.out.print("Український переклад: ");
                String ukrainian = scanner.nextLine().trim();
                if (ukrainian.isEmpty()) {
                    System.out.println("Помилка: переклад не може бути порожнім.");
                    continue;
                }
                translator.addPair(english, ukrainian);
                System.out.println("Додано. Слів у словнику: " + translator.size());
            }

            while (true) {
                System.out.print("Фраза англійською (порожній рядок - вихід): ");
                String phrase = scanner.nextLine();
                if (phrase.isBlank()) {
                    break;
                }
                System.out.println("Переклад: " + translator.translate(phrase));
            }
        } catch (NoSuchElementException e) {
            System.out.println("Введення завершено.");
        }
    }

    /* Початковий вміст словника. */
    private static void fillDictionary(PhraseTranslator translator) {
        String[][] pairs = {
            {"hello", "привіт"}, {"world", "світ"}, {"good", "добрий"}, {"morning", "ранок"},
            {"evening", "вечір"}, {"night", "ніч"}, {"i", "я"}, {"you", "ти"}, {"we", "ми"},
            {"love", "люблю"}, {"like", "подобається"}, {"study", "вивчаю"}, {"my", "мій"},
            {"friend", "друг"}, {"book", "книга"}, {"read", "читаю"}, {"write", "пишу"},
            {"code", "код"}, {"student", "студент"}, {"university", "університет"},
            {"is", "є"}, {"today", "сьогодні"}, {"programming", "програмування"}, {"language", "мова"}
        };
        for (String[] pair : pairs) {
            translator.addPair(pair[0], pair[1]);
        }
    }
}
