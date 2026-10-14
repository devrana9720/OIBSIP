package com.oibsip.reservation.dao;

import com.oibsip.reservation.db.DBConnection;
import com.oibsip.reservation.model.Reservation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReservationDAO {

    public boolean save(Reservation reservation) throws SQLException {

        String pnr = generatePNR();

        String sql = """
                INSERT INTO reservations
                (pnr, passenger_name, train_number, train_name,
                 source_station, destination_station, journey_date,
                 travel_class, booking_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'CONFIRMED')
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, pnr);
            statement.setString(2, reservation.getPassengerName());
            statement.setInt(3, Integer.parseInt(reservation.getTrainNumber()));
            statement.setString(4, reservation.getTrainName());
            statement.setString(5, reservation.getSourceStation());
            statement.setString(6, reservation.getDestinationStation());
            statement.setDate(7, Date.valueOf(reservation.getJourneyDate()));
            statement.setString(8, reservation.getTravelClass());

            int rows = statement.executeUpdate();

            if (rows > 0) {
                reservation.setPnr(pnr);

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        reservation.setId(keys.getInt(1));
                    }
                }

                return true;
            }

            return false;
        }
    }

    private String generatePNR() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();
    }

    public List<Reservation> findAll() throws SQLException {

        List<Reservation> reservations = new ArrayList<>();

        String sql = """
                SELECT id, pnr, passenger_name, train_number,
                       train_name, source_station, destination_station,
                       journey_date, travel_class, booking_status
                FROM reservations
                ORDER BY id DESC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                reservations.add(mapReservation(resultSet));
            }
        }

        return reservations;
    }

    public Reservation findByPnr(String pnr) throws SQLException {

        String sql = """
                SELECT id, pnr, passenger_name, train_number,
                       train_name, source_station, destination_station,
                       journey_date, travel_class, booking_status
                FROM reservations
                WHERE pnr = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pnr.trim().toUpperCase());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapReservation(resultSet);
                }
            }
        }

        return null;
    }

    public boolean cancelByPnr(String pnr) throws SQLException {

        String sql = """
                UPDATE reservations
                SET booking_status = 'CANCELLED'
                WHERE pnr = ?
                  AND booking_status = 'CONFIRMED'
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pnr.trim().toUpperCase());

            return statement.executeUpdate() > 0;
        }
    }

    private Reservation mapReservation(ResultSet resultSet)
            throws SQLException {

        return new Reservation(
                resultSet.getInt("id"),
                resultSet.getString("pnr"),
                resultSet.getString("passenger_name"),
                String.valueOf(resultSet.getInt("train_number")),
                resultSet.getString("train_name"),
                resultSet.getString("source_station"),
                resultSet.getString("destination_station"),
                resultSet.getDate("journey_date").toLocalDate(),
                resultSet.getString("travel_class"),
                resultSet.getString("booking_status")
        );
    }
}