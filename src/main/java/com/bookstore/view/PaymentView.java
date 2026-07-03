package com.bookstore.view;

import com.bookstore.model.*;
import javax.swing.*;
import java.awt.*;

public class PaymentView extends JFrame {

    public void showPaymentMethods() {
        String[] methods = {
            "1. Thanh toán khi nhận hàng (COD)",
            "2. Thanh toán Online qua Ngân hàng",
            "3. Ví MoMo",
            "4. Ví VNPay"
        };
        JOptionPane.showMessageDialog(this,
            String.join("\n", methods),
            "Chọn phương thức thanh toán",
            JOptionPane.QUESTION_MESSAGE);
    }

    public int getUserChoice(int min, int max) {
        String input = JOptionPane.showInputDialog(this,
            "Nhập lựa chọn (1-" + max + "):",
            "Chọn phương thức",
            JOptionPane.PLAIN_MESSAGE);
        try {
            int choice = Integer.parseInt(input);
            return (choice >= min && choice <= max) ? choice : min;
        } catch (Exception e) {
            return min;
        }
    }

    public void showOrderSummary(Order order) {
        JOptionPane.showMessageDialog(this,
            "Tóm tắt đơn hàng:\n" + order.toString() +
            "\nTổng tiền: " + String.format("%,.0f", order.getTotalAmount()) + " VND",
            "Xác nhận đơn hàng",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void showInvoice(Invoice invoice) {
        JOptionPane.showMessageDialog(this, invoice.toString(), "Hóa đơn thanh toán", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
