package com.bookstore.model;

public class InventoryDetail {
    private int ticketId;
    private int bookId;
    private int quantity;
    private double unitPrice;
    private Book newBook; // for new books that need to be created during receipt processing
    private int rentDays; // so ngay thue du kien, luu vao cot rent_days trong bang inventory_detail

    public InventoryDetail() {}

    public InventoryDetail(int ticketId, int bookId, int quantity, double unitPrice) {
        this.ticketId = ticketId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public InventoryDetail(int ticketId, Book newBook, int quantity, double unitPrice) {
        this.ticketId = ticketId;
        this.newBook = newBook;
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
    public Book getNewBook() { return newBook; }
    public void setNewBook(Book newBook) { this.newBook = newBook; }
    public int getRentDays() { return rentDays; }
    public void setRentDays(int rentDays) { this.rentDays = rentDays; }
}
