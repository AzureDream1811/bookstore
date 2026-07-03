package com.bookstore.controller;

import com.bookstore.model.Combo;
import com.bookstore.model.ComboDetail;
import com.bookstore.service.DiscountService;
import com.bookstore.view.ConsoleView;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DiscountController {
    private final ConsoleView view;
    private final DiscountService discountService = new DiscountService();
    private final ComboController comboController;

    public DiscountController(ConsoleView view) {
        this.view = view;
        this.comboController = new ComboController(view);
    }

    public void open() {
        boolean back = false;
        while (!back) {
            view.print("");
            view.print("--- QUAN LY GIAM GIA ---");
            view.print("1. Danh sach combo");
            view.print("2. Tao combo");
            view.print("3. Huy combo");
            view.print("4. Cap nhat combo");
            view.print("5. Tao voucher");
            view.print("6. Thong bao sale");
            view.print("0. Quay lai");
            switch (view.readInt("Chon chuc nang: ")) {
                case 1 -> listCombos();
                case 2 -> createCombo();
                case 3 -> cancelCombo();
                case 4 -> comboController.updateCombo();
                case 5 -> createVoucher();
                case 6 -> view.print("Thong bao: he thong da kich hoat chien dich giam gia.");
                case 0 -> back = true;
                default -> view.printError("Lua chon khong hop le");
            }
        }
    }

    public void listCombos() {
        try {
            List<Combo> combos = discountService.listCombos();
            if (combos.isEmpty()) {
                view.print("Chua co combo nao");
                return;
            }
            for (Combo combo : combos) {
                view.print(formatCombo(combo));
            }
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void createCombo() {
        try {
            String name = view.readLine("Ten combo: ");
            requireText(name, "Ten combo khong duoc de trong");
            double price = view.readDouble("Gia combo: ");
            if (price < 0) {
                throw new IllegalArgumentException("Gia combo khong hop le");
            }
            LocalDate startDate = readDate("Ngay bat dau (yyyy-MM-dd): ");
            LocalDate endDate = readDate("Ngay ket thuc (yyyy-MM-dd): ");
            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("Ngay ket thuc phai sau hoac bang ngay bat dau");
            }
            List<ComboDetail> items = collectComboItems();
            Combo combo = discountService.createCombo(name, price, startDate, endDate, items);
            view.print("Tao combo thanh cong, comboId=" + combo.getComboId());
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void cancelCombo() {
        int comboId = view.readInt("Nhap comboId can huy: ");
        try {
            discountService.cancelCombo(comboId);
            view.print("Da huy combo id=" + comboId);
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void createVoucher() {
        try {
            String code = view.readLine("Ma voucher: ");
            requireText(code, "Ma voucher khong duoc de trong");
            double discountValue = view.readDouble("Gia tri giam: ");
            double minOrderAmount = view.readDouble("Don toi thieu: ");
            if (discountValue <= 0 || minOrderAmount < 0) {
                throw new IllegalArgumentException("Gia tri voucher khong hop le");
            }
            LocalDate expiryDate = readDate("Ngay het han (yyyy-MM-dd): ");
            discountService.createVoucher(code, discountValue, minOrderAmount, expiryDate);
            view.print("Tao voucher thanh cong");
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private List<ComboDetail> collectComboItems() {
        List<ComboDetail> items = new ArrayList<>();
        view.print("Nhap danh sach sach trong combo (bookId=0 de ket thuc)");
        while (true) {
            int bookId = view.readInt("BookId: ");
            if (bookId == 0) {
                break;
            }
            int quantity = view.readInt("So luong: ");
            items.add(new ComboDetail(0, bookId, quantity));
        }
        return items;
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            String input = view.readLine(prompt);
            try {
                requireText(input, "Ngay khong duoc de trong");
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                view.printError("Ngay khong hop le, dung dinh dang yyyy-MM-dd");
            }
        }
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private String formatCombo(Combo combo) {
        return "[" + combo.getComboId() + "] " + combo.getName()
                + " - gia: " + combo.getPrice()
                + ", tu: " + combo.getStartDate()
                + ", den: " + combo.getEndDate()
                + ", trang thai: " + combo.getStatus()
                + ", active: " + combo.isActive();
    }
}
