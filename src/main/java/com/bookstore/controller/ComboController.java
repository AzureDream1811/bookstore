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

public class ComboController {
    private final ConsoleView view;
    private final DiscountService discountService = new DiscountService();

    public ComboController(ConsoleView view) {
        this.view = view;
    }

    // 2.1.1 / 2.2.1: hien thi danh sach combo hien co
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

    // Basic Flow buoc 2-11: tao combo moi
    public void createCombo() {
        try {
            String name = view.readLine("Ten combo: ");
            requireText(name, "Ten combo khong duoc de trong");

            double price = view.readDouble("Gia combo: ");

            LocalDate startDate = readDate("Ngay bat dau (yyyy-MM-dd): ");
            LocalDate endDate = readDate("Ngay ket thuc (yyyy-MM-dd): ");

            view.print("Nhap danh sach sach tham gia combo (bookId=0 de ket thuc)");
            List<ComboDetail> items = collectComboItems();
            if (items.isEmpty()) {
                view.printError("Combo phai co it nhat 1 sach, huy tao combo");
                return;
            }

            Combo combo = discountService.createCombo(name, price, startDate, endDate, items);
            view.print("Tao combo thanh cong, comboId=" + combo.getComboId());
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    // Alternative Flow 2.1.1 -> 2.1.11: cap nhat combo co san
    public void updateCombo() {
        try {
            List<Combo> combos = discountService.listCombos();
            if (combos.isEmpty()) {
                view.print("Chua co combo nao de cap nhat");
                return;
            }
            for (Combo combo : combos) {
                view.print(formatCombo(combo));
            }

            // 2.1.2: chon combo muon chinh sua
            int comboId = view.readInt("Nhap comboId muon sua (0 de huy): ");
            if (comboId == 0) {
                view.print("Da huy cap nhat combo");
                return;
            }
            Combo existing = discountService.getCombo(comboId);
            if (existing == null || !"ACTIVE".equals(existing.getStatus())) {
                view.printError("Combo khong ton tai hoac da bi xoa");
                return;
            }

            // 2.1.3: hien thi form nhap lieu (gia tri cu lam mac dinh)
            view.print("De trong (Enter) neu khong doi gia tri hien tai");

            String name = view.readLine("Ten combo (" + existing.getName() + "): ");
            if (name.isBlank()) name = existing.getName();

            String priceInput = view.readLine("Gia combo (" + existing.getPrice() + "): ");
            double price = priceInput.isBlank() ? existing.getPrice() : Double.parseDouble(priceInput);

            String startInput = view.readLine("Ngay bat dau (" + existing.getStartDate() + "): ");
            LocalDate startDate = startInput.isBlank() ? existing.getStartDate() : LocalDate.parse(startInput.trim());

            String endInput = view.readLine("Ngay ket thuc (" + existing.getEndDate() + "): ");
            LocalDate endDate = endInput.isBlank() ? existing.getEndDate() : LocalDate.parse(endInput.trim());

            // 2.1.5: chinh sua danh sach sach tham gia (nhap lai toan bo danh sach moi)
            view.print("Combo hien co " + existing.getDetails().size() + " dau sach");
            view.print("Nhap lai toan bo danh sach sach cho combo (bookId=0 de ket thuc)");
            List<ComboDetail> items = collectComboItems();
            if (items.isEmpty()) {
                view.printError("Combo phai co it nhat 1 sach, huy cap nhat");
                return;
            }

            discountService.updateCombo(comboId, name, price, startDate, endDate, items);
            view.print("Cap nhat combo thanh cong");
        } catch (NumberFormatException e) {
            view.printError("Gia tri nhap khong hop le");
        } catch (DateTimeParseException e) {
            view.printError("Ngay khong hop le, dung dinh dang yyyy-MM-dd");
        } catch (IllegalArgumentException | SQLException e) {
            view.printError(e.getMessage());
        }
    }

    // Alternative Flow 2.2.1 -> 2.2.5: xoa 1 combo
    public void cancelCombo() {
        try {
            List<Combo> combos = discountService.listCombos();
            if (combos.isEmpty()) {
                view.print("Chua co combo nao de xoa");
                return;
            }
            for (Combo combo : combos) {
                view.print(formatCombo(combo));
            }

            // 2.2.2: chon combo muon xoa (0 de huy)
            int comboId = view.readInt("Nhap comboId muon xoa (0 de huy): ");
            if (comboId == 0) {
                view.print("Da huy thao tac xoa");
                return;
            }

            // 2.2.3 / 2.2.4: xac nhan xoa
            String confirm = view.readLine("Xac nhan xoa combo id=" + comboId + "? (y/n): ");
            if (!confirm.equalsIgnoreCase("y")) {
                view.print("Da huy thao tac xoa");
                return;
            }

            discountService.cancelCombo(comboId);
            view.print("Da xoa combo id=" + comboId);
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    private List<ComboDetail> collectComboItems() {
        List<ComboDetail> items = new ArrayList<>();
        while (true) {
            int bookId = view.readInt("BookId: ");
            if (bookId == 0) break;
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
                return LocalDate.parse(input.trim());
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