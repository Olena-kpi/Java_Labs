package util;

import java.nio.charset.CharacterCodingException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class FileErrors {

    /* Зрозуміле пояснення помилки роботи з файлом. */
    public static String describe(Exception e, String... typedNames) {
        if (e instanceof NoSuchFileException noFile) {
            for (String name : typedNames) {
                if (Path.of(name).toString().equals(noFile.getFile())) {
                    return "файл не знайдено: " + name;
                }
            }
            return "файл або папку не знайдено: " + noFile.getFile();
        }
        if (e instanceof InvalidPathException) {
            return "некоректний шлях до файлу";
        }
        if (e instanceof CharacterCodingException) {
            return "файл не в кодуванні UTF-8 або пошкоджений";
        }
        return e.getMessage();
    }
}
