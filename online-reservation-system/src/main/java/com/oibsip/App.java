
package com.oibsip;

import com.oibsip.reservation.dao.ReservationDAO;
import com.oibsip.reservation.model.Reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::createHomeScreen);
    }

    private static void createHomeScreen() {

        JFrame frame = new JFrame("Online Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 25));
        panel.setBorder(
                BorderFactory.createEmptyBorder(30, 20, 30, 20)
        );

        JLabel title = new JLabel(
                "Welcome to Online Reservation System",
                JLabel.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 10)
        );

        JButton bookButton = new JButton("Book Reservation");
        JButton viewButton = new JButton("View Reservations");
        JButton cancelButton = new JButton("Cancel Reservation");

        bookButton.addActionListener(event -> openBookingForm(frame));

        viewButton.addActionListener(event ->
                JOptionPane.showMessageDialog(
                        frame,
                        "View Reservations will be implemented later."
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
        bookingFrame.setSize(500, 400);
        bookingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bookingFrame.setLocationRelativeTo(homeFrame);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 15));
        formPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JTextField nameField = new JTextField();
        JTextField trainField = new JTextField();
        JTextField dateField = new JTextField();

        String[] classes = {
                "Sleeper",
                "AC 3 Tier",
                "AC 2 Tier",
                "AC First Class"
        };

        JComboBox<String> classBox = new JComboBox<>(classes);

        JButton submitButton = new JButton("Book");
        JButton clearButton = new JButton("Clear");

        submitButton.addActionListener(event -> {

            String name = nameField.getText().trim();
            String trainNumber = trainField.getText().trim();
            String dateText = dateField.getText().trim();
            String travelClass = (String) classBox.getSelectedItem();

            if (name.isEmpty()
                    || trainNumber.isEmpty()
                    || dateText.isEmpty()) {

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
                        "Train number must contain digits only.",
                        "Invalid Train Number",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            LocalDate journeyDate;

            try {
                journeyDate = LocalDate.parse(dateText);
            } catch (DateTimeParseException exception) {
                JOptionPane.showMessageDialog(
                        bookingFrame,
                        "Enter a valid date in YYYY-MM-DD format.",
                        "Invalid Date",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (journeyDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(
                        bookingFrame,
                        "Journey date cannot be in the past.",
                        "Invalid Date",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Reservation reservation = new Reservation(
                    name,
                    trainNumber,
                    journeyDate,
                    travelClass
            );

            try {
                ReservationDAO reservationDAO = new ReservationDAO();

                boolean saved = reservationDAO.save(reservation);

                if (saved) {
                    JOptionPane.showMessageDialog(
                            bookingFrame,
                            "Reservation booked successfully!\n\n"
                                    + "Passenger: "
                                    + reservation.getPassengerName() + "\n"
                                    + "Train Number: "
                                    + reservation.getTrainNumber() + "\n"
                                    + "Journey Date: "
                                    + reservation.getJourneyDate() + "\n"
                                    + "Travel Class: "
                                    + reservation.getTravelClass(),
                            "Booking Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    nameField.setText("");
                    trainField.setText("");
                    dateField.setText("");
                    classBox.setSelectedIndex(0);

                } else {
                    JOptionPane.showMessageDialog(
                            bookingFrame,
                            "Reservation could not be saved.",
                            "Booking Failed",
                            JOptionPane.WARNING_MESSAGE
                    );
                }

            } catch (SQLException exception) {
                JOptionPane.showMessageDialog(
                        bookingFrame,
                        "Database error: " + exception.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );

            } catch (IllegalStateException exception) {
                JOptionPane.showMessageDialog(
                        bookingFrame,
                        exception.getMessage(),
                        "Configuration Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        clearButton.addActionListener(event -> {
            nameField.setText("");
            trainField.setText("");
            dateField.setText("");
            classBox.setSelectedIndex(0);
        });

        formPanel.add(new JLabel("Passenger Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Train Number:"));
        formPanel.add(trainField);

        formPanel.add(new JLabel("Journey Date (YYYY-MM-DD):"));
        formPanel.add(dateField);

        formPanel.add(new JLabel("Travel Class:"));
        formPanel.add(classBox);

        formPanel.add(submitButton);
        formPanel.add(clearButton);

        bookingFrame.add(formPanel);
        bookingFrame.setVisible(true);
    }
}
