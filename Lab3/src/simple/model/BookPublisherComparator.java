package simple.model;

import java.util.Comparator;

/* Порівнює книги за видавництвом, а якщо вони однакові - за назвою. */
public class BookPublisherComparator implements Comparator<Book> {

    @Override
    public int compare(Book first, Book second) {
        int result = first.getPublisher().compareToIgnoreCase(second.getPublisher());
        if (result != 0) {
            return result;
        }
        return first.getTitle().compareToIgnoreCase(second.getTitle());
    }
}
