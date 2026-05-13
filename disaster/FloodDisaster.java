package disaster;

import city.Zone;

/**
 * Flood disaster — causes rising water levels and patient admissions.
 */
public class FloodDisaster extends Disaster {

    public FloodDisaster(int severity) {
        super("Flood", severity);
    }

    @Override
    public String applyTo(Zone zone) {
        zone.setState(Zone.DisasterState.FLOODED);

        int patients = estimateCasualties();
        int admitted = zone.getHospital().admitPatients(patients);
        int overflow = patients - admitted;

        String base = "⚠ FLOOD in " + zone.getName()
                + " | Severity: " + severity
                + " | Rising water levels detected."
                + " | " + admitted + " patients admitted to " + zone.getHospital().getName();

        if (zone.getHospital().isFull()) {
            base += " | WARNING: Hospital at full capacity!";
        }
        if (overflow > 0) {
            base += " | CRITICAL: " + overflow + " patients turned away — mass casualty risk!";
        }
        return base;
    }

    @Override
    public String recover(Zone zone) {
        int discharged = Math.min(severity * 2, zone.getHospital().getCurrentPatients());
        zone.getHospital().dischargePatients(discharged);
        zone.clearRescueUnits();
        zone.setState(Zone.DisasterState.RECOVERING);
        return "✔ Flood waters receding in " + zone.getName()
                + " | " + discharged + " patients discharged. Zone recovering.";
    }

    @Override
    public Zone.DisasterState getDisasterState() {
        return Zone.DisasterState.FLOODED;
    }
}
