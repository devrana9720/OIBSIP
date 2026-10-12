
package com.oibsip;

import javax.swing.*;
import java.awt.*;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createHomeScreen());
    }

    private static void createHomeScreen() {

        JFrame frame = new JFrame("Online Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 250);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel title = new JLabel(
                "Welcome to Online Reservation System",
                JLabel.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10)
        );

        JButton bookButton = new JButton("Book Reservation");
        JButton viewButton = new JButton("View Reservations");
        JButton cancelButton = new JButton("Cancel Reservation");

        bookButton.addActionListener(event -> openBookingForm(frame));

        viewButton.addActionListener(event ->
                JOptionPane.showMessageDialog(
                        frame,
                        "View Reservations will be implemented next."
                )
        );

        cancelButton.addActionListener(event ->
                JOptionPane.showMessageDialog(
                        frame,
                        "Cancel Reservation will be implemented later."
                )
        );

        buttonPanel.add(bookButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(cancelButton);

        panel.add(title, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void openBookingForm(JFrame homeFrame) {

        JFrame bookingFrame = new JFrame("Book Reservation");
        bookingFrame.setSize(450, 350);
        bookingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bookingFrame.setLocationRelativeTo(homeFrame);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 15));
        formPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel nameLabel = new JLabel("Passenger Name:");
        JTextField nameField = new JTextField(15);

        JLabel trainLabel = new JLabel("Train Number:");
        JTextField trainField = new JTextField(15);

        JLabel dateLabel = new JLabel("Journey Date (YYYY-MM-DD):");
        JTextField dateField = new JTextField(15);

        JLabel classLabel = new JLabel("Travel Class:");
        String[] classes = {
                "Sleeper", "AC 3 Tier", "AC 2 Tier", "AC First Class"
        };
        JComboBox<String> classBox = new JComboBox<>(classes);

        JButton submitButton = new JButton("Book");
        JButton clearButton = new JButton("Clear");

        submitButton.addActionListener(event -> {

            String name = nameField.getText().trim();
            String trainNumber = trainField.getText().trim();
            String journeyDate = dateField.getText().trim();
            String travelClass = (String) classBox.getSelectedItem();

            if (name.isEmpty() || trainNumber.isEmpty()
                    || journeyDate.isEmpty()) {

                JOptionPane.showMessageDialog(
                        bookingFrame,
                        "Please fill in all fields.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!trainNumber.matches("\\d+")) {
                JOptionPane.showMessageDialog(
                        bookingFrame,
                        "Train number must contain digits only."
                );
                return;
            }

            try {
                java.time.LocalDate date =
                        java.time.LocalDate.parse(journeyDate);

                if (date.isBefore(java.time.LocalDate.now())) {
                    JOptionPane.showMessageDialog(
                            bookingFrame,
                            "Journey date cannot be in the past."
                    );
                    return;
                }

            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(
                        bookingFrame,
                        "Enter a valid date in YYYY-MM-DD format."
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    bookingFrame,
                    "Form validated successfully!\n\n"
                            + "Passenger: " + name + "\n"
                            + "Train Number: " + trainNumber + "\n"
                            + "Journey Date: " + journeyDate + "\n"
                            + "Travel Class: " + travelClass
                            + "\n\nThis reservation is not saved yet."
            );
        });

        clearButton.addActionListener(event -> {
            nameField.setText("");
            trainField.setText("");
            dateField.setText("");
            classBox.setSelectedIndex(0);
        });

        formPanel.add(nameLabel);
        formPanel.add(nameField);

        formPanel.add(trainLabel);
        formPanel.add(trainField);

        formPanel.add(dateLabel);
        formPanel.add(dateField);

        formPanel.add(classLabel);
        formPanel.add(classBox);

        formPanel.add(submitButton);
        formPanel.add(clearButton);

        bookingFrame.add(formPanel);
        bookingFrame.setVisible(true);
    }
}
