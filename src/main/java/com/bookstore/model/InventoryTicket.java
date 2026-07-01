package com.bookstore.model;

import java.util.ArrayList;
import java.util.List;

public class InventoryTicket {
    private int ticketId;
    private String type; // IN, OUT
    private String status; // PENDING, CONFIRMED
    private List<InventoryDetail> details = new ArrayList<>();

    public InventoryTicket() {}

    public InventoryTicket(int ticketId, String type, String status) {
        this.ticketId = ticketId;
        this.type = type;
        this.status = status;
    }

    public double calculateTotalCost() {
        double total = 0;
        for (InventoryDetail d : details) {
            total += d.getQuantity() * d.getUnitPrice();
        }
        return total;
    }

    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<InventoryDetail> getDetails() { return details; }
    public void setDetails(List<InventoryDetail> details) { this.details = details; }
}
