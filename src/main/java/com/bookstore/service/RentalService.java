package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.RentalDAO;
import com.bookstore.model.Book;
import com.bookstore.model.Rental;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RentalService {
    private static final int MAX_ACTIVE_RENTALS = 5;
    private static final double LATE_FEE_PER_DAY = 5000;

    private final RentalDAO rentalDAO = new RentalDAO();
    private final BookDAO bookDAO = new BookDAO();

    public Rental rentBook(int userId, int bookId, int days) throws SQLException {
        if (days < 1 || days > 30) throw new IllegalArgumentException("Thoi gian thue phai tu 1 den 30 ngay");
        Book book = bookDAO.findById(bookId);
        if (book == null || !book.isAvailable()) throw new IllegalStateException("Sach hien khong the thue");
        if (rentalDAO.countActiveByUser(userId) >= MAX_ACTIVE_RENTALS) {
            throw new IllegalStateException("Da dat gioi han thue toi da " + MAX_ACTIVE_RENTALS + " cuon");
        }
        Rental rental = new Rental(0, userId, bookId, LocalDate.now(), days, "RENTED");
        int id = rentalDAO.insert(rental);
        rental.setRentalId(id);
        bookDAO.updateStock(bookId, -1);
        return rental;
    }

    public double returnBook(int rentalId) throws SQLException {
        Rental rental = rentalDAO.findById(rentalId);
        if (rental == null || "RETURNED".equals(rental.getStatus())) {
            throw new IllegalArgumentException("Phieu thue khong ton tai hoac da tra");
        }
        LocalDate today = LocalDate.now();
        LocalDate dueDate = rental.getRentDate().plusDays(rental.getDays());
        long overdueDays = Math.max(0, ChronoUnit.DAYS.between(dueDate, today));
        double lateFee = overdueDays * LATE_FEE_PER_DAY;
        rentalDAO.complete(rentalId, today, lateFee);
        bookDAO.updateStock(rental.getBookId(), 1);
        return lateFee;
    }

    public List<Rental> search(String keyword) throws SQLException {
        return rentalDAO.search(keyword);
    }

    public List<Rental> listOverdueRentals() throws SQLException {
        return rentalDAO.findOverdue();
    }
}
