package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.Order;
import com.bookstore.model.OrderDetail;
import com.bookstore.model.User;
import com.bookstore.service.CartService;
import com.bookstore.view.ConsoleView;

import java.sql.SQLException;
import java.util.List;

public class SalesController {
    private final ConsoleView view;
    private final CartService cartService = new CartService();

    public SalesController(ConsoleView view) {
        this.view = view;
    }

    public User open(User currentUser) {
        boolean back = false;
        while (!back) {
            view.print("");
            view.print("--- QUAN LY BAN HANG ---");
            view.print("1. Xu ly hoan tien");
            view.print("2. Doi tra san pham");
            view.print("3. Quan ly hoa don");
            view.print("0. Quay lai");
            switch (view.readInt("Chon chuc nang: ")) {
                case 1 -> processRefund();
                case 2 -> exchangeProduct();
                case 3 -> handleInvoiceMenu();
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
        return currentUser;
    }

    public void processRefund() {
    int orderId = view.readInt("Nhập orderId cần hoàn tiền: ");
    try {
        if (orderId < 1) {
            throw new IllegalArgumentException("OrderId không hợp lệ");
        }

        Order order = cartService.getInvoiceDetail(orderId);
        printInvoice(order);

        // Kiểm tra điều kiện hoàn tiền (theo diagram)
        if (!"PAID".equals(order.getStatus())) {
            view.printError("Chỉ có thể hoàn tiền cho đơn hàng đã thanh toán (PAID). Trạng thái hiện tại: " + order.getStatus());
            return;
        }

        // Kiểm tra thời hạn hoàn tiền (ví dụ: trong 7 ngày)
        if (order.getCreatedDate() != null && 
            order.getCreatedDate().isBefore(java.time.LocalDateTime.now().minusDays(7))) {
            view.printError("Đơn hàng đã quá thời hạn hoàn tiền (7 ngày).");
            return;
        }

        view.print("\n=== XÁC NHẬN HOÀN TIỀN ===");
        view.print("Sẽ hoàn toàn bộ số tiền: " + order.getTotalAmount() + " VND");
        String confirm = view.readLine("Bạn có chắc chắn muốn hoàn tiền? (Y/N): ");

        if (!confirm.equalsIgnoreCase("Y")) {
            view.print("Đã hủy yêu cầu hoàn tiền.");
            return;
        }

        // Thực hiện hoàn tiền
        double refundAmount = cartService.processRefund(orderId);
        
        view.print("Hoàn tiền thành công!");
        view.print("Số tiền đã hoàn: " + refundAmount + " VND");
        view.print("Trạng thái đơn hàng: REFUNDED");

    } catch (IllegalArgumentException | IllegalStateException | SQLException e) {
        view.printError(e.getMessage());
    }
}

    public void exchangeProduct() {
        int orderId = view.readInt("Nhap orderId can doi tra: ");
        try {
            if (orderId < 1) {
                throw new IllegalArgumentException("OrderId khong hop le");
            }
            Order order = cartService.getInvoiceDetail(orderId);
            printInvoice(order);
            if (!"PAID".equals(order.getStatus())) {
                view.printError("Chi co the doi tra san pham cho don hang da thanh toan (PAID). Trang thai hien tai: " + order.getStatus());
                return;
            }

            int oldBookId = view.readInt("BookId san pham muon tra: ");
            OrderDetail oldDetail = null;
            for (OrderDetail detail : order.getDetails()) {
                if (detail.getBookId() == oldBookId) {
                    oldDetail = detail;
                    break;
                }
            }
            if (oldDetail == null) {
                view.printError("Hoa don khong co san pham voi bookId=" + oldBookId);
                return;
            }
            int oldQuantity = view.readInt("So luong tra (toi da " + oldDetail.getQuantity() + "): ");
            if (oldQuantity <= 0 || oldQuantity > oldDetail.getQuantity()) {
                view.printError("So luong tra khong hop le");
                return;
            }

            int newBookId = view.readInt("BookId san pham muon doi den: ");
            Book newBook = cartService.getBookById(newBookId);
            if (newBook == null || !newBook.isAvailable()) {
                view.printError("San pham doi den khong kha dung");
                return;
            }
            view.print("San pham doi den: " + newBook.getTitle() + " (Gia: " + newBook.getPrice()
                    + ", Ton kho: " + newBook.getStockQuantity() + ")");
            int newQuantity = view.readInt("So luong doi den: ");
            if (newQuantity <= 0) {
                view.printError("So luong phai lon hon 0");
                return;
            }
            if (newQuantity > newBook.getStockQuantity()) {
                view.printError("San pham doi den khong du ton kho. Ton kho chi con: " + newBook.getStockQuantity());
                return;
            }

            double oldValue = oldDetail.getPrice() * oldQuantity;
            double newValue = newBook.getPrice() * newQuantity;
            double estimatedDiff = newValue - oldValue;
            view.print(String.format("Gia tri tra: %.0f | Gia tri doi den: %.0f", oldValue, newValue));
            if (estimatedDiff > 0) {
                view.print(String.format("Du kien khach can tra them: %.0f", estimatedDiff));
            } else if (estimatedDiff < 0) {
                view.print(String.format("Du kien hoan lai cho khach: %.0f", -estimatedDiff));
            } else {
                view.print("Khong phat sinh chenh lech tien.");
            }

            String confirm = view.readLine("Xac nhan doi tra? (Y/N): ");
            if (!confirm.equalsIgnoreCase("Y")) {
                view.print("Da huy doi tra.");
                return;
            }

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

    public void handleInvoiceMenu() {
        boolean back = false;
        while (!back) {
            view.print("");
            view.print("--- QUAN LY HOA DON ---");
            view.print("1. Danh sach tat ca hoa don");
            view.print("2. Tim kiem hoa don");
            view.print("3. Xem chi tiet hoa don");
            view.print("0. Quay lai");
            switch (view.readInt("Chon chuc nang: ")) {
                case 1 -> listAllInvoices();
                case 2 -> searchOrders();
                case 3 -> viewInvoiceDetail();
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
    }

    public void listAllInvoices() {
        try {
            List<Order> orders = cartService.listAllOrders();
            if (orders.isEmpty()) {
                view.print("Chua co hoa don nao");
                return;
            }
            for (Order order : orders) {
                view.print("[" + order.getOrderId() + "] user=" + order.getUserId()
                        + ", ngay tao=" + order.getCreatedDate()
                        + ", tong tien=" + order.getTotalAmount()
                        + ", trang thai=" + order.getStatus()
                        + ", voucher=" + order.getVoucherCode());
            }
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void viewInvoiceDetail() {
        int orderId = view.readInt("Nhap orderId: ");
        try {
            Order order = cartService.getInvoiceDetail(orderId);
            printInvoice(order);
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void searchOrders() {
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

    private void printInvoice(Order order) {
        view.print("Hoa don #" + order.getOrderId() + " - user=" + order.getUserId()
                + ", ngay tao=" + order.getCreatedDate()
                + ", trang thai=" + order.getStatus()
                + ", voucher=" + order.getVoucherCode()
                + ", tong tien=" + order.getTotalAmount());
        for (OrderDetail detail : order.getDetails()) {
            view.print(String.format("  - BookId: %d | SL: %d | Don gia: %.0f | Thanh tien: %.0f",
                    detail.getBookId(), detail.getQuantity(), detail.getPrice(), detail.subTotal()));
        }
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
