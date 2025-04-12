package org.example;

import javax.swing.*;
import igu.Principal;

public class Main {
    public static void main(String[] args) {
        // Ejecutar la GUI en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Principal");
            frame.setContentPane(new Principal().getMainPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800,410);
            frame.setLocationRelativeTo(null);

            frame.setVisible(true);
        });
    }
}
