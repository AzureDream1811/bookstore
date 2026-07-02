package com.bookstore;

import com.bookstore.controller.*;
import com.bookstore.view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        // 1. Khởi tạo View
        ConsoleView view = new ConsoleView();

        // 2. Khởi tạo các Controller chức năng
        AuthController authController = new AuthController(view);
        ReportController reportController = new ReportController(view);
        // (Khởi tạo các Controller khác ở đây...)

        // 3. Khởi tạo Controller Tổng
        AppController app = new AppController(view, authController, reportController);

        // 4. Bắt đầu chạy chương trình
        app.start();
    }
}