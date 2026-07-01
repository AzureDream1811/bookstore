package com.bookstore.model;

import java.time.LocalDate;

public class Voucher {
    private String voucherCode;
    private double discountValue;
    private double minOrderAmount;
    private LocalDate expiryDate;
    private boolean used;

    public Voucher() {}

    public Voucher(String voucherCode, double discountValue, double minOrderAmount,
                   LocalDate expiryDate, boolean used) {
        this.voucherCode = voucherCode;
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
        this.expiryDate = expiryDate;
        this.used = used;
    }

    public boolean isValid() {
        return !used && expiryDate != null && !expiryDate.isBefore(LocalDate.now());
    }

    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
    public double getDiscountValue() { return discountValue; }
    public void setDiscountValue(double discountValue) { this.discountValue = discountValue; }
    public double getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(double minOrderAmount) { this.minOrderAmount = minOrderAmount; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}
