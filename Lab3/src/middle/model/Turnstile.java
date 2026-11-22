package middle.model;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

/* Турнікет перевіряє картку та веде облік дозволів і відмов (сумарно та за типами). */
public class Turnstile {

    private final SkiPassRegistry registry;

    private int allowed = 0;
    private int denied = 0;
    private int unreadable = 0; /* відмови через збій зчитування (тип невідомий) */
    private final Map<PassType, Integer> allowedByType = new EnumMap<>(PassType.class);
    private final Map<PassType, Integer> deniedByType = new EnumMap<>(PassType.class);

    public Turnstile(SkiPassRegistry registry) {
        this.registry = registry;
    }

    public AccessResult enter(SkiPass pass, LocalDateTime now) {
        if (pass.isDamaged()) {
            denied++;
            unreadable++;
            return AccessResult.UNREADABLE;
        }

        AccessResult result;
        if (registry.isBlocked(pass.getId())) {
            result = AccessResult.BLOCKED;
        } else {
            result = pass.check(now);
        }

        if (result.isAllowed()) {
            pass.takeRide();
            allowed++;
            allowedByType.merge(pass.getType(), 1, Integer::sum);
        } else {
            denied++;
            deniedByType.merge(pass.getType(), 1, Integer::sum);
        }
        return result;
    }

    public int getAllowed() {
        return allowed;
    }

    public int getDenied() {
        return denied;
    }

    public int getUnreadable() {
        return unreadable;
    }

    public Map<PassType, Integer> getAllowedByType() {
        return allowedByType;
    }

    public Map<PassType, Integer> getDeniedByType() {
        return deniedByType;
    }
}
