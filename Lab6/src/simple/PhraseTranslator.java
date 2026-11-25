package simple;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhraseTranslator {

    /* слово: латинські літери, можливо з апострофом всередині (don't) */
    private static final Pattern WORD = Pattern.compile("[A-Za-z]+(?:'[A-Za-z]+)?");

    /* англійське слово (малими літерами) -> український переклад */
    private final Map<String, String> dictionary = new HashMap<>();

    public void addPair(String english, String ukrainian) {
        dictionary.put(english.toLowerCase(), ukrainian);
    }

    public int size() {
        return dictionary.size();
    }

    /* Перекладає кожне слово фрази. Слова, яких немає у словнику, лишаються без змін,
        розділові знаки та пропуски зберігаються. */
    public String translate(String phrase) {
        return WORD.matcher(phrase)
                .replaceAll(match -> Matcher.quoteReplacement(translateWord(match.group())));
    }

    private String translateWord(String word) {
        String translation = dictionary.get(word.toLowerCase());
        if (translation == null) {
            return word;
        }
        /* слово з великої літери - переклад теж з великої */
        if (Character.isUpperCase(word.charAt(0))) {
            return Character.toUpperCase(translation.charAt(0)) + translation.substring(1);
        }
        return translation;
    }
}
