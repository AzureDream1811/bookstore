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
    private static final double RENT_RATE = 0.15;
    private final RentalDAO rentalDAO = new RentalDAO();
    private final BookDAO bookDAO = new BookDAO();

    public Rental rentBook(int userId, int bookId, int days) throws SQLException {

        if (days < 1 || days > 30) {
            throw new IllegalArgumentException("Thoi gian thue phai tu 1 den 30 ngay");
        }

        Book book = bookDAO.findById(bookId);

        if (book == null) {
            throw new IllegalArgumentException("Khong tim thay sach");
        }

        if (!book.isAvailable()) {
            throw new IllegalStateException("Sach hien khong the thue");
        }

        if (rentalDAO.countActiveByUser(userId) >= MAX_ACTIVE_RENTALS) {
            throw new IllegalStateException("Ban da dat gioi han thue toi da 5 cuon");
        }

        Rental rental = new Rental(
                0,
                userId,
                bookId,
                LocalDate.now(),
                days,
                "RENTED"
        );

        rental.setRentalFee(book.getPrice() * RENT_RATE * days);
        int id = rentalDAO.insert(rental);

        if (id <= 0) {
            throw new SQLException("Khong tao duoc phieu thue");
        }

        rental.setRentalId(id);

        bookDAO.updateStock(bookId, -1);

        return rental;
    }
    public List<Book> getAvailableBooks() throws SQLException {
        return bookDAO.findAvailableBooks();
    }
    public double returnBook(int rentalId) throws SQLException {

        Rental rental = rentalDAO.findById(rentalId);

        if (rental == null) {
            throw new IllegalArgumentException("Khong tim thay phieu thue");
        }

        if (!"RENTED".equals(rental.getStatus())) {
            throw new IllegalStateException("Phieu thue khong hop le");
        }

        LocalDate today = LocalDate.now();

        long overdueDays = Math.max(
                0,
                ChronoUnit.DAYS.between(rental.getDueDate(), today)
        );

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
    public List<Rental> getAllRentals() throws SQLException {
        return rentalDAO.getAllRentals();
    }

    public List<Rental> searchRental(String keyword) throws SQLException {
        return rentalDAO.searchRental(keyword);
    }

    public boolean updateStatus(int rentalId, String status)
            throws SQLException {

        return rentalDAO.updateStatus(rentalId, status);
    }
}