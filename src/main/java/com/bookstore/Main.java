package com.bookstore;

import com.bookstore.controller.*;
import com.bookstore.view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        ConsoleView view = new ConsoleView();
        AuthController authController = new AuthController(view);
        ReportController reportController = new ReportController(view);
        AppController app = new AppController(view, authController,reportController);
        app.start();
    }
}