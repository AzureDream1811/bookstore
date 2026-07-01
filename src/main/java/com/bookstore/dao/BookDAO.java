package com.bookstore.dao;

import com.bookstore.model.Book;
import com.bookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public List<Book> findAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book ORDER BY title";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) books.add(map(rs));
        }
        return books;
    }

    public Book findById(int bookId) throws SQLException {
        String sql = "SELECT * FROM book WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Book> search(String keyword) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE title LIKE ? OR author LIKE ? OR genre LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) books.add(map(rs));
            }
        }
        return books;
    }

    public int insert(Book book) throws SQLException {
        String sql = "INSERT INTO book (title, author, genre, price, rent_price_per_day, stock_quantity, is_faulty, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, book);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public void update(Book book) throws SQLException {
        String sql = "UPDATE book SET title=?, author=?, genre=?, price=?, rent_price_per_day=?, " +
                "stock_quantity=?, is_faulty=?, status=? WHERE book_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, book);
            ps.setInt(9, book.getBookId());
            ps.executeUpdate();
        }
    }

    public void updateStock(int bookId, int delta) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            updateStock(conn, bookId, delta);
        }
    }

    public void updateStock(Connection conn, int bookId, int delta) throws SQLException {
        String sql = "UPDATE book SET stock_quantity = stock_quantity + ? WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        }
    }

    public void setStatus(int bookId, String status) throws SQLException {
        String sql = "UPDATE book SET status = ? WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        }
    }

    public void markFaulty(int bookId, boolean faulty) throws SQLException {
        String sql = "UPDATE book SET is_faulty = ? WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, faulty);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, Book b) throws SQLException {
        ps.setString(1, b.getTitle());
        ps.setString(2, b.getAuthor());
        ps.setString(3, b.getGenre());
        ps.setDouble(4, b.getPrice());
        ps.setDouble(5, b.getRentPricePerDay());
        ps.setInt(6, b.getStockQuantity());
        ps.setBoolean(7, b.isFaulty());
        ps.setString(8, b.getStatus());
    }

    private Book map(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
                rs.getString("genre"), rs.getDouble("price"), rs.getDouble("rent_price_per_day"),
                rs.getInt("stock_quantity"), rs.getBoolean("is_faulty"), rs.getString("status")
        );
    }
}
