package com.bookstore.view;

import com.bookstore.model.*;
import com.bookstore.service.CartService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SwingPaymentView extends JFrame {
    private final CartService cartService = new CartService();
    private User currentCustomer;
    private JTextArea orderSummaryArea;
    private JComboBox<PaymentMethod> methodCombo;
    private Order selectedOrder;

    public SwingPaymentView(User customer) {
        this.currentCustomer = customer;
        initUI();
    }

    private void initUI() {
        setTitle("Thanh Toan - Bookstore");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel header = new JLabel("💳 THANH TOAN DON HANG", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        add(header, BorderLayout.NORTH);

        // Order Summary
        orderSummaryArea = new JTextArea(10, 40);
        orderSummaryArea.setEditable(false);
        add(new JScrollPane(orderSummaryArea), BorderLayout.CENTER);

        // Payment Method
        JPanel methodPanel = new JPanel(new FlowLayout());
        methodPanel.add(new JLabel("Phuong thuc thanh toan:"));
        methodCombo = new JComboBox<>(PaymentMethod.values());
        methodPanel.add(methodCombo);
        add(methodPanel, BorderLayout.SOUTH);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton payBtn = new JButton("Thanh Toan Ngay");
        JButton cancelBtn = new JButton("Huy");

        payBtn.addActionListener(this::handlePayment);
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(payBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.PAGE_END);

        loadPendingOrders();
    }

    private void loadPendingOrders() {
        try {
            List<Order> pendingOrders = cartService.getPendingOrdersForUser(currentCustomer.getUserId());
            if (pendingOrders.isEmpty()) {
                orderSummaryArea.setText("Khong co don hang cho thanh toan nao cho tai khoan nay.");
                selectedOrder = null;
                return;
            }

            // Hiển thị tóm tắt
            StringBuilder summary = new StringBuilder("=== DON HANG CHO THANH TOAN ===\n\n");
            for (Order o : pendingOrders) {
                summary.append(String.format("Ma don: %d | Tong: %, .0f VND | Trang thai: %s\n",
                        o.getOrderId(), o.getTotalAmount(), o.getStatus()));
            }
            summary.append("\nSe thanh toan don hang moi nhat (ID cao nhat).");
            orderSummaryArea.setText(summary.toString());

            // Chọn đơn mới nhất
            selectedOrder = pendingOrders.stream()
                    .max((a, b) -> Integer.compare(a.getOrderId(), b.getOrderId()))
                    .orElse(null);

        } catch (Exception e) {
            orderSummaryArea.setText("Loi tai don hang: " + e.getMessage());
            selectedOrder = null;
        }
    }

    private void handlePayment(ActionEvent e) {
        try {
            if (selectedOrder == null) {
                JOptionPane.showMessageDialog(this, "Khong co don hang cho thanh toan!", "Loi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!"PENDING".equals(selectedOrder.getStatus())) {
                JOptionPane.showMessageDialog(this, "Don hang khong o trang thai cho thanh toan!", "Loi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PaymentMethod method = (PaymentMethod) methodCombo.getSelectedItem();

            cartService.confirmPayment(selectedOrder.getOrderId(), method.name());

            JOptionPane.showMessageDialog(this, 
                "Thanh toan thanh cong cho don #" + selectedOrder.getOrderId() + 
                "\nPhuong thuc: " + method.getDisplayName() + 
                "\nTong tien: " + String.format("%,.0f", selectedOrder.getTotalAmount()) + " VND", 
                "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Loi thanh toan: " + ex.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
