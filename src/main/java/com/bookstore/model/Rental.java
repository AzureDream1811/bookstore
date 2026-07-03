package com.bookstore.model;

import java.time.LocalDate;

public class Rental {
    private int rentalId;
    private int userId;
    private int bookId;

    private LocalDate rentDate;
    private int days;

    private LocalDate dueDate;      // Hạn trả
    private LocalDate returnDate;

    private double rentalFee;       // Phí thuê
    private double lateFee;         // Phí phạt

    private String status;          // RENTED, RETURNED

    public Rental() {
    }

    public Rental(int rentalId, int userId, int bookId,
                  LocalDate rentDate, int days, String status) {
        this.rentalId = rentalId;
        this.userId = userId;
        this.bookId = bookId;
        this.rentDate = rentDate;
        this.days = days;
        this.status = status;
        this.dueDate = rentDate.plusDays(days);
    }

    public boolean checkOverdue() {
        if (returnDate != null) {
            return returnDate.isAfter(dueDate);
        }
        return LocalDate.now().isAfter(dueDate);
    }

    // Getter & Setter

    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public void setRentDate(LocalDate rentDate) {
        this.rentDate = rentDate;
        this.dueDate = rentDate.plusDays(days);
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
        if (rentDate != null) {
            this.dueDate = rentDate.plusDays(days);
        }
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getRentalFee() {
        return rentalFee;
    }

    public void setRentalFee(double rentalFee) {
        this.rentalFee = rentalFee;
    }

    public double getLateFee() {
        return lateFee;
    }

    public void setLateFee(double lateFee) {
        this.lateFee = lateFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}