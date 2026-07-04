package com.bookstore.service;

import com.bookstore.dao.UserDAO;
import com.bookstore.util.EmailService;

import java.sql.SQLException;
import java.util.Random;

public class ProfileService {
    private final UserDAO userDAO = new UserDAO();
    private final EmailService emailService = new EmailService();

    public String sendOTP(String email) throws Exception {
        String otp = generateOTP();
        String subject = "Mã OTP xác thực tài khoản Bookstore";
        String body = "<h2>Mã OTP của bạn là: <b>" + otp + "</b></h2><p>Mã này có hiệu lực trong 5 phút.</p>";

        emailService.sendMail(email, subject, body);
        // Trong thực tế nên lưu OTP + thời gian hết hạn vào DB
        return otp;
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void changePassword(int userId, String oldPassword, String newPassword, String otp) throws SQLException {
        // TODO: Validate OTP (có thể lưu OTP tạm thời)
        if (otp == null || otp.length() != 6) {
            throw new IllegalArgumentException("Mã OTP không hợp lệ");
        }

        // Kiểm tra mật khẩu cũ (cần implement trong AuthService)
        userDAO.updatePassword(userId, newPassword); // cần thêm method này
    }

    public void updateContactInfo(int userId, String newEmail, String newPhone, String otp) throws SQLException {
        if (otp == null || otp.length() != 6) {
            throw new IllegalArgumentException("Mã OTP không hợp lệ");
        }

        if (!newEmail.isBlank()) {
            // Kiểm tra email đã tồn tại
            if (userDAO.findByEmail(newEmail) != null) {
                throw new IllegalArgumentException("Email đã được sử dụng");
            }
            userDAO.updateEmail(userId, newEmail);
        }
        // Có thể cập nhật phone nếu có bảng riêng
    }
}
