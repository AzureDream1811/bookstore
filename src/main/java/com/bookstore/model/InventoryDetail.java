package com.bookstore.model;

public class InventoryDetail {
    private int ticketId;
    private int bookId;
    private int quantity;
    private double unitPrice;

    public InventoryDetail() {}

    public InventoryDetail(int ticketId, int bookId, int quantity, double unitPrice) {
        this.ticketId = ticketId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
}
