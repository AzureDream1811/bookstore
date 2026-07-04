package com.bookstore.model;

import java.util.List;

public class RevenueReportData {
    private List<RevenueResult> dailyResults;
    private RevenueResult totalResult;

    public RevenueReportData(List<RevenueResult> dailyResults, RevenueResult totalResult) {
        this.dailyResults = dailyResults;
        this.totalResult = totalResult;
    }

    public List<RevenueResult> getDailyResults() {
        return dailyResults;
    }

    public RevenueResult getTotalResult() {
        return totalResult;
    }
}