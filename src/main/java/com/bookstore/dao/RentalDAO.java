package com.bookstore.dao;

import com.bookstore.model.Rental;
import com.bookstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    public int countActiveByUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM rental WHERE user_id = ? AND status = 'RENTED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }
        }
    }

    public int insert(Rental rental) throws SQLException {

        String sql = " INSERT INTO rental (user_id, book_id, rent_date, due_date, days, rental_fee, status) VALUES (?, ?, ?, ?, ?, ?, ?) ";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, rental.getUserId());
            ps.setInt(2, rental.getBookId());
            ps.setDate(3, Date.valueOf(rental.getRentDate()));
            ps.setDate(4, Date.valueOf(rental.getDueDate()));
            ps.setInt(5, rental.getDays());
            ps.setDouble(6, rental.getRentalFee());
            ps.setString(7, rental.getStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public Rental findById(int rentalId) throws SQLException {
        String sql = "SELECT * FROM rental WHERE rental_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rentalId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Rental> search(String keyword) throws SQLException {
        List<Rental> list = new ArrayList<>();
        String sql = "SELECT * FROM rental WHERE CAST(rental_id AS CHAR) LIKE ? OR CAST(user_id AS CHAR) LIKE ? OR status LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<Rental> findOverdue() throws SQLException {
        List<Rental> list = new ArrayList<>();
        String sql = "SELECT * FROM rental WHERE status='RENTED' AND due_date < CURDATE() ";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void complete(int rentalId, java.time.LocalDate returnDate, double lateFee) throws SQLException {
        String sql = "UPDATE rental SET status = 'RETURNED', return_date = ?, late_fee = ? WHERE rental_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(returnDate));
            ps.setDouble(2, lateFee);
            ps.setInt(3, rentalId);
            ps.executeUpdate();
        }
    }

    private Rental map(ResultSet rs) throws SQLException {

        Rental r = new Rental(
                rs.getInt("rental_id"),
                rs.getInt("user_id"),
                rs.getInt("book_id"),
                rs.getDate("rent_date").toLocalDate(),
                rs.getInt("days"),
                rs.getString("status")
        );

        Date due = rs.getDate("due_date");

        if (due != null) {
            r.setDueDate(due.toLocalDate());
        }

        Date ret = rs.getDate("return_date");

        if (ret != null) {
            r.setReturnDate(ret.toLocalDate());
        }

        r.setRentalFee(rs.getDouble("rental_fee"));
        r.setLateFee(rs.getDouble("late_fee"));

        return r;
    }
    public List<Rental> getAllRentals() throws SQLException {

        List<Rental> list = new ArrayList<>();

        String sql = "SELECT * FROM rental ORDER BY rental_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        }

        return list;
    }

    public List<Rental> searchRental(String keyword) throws SQLException {
            return search(keyword);

    }

    public boolean updateStatus(int rentalId, String status) throws SQLException {

        String sql =
                "UPDATE rental SET status=? WHERE rental_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, rentalId);

            return ps.executeUpdate() > 0;
        }
    }
    public List<Rental> findRentalTickets(int userId) throws SQLException {

        List<Rental> list = new ArrayList<>();

        String sql = " SELECT * FROM rental WHERE user_id = ? AND status = 'RENTED' ORDER BY rental_id DESC ";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }
        }

        return list;
    }
}
