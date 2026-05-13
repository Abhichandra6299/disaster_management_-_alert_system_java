package city;

/**
 * Hospital manages patient capacity within a city zone.
 */
public class Hospital {

    private final String name;
    private final int capacity;
    private int currentPatients;

    public Hospital(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.currentPatients = 0;
    }

    /**
     * Attempt to admit a number of patients.
     * @return the number actually admitted (may be less if capacity is exceeded).
     */
    public int admitPatients(int count) {
        int available = capacity - currentPatients;
        int admitted = Math.min(count, available);
        currentPatients += admitted;
        return admitted;
    }

    /**
     * Discharge patients after a disaster is resolved.
     */
    public void dischargePatients(int count) {
        currentPatients = Math.max(0, currentPatients - count);
    }

    public boolean isFull() {
        return currentPatients >= capacity;
    }

    public int getAvailableBeds() {
        return capacity - currentPatients;
    }

    public int getCurrentPatients() {
        return currentPatients;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getName() {
        return name;
    }

    /** Reset hospital state (e.g. on simulation reset). */
    public void reset() {
        currentPatients = 0;
    }

    @Override
    public String toString() {
        return name + " [" + currentPatients + "/" + capacity + "]";
    }
}
