package com.bookstore.controller;

import com.bookstore.model.*;
import com.bookstore.service.CartService;
import com.bookstore.view.SwingPaymentView;   // Sử dụng Swing
import com.bookstore.view.ConsoleView;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class PaymentController {

    private final CartService cartService = new CartService();
    private final ConsoleView consoleView;

    public PaymentController(ConsoleView consoleView) {
        this.consoleView = consoleView;
    }

    /**
     * Main flow thanh toán - Sử dụng Swing UI
     */
    public void processPayment(User customer) {
        if (customer == null) {
            JOptionPane.showMessageDialog(null, 
                "Cần đăng nhập trước khi thanh toán!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mở giao diện Swing Payment
        SwingUtilities.invokeLater(() -> {
            new SwingPaymentView(customer).setVisible(true);
        });
    }
}
