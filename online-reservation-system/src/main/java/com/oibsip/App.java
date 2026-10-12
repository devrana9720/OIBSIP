
package com.oibsip;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.FlowLayout;

public class App {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Online Reservation System");

            JPanel panel = new JPanel(new FlowLayout());

            JLabel label = new JLabel("Welcome to Online Reservation System");

            JButton button = new JButton("Book Reservation");

            button.addActionListener(event -> {
                label.setText("You clicked Book Reservation!");
            });

            panel.add(label);
            panel.add(button);

            frame.add(panel);

            frame.setSize(450, 180);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
