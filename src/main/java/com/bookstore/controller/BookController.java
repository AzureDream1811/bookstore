package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import com.bookstore.view.ConsoleView;

import java.sql.SQLException;
import java.util.List;

public class BookController {
    private final BookService bookService = new BookService();
    private final ConsoleView view;

    public BookController(ConsoleView view) { this.view = view; }

    public void listAll() {
        try {
            List<Book> books = bookService.listAll();
            if (books.isEmpty()) { view.print("Chua co sach nao"); return; }
            books.forEach(b -> view.print(b.toString()));
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void search() {
        String keyword = view.readLine("Tu khoa (ten/tac gia/the loai): ");
        try {
            List<Book> books = bookService.search(keyword);
            if (books.isEmpty()) { view.print("Khong tim thay sach phu hop"); return; }
            books.forEach(b -> view.print(b.toString()));
            List<Book> recommendations = bookService.recommendBooks(keyword, 5);
            if (!recommendations.isEmpty()) {
                view.print("--- Goi y sach lien quan ---");
                recommendations.forEach(b -> view.print(b.toString()));
            }
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void recommend() {
        String keyword = view.readLine("Nhap tu khoa de goi y sach: ");
        try {
            List<Book> recommendations = bookService.recommendBooks(keyword, 5);
            if (recommendations.isEmpty()) {
                view.print("Khong co sach goi y phu hop");
                return;
            }
            recommendations.forEach(b -> view.print(b.toString()));
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void addBook() {
        String title = view.readLine("Ten sach: ");
        String author = view.readLine("Tac gia: ");
        String genre = view.readLine("The loai: ");
        double price = view.readDouble("Gia ban: ");
        double rentPrice = view.readDouble("Gia thue/ngay: ");
        int stock = view.readInt("So luong ton: ");
        try {
            Book book = bookService.addBook(title, author, genre, price, rentPrice, stock);
            view.print("Them sach thanh cong, bookId=" + book.getBookId());
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void editBook() {
        int id = view.readInt("Nhap bookId can sua: ");
        try {
            Book book = bookService.getById(id);
            if (book == null) { view.printError("Khong tim thay sach"); return; }
            view.print("Hien tai: " + book);
            String title = view.readLine("Ten sach moi (Enter de giu nguyen): ");
            if (!title.isBlank()) book.setTitle(title);
            String priceStr = view.readLine("Gia moi (Enter de giu nguyen): ");
            if (!priceStr.isBlank()) book.setPrice(Double.parseDouble(priceStr));
            String stockStr = view.readLine("So luong ton moi (Enter de giu nguyen): ");
            if (!stockStr.isBlank()) book.setStockQuantity(Integer.parseInt(stockStr));
            bookService.updateBook(book);
            view.print("Cap nhat thanh cong");
        } catch (NumberFormatException e) {
            view.printError("Gia tri nhap khong hop le");
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void hideBook() {
        int id = view.readInt("Nhap bookId can an: ");
        try {
            bookService.hideBook(id);
            view.print("Da an sach id=" + id);
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void markFaulty() {
        int id = view.readInt("Nhap bookId bi loi: ");
        try {
            bookService.markFaulty(id, true);
            view.print("Da danh dau sach id=" + id + " la LOI");
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }
}
