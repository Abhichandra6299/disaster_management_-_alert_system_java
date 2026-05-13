package simulation;

import alert.AlertLevel;

/**
 * Listener interface for receiving simulation events.
 * Implement this to be notified when something happens in the simulation.
 */
public interface SimulationListener {

    /**
     * Called when a notable event occurs in the simulation.
     *
     * @param message   Human-readable description of the event
     * @param level     The alert level / severity of the event
     */
    void onEvent(String message, AlertLevel level);
}
