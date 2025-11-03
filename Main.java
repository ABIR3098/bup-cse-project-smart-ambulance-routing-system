package com.ambulance.routing;



import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String startInput = JOptionPane.showInputDialog("Enter Start Node (A-F):");
            String endInput = JOptionPane.showInputDialog("Enter Destination Node (A-F):");
            if (startInput == null || endInput == null) return;

            int start = Character.toUpperCase(startInput.charAt(0)) - 'A';
            int end = Character.toUpperCase(endInput.charAt(0)) - 'A';

            JFrame frame = new JFrame("Ambulance Routing System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new AmbulanceRoutingSystem(start, end));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
