package com.bookstore.dao;

import com.bookstore.model.ReportFilter;
import com.bookstore.model.RevenueResult;
import com.bookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    public List<RevenueResult> getRevenueByFilter(ReportFilter filter) throws SQLException {
        List<RevenueResult> list = new ArrayList<>();
        // SQL query sử dụng hàm SUM để gom nhóm dữ liệu.
        // Lọc trạng thái: Completed, Paid, Delivered.
        String sql = "SELECT DATE(created_date) as report_date, " +
                "SUM(total_product_amount) as total_amount, " +
                "SUM(discount) as total_discount, " +
                "SUM(shipping_fee) as total_shipping, " +
                "SUM(refund_amount) as total_refund " +
                "FROM orders " +
                "WHERE DATE(created_date) BETWEEN ? AND ? " +
                "AND status IN ('Completed', 'Paid', 'Delivered') " +
                "GROUP BY report_date " +
                "ORDER BY report_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(filter.getFromDate()));
            stmt.setDate(2, Date.valueOf(filter.getToDate()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { // Đổi từ if (rs.next()) thành while để lấy nhiều dòng
                    RevenueResult row = new RevenueResult();
                    row.setDate(rs.getDate("report_date").toLocalDate()); // Lưu ngày
                    row.setTotalProductAmount(rs.getDouble("total_amount"));
                    row.setTotalDiscount(rs.getDouble("total_discount"));
                    row.setTotalShippingFee(rs.getDouble("total_shipping"));
                    row.setTotalRefund(rs.getDouble("total_refund"));
                    row.calculateNetRevenue();
                    list.add(row);
                }
            }
        }
        return list;
    }
}
