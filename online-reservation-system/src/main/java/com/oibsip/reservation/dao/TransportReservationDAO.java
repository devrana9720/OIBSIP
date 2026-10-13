
package com.oibsip.reservation.dao;

import com.oibsip.reservation.db.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransportReservationDAO {

    public record TransportReservation(
            int id,
            String type,
            String passengerName,
            String sourceCity,
            String destinationCity,
            LocalDate journeyDate,
            String travelClass,
            String serviceName,
            BigDecimal fare,
            String status) {
    }

    // Save a Flight or Bus reservation.
    public int save(
            String type,
            String passengerName,
            String sourceCity,
            String destinationCity,
            LocalDate journeyDate,
            String travelClass,
            String serviceName,
            BigDecimal fare) throws SQLException {

        validateType(type);

        String sql = """
                INSERT INTO transport_reservations
                (transport_type, passenger_name, source_city,
                 destination_city, journey_date, travel_class,
                 service_name, estimated_fare, booking_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'CONFIRMED')
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, type);
            statement.setString(2, passengerName);
            statement.setString(3, sourceCity);
            statement.setString(4, destinationCity);
            statement.setDate(5, Date.valueOf(journeyDate));
            statement.setString(6, travelClass);
            statement.setString(7, serviceName);
            statement.setBigDecimal(8, fare);

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Could not retrieve the booking ID.");
    }

    // Retrieve all reservations for a particular transport type.
    public List<TransportReservation> findAll(String type)
            throws SQLException {

        validateType(type);

        String sql = """
                SELECT id, transport_type, passenger_name,
                       source_city, destination_city, journey_date,
                       travel_class, service_name, estimated_fare,
                       booking_status
                FROM transport_reservations
                WHERE transport_type = ?
                ORDER BY id DESC
                """;

        List<TransportReservation> reservations = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, type);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    reservations.add(new TransportReservation(
                            result.getInt("id"),
                            result.getString("transport_type"),
                            result.getString("passenger_name"),
                            result.getString("source_city"),
                            result.getString("destination_city"),
                            result.getDate("journey_date").toLocalDate(),
                            result.getString("travel_class"),
                            result.getString("service_name"),
                            result.getBigDecimal("estimated_fare"),
                            result.getString("booking_status")
                    ));
                }
            }
        }

        return reservations;
    }

    // Cancel a confirmed booking without deleting its database record.
    public boolean cancelById(int id, String type)
            throws SQLException {

        validateType(type);

        String sql = """
                UPDATE transport_reservations
                SET booking_status = 'CANCELLED'
                WHERE id = ?
                  AND transport_type = ?
                  AND booking_status = 'CONFIRMED'
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.setString(2, type);

            return statement.executeUpdate() == 1;
        }
    }

    // Accept only the two supported transport types.
    private void validateType(String type) {
        if (!"FLIGHT".equals(type) && !"BUS".equals(type)) {
            throw new IllegalArgumentException(
                    "Transport type must be FLIGHT or BUS.");
        }
    }
}
