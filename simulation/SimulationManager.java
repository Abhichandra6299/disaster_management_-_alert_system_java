package simulation;

import alert.AlertLevel;
import alert.AlertSystem;
import city.Zone;
import disaster.*;
import exception.SimulationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SimulationManager drives the disaster simulation loop.
 *
 * <p>Each "tick" it may:
 * <ul>
 *   <li>Strike a random zone with a random disaster</li>
 *   <li>Deploy rescue units to disaster zones</li>
 *   <li>Advance recovery for zones that have been in disaster long enough</li>
 * </ul>
 *
 * <p>All events are broadcast to registered {@link SimulationListener}s
 * and also forwarded through the {@link AlertSystem}.
 */
public class SimulationManager {

    // ── Constants ─────────────────────────────────────────────────────────────
    private static final int ZONE_COUNT         = 9;
    private static final int DISASTER_CHANCE    = 40;   // % chance per tick a new disaster strikes
    private static final int RECOVERY_THRESHOLD = 3;    // ticks before a zone starts recovering
    private static final int TOTAL_RESCUE_UNITS = 5;

    // ── State ─────────────────────────────────────────────────────────────────
    private final List<Zone>               zones     = new ArrayList<>();
    private final List<SimulationListener> listeners = new ArrayList<>();
    private final AlertSystem              alertSystem;
    private final Random                   random    = new Random();

    private int  tick            = 0;
    private int  availableRescue = TOTAL_RESCUE_UNITS;
    private boolean running      = false;

    // ── Zone names ────────────────────────────────────────────────────────────
    private static final String[] ZONE_NAMES = {
        "North District", "East District", "South District",
        "West District",  "Central Hub",   "Harbor Zone",
        "Industrial Area","Residential Park","Old Town"
    };

    // ── Hospital capacities per zone ─────────────────────────────────────────
    private static final int[] HOSPITAL_CAPS = {
        30, 25, 35, 20, 50, 25, 15, 40, 20
    };

    // ── Constructor ───────────────────────────────────────────────────────────

    public SimulationManager(AlertSystem alertSystem) {
        this.alertSystem = alertSystem;
        initZones();

        // Forward AlertSystem alerts to SimulationListeners
        alertSystem.addListener((msg, level, timestamp) ->
            notifyListeners("[" + timestamp + "] " + msg, level)
        );
    }

    // ── Initialization ────────────────────────────────────────────────────────

    private void initZones() {
        zones.clear();
        for (int i = 0; i < ZONE_COUNT; i++) {
            zones.add(new Zone(ZONE_NAMES[i], HOSPITAL_CAPS[i]));
        }
    }

    // ── Listener Management ───────────────────────────────────────────────────

