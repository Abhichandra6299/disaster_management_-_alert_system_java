package gui;

import simulation.SimulationManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern dashboard panel matching the screenshot exactly.
 * Layout:
 * - Top: Search bar, title, user profile
 * - Center: Left (Weather Reports + City Map), Right (Analytics cards +
 * metrics)
 * - Bottom: Live Disaster Alerts (severity cards)
 */
public class ModernDashboardPanel extends JPanel {

    private static final Color BG_MAIN = new Color(10, 15, 30);
    private static final Color BG_CARD = new Color(18, 28, 46);
    private static final Color FG_TITLE = new Color(226, 232, 240);
    private static final Color FG_LABEL = new Color(148, 163, 184);

    public ModernDashboardPanel(CityMapPanel cityMap, AlertPanel alertPanel, SimulationManager simManager) {
        setLayout(new BorderLayout(12, 12));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // ─── TOP: Search + Title + User Profile ───────────────────────────────
        add(buildTopBar(), BorderLayout.NORTH);

        // ─── CENTER: Left (reports) + Right (analytics) ────────────────────────
        JPanel center = buildCenter(cityMap, alertPanel, simManager);
        add(center, BorderLayout.CENTER);

        // ─── BOTTOM: Disaster Alerts ────────────────────────────────────────────
        add(buildDisasterAlerts(simManager), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(16, 0));
        top.setBackground(BG_MAIN);

        // Left: Title
        JLabel title = new JLabel("Live Weather Intelligence");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(FG_TITLE);

        // Center: Location selector (real world cities with coordinates)
        LocationSelector locationSelector = new LocationSelector();

        // Right: User profile
        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        profile.setOpaque(false);

        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel userInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        userInfo.setOpaque(false);
        JLabel userName = new JLabel("Ava Winters");
        userName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userName.setForeground(FG_TITLE);
        JLabel userRole = new JLabel("Meteorologist");
        userRole.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        userRole.setForeground(FG_LABEL);
        userInfo.add(userName);
        userInfo.add(userRole);

        profile.add(userIcon);
        profile.add(userInfo);

        top.add(title, BorderLayout.WEST);
        top.add(locationSelector, BorderLayout.CENTER);
        top.add(profile, BorderLayout.EAST);

        return top;
    }

    private JPanel buildCenter(CityMapPanel cityMap, AlertPanel alertPanel, SimulationManager simManager) {
        JPanel center = new JPanel(new BorderLayout(12, 0));
        center.setBackground(BG_MAIN);

        // ─── LEFT: Weather Reports + City Map ───────────────────────────────────
        JPanel left = new JPanel(new BorderLayout(0, 8));
        left.setBackground(BG_MAIN);

        JLabel reportTitle = new JLabel("  Weather Reports");
        reportTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        reportTitle.setForeground(FG_TITLE);
        reportTitle.setBackground(new Color(30, 41, 59));
        reportTitle.setOpaque(true);
        reportTitle.setBorder(new EmptyBorder(8, 10, 8, 10));

        JPanel forecastCards = new JPanel(new GridLayout(1, 2, 8, 0));
        forecastCards.setBackground(BG_MAIN);
        forecastCards.add(forecastCard("Kyiv", "Ukraine", "❄️", "110"));
        forecastCards.add(forecastCard("Delhi", "India", "☀️", "98"));

        left.add(reportTitle, BorderLayout.NORTH);
        left.add(forecastCards, BorderLayout.CENTER);

        JPanel mapSection = new JPanel(new BorderLayout());
        mapSection.setBackground(BG_MAIN);
        mapSection.add(cityMap, BorderLayout.CENTER);
        mapSection.add(CityMapPanel.createLegend(), BorderLayout.SOUTH);
        left.add(mapSection, BorderLayout.SOUTH);

        // ─── RIGHT: Analytics Cards + Metrics ────────────────────────────────────
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setBackground(BG_MAIN);
        right.setPreferredSize(new Dimension(450, 0));

        // Top: Active alerts + Certainty
        JPanel topMetrics = new JPanel(new GridLayout(1, 2, 8, 0));
        topMetrics.setBackground(BG_MAIN);
        long activeAlerts = simManager.getZones().stream().filter(z -> z.isInDisaster()).count();
        topMetrics.add(metricCard("Active Alerts", String.valueOf(activeAlerts), "", new Color(239, 68, 68)));
        topMetrics.add(metricCard("Prediction Certainty", "92", "%", new Color(59, 130, 246)));
        right.add(topMetrics, BorderLayout.NORTH);

        // Middle: Alert panel
        alertPanel.setPreferredSize(new Dimension(0, 180));
        right.add(alertPanel, BorderLayout.CENTER);

        // Bottom: More metrics
        JPanel botMetrics = new JPanel(new GridLayout(3, 1, 0, 8));
        botMetrics.setBackground(BG_MAIN);
        botMetrics.add(metricCard("Temperature Trend", "↗ +2.5°C", "", new Color(255, 140, 50)));
        botMetrics.add(metricCard("Air Quality Index", "72", "", new Color(34, 197, 94)));
        botMetrics.add(metricCard("Humidity", "68", "%", new Color(100, 180, 255)));
        right.add(botMetrics, BorderLayout.SOUTH);

        center.add(left, BorderLayout.CENTER);
        center.add(right, BorderLayout.EAST);
        return center;
    }

    private JPanel buildDisasterAlerts(SimulationManager simManager) {
        JPanel alertsSection = new JPanel(new BorderLayout(0, 8));
        alertsSection.setBackground(BG_MAIN);

        JLabel title = new JLabel("  Live Disaster Alerts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(FG_TITLE);
        title.setBackground(new Color(30, 41, 59));
        title.setOpaque(true);
        title.setBorder(new EmptyBorder(8, 10, 8, 10));

        // Disaster severity cards
        JPanel alerts = new JPanel(new GridLayout(1, 4, 8, 0));
        alerts.setBackground(BG_MAIN);
        alerts.add(disasterCard("Flood Warning", 88, "Active", new Color(59, 130, 246)));
        alerts.add(disasterCard("Cyclone Alert", 76, "Monitoring", new Color(168, 85, 247)));
        alerts.add(disasterCard("Earthquake Risk", 64, "Prepared", new Color(59, 130, 246)));
        alerts.add(disasterCard("Heavy Rainfall", 94, "Urgent", new Color(239, 68, 68)));

        // Emergency SOS button
        JPanel sosPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        sosPanel.setBackground(BG_MAIN);
        JButton sosBtn = new JButton("EMERGENCY SOS");
        sosBtn.setBackground(new Color(239, 68, 68));
        sosBtn.setForeground(Color.WHITE);
        sosBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sosBtn.setPreferredSize(new Dimension(180, 40));
        sosBtn.setBorderPainted(false);
        sosBtn.setFocusPainted(false);
        sosBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sosPanel.add(sosBtn);

        JPanel alertsWithSos = new JPanel(new BorderLayout());
        alertsWithSos.setBackground(BG_MAIN);
        alertsWithSos.add(alerts, BorderLayout.CENTER);
        alertsWithSos.add(sosPanel, BorderLayout.EAST);

        alertsSection.add(title, BorderLayout.NORTH);
        alertsSection.add(alertsWithSos, BorderLayout.CENTER);

        return alertsSection;
    }

    private JPanel forecastCard(String city, String country, String icon, String temp) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), new EmptyBorder(12, 12, 12, 12)));

        JLabel icn = new JLabel(icon);
        icn.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 4));
        info.setOpaque(false);
        JLabel nm = new JLabel(city);
        nm.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nm.setForeground(FG_TITLE);
        JLabel cty = new JLabel(country);
        cty.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        cty.setForeground(FG_LABEL);
        info.add(nm);
        info.add(cty);

        JLabel tmp = new JLabel(temp + "°");
        tmp.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tmp.setForeground(new Color(255, 140, 50));

        card.add(icn, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        card.add(tmp, BorderLayout.EAST);

        return card;
    }

    private JPanel metricCard(String title, String value, String suffix, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), new EmptyBorder(10, 12, 10, 12)));

        JLabel t = new JLabel(title);
        t.setForeground(FG_LABEL);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JLabel v = new JLabel(value + suffix);
        v.setForeground(accent);
        v.setFont(new Font("Segoe UI", Font.BOLD, 22));

        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);
        return card;
    }

    private JPanel disasterCard(String name, int severity, String status, Color colorBg) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)), new EmptyBorder(12, 12, 12, 12)));

        JLabel nm = new JLabel(name);
        nm.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nm.setForeground(FG_TITLE);

        JLabel svr = new JLabel("Severity: " + severity + "%");
        svr.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        svr.setForeground(colorBg);

        // Simple severity bar
        JPanel bar = new JPanel();
        bar.setBackground(colorBg);
        bar.setPreferredSize(new Dimension(0, 6));

        JLabel sts = new JLabel(status);
        sts.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        sts.setForeground(FG_LABEL);

        JPanel info = new JPanel(new BorderLayout(0, 4));
        info.setOpaque(false);
        info.add(nm, BorderLayout.NORTH);
        info.add(bar, BorderLayout.CENTER);
        JPanel bottomInfo = new JPanel(new BorderLayout());
        bottomInfo.setOpaque(false);
        bottomInfo.add(svr, BorderLayout.WEST);
        bottomInfo.add(sts, BorderLayout.EAST);
        info.add(bottomInfo, BorderLayout.SOUTH);

        card.add(info, BorderLayout.CENTER);
        return card;
    }
}
