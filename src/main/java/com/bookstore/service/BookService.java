package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.model.Book;
import com.bookstore.model.Page;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BookService {
    private final BookDAO bookDAO = new BookDAO();

    public List<Book> listAll() throws SQLException {
        return bookDAO.findAll();
    }

    public Page<Book> listPage(int page, int pageSize) throws SQLException {
        List<Book> books = bookDAO.findPage(page, pageSize);
        int totalBooks = bookDAO.countAll();
        return new Page<>(books, page, pageSize, totalBooks);
    }

    public List<Book> search(String keyword) throws SQLException {
        return bookDAO.search(keyword);
    }

    public List<Book> recommendBooks(String keyword, int limit) throws SQLException {
        List<Book> matches = search(keyword);
        if (matches.isEmpty()) {
            return List.of();
        }

        Book anchor = matches.get(0);
        List<Book> candidates = new ArrayList<>();
        for (Book book : listAll()) {
            if (book.getBookId() == anchor.getBookId()) {
                continue;
            }
            if (!book.isAvailable()) {
                continue;
            }
            boolean sameGenre = anchor.getGenre() != null && !anchor.getGenre().isBlank()
                    && anchor.getGenre().equalsIgnoreCase(book.getGenre());
            boolean sameAuthor = anchor.getAuthor() != null && !anchor.getAuthor().isBlank()
                    && anchor.getAuthor().equalsIgnoreCase(book.getAuthor());
            if (sameGenre || sameAuthor) {
                candidates.add(book);
            }
        }

        return candidates.stream()
                .sorted(Comparator.comparingInt(Book::getStockQuantity).reversed()
                        .thenComparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
                .limit(Math.max(limit, 0))
                .collect(Collectors.toList());
    }

    public List<Book> lowStockBooks(int threshold) throws SQLException {
        return listAll().stream()
                .filter(book -> book.getStockQuantity() <= threshold)
                .sorted(Comparator.comparingInt(Book::getStockQuantity)
                        .thenComparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Book getById(int bookId) throws SQLException {
        return bookDAO.findById(bookId);
    }

    public Book addBook(String title, String author, String genre, double price,
                         double rentPrice, int stock) throws SQLException {
        validate(title, author, genre, price, rentPrice, stock);
        Book book = new Book(0, title, author, genre, price, rentPrice, stock, false, "ACTIVE");
        int id = bookDAO.insert(book);
        book.setBookId(id);
        return book;
    }

    public void updateBook(Book book) throws SQLException {
        validate(book.getTitle(), book.getAuthor(), book.getGenre(), book.getPrice(),
                book.getRentPricePerDay(), book.getStockQuantity());
        bookDAO.update(book);
    }

    public void hideBook(int bookId) throws SQLException {
        bookDAO.setStatus(bookId, "INACTIVE");
    }

    public void markFaulty(int bookId, boolean faulty) throws SQLException {
        bookDAO.markFaulty(bookId, faulty);
    }

    private void validate(String title, String author, String genre, double price, double rentPrice, int stock) {
        if (title == null || title.isBlank()
                || author == null || author.isBlank()
                || genre == null || genre.isBlank()) {
            throw new IllegalArgumentException("Vui long nhap day du thong tin");
        }
        if (price < 0 || rentPrice < 0 || stock < 0) {
            throw new IllegalArgumentException("Gia hoac so luong khong hop le");
        }
    }
}
