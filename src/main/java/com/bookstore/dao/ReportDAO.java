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
                while (rs.next()) {
                    RevenueResult row = new RevenueResult();
                    row.setDate(rs.getDate("report_date").toLocalDate());
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

    public List<Object[]> getSoldQuantityByBook(java.time.LocalDate fromDate, java.time.LocalDate toDate) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT od.book_id AS book_id, SUM(od.quantity) AS qty " +
                "FROM order_detail od " +
                "JOIN orders o ON o.order_id = od.order_id " +
                "WHERE o.status = 'PAID' AND DATE(o.created_date) BETWEEN ? AND ? " +
                "GROUP BY od.book_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Object[]{rs.getInt("book_id"), rs.getInt("qty")});
                }
            }
        }
        return result;
    }

    public List<Object[]> getRentedQuantityByBook(java.time.LocalDate fromDate, java.time.LocalDate toDate) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT book_id, COUNT(*) AS qty " +
                "FROM rental " +
                "WHERE status IN ('RENTED', 'RETURNED') AND rent_date BETWEEN ? AND ? " +
                "GROUP BY book_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Object[]{rs.getInt("book_id"), rs.getInt("qty")});
                }
            }
        }
        return result;
    }
}
