package gui;

import alert.AlertLevel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * LogPanel displays a scrollable, color-coded event log table.
 * Each row shows a timestamp, level badge, and the event message.
 */
public class LogPanel extends JPanel {

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final Color BG_PANEL      = new Color(15, 23, 42);
    private static final Color BG_TABLE      = new Color(22, 33, 55);
    private static final Color BG_HEADER     = new Color(30, 41, 59);
    private static final Color FG_DEFAULT    = new Color(203, 213, 225);
    private static final Color FG_INFO       = new Color(147, 197, 253);   // light blue
    private static final Color FG_WARNING    = new Color(253, 224, 71);    // yellow
    private static final Color FG_CRITICAL   = new Color(252, 165, 165);   // light red
    private static final Color ROW_CRITICAL  = new Color(60, 20, 20);
    private static final Color ROW_WARNING   = new Color(55, 45, 10);
    private static final Color ROW_INFO      = new Color(18, 30, 50);
    private static final Color ROW_ALT       = new Color(20, 35, 58);

    private final DefaultTableModel tableModel;
    private final JTable            table;
    private int rowCount = 0;

    public LogPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_PANEL);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));

        // ── Title bar ────────────────────────────────────────────────────────
        JLabel title = new JLabel("  📋  Event Log", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(226, 232, 240));
        title.setOpaque(true);
        title.setBackground(BG_HEADER);
        title.setBorder(new EmptyBorder(8, 10, 8, 10));

        // ── Table setup ───────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(new String[]{"#", "Level", "Message"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                String level = (String) tableModel.getValueAt(row, 1);
                if ("CRITICAL".equals(level)) {
                    c.setBackground(ROW_CRITICAL);
                    c.setForeground(FG_CRITICAL);
                } else if ("WARNING".equals(level)) {
                    c.setBackground(ROW_WARNING);
                    c.setForeground(FG_WARNING);
                } else {
                    c.setBackground(row % 2 == 0 ? ROW_INFO : ROW_ALT);
                    c.setForeground(FG_INFO);
                }
                return c;
            }
        };

        table.setBackground(BG_TABLE);
        table.setForeground(FG_DEFAULT);
        table.setGridColor(new Color(30, 42, 65));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(22);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        // Header style
        table.getTableHeader().setBackground(BG_HEADER);
        table.getTableHeader().setForeground(new Color(148, 163, 184));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(75);
        table.getColumnModel().getColumn(1).setMaxWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(900);

        // Center-align the # and Level columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG_TABLE);
        scrollPane.getVerticalScrollBar().setBackground(BG_PANEL);

        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Append a new log entry. Must be called from the Swing EDT.
     */
    public void appendLog(String message, AlertLevel level) {
        rowCount++;
        tableModel.addRow(new Object[]{rowCount, level.getLabel(), message});
        // Auto-scroll to last row
        int lastRow = table.getRowCount() - 1;
        if (lastRow >= 0) {
            table.scrollRectToVisible(table.getCellRect(lastRow, 0, true));
        }
    }

    /** Clear all log entries. */
    public void clear() {
        tableModel.setRowCount(0);
        rowCount = 0;
    }
}
