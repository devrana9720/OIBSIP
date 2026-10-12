
package com.oibsip;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

public class App {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Online Reservation System");

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

            JLabel label = new JLabel("Welcome to Online Reservation System", JLabel.CENTER);

            JButton bookButton = new JButton("Book Reservation");
            JButton viewButton = new JButton("View Reservations");
            JButton cancelButton = new JButton("Cancel Reservation");

            bookButton.addActionListener(event ->
                label.setText("Book Reservation selected")
            );

            viewButton.addActionListener(event ->
                label.setText("View Reservations selected")
            );

            cancelButton.addActionListener(event ->
                label.setText("Cancel Reservation selected")
            );

            panel.add(label, BorderLayout.PAGE_START);
            buttonPanel.add(bookButton);
            buttonPanel.add(viewButton);
            buttonPanel.add(cancelButton);
            panel.add(buttonPanel, BorderLayout.CENTER);
            frame.add(panel);

            frame.setSize(550, 180);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
