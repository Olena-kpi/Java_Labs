package hard;

import util.ConsoleInput;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/* Консоль керується короткими командами (help - їхній список). Кожна команда, що змінює
    дерево, повідомляє, скільки обертань і перефарбувань знадобилося. */
public class Main {

    private static final int RANGE = 200;      /* випадкові числа від 1 до 200 */
    private static final int MAX_COUNT = 40;

    private static final Random random = new Random();
    private static final RedBlackTree tree = new RedBlackTree();

    public static void main(String[] args) {
        Scanner scanner = ConsoleInput.open();
        System.out.println("Червоно-чорне дерево. Команда help покаже список команд.");
        try {
            while (true) {
                System.out.print("rbt> ");
                String[] words = scanner.nextLine().trim().split("\\s+");
                String command = words[0].toLowerCase();
                String[] arguments = Arrays.copyOfRange(words, 1, words.length);
                if (!execute(command, arguments)) {
                    return;
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Введення завершено.");
        }
    }

    /* false - команда завершення програми. */
    private static boolean execute(String command, String[] arguments) {
        switch (command) {
            case "" -> { }
            case "help" -> printHelp();
            case "random" -> fillRandom(arguments);
            case "add" -> change(arguments, true);
            case "del" -> change(arguments, false);
            case "find" -> find(arguments);
            case "show" -> show();
            case "walk" -> walk();
            case "check" -> System.out.println(tree.isValid()
                    ? "Дерево коректне: корінь чорний, червоні вузли не йдуть підряд, чорна висота однакова."
                    : "Увага: властивості дерева порушено!");
            case "clear" -> {
                while (!tree.isEmpty()) {
                    tree.delete(tree.preOrder().get(0));
                }
                System.out.println("Дерево очищено.");
            }
            case "quit", "exit" -> {
                return false;
            }
            default -> System.out.println("Невідома команда \"" + command + "\". Список команд: help.");
        }
        return true;
    }

    private static void printHelp() {
        System.out.println("  random N [sorted] - N випадкових чисел (1.." + RANGE + ", не більше " + MAX_COUNT
                + "); з sorted їх спершу впорядковують за зростанням");
        System.out.println("  add x y z ...     - додати числа з клавіатури");
        System.out.println("  del x y z ...     - видалити числа");
        System.out.println("  find x            - показати шлях пошуку числа від кореня");
        System.out.println("  show              - намалювати дерево");
        System.out.println("  walk              - три обходи дерева");
        System.out.println("  check             - перевірити властивості дерева");
        System.out.println("  clear             - видалити всі числа");
        System.out.println("  quit              - вийти");
    }

    /* Масив випадкових чисел виводиться, щоб було видно порядок додавання. */
    private static void fillRandom(String[] arguments) {
        if (arguments.length == 0 || arguments.length > 2
                || (arguments.length == 2 && !arguments[1].equalsIgnoreCase("sorted"))) {
            System.out.println("Формат: random N [sorted]");
            return;
        }
        Integer count = parse(arguments[0]);
        if (count == null || count < 1 || count > MAX_COUNT) {
            System.out.println("N має бути цілим числом від 1 до " + MAX_COUNT + ".");
            return;
        }
        int[] numbers = random.ints(count, 1, RANGE + 1).toArray();
        System.out.println("Згенеровано масив: " + Arrays.toString(numbers));
        if (arguments.length == 2) {
            Arrays.sort(numbers);
            System.out.println("Після впорядкування: " + Arrays.toString(numbers));
        }
        int rotationsBefore = tree.rotations();
        int flipsBefore = tree.colorFlips();
        int added = 0;
        for (int number : numbers) {
            if (tree.insert(number)) {
                added++;
            }
        }
        System.out.println("Додано чисел: " + added + ", дублікатів пропущено: " + (count - added));
        System.out.println("Обертань: " + (tree.rotations() - rotationsBefore)
                + ", перефарбувань: " + (tree.colorFlips() - flipsBefore));
    }

    private static void change(String[] arguments, boolean adding) {
        if (arguments.length == 0) {
            System.out.println("Вкажіть хоча б одне число, наприклад: " + (adding ? "add 5 8 13" : "del 8"));
            return;
        }
        int rotationsBefore = tree.rotations();
        int flipsBefore = tree.colorFlips();
        for (String word : arguments) {
            Integer number = parse(word);
            if (number == null) {
                System.out.println("  \"" + word + "\" - не ціле число, пропущено");
            } else if (adding) {
                System.out.println(tree.insert(number) ? "  + " + number : "  " + number + " уже є в дереві");
            } else {
                System.out.println(tree.delete(number) ? "  - " + number : "  " + number + " у дереві немає");
            }
        }
        System.out.println("Обертань: " + (tree.rotations() - rotationsBefore)
                + ", перефарбувань: " + (tree.colorFlips() - flipsBefore));
    }

    private static void find(String[] arguments) {
        Integer number = arguments.length == 1 ? parse(arguments[0]) : null;
        if (number == null) {
            System.out.println("Формат: find x (x - ціле число)");
            return;
        }
        List<Integer> path = tree.searchPath(number);
        if (path.isEmpty()) {
            System.out.println("Дерево порожнє.");
            return;
        }
        String route = path.toString().replace(", ", " -> ").replaceAll("[\\[\\]]", "");
        System.out.println(route + (tree.contains(number)
                ? " (знайдено, кроків: " + (path.size() - 1) + ")"
                : " (числа " + number + " немає)"));
    }

    private static void show() {
        if (tree.isEmpty()) {
            System.out.println("Дерево порожнє.");
            return;
        }
        System.out.print(tree.draw());
        System.out.println("Вузлів: " + tree.size() + ", висота: " + tree.height());
    }

    private static void walk() {
        if (tree.isEmpty()) {
            System.out.println("Дерево порожнє.");
            return;
        }
        System.out.println("Зростання (симетричний): " + tree.inOrder());
        System.out.println("Прямий (корінь першим):  " + tree.preOrder());
        System.out.println("Зворотний (корінь у кінці): " + tree.postOrder());
    }

    private static Integer parse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
