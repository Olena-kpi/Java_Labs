package task3;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class TextFileCipher {

    /* Код кожного символу збільшується на код ключового символу. */
    public void encryptFile(Path input, Path output, char key) throws IOException {
        convert(input, output, key);
    }

    /* Код кожного символу зменшується на код ключового символу. */
    public void decryptFile(Path input, Path output, char key) throws IOException {
        convert(input, output, -key);
    }

    private void convert(Path input, Path output, int offset) throws IOException {
        if (input.toAbsolutePath().normalize().equals(output.toAbsolutePath().normalize())) {
            throw new IOException("вхідний і вихідний файли мають бути різними");
        }
        /* вхідний файл відкривається першим: якщо його немає, вихідний не створюється */
        try (Reader reader = new KeyShiftReader(Files.newBufferedReader(input), offset);
             Writer writer = Files.newBufferedWriter(output)) {
            reader.transferTo(writer);
        }
    }
}
