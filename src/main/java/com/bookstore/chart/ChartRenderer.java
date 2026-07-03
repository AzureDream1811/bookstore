package com.bookstore.chart;

import com.bookstore.model.RevenueReportData;

public interface ChartRenderer {
    void render(RevenueReportData data);
}