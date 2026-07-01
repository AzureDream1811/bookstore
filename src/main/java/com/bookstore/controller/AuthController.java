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
        String fullName = view.readLine("Ho ten: ");
        String email = view.readLine("Email: ");
        String password = view.readLine("Mat khau: ");
        String role = view.readLine("Vai tro (CUSTOMER/STAFF/MANAGER) [CUSTOMER]: ");
        if (role.isBlank()) role = "CUSTOMER";
        try {
            User user = authService.register(fullName, email, password, role.toUpperCase());
            view.print("Dang ky thanh cong. userId=" + user.getUserId());
            return user;
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
            return null;
        }
    }
}
