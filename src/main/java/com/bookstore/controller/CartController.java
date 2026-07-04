package com.bookstore.controller;

import com.bookstore.model.Order;
import com.bookstore.model.OrderDetail;
import com.bookstore.model.User;
import com.bookstore.service.CartService;
import com.bookstore.view.ConsoleView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartController {
    private final ConsoleView view;
    private final CartService cartService = new CartService();

    public CartController(ConsoleView view) {
        this.view = view;
    }

    public User open(User currentUser) {
        boolean back = false;
        while (!back) {
            int choice = view.showCartMenu();
            switch (choice) {
                case 1 -> currentUser = checkout(currentUser);
                case 2 -> confirmPayment();
                case 3 -> cancelOrder();
                case 4 -> searchOrders();
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
        return currentUser;
    }

    public User checkout(User currentUser) {
        if (currentUser == null) {
            view.printError("Can dang nhap truoc khi dat hang");
            return null;
    }

    List<OrderDetail> items = collectOrderItems();
        if (items.isEmpty()) {
            view.printError("Gio hang trong");
            return currentUser;
    }

    String voucherCode = view.readLine("Nhap ma Voucher (bo trong neu khong co): ");
        if (voucherCode.isBlank()) {
            voucherCode = null;
    }

        try {
            Order order = cartService.checkout(currentUser.getUserId(), items, voucherCode);
            view.print("Dat hang thanh cong, orderId=" + order.getOrderId()  + ", tong tien=" + order.getTotalAmount());
        } catch (IllegalArgumentException | IllegalStateException | SQLException e) {
            view.printError(e.getMessage());   // Hiển thị lỗi rõ ràng theo use case
        }
    return currentUser;
}

    public void confirmPayment() {
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

    public void cancelOrder() {
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

    private List<OrderDetail> collectOrderItems() {
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

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public void addToCart(User currentUser, OrderDetail item) {
        if (currentUser == null) {
            view.printError("Can dang nhap truoc khi them vao gio hang");
            return;
        }
        try {
            Order order = cartService.checkout(currentUser.getUserId(), List.of(item), null);
            view.print("Da them vao gio hang va tao don hang moi, orderId=" + order.getOrderId() + ", tong tien=" + order.getTotalAmount());
        } catch (IllegalArgumentException | IllegalStateException | SQLException e) {
            view.printError(e.getMessage());
        }
    }
}
