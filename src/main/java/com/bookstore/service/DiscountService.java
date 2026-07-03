package com.bookstore.service;

import com.bookstore.dao.ComboDAO;
import com.bookstore.dao.VoucherDAO;
import com.bookstore.model.Book;
import com.bookstore.model.Combo;
import com.bookstore.model.ComboDetail;
import com.bookstore.model.Voucher;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class DiscountService {
    private final ComboDAO comboDAO = new ComboDAO();
    private final VoucherDAO voucherDAO = new VoucherDAO();
    private final BookService bookService = new BookService();

    // ================= COMBO =================

    //tạo combo mới
    public Combo createCombo(String name, double price, LocalDate start, LocalDate end,
                              List<ComboDetail> bookItems) throws SQLException {
        validateCombo(name, price, start, end, bookItems);

        Combo combo = new Combo(0, name, price, start, end, "ACTIVE");
        combo.setDetails(bookItems);
        int id = comboDAO.insert(combo);
        combo.setComboId(id);
        return combo;
    }

    // cập nhật combo có sẵn
    public void updateCombo(int comboId, String name, double price, LocalDate start, LocalDate end,
                             List<ComboDetail> bookItems) throws SQLException {
        Combo existing = comboDAO.findById(comboId);
        if (existing == null) {
            throw new IllegalArgumentException("Combo khong ton tai");
        }
        validateCombo(name, price, start, end, bookItems);

        existing.setName(name);
        existing.setPrice(price);
        existing.setStartDate(start);
        existing.setEndDate(end);
        existing.setDetails(bookItems);
        comboDAO.update(existing);
    }

    //  hiển thị danh sách combo hiện có
    public List<Combo> listCombos() throws SQLException {
        return comboDAO.findAll();
    }

    // lấy chi tiết 1 combo (kèm sách) để hiển thị form sửa
    public Combo getCombo(int comboId) throws SQLException {
        return comboDAO.findById(comboId);
    }

    // xóa 1 hoặc nhiều combo cùng lúc
    public void cancelCombo(int comboId) throws SQLException {
        comboDAO.cancel(comboId);
    }

    //  kiểm tra dữ liệu nhập & tồn kho sách
    private void validateCombo(String name, double price, LocalDate start, LocalDate end,
                                List<ComboDetail> bookItems) throws SQLException {
        //  kiểm tra các ô nhập liệu có bị trống không
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ten combo khong duoc de trong");
        }
        if (start == null || end == null) {
            throw new IllegalArgumentException("Thoi gian ap dung combo khong duoc de trong");
        }
        if (bookItems == null || bookItems.isEmpty()) {
            throw new IllegalArgumentException("Combo phai co it nhat 1 sach");
        }

        //  kiểm tra tiêu chí hợp lệ
        if (price < 0) {
            throw new IllegalArgumentException("Gia combo khong hop le");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Ngay ket thuc phai sau ngay bat dau");
        }

        // kiểm tra số lượng sách tồn kho
        for (ComboDetail item : bookItems) {
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("So luong sach trong combo khong hop le");
            }
            Book book = bookService.getById(item.getBookId());
            if (book == null) {
                throw new IllegalArgumentException("Sach khong ton tai: id=" + item.getBookId());
            }
            if (!book.isAvailable()) {
                throw new IllegalArgumentException("Sach khong kha dung: " + book.getTitle());
            }
            if (book.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Sach khong du ton kho: " + book.getTitle());
            }
        }
    }

    // ================= VOUCHER =================

    public Voucher createVoucher(String code, double discountValue, double minOrderAmount,
                                  LocalDate expiryDate) throws SQLException {
        Voucher voucher = new Voucher(code, discountValue, minOrderAmount, expiryDate, false);
        voucherDAO.insert(voucher);
        return voucher;
    }
}