
package com.oibsip.reservation.dao;

import com.oibsip.reservation.model.Reservation;

import java.sql.SQLException;
import java.time.LocalDate;

public class ReservationDAOTest {

    public static void main(String[] args) {

        Reservation reservation = new Reservation(
                "Test Passenger",
                "12345",
                LocalDate.now().plusDays(7),
                "Sleeper"
        );

        ReservationDAO dao = new ReservationDAO();

        try {
            boolean saved = dao.save(reservation);

            if (saved) {
                System.out.println("Reservation saved successfully!");
                System.out.println("Passenger: "
                        + reservation.getPassengerName());
                System.out.println("Train Number: "
                        + reservation.getTrainNumber());
                System.out.println("Journey Date: "
                        + reservation.getJourneyDate());
                System.out.println("Travel Class: "
                        + reservation.getTravelClass());
            } else {
                System.out.println("Reservation was not saved.");
            }

        } catch (SQLException e) {
            System.out.println("Database operation failed.");
            System.out.println("Reason: " + e.getMessage());
        }
    }
}
