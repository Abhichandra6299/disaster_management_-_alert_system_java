package city;

/**
 * Represents a city zone on the map.
 * Each zone has a name, a hospital, and a current disaster state.
 */
public class Zone {

    /** Possible states a zone can be in. */
    public enum DisasterState {
        SAFE,       // No active disaster
        FLOODED,    // Flood in progress
        EARTHQUAKE, // Earthquake in progress
        FIRE,       // Fire in progress
        RECOVERING  // Disaster ended, zone recovering
    }

    private final String name;
    private final Hospital hospital;
    private DisasterState state;
    private int rescueUnitsAssigned;
    private int ticksInCurrentState; // how many simulation ticks in current state

    public Zone(String name, int hospitalCapacity) {
        this.name = name;
        this.hospital = new Hospital(name + " Hospital", hospitalCapacity);
        this.state = DisasterState.SAFE;
        this.rescueUnitsAssigned = 0;
        this.ticksInCurrentState = 0;
    }

    // ── State Management ──────────────────────────────────────────────────────

    public void setState(DisasterState newState) {
        this.state = newState;
        this.ticksInCurrentState = 0;
    }

    public DisasterState getState() {
        return state;
    }

    public boolean isInDisaster() {
        return state == DisasterState.FLOODED
                || state == DisasterState.EARTHQUAKE
                || state == DisasterState.FIRE;
    }

    public void incrementStateTick() {
        ticksInCurrentState++;
    }

    public int getTicksInCurrentState() {
        return ticksInCurrentState;
    }

    // ── Rescue Units ──────────────────────────────────────────────────────────

    public void assignRescueUnit() {
        rescueUnitsAssigned++;
    }

    public void clearRescueUnits() {
        rescueUnitsAssigned = 0;
    }

    public int getRescueUnitsAssigned() {
        return rescueUnitsAssigned;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String getName() {
        return name;
    }

    public Hospital getHospital() {
        return hospital;
    }

    /** Reset zone to its initial safe state. */
    public void reset() {
        state = DisasterState.SAFE;
        rescueUnitsAssigned = 0;
        ticksInCurrentState = 0;
        hospital.reset();
    }

    @Override
    public String toString() {
        return name + " [" + state + "] | " + hospital;
    }
}
