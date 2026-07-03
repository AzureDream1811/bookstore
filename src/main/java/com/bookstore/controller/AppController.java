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
            if (currentUser == null) {
                running = handleGuestMenu();
            } else if ("CUSTOMER".equals(currentUser.getRole())) {
                running = handleCustomerMenu();
            } else {
                // STAFF hoac MANAGER dung chung 1 menu
                running = handleStaffMenu();
            }
        }
    }

    /** @return false neu nguoi dung chon "Thoat" */
    private boolean handleGuestMenu() {
        switch (view.showGuestMenu()) {
            case 1 -> currentUser = authController.login();
            case 2 -> currentUser = authController.register();
            case 0 -> { return false; }
            default -> view.printError("Lua chon khong hop le");
        }
        return true;
    }
    //menu cho khach
    private boolean handleCustomerMenu() {
        String status = "Trang thai: " + currentUser.getFullName() + " (" + currentUser.getRole() + ")";
        switch (view.showCustomerMenu(status)) {
            case 1 -> handleBookMenuCustomer(currentUser);
            case 2 -> currentUser = cartController.open(currentUser);
            case 3 -> currentUser = salesController.open(currentUser);
            case 0 -> {
                this.currentUser = null;
                view.print("Da dang xuat.");
            }
            default -> view.printError("Ban khong co quyen truy cap chuc nang nay");
        }
        return true;
    }
    //menu cho nhan vien
    private boolean handleStaffMenu() {
        String status = "Trang thai: " + currentUser.getFullName() + " (" + currentUser.getRole() + ")";
        switch (view.showStaffMenu(status)) {
            case 1 -> handleBookMenu(currentUser);
            case 2 -> handleInventoryMenu();
            case 3 -> discountController.open();
            case 4 -> currentUser = cartController.open(currentUser);
            //case 5 -> reportController.open(); khong thay open() trong reportController
            case 6 -> handleUserMenu();
            case 7 -> currentUser = salesController.open(currentUser);
            case 0 -> {
                this.currentUser = null;
                view.print("Da dang xuat.");
            }
            default -> view.printError("Ban khong co quyen truy cap chuc nang nay");
        }
        return true;
    }
    //menu search cua khach
    private void handleBookMenuCustomer(User user) {
        boolean back = false;
        while (!back) {
            switch (view.showBookMenuCustomer()) {
                case 1 -> bookController.listAll();
                case 2 -> bookController.search(user, cartController);
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
    }

    // =========================================================================
    // CÁC HÀM XỬ LÝ MENU CON ( SẼ HOÀN THIỆN LOGIC Ở ĐÂY)
    // =========================================================================

    private void handleBookMenu(User user) {
        boolean back = false;
        while (!back) {
            switch (view.showBookMenu()) {
                case 1 -> bookController.listAll();
                case 2 -> bookController.search(user, cartController);
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
                case 1 -> reportController.handleRevenueReportRequest(currentUser);
                case 0 -> back = true;
                default -> view.printError("Lựa chọn không hợp lệ");
            }
        }
    }
}


