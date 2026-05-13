package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Real-world location selector with city coordinates and map display.
 * Provides a dropdown of major world cities with their lat/lng.
 */
public class LocationSelector extends JPanel {

    // Real world city data (name, country, latitude, longitude)
    private static final String[][] CITIES = {
            { "Lahore", "Pakistan", "31.5497°N", "74.3436°E" },
            { "Delhi", "India", "28.7041°N", "77.1025°E" },
            { "New York", "USA", "40.7128°N", "74.0060°W" },
            { "London", "UK", "51.5074°N", "0.1278°W" },
            { "Tokyo", "Japan", "35.6762°N", "139.6503°E" },
            { "Sydney", "Australia", "33.8688°S", "151.2093°E" },
            { "Toronto", "Canada", "43.6532°N", "79.3832°W" },
            { "Dubai", "UAE", "25.2048°N", "55.2708°E" },
            { "Singapore", "Singapore", "1.3521°N", "103.8198°E" },
            { "Bangkok", "Thailand", "13.7563°N", "100.5018°E" },
            { "Paris", "France", "48.8566°N", "2.3522°E" },
            { "Berlin", "Germany", "52.5200°N", "13.4050°E" },
            { "Moscow", "Russia", "55.7558°N", "37.6173°E" },
            { "Istanbul", "Turkey", "41.0082°N", "28.9784°E" },
            { "Cairo", "Egypt", "30.0444°N", "31.2357°E" },
            { "Lagos", "Nigeria", "6.5244°N", "3.3792°E" },
            { "Mumbai", "India", "19.0760°N", "72.8777°E" },
            { "Kyiv", "Ukraine", "50.4501°N", "30.5234°E" },
            { "São Paulo", "Brazil", "23.5505°S", "46.6333°W" },
            { "Mexico City", "Mexico", "19.4326°N", "99.1332°W" }
    };

    private JComboBox<String> cityDropdown;
    private JLabel coordLabel;
    private JLabel liveIndicator;
    private String selectedCity = CITIES[0][0];
    private String selectedCountry = CITIES[0][1];
    private String selectedLat = CITIES[0][2];
    private String selectedLng = CITIES[0][3];

    public LocationSelector() {
        setLayout(new BorderLayout(8, 0));
        setOpaque(false);

        // City dropdown
        String[] cityNames = new String[CITIES.length];
        for (int i = 0; i < CITIES.length; i++) {
            cityNames[i] = CITIES[i][0] + ", " + CITIES[i][1];
        }

        cityDropdown = new JComboBox<>(cityNames);
        cityDropdown.setSelectedIndex(0);
        cityDropdown.setBackground(new Color(30, 41, 59));
        cityDropdown.setForeground(new Color(203, 213, 225));
        cityDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cityDropdown.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        cityDropdown.setPreferredSize(new Dimension(320, 36));

        cityDropdown.addActionListener(e -> updateLocation());

        // Coordinates display
        coordLabel = new JLabel("📍 " + selectedLat + ", " + selectedLng);
        coordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        coordLabel.setForeground(new Color(203, 213, 225));

        // Live indicator
        liveIndicator = new JLabel("⚫ LIVE  5 sec ago");
        liveIndicator.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        liveIndicator.setForeground(new Color(100, 200, 100));

        add(cityDropdown, BorderLayout.WEST);
        add(coordLabel, BorderLayout.CENTER);
        add(liveIndicator, BorderLayout.EAST);
    }

    private void updateLocation() {
        int idx = cityDropdown.getSelectedIndex();
        if (idx >= 0 && idx < CITIES.length) {
            selectedCity = CITIES[idx][0];
            selectedCountry = CITIES[idx][1];
            selectedLat = CITIES[idx][2];
            selectedLng = CITIES[idx][3];
            coordLabel.setText("📍 " + selectedLat + ", " + selectedLng);
        }
    }

    public String getSelectedCity() {
        return selectedCity;
    }

    public String getSelectedCountry() {
        return selectedCountry;
    }

    public String getSelectedLat() {
        return selectedLat;
    }

    public String getSelectedLng() {
        return selectedLng;
    }

    public String getLocationDisplay() {
        return "Live location: " + selectedCity + ", " + selectedCountry;
    }
}
