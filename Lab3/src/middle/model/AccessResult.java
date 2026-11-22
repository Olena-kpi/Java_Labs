package middle.model;

public enum AccessResult {

    ALLOWED("прохід дозволено"),
    UNREADABLE("дані з картки не вдалося зчитати"),
    BLOCKED("картку заблоковано через порушення правил підйому"),
    EXPIRED("термін дії картки закінчився"),
    NOT_VALID_NOW("картка не діє в цей день або час"),
    NO_RIDES("на картці не залишилось підйомів");

    private final String description;

    AccessResult(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAllowed() {
        return this == ALLOWED;
    }
}
