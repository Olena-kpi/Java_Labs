package hard;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/* Компілює java-файл з коду програми через javax.tools.JavaCompiler.
Працює лише під JDK. */
public final class SourceCompiler {

    /* Результат компіляції. */
    public record Result(boolean success, String messages) {
    }

    private final JavaCompiler compiler;
    private final Path outputDir;

    /* outputDir – каталог для .class-файлів (як параметр -d у javac) */
    public SourceCompiler(Path outputDir) {
        this.compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException(
                    "Java-компілятор недоступний. Запустіть програму під JDK, не JRE.");
        }
        this.outputDir = outputDir;
    }

    /* Компілює java-файл. Якщо не вдалося створити каталог або прочитати файл,
    кидається IOException. */
    public Result compile(Path sourceFile) throws IOException {
        Files.createDirectories(outputDir);

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        boolean success;
        /* кодування UTF-8 (як javac -encoding UTF-8) */
        try (StandardJavaFileManager fm = compiler.getStandardFileManager(
                diagnostics, null, StandardCharsets.UTF_8)) {
            Iterable<? extends JavaFileObject> units =
                    fm.getJavaFileObjects(sourceFile);
            List<String> options = List.of("-d", outputDir.toString());
            success = compiler.getTask(null, fm, diagnostics, options, null, units)
                    .call();
        }

        StringBuilder messages = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            messages.append(d.getKind())
                    .append(" (рядок ").append(d.getLineNumber()).append("): ")
                    .append(d.getMessage(null))
                    .append(System.lineSeparator());
        }
        return new Result(success, messages.toString().stripTrailing());
    }
}
