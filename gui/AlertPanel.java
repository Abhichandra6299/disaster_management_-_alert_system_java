package gui;

import alert.AlertLevel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AlertPanel shows:
 *  1. A live "banner" that flashes the latest alert (color-coded by level).
 *  2. A scrollable alert history list below the banner.
 */
public class AlertPanel extends JPanel {

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final Color BG_PANEL        = new Color(15, 23, 42);
    private static final Color BG_HEADER       = new Color(30, 41, 59);
    private static final Color BANNER_INFO     = new Color(30, 64, 130);
    private static final Color BANNER_WARNING  = new Color(92, 70, 10);
    private static final Color BANNER_CRITICAL = new Color(100, 20, 20);
    private static final Color TEXT_INFO       = new Color(147, 197, 253);
    private static final Color TEXT_WARNING    = new Color(253, 224, 71);
    private static final Color TEXT_CRITICAL   = new Color(252, 165, 165);
    private static final Color BG_HISTORY      = new Color(18, 28, 46);

    // ── Banner ────────────────────────────────────────────────────────────────
    private final JLabel  bannerIcon;
    private final JLabel  bannerText;
    private final JPanel  bannerPanel;

    // ── History list ──────────────────────────────────────────────────────────
    private final DefaultListModel<String> historyModel = new DefaultListModel<>();
    private final JList<String>            historyList;

    // ── Flash timer ───────────────────────────────────────────────────────────
    private Timer flashTimer;
    private int   flashCount = 0;
    private Color flashBase  = BANNER_INFO;

    public AlertPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_PANEL);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));

        // ── Section title ─────────────────────────────────────────────────────
        JLabel title = new JLabel("  🔔  Alert Center", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(226, 232, 240));
        title.setOpaque(true);
        title.setBackground(BG_HEADER);
        title.setBorder(new EmptyBorder(8, 10, 8, 10));

        // ── Live alert banner ─────────────────────────────────────────────────
        bannerPanel = new JPanel(new BorderLayout(8, 0));
        bannerPanel.setBackground(BANNER_INFO);
        bannerPanel.setBorder(new EmptyBorder(10, 14, 10, 14));
        bannerPanel.setPreferredSize(new Dimension(0, 55));

        bannerIcon = new JLabel("ℹ");
        bannerIcon.setFont(new Font("Segoe UI", Font.BOLD, 20));
        bannerIcon.setForeground(TEXT_INFO);

        bannerText = new JLabel("No alerts yet — system monitoring...");
        bannerText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bannerText.setForeground(TEXT_INFO);

        bannerPanel.add(bannerIcon, BorderLayout.WEST);
        bannerPanel.add(bannerText, BorderLayout.CENTER);

        // ── History list ──────────────────────────────────────────────────────
        JLabel histTitle = new JLabel("  Alert History", SwingConstants.LEFT);
        histTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        histTitle.setForeground(new Color(148, 163, 184));
        histTitle.setOpaque(true);
        histTitle.setBackground(new Color(22, 33, 55));
        histTitle.setBorder(new EmptyBorder(5, 10, 5, 10));

        historyList = new JList<>(historyModel);
        historyList.setBackground(BG_HISTORY);
        historyList.setForeground(new Color(203, 213, 225));
        historyList.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        historyList.setFixedCellHeight(20);
        historyList.setSelectionBackground(new Color(51, 65, 85));
        historyList.setCellRenderer(new AlertHistoryCellRenderer());

        JScrollPane scrollPane = new JScrollPane(historyList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG_HISTORY);
        scrollPane.getVerticalScrollBar().setBackground(BG_PANEL);

        JPanel historyContainer = new JPanel(new BorderLayout());
        historyContainer.setBackground(BG_HISTORY);
        historyContainer.add(histTitle, BorderLayout.NORTH);
        historyContainer.add(scrollPane, BorderLayout.CENTER);

        // ── Assemble ──────────────────────────────────────────────────────────
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG_PANEL);
        top.add(title, BorderLayout.NORTH);
        top.add(bannerPanel, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(historyContainer, BorderLayout.CENTER);
    }

    /**
     * Post a new alert — updates the banner and appends to history.
     * Must be called from the Swing EDT.
     */
    public void postAlert(String message, AlertLevel level, String timestamp) {
        // Update banner
        updateBanner(message, level);

        // Append to history
        String entry = "[" + timestamp + "] [" + level.getLabel() + "] " + message;
        historyModel.add(0, entry); // newest first
        if (historyModel.size() > 200) {
            historyModel.remove(historyModel.size() - 1);
        }

        // Flash banner for CRITICAL
        if (level == AlertLevel.CRITICAL) {
            startFlash(BANNER_CRITICAL);
        }
    }

    private void updateBanner(String message, AlertLevel level) {
        stopFlash();
        Color bg, fg;
        String icon;

        switch (level) {
            case CRITICAL:
                bg = BANNER_CRITICAL; fg = TEXT_CRITICAL; icon = "🚨";
                break;
            case WARNING:
                bg = BANNER_WARNING;  fg = TEXT_WARNING;  icon = "⚠";
                break;
            default:
                bg = BANNER_INFO;     fg = TEXT_INFO;     icon = "ℹ";
                break;
        }

        bannerPanel.setBackground(bg);
        bannerIcon.setForeground(fg);
        bannerText.setForeground(fg);
        bannerIcon.setText(icon);

        // Trim message for banner display
        String display = message.length() > 100 ? message.substring(0, 97) + "…" : message;
        bannerText.setText(display);
        flashBase = bg;
    }

    private void startFlash(Color base) {
        stopFlash();
        flashCount = 0;
        flashTimer = new Timer(300, e -> {
            flashCount++;
            if (flashCount % 2 == 0) {
                bannerPanel.setBackground(base);
            } else {
                bannerPanel.setBackground(base.brighter());
            }
            if (flashCount >= 8) stopFlash();
        });
        flashTimer.start();
    }

    private void stopFlash() {
        if (flashTimer != null && flashTimer.isRunning()) {
            flashTimer.stop();
            bannerPanel.setBackground(flashBase);
        }
    }

    /** Clear all alert history. */
    public void clear() {
        historyModel.clear();
        bannerText.setText("No alerts yet — system monitoring...");
        bannerPanel.setBackground(BANNER_INFO);
        bannerIcon.setText("ℹ");
    }

    // ── Cell renderer for history list ────────────────────────────────────────

    private static class AlertHistoryCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            String text = value.toString();
            if (text.contains("[CRITICAL]")) {
                label.setForeground(new Color(252, 165, 165));
                label.setBackground(isSelected ? new Color(80, 30, 30) : new Color(40, 18, 18));
            } else if (text.contains("[WARNING]")) {
                label.setForeground(new Color(253, 224, 71));
                label.setBackground(isSelected ? new Color(60, 50, 10) : new Color(30, 26, 10));
            } else {
                label.setForeground(new Color(147, 197, 253));
                label.setBackground(isSelected ? new Color(30, 50, 80) : new Color(18, 28, 46));
            }
            label.setBorder(new EmptyBorder(2, 10, 2, 10));
            label.setOpaque(true);
            return label;
        }
    }
}
