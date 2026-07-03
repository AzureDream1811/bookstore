package com.bookstore.dao;

import com.bookstore.model.Combo;
import com.bookstore.model.ComboDetail;
import com.bookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComboDAO {

    // 2.1.1 / 2.2.1: hiển thị danh sách combo đang hoạt động (chưa bị xóa)
    public List<Combo> findAll() throws SQLException {
        List<Combo> combos = new ArrayList<>();
        String sql = "SELECT * FROM combo WHERE status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) combos.add(map(rs));
        }
        return combos;
    }

    // 2.1.2 / 2.1.3: lấy 1 combo (kèm danh sách sách) để hiển thị lên form sửa
    public Combo findById(int comboId) throws SQLException {
        Combo combo = null;
        String sql = "SELECT * FROM combo WHERE combo_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, comboId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) combo = map(rs);
            }
        }
        if (combo != null) {
            combo.setDetails(findDetailsByComboId(comboId));
        }
        return combo;
    }

    // Basic Flow bước 9-10: lưu combo + toàn bộ sách tham gia trong 1 transaction
    public int insert(Combo combo) throws SQLException {
        String sqlCombo = "INSERT INTO combo (name, price, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int comboId;
                try (PreparedStatement ps = conn.prepareStatement(sqlCombo, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, combo.getName());
                    ps.setDouble(2, combo.getPrice());
                    ps.setDate(3, Date.valueOf(combo.getStartDate()));
                    ps.setDate(4, Date.valueOf(combo.getEndDate()));
                    ps.setString(5, combo.getStatus());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        comboId = keys.getInt(1);
                    }
                }
                insertDetails(conn, comboId, combo.getDetails());
                conn.commit();
                combo.setComboId(comboId);
                return comboId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Alternative Flow 2.1.9 -> 2.1.10: cập nhật combo + thay toàn bộ danh sách sách
    public void update(Combo combo) throws SQLException {
        String sqlCombo = "UPDATE combo SET name = ?, price = ?, start_date = ?, end_date = ? WHERE combo_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlCombo)) {
                    ps.setString(1, combo.getName());
                    ps.setDouble(2, combo.getPrice());
                    ps.setDate(3, Date.valueOf(combo.getStartDate()));
                    ps.setDate(4, Date.valueOf(combo.getEndDate()));
                    ps.setInt(5, combo.getComboId());
                    ps.executeUpdate();
                }
                deleteDetails(conn, combo.getComboId());
                insertDetails(conn, combo.getComboId(), combo.getDetails());
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
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

    private void insertDetails(Connection conn, int comboId, List<ComboDetail> details) throws SQLException {
        if (details == null || details.isEmpty()) return;
        String sql = "INSERT INTO combo_detail (combo_id, book_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (ComboDetail d : details) {
                ps.setInt(1, comboId);
                ps.setInt(2, d.getBookId());
                ps.setInt(3, d.getQuantity());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteDetails(Connection conn, int comboId) throws SQLException {
        String sql = "DELETE FROM combo_detail WHERE combo_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, comboId);
            ps.executeUpdate();
        }
    }

    private List<ComboDetail> findDetailsByComboId(int comboId) throws SQLException {
        List<ComboDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM combo_detail WHERE combo_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, comboId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    details.add(new ComboDetail(
                            rs.getInt("combo_id"),
                            rs.getInt("book_id"),
                            rs.getInt("quantity")));
                }
            }
        }
        return details;
    }

    private Combo map(ResultSet rs) throws SQLException {
        return new Combo(rs.getInt("combo_id"), rs.getString("name"), rs.getDouble("price"),
                rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(), rs.getString("status"));
    }
}