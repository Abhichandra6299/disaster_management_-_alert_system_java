package disaster;

import city.Zone;

/**
 * Abstract base class for all disaster types.
 * Each subclass defines its specific impact behavior.
 */
public abstract class Disaster {

    protected final String disasterName;
    protected final int severity; // 1 (mild) to 10 (catastrophic)

    public Disaster(String disasterName, int severity) {
        this.disasterName = disasterName;
        this.severity = Math.max(1, Math.min(10, severity));
    }

    /**
     * Apply disaster effects to the given zone.
     * Subclasses implement the specifics (patients, state, etc.).
     *
     * @param zone the city zone being hit
     * @return a descriptive event message
     */
    public abstract String applyTo(Zone zone);

    /**
     * Describe a recovery action for this disaster type.
     *
     * @param zone the recovering zone
     * @return a descriptive recovery message
     */
    public abstract String recover(Zone zone);

    /**
     * The zone state this disaster maps to.
     */
    public abstract Zone.DisasterState getDisasterState();

    // ── Getters ───────────────────────────────────────────────────────────────

    public String getDisasterName() {
        return disasterName;
    }

    public int getSeverity() {
        return severity;
    }

    /**
     * Compute casualty estimate based on severity.
     */
    protected int estimateCasualties() {
        return severity * 3;
    }

    @Override
    public String toString() {
        return disasterName + " (severity " + severity + ")";
    }
}
