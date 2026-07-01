package com.bookstore.dao;

import com.bookstore.model.Order;
import com.bookstore.model.OrderDetail;
import com.bookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public int createOrder(Connection conn, Order order) throws SQLException {
        String sql = "INSERT INTO orders (user_id, total_amount, status, voucher_code) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserId());
            ps.setDouble(2, order.getTotalAmount());
            ps.setString(3, order.getStatus());
            ps.setString(4, order.getVoucherCode());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public void addDetail(Connection conn, OrderDetail detail) throws SQLException {
        String sql = "INSERT INTO order_detail (order_id, book_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detail.getOrderId());
            ps.setInt(2, detail.getBookId());
            ps.setInt(3, detail.getQuantity());
            ps.setDouble(4, detail.getPrice());
            ps.executeUpdate();
        }
    }

    public void updateTotal(Connection conn, int orderId, double total) throws SQLException {
        String sql = "UPDATE orders SET total_amount = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, total);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    public void updateStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    public void updateStatus(Connection conn, int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    public String findStatus(int orderId) throws SQLException {
        String sql = "SELECT status FROM orders WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("status") : null;
            }
        }
    }

    public Order findById(int orderId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Order order = mapOrder(rs);
                order.setDetails(findDetails(conn, orderId));
                return order;
            }
        }
    }

    public List<Order> search(String criteria) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE CAST(order_id AS CHAR) LIKE ? OR status LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + criteria + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) orders.add(mapOrder(rs));
            }
        }
        return orders;
    }

    public List<Order> findByUserId(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) orders.add(mapOrder(rs));
            }
        }
        return orders;
    }

    public double sumRevenue(String fromDate, String toDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS revenue FROM orders " +
                "WHERE status = 'PAID' AND created_date BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fromDate);
            ps.setString(2, toDate);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("revenue") : 0;
            }
        }
    }

    public List<Object[]> bestSellers(int limit) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT od.book_id, b.title, SUM(od.quantity) AS qty FROM order_detail od " +
                "JOIN book b ON b.book_id = od.book_id " +
                "JOIN orders o ON o.order_id = od.order_id AND o.status = 'PAID' " +
                "GROUP BY od.book_id, b.title ORDER BY qty DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Object[]{rs.getInt("book_id"), rs.getString("title"), rs.getInt("qty")});
                }
            }
        }
        return result;
    }

    private List<OrderDetail> findDetails(Connection conn, int orderId) throws SQLException {
        List<OrderDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM order_detail WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    details.add(new OrderDetail(rs.getInt("order_id"), rs.getInt("book_id"),
                            rs.getInt("quantity"), rs.getDouble("price")));
                }
            }
        }
        return details;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        return new Order(rs.getInt("order_id"), rs.getInt("user_id"), rs.getDouble("total_amount"),
                rs.getString("status"), rs.getString("voucher_code"));
    }
}
