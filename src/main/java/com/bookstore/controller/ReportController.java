package com.bookstore.controller;

import com.bookstore.chart.ChartType;
import com.bookstore.model.ReportFilter;
import com.bookstore.model.RevenueReportData;
import com.bookstore.model.RevenueResult;
import com.bookstore.model.Book;
import com.bookstore.service.ChartService;
import com.bookstore.service.ReportService;
import com.bookstore.view.ConsoleView;
import com.bookstore.model.BestSellerFilter;
import com.bookstore.model.BestSellerItem;

import java.sql.SQLException;
import java.util.List;

public class ReportController {
    private ReportService reportService;
    private ConsoleView view;
    private ChartService chartService;

    public ReportController(ConsoleView view) {
        this.reportService = new ReportService();
        this.view = view;
        this.chartService= new ChartService();
    }

    // Trigger: Quản lý chọn chức năng "Báo cáo doanh thu"
    public void handleRevenueReportRequest() {
        boolean isSuccess = false;
        RevenueReportData reportData = null;
        while (!isSuccess) {
            try {
                // Basic Flow 3 & 4: Lấy điều kiện lọc từ View
                ReportFilter filter = view.getRevenueFilterInput();
                reportData = reportService.generateRevenueReport(filter);
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
            handlePostReportActions(reportData);
        }
    }
    private void handlePostReportActions(RevenueReportData reportData) {
        boolean inPostMenu = true;
        while (inPostMenu) {
            int actionChoice = view.showPostReportMenu();
            switch (actionChoice) {
                case 1 -> {
                    // Sẽ xử lý chức năng xuất file Excel tại đây theo đặc tả sau
                    view.displayError("Chức năng xuất báo cáo file Excel đang được phát triển.");
                }
                case 2 -> {
                    // Gọi hàm xử lý quy trình vẽ biểu đồ độc lập
                    handleChartGenerationFlow(reportData);
                }
                case 0 -> inPostMenu = false; // Thoát quay về màn hình trước
                default -> view.displayError("Lựa chọn thao tác kế tiếp không hợp lệ.");
            }
        }
    }
        private void handleChartGenerationFlow(RevenueReportData reportData) {
            boolean isChartDone = false;
            while (!isChartDone) {
                int chartChoice = view.showChartTypeMenu();
                if (chartChoice == 0) return; // Người dùng chọn quay lại

                ChartType selectedType = null;
                switch (chartChoice) {
                    case 1 -> selectedType = ChartType.BAR;
                    case 2 -> selectedType = ChartType.PIE;
                    case 3 -> selectedType = ChartType.LINE;
                    default -> {
                        view.displayError("Loại biểu đồ không tồn tại trên hệ thống.");
                        continue;
                    }
                }

                try {
                    // Đẩy sang Module xử lý kiểm tra nghiệp vụ và vẽ
                    chartService.processAndRenderChart(selectedType, reportData);
                    isChartDone = true; // Vẽ thành công, kết thúc luồng vẽ
                } catch (IllegalArgumentException e) {
                    // Exception Flow 5.1: Loại biểu đồ không phù hợp với dữ liệu dữ liệu -> Yêu cầu chọn lại
                    view.displayError(e.getMessage());
                } catch (Exception e) {
                    // Exception Flow 8.1: Không thể render biểu đồ -> Cho phép chọn/thử lại
                    view.displayError(e.getMessage());
                }
            }
        }
        
        public void handleBestSellerReportRequest() {
            boolean isSuccess = false;
            while (!isSuccess) {
                try {
                    BestSellerFilter filter = view.getBestSellerFilterInput();
                    List<BestSellerItem> result = reportService.generateBestSellerReport(filter);
                    view.displayBestSellerResult(result, filter.getType());
                    isSuccess = true;
                } catch (IllegalArgumentException e) {
                    view.displayError(e.getMessage());
                } catch (Exception e) {
                    view.displayError(e.getMessage());
                    isSuccess = true;
                }
            }
        }
}
