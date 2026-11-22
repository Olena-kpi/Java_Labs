package middle.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* Система з реєстром карток: випускає й блокує їх. Турнікет питає тут, чи картку заблоковано. */
public class SkiPassRegistry {

    /* Скільки місяців діють абонемент на сезон і картки з обліком підйомів. */
    private static final int SEASON_MONTHS = 5;

    private final Map<String, SkiPass> passes = new LinkedHashMap<>();
    private final Set<String> blockedIds = new HashSet<>();

    public SkiPass issue(PassType type, LocalDate firstDay) {
        String id = String.format("SP-%04d", passes.size() + 1);
        /* Картка діє лише у свої дні, тому перший день - найближчий підходящий. */
        while (!type.getDays().matches(firstDay)) {
            firstDay = firstDay.plusDays(1);
        }
        LocalDate lastDay;
        if (type.isSeason() || type.isCounted()) {
            lastDay = firstDay.plusMonths(SEASON_MONTHS);
        } else {
            lastDay = lastValidDay(type, firstDay);
        }
        SkiPass pass = new SkiPass(id, type, firstDay, lastDay);
        passes.put(id, pass);
        return pass;
    }

    /* Блокує картку. Повертає false, якщо такої картки немає в реєстрі. */
    public boolean block(String id) {
        if (!passes.containsKey(normalize(id))) {
            return false;
        }
        blockedIds.add(normalize(id));
        return true;
    }

    public boolean isBlocked(String id) {
        return blockedIds.contains(normalize(id));
    }

    /* Шукає картку за ідентифікатором; якщо її немає, повертає null. */
    public SkiPass find(String id) {
        return passes.get(normalize(id));
    }

    public List<SkiPass> getAll() {
        return new ArrayList<>(passes.values());
    }

    /* Останній день дії картки на кілька днів. Рахуються лише дні потрібного виду. */
    private LocalDate lastValidDay(PassType type, LocalDate firstDay) {
        LocalDate day = firstDay;
        int counted = 0;
        while (true) {
            if (type.getDays().matches(day)) {
                counted++;
                if (counted == type.getDaysCount()) {
                    return day;
                }
            }
            day = day.plusDays(1);
        }
    }

    private String normalize(String id) {
        return id.trim().toUpperCase();
    }
}
