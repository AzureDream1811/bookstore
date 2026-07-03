package com.bookstore.service;

import com.bookstore.chart.*;
import com.bookstore.chart.ChartType;
import com.bookstore.model.RevenueReportData;
import java.util.List;

public class ChartService {

    public void processAndRenderChart(ChartType type, RevenueReportData data) throws Exception {
        // 1. Kiểm tra dữ liệu có sẵn (Pre-condition)
        if (data == null || data.getDailyResults().isEmpty()) {
            throw new IllegalArgumentException("Dữ liệu không hợp lệ: Không tìm thấy dữ liệu báo cáo.");
        }

        // 2. Kiểm tra tính phù hợp của loại biểu đồ dựa trên Business Rules
        validateChartRules(type, data.getDailyResults().size());

        // 3. Khởi tạo Renderer tương ứng (Factory Pattern đơn giản)
        ChartRenderer renderer = switch (type) {
            case BAR -> new ConsoleBarChartRenderer();
            case PIE -> new ConsolePieChartRenderer();
            case LINE -> new ConsoleLineChartRenderer();
        };

        // 4. Tiến hành kết xuất biểu đồ
        try {
            renderer.render(data);
        } catch (Exception e) {
            throw new Exception("Hệ thống gặp lỗi không thể render biểu đồ lúc này.", e);
        }
    }

    private void validateChartRules(ChartType type, int dataSize) throws IllegalArgumentException {
        // Rule: Line chart dùng cho dữ liệu theo thời gian (cần chuỗi thời gian liên tiếp từ 2 mốc trở lên)
        if (type == ChartType.LINE && dataSize < 2) {
            throw new IllegalArgumentException("Loại biểu đồ không phù hợp với dữ liệu: Biểu đồ đường yêu cầu dữ liệu biến động từ 2 ngày trở lên.");
        }

        // Rule: Pie chart chỉ dùng tối ưu khi phân loại cấu thành dòng tiền đơn lẻ
        if (type == ChartType.PIE && dataSize > 31) {
            throw new IllegalArgumentException("Loại biểu đồ không phù hợp với dữ liệu: Quá nhiều mốc thời gian, không thể gom nhóm vào biểu đồ tròn.");
        }
    }
}