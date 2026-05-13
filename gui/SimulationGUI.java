package gui;

import alert.AlertLevel;
import alert.AlertSystem;
import simulation.SimulationManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * SimulationGUI — the main JFrame dashboard.
 *
 * Layout:
 * ┌─────────────────────────────────────────────────┐
 * │ Header: title + tick counter + controls │
 * ├────────────────────┬────────────────────────────┤
 * │ CityMapPanel │ AlertPanel │
 * │ (zone grid) │ (banner + history) │
 * ├────────────────────┴────────────────────────────┤
 * │ LogPanel (full-width scrollable event log) │
 * └─────────────────────────────────────────────────┘
 */
public class SimulationGUI extends JFrame {

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final Color BG_MAIN = new Color(10, 15, 30);
    private static final Color BG_HEADER = new Color(15, 23, 42);
    private static final Color FG_TITLE = new Color(226, 232, 240);
    private static final Color FG_TICK = new Color(148, 163, 184);
    private static final Color BTN_START = new Color(34, 197, 94);
    private static final Color BTN_STOP = new Color(239, 68, 68);
    private static final Color BTN_RESET = new Color(100, 116, 139);

    // ── Core objects ──────────────────────────────────────────────────────────
    private final AlertSystem alertSystem;
    private final SimulationManager simManager;

    // ── GUI components ────────────────────────────────────────────────────────
    private final CityMapPanel cityMapPanel;
    private final LogPanel logPanel;
    private final AlertPanel alertPanel;
    private final JLabel tickLabel;
    private final JLabel rescueLabel;
    private final JButton btnStart;
    private final JButton btnStop;
    private final JButton btnReset;

    // ── Simulation timer ──────────────────────────────────────────────────────
    private Timer simTimer;
    private static final int TICK_INTERVAL_MS = 2000; // 2 seconds per tick

    public SimulationGUI() {
        super("🏙 Disaster Management Simulation Dashboard");

        // ── Build core objects ────────────────────────────────────────────────
        alertSystem = new AlertSystem();
        simManager = new SimulationManager(alertSystem);

        // ── Build panels ──────────────────────────────────────────────────────
        cityMapPanel = new CityMapPanel(simManager.getZones());
        logPanel = new LogPanel();
        alertPanel = new AlertPanel();

        // ── Header widgets (need refs before wireUp) ──────────────────────────
        tickLabel = new JLabel("Tick: 0");
        rescueLabel = new JLabel("🚒 Rescue Units: 5 / 5");
        btnStart = createButton("▶  Start", BTN_START);
        btnStop = createButton("⏸  Pause", BTN_STOP);
        btnReset = createButton("↺  Reset", BTN_RESET);

        // ── Wire simulation listener ──────────────────────────────────────────
        simManager.addListener((message, level) -> SwingUtilities.invokeLater(() -> {
            logPanel.appendLog(message, level);
            cityMapPanel.refresh();
            tickLabel.setText("Tick: " + simManager.getTick());
            rescueLabel.setText("🚒 Rescue: "
                    + simManager.getAvailableRescueUnits()
                    + " / " + simManager.getTotalRescueUnits());
        }));

        // Wire alert system → AlertPanel
        alertSystem.addListener(
                (msg, level, ts) -> SwingUtilities.invokeLater(() -> alertPanel.postAlert(msg, level, ts)));

        // ── Simulation timer ──────────────────────────────────────────────────
        simTimer = new Timer(TICK_INTERVAL_MS, (ActionEvent e) -> simManager.tick());

        // ── Assemble frame ────────────────────────────────────────────────────
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setPreferredSize(new Dimension(1280, 780));
        getContentPane().setBackground(BG_MAIN);
        getContentPane().setLayout(new BorderLayout(0, 0));

        getContentPane().add(buildHeader(), BorderLayout.NORTH);
        getContentPane().add(buildCenter(), BorderLayout.CENTER);
        getContentPane().add(buildLogArea(), BorderLayout.SOUTH);

        // ── Button actions ────────────────────────────────────────────────────
        btnStart.addActionListener(e -> startSim());
        btnStop.addActionListener(e -> stopSim());
        btnReset.addActionListener(e -> resetSim());

        updateButtonStates(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ── Layout builders ───────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(BG_HEADER);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Left: title
        JLabel title = new JLabel("🌐  Disaster Management Simulation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(FG_TITLE);

        // Center: tick + rescue counters
        JPanel counters = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        counters.setOpaque(false);

        tickLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tickLabel.setForeground(FG_TICK);

        rescueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rescueLabel.setForeground(FG_TICK);

        counters.add(tickLabel);
        counters.add(new JSeparator(SwingConstants.VERTICAL));
        counters.add(rescueLabel);

        // Right: control buttons
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);
        controls.add(btnStart);
        controls.add(btnStop);
        controls.add(btnReset);

        header.add(title, BorderLayout.WEST);
        header.add(counters, BorderLayout.CENTER);
        header.add(controls, BorderLayout.EAST);

        // Bottom separator line
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(51, 65, 85));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_HEADER);
        wrapper.add(header, BorderLayout.CENTER);
        wrapper.add(sep, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildCenter() {
        // Replace the previous grid layout with a modern dashboard panel
        return new ModernDashboardPanel(cityMapPanel, alertPanel, simManager);
    }

    private JPanel buildLogArea() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_MAIN);
        wrapper.setBorder(new EmptyBorder(5, 10, 10, 10));
        logPanel.setPreferredSize(new Dimension(0, 220));
        wrapper.add(logPanel, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Simulation controls ───────────────────────────────────────────────────

    private void startSim() {
        simManager.start();
        simTimer.start();
        updateButtonStates(true);
    }

    private void stopSim() {
        simTimer.stop();
        simManager.stop();
        updateButtonStates(false);
    }

    private void resetSim() {
        simTimer.stop();
        simManager.reset();
        logPanel.clear();
        alertPanel.clear();
        cityMapPanel.refresh();
        tickLabel.setText("Tick: 0");
        rescueLabel.setText("🚒 Rescue Units: 5 / 5");
        updateButtonStates(false);
    }

    private void updateButtonStates(boolean running) {
        btnStart.setEnabled(!running);
        btnStop.setEnabled(running);
    }

    // ── Button factory ────────────────────────────────────────────────────────

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 32));
        btn.setOpaque(true);

        // Hover effect
        Color darker = bg.darker();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.isEnabled())
                    btn.setBackground(darker);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }
}
