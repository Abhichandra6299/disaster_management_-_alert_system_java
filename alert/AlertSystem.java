package alert;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * AlertSystem classifies incoming events into alert levels
 * and dispatches them to registered listeners.
 */
public class AlertSystem {

    public interface AlertListener {
        void onAlert(String message, AlertLevel level, String timestamp);
    }

    private final List<AlertListener> listeners = new ArrayList<>();
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    public void addListener(AlertListener listener) {
        listeners.add(listener);
    }

    /**
     * Classifies a raw message into an alert level, then notifies listeners.
     */
    public void dispatch(String message, AlertLevel level) {
        String timestamp = LocalTime.now().format(TIME_FMT);
        for (AlertListener l : listeners) {
            l.onAlert(message, level, timestamp);
        }
    }

    /**
     * Classify an event message automatically based on keywords.
     */
    public AlertLevel classify(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("critical") || lower.contains("collapse")
                || lower.contains("mass casualty") || lower.contains("uncontrolled")) {
            return AlertLevel.CRITICAL;
        } else if (lower.contains("warning") || lower.contains("spreading")
                || lower.contains("rising") || lower.contains("responding")
                || lower.contains("injured")) {
            return AlertLevel.WARNING;
        } else {
            return AlertLevel.INFO;
        }
    }

    /**
     * Convenience: classify and dispatch in one call.
     */
    public void classifyAndDispatch(String message) {
        dispatch(message, classify(message));
    }
}
