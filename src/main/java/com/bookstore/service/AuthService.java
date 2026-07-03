package com.bookstore.service;

import com.bookstore.dao.UserDAO;
import com.bookstore.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User register(String fullName,
                         String email,
                         String password,
                         String role) throws SQLException {

        if (userDAO.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email da ton tai.");
        }

        // Kiểm tra email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Email khong dung dinh dang.");
        }

        // Kiểm tra mật khẩu
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

        if (!password.matches(passwordRegex)) {
            throw new IllegalArgumentException(
                    "Mat khau phai co it nhat 8 ky tu, gom chu hoa, chu thuong va so.");
        }

        // Kiểm tra vai trò
        if (!isValidRole(role)) {
            throw new IllegalArgumentException("Vai tro khong hop le.");
        }

        String otp = String.valueOf(
                100000 + new java.util.Random().nextInt(900000));

        User user = new User();

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPasswordHash(hash(password));
        user.setRole(role);

        user.setVerifyCode(otp);
        user.setVerified(false);

        int id = userDAO.insert(user);
        user.setUserId(id);

        System.out.println("====================");
        System.out.println("Ma OTP: " + otp);
        System.out.println("====================");

        return user;
    }

    public User login(String email, String password) throws SQLException {

        User user = userDAO.findByEmail(email);

        // 1. Kiểm tra email có tồn tại không
        if (user == null) {
            throw new IllegalArgumentException("Email khong ton tai.");
        }

        // 2. Kiểm tra mật khẩu
        if (!user.getPasswordHash().equals(hash(password))) {
            throw new IllegalArgumentException("Sai mat khau.");
        }

        // 3. Kiểm tra xác thực
        if (!user.isVerified()) {
            throw new IllegalArgumentException("Tai khoan chua duoc xac thuc.");
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
    public boolean verify(String email,
                          String otp)
            throws SQLException {
        User user = userDAO.findByEmail(email);
        if(user==null)
            return false;
        if(user.isVerified())
            return true;
        if(!otp.equals(user.getVerifyCode()))
            return false;
        userDAO.verifyAccount(email);
        return true;

    }
}