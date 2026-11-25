package task4;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/* Завантажує сторінку й рахує її теги. Парсер викликає методи цього класу для кожного
    тегу, який зустрів; закриваючі теги, коментарі та вміст скриптів не враховуються. */
public class PageTagCounter extends HTMLEditorKit.ParserCallback {

    private TagStatistics statistics;

    public TagStatistics count(String address) throws IOException {
        statistics = new TagStatistics();
        HttpURLConnection connection = connect(address);
        try (Reader page = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
            new ParserDelegator().parse(page, this, true);
        } finally {
            connection.disconnect();
        }
        return statistics;
    }

    private HttpURLConnection connect(String address) throws IOException {
        URI uri = URI.create(address);
        String scheme = uri.getScheme();
        if (scheme == null || !(scheme.equals("http") || scheme.equals("https"))) {
            throw new IllegalArgumentException("потрібна адреса http:// або https://");
        }
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        /* обмеження часу, щоб програма не зависала на недоступному сервері */
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        int code = connection.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            connection.disconnect();
            throw new IOException("сервер повернув код " + code);
        }
        return connection;
    }

    @Override
    public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
        statistics.add(tag.toString());
    }

    /* теги без закриваючої пари, наприклад br або meta */
    @Override
    public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
        statistics.add(tag.toString());
    }
}
