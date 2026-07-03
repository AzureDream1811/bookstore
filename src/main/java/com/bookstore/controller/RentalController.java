package com.bookstore.controller;

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
            view.printError("Can dang nhap truoc khi thue sach");
            return null;
        }
        int bookId = view.readInt("Nhap bookId: ");
        int days = view.readInt("So ngay thue: ");
        try {
            if (bookId < 1) {
                throw new IllegalArgumentException("BookId khong hop le");
            }
            Rental rental = rentalService.rentBook(currentUser.getUserId(), bookId, days);
            view.print("Thue sach thanh cong, rentalId=" + rental.getRentalId());
        } catch (IllegalArgumentException | IllegalStateException | SQLException e) {
            view.printError(e.getMessage());
        }
        return currentUser;
    }

    public void returnBook() {
        int rentalId = view.readInt("Nhap rentalId: ");
        try {
            if (rentalId < 1) {
                throw new IllegalArgumentException("RentalId khong hop le");
            }
            double lateFee = rentalService.returnBook(rentalId);
            view.print(String.format("Tra sach thanh cong. Phi tre han: %.0f", lateFee));
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void searchRentals() {
        String keyword = view.readLine("Tu khoa tim kiem phieu thue: ");
        try {
            requireText(keyword, "Tu khoa tim kiem khong duoc de trong");
            List<Rental> rentals = rentalService.search(keyword);
            if (rentals.isEmpty()) {
                view.print("Khong tim thay phieu thue");
                return;
            }
            for (Rental rental : rentals) {
                view.print("[" + rental.getRentalId() + "] user=" + rental.getUserId()
                        + ", book=" + rental.getBookId()
                        + ", ngay thue=" + rental.getRentDate()
                        + ", so ngay=" + rental.getDays()
                        + ", trang thai=" + rental.getStatus());
            }
        } catch (SQLException | IllegalArgumentException e) {
            view.printError(e.getMessage());
        }
    }

    public void showOverdueRentals() {
        try {
            List<Rental> rentals = rentalService.listOverdueRentals();
            if (rentals.isEmpty()) {
                view.print("Khong co phieu thue qua han");
                return;
            }
            for (Rental rental : rentals) {
                view.print("[" + rental.getRentalId() + "] user=" + rental.getUserId()
                        + ", book=" + rental.getBookId()
                        + ", ngay thue=" + rental.getRentDate()
                        + ", so ngay=" + rental.getDays());
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
