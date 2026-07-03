package com.bookstore.view;

import com.bookstore.model.ReportFilter;
import com.bookstore.model.RevenueReportData;
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

    public void displayRevenueResult(RevenueReportData reportData) {
        java.util.List<RevenueResult> dailyResults = reportData.getDailyResults();
        RevenueResult totalResult = reportData.getTotalResult();

        System.out.println("\n==================================== BẢNG CHI TIẾT DOANH THU THEO NGÀY ====================================");
        // Định dạng tiêu đề cột cho thẳng hàng
        String formatHeader = "| %-12s | %-18s | %-15s | %-18s | %-15s | %-20s |\n";
        String formatRow    = "| %-12s | %-18.1f | %-15.1f | %-18.1f | %-15.1f | %-20.1f |\n";

        System.out.printf(formatHeader, "Ngày", "Tiền sản phẩm", "Giảm giá", "Phí vận chuyển", "Hoàn tiền", "Doanh thu thuần");
        System.out.println("-----------------------------------------------------------------------------------------------------------");

        for (RevenueResult row : dailyResults) {
            System.out.printf(formatRow,
                    row.getDate().format(formatter),
                    row.getTotalProductAmount(),
                    row.getTotalDiscount(),
                    row.getTotalShippingFee(),
                    row.getTotalRefund(),
                    row.getNetRevenue());
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.println("\n=== KẾT QUẢ BÁO CÁO DOANH THU THUẦN ===");
        System.out.println("Tổng tiền sản phẩm : " + totalResult.getTotalProductAmount());
        System.out.println("- Giảm giá         : " + totalResult.getTotalDiscount());
        System.out.println("+ Phí vận chuyển   : " + totalResult.getTotalShippingFee());
        System.out.println("- Hoàn tiền        : " + totalResult.getTotalRefund());
        System.out.println("-------------------------------------");
        System.out.println("DOANH THU THUẦN    : " + totalResult.getNetRevenue());
    }

    public void displayError(String message) {
        System.out.println("[LỖI] " + message);
    }
    // ==========================================
    // CÁC HÀM HIỂN THỊ MENU ĐIỀU HƯỚNG
    // ==========================================

    public int showMainMenu(String userStatus) {
        print("");
        print("=== HE THONG QUAN LY NHA SACH ===");
        print(userStatus);
        print("1. Dang nhap");
        print("2. Dang ky");
        print("3. Quan ly sach");
        print("4. Quan ly kho hang");
        print("5. Quan ly giam gia");
        print("6. Quan ly gio hang / hoa don");
        print("7. Quan ly thue sach");
        print("8. Thong ke bao cao");
        print("9. Quan ly tai khoan nguoi dung");
        print("10. Quan ly ban hang");
        print("0. Thoat");
        return readInt("Chon chuc nang: ");
    }

    public int showBookMenu() {
        print("");
        print("--- QUAN LY SACH ---");
        print("1. Danh sach sach");
        print("2. Tim kiem va goi y sach");
        print("3. Them sach");
        print("4. Sua sach");
        print("5. An sach");
        print("6. Danh dau sach loi");
        print("0. Quay lai");
        return readInt("Chon chuc nang: ");
    }

    public int showInventoryMenu() {
        print("");
        print("--- QUAN LY KHO HANG ---");
        print("1. Lap phieu nhap");
        print("2. Lap phieu xuat");
        print("0. Quay lai");
        return readInt("Chon chuc nang: ");
    }

    public int showDiscountMenu() {
        print("");
        print("--- QUAN LY GIAM GIA ---");
        print("1. Danh sach combo");
        print("2. Tao combo");
        print("3. Huy combo");
        print("4. Tao voucher");
        print("5. Thong bao sale");
        print("0. Quay lai");
        return readInt("Chon chuc nang: ");
    }

    public int showReportMenu() {
        print("");
        print("--- THONG KE BAO CAO ---");
        print("1. Doanh thu theo khoang ngay");
        print("2. San pham ban chay");
        print("3. Bao cao ton kho");
        print("4. Ve bieu do ban chay");
        print("5. Xuat file bao cao");
        print("0. Quay lai");
        return readInt("Chon chuc nang: ");
    }

    public int showUserMenu() {
        print("");
        print("--- QUAN LY TAI KHOAN NGUOI DUNG ---");
        print("1. Danh sach tai khoan");
        print("2. Cap nhat vai tro");
        print("0. Quay lai");
        return readInt("Chon chuc nang: ");
    }

    public int showCartMenu() {
        print("");
        print("--- GIO HANG / HOA DON ---");
        print("1. Dat hang");
        print("2. Xac nhan thanh toan");
        print("3. Huy don / hoan tien");
        print("4. Tim kiem hoa don");
        print("0. Quay lai");
        return readInt("Chon chuc nang: ");
    }

    public int showRentalMenu() {
        print("");
        print("--- QUAN LY THUE SACH ---");
        print("1. Thue sach");
        print("2. Tra sach");
        print("3. Tim kiem phieu thue");
        print("4. Canh bao qua han tra sach");
        print("0. Quay lai");
        return readInt("Chon chuc nang: ");
    }
    public int showPostReportMenu() {
        print("\n--- THAO TÁC TIẾP THEO ---");
        print("1. Xuất file báo cáo");
        print("2. Xuất biểu đồ");
        print("0. Về trang chủ (Quay lại)");
        return readInt("Chọn chức năng: ");
    }
    public int showChartTypeMenu() {
        print("\n--- DANH SÁCH LOẠI BIỂU ĐỒ HỖ TRỢ ---");
        print("1. Biểu đồ cột (Bar Chart) - So sánh theo ngày");
        print("2. Biểu đồ tròn (Pie Chart) - Tỷ lệ dòng tiền tổng cộng");
        print("3. Biểu đồ đường (Line Chart) - Biến động xu hướng");
        print("0. Hủy bỏ quay lại");
        return readInt("Chọn loại biểu đồ bạn muốn vẽ: ");
    }
}
