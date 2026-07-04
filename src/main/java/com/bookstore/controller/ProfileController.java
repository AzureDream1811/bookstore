package com.bookstore.controller;

import com.bookstore.model.User;
import com.bookstore.service.AuthService;
import com.bookstore.service.ProfileService;
import com.bookstore.view.ConsoleView;

import java.sql.SQLException;

public class ProfileController {
    private final ConsoleView view;
    private final AuthService authService;
    private final ProfileService profileService;

    public ProfileController(ConsoleView view) {
        this.view = view;
        this.authService = new AuthService();
        this.profileService = new ProfileService();
    }

    public void open(User currentUser) {
        if (currentUser == null) {
            view.printError("Bạn cần đăng nhập để quản lý tài khoản");
            return;
        }

        boolean running = true;
        while (running) {
            view.print("\n=== QUẢN LÝ TÀI KHOẢN ===");
            view.print("1. Xem thông tin cá nhân");
            view.print("2. Đổi mật khẩu");
            view.print("3. Cập nhật email / SĐT");
            view.print("0. Quay lại");

            int choice = view.readInt("Chọn chức năng: ");

            switch (choice) {
                case 1 -> showProfile(currentUser);
                case 2 -> changePassword(currentUser);
                case 3 -> updateContactInfo(currentUser);
                case 0 -> running = false;
                default -> view.printError("Lựa chọn không hợp lệ");
            }
        }
    }

    private void showProfile(User user) {
        view.print("\n=== THÔNG TIN TÀI KHOẢN ===");
        view.print("Họ tên: " + user.getFullName());
        view.print("Email: " + user.getEmail());
        view.print("Vai trò: " + user.getRole());
        view.print("User ID: " + user.getUserId());
    }

    private void changePassword(User user) {
        try {
            String oldPassword = view.readLine("Nhập mật khẩu cũ: ");
            String newPassword = view.readLine("Nhập mật khẩu mới: ");
            String confirmPassword = view.readLine("Xác nhận mật khẩu mới: ");

            if (!newPassword.equals(confirmPassword)) {
                view.printError("Mật khẩu xác nhận không khớp");
                return;
            }

            // Gửi OTP
            String otp = profileService.sendOTP(user.getEmail());
            view.print("Đã gửi mã OTP đến email: " + user.getEmail());

            String inputOtp = view.readLine("Nhập mã OTP: ");

            profileService.changePassword(user.getUserId(), oldPassword, newPassword, inputOtp);
            view.print("✅ Đổi mật khẩu thành công!");

        } catch (Exception e) {
            view.printError(e.getMessage());
        }
    }

    private void updateContactInfo(User user) {
        try {
            view.print("Cập nhật thông tin liên hệ:");
            String newEmail = view.readLine("Email mới (Enter để giữ nguyên): ");
            String newPhone = view.readLine("Số điện thoại mới (Enter để giữ nguyên): ");

            if (newEmail.isBlank() && newPhone.isBlank()) {
                view.print("Không có thay đổi nào.");
                return;
            }

            // Gửi OTP xác thực
            String otp = profileService.sendOTP(user.getEmail());
            view.print("Đã gửi mã OTP đến email hiện tại.");

            String inputOtp = view.readLine("Nhập mã OTP để xác nhận: ");

            profileService.updateContactInfo(user.getUserId(), newEmail, newPhone, inputOtp);
            view.print("✅ Cập nhật thông tin thành công!");

        } catch (Exception e) {
            view.printError(e.getMessage());
        }
    }
}
