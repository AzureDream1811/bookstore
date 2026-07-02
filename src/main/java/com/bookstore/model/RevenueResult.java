package com.bookstore.model;

public class RevenueResult {
    private double totalProductAmount;
    private double totalDiscount;
    private double totalShippingFee;
    private double totalRefund;
    private double netRevenue;

    public void calculateNetRevenue() {
        this.netRevenue = this.totalProductAmount - this.totalDiscount + this.totalShippingFee - this.totalRefund;
    }

    public double getTotalProductAmount() {
        return totalProductAmount;
    }

    public void setTotalProductAmount(double totalProductAmount) {
        this.totalProductAmount = totalProductAmount;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public double getTotalShippingFee() {
        return totalShippingFee;
    }

    public void setTotalShippingFee(double totalShippingFee) {
        this.totalShippingFee = totalShippingFee;
    }

    public double getTotalRefund() {
        return totalRefund;
    }

    public void setTotalRefund(double totalRefund) {
        this.totalRefund = totalRefund;
    }

    public double getNetRevenue() {
        return netRevenue;
    }

    public void setNetRevenue(double netRevenue) {
        this.netRevenue = netRevenue;
    }
}
