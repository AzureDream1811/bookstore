package com.bookstore.controller;

import com.bookstore.model.ReportFilter;
import com.bookstore.model.RevenueReportData;
import com.bookstore.model.RevenueResult;
import com.bookstore.model.Book;
import com.bookstore.service.ReportService;
import com.bookstore.view.ConsoleView;

import java.sql.SQLException;
import java.util.List;

public class ReportController {
    private ReportService reportService;
    private ConsoleView view;

    public ReportController(ConsoleView view) {
        this.reportService = new ReportService();
        this.view = view;
    }

    // Trigger: Quản lý chọn chức năng "Báo cáo doanh thu"
    public void handleRevenueReportRequest() {
        boolean isSuccess = false;

        while (!isSuccess) {
            try {
                // Basic Flow 3 & 4: Lấy điều kiện lọc từ View
                ReportFilter filter = view.getRevenueFilterInput();

                // Basic Flow 5, 6, 7: Gọi Service xử lý
                RevenueReportData result = reportService.generateRevenueReport(filter);

                // Basic Flow 9 & 10: Hiển thị kết quả
                view.displayRevenueResult(result);
                isSuccess = true; // Kết thúc thành công

            } catch (IllegalArgumentException e) {
                // Exception Flow 5.1.1 & 5.1.2: Lỗi thời gian, vòng lặp while sẽ quay lại bước nhập
                e.printStackTrace();
                view.displayError(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                // Exception Flow 2.1, 6.1, 6.2: Lỗi hệ thống hoặc không có dữ liệu, hiển thị lỗi và dừng
                view.displayError(e.getMessage());
                isSuccess = true; // Thoát vòng lặp để quay lại menu chính
            }
        }

    }
}
