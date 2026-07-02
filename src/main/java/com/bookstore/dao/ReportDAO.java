package com.bookstore.dao;

import com.bookstore.model.ReportFilter;
import com.bookstore.model.RevenueResult;
import com.bookstore.util.DBConnection;

import java.sql.*;

public class ReportDAO {
    public RevenueResult getRevenueByFilter(ReportFilter filter) throws SQLException {
        RevenueResult result = null;
        // SQL query sử dụng hàm SUM để gom nhóm dữ liệu.
        // Lọc trạng thái: Completed, Paid, Delivered.
        String sql = "SELECT " +
                "SUM(total_product_amount) as total_amount, " +
                "SUM(discount) as total_discount, " +
                "SUM(shipping_fee) as total_shipping, " +
                "SUM(refund_amount) as total_refund " +
                "FROM orders " +
                "WHERE created_date BETWEEN ? AND ? " +  "AND status IN ('Completed', 'Paid', 'Delivered')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(filter.getFromDate()));
            stmt.setDate(2, Date.valueOf(filter.getToDate()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Kiểm tra null nếu không có dữ liệu
                    if (rs.getObject("total_amount") == null) {
                        return null; // Phục vụ Exception Flow 6.2
                    }

                    result = new RevenueResult();
                    result.setTotalProductAmount(rs.getDouble("total_amount"));
                    result.setTotalDiscount(rs.getDouble("total_discount"));
                    result.setTotalShippingFee(rs.getDouble("total_shipping"));
                    result.setTotalRefund(rs.getDouble("total_refund"));
                    result.calculateNetRevenue();
                }
            }
        }
        return result;
    }
}
