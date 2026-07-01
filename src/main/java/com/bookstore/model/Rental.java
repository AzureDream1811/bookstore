package com.bookstore.model;

import java.time.LocalDate;

public class Rental {
    private int rentalId;
    private int userId;
    private int bookId;
    private LocalDate rentDate;
    private int days;
    private String status; // RENTED, RETURNED
    private LocalDate returnDate;
    private double lateFee;

    public Rental() {}

    public Rental(int rentalId, int userId, int bookId, LocalDate rentDate, int days, String status) {
        this.rentalId = rentalId;
        this.userId = userId;
        this.bookId = bookId;
        this.rentDate = rentDate;
        this.days = days;
        this.status = status;
    }

    public boolean checkOverdue() {
        if (returnDate != null) return false;
        return LocalDate.now().isAfter(rentDate.plusDays(days));
    }

    public int getRentalId() { return rentalId; }
    public void setRentalId(int rentalId) { this.rentalId = rentalId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public LocalDate getRentDate() { return rentDate; }
    public void setRentDate(LocalDate rentDate) { this.rentDate = rentDate; }
    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public double getLateFee() { return lateFee; }
    public void setLateFee(double lateFee) { this.lateFee = lateFee; }
}
