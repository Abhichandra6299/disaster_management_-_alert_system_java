package gui;

import simulation.SimulationManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A simplified modern dashboard panel inspired by the provided screenshot.
 * Uses the existing `CityMapPanel` and `AlertPanel` and adds a top search
 * bar, a large title area and a small set of metric cards on the right.
 */
public class ModernDashboardPanel extends JPanel {

    private static final Color BG_MAIN = new Color(10, 15, 30);
    private static final Color FG_TITLE = new Color(226, 232, 240);
    private static final Color CARD_BG = new Color(18, 28, 46);
    private static final Color CARD_BORDER = new Color(51, 65, 85);

    public ModernDashboardPanel(CityMapPanel cityMap, AlertPanel alertPanel, SimulationManager simManager) {
        setLayout(new BorderLayout(12, 12));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top: title + search
        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setBackground(BG_MAIN);

        JLabel title = new JLabel("Live Weather Intelligence");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(FG_TITLE);

        JTextField search = new JTextField("Live location: Lahore, Pakistan");
        search.setPreferredSize(new Dimension(380, 34));
        search.setBackground(new Color(30, 41, 59));
        search.setForeground(new Color(203, 213, 225));
        search.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        top.add(title, BorderLayout.WEST);
        top.add(search, BorderLayout.EAST);

        // Center split: left map, right alerts + metrics
        JPanel center = new JPanel(new BorderLayout(12, 0));
        center.setBackground(BG_MAIN);

        // Left: map wrapped
        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setBackground(BG_MAIN);
        leftWrapper.add(cityMap, BorderLayout.CENTER);
        leftWrapper.add(CityMapPanel.createLegend(), BorderLayout.SOUTH);

        // Right: alerts panel on top, metrics below
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setBackground(BG_MAIN);

        alertPanel.setPreferredSize(new Dimension(420, 320));
        right.add(alertPanel, BorderLayout.NORTH);

        JPanel metrics = new JPanel(new GridLayout(3, 1, 8, 8));
        metrics.setBackground(BG_MAIN);

        long activeAlerts = simManager.getZones().stream().filter(z -> z.isInDisaster()).count();
        metrics.add(metricCard("Active Alerts", String.valueOf(activeAlerts), "", new Color(239, 68, 68)));
        metrics.add(metricCard("Prediction Certainty", "92", "%", new Color(59, 130, 246)));
        metrics.add(metricCard("Air Quality Index", "72", "", new Color(34, 197, 94)));

        right.add(metrics, BorderLayout.CENTER);

        center.add(leftWrapper, BorderLayout.CENTER);
        center.add(right, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private JPanel metricCard(String title, String value, String suffix, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER), new EmptyBorder(10, 12, 10, 12)));

        JLabel t = new JLabel(title);
        t.setForeground(new Color(148, 163, 184));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel v = new JLabel(value + (suffix.isEmpty() ? "" : suffix));
        v.setForeground(accent);
        v.setFont(new Font("Segoe UI", Font.BOLD, 18));

        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);
        return card;
    }
}
