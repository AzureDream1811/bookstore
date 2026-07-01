package com.bookstore.dao;

import com.bookstore.model.InventoryDetail;
import com.bookstore.model.InventoryTicket;
import com.bookstore.util.DBConnection;

import java.sql.*;

public class InventoryDAO {

    public int createTicket(Connection conn, InventoryTicket ticket) throws SQLException {
        String sql = "INSERT INTO inventory_ticket (type, status) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ticket.getType());
            ps.setString(2, ticket.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public void addDetail(Connection conn, InventoryDetail detail) throws SQLException {
        String sql = "INSERT INTO inventory_detail (ticket_id, book_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detail.getTicketId());
            ps.setInt(2, detail.getBookId());
            ps.setInt(3, detail.getQuantity());
            ps.setDouble(4, detail.getUnitPrice());
            ps.executeUpdate();
        }
    }

    public void confirmTicket(Connection conn, int ticketId) throws SQLException {
        String sql = "UPDATE inventory_ticket SET status = 'CONFIRMED' WHERE ticket_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            ps.executeUpdate();
        }
    }

    public Connection newConnection() throws SQLException {
        return DBConnection.getConnection();
    }
}
