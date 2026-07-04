package com.bookstore.controller;

import com.bookstore.model.Order;
import com.bookstore.model.User;
import com.bookstore.service.CartService;
import com.bookstore.view.ConsoleView;

import java.sql.SQLException;
import java.util.List;

public class PaymentController {

    private final CartService cartService = new CartService();
    private final ConsoleView view;

    public PaymentController(ConsoleView view) {
        this.view = view;
    }

    /**
     * Thanh toán qua Console (không dùng Swing nữa)
     */
    public void processPayment(User customer) {
        if (customer == null) {
            view.printError("Cần đăng nhập trước khi thanh toán!");
            return;
        }

        try {
            List<Order> pendingOrders = cartService.getPendingOrdersForUser(customer.getUserId());

            if (pendingOrders.isEmpty()) {
                view.print("Bạn không có đơn hàng nào đang chờ thanh toán.");
                return;
            }

            // Hiển thị danh sách đơn chờ thanh toán
            view.print("\n=== DANH SÁCH ĐƠN HÀNG CHỜ THANH TOÁN ===");
            for (Order order : pendingOrders) {
                view.print(String.format("Mã đơn: %d | Tổng tiền: %, .0f VND | Trạng thái: %s", 
                        order.getOrderId(), order.getTotalAmount(), order.getStatus()));
            }

            // Chọn đơn hàng
            int orderId = view.readInt("\nNhập mã đơn hàng muốn thanh toán (0 để hủy): ");
            if (orderId == 0) {
                view.print("Đã hủy thanh toán.");
                return;
            }

            // Kiểm tra đơn hàng có tồn tại và thuộc user
            Order selectedOrder = null;
            for (Order o : pendingOrders) {
                if (o.getOrderId() == orderId) {
                    selectedOrder = o;
                    break;
                }
            }

            if (selectedOrder == null) {
                view.printError("Không tìm thấy đơn hàng hoặc đơn hàng không thuộc về bạn.");
                return;
            }

            // Chọn phương thức thanh toán
            view.print("\n--- Chọn phương thức thanh toán ---");
            view.print("1. Tiền mặt khi nhận hàng (COD)");
            view.print("2. Chuyển khoản ngân hàng");
            view.print("3. Ví MoMo");
            view.print("4. VNPay");
            int methodChoice = view.readInt("Chọn phương thức (1-4): ");

            String method = switch (methodChoice) {
                case 1 -> "COD";
                case 2 -> "BANK_TRANSFER";
                case 3 -> "MOMO";
                case 4 -> "VNPAY";
                default -> "COD";
            };

            // Xác nhận thanh toán
            view.print(String.format("\nXác nhận thanh toán đơn #%d - Tổng: %, .0f VND?", 
                    selectedOrder.getOrderId(), selectedOrder.getTotalAmount()));
            String confirm = view.readLine("Nhập 'Y' để xác nhận: ");

            if (confirm.equalsIgnoreCase("Y")) {
                cartService.confirmPayment(selectedOrder.getOrderId(), method);
                view.print("✅ Thanh toán thành công! Cảm ơn bạn đã mua hàng.");
            } else {
                view.print("Đã hủy thanh toán.");
            }

        } catch (SQLException e) {
            view.printError("Lỗi hệ thống: " + e.getMessage());
        } catch (Exception e) {
            view.printError("Lỗi: " + e.getMessage());
        }
    }
    public boolean processLateFee(double amount) {

        view.print("\n=== THANH TOAN PHI PHAT ===");
        view.print("So tien can thanh toan: " + amount);

        view.print("1. COD");
        view.print("2. Chuyen khoan");
        view.print("3. MoMo");
        view.print("4. VNPay");

        int choice = view.readInt("Chon phuong thuc: ");

        String confirm = view.readLine("Nhap Y de xac nhan: ");

        if (confirm.equalsIgnoreCase("Y")) {
            view.print("Thanh toan thanh cong.");
            return true;
        }

        view.print("Da huy thanh toan.");
        return false;
    }
}
