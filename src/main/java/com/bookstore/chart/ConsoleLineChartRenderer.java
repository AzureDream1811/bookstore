package com.bookstore.chart;

import com.bookstore.model.RevenueReportData;
import com.bookstore.model.RevenueResult;
import java.time.format.DateTimeFormatter;

public class ConsoleLineChartRenderer implements ChartRenderer {
    @Override
    public void render(RevenueReportData data) {
        System.out.println("\n--- [RENDER] BIỂU ĐỒ ĐƯỜNG: XU HƯỚNG TĂNG TRƯỞNG DOANH THU THUẦN ---");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

        double prevRevenue = -1;
        for (RevenueResult row : data.getDailyResults()) {
            String dateStr = row.getDate().format(fmt);
            String trend = "";

            if (prevRevenue != -1) {
                if (row.getNetRevenue() > prevRevenue) trend = " 📈 [Tăng]";
                else if (row.getNetRevenue() < prevRevenue) trend = " 📉 [Giảm]";
                else trend = " ➡ [Bình ổn]";
            } else {
                trend = "  [Bắt đầu]";
            }

            System.out.printf("%s : %.1f VNĐ %s\n", dateStr, row.getNetRevenue(), trend);
            prevRevenue = row.getNetRevenue();
        }
        System.out.println("------------------------------------------------------------------");
    }
}