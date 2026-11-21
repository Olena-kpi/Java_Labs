package hard;

import java.nio.file.Files;
import java.nio.file.Path;

/* Hard Task: програма постійно працює і підхоплює зміни у файлі TestModule.java:
перекомпільовує його, завантажує нову версію класу і виводить нове повідомлення.

Необов'язкові параметри командного рядка:
  - шлях до TestModule.java (за замовчуванням modules/TestModule.java);
  - каталог для скомпільованих класів (за замовчуванням out/dynamic).

Шляхи відраховуються від робочого каталогу (кореня проєкту). */
public class Main {

    private static final String CLASS_NAME = "TestModule";
    private static final long POLL_INTERVAL_MS = 500;

    public static void main(String[] args) throws Exception {
        Path sourceFile =
                Path.of(args.length > 0 ? args[0] : "modules/TestModule.java");
        Path outputDir = Path.of(args.length > 1 ? args[1] : "out/dynamic");

        if (!Files.isRegularFile(sourceFile)) {
            System.err.println("Файл не знайдено: " + sourceFile.toAbsolutePath());
            System.exit(1);
        }

        HotReloader reloader = new HotReloader(sourceFile, CLASS_NAME, outputDir);

        System.out.println("Спостереження за файлом: " + sourceFile.toAbsolutePath());
        System.out.println("Змініть та збережіть файл — нова версія завантажиться "
                + "автоматично.");
        System.out.println("Для виходу натисніть Ctrl+C "
                + "(або зупиніть програму в IDE).");
        System.out.println();

        /* програма працює, поки її не зупинять */
        while (true) {
            reloader.reloadIfChanged();
            Thread.sleep(POLL_INTERVAL_MS);
        }
    }
}
