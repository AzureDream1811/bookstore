package com.bookstore.controller;

import com.bookstore.model.User;
import com.bookstore.view.ConsoleView;
import com.bookstore.controller.ComboController;

public class AppController {
    private ConsoleView view;
    private AuthController authController;
    //private ReportController reportController;

    // TODO: 1. Khai báo thêm các Controller/Service khác ở đây
    // VD: private BookController bookController;
    //     private CartService cartService;
    //     ...
    private ComboController comboController;

    private User currentUser = null;

    // TODO: 2. Nhớ truyền thêm các Controller đó vào constructor này
    public AppController(ConsoleView view, AuthController authController, ComboController comboController/*, ReportController reportController*/) {
        this.view = view;
        this.authController = authController;
        this.comboController = comboController;
//        this.reportController = reportController;
    }

    public void start() {
        boolean running = true;
        while (running) {
            String status = (currentUser == null) ? "Trạng thái: chưa đăng nhập"
                    : "Trạng thái: " + currentUser.getFullName();

            // Gọi View để hiển thị menu và lấy số lựa chọn
            int choice = view.showMainMenu(status);

            // Logic rẽ nhánh
            switch (choice) {
                case 1 -> currentUser = authController.login();
                case 2 -> currentUser = authController.register();
                case 3 -> handleBookMenu();
                case 4 -> handleInventoryMenu();
                case 5 -> handleDiscountMenu();
                case 6 -> handleCartMenu();
                case 7 -> view.printError("Tính năng thuê/mượn sách không được hỗ trợ trong phiên bản dự án này."); // Đã chốt loại bỏ tính năng mượn sách
                case 8 -> handleReportMenu();
                case 9 -> handleUserMenu();
                case 0 -> running = false;
                default -> view.printError("Lựa chọn không hợp lệ");
            }
        }
    }

    // =========================================================================
    // CÁC HÀM XỬ LÝ MENU CON ( SẼ HOÀN THIỆN LOGIC Ở ĐÂY)
    // =========================================================================

    private void handleBookMenu() {
        boolean back = false;
        while (!back) {
            int choice = view.showBookMenu();
            switch (choice) {
                // TODO: 3. Gọi các hàm của bookController tại đây
                // VD: case 1 -> bookController.listAll();
                case 0 -> back = true;
                default -> view.printError("Lựa chọn không hợp lệ");
            }
        }
    }

    private void handleInventoryMenu() {
        boolean back = false;
        while (!back) {
            int choice = view.showInventoryMenu();
            switch (choice) {
                // TODO : 4. Gọi các hàm của inventoryController tại đây
                // VD: case 1 -> inventoryController.createGoodsReceipt();
                case 0 -> back = true;
                default -> view.printError("Lựa chọn không hợp lệ");
            }
        }
    }

    private void handleDiscountMenu() {
        boolean back = false;
        while (!back) {
            int choice = view.showDiscountMenu();
            switch (choice) {
                case 1 -> comboController.listCombos();
                case 2 -> comboController.createCombo();
                case 3 -> comboController.cancelCombo();
                case 4 -> comboController.updateCombo();
                // case 5 -> Tao voucher: can them discountService/voucherController
                // case 6 -> Thong bao sale
                case 0 -> back = true;
                default -> view.printError("Lựa chọn không hợp lệ");
            }
        }
    }

    private void handleCartMenu() {
        boolean back = false;
        while (!back) {
            int choice = view.showCartMenu();
            switch (choice) {
                // TODO : 6. Gọi các hàm của cartService tại đây.
                // Lưu ý truyền currentUser.getUserId() nếu chức năng cần user đang đăng nhập
                // VD: case 1 -> currentUser = checkout(...);
                case 0 -> back = true;
                default -> view.printError("Lựa chọn không hợp lệ");
            }
        }
    }

    private void handleReportMenu() {
        boolean back = false;
        while (!back) {
            int choice = view.showReportMenu();
            switch (choice) {
//                case 1 -> reportController.handleRevenueReportRequest();
                // TODO : 7. Bổ sung các báo cáo khác ở đây nếu có
                case 0 -> back = true;
                default -> view.printError("Lựa chọn không hợp lệ");
            }
        }
    }

    private void handleUserMenu() {
        boolean back = false;
        while (!back) {
            int choice = view.showUserMenu();
            switch (choice) {
                // TODO : 8. Gọi các hàm của userController tại đây
                // VD: case 1 -> userController.listUsers();
                case 0 -> back = true;
                default -> view.printError("Lựa chọn không hợp lệ");
            }
        }
    }
}