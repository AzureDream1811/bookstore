package com.bookstore.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Combo {
    private int comboId;
    private String name;
    private double price;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // ACTIVE, CANCELLED
    private List<ComboDetail> details = new ArrayList<>();

    public Combo() {}

    public Combo(int comboId, String name, double price, LocalDate startDate, LocalDate endDate, String status) {
        this.comboId = comboId;
        this.name = name;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return "ACTIVE".equals(status)
                && (startDate == null || !today.isBefore(startDate))
                && (endDate == null || !today.isAfter(endDate));
    }

    public int getComboId() { return comboId; }
    public void setComboId(int comboId) { this.comboId = comboId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<ComboDetail> getDetails() { return details; }
    public void setDetails(List<ComboDetail> details) { this.details = details; }
}
