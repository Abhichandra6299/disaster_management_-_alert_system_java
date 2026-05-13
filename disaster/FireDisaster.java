package disaster;

import city.Zone;

/**
 * Fire disaster — spreads rapidly and requires immediate firefighting response.
 */
public class FireDisaster extends Disaster {

    public FireDisaster(int severity) {
        super("Fire", severity);
    }

    @Override
    public String applyTo(Zone zone) {
        zone.setState(Zone.DisasterState.FIRE);

        int patients = estimateCasualties() - 2; // slightly fewer direct casualties than quake
        patients = Math.max(1, patients);
        int admitted = zone.getHospital().admitPatients(patients);
        int overflow = patients - admitted;

        String base = "⚠ FIRE in " + zone.getName()
                + " | Severity: " + severity
                + " | Fire spreading through structures."
                + " | " + admitted + " burn victims admitted to " + zone.getHospital().getName();

        if (severity >= 6) {
            base += " | WARNING: Fire spreading to adjacent blocks — uncontrolled blaze!";
        }
        if (overflow > 0) {
            base += " | CRITICAL: " + overflow + " victims cannot be treated — critical overflow!";
        }
        return base;
    }

    @Override
    public String recover(Zone zone) {
        int discharged = Math.min(severity * 2, zone.getHospital().getCurrentPatients());
        zone.getHospital().dischargePatients(discharged);
        zone.clearRescueUnits();
        zone.setState(Zone.DisasterState.RECOVERING);
        return "✔ Fire contained in " + zone.getName()
                + " | " + discharged + " patients discharged. Damage assessment underway.";
    }

    @Override
    public Zone.DisasterState getDisasterState() {
        return Zone.DisasterState.FIRE;
    }
}
