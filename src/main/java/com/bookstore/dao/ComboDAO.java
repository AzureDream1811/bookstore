package com.bookstore.dao;

import com.bookstore.model.Combo;
import com.bookstore.model.ComboDetail;
import com.bookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComboDAO {

    public List<Combo> findAll() throws SQLException {
        List<Combo> combos = new ArrayList<>();
        String sql = "SELECT * FROM combo";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) combos.add(map(rs));
        }
        return combos;
    }

    public int insert(Combo combo) throws SQLException {
        String sql = "INSERT INTO combo (name, price, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, combo.getName());
            ps.setDouble(2, combo.getPrice());
            ps.setDate(3, Date.valueOf(combo.getStartDate()));
            ps.setDate(4, Date.valueOf(combo.getEndDate()));
            ps.setString(5, combo.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public void addDetail(int comboId, ComboDetail detail) throws SQLException {
        String sql = "INSERT INTO combo_detail (combo_id, book_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, comboId);
            ps.setInt(2, detail.getBookId());
            ps.setInt(3, detail.getQuantity());
            ps.executeUpdate();
        }
    }

    public void cancel(int comboId) throws SQLException {
        String sql = "UPDATE combo SET status = 'CANCELLED' WHERE combo_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, comboId);
            ps.executeUpdate();
        }
    }

    private Combo map(ResultSet rs) throws SQLException {
        return new Combo(rs.getInt("combo_id"), rs.getString("name"), rs.getDouble("price"),
                rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(), rs.getString("status"));
    }
}
