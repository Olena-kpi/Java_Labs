package middle.controller;

import middle.model.AccessResult;
import middle.model.PassType;
import middle.model.SkiPass;
import middle.model.SkiPassRegistry;
import middle.model.Turnstile;
import middle.view.ConsoleView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

public class SkiController {

    private final SkiPassRegistry registry;
    private final Turnstile turnstile;
    private final ConsoleView view;
    private LocalDateTime now; /* поточний час (його можна змінювати з меню) */

    public SkiController(SkiPassRegistry registry, Turnstile turnstile, ConsoleView view) {
        this.registry = registry;
        this.turnstile = turnstile;
        this.view = view;
        this.now = LocalDateTime.now().withSecond(0).withNano(0);
    }

    public void run() {
        try {
            boolean running = true;
            while (running) {
                view.showMenu(now);
                running = handle(view.askText("Ваш вибір"));
            }
        } catch (NoSuchElementException e) {
            view.showMessage("Введення завершено.");
        }
    }

    /* Повертає false, якщо треба вийти. */
    private boolean handle(String choice) {
        switch (choice) {
            case "1":
                issuePass();
                break;
            case "2":
                blockPass();
                break;
            case "3":
                passThroughTurnstile();
                break;
            case "4":
                damagePass();
                break;
            case "5":
                view.showPasses(registry.getAll());
                break;
            case "6":
                view.showTotal(turnstile.getAllowed(), turnstile.getDenied());
                break;
            case "7":
                view.showByType(turnstile.getAllowedByType(),
                        turnstile.getDeniedByType(), turnstile.getUnreadable());
                break;
            case "8":
                now = view.askDateTime("Новий час (рррр-мм-дд гг:хх)", now);
                view.showMessage("Поточний час змінено.");
                break;
            case "0":
                view.showMessage("Роботу завершено.");
                return false;
            default:
                view.showMessage("Невірний вибір. Введіть число від 0 до 8.");
        }
        return true;
    }

    private void issuePass() {
        view.showPassTypes();
        PassType[] types = PassType.values();
        int number = view.askNumber("Номер виду ski-pass", 1, types.length);
        LocalDate firstDay = view.askDate("Перший день дії (рррр-мм-дд)", now.toLocalDate());
        SkiPass pass = registry.issue(types[number - 1], firstDay);
        view.showMessage("Картку випущено: " + pass.describe());
    }

    private void blockPass() {
        String id = view.askText("ID картки для блокування");
        if (registry.block(id)) {
            view.showMessage("Картку заблоковано.");
        } else {
            view.showMessage("Картку з ID " + id + " не знайдено в реєстрі.");
        }
    }

    private void passThroughTurnstile() {
        SkiPass pass = findPass("ID картки");
        if (pass == null) {
            return;
        }
        AccessResult result = turnstile.enter(pass, now);
        if (result.isAllowed()) {
            view.showMessage("ПРОХІД ДОЗВОЛЕНО. " + pass.describe());
        } else {
            view.showMessage("ПРОХІД ЗАБОРОНЕНО: " + result.getDescription() + ".");
        }
    }

    private void damagePass() {
        SkiPass pass = findPass("ID картки для пошкодження");
        if (pass != null) {
            pass.damage();
            view.showMessage("Картку " + pass.getId() + " пошкоджено.");
        }
    }

    /* Запитує ID і шукає картку; якщо її немає, повідомляє про це й повертає null. */
    private SkiPass findPass(String prompt) {
        String id = view.askText(prompt);
        SkiPass pass = registry.find(id);
        if (pass == null) {
            view.showMessage("Картку з ID " + id + " не знайдено в реєстрі.");
        }
        return pass;
    }
}
