
package com.oibsip.reservation.dao;

import com.oibsip.reservation.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UserDAO {

    private static final int ITERATIONS = 210_000;
    private static final int KEY_LENGTH = 256;

    private final SecureRandom random = new SecureRandom();

    public boolean register(
            String fullName,
            String username,
            String email,
            String password) throws SQLException {

        String sql = """
                INSERT INTO users
                (full_name, username, email, password_hash)
                VALUES (?, ?, ?, ?)
                """;

        String hash = hashPassword(password);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, fullName.trim());
            statement.setString(2, username.trim());
            statement.setString(
                    3,
                    email == null || email.isBlank()
                            ? null : email.trim());
            statement.setString(4, hash);

            return statement.executeUpdate() == 1;
        }
    }

    public boolean authenticate(
            String username,
            String password) throws SQLException {

        String sql = """
                SELECT password_hash
                FROM users
                WHERE username = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username.trim());

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return false;
                }

                return verifyPassword(
                        password,
                        result.getString("password_hash"));
            }
        }
    }

    private String hashPassword(String password) {
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        byte[] hash = derive(password.toCharArray(), salt);

        return "PBKDF2$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt)
                + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    private boolean verifyPassword(
            String password,
            String stored) {

        try {
            String[] parts = stored.split("\\$");

            if (parts.length != 4
                    || !"PBKDF2".equals(parts[0])) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);

            byte[] actual = derive(
                    password.toCharArray(), salt, iterations);

            return MessageDigest.isEqual(expected, actual);

        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private byte[] derive(char[] password, byte[] salt) {
        return derive(password, salt, ITERATIONS);
    }

    private byte[] derive(
            char[] password,
            byte[] salt,
            int iterations) {

        PBEKeySpec spec = new PBEKeySpec(
                password, salt, iterations, KEY_LENGTH);

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256");

            return factory.generateSecret(spec).getEncoded();

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Could not securely process the password.", ex);
        } finally {
            spec.clearPassword();
            java.util.Arrays.fill(password, '\0');
        }
    }
}
