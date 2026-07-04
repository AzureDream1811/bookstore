package com.bookstore.controller;

import com.bookstore.chart.ChartType;
import com.bookstore.model.*;
import com.bookstore.service.ChartService;
import com.bookstore.service.ExportExcelService;
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
    private ExportExcelService exportService;

    public ReportController(ConsoleView view) {
        this.reportService = new ReportService();
        this.view = view;
        this.chartService = new ChartService();
        this.exportService = new ExportExcelService();

    }

    public void handleRevenueReportRequest(User currentUser) {
        boolean isSuccess = false;
        RevenueReportData reportData = null;
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            view.displayError("Truy cập bị từ chối: Chỉ Quản trị viên (ADMIN) mới được quyền xem báo cáo doanh thu.");
            return;
        }
        while (!isSuccess) {
            try {
                ReportFilter filter = view.getRevenueFilterInput();
                reportData = reportService.generateRevenueReport(filter);
                RevenueReportData result = reportService.generateRevenueReport(filter);
                view.displayRevenueResult(result);
                isSuccess = true;

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                view.displayError(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                view.displayError(e.getMessage());
                isSuccess = true;
            }
            handlePostReportActions(reportData, currentUser);
        }
    }

    private void handlePostReportActions(RevenueReportData reportData, User currentUser) {
        boolean inPostMenu = true;
        while (inPostMenu) {
            int actionChoice = view.showPostReportMenu();
            switch (actionChoice) {
                case 1 -> {
                    try {
                        exportService.exportRevenueReport(currentUser, reportData);
                    } catch (SecurityException e) {
                        view.displayError(e.getMessage());
                    } catch (Exception e) {
                        view.displayError(e.getMessage());
                    }
                }
                case 2 -> {
                    handleChartGenerationFlow(reportData);
                }
                case 0 -> inPostMenu = false;
                default -> view.displayError("Lựa chọn thao tác kế tiếp không hợp lệ.");
            }
        }
    }

    private void handleChartGenerationFlow(RevenueReportData reportData) {
        boolean isChartDone = false;
        while (!isChartDone) {
            int chartChoice = view.showChartTypeMenu();
            if (chartChoice == 0) return;
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
                chartService.processAndRenderChart(selectedType, reportData);
                isChartDone = true;
            } catch (IllegalArgumentException e) {
                view.displayError(e.getMessage());
            } catch (Exception e) {
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
