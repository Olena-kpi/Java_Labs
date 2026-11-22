package simple;

import simple.controller.BookController;
import simple.model.Book;
import simple.model.BookCatalog;
import simple.view.ConsoleView;

public class Main {

    public static void main(String[] args) {
        Book[] books = {
            new Book("Кобзар", "Тарас Шевченко", "Дніпро", 2015, 704, 450.00),
            new Book("Захар Беркут", "Іван Франко", "Ранок", 2018, 288, 180.00),
            new Book("Лісова пісня", "Леся Українка", "Фоліо", 2012, 160, 120.50),
            new Book("Маруся Чурай", "Ліна Костенко", "А-ба-ба-га-ла-ма-га", 2016, 224, 260.00),
            new Book("Тіні забутих предків", "Михайло Коцюбинський", "Дніпро", 2010, 192, 140.00),
            new Book("Собор", "Олесь Гончар", "Дніпро", 2009, 416, 210.00),
            new Book("Палімпсест", "Василь Стус", "Фоліо", 2019, 320, 330.00),
            new Book("Рекреації", "Юрій Андрухович", "Критика", 2008, 192, 190.00),
            new Book("Інтернат", "Сергій Жадан", "Фабула", 2017, 320, 275.00),
            new Book("Каменярі", "Іван Франко", "Каменяр", 2011, 96, 90.00),
            new Book("Гайдамаки", "Тарас Шевченко", "Ранок", 2020, 208, 175.00),
            new Book("Intermezzo", "Михайло Коцюбинський", "Фоліо", 2014, 128, 110.00)
        };

        BookCatalog catalog = new BookCatalog(books);
        new BookController(catalog, new ConsoleView()).run();
    }
}
