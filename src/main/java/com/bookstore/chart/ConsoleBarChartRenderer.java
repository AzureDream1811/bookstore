package com.bookstore.chart;

import com.bookstore.model.RevenueReportData;
import com.bookstore.model.RevenueResult;
import java.time.format.DateTimeFormatter;

public class ConsoleBarChartRenderer implements ChartRenderer {
    @Override
    public void render(RevenueReportData data) {
        System.out.println("\n--- [RENDER] BIỂU ĐỒ CỘT: SO SÁNH DOANH THU THUẦN GIỮA CÁC NGÀY ---");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        double maxNet = data.getDailyResults().stream()
                .mapToDouble(RevenueResult::getNetRevenue)
                .max().orElse(1.0);
        if (maxNet <= 0) maxNet = 1.0;

        for (RevenueResult row : data.getDailyResults()) {
            String dateStr = row.getDate().format(fmt);
            int barLength = (int) Math.max(1, (row.getNetRevenue() * 30) / maxNet);
            System.out.printf("%s | %s (%.1f)\n", dateStr, "#".repeat(barLength), row.getNetRevenue());
        }
        System.out.println("------------------------------------------------------------------");
    }
}