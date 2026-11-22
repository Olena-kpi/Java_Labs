package middle.model;

import java.time.DayOfWeek;
import java.time.LocalDate;

/* Види ski-pass: дні дії, кількість днів, підйомів і години дії (0-24 - без обмеження). */
public enum PassType {

    WORKDAY_HALF_DAY_MORNING("Будні: півдня (9:00-13:00)", Days.WORKDAYS, 1, 0, 9, 13),
    WORKDAY_HALF_DAY_AFTERNOON("Будні: півдня (13:00-17:00)", Days.WORKDAYS, 1, 0, 13, 17),
    WORKDAY_ONE_DAY("Будні: 1 день", Days.WORKDAYS, 1, 0, 0, 24),
    WORKDAY_TWO_DAYS("Будні: 2 дні", Days.WORKDAYS, 2, 0, 0, 24),
    WORKDAY_FIVE_DAYS("Будні: 5 днів", Days.WORKDAYS, 5, 0, 0, 24),
    WORKDAY_RIDES_10("Будні: 10 підйомів", Days.WORKDAYS, 0, 10, 0, 24),
    WORKDAY_RIDES_20("Будні: 20 підйомів", Days.WORKDAYS, 0, 20, 0, 24),
    WORKDAY_RIDES_50("Будні: 50 підйомів", Days.WORKDAYS, 0, 50, 0, 24),
    WORKDAY_RIDES_100("Будні: 100 підйомів", Days.WORKDAYS, 0, 100, 0, 24),

    WEEKEND_HALF_DAY_MORNING("Вихідні: півдня (9:00-13:00)", Days.WEEKENDS, 1, 0, 9, 13),
    WEEKEND_HALF_DAY_AFTERNOON("Вихідні: півдня (13:00-17:00)", Days.WEEKENDS, 1, 0, 13, 17),
    WEEKEND_ONE_DAY("Вихідні: 1 день", Days.WEEKENDS, 1, 0, 0, 24),
    WEEKEND_TWO_DAYS("Вихідні: 2 дні", Days.WEEKENDS, 2, 0, 0, 24),
    WEEKEND_RIDES_10("Вихідні: 10 підйомів", Days.WEEKENDS, 0, 10, 0, 24),
    WEEKEND_RIDES_20("Вихідні: 20 підйомів", Days.WEEKENDS, 0, 20, 0, 24),
    WEEKEND_RIDES_50("Вихідні: 50 підйомів", Days.WEEKENDS, 0, 50, 0, 24),
    WEEKEND_RIDES_100("Вихідні: 100 підйомів", Days.WEEKENDS, 0, 100, 0, 24),

    SEASON("Абонемент на сезон", Days.ALL, 0, 0, 0, 24);

    public enum Days {
        WORKDAYS, WEEKENDS, ALL;

        public boolean matches(LocalDate date) {
            DayOfWeek day = date.getDayOfWeek();
            boolean weekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
            if (this == WORKDAYS) {
                return !weekend;
            }
            if (this == WEEKENDS) {
                return weekend;
            }
            return true;
        }
    }

    private final String title;
    private final Days days;
    private final int daysCount;
    private final int rides;
    private final int fromHour;
    private final int toHour;

    PassType(String title, Days days, int daysCount, int rides, int fromHour, int toHour) {
        this.title = title;
        this.days = days;
        this.daysCount = daysCount;
        this.rides = rides;
        this.fromHour = fromHour;
        this.toHour = toHour;
    }

    public String getTitle() {
        return title;
    }

    public Days getDays() {
        return days;
    }

    /* На скільки днів картка (0 для карток з обліком підйомів і абонемента). */
    public int getDaysCount() {
        return daysCount;
    }

    public int getRides() {
        return rides;
    }

    public int getFromHour() {
        return fromHour;
    }

    /* Година, з якої картка вже не діє (13 - о 13:00 уже не можна). */
    public int getToHour() {
        return toHour;
    }

    public boolean isCounted() {
        return rides > 0;
    }

    public boolean isSeason() {
        return this == SEASON;
    }
}
