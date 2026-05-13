package exception;

/**
 * Custom exception thrown when a simulation error occurs.
 */
public class SimulationException extends RuntimeException {

    private final String context;

    public SimulationException(String message) {
        super(message);
        this.context = "General";
    }

    public SimulationException(String message, String context) {
        super(message);
        this.context = context;
    }

    public SimulationException(String message, Throwable cause) {
        super(message, cause);
        this.context = "General";
    }

    public String getContext() {
        return context;
    }

    @Override
    public String toString() {
        return "[SimulationException | " + context + "] " + getMessage();
    }
}
