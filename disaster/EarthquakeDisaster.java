package disaster;

import city.Zone;

/**
 * Earthquake disaster — causes structural damage and high casualty counts.
 */
public class EarthquakeDisaster extends Disaster {

    public EarthquakeDisaster(int severity) {
        super("Earthquake", severity);
    }

    @Override
    public String applyTo(Zone zone) {
        zone.setState(Zone.DisasterState.EARTHQUAKE);

        int patients = estimateCasualties() + severity; // earthquakes cause more injuries
        int admitted = zone.getHospital().admitPatients(patients);
        int overflow = patients - admitted;

        String base = "⚠ EARTHQUAKE in " + zone.getName()
                + " | Severity: " + severity
                + " | Structural collapse reported."
                + " | " + admitted + " injured admitted to " + zone.getHospital().getName();

        if (severity >= 7) {
            base += " | CRITICAL: High-magnitude quake — buildings collapsing!";
        }
        if (overflow > 0) {
            base += " | CRITICAL: " + overflow + " patients cannot be admitted — mass casualty event!";
        }
        return base;
    }

    @Override
    public String recover(Zone zone) {
        int discharged = Math.min(severity * 2, zone.getHospital().getCurrentPatients());
        zone.getHospital().dischargePatients(discharged);
        zone.clearRescueUnits();
        zone.setState(Zone.DisasterState.RECOVERING);
        return "✔ Earthquake rescue complete in " + zone.getName()
                + " | " + discharged + " patients discharged. Structural assessment ongoing.";
    }

    @Override
    public Zone.DisasterState getDisasterState() {
        return Zone.DisasterState.EARTHQUAKE;
    }
}
