package com.bookstore.service;

import com.bookstore.dao.UserDAO;
import com.bookstore.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User register(String fullName, String email, String password, String role) throws SQLException {
        if (userDAO.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email da duoc dang ky");
        }
        User user = new User(0, fullName, email, hash(password), role);
        int id = userDAO.insert(user);
        user.setUserId(id);
        return user;
    }

    public User login(String email, String password) throws SQLException {
        User user = userDAO.findByEmail(email);
        if (user == null || !user.getPasswordHash().equals(hash(password))) {
            throw new IllegalArgumentException("Sai email hoac mat khau");
        }
        return user;
    }

    public List<User> listUsers() throws SQLException {
        return userDAO.findAll();
    }

    public void changeRole(int userId, String role) throws SQLException {
        if (!isValidRole(role)) {
            throw new IllegalArgumentException("Vai tro khong hop le");
        }
        userDAO.updateRole(userId, role);
    }

    private boolean isValidRole(String role) {
        return "CUSTOMER".equals(role) || "STAFF".equals(role) || "MANAGER".equals(role);
    }

    private String hash(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
