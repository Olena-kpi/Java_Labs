package simple.view;

import simple.model.Book;

import java.util.Scanner;

public class ConsoleView {

    private final Scanner scanner = new Scanner(System.in);

    public void showMenu() {
        System.out.println();
        System.out.println("--- МЕНЮ ---");
        System.out.println("1. Книги вибраного автора");
        System.out.println("2. Книги вибраного видавництва");
        System.out.println("3. Книги, видані пізніше вибраного року");
        System.out.println("4. Відсортувати книги за видавництвами");
        System.out.println("5. Показати всі книги");
        System.out.println("0. Вихід");
    }

    public String readChoice() {
        System.out.print("Ваш вибір: ");
        return scanner.nextLine().trim();
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showBooks(String title, Book[] books) {
        System.out.println();
        System.out.println(title + " (" + books.length + "):");
        System.out.printf("%-3s%-22s %-21s %-19s %4s %5s %8s%n",
                "№", "Назва", "Автор", "Видавництво", "Рік", "Стор.", "Ціна");
        for (int i = 0; i < books.length; i++) {
            Book b = books[i];
            System.out.printf("%-3d%-22s %-21s %-19s %4d %5d %8.2f%n",
                    i + 1, b.getTitle(), b.getAuthor(), b.getPublisher(),
                    b.getYear(), b.getPages(), b.getPrice());
        }
    }
}
