package com.bookstore.controller;

import com.bookstore.model.User;
import com.bookstore.service.AuthService;
import com.bookstore.view.ConsoleView;

import java.sql.SQLException;

public class AuthController {
    private final AuthService authService = new AuthService();
    private final ConsoleView view;

    public AuthController(ConsoleView view) { this.view = view; }

    public User login() {
        String email = view.readLine("Email: ");
        String password = view.readLine("Mat khau: ");
        try {
            User user = authService.login(email, password);
            view.print("Dang nhap thanh cong. Xin chao " + user.getFullName() + " (" + user.getRole() + ")");
            return user;
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
            return null;
        }
    }

    public User register() {

        String fullName = view.readLine("Ho ten: ").trim();
        String email = view.readLine("Email: ").trim();
        String password = view.readLine("Mat khau: ");
        String confirmPassword = view.readLine("Nhap lai mat khau: ");

        if (fullName.isEmpty()) {
            view.printError("Ho ten khong duoc de trong.");
            return null;
        }

        if (email.isEmpty()) {
            view.printError("Email khong duoc de trong.");
            return null;
        }

        if (!password.equals(confirmPassword)) {
            view.printError("Mat khau xac nhan khong dung.");
            return null;
        }

        try {

            User user = authService.register(
                    fullName,
                    email,
                    password,
                    "CUSTOMER"
            );

            view.print("Dang ky thanh cong!");
            view.print("Ma OTP da duoc gui.");

            String otp = view.readLine("Nhap ma OTP: ");

            if (authService.verify(email, otp)) {
                view.print("Xac thuc tai khoan thanh cong!");
                return user;
            } else {
                view.printError("Sai ma OTP.");
                return null;
            }

        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
            return null;
        }
    }
}