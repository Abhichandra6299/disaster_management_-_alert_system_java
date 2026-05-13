package alert;

/**
 * Enum representing the severity level of an alert.
 */
public enum AlertLevel {
    INFO("INFO", "Informational event"),
    WARNING("WARNING", "Situation requires attention"),
    CRITICAL("CRITICAL", "Immediate action required");

    private final String label;
    private final String description;

    AlertLevel(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return label;
    }
}
