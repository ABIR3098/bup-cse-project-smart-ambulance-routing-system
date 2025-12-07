package com.ambulance.routing;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            String startInput = JOptionPane.showInputDialog("Enter Start Node (A-P):");
            String endInput = JOptionPane.showInputDialog("Enter Destination Node (A-P):");
            if (startInput == null || endInput == null) return;

            int start = Character.toUpperCase(startInput.charAt(0)) - 'A';
            int end = Character.toUpperCase(endInput.charAt(0)) - 'A';

            // OBSTACLE INPUT
            String obs1 = JOptionPane.showInputDialog("Enter Obstacle Node 1 (A-P):");
            String obs2 = JOptionPane.showInputDialog("Enter Obstacle Node 2 (A-P):");

            // Handle potential null or empty input for obstacle gracefully
            int o1 = -1, o2 = -1;
            if (obs1 != null && obs1.length() > 0) {
                o1 = Character.toUpperCase(obs1.charAt(0)) - 'A';
            }
            if (obs2 != null && obs2.length() > 0) {
                o2 = Character.toUpperCase(obs2.charAt(0)) - 'A';
            }


            JFrame frame = new JFrame("Ambulance Routing System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // Pass the obstacle indices (o1, o2)
            frame.add(new AmbulanceRoutingSystem(start, end, o1, o2));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
