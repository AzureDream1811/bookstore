package com.bookstore.model;

public class ComboDetail {
    private int comboId;
    private int bookId;
    private int quantity;

    public ComboDetail() {}

    public ComboDetail(int comboId, int bookId, int quantity) {
        this.comboId = comboId;
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public int getComboId() { return comboId; }
    public void setComboId(int comboId) { this.comboId = comboId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
