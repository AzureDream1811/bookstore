package com.bookstore.controller;

import com.bookstore.model.User;
import com.bookstore.service.AuthService;
import com.bookstore.view.ConsoleView;

import java.sql.SQLException;
import java.util.List;

public class UserController {
    private final AuthService authService = new AuthService();
    private final ConsoleView view;

    public UserController(ConsoleView view) { this.view = view; }

    public void listUsers() {
        try {
            List<User> users = authService.listUsers();
            if (users.isEmpty()) {
                view.print("Chua co tai khoan nao");
                return;
            }
            for (User user : users) {
                view.print("[" + user.getUserId() + "] " + user.getFullName()
                        + " | " + user.getEmail() + " | " + user.getRole());
            }
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void changeRole() {
        int userId = view.readInt("Nhap userId: ");
        String role = view.readLine("Vai tro moi (CUSTOMER/STAFF/MANAGER): ").toUpperCase();
        try {
            authService.changeRole(userId, role);
            view.print("Da cap nhat vai tro cho userId=" + userId);
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }
}