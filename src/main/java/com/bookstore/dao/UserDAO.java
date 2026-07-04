package com.bookstore.dao;

import com.bookstore.model.User;
import com.bookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public int insert(User user) throws SQLException {
        String sql =
                """
                        INSERT INTO user
                        (full_name,email,password_hash,role,verify_code,otp_expires_at,verified)
                        VALUES(?,?,?,?,?,?,?)
                        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getVerifyCode());
            ps.setTimestamp(6, Timestamp.valueOf(user.getOtpExpiresAt()));
            ps.setBoolean(7, user.isVerified());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY user_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(map(rs));
        }
        return users;
    }

    public void updateRole(int userId, String role) throws SQLException {
        String sql = "UPDATE user SET role = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();

        u.setUserId(rs.getInt("user_id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setVerifyCode(rs.getString("verify_code"));
        Timestamp otpExpiresAt = rs.getTimestamp("otp_expires_at");
        if (otpExpiresAt != null) {
            u.setOtpExpiresAt(otpExpiresAt.toLocalDateTime());
        }
        u.setVerified(rs.getBoolean("verified"));

        return u;
    }

    public void verifyAccount(String email) throws SQLException {
        String sql =
                "UPDATE user SET verified = true, verify_code = NULL, otp_expires_at = NULL WHERE email=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        }
    }
    public void updatePassword(int userId, String newPasswordHash) throws SQLException {
        String sql = "UPDATE user SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
    }
}

    public void updateEmail(int userId, String newEmail) throws SQLException {
        String sql = "UPDATE user SET email = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newEmail);
            ps.setInt(2, userId);
            ps.executeUpdate();
    }
}
}
