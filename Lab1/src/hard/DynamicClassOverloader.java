package hard;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/* Власний завантажувач класів. Кожен новий екземпляр має свій кеш, тому нову версію
.class-файла можна завантажити, створивши новий екземпляр.

Спочатку клас шукається у власних каталогах. Якщо файлу немає (наприклад, для java.lang.Object),
клас завантажує системний завантажувач. Класи, в імені яких є TRUE_STATIC_MARKER,
завжди завантажує системний завантажувач. */
public class DynamicClassOverloader extends ClassLoader {

    /* Якщо ім'я класу містить цей фрагмент, клас завантажує системний завантажувач. */
    public static final String TRUE_STATIC_MARKER = "truestatic";

    /* Кеш класів, завантажених цим завантажувачем. */
    private final Map<String, Class<?>> classesHash = new HashMap<>();

    /* Каталоги пошуку .class-файлів. */
    private final Path[] classPath;

    /* classPath – каталоги, у яких шукаються .class-файли */
    public DynamicClassOverloader(String[] classPath) {
        this.classPath = new Path[classPath.length];
        for (int i = 0; i < classPath.length; i++) {
            this.classPath[i] = Path.of(classPath[i]);
        }
    }

    /* Спочатку пробує завантажити клас сам (через findClass), і лише потім звертається
    до системного завантажувача. */
    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        Class<?> result = findClass(name);
        if (resolve) {
            resolveClass(result);
        }
        return result;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> result = classesHash.get(name);
        if (result != null) {
            return result;
        }

        /* true-static класи завантажує системний завантажувач */
        if (name.toLowerCase(Locale.ROOT).contains(TRUE_STATIC_MARKER)) {
            return findSystemClass(name);
        }

        /* клас mypackage.MyClass лежить у файлі mypackage/MyClass.class */
        Path file = findFile(name.replace('.', '/'), ".class");
        if (file == null) {
            /* стандартні класи (java.lang.* тощо) завантажує системний завантажувач */
            return findSystemClass(name);
        }

        try {
            byte[] classBytes = loadFileAsBytes(file);
            /* перетворюємо байт-код на клас */
            result = defineClass(name, classBytes, 0, classBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("Cannot load class " + name + ": " + e, e);
        } catch (ClassFormatError e) {
            throw new ClassNotFoundException(
                    "Format of class file incorrect for class " + name + ": " + e, e);
        }

        classesHash.put(name, result);
        return result;
    }

    @Override
    protected URL findResource(String name) {
        Path file = findFile(name, "");
        if (file == null) {
            return null;
        }
        try {
            return file.toUri().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /* Шукає файл name + extension у каталогах пошуку. У name підкаталоги розділені символом '/'. */
    private Path findFile(String name, String extension) {
        for (Path directory : classPath) {
            Path file = directory.resolve(name + extension);
            if (Files.isRegularFile(file)) {
                return file;
            }
        }
        return null;
    }

    /* Зчитує файл у масив байтів. */
    public static byte[] loadFileAsBytes(Path file) throws IOException {
        return Files.readAllBytes(file);
    }
}
