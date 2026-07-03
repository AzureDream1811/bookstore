package com.bookstore.dao;

import com.bookstore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmailDAO {

    public void save(String to, String subject, String status, String errMessage, int retryCount) {
        String sql = "INSERT INTO email_log (recipient, subject, status, error_message, retry_count) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, to);
            stmt.setString(2, subject);
            stmt.setString(3, status);
            stmt.setString(4, errMessage);
            stmt.setInt(5, retryCount);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.printf("Failed to save email log: %s%n", e.getMessage());
        }
    }
}
