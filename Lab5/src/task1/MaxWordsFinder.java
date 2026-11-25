package task1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MaxWordsFinder {

    /* Підсумок пошуку: номер рядка (з 1), сам рядок, кількість слів і те, скільки ще рядків
        мають стільки ж слів. */
    public record Result(int lineNumber, String line, int words, int otherLinesWithSameCount) {
    }

    /* Шукає в файлі рядок з найбільшою кількістю слів. Якщо таких кілька, береться перший.
        Слово - послідовність символів між пропусками. Для файлу без рядків повертає null. */
    public Result find(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        int bestIndex = -1;
        int bestWords = -1;
        int ties = 0;
        for (int i = 0; i < lines.size(); i++) {
            int words = countWords(lines.get(i));
            if (words > bestWords) {
                bestIndex = i;
                bestWords = words;
                ties = 0;
            } else if (words == bestWords) {
                ties++;
            }
        }
        return bestIndex < 0 ? null : new Result(bestIndex + 1, lines.get(bestIndex), bestWords, ties);
    }

    public static int countWords(String line) {
        String trimmed = line.strip();
        return trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
    }
}
