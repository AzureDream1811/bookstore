package com.bookstore.service;

import com.bookstore.dao.UserDAO;
import com.bookstore.model.User;
import jakarta.mail.MessagingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    private final SecureRandom random = new SecureRandom();

    public User register(String fullName,
                         String email,
                         String password,
                         String role) throws SQLException, MessagingException {

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

        String otp = String.valueOf(100000 + random.nextInt(900000));

        User user = new User();

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPasswordHash(hash(password));
        user.setRole(role);

        user.setVerifyCode(otp);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(5));
        user.setVerified(false);

        int id = userDAO.insert(user);
        user.setUserId(id);

        sendOtpEmail(email, fullName, otp);

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
        if (user.getOtpExpiresAt() == null || LocalDateTime.now().isAfter(user.getOtpExpiresAt()))
            return false;
        if(!otp.equals(user.getVerifyCode()))
            return false;
        userDAO.verifyAccount(email);
        return true;

    }

    private void sendOtpEmail(String email, String fullName, String otp) throws MessagingException {
        String subject = "Ma xac thuc tai khoan Bookstore";
        String body = """
                <p>Xin chao %s,</p>
                <p>Ma OTP xac thuc tai khoan cua ban la: <b>%s</b></p>
                <p>Ma nay co hieu luc trong 5 phut.</p>
                """.formatted(fullName, otp);
        new EmailService().sendMail(email, subject, body);
    }
}
