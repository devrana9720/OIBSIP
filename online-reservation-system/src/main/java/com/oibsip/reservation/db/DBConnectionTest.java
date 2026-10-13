
package com.oibsip.reservation.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionTest {

    public static void main(String[] args) {

        try (Connection connection = DBConnection.getConnection()) {

            System.out.println("Database connection successful!");
            System.out.println("Database: " + connection.getCatalog());

        } catch (SQLException e) {

            System.out.println("Database connection failed.");
            System.out.println("Reason: " + e.getMessage());

        } catch (IllegalStateException e) {

            System.out.println(e.getMessage());

        }
    }
}
