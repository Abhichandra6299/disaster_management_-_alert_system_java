import gui.SimulationGUI;

import javax.swing.*;

/**
 * Application entry point.
 * Launches the Disaster Management Simulation dashboard on the Swing EDT.
 */
public class Main {

    public static void main(String[] args) {
        // Use system look-and-feel as a base; our custom colors override it.
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to default L&F silently
        }

        SwingUtilities.invokeLater(SimulationGUI::new);
    }
}
