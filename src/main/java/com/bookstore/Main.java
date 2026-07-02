package com.bookstore;

import com.bookstore.controller.*;
import com.bookstore.view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        // 1. Khởi tạo View
        ConsoleView view = new ConsoleView();
        AuthController authController = new AuthController(view);
        BookController bookController = new BookController(view);
        InventoryController inventoryController = new InventoryController(view);
        UserController userController = new UserController(view);
        CartService cartService = new CartService();
        DiscountService discountService = new DiscountService();
        RentalService rentalService = new RentalService();
        ReportService reportService = new ReportService();

        User currentUser = null;
        boolean running = true;

        try {
            while (running) {
                view.print("");
                view.print("=== HE THONG QUAN LY NHA SACH ===");
                view.print(currentUser == null
                        ? "Trang thai: chua dang nhap"
                        : "Trang thai: " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
                view.print("1. Dang nhap");
                view.print("2. Dang ky");
                view.print("3. Quan ly sach");
                view.print("4. Quan ly kho hang");
                view.print("5. Quan ly giam gia");
                view.print("6. Quan ly gio hang / hoa don");
                view.print("7. Quan ly thue sach");
                view.print("8. Thong ke bao cao");
                view.print("9. Quan ly tai khoan nguoi dung");
                view.print("10. Quan ly ban hang");
                view.print("0. Thoat");

                int choice = view.readInt("Chon chuc nang: ");
                switch (choice) {
                    case 1 -> currentUser = authController.login();
                    case 2 -> currentUser = authController.register();
                    case 3 -> handleBookMenu(view, bookController);
                    case 4 -> handleInventoryMenu(view, inventoryController);
                    case 5 -> handleDiscountMenu(view, discountService);
                    case 6 -> currentUser = handleCartMenu(view, cartService, currentUser);
                    case 7 -> currentUser = handleRentalMenu(view, rentalService, currentUser);
                    case 8 -> handleReportMenu(view, reportService);
                    case 9 -> handleUserMenu(view, userController);
                    case 10 -> currentUser = handleSalesMenu(view, cartService, currentUser);
                    case 0 -> running = false;
                    default -> view.printError("Lua chon khong hop le");
                }
            }
        } catch (NoSuchElementException e) {
            view.printError("Khong con du lieu nhap, dung chuong trinh.");
        }
    }

    private static void handleBookMenu(ConsoleView view, BookController bookController) {
        boolean back = false;
        while (!back) {
            view.print("");
            view.print("--- QUAN LY SACH ---");
            view.print("1. Danh sach sach");
            view.print("2. Tim kiem va goi y sach");
            view.print("3. Them sach");
            view.print("4. Sua sach");
            view.print("5. An sach");
            view.print("6. Danh dau sach loi");
            view.print("0. Quay lai");
            switch (view.readInt("Chon chuc nang: ")) {
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

    private static void handleInventoryMenu(ConsoleView view, InventoryController inventoryController) {
        boolean back = false;
        while (!back) {
            view.print("");
            view.print("--- QUAN LY KHO HANG ---");
            view.print("1. Lap phieu nhap");
            view.print("2. Lap phieu xuat");
            view.print("0. Quay lai");
            switch (view.readInt("Chon chuc nang: ")) {
                case 1 -> inventoryController.createGoodsReceipt();
                case 2 -> inventoryController.createGoodsIssue();
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
    }

    private static void handleDiscountMenu(ConsoleView view, DiscountService discountService) {
        boolean back = false;
        while (!back) {
            view.print("");
            view.print("--- QUAN LY GIAM GIA ---");
            view.print("1. Danh sach combo");
            view.print("2. Tao combo");
            view.print("3. Huy combo");
            view.print("4. Tao voucher");
            view.print("5. Thong bao sale");
            view.print("0. Quay lai");
            switch (view.readInt("Chon chuc nang: ")) {
                case 1 -> listCombos(view, discountService);
                case 2 -> createCombo(view, discountService);
                case 3 -> cancelCombo(view, discountService);
                case 4 -> createVoucher(view, discountService);
                case 5 -> view.print("Thong bao: he thong da kich hoat chien dich giam gia.");
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
    }

    private static void handleReportMenu(ConsoleView view, ReportService reportService) {
        boolean back = false;
        while (!back) {
            view.print("");
            view.print("--- THONG KE BAO CAO ---");
            view.print("1. Doanh thu theo khoang ngay");
            view.print("2. San pham ban chay");
            view.print("3. Bao cao ton kho");
            view.print("4. Ve bieu do ban chay");
            view.print("5. Xuat file bao cao");
            view.print("0. Quay lai");
            switch (view.readInt("Chon chuc nang: ")) {
                case 1 -> showRevenue(view, reportService);
                case 2 -> showBestSellers(view, reportService);
                case 3 -> showLowStock(view, reportService);
                case 4 -> showChart(view, reportService);
                case 5 -> exportReport(view, reportService);
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
    }

    private static void handleUserMenu(ConsoleView view, UserController userController) {
        boolean back = false;
        while (!back) {
            view.print("");
            view.print("--- QUAN LY TAI KHOAN NGUOI DUNG ---");
            view.print("1. Danh sach tai khoan");
            view.print("2. Cap nhat vai tro");
            view.print("0. Quay lai");
            switch (view.readInt("Chon chuc nang: ")) {
                case 1 -> userController.listUsers();
                case 2 -> userController.changeRole();
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
    }

    private static User handleSalesMenu(ConsoleView view, CartService cartService, User currentUser) {
        boolean back = false;
        while (!back) {
            view.print("");
            view.print("--- QUAN LY BAN HANG ---");
            view.print("1. Xu ly hoan tien");
            view.print("2. Doi tra san pham");
            view.print("3. Quan ly hoa don");
            view.print("0. Quay lai");
            switch (view.readInt("Chon chuc nang: ")) {
                case 1 -> processRefund(view, cartService);
                case 2 -> exchangeProduct(view, cartService);
                case 3 -> handleInvoiceMenu(view, cartService);
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
        return currentUser;
    }

    private static void processRefund(ConsoleView view, CartService cartService) {
        int orderId = view.readInt("Nhap orderId can hoan tien: ");
        try {
            if (orderId < 1) {
                throw new IllegalArgumentException("OrderId khong hop le");
            }
            double refund = cartService.processRefund(orderId);
            view.print(String.format("Da hoan tien cho don %d, so tien hoan: %.0f", orderId, refund));
        } catch (IllegalArgumentException | IllegalStateException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private static void exchangeProduct(ConsoleView view, CartService cartService) {
        int orderId = view.readInt("Nhap orderId can doi tra: ");
        int oldBookId = view.readInt("BookId san pham muon tra: ");
        int oldQuantity = view.readInt("So luong tra: ");
        int newBookId = view.readInt("BookId san pham muon doi den: ");
        int newQuantity = view.readInt("So luong doi den: ");
        try {
            double priceDiff = cartService.exchangeProduct(orderId, oldBookId, oldQuantity, newBookId, newQuantity);
            if (priceDiff > 0) {
                view.print(String.format("Doi tra thanh cong. Khach can tra them: %.0f", priceDiff));
            } else if (priceDiff < 0) {
                view.print(String.format("Doi tra thanh cong. Hoan lai cho khach: %.0f", -priceDiff));
            } else {
                view.print("Doi tra thanh cong. Khong phat sinh chenh lech tien.");
            }
        } catch (IllegalArgumentException | IllegalStateException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private static void handleInvoiceMenu(ConsoleView view, CartService cartService) {
        boolean back = false;
        while (!back) {
            view.print("");
            view.print("--- QUAN LY HOA DON ---");
            view.print("1. Tim kiem hoa don");
            view.print("2. Xem chi tiet hoa don");
            view.print("0. Quay lai");
            switch (view.readInt("Chon chuc nang: ")) {
                case 1 -> searchOrders(view, cartService);
                case 2 -> viewInvoiceDetail(view, cartService);
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
    }

    private static void viewInvoiceDetail(ConsoleView view, CartService cartService) {
        int orderId = view.readInt("Nhap orderId: ");
        try {
            Order order = cartService.getInvoiceDetail(orderId);
            view.print("Hoa don #" + order.getOrderId() + " - user=" + order.getUserId()
                    + ", trang thai=" + order.getStatus()
                    + ", voucher=" + order.getVoucherCode()
                    + ", tong tien=" + order.getTotalAmount());
            for (OrderDetail d : order.getDetails()) {
                view.print(String.format("  - BookId: %d | SL: %d | Don gia: %.0f | Thanh tien: %.0f",
                        d.getBookId(), d.getQuantity(), d.getPrice(), d.subTotal()));
            }
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private static User handleCartMenu(ConsoleView view, CartService cartService, User currentUser) {
        boolean back = false;
        while (!back) {
            view.print("");
            view.print("--- GIO HANG / HOA DON ---");
            view.print("1. Dat hang");
            view.print("2. Xac nhan thanh toan");
            view.print("3. Huy don / hoan tien");
            view.print("4. Tim kiem hoa don");
            view.print("0. Quay lai");
            switch (view.readInt("Chon chuc nang: ")) {
                case 1 -> currentUser = checkout(view, cartService, currentUser);
                case 2 -> confirmPayment(view, cartService);
                case 3 -> cancelOrder(view, cartService);
                case 4 -> searchOrders(view, cartService);
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
        return currentUser;
    }

    private static User handleRentalMenu(ConsoleView view, RentalService rentalService, User currentUser) {
        boolean back = false;
        while (!back) {
            view.print("");
            view.print("--- QUAN LY THUE SACH ---");
            view.print("1. Thue sach");
            view.print("2. Tra sach");
            view.print("3. Tim kiem phieu thue");
            view.print("4. Canh bao qua han tra sach");
            view.print("0. Quay lai");
            switch (view.readInt("Chon chuc nang: ")) {
                case 1 -> currentUser = rentBook(view, rentalService, currentUser);
                case 2 -> returnBook(view, rentalService);
                case 3 -> searchRentals(view, rentalService);
                case 4 -> showOverdueRentals(view, rentalService);
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
        return currentUser;
    }

    private static void listCombos(ConsoleView view, DiscountService discountService) {
        try {
            List<Combo> combos = discountService.listCombos();
            if (combos.isEmpty()) {
                view.print("Chua co combo nao");
                return;
            }
            for (Combo combo : combos) {
                view.print(formatCombo(combo));
            }
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private static void createCombo(ConsoleView view, DiscountService discountService) {
        try {
            String name = view.readLine("Ten combo: ");
            requireText(name, "Ten combo khong duoc de trong");
            double price = view.readDouble("Gia combo: ");
            if (price < 0) {
                throw new IllegalArgumentException("Gia combo khong hop le");
            }
            LocalDate startDate = readDate(view, "Ngay bat dau (yyyy-MM-dd): ");
            LocalDate endDate = readDate(view, "Ngay ket thuc (yyyy-MM-dd): ");
            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("Ngay ket thuc phai sau hoac bang ngay bat dau");
            }
            List<ComboDetail> items = collectComboItems(view);
            Combo combo = discountService.createCombo(name, price, startDate, endDate, items);
            view.print("Tao combo thanh cong, comboId=" + combo.getComboId());
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private static void cancelCombo(ConsoleView view, DiscountService discountService) {
        int comboId = view.readInt("Nhap comboId can huy: ");
        try {
            discountService.cancelCombo(comboId);
            view.print("Da huy combo id=" + comboId);
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private static void createVoucher(ConsoleView view, DiscountService discountService) {
        try {
            String code = view.readLine("Ma voucher: ");
            requireText(code, "Ma voucher khong duoc de trong");
            double discountValue = view.readDouble("Gia tri giam: ");
            double minOrderAmount = view.readDouble("Don toi thieu: ");
            if (discountValue <= 0 || minOrderAmount < 0) {
                throw new IllegalArgumentException("Gia tri voucher khong hop le");
            }
            LocalDate expiryDate = readDate(view, "Ngay het han (yyyy-MM-dd): ");
            discountService.createVoucher(code, discountValue, minOrderAmount, expiryDate);
            view.print("Tao voucher thanh cong");
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private static void showRevenue(ConsoleView view, ReportService reportService) {
        String fromDate = view.readLine("Tu ngay (yyyy-MM-dd hoac yyyy-MM-dd HH:mm:ss): ");
        String toDate = view.readLine("Den ngay (yyyy-MM-dd hoac yyyy-MM-dd HH:mm:ss): ");
        try {
            requireText(fromDate, "Tu ngay khong duoc de trong");
            requireText(toDate, "Den ngay khong duoc de trong");
            double revenue = reportService.revenueBetween(fromDate, toDate);
            view.print(String.format("Doanh thu: %.0f", revenue));
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private static void showBestSellers(ConsoleView view, ReportService reportService) {
        int limit = view.readInt("So luong top san pham: ");
        try {
            if (limit < 1) {
                throw new IllegalArgumentException("So luong top san pham phai lon hon 0");
            }
            List<Object[]> items = reportService.bestSellers(limit);
            if (items.isEmpty()) {
                view.print("Chua co du lieu ban chay");
                return;
            }
            for (Object[] item : items) {
                view.print("[" + item[0] + "] " + item[1] + " - da ban: " + item[2]);
            }
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private static void showLowStock(ConsoleView view, ReportService reportService) {
        int threshold = view.readInt("Nguong ton kho toi da: ");
        try {
            if (threshold < 0) {
                throw new IllegalArgumentException("Nguong ton kho khong hop le");
            }
            List<Book> books = reportService.lowStockBooks(threshold);
            if (books.isEmpty()) {
                view.print("Khong co sach can canh bao ton kho");
                return;
            }
            for (Book book : books) {
                view.print(book.toString());
            }
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private static void showChart(ConsoleView view, ReportService reportService) {
        int limit = view.readInt("So luong top san pham de ve bieu do: ");
        try {
            if (limit < 1) {
                throw new IllegalArgumentException("So luong top san pham phai lon hon 0");
            }
            view.print(reportService.bestSellersChart(limit));
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private static void exportReport(ConsoleView view, ReportService reportService) {
        String fromDate = view.readLine("Tu ngay: ");
        String toDate = view.readLine("Den ngay: ");
        String filePath = view.readLine("Duong dan file xuat (vd: revenue.csv): ");
        try {
            requireText(fromDate, "Tu ngay khong duoc de trong");
            requireText(toDate, "Den ngay khong duoc de trong");
            requireText(filePath, "Duong dan file khong duoc de trong");
            reportService.exportRevenueCsv(fromDate, toDate, filePath);
            view.print("Da xuat bao cao ra file: " + filePath);
        } catch (IllegalArgumentException | SQLException | java.io.IOException e) {
            view.printError(e.getMessage());
        }
    }

    private static User checkout(ConsoleView view, CartService cartService, User currentUser) {
        if (currentUser == null) {
            view.printError("Can dang nhap truoc khi dat hang");
            return null;
        }
        List<OrderDetail> items = collectOrderItems(view);
        if (items.isEmpty()) {
            view.printError("Gio hang trong");
            return currentUser;
        }
        String voucherCode = view.readLine("Voucher (bo trong neu khong co): ");
        if (voucherCode.isBlank()) {
            voucherCode = null;
        }
        try {
            Order order = cartService.checkout(currentUser.getUserId(), items, voucherCode);
            view.print("Dat hang thanh cong, orderId=" + order.getOrderId() + ", tong tien=" + order.getTotalAmount());
        } catch (IllegalArgumentException | IllegalStateException | SQLException e) {
            view.printError(e.getMessage());
        }
        return currentUser;
    }

    private static void confirmPayment(ConsoleView view, CartService cartService) {
        int orderId = view.readInt("Nhap orderId: ");
        String method = view.readLine("Phuong thuc thanh toan: ");
        try {
            requireText(method, "Phuong thuc thanh toan khong duoc de trong");
            cartService.confirmPayment(orderId, method);
            view.print("Da xac nhan thanh toan cho don " + orderId);
        } catch (SQLException | IllegalArgumentException e) {
            view.printError(e.getMessage());
        }
    }

    private static void cancelOrder(ConsoleView view, CartService cartService) {
        int orderId = view.readInt("Nhap orderId can huy: ");
        try {
            if (orderId < 1) {
                throw new IllegalArgumentException("OrderId khong hop le");
            }
            double refund = cartService.cancelOrder(orderId);
            view.print(String.format("Da huy don %d, so tien hoan: %.0f", orderId, refund));
        } catch (SQLException | IllegalArgumentException e) {
            view.printError(e.getMessage());
        }
    }

    private static void searchOrders(ConsoleView view, CartService cartService) {
        String criteria = view.readLine("Tu khoa tim kiem hoa don: ");
        try {
            requireText(criteria, "Tu khoa tim kiem khong duoc de trong");
            List<Order> orders = cartService.searchOrders(criteria);
            if (orders.isEmpty()) {
                view.print("Khong tim thay hoa don");
                return;
            }
            for (Order order : orders) {
                view.print("[" + order.getOrderId() + "] user=" + order.getUserId()
                        + ", tong tien=" + order.getTotalAmount()
                        + ", trang thai=" + order.getStatus()
                        + ", voucher=" + order.getVoucherCode());
            }
        } catch (SQLException | IllegalArgumentException e) {
            view.printError(e.getMessage());
        }
    }

    private static User rentBook(ConsoleView view, RentalService rentalService, User currentUser) {
        if (currentUser == null) {
            view.printError("Can dang nhap truoc khi thue sach");
            return null;
        }
        int bookId = view.readInt("Nhap bookId: ");
        int days = view.readInt("So ngay thue: ");
        try {
            if (bookId < 1) {
                throw new IllegalArgumentException("BookId khong hop le");
            }
            Rental rental = rentalService.rentBook(currentUser.getUserId(), bookId, days);
            view.print("Thue sach thanh cong, rentalId=" + rental.getRentalId());
        } catch (IllegalArgumentException | IllegalStateException | SQLException e) {
            view.printError(e.getMessage());
        }
        return currentUser;
    }

    private static void returnBook(ConsoleView view, RentalService rentalService) {
        int rentalId = view.readInt("Nhap rentalId: ");
        try {
            if (rentalId < 1) {
                throw new IllegalArgumentException("RentalId khong hop le");
            }
            double lateFee = rentalService.returnBook(rentalId);
            view.print(String.format("Tra sach thanh cong. Phi tre han: %.0f", lateFee));
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private static void searchRentals(ConsoleView view, RentalService rentalService) {
        String keyword = view.readLine("Tu khoa tim kiem phieu thue: ");
        try {
            requireText(keyword, "Tu khoa tim kiem khong duoc de trong");
            List<Rental> rentals = rentalService.search(keyword);
            if (rentals.isEmpty()) {
                view.print("Khong tim thay phieu thue");
                return;
            }
            for (Rental rental : rentals) {
                view.print("[" + rental.getRentalId() + "] user=" + rental.getUserId()
                        + ", book=" + rental.getBookId()
                        + ", ngay thue=" + rental.getRentDate()
                        + ", so ngay=" + rental.getDays()
                        + ", trang thai=" + rental.getStatus());
            }
        } catch (SQLException | IllegalArgumentException e) {
            view.printError(e.getMessage());
        }
    }

    private static void showOverdueRentals(ConsoleView view, RentalService rentalService) {
        try {
            List<Rental> rentals = rentalService.listOverdueRentals();
            if (rentals.isEmpty()) {
                view.print("Khong co phieu thue qua han");
                return;
            }
            for (Rental rental : rentals) {
                view.print("[" + rental.getRentalId() + "] user=" + rental.getUserId()
                        + ", book=" + rental.getBookId()
                        + ", ngay thue=" + rental.getRentDate()
                        + ", so ngay=" + rental.getDays());
            }
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private static List<OrderDetail> collectOrderItems(ConsoleView view) {
        List<OrderDetail> items = new ArrayList<>();
        view.print("Nhap danh sach san pham (bookId=0 de ket thuc)");
        while (true) {
            int bookId = view.readInt("BookId: ");
            if (bookId == 0) {
                break;
            }
            int quantity = view.readInt("So luong: ");
            items.add(new OrderDetail(0, bookId, quantity, 0));
        }
        return items;
    }

    private static List<ComboDetail> collectComboItems(ConsoleView view) {
        List<ComboDetail> items = new ArrayList<>();
        view.print("Nhap danh sach sach trong combo (bookId=0 de ket thuc)");
        while (true) {
            int bookId = view.readInt("BookId: ");
            if (bookId == 0) {
                break;
            }
            int quantity = view.readInt("So luong: ");
            items.add(new ComboDetail(0, bookId, quantity));
        }
        return items;
    }

    private static LocalDate readDate(ConsoleView view, String prompt) {
        while (true) {
            String input = view.readLine(prompt);
            try {
                requireText(input, "Ngay khong duoc de trong");
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                view.printError("Ngay khong hop le, dung dinh dang yyyy-MM-dd");
            }
        }
    }


        // 2. Khởi tạo các Controller chức năng
        AuthController authController = new AuthController(view);
//        ReportController reportController = new ReportController(view);
        // (Khởi tạo các Controller khác ở đây...)


        // 3. Khởi tạo Controller Tổng
        AppController app = new AppController(view, authController/*, reportController*/);

        // 4. Bắt đầu chạy chương trình
        app.start();
    }
}