package simple.controller;

import simple.model.Book;
import simple.model.BookCatalog;
import simple.model.BookPublisherComparator;
import simple.view.ConsoleView;

import java.util.NoSuchElementException;
import java.util.Random;

public class BookController {

    /* Заздалегідь підготовлені дані для пошуку. Останні значення в кожному
        списку відсутні в наборі книг - так можна побачити "Нічого не знайдено". */
    private static final String[] AUTHORS = {
        "Тарас Шевченко", "Іван Франко", "Леся Українка", "Ліна Костенко",
        "Михайло Коцюбинський", "Олесь Гончар", "Василь Стус",
        "Юрій Андрухович", "Сергій Жадан", "Григорій Сковорода"
    };
    private static final String[] PUBLISHERS = {
        "Дніпро", "Ранок", "Фоліо", "А-ба-ба-га-ла-ма-га", "Критика",
        "Фабула", "Каменяр", "Основи"
    };
    private static final int[] YEARS = {2005, 2010, 2015, 2018, 2020, 2025};

    private final BookCatalog catalog;
    private final ConsoleView view;
    private final Random random = new Random();

    public BookController(BookCatalog catalog, ConsoleView view) {
        this.catalog = catalog;
        this.view = view;
    }

    public void run() {
        view.showBooks("Вихідний набір даних", catalog.getBooks());
        try {
            boolean running = true;
            while (running) {
                view.showMenu();
                running = handle(view.readChoice());
            }
        } catch (NoSuchElementException e) {
            view.showMessage("Введення завершено.");
        }
    }

    /* Повертає false, якщо треба вийти. */
    private boolean handle(String choice) {
        switch (choice) {
            case "1":
                String author = AUTHORS[random.nextInt(AUTHORS.length)];
                view.showMessage("Шукаємо книги автора: " + author);
                showResult(catalog.findByAuthor(author), "Книги автора");
                break;
            case "2":
                String publisher = PUBLISHERS[random.nextInt(PUBLISHERS.length)];
                view.showMessage("Шукаємо книги видавництва: " + publisher);
                showResult(catalog.findByPublisher(publisher), "Книги видавництва");
                break;
            case "3":
                int year = YEARS[random.nextInt(YEARS.length)];
                view.showMessage("Шукаємо книги, видані пізніше " + year + " року");
                showResult(catalog.findPublishedAfter(year), "Книги, видані пізніше");
                break;
            case "4":
                catalog.sort(new BookPublisherComparator());
                view.showBooks("Книги, відсортовані за видавництвами", catalog.getBooks());
                break;
            case "5":
                view.showBooks("Усі книги", catalog.getBooks());
                break;
            case "0":
                view.showMessage("Роботу завершено.");
                return false;
            default:
                view.showMessage("Невірний вибір. Введіть число від 0 до 5.");
        }
        return true;
    }

    private void showResult(Book[] found, String title) {
        if (found.length == 0) {
            view.showMessage("Нічого не знайдено.");
        } else {
            view.showBooks(title, found);
        }
    }
}
