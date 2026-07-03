package com.bookstore.chart;

public enum ChartType {
    BAR("Biểu đồ cột (Bar Chart) - So sánh các nhóm"),
    PIE("Biểu đồ tròn (Pie Chart) - Tỷ lệ thành phần"),
    LINE("Biểu đồ đường (Line Chart) - Xu hướng theo thời gian");

    private final String description;
    ChartType(String description) { this.description = description; }
    public String getDescription() { return description; }
}