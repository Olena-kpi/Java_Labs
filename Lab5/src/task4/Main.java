package task4;

import util.ConsoleInput;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String DEFAULT_URL = "https://example.com";

    public static void main(String[] args) {
        Scanner scanner = ConsoleInput.open();
        System.out.print("URL сторінки (Enter - " + DEFAULT_URL + "): ");
        String url = scanner.nextLine().trim();
        if (url.isEmpty()) {
            url = DEFAULT_URL;
        }

        try {
            TagStatistics statistics = new PageTagCounter().count(url);
            if (statistics.isEmpty()) {
                System.out.println("На сторінці тегів не знайдено.");
                return;
            }
            System.out.println("Усього тегів на сторінці: " + statistics.total());
            printTable("За назвою тегу (за алфавітом, за зростанням)", statistics.byName(), statistics.total());
            printTable("За частотою появи (за зростанням)", statistics.byFrequency(), statistics.total());
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка: некоректна адреса " + url + " (" + e.getMessage() + ")");
        } catch (IOException e) {
            System.out.println("Помилка: не вдалося прочитати сторінку (" + e.getMessage() + ")");
        }
    }

    /* Таблиця: тег, кількість, частка від усіх тегів і смужка з "#" для наочності. */
    private static void printTable(String title, List<TagStatistics.Entry> entries, int total) {
        System.out.println();
        System.out.println("== " + title + " ==");
        for (TagStatistics.Entry entry : entries) {
            double share = 100.0 * entry.count() / total;
            System.out.printf("%-8s %4d  %5.1f%%  %s%n", entry.tag(), entry.count(), share,
                    "#".repeat((int) Math.ceil(share / 5)));
        }
    }
}
