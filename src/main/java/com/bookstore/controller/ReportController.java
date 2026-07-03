package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.ReportService;
import com.bookstore.view.ConsoleView;

import java.sql.SQLException;
import java.util.List;

public class ReportController {
    private final ConsoleView view;
    private final ReportService reportService = new ReportService();

    public ReportController(ConsoleView view) {
        this.view = view;
    }

    public void open() {
        boolean back = false;
        while (!back) {
            int choice = view.showReportMenu();
            switch (choice) {
                case 1 -> showRevenue();
                case 2 -> showBestSellers();
                case 3 -> showLowStock();
                case 4 -> showChart();
                case 5 -> exportReport();
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
    }

    public void showRevenue() {
        String fromDate = view.readLine("Tu ngay (yyyy-MM-dd hoac yyyy-MM-dd HH:mm:ss): ");
        String toDate = view.readLine("Den ngay (yyyy-MM-dd hoac yyyy-MM-dd HH:mm:ss): ");
        try {
            requireText(fromDate, "Tu ngay khong duoc de trong");
            requireText(toDate, "Den ngay khong duoc de trong");
            double revenue = reportService.revenueBetween(fromDate, toDate);
            view.print(String.format("Doanh thu: %.0f", revenue));
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void showBestSellers() {
        int limit = view.readInt("So luong top san pham: ");
        try {
            if (limit < 1) {
                throw new IllegalArgumentException("So luong top san pham phai lon hon 0");
            }
            List<Object[]> items = reportService.bestSellers(limit);
            if (items.isEmpty()) {
                view.print("Chua co du lieu ban chay");
                return;
            }
            for (Object[] item : items) {
                view.print("[" + item[0] + "] " + item[1] + " - da ban: " + item[2]);
            }
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void showLowStock() {
        int threshold = view.readInt("Nguong ton kho toi da: ");
        try {
            if (threshold < 0) {
                throw new IllegalArgumentException("Nguong ton kho khong hop le");
            }
            List<Book> books = reportService.lowStockBooks(threshold);
            if (books.isEmpty()) {
                view.print("Khong co sach can canh bao ton kho");
                return;
            }
            for (Book book : books) {
                view.print(book.toString());
            }
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void showChart() {
        int limit = view.readInt("So luong top san pham de ve bieu do: ");
        try {
            if (limit < 1) {
                throw new IllegalArgumentException("So luong top san pham phai lon hon 0");
            }
            view.print(reportService.bestSellersChart(limit));
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void exportReport() {
        String fromDate = view.readLine("Tu ngay: ");
        String toDate = view.readLine("Den ngay: ");
        String filePath = view.readLine("Duong dan file xuat (vd: revenue.csv): ");
        try {
            requireText(fromDate, "Tu ngay khong duoc de trong");
            requireText(toDate, "Den ngay khong duoc de trong");
            requireText(filePath, "Duong dan file khong duoc de trong");
            reportService.exportRevenueCsv(fromDate, toDate, filePath);
            view.print("Da xuat bao cao ra file: " + filePath);
        } catch (IllegalArgumentException | SQLException | java.io.IOException e) {
            view.printError(e.getMessage());
        }
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
