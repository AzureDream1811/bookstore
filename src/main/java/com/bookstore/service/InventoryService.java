package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.InventoryDAO;
import com.bookstore.model.Book;
import com.bookstore.model.InventoryDetail;
import com.bookstore.model.InventoryTicket;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class InventoryService {
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final BookDAO bookDAO = new BookDAO();

    /** Lap phieu nhap hang: cong ton kho cho tung sach trong danh sach. */
    public int createGoodsReceipt(List<InventoryDetail> items) throws SQLException {
        if (items.isEmpty()) throw new IllegalArgumentException("Phieu nhap can it nhat 1 san pham");
        try (Connection conn = inventoryDAO.newConnection()) {
            conn.setAutoCommit(false);
            try {
                int ticketId = inventoryDAO.createTicket(conn, new InventoryTicket(0, "IN", "PENDING"));
                for (InventoryDetail item : items) {
                    item.setTicketId(ticketId);
                    inventoryDAO.addDetail(conn, item);
                    bookDAO.updateStock(conn, item.getBookId(), item.getQuantity());
                }
                inventoryDAO.confirmTicket(conn, ticketId);
                conn.commit();
                return ticketId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /** Lap phieu xuat hang: kiem tra du ton kho truoc khi tru. */
    public int createGoodsIssue(List<InventoryDetail> items) throws SQLException {
        if (items.isEmpty()) throw new IllegalArgumentException("Phieu xuat can it nhat 1 san pham");
        for (InventoryDetail item : items) {
            Book book = bookDAO.findById(item.getBookId());
            if (book == null) throw new IllegalArgumentException("Khong tim thay sach id=" + item.getBookId());
            if (book.getStockQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Sach '" + book.getTitle() + "' khong du ton kho de xuat");
            }
        }
        try (Connection conn = inventoryDAO.newConnection()) {
            conn.setAutoCommit(false);
            try {
                int ticketId = inventoryDAO.createTicket(conn, new InventoryTicket(0, "OUT", "PENDING"));
                for (InventoryDetail item : items) {
                    item.setTicketId(ticketId);
                    inventoryDAO.addDetail(conn, item);
                    bookDAO.updateStock(conn, item.getBookId(), -item.getQuantity());
                }
                inventoryDAO.confirmTicket(conn, ticketId);
                conn.commit();
                return ticketId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
