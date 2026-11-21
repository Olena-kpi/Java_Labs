package hard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/* Стежить за змінами java-файла, перекомпільовує його і завантажує нову версію класу
новим DynamicClassOverloader. Про вміст класу нічого не знає: створює екземпляр через
Class.forName і виводить його через System.out.println. */
public final class HotReloader {

    /* Пауза перед компіляцією, щоб редактор встиг дописати файл. */
    private static final long DEBOUNCE_MS = 200;

    private static final DateTimeFormatter TIME =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    /* Стан файла: час останньої зміни і розмір. */
    private record Snapshot(long lastModified, long size) {
    }

    private final Path sourceFile;
    private final String className;
    private final Path outputDir;
    private final SourceCompiler compiler;

    private Snapshot lastSnapshot;
    private int loadCounter;

    /* sourceFile – java-файл, className – ім'я класу, outputDir – каталог для .class-файлів */
    public HotReloader(Path sourceFile, String className, Path outputDir) {
        this.sourceFile = sourceFile;
        this.className = className;
        this.outputDir = outputDir;
        this.compiler = new SourceCompiler(outputDir);
    }

    /* Перевіряє, чи змінився файл. Якщо так (або це перший виклик) – перекомпільовує його
    і завантажує нову версію класу. */
    public boolean reloadIfChanged() throws IOException, InterruptedException {
        Snapshot current = snapshot();
        if (current == null || current.equals(lastSnapshot)) {
            return false;
        }

        boolean firstRun = lastSnapshot == null;
        if (!firstRun) {
            Thread.sleep(DEBOUNCE_MS);
            current = snapshot();
            if (current == null) {
                return false;
            }
        }
        lastSnapshot = current;

        log(firstRun
                ? "Первинне завантаження " + sourceFile.getFileName()
                : "Виявлено зміни у " + sourceFile.getFileName());
        reload();
        return true;
    }

    private void reload() throws IOException {
        long start = System.nanoTime();
        SourceCompiler.Result result = compiler.compile(sourceFile);
        long millis = (System.nanoTime() - start) / 1_000_000;

        if (!result.messages().isEmpty()) {
            System.out.println(result.messages());
        }
        if (!result.success()) {
            log("Помилка компіляції. Залишається попередня версія класу " + className);
            return;
        }
        log("Компіляція успішна (" + millis + " мс)");

        try {
            /* новий завантажувач = новий кеш класів */
            ClassLoader loader =
                    new DynamicClassOverloader(new String[]{outputDir.toString()});
            Class<?> clazz = Class.forName(className, true, loader);
            Object t = clazz.getDeclaredConstructor().newInstance();

            loadCounter++;
            String loaderId = Integer.toHexString(System.identityHashCode(loader));
            log("Клас завантажено (перезавантажень: " + loadCounter
                    + ", завантажувач: " + loaderId + ")");
            System.out.println(t);
        } catch (ReflectiveOperationException | LinkageError e) {
            log("Не вдалося завантажити клас " + className + ": " + e);
        }
    }

    /* Повертає стан файла або null, якщо файл тимчасово зник
    (деякі редактори зберігають зміни через видалення і перейменування файла). */
    private Snapshot snapshot() throws IOException {
        try {
            BasicFileAttributes attributes =
                    Files.readAttributes(sourceFile, BasicFileAttributes.class);
            return new Snapshot(
                    attributes.lastModifiedTime().toMillis(), attributes.size());
        } catch (NoSuchFileException e) {
            return null;
        }
    }

    private static void log(String message) {
        System.out.println("[" + LocalTime.now().format(TIME) + "] " + message);
    }
}
