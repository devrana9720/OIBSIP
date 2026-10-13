
package com.oibsip.reservation.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/online_reservation";

    private static final String USER =
            System.getenv("MYSQL_DB_USER");

    private static final String PASSWORD =
            System.getenv("MYSQL_DB_PASSWORD");

    public static Connection getConnection() throws SQLException {

        if (USER == null || PASSWORD == null) {
            throw new IllegalStateException(
                    "MySQL credentials are not configured."
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
