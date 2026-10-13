package com.oibsip;

import com.oibsip.reservation.dao.ReservationDAO;
import com.oibsip.reservation.model.Reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

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
        viewButton.addActionListener(event -> showReservations(frame));
        cancelButton.addActionListener(
                event -> openCancellationScreen(frame)
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

            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(
                        bookingFrame,
                        "Train number is too large.",
                        "Invalid Train Number",
                        JOptionPane.WARNING_MESSAGE
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

    private static void showReservations(JFrame homeFrame) {

        try {
            ReservationDAO reservationDAO = new ReservationDAO();
            List<Reservation> reservations = reservationDAO.findAll();

            if (reservations.isEmpty()) {
                JOptionPane.showMessageDialog(
                        homeFrame,
                        "No reservations found.",
                        "View Reservations",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            String[] columns = {
                    "Reservation ID",
                    "Passenger Name",
                    "Train Number",
                    "Journey Date",
                    "Travel Class"
            };

            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Reservation reservation : reservations) {
                model.addRow(new Object[]{
                        reservation.getId(),
                        reservation.getPassengerName(),
                        reservation.getTrainNumber(),
                        reservation.getJourneyDate(),
                        reservation.getTravelClass()
                });
            }

            JTable table = new JTable(model);
            table.setRowHeight(28);
            table.setAutoCreateRowSorter(true);
            table.setFillsViewportHeight(true);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(700, 300));

            JOptionPane.showMessageDialog(
                    homeFrame,
                    scrollPane,
                    "View Reservations",
                    JOptionPane.PLAIN_MESSAGE
            );

        } catch (SQLException | IllegalStateException exception) {
            JOptionPane.showMessageDialog(
                    homeFrame,
                    "Could not load reservations.\n"
                            + exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static void openCancellationScreen(JFrame homeFrame) {

        JFrame cancelFrame = new JFrame("Cancel Reservation");
        cancelFrame.setSize(850, 400);
        cancelFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cancelFrame.setLocationRelativeTo(homeFrame);

        String[] columns = {
                "Reservation ID",
                "Passenger Name",
                "Train Number",
                "Journey Date",
                "Travel Class"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.setAutoCreateRowSorter(true);

        JButton cancelButton =
                new JButton("Cancel Selected Reservation");
        JButton closeButton = new JButton("Close");

        Runnable loadReservations = () -> {
            try {
                List<Reservation> reservations =
                        new ReservationDAO().findAll();

                model.setRowCount(0);

                for (Reservation reservation : reservations) {
                    model.addRow(new Object[]{
                            reservation.getId(),
                            reservation.getPassengerName(),
                            reservation.getTrainNumber(),
                            reservation.getJourneyDate(),
                            reservation.getTravelClass()
                    });
                }

                cancelButton.setEnabled(!reservations.isEmpty());

            } catch (SQLException | IllegalStateException exception) {
                JOptionPane.showMessageDialog(
                        cancelFrame,
                        "Could not load reservations.\n"
                                + exception.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        };

        cancelButton.addActionListener(event -> {

            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(
                        cancelFrame,
                        "Please select a reservation to cancel.",
                        "No Reservation Selected",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int modelRow = table.convertRowIndexToModel(selectedRow);

            int reservationId =
                    ((Number) model.getValueAt(modelRow, 0)).intValue();

            String passengerName =
                    String.valueOf(model.getValueAt(modelRow, 1));

            int confirmation = JOptionPane.showConfirmDialog(
                    cancelFrame,
                    "Are you sure you want to cancel the reservation for "
                            + passengerName + "?\nReservation ID: "
                            + reservationId,
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                boolean deleted =
                        new ReservationDAO().deleteById(reservationId);

                if (deleted) {
                    JOptionPane.showMessageDialog(
                            cancelFrame,
                            "Reservation cancelled successfully.",
                            "Cancellation Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                            cancelFrame,
                            "Reservation was not found. Refreshing the list.",
                            "Cancellation Failed",
                            JOptionPane.WARNING_MESSAGE
                    );
                }

                loadReservations.run();

            } catch (SQLException | IllegalStateException exception) {
                JOptionPane.showMessageDialog(
                        cancelFrame,
                        "Could not cancel reservation.\n"
                                + exception.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        closeButton.addActionListener(event -> cancelFrame.dispose());

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 10)
        );
        buttonPanel.add(cancelButton);
        buttonPanel.add(closeButton);

        cancelFrame.setLayout(new BorderLayout(10, 10));
        cancelFrame.add(new JScrollPane(table), BorderLayout.CENTER);
        cancelFrame.add(buttonPanel, BorderLayout.SOUTH);

        loadReservations.run();
        cancelFrame.setVisible(true);
    }
}