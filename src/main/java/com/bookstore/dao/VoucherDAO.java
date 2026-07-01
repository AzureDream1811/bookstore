package com.bookstore.dao;

import com.bookstore.model.Voucher;
import com.bookstore.util.DBConnection;

import java.sql.*;

public class VoucherDAO {

    public Voucher findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM voucher WHERE voucher_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void insert(Voucher v) throws SQLException {
        String sql = "INSERT INTO voucher (voucher_code, discount_value, min_order_amount, expiry_date, used) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getVoucherCode());
            ps.setDouble(2, v.getDiscountValue());
            ps.setDouble(3, v.getMinOrderAmount());
            ps.setDate(4, Date.valueOf(v.getExpiryDate()));
            ps.setBoolean(5, v.isUsed());
            ps.executeUpdate();
        }
    }

    public void markUsed(Connection conn, String code) throws SQLException {
        String sql = "UPDATE voucher SET used = TRUE WHERE voucher_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.executeUpdate();
        }
    }

    private Voucher map(ResultSet rs) throws SQLException {
        return new Voucher(rs.getString("voucher_code"), rs.getDouble("discount_value"),
                rs.getDouble("min_order_amount"), rs.getDate("expiry_date").toLocalDate(), rs.getBoolean("used"));
    }
}
