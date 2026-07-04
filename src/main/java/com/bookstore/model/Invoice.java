package com.bookstore.model;

import java.util.Date;

public class Invoice {
    private Order order;
    private Payment paymentMethod;
    private Date invoiceDate;
    private double totalAmount;

    // Getters & Setters
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Payment getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(Payment paymentMethod) { this.paymentMethod = paymentMethod; }
    public Date getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(Date invoiceDate) { this.invoiceDate = invoiceDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    @Override
    public String toString() {
        return "Hóa đơn #" + (order != null ? order.getOrderId() : "N/A") +
               "\nNgày: " + invoiceDate +
               "\nPhương thức: " + paymentMethod.getDisplayName() +
               "\nTổng tiền: " + String.format("%,.0f", totalAmount) + " VND";
    }
}
