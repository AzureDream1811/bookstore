package com.bookstore.model;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String genre;
    private double price;
    private double rentPricePerDay;
    private int stockQuantity;
    private boolean faulty;
    private String status; // ACTIVE, INACTIVE

    public Book() {}

    public Book(int bookId, String title, String author, String genre, double price,
                double rentPricePerDay, int stockQuantity, boolean faulty, String status) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.rentPricePerDay = rentPricePerDay;
        this.stockQuantity = stockQuantity;
        this.faulty = faulty;
        this.status = status;
    }

    public boolean isAvailable() {
        return "ACTIVE".equals(status) && !faulty && stockQuantity > 0;
    }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getRentPricePerDay() { return rentPricePerDay; }
    public void setRentPricePerDay(double rentPricePerDay) { this.rentPricePerDay = rentPricePerDay; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public boolean isFaulty() { return faulty; }
    public void setFaulty(boolean faulty) { this.faulty = faulty; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s (gia: %.0f, thue/ngay: %.0f, ton: %d, trang thai: %s%s)",
                bookId, title, author, price, rentPricePerDay, stockQuantity, status, faulty ? ", LOI" : "");
    }
}