    public void addListener(SimulationListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(String message, AlertLevel level) {
        for (SimulationListener l : listeners) {
            l.onEvent(message, level);
        }
    }

    // ── Public Controls ───────────────────────────────────────────────────────

    public void start() {
        running = true;
        notifyListeners("Simulation started. Monitoring " + zones.size() + " city zones.", AlertLevel.INFO);
    }

    public void stop() {
        running = false;
        notifyListeners("Simulation paused.", AlertLevel.INFO);
    }

    public void reset() {
        running = false;
        tick = 0;
        availableRescue = TOTAL_RESCUE_UNITS;
        initZones();
        notifyListeners("Simulation reset. All zones cleared.", AlertLevel.INFO);
    }

    public boolean isRunning() {
        return running;
    }

    // ── Core Tick ─────────────────────────────────────────────────────────────

    /**
     * Advance the simulation by one tick.
     * Called by the GUI timer.
     */
    public void tick() {
        if (!running) return;

        tick++;
        notifyListeners("── Tick " + tick + " ──────────────────────────────", AlertLevel.INFO);

        try {
            advanceRecoveries();
            releaseRecoveredRescueUnits();
            maybeStrikeDisaster();
            deployRescueUnits();
        } catch (SimulationException e) {
            notifyListeners("Simulation error: " + e.getMessage(), AlertLevel.CRITICAL);
        }
    }

    // ── Internal Tick Steps ───────────────────────────────────────────────────

    /** Advance disaster timers; trigger recovery when threshold is met. */
    private void advanceRecoveries() {
        for (Zone zone : zones) {
            if (zone.isInDisaster()) {
                zone.incrementStateTick();
                if (zone.getTicksInCurrentState() >= RECOVERY_THRESHOLD) {
                    triggerRecovery(zone);
                }
            } else if (zone.getState() == Zone.DisasterState.RECOVERING) {
                zone.incrementStateTick();
                if (zone.getTicksInCurrentState() >= 2) {
                    zone.setState(Zone.DisasterState.SAFE);
                    notifyListeners(zone.getName() + " is now fully safe.", AlertLevel.INFO);
                }
            }
        }
    }

    private void triggerRecovery(Zone zone) {
        // Build a temporary disaster object matching zone state just to call recover()
        Disaster d = disasterForState(zone.getState(), 5);
        if (d == null) return;
        String msg = d.recover(zone);
        alertSystem.classifyAndDispatch(msg);
    }

    /** Return rescue units from fully safe zones back to the pool. */
    private void releaseRecoveredRescueUnits() {
        for (Zone zone : zones) {
            if (zone.getState() == Zone.DisasterState.SAFE && zone.getRescueUnitsAssigned() > 0) {
                availableRescue += zone.getRescueUnitsAssigned();
                availableRescue = Math.min(availableRescue, TOTAL_RESCUE_UNITS);
                zone.clearRescueUnits();
            }
        }
    }

    /** Randomly decide whether to strike a disaster and which zone + type. */
    private void maybeStrikeDisaster() {
        if (random.nextInt(100) >= DISASTER_CHANCE) return;

        // Pick a zone that is currently SAFE
        List<Zone> safeZones = zones.stream()
                .filter(z -> z.getState() == Zone.DisasterState.SAFE)
                .collect(java.util.stream.Collectors.toList());

        if (safeZones.isEmpty()) {
            notifyListeners("All zones already under disaster — no new strike this tick.", AlertLevel.WARNING);
            return;
        }

        Zone target   = safeZones.get(random.nextInt(safeZones.size()));
        int severity  = 1 + random.nextInt(10);
        Disaster disaster = randomDisaster(severity);

        String eventMsg = disaster.applyTo(target);
        alertSystem.classifyAndDispatch(eventMsg);
    }

    /** Deploy one rescue unit to each zone currently in disaster (if units available). */
    private void deployRescueUnits() {
        for (Zone zone : zones) {
            if (zone.isInDisaster() && availableRescue > 0) {
                zone.assignRescueUnit();
                availableRescue--;
                String msg = "🚒 Rescue unit responding to " + zone.getName()
                        + " | " + availableRescue + " unit(s) remaining in pool.";
                notifyListeners(msg, AlertLevel.WARNING);
            }
        }
        if (availableRescue == 0) {
            notifyListeners("WARNING: All rescue units are deployed! No reserves available.", AlertLevel.CRITICAL);
        }
    }

    // ── Helper Factories ──────────────────────────────────────────────────────

    private Disaster randomDisaster(int severity) {
        int roll = random.nextInt(3);
        switch (roll) {
            case 0: return new FloodDisaster(severity);
            case 1: return new EarthquakeDisaster(severity);
            default: return new FireDisaster(severity);
        }
    }

    private Disaster disasterForState(Zone.DisasterState state, int severity) {
        switch (state) {
            case FLOODED:    return new FloodDisaster(severity);
            case EARTHQUAKE: return new EarthquakeDisaster(severity);
            case FIRE:       return new FireDisaster(severity);
            default:         return null;
        }
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public List<Zone> getZones() {
        return zones;
    }

    public int getTick() {
        return tick;
    }

    public int getAvailableRescueUnits() {
        return availableRescue;
    }

    public int getTotalRescueUnits() {
        return TOTAL_RESCUE_UNITS;
    }
}
