package com.bookstore.service;

import com.bookstore.model.RevenueReportData;
import com.bookstore.model.RevenueResult;
import com.bookstore.model.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportExcelService {
    // Ngưỡng chia nhỏ file (Giả lập thay cho 10MB -> khoảng 50,000 dòng Excel)
    private static final int MAX_ROWS_PER_FILE = 50000;
    private static final String EXPORT_DIR = "exports/"; // Thư mục lưu file

    public void exportRevenueReport(User currentUser, RevenueReportData data) throws Exception {
        // Exception Flow 3.1: Kiểm tra quyền quản lý
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            throw new SecurityException("Không đủ quyền thực hiện chức năng này");
        }

        // Business Rule 4: Không xuất file rỗng
        if (data == null || data.getDailyResults().isEmpty()) {
            throw new IllegalArgumentException("Không thể xuất file: Dữ liệu báo cáo trống.");
        }

        List<RevenueResult> dailyResults = data.getDailyResults();

        // Tạo thư mục nếu chưa tồn tại
        File dir = new File(EXPORT_DIR);
        if (!dir.exists()) dir.mkdirs();

        // Business Rule 5: Format tên file
        String dateSuffix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseFileName = "DoanhThu_" + dateSuffix;

        // Alternative Flow 5.1: Dữ liệu quá lớn, cần chia nhỏ (Chunking)
        if (dailyResults.size() > MAX_ROWS_PER_FILE) {
            System.out.println("[THÔNG BÁO] Dữ liệu lớn, sẽ được xuất thành nhiều file...");
            int fileIndex = 1;
            for (int i = 0; i < dailyResults.size(); i += MAX_ROWS_PER_FILE) {
                int end = Math.min(i + MAX_ROWS_PER_FILE, dailyResults.size());
                List<RevenueResult> chunk = dailyResults.subList(i, end);
                String fileName = EXPORT_DIR + baseFileName + "_part" + fileIndex + ".xlsx";
                generateExcelFile(fileName, chunk, data.getTotalResult());
                fileIndex++;
            }
        } else {
            // Basic Flow 6 & 7: Tạo 1 file duy nhất
            String fileName = EXPORT_DIR + baseFileName + ".xlsx";
            generateExcelFile(fileName, dailyResults, data.getTotalResult());
            System.out.println("\n[THÀNH CÔNG] Đã xuất file báo cáo tại: " + new File(fileName).getAbsolutePath());
        }
    }

    private void generateExcelFile(String filePath, List<RevenueResult> dailyResults, RevenueResult totalResult) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo Cáo Doanh Thu");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Tạo Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Ngày", "Tiền sản phẩm", "Giảm giá", "Phí vận chuyển", "Hoàn tiền", "Doanh thu thuần"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                // Style in đậm cho Header
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // Đổ dữ liệu Data Rows
            int rowNum = 1;
            for (RevenueResult result : dailyResults) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(result.getDate().format(fmt));
                row.createCell(1).setCellValue(result.getTotalProductAmount());
                row.createCell(2).setCellValue(result.getTotalDiscount());
                row.createCell(3).setCellValue(result.getTotalShippingFee());
                row.createCell(4).setCellValue(result.getTotalRefund());
                row.createCell(5).setCellValue(result.getNetRevenue());
            }

            // Tạo dòng Tổng cộng (Footer Row)
            Row totalRow = sheet.createRow(rowNum + 1); // Cách 1 dòng
            totalRow.createCell(0).setCellValue("TỔNG CỘNG");
            totalRow.createCell(1).setCellValue(totalResult.getTotalProductAmount());
            totalRow.createCell(2).setCellValue(totalResult.getTotalDiscount());
            totalRow.createCell(3).setCellValue(totalResult.getTotalShippingFee());
            totalRow.createCell(4).setCellValue(totalResult.getTotalRefund());
            totalRow.createCell(5).setCellValue(totalResult.getNetRevenue());

            // Tự động căn chỉnh độ rộng cột
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi file ra ổ cứng
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            // Exception Flow 6.1: Gặp lỗi khi generate/ghi file
            throw new Exception("Không thể tạo file Excel: Đang có lỗi truy xuất hoặc file đang được mở bởi phần mềm khác.", e);
        }
    }
}