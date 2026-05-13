package gui;

import alert.AlertLevel;
import city.Zone;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * CityMapPanel displays a 3×3 grid of zone tiles.
 * Each tile changes color based on the zone's current disaster state.
 */
public class CityMapPanel extends JPanel {

    // ── Colors per state ─────────────────────────────────────────────────────
    private static final Color COLOR_SAFE       = new Color(34, 197, 94);    // green
    private static final Color COLOR_FLOODED    = new Color(59, 130, 246);   // blue
    private static final Color COLOR_EARTHQUAKE = new Color(234, 179, 8);    // amber
    private static final Color COLOR_FIRE       = new Color(239, 68, 68);    // red
    private static final Color COLOR_RECOVERING = new Color(168, 85, 247);   // purple
    private static final Color COLOR_TEXT       = Color.WHITE;
    private static final Color PANEL_BG         = new Color(15, 23, 42);

    private final List<Zone> zones;
    private final JPanel[]   tiles;

    public CityMapPanel(List<Zone> zones) {
        this.zones = zones;
        this.tiles = new JPanel[zones.size()];

        setBackground(PANEL_BG);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        setLayout(new GridLayout(3, 3, 8, 8));

        for (int i = 0; i < zones.size(); i++) {
            tiles[i] = createZoneTile(zones.get(i));
            add(tiles[i]);
        }
    }

    private JPanel createZoneTile(Zone zone) {
        JPanel tile = new JPanel(new BorderLayout(0, 4));
        tile.setOpaque(true);
        tile.setBorder(new EmptyBorder(8, 8, 8, 8));
        tile.setBackground(COLOR_SAFE);

        // Zone name label
        JLabel nameLabel = new JLabel(zone.getName(), SwingConstants.CENTER);
        nameLabel.setForeground(COLOR_TEXT);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));

        // State label
        JLabel stateLabel = new JLabel("SAFE", SwingConstants.CENTER);
        stateLabel.setForeground(new Color(220, 240, 220));
        stateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        stateLabel.setName("state");

        // Hospital label
        JLabel hospLabel = new JLabel("🏥 " + zone.getHospital().getCurrentPatients()
                + "/" + zone.getHospital().getCapacity(), SwingConstants.CENTER);
        hospLabel.setForeground(COLOR_TEXT);
        hospLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        hospLabel.setName("hosp");

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setOpaque(false);
        infoPanel.add(stateLabel);
        infoPanel.add(hospLabel);

        tile.add(nameLabel, BorderLayout.NORTH);
        tile.add(infoPanel, BorderLayout.CENTER);

        return tile;
    }

    /**
     * Refresh all zone tiles to reflect current state.
     * Call this from the Swing EDT.
     */
    public void refresh() {
        for (int i = 0; i < zones.size(); i++) {
            Zone zone = zones.get(i);
            JPanel tile = tiles[i];

            // Update background color
            tile.setBackground(colorForState(zone.getState()));

            // Update state label
            Component[] north = tile.getComponents();
            for (Component c : tile.getComponents()) {
                if (c instanceof JPanel) {
                    JPanel info = (JPanel) c;
                    for (Component ic : info.getComponents()) {
                        if (ic instanceof JLabel) {
                            JLabel lbl = (JLabel) ic;
                            if ("state".equals(lbl.getName())) {
                                lbl.setText(zone.getState().name());
                            } else if ("hosp".equals(lbl.getName())) {
                                lbl.setText("🏥 " + zone.getHospital().getCurrentPatients()
                                        + "/" + zone.getHospital().getCapacity());
                                // Turn red if full
                                if (zone.getHospital().isFull()) {
                                    lbl.setForeground(new Color(255, 200, 200));
                                } else {
                                    lbl.setForeground(COLOR_TEXT);
                                }
                            }
                        }
                    }
                }
            }
            tile.repaint();
        }
    }

    private Color colorForState(Zone.DisasterState state) {
        switch (state) {
            case FLOODED:    return COLOR_FLOODED;
            case EARTHQUAKE: return COLOR_EARTHQUAKE;
            case FIRE:       return COLOR_FIRE;
            case RECOVERING: return COLOR_RECOVERING;
            default:         return COLOR_SAFE;
        }
    }

    // ── Legend panel (static) ─────────────────────────────────────────────────

    public static JPanel createLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 4));
        legend.setBackground(new Color(15, 23, 42));
        legend.setBorder(new EmptyBorder(4, 8, 4, 8));

        legend.add(legendItem(COLOR_SAFE,       "Safe"));
        legend.add(legendItem(COLOR_FLOODED,    "Flood"));
        legend.add(legendItem(COLOR_EARTHQUAKE, "Earthquake"));
        legend.add(legendItem(COLOR_FIRE,       "Fire"));
        legend.add(legendItem(COLOR_RECOVERING, "Recovering"));
        return legend;
    }

    private static JPanel legendItem(Color color, String label) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        item.setOpaque(false);

        JPanel swatch = new JPanel();
        swatch.setPreferredSize(new Dimension(12, 12));
        swatch.setBackground(color);
        swatch.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(203, 213, 225));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        item.add(swatch);
        item.add(lbl);
        return item;
    }
}
