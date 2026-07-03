package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.Rental;
import com.bookstore.model.User;
import com.bookstore.service.RentalService;
import com.bookstore.view.ConsoleView;

import java.sql.SQLException;
import java.util.List;

public class RentalController {
    private final ConsoleView view;
    private final RentalService rentalService = new RentalService();

    public RentalController(ConsoleView view) {
        this.view = view;
    }

    public User open(User currentUser) {
        boolean back = false;
        while (!back) {
            int choice = view.showRentalMenu();
            switch (choice) {
                case 1 -> currentUser = rentBook(currentUser);
                case 2 -> returnBook();
                case 3 -> searchRentals();
                case 4 -> showOverdueRentals();
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
        return currentUser;
    }

    public User rentBook(User currentUser) {

        if (currentUser == null) {
            view.printError("Ban can dang nhap truoc.");
            return null;
        }

        try {

            // Hiển thị danh sách sách trước
            List<Book> books = rentalService.getAvailableBooks();

            if (books.isEmpty()) {
                view.print("Khong co sach nao co the thue.");
                return currentUser;
            }

            view.showRentalInfo();
            view.print("===== DANH SACH SACH CO THE THUE =====");

            for (Book b : books) {
                view.print(String.format(
                        "[%d] %s | Tac gia: %s | Thue/ngay: %.0f | Con: %d",
                        b.getBookId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getRentPricePerDay(),
                        b.getStockQuantity()
                ));
            }

            // Sau đó mới nhập
            int bookId = view.inputBookId();
            int days = view.inputRentalDays();

            Rental rental = rentalService.rentBook(
                    currentUser.getUserId(),
                    bookId,
                    days
            );

            view.showRentalSuccess(
                    rental.getRentalId(),
                    rental.getRentalFee()
            );

        } catch (Exception e) {
            view.printError(e.getMessage());
        }

        return currentUser;
    }

    public void returnBook() {

        int rentalId = view.inputRentalId();

        try {

            double lateFee = rentalService.returnBook(rentalId);

            view.showReturnSuccess(lateFee);

        } catch (Exception e) {
            view.printError(e.getMessage());
        }
    }

    public void searchRentals() {

        String keyword = view.inputKeyword();

        try {

            requireText(keyword, "Tu khoa khong duoc de trong.");

            List<Rental> list = rentalService.search(keyword);

            if (list.isEmpty()) {
                view.print("Khong tim thay phieu thue.");
                return;
            }

            for (Rental r : list) {

                view.print("--------------------------------");

                view.print("Ma phieu : " + r.getRentalId());
                view.print("User     : " + r.getUserId());
                view.print("Book     : " + r.getBookId());
                view.print("Ngay thue: " + r.getRentDate());
                view.print("Han tra  : " + r.getDueDate());
                view.print("So ngay  : " + r.getDays());
                view.print("Tien thue: " + r.getRentalFee());
                view.print("Tre han  : " + r.getLateFee());
                view.print("Trang thai: " + r.getStatus());

            }

        } catch (Exception e) {
            view.printError(e.getMessage());
        }

    }

    public void showOverdueRentals() {
        try {
            List<Rental> list = rentalService.listOverdueRentals();
            if (list.isEmpty()) {
                view.print("Khong co phieu thue qua han.");
                return;
            }
            for (Rental r : list) {
                view.print("--------------------------------");
                view.print("Rental : " + r.getRentalId());
                view.print("Book   : " + r.getBookId());
                view.print("User   : " + r.getUserId());
                view.print("Ngay thue : " + r.getRentDate());
                view.print("Han tra   : " + r.getDueDate());

            }
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
