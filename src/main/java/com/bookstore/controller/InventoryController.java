package com.bookstore.controller;

import com.bookstore.model.InventoryDetail;
import com.bookstore.service.InventoryService;
import com.bookstore.view.ConsoleView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryController {
    private final InventoryService inventoryService = new InventoryService();
    private final ConsoleView view;

    public InventoryController(ConsoleView view) { this.view = view; }

    public void createGoodsReceipt() {
        List<InventoryDetail> items = collectItems(true);
        if (items == null) return;
        try {
            int ticketId = inventoryService.createGoodsReceipt(items);
            view.print("Lap phieu nhap thanh cong, ticketId=" + ticketId);
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void createGoodsIssue() {
        List<InventoryDetail> items = collectItems(false);
        if (items == null) return;
        try {
            int ticketId = inventoryService.createGoodsIssue(items);
            view.print("Lap phieu xuat thanh cong, ticketId=" + ticketId);
        } catch (IllegalArgumentException | IllegalStateException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private List<InventoryDetail> collectItems(boolean askPrice) {
        List<InventoryDetail> items = new ArrayList<>();
        view.print("Nhap danh sach san pham (nhap bookId=0 de ket thuc)");
        while (true) {
            int bookId = view.readInt("BookId: ");
            if (bookId == 0) break;
            int quantity = view.readInt("So luong: ");
            double unitPrice = askPrice ? view.readDouble("Don gia: ") : 0;
            items.add(new InventoryDetail(0, bookId, quantity, unitPrice));
        }
        return items;
    }
}
