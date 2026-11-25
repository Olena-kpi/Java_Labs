package util;

import java.nio.charset.Charset;
import java.util.Scanner;

public class ConsoleInput {

    /* Сканер з кодуванням консолі (Java 19+ зберігає його у stdin.encoding),
        щоб кирилиця вводилась правильно. */
    public static Scanner open() {
        String name = System.getProperty("stdin.encoding");
        if (name != null && Charset.isSupported(name)) {
            return new Scanner(System.in, Charset.forName(name));
        }
        return new Scanner(System.in);
    }
}
