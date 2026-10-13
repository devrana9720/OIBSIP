
package com.oibsip.reservation.dao;

import com.oibsip.reservation.db.DBConnection;
import com.oibsip.reservation.model.Reservation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
            statement.setInt(2, Integer.parseInt(reservation.getTrainNumber()));
            statement.setDate(3, Date.valueOf(reservation.getJourneyDate()));
            statement.setString(4, reservation.getTravelClass());

            return statement.executeUpdate() > 0;
        }
    }
}
