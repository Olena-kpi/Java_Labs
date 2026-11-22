package middle.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SkiPass {

    private final String id;
    private final PassType type;
    private final LocalDate validFrom;
    private final LocalDate validTo;
    private int ridesLeft; /* -1, якщо підйоми не рахуються */
    private boolean damaged;

    public SkiPass(String id, PassType type, LocalDate validFrom, LocalDate validTo) {
        this.id = id;
        this.type = type;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.ridesLeft = type.isCounted() ? type.getRides() : -1;
    }

    public String getId() {
        return id;
    }

    public PassType getType() {
        return type;
    }

    public boolean isDamaged() {
        return damaged;
    }

    public void damage() {
        damaged = true;
    }

    public void takeRide() {
        if (ridesLeft > 0) {
            ridesLeft--;
        }
    }

    /* Перевірка без урахування блокування картки. */
    public AccessResult check(LocalDateTime now) {
        LocalDate day = now.toLocalDate();
        int hour = now.getHour();

        /* Термін закінчився: після останнього дня або після кінця вікна в останній день. */
        if (day.isAfter(validTo) || (day.equals(validTo) && hour >= type.getToHour())) {
            return AccessResult.EXPIRED;
        }
        /* Картка ще не діє, не той день (будні/вихідні) або не ті години. */
        if (day.isBefore(validFrom) || !type.getDays().matches(day)
                || hour < type.getFromHour() || hour >= type.getToHour()) {
            return AccessResult.NOT_VALID_NOW;
        }
        if (type.isCounted() && ridesLeft <= 0) {
            return AccessResult.NO_RIDES;
        }
        return AccessResult.ALLOWED;
    }

    public String describe() {
        String rides = type.isCounted() ? ", підйомів залишилось: " + ridesLeft : "";
        return id + " | " + type.getTitle() + " | діє " + validFrom + " - " + validTo + rides;
    }
}
