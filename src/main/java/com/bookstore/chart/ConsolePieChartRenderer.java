package com.bookstore.chart;

import com.bookstore.model.RevenueReportData;
import com.bookstore.model.RevenueResult;

public class ConsolePieChartRenderer implements ChartRenderer {
    @Override
    public void render(RevenueReportData data) {
        System.out.println("\n--- [RENDER] BIỂU ĐỒ TRÒN: TỶ LỆ CẤU THÀNH TỔNG DÒNG TIỀN ---");
        RevenueResult total = data.getTotalResult();

        double totalFlow = total.getTotalProductAmount() + total.getTotalShippingFee();
        if (totalFlow <= 0) {
            System.out.println("[!] Không có dòng tiền phát sinh để phân tích tỷ lệ phần trăm.");
            return;
        }

        printPieSlice("Tiền sản phẩm ", total.getTotalProductAmount(), totalFlow);
        printPieSlice("Phí vận chuyển", total.getTotalShippingFee(), totalFlow);
        printPieSlice("Voucher/Combo ", total.getTotalDiscount(), totalFlow);
        printPieSlice("Tiền hoàn trả ", total.getTotalRefund(), totalFlow);
    }

    private void printPieSlice(String label, double value, double total) {
        double percentage = (value / total) * 100;
        int blocks = (int) (percentage / 4); // Mỗi block tương ứng ~4%
        System.out.printf("%s: [%s%s] %.2f%%\n",
                label, "█".repeat(blocks), "░".repeat(25 - blocks), percentage);
    }
}