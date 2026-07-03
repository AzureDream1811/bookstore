package com.bookstore.view;

import com.bookstore.model.Rental;
import com.bookstore.model.ReportFilter;
import com.bookstore.model.RevenueReportData;
import com.bookstore.model.RevenueResult;
import com.bookstore.model.BestSellerFilter;
import com.bookstore.model.BestSellerItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.List;

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
    public String inputStatus() {
        return readLine("Nhap trang thai moi (RENTED/RETURNED/CANCELLED): ");
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
    public void showUpdateSuccess() {
        print("Cap nhat trang thai thanh cong.");
    }

    public void showUpdateError() {
        printError("Cap nhat trang thai that bai.");
    }
    public void displayError(String message) {
        System.out.println("[LỖI] " + message);
    }
    public BestSellerFilter getBestSellerFilterInput() {
        System.out.println("=== BỘ LỌC SẢN PHẨM BÁN CHẠY ===");

        print("1. Tất cả");
        print("2. Bán");
        print("3. Thuê");
        int typeChoice = readInt("Chọn loại (1-3): ");
        BestSellerFilter.Type type = switch (typeChoice) {
            case 2 -> BestSellerFilter.Type.SOLD;
            case 3 -> BestSellerFilter.Type.RENTED;
            default -> BestSellerFilter.Type.ALL;
        };

        String fromStr = readLine("Nhập ngày bắt đầu (dd/MM/yyyy ");
        String toStr = readLine("Nhập ngày kết thúc (dd/MM/yyyy ");
        java.time.LocalDate fromDate = fromStr.isBlank() ? null : java.time.LocalDate.parse(fromStr, formatter);
        java.time.LocalDate toDate = toStr.isBlank() ? null : java.time.LocalDate.parse(toStr, formatter);

        print("1. Tăng dần");
        print("2. Giảm dần");
        int sortChoice = readInt("Chọn thứ tự sắp xếp (1-2): ");
        BestSellerFilter.SortOrder sortOrder = sortChoice == 1
                ? BestSellerFilter.SortOrder.ASC
                : BestSellerFilter.SortOrder.DESC;

        return new BestSellerFilter(fromDate, toDate, type, sortOrder);
    }

    public void displayBestSellerResult(List<BestSellerItem> items, BestSellerFilter.Type type) {
        System.out.println("\n=== TOP SẢN PHẨM BÁN CHẠY ===");
        int rank = 1;
        for (BestSellerItem item : items) {
            StringBuilder line = new StringBuilder();
            line.append(rank++).append(". ").append(item.getTitle());
            if (type == BestSellerFilter.Type.SOLD) {
                line.append(" - ban: ").append(item.getSoldQty()).append(" cuon");
            } else if (type == BestSellerFilter.Type.RENTED) {
                line.append(" - thue: ").append(item.getRentedQty()).append(" cuon");
            } else {
                line.append(" - ban: ").append(item.getSoldQty()).append(" cuon")
                        .append(" - thue: ").append(item.getRentedQty()).append(" cuon");
            }
            System.out.println(line);
        }
    }
    // ==========================================
    // CÁC HÀM HIỂN THỊ MENU ĐIỀU HƯỚNG
    // ==========================================

    /** Menu khi CHUA dang nhap */
    public int showGuestMenu() {
        print("");
        print("=== HE THONG QUAN LY NHA SACH ===");
        print("Trang thai: chua dang nhap");
        print("1. Dang nhap");
        print("2. Dang ky");
        print("0. Thoat");
        return readInt("Chon chuc nang: ");
    }

    /** Menu danh cho role CUSTOMER sau khi dang nhap */
    public int showCustomerMenu(String userStatus) {
        print("");
        print("=== HE THONG QUAN LY NHA SACH ===");
        print(userStatus);
        print("1. Xem và tìm kiếm sách");
        print("2. Gio hang / hoa don");
        print("3. Quan ly ban hang");
        print("4. Thue sach");
        print("5. Tra sach");
        print("0. Dang xuat");
        return readInt("Chon chuc nang: ");
    }

    /** Menu danh cho role STAFF / MANAGER sau khi dang nhap */
    public int showStaffMenu(String userStatus) {
        print("");
        print("=== HE THONG QUAN LY NHA SACH ===");
        print(userStatus);
        print("1. Quan ly sach");
        print("2. Quan ly kho hang");
        print("3. Quan ly giam gia");
        print("4. Gio hang / hoa don");
        print("5. Thong ke bao cao");
        print("6. Quan ly tai khoan nguoi dung");
        print("7. Quan ly ban hang");
        print("8. Quan ly phieu thue");
        print("0. Dang xuat");
        return readInt("Chon chuc nang: ");
    }

    /** Menu sach rut gon danh cho khach hang: chi xem + tim kiem */
    public int showBookMenuCustomer() {
        print("");
        print("--- XEM VÀ TÌM KIẾM SÁCH ---");
        print("1. Danh sach sach");
        print("2. Tim kiem va goi y sach");
        print("0. Quay lai");
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
        print("2. Vẽ biểu đồ");
        print("0. Về trang chủ (Quay lại)");
        return readInt("Chọn chức năng: ");
    }
    public int showChartTypeMenu() {
        print("\n--- DANH SÁCH LOẠI BIỂU ĐỒ HỖ TRỢ ---");
        print("1. Biểu đồ cột (Bar Chart) - So sánh theo ngày");
        print("2. Biểu đồ tròn (Pie Chart) - Tỷ lệ dòng tiền tổng cộng");
        print("3. Biểu đồ đường (Line Chart) - Biến động xu hướn1g");
        print("0. Hủy bỏ quay lại");
        return readInt("Chọn loại biểu đồ bạn muốn vẽ: ");
    }
    // ==========================================
    // THUÊ SÁCH
    // ==========================================

    public void showRentalInfo() {
        print("");
        print("===== THUE SACH =====");
    }
    public void showRentalTickets(List<Rental> list) {

        print("");
        print("===== DANH SACH PHIEU THUE =====");

        for (Rental r : list) {

            print("--------------------------------");
            print("Ma phieu : " + r.getRentalId());
            print("Ma sach  : " + r.getBookId());
            print("Ngay thue: " + r.getRentDate());
            print("Han tra  : " + r.getDueDate());
            print("Trang thai: " + r.getStatus());
        }
    }
    public int inputBookId() {
        return readInt("Nhap ma sach: ");
    }

    public int inputRentalDays() {
        return readInt("Nhap so ngay thue (1-30): ");
    }

    public int inputRentalId() {
        return readInt("Nhap ma phieu thue: ");
    }

    public String inputKeyword() {
        return readLine("Nhap tu khoa tim kiem: ");
    }

    public void showRentalSuccess(int rentalId, double rentalFee) {
        print("");
        print("===== THUE SACH THANH CONG =====");
        print("Ma phieu thue : " + rentalId);
        print("Tong tien thue: " + rentalFee);
    }

    public void showReturnSuccess(double lateFee) {
        print("");
        print("===== TRA SACH THANH CONG =====");
        print("Phi tre han : " + lateFee);
    }
// ==========================================
// UC22 - QUAN LY PHIEU THUE
// ==========================================

    public int showRentalManagementMenu() {

        print("");
        print("===== QUAN LY PHIEU THUE =====");
        print("1. Danh sach phieu thue");
        print("2. Tim kiem phieu thue");
        print("3. Cap nhat trang thai");
        print("0. Quay lai");

        return readInt("Chon chuc nang: ");
    }

    public void showRentalDetail(Rental rental) {

        print("--------------------------------");
        print("Ma phieu   : " + rental.getRentalId());
        print("User       : " + rental.getUserId());
        print("Book       : " + rental.getBookId());
        print("Ngay thue  : " + rental.getRentDate());
        print("Han tra    : " + rental.getDueDate());
        print("Ngay tra   : " + rental.getReturnDate());
        print("Tien thue  : " + rental.getRentalFee());
        print("Phi tre han: " + rental.getLateFee());
        print("Trang thai : " + rental.getStatus());
    }
    public void showBookList(com.bookstore.model.Page<com.bookstore.model.Book> bookPage) {
        print("");
        print("--- DANH SACH SACH ---");
        print(String.format("Trang %d / %d", bookPage.getPageNumber(), bookPage.getTotalPages()));
        for (com.bookstore.model.Book book : bookPage.getContent()) {
            print(book.toString());
        }
    }

    public String getPaginationInput() {
        return readLine("Chon ([N]ext/[P]revious/[E]xit): ");
    }
}
