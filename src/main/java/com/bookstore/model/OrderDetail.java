package com.bookstore.model;

public class OrderDetail {
    private int orderId;
    private int bookId;
    private int quantity;
    private double price;

    public OrderDetail() {}

    public OrderDetail(int orderId, int bookId, int quantity, double price) {
        this.orderId = orderId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.price = price;
    }

    public double subTotal() { return quantity * price; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
