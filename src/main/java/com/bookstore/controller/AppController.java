package com.bookstore.controller;

import com.bookstore.model.User;
import com.bookstore.view.ConsoleView;

public class AppController {
    private final ConsoleView view;
    private final AuthController authController;
    private final BookController bookController;
    private final InventoryController inventoryController;
    private final UserController userController;
    private final DiscountController discountController;
    private final CartController cartController;
    private final RentalController rentalController;
    private final ReportController reportController;
    private final SalesController salesController;




    // TODO: 1. Khai báo thêm các Controller/Service khác ở đây
    // VD: private BookController bookController;
    //     private CartService cartService;
    //     ...

    private User currentUser = null;

    // TODO: 2. Nhớ truyền thêm các Controller đó vào constructor này
    public AppController(ConsoleView view, AuthController authController, ReportController reportController) {
        this.view = view;
        this.authController = authController;
        this.reportController = reportController;
        this.bookController = new BookController(view);
        this.inventoryController = new InventoryController(view);
        this.userController = new UserController(view);
        this.discountController = new DiscountController(view);
        this.cartController = new CartController(view);
        this.rentalController = new RentalController(view);
        this.salesController = new SalesController(view);
    }

    public void start() {
        boolean running = true;
        while (running) {
            String status = currentUser == null
                    ? "Trang thai: chua dang nhap"
                    : "Trang thai: " + currentUser.getFullName();

            switch (view.showMainMenu(status)) {
                case 1 -> currentUser = authController.login();
                case 2 -> currentUser = authController.register();
                case 3 -> handleBookMenu();
                case 4 -> handleInventoryMenu();
                case 5 -> discountController.open();
                case 6 -> currentUser = cartController.open(currentUser);
                case 7 -> currentUser = rentalController.open(currentUser);
                case 8 -> handleReportMenu();
                case 9 -> handleUserMenu();
                case 10 -> currentUser = salesController.open(currentUser);
                case 0 -> running = false;
                default -> view.printError("Lua chon khong hop le");
            }
        }
    }

    // =========================================================================
    // CÁC HÀM XỬ LÝ MENU CON ( SẼ HOÀN THIỆN LOGIC Ở ĐÂY)
    // =========================================================================

    private void handleBookMenu() {
        boolean back = false;
        while (!back) {
            switch (view.showBookMenu()) {
                case 1 -> bookController.listAll();
                case 2 -> bookController.search();
                case 3 -> bookController.addBook();
                case 4 -> bookController.editBook();
                case 5 -> bookController.hideBook();
                case 6 -> bookController.markFaulty();
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
    }

    private void handleInventoryMenu() {
        boolean back = false;
        while (!back) {
            switch (view.showInventoryMenu()) {
                case 1 -> inventoryController.createGoodsReceipt();
                case 2 -> inventoryController.createGoodsIssue();
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
    }

    private void handleUserMenu() {
        boolean back = false;
        while (!back) {
            switch (view.showUserMenu()) {
                case 1 -> userController.listUsers();
                case 2 -> userController.changeRole();
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
    }

    private void handleCartMenu() {
        currentUser = cartController.open(currentUser);
    }


    private void handleReportMenu() {
        boolean back = false;
        while (!back) {
            int choice = view.showReportMenu();
            switch (choice) {
                case 1 -> reportController.handleRevenueReportRequest();
                // TODO (Bạn của bạn): 7. Bổ sung các báo cáo khác ở đây nếu có
                case 0 -> back = true;
                default -> view.printError("Lựa chọn không hợp lệ");
            }
        }
    }
}


