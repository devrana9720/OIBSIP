package com.oibsip.reservation.dao;

import com.oibsip.reservation.db.DBConnection;
import com.oibsip.reservation.model.Reservation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public boolean save(Reservation reservation) throws SQLException {

        String sql = """
                INSERT INTO reservations
                (passenger_name, train_number, journey_date, travel_class)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, reservation.getPassengerName());
            statement.setInt(
                    2,
                    Integer.parseInt(reservation.getTrainNumber())
            );
            statement.setDate(
                    3,
                    Date.valueOf(reservation.getJourneyDate())
            );
            statement.setString(4, reservation.getTravelClass());

            return statement.executeUpdate() > 0;
        }
    }

    public List<Reservation> findAll() throws SQLException {

        List<Reservation> reservations = new ArrayList<>();

        String sql = """
                SELECT id, passenger_name, train_number,
                       journey_date, travel_class
                FROM reservations
                ORDER BY id DESC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Reservation reservation = new Reservation(
                        resultSet.getInt("id"),
                        resultSet.getString("passenger_name"),
                        String.valueOf(resultSet.getInt("train_number")),
                        resultSet.getDate("journey_date").toLocalDate(),
                        resultSet.getString("travel_class")
                );

                reservations.add(reservation);
            }
        }

        return reservations;
    }

    public boolean deleteById(int id) throws SQLException {

        String sql = "DELETE FROM reservations WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        }
    }
}
