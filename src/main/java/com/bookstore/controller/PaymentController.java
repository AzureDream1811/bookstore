package com.bookstore.controller;

import com.bookstore.model.*;
import com.bookstore.view.PaymentView;
import java.util.List;
import java.util.Scanner;

public class PaymentController {

    private final CartController cartController;
    private final OrderController orderController; // Giả sử bạn đã có hoặc sẽ thêm
    private final PaymentView paymentView;

    public PaymentController() {
        this.cartController = new CartController();
        this.orderController = new OrderController();
        this.paymentView = new PaymentView();
    }

    /**
     * Main flow thanh toán
     */
    public void processPayment(Customer customer) {
        // 1. Xác nhận đơn hàng
        Order order = orderController.getCurrentOrder(customer);
        if (order == null || order.getItems().isEmpty()) {
            paymentView.showMessage("Giỏ hàng trống! Không thể thanh toán.");
            return;
        }

        paymentView.showOrderSummary(order);

        // 2. Chọn phương thức thanh toán
        PaymentMethod method = choosePaymentMethod();

        // 3. Xử lý theo phương thức
        boolean success = false;
        switch (method) {
            case CASH_ON_DELIVERY:
                success = handleCashOnDelivery(order, customer);
                break;
            case ONLINE_BANKING:
            case MOMO:
            case VNPAY:
                success = handleOnlinePayment(order, customer, method);
                break;
        }

        if (success) {
            // Tạo hóa đơn
            Invoice invoice = generateInvoice(order, method);
            paymentView.showInvoice(invoice);
            
            // Cập nhật trạng thái
            order.setStatus(OrderStatus.PAID);
            orderController.saveOrder(order);
            
            // Xóa giỏ hàng
            cartController.clearCart(customer);
            
            paymentView.showMessage("Thanh toán thành công! Cảm ơn quý khách.");
        } else {
            paymentView.showError("Thanh toán thất bại. Vui lòng thử lại sau.");
        }
    }

    private PaymentMethod choosePaymentMethod() {
        paymentView.showPaymentMethods();
        int choice = paymentView.getUserChoice(1, 4); // 4 phương thức

        switch (choice) {
            case 1: return PaymentMethod.CASH_ON_DELIVERY;
            case 2: return PaymentMethod.ONLINE_BANKING;
            case 3: return PaymentMethod.MOMO;
            case 4: return PaymentMethod.VNPAY;
            default: return PaymentMethod.CASH_ON_DELIVERY;
        }
    }

    private boolean handleCashOnDelivery(Order order, Customer customer) {
        paymentView.showMessage("Bạn đã chọn thanh toán khi nhận hàng (COD).");
        // Logic gửi thông báo cho shipper (có thể tích hợp email/SMS sau)
        return true;
    }

    private boolean handleOnlinePayment(Order order, Customer customer, PaymentMethod method) {
        paymentView.showMessage("Đang chuyển hướng đến cổng thanh toán " + method.getDisplayName() + "...");
        
        // Giả lập thanh toán online (thực tế integrate API VNPay, Momo, ...)
        String transactionId = "TXN_" + System.currentTimeMillis();
        boolean paid = simulateOnlinePayment(order.getTotalAmount());
        
        if (paid) {
            order.setTransactionId(transactionId);
            order.setPaymentMethod(method);
            paymentView.showMessage("Thanh toán " + method.getDisplayName() + " thành công. Mã giao dịch: " + transactionId);
            return true;
        }
        return false;
    }

    private boolean simulateOnlinePayment(double amount) {
        // TODO: Thay bằng API thật
        paymentView.showMessage("Giả lập thanh toán " + amount + " VND...");
        return true; // Giả sử thành công 100% trong demo
    }

    private Invoice generateInvoice(Order order, PaymentMethod method) {
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setPaymentMethod(method);
        invoice.setInvoiceDate(new java.util.Date());
        invoice.setTotalAmount(order.getTotalAmount());
        return invoice;
    }
}
