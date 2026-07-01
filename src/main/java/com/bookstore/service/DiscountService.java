package com.bookstore.service;

import com.bookstore.dao.ComboDAO;
import com.bookstore.dao.VoucherDAO;
import com.bookstore.model.Combo;
import com.bookstore.model.ComboDetail;
import com.bookstore.model.Voucher;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class DiscountService {
    private final ComboDAO comboDAO = new ComboDAO();
    private final VoucherDAO voucherDAO = new VoucherDAO();

    public Combo createCombo(String name, double price, LocalDate start, LocalDate end,
                              List<ComboDetail> bookItems) throws SQLException {
        if (bookItems.isEmpty()) throw new IllegalArgumentException("Combo can it nhat 1 sach");
        Combo combo = new Combo(0, name, price, start, end, "ACTIVE");
        int id = comboDAO.insert(combo);
        combo.setComboId(id);
        for (ComboDetail item : bookItems) {
            comboDAO.addDetail(id, item);
        }
        combo.setDetails(bookItems);
        return combo;
    }

    public List<Combo> listCombos() throws SQLException {
        return comboDAO.findAll();
    }

    public void cancelCombo(int comboId) throws SQLException {
        comboDAO.cancel(comboId);
    }

    public Voucher createVoucher(String code, double discountValue, double minOrderAmount,
                                  LocalDate expiryDate) throws SQLException {
        Voucher voucher = new Voucher(code, discountValue, minOrderAmount, expiryDate, false);
        voucherDAO.insert(voucher);
        return voucher;
    }
}
