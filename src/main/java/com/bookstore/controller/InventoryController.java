package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.InventoryDetail;
import com.bookstore.service.BookService;
import com.bookstore.service.InventoryService;
import com.bookstore.view.ConsoleView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryController {
    private final InventoryService inventoryService = new InventoryService();
    private final BookService bookService = new BookService();
    private final ConsoleView view;

    public InventoryController(ConsoleView view) { this.view = view; }

    public void createGoodsReceipt() {
        List<InventoryDetail> items = new ArrayList<>();
        view.print("\n--- LAP PHIEU NHAP HANG ---");
        boolean done = false;
        while (!done) {
            view.print("\n1. Them sach co san vao danh sach");
            view.print("2. Them sach moi vao danh sach");
            view.print("0. Hoan tat nhap va xac nhan lap phieu");
            int choice = view.readInt("Chon chuc nang: ");
            switch (choice) {
                case 1 -> {
                    int bookId = view.readInt("Nhap BookId: ");
                    try {
                        Book book = bookService.getById(bookId);
                        if (book == null) {
                            view.printError("Khong tim thay sach voi BookId = " + bookId);
                            break;
                        }
                        view.print("Sach chon: " + book.getTitle() + " (Ton hien tai: " + book.getStockQuantity() + ")");
                        int quantity = view.readInt("So luong nhap: ");
                        if (quantity <= 0) {
                            view.printError("So luong phai lon hon 0");
                            break;
                        }
                        double unitPrice = view.readDouble("Don gia nhap: ");
                        if (unitPrice < 0) {
                            view.printError("Don gia nhap khong duoc am");
                            break;
                        }
                        int rentDays = view.readInt("So ngay thue du kien (0 neu khong thue): ");
                        if (rentDays < 0) {
                            view.printError("So ngay thue khong duoc am");
                            break;
                        }

                        boolean merged = false;
                        for (InventoryDetail item : items) {
                            if (item.getNewBook() == null && item.getBookId() == bookId) {
                                item.setQuantity(quantity);
                                item.setUnitPrice(unitPrice);
                                item.setRentDays(rentDays);
                                merged = true;
                                break;
                            }
                        }
                        if (!merged) {
                            InventoryDetail detail = new InventoryDetail(0, bookId, quantity, unitPrice);
                            detail.setRentDays(rentDays);
                            items.add(detail);
                        }
                        view.print(merged ? "Da cap nhat so luong sach trong danh sach tam thoi."
                                : "Da them sach vao danh sach tam thoi.");
                    } catch (SQLException e) {
                        view.printError("Loi doc co so du lieu: " + e.getMessage());
                    }
                }
                case 2 -> {
                    String title = view.readLine("Ten sach moi: ");
                    if (title.isBlank()) {
                        view.printError("Ten sach khong duoc de trong");
                        break;
                    }
                    String author = view.readLine("Tac gia: ");
                    String genre = view.readLine("The loai: ");
                    double price = view.readDouble("Gia ban: ");
                    if (price < 0) {
                        view.printError("Gia ban khong hop le");
                        break;
                    }
                    double rentPrice = view.readDouble("Gia thue/ngay: ");
                    if (rentPrice < 0) {
                        view.printError("Gia thue khong hop le");
                        break;
                    }
                    int quantity = view.readInt("So luong nhap: ");
                    if (quantity <= 0) {
                        view.printError("So luong phai lon hon 0");
                        break;
                    }
                    double unitPrice = view.readDouble("Don gia nhap: ");
                    if (unitPrice < 0) {
                        view.printError("Don gia nhap khong duoc am");
                        break;
                    }
                    int rentDays = view.readInt("So ngay thue du kien (0 neu khong thue): ");
                    if (rentDays < 0) {
                        view.printError("So ngay thue khong duoc am");
                        break;
                    }

                    Book newBook = new Book(0, title, author, genre, price, rentPrice, 0, false, "ACTIVE");
                    InventoryDetail detail = new InventoryDetail(0, newBook, quantity, unitPrice);
                    detail.setRentDays(rentDays);
                    items.add(detail);
                    view.print("Da them sach moi vao danh sach tam thoi.");
                }
                case 0 -> done = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }

        if (items.isEmpty()) {
            view.print("Danh sach sach nhap trong. Huy bo lap phieu.");
            return;
        }

        try {
            double totalCost = 0;
            view.print("\n--- TOM TAT PHIEU NHAP HANG ---");
            for (int i = 0; i < items.size(); i++) {
                InventoryDetail item = items.get(i);
                double nhapCost = item.getQuantity() * item.getUnitPrice();
                double rentPricePerDay;
                String title;
                String tag;
                if (item.getNewBook() != null) {
                    rentPricePerDay = item.getNewBook().getRentPricePerDay();
                    title = item.getNewBook().getTitle();
                    tag = "[Sach moi]";
                } else {
                    Book b = bookService.getById(item.getBookId());
                    rentPricePerDay = (b != null) ? b.getRentPricePerDay() : 0;
                    title = (b != null) ? b.getTitle() : "Khong xac dinh";
                    tag = "[Co san] (ID: " + item.getBookId() + ")";
                }
                double rentCost = rentPricePerDay * item.getRentDays();
                double itemTotal = nhapCost + rentCost;
                totalCost += itemTotal;
                view.print(String.format(
                        "%d. %s %s | SL: %d | Don gia nhap: %.0f | Tien thue/ngay: %.0f | So ngay thue: %d | Thanh tien: %.0f",
                        i + 1, tag, title, item.getQuantity(), item.getUnitPrice(), rentPricePerDay, item.getRentDays(), itemTotal));
            }
            view.print(String.format("Tong so mat hang: %d", items.size()));
            view.print(String.format("Tong tien (nhap hang + tien thue du kien): %.0f", totalCost));

            String confirm = view.readLine("Xac nhan lap phieu? (Y/N): ");
            if (confirm.equalsIgnoreCase("Y")) {
                int ticketId = inventoryService.createGoodsReceipt(items);
                view.print("Lap phieu nhap thanh cong, ticketId=" + ticketId);
            } else {
                view.print("Da huy bo lap phieu nhap.");
            }
        } catch (IllegalArgumentException | SQLException e) {
            view.printError("Loi lap phieu nhap hang: " + e.getMessage());
        }
    }

    public void createGoodsIssue() {
        List<InventoryDetail> items = new ArrayList<>();
        view.print("\n--- LAP PHIEU XUAT HANG ---");
        boolean done = false;
        while (!done) {
            view.print("\n1. Them san pham can xuat vao danh sach");
            view.print("0. Hoan tat xuat va xac nhan lap phieu");
            int choice = view.readInt("Chon chuc nang: ");
            switch (choice) {
                case 1 -> {
                    int bookId = view.readInt("Nhap BookId: ");
                    try {
                        Book book = bookService.getById(bookId);
                        if (book == null) {
                            view.printError("Khong tim thay sach voi BookId = " + bookId);
                            break;
                        }
                        InventoryDetail existing = null;
                        for (InventoryDetail item : items) {
                            if (item.getBookId() == bookId) {
                                existing = item;
                                break;
                            }
                        }
                        if (book.getStockQuantity() <= 0) {
                            view.printError("Sach '" + book.getTitle() + "' khong du ton kho de xuat");
                            break;
                        }
                        view.print("Sach chon: " + book.getTitle() + " (Ton kho hien tai: " + book.getStockQuantity()
                                + (existing != null ? ", so luong da chon truoc do: " + existing.getQuantity() : "") + ")");

                        int quantity = view.readInt(existing != null
                                ? "Nhap lai so luong xuat (thay the so luong cu): "
                                : "So luong xuat: ");
                        if (quantity <= 0) {
                            view.printError("So luong phai lon hon 0");
                            break;
                        }
                        // Kiem tra ton kho co du khong
                        if (quantity > book.getStockQuantity()) {
                            view.printError("Khong du ton kho de xuat. Ton kho chi con: " + book.getStockQuantity());
                            break;
                        }

                        if (existing != null) {
                            existing.setQuantity(quantity);
                            view.print("Da sua so luong xuat trong danh sach tam thoi.");
                        } else {
                            items.add(new InventoryDetail(0, bookId, quantity, 0));
                            view.print("Da them sach vao danh sach xuat tam thoi.");
                        }
                    } catch (SQLException e) {
                        view.printError("Loi doc co so du lieu: " + e.getMessage());
                    }
                }
                case 0 -> done = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }

        if (items.isEmpty()) {
            view.print("Danh sach sach xuat trong. Huy bo lap phieu.");
            return;
        }

        try {
            view.print("\n--- TOM TAT PHIEU XUAT HANG ---");
            int totalQuantity = 0;
            for (int i = 0; i < items.size(); i++) {
                InventoryDetail item = items.get(i);
                Book b = bookService.getById(item.getBookId());
                String title = (b != null) ? b.getTitle() : "Khong xac dinh";
                totalQuantity += item.getQuantity();
                view.print(String.format("%d. %s (ID: %d) | SL xuat: %d",
                        i + 1, title, item.getBookId(), item.getQuantity()));
            }
            view.print(String.format("Tong so mat hang: %d", items.size()));
            view.print(String.format("Tong so luong xuat: %d", totalQuantity));

            String confirm = view.readLine("Xac nhan lap phieu? (Y/N): ");
            if (confirm.equalsIgnoreCase("Y")) {
                int ticketId = inventoryService.createGoodsIssue(items);
                view.print("Lap phieu xuat thanh cong, ticketId=" + ticketId);
            } else {
                view.print("Da huy bo lap phieu xuat.");
            }
        } catch (IllegalArgumentException | IllegalStateException | SQLException e) {
            view.printError("Loi lap phieu xuat hang: " + e.getMessage());
        }
    }
}
