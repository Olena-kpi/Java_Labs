package simple.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class BookCatalog {

    private final Book[] books;

    public BookCatalog(Book[] books) {
        this.books = books;
    }

    public Book[] getBooks() {
        return books;
    }

    public Book[] findByAuthor(String author) {
        List<Book> found = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthor().equals(author)) {
                found.add(book);
            }
        }
        return found.toArray(new Book[0]);
    }

    public Book[] findByPublisher(String publisher) {
        List<Book> found = new ArrayList<>();
        for (Book book : books) {
            if (book.getPublisher().equals(publisher)) {
                found.add(book);
            }
        }
        return found.toArray(new Book[0]);
    }

    public Book[] findPublishedAfter(int year) {
        List<Book> found = new ArrayList<>();
        for (Book book : books) {
            if (book.getYear() > year) {
                found.add(book);
            }
        }
        return found.toArray(new Book[0]);
    }

    public void sort(Comparator<Book> comparator) {
        Arrays.sort(books, comparator);
    }
}
