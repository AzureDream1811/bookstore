package com.bookstore.view;

import com.bookstore.model.ReportFilter;
import com.bookstore.model.RevenueResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ConsoleView {
    private final Scanner scanner = new Scanner(System.in);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public void print(String msg) { System.out.println(msg); }

    public void printError(String msg) { System.out.println("[LOI] " + msg); }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                printError("Vui long nhap so nguyen");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readLine(prompt));
            } catch (NumberFormatException e) {
                printError("Vui long nhap so");
            }
        }
    }
    public LocalDate readDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine().trim(), formatter);
            } catch (java.time.format.DateTimeParseException e) {
                printError("Vui lòng nhập đúng định dạng dd/MM/yyyy (VD: 01/10/2025)");
            }
        }
    }
    public ReportFilter getRevenueFilterInput() {
        System.out.println("=== BỘ LỌC BÁO CÁO DOANH THU ===");
        LocalDate fromDate = readDate("Nhập ngày bắt đầu (dd/MM/yyyy): ");
        LocalDate toDate = readDate("Nhập ngày kết thúc (dd/MM/yyyy): ");
        return new ReportFilter(fromDate, toDate);
    }

    public void displayRevenueResult(RevenueResult result) {
        System.out.println("\n=== KẾT QUẢ BÁO CÁO DOANH THU THUẦN ===");
        System.out.println("Tổng tiền sản phẩm : " + result.getTotalProductAmount());
        System.out.println("- Giảm giá         : " + result.getTotalDiscount());
        System.out.println("+ Phí vận chuyển   : " + result.getTotalShippingFee());
        System.out.println("- Hoàn tiền        : " + result.getTotalRefund());
        System.out.println("-------------------------------------");
        System.out.println("DOANH THU THUẦN    : " + result.getNetRevenue());
    }

    public void displayError(String message) {
        System.out.println("[LỖI] " + message);
    }
}
