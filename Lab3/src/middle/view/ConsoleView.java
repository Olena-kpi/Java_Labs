package middle.view;

import middle.model.PassType;
import middle.model.SkiPass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleView {

    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Scanner scanner = new Scanner(System.in);

    public void showMenu(LocalDateTime now) {
        System.out.println();
        System.out.println("--- ТУРНІКЕТ ЛИЖНОГО ПІДЙОМНИКА (поточний час: "
                + now.format(DATE_TIME) + ") ---");
        System.out.println("1. Випустити ski-pass");
        System.out.println("2. Заблокувати ski-pass");
        System.out.println("3. Пройти через турнікет");
        System.out.println("4. Пошкодити картку (імітація збою зчитування)");
        System.out.println("5. Реєстр виданих карток");
        System.out.println("6. Статистика турнікета: сумарна");
        System.out.println("7. Статистика турнікета: за типами ski-pass");
        System.out.println("8. Встановити поточний час (імітація)");
        System.out.println("0. Вихід");
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public String askText(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    public int askNumber(String prompt, int min, int max) {
        while (true) {
            String line = askText(prompt + " (" + min + "-" + max + ")");
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                /* не число - нижче буде повідомлення про помилку */
            }
            System.out.println("Помилка: введіть ціле число від " + min + " до " + max + ".");
        }
    }

    public LocalDate askDate(String prompt, LocalDate defaultValue) {
        while (true) {
            String line = askText(prompt + " [Enter = " + defaultValue + "]");
            if (line.isEmpty()) {
                return defaultValue;
            }
            try {
                return LocalDate.parse(line);
            } catch (DateTimeParseException e) {
                System.out.println("Помилка: потрібна дата у форматі рррр-мм-дд.");
            }
        }
    }

    public LocalDateTime askDateTime(String prompt, LocalDateTime defaultValue) {
        while (true) {
            String line = askText(prompt + " [Enter = " + defaultValue.format(DATE_TIME) + "]");
            if (line.isEmpty()) {
                return defaultValue;
            }
            try {
                return LocalDateTime.parse(line, DATE_TIME);
            } catch (DateTimeParseException e) {
                System.out.println("Помилка: потрібен формат рррр-мм-дд гг:хх.");
            }
        }
    }

    public void showPassTypes() {
        System.out.println();
        System.out.println("Види ski-pass:");
        PassType[] types = PassType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.printf("%2d. %s%n", i + 1, types[i].getTitle());
        }
    }

    public void showPasses(List<SkiPass> passes) {
        System.out.println();
        if (passes.isEmpty()) {
            System.out.println("Реєстр порожній: жодної картки ще не видано.");
            return;
        }
        System.out.println("Реєстр виданих карток (" + passes.size() + "):");
        for (SkiPass pass : passes) {
            System.out.println(pass.describe());
        }
    }

    public void showTotal(int allowed, int denied) {
        System.out.println();
        System.out.println("=== Сумарна статистика турнікета ===");
        System.out.println("Проходів дозволено: " + allowed);
        System.out.println("Проходів заборонено: " + denied);
        System.out.println("Усього спроб: " + (allowed + denied));
    }

    public void showByType(Map<PassType, Integer> allowed,
                           Map<PassType, Integer> denied, int unreadable) {
        System.out.println();
        System.out.println("=== Статистика турнікета за типами ski-pass ===");
        if (allowed.isEmpty() && denied.isEmpty() && unreadable == 0) {
            System.out.println("Спроб проходу ще не було.");
            return;
        }
        System.out.printf("%-32s %10s %10s%n", "Тип", "Дозволено", "Заборонено");
        for (PassType type : PassType.values()) {
            int a = allowed.getOrDefault(type, 0);
            int d = denied.getOrDefault(type, 0);
            if (a > 0 || d > 0) {
                System.out.printf("%-32s %10d %10d%n", type.getTitle(), a, d);
            }
        }
        if (unreadable > 0) {
            System.out.printf("%-32s %10s %10d%n",
                    "Тип невідомий (збій зчитування)", "-", unreadable);
        }
    }
}
