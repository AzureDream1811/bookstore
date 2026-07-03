package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.dao.VoucherDAO;
import com.bookstore.model.Book;
import com.bookstore.model.Order;
import com.bookstore.model.OrderDetail;
import com.bookstore.model.Voucher;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CartService {
    private final OrderDAO orderDAO = new OrderDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final VoucherDAO voucherDAO = new VoucherDAO();

    // Trong CartService.java - thay thế hoặc cập nhật phần checkout và applyVoucher
public Order checkout(int userId, List<OrderDetail> cartItems, String voucherCode) throws SQLException {
    if (cartItems.isEmpty()) throw new IllegalArgumentException("Gio hang trong");

    double totalProductAmount = 0;
    for (OrderDetail item : cartItems) {
        Book book = bookDAO.findById(item.getBookId());
        if (book == null || !book.isAvailable() || book.getStockQuantity() < item.getQuantity()) {
            throw new IllegalStateException("Sach id=" + item.getBookId() + " khong du de ban");
        }
        item.setPrice(book.getPrice());
        totalProductAmount += item.subTotal();
    }

    double discount = 0;
    String appliedVoucher = null;

    // ========== USE CASE ÁP DỤNG VOUCHER ==========
    if (voucherCode != null && !voucherCode.isBlank()) {
        try {
            discount = applyVoucher(voucherCode, totalProductAmount);
            appliedVoucher = voucherCode;
            view.print("Áp dụng voucher " + voucherCode + " thành công. Giảm: " + discount + " VND"); // giả sử có view
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Exception flow: Thông báo lỗi cho khách hàng
            throw new IllegalArgumentException("Voucher không hợp lệ: " + e.getMessage());
        }
    }

    double finalAmount = totalProductAmount - discount;

    // Transaction
    try (Connection conn = DBConnection.getConnection()) {
        conn.setAutoCommit(false);
        try {
            Order order = new Order(0, userId, finalAmount, "PENDING", appliedVoucher);
            int orderId = orderDAO.createOrder(conn, order);
            order.setOrderId(orderId);

            for (OrderDetail item : cartItems) {
                item.setOrderId(orderId);
                orderDAO.addDetail(conn, item);
                bookDAO.updateStock(conn, item.getBookId(), -item.getQuantity());
            }

            if (appliedVoucher != null) {
                voucherDAO.markUsed(conn, appliedVoucher);
            }

            order.setDetails(cartItems);
            conn.commit();
            return order;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }
}

   public double applyVoucher(String code, double orderTotal) throws SQLException {
        Voucher voucher = voucherDAO.findByCode(code);
    
        // Kiểm tra tồn tại
        if (voucher == null) {
            throw new IllegalArgumentException("Mã voucher không tồn tại");
    }

        // Kiểm tra hết hạn
        if (!voucher.isValid()) {
            throw new IllegalArgumentException("Voucher đã hết hạn hoặc đã được sử dụng");
    }

        // Kiểm tra giá trị đơn tối thiểu
        if (orderTotal < voucher.getMinOrderAmount()) {
            throw new IllegalStateException("Đơn hàng chưa đủ điều kiện áp dụng voucher (tối thiểu " 
                + voucher.getMinOrderAmount() + " VND)");
    }

        return voucher.getDiscountValue();
    }

    /** Xac nhan thanh toan: COD giu nguyen PENDING->PAID ngay, ONLINE gia lap luon thanh cong. */
    public void confirmPayment(int orderId, String method) throws SQLException {
        if (orderDAO.findById(orderId) == null) {
            throw new IllegalArgumentException("Khong tim thay don hang id=" + orderId);
        }
        orderDAO.updateStatus(orderId, "PAID");
    }

    public double cancelOrder(int orderId) throws SQLException {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Khong tim thay don hang id=" + orderId);
        }
        String status = orderDAO.findStatus(orderId);
        if ("CANCELLED".equals(status)) {
            throw new IllegalArgumentException("Don hang da bi huy");
        }

        try (Connection conn = com.bookstore.util.DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (OrderDetail detail : order.getDetails()) {
                    bookDAO.updateStock(conn, detail.getBookId(), detail.getQuantity());
                }
                orderDAO.updateStatus(conn, orderId, "CANCELLED");
                conn.commit();
                return order.getTotalAmount();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<Order> searchOrders(String criteria) throws SQLException {
        return orderDAO.search(criteria);
    }

    public List<Order> listOrdersByUser(int userId) throws SQLException {
        return orderDAO.findByUserId(userId);
    }

    /** Quan ly hoa don: liet ke toan bo hoa don, moi nhat truoc. */
    public List<Order> listAllOrders() throws SQLException {
        return orderDAO.findAll();
    }

    /** Tra ve thong tin sach de xem truoc (dung khi doi tra san pham). */
    public Book getBookById(int bookId) throws SQLException {
        return bookDAO.findById(bookId);
    }

    /** Xu ly hoan tien: chi ap dung cho don da PAID, hoan lai ton kho va danh dau REFUNDED. */
    public double processRefund(int orderId) throws SQLException {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Khong tim thay don hang id=" + orderId);
        }
        if (!"PAID".equals(order.getStatus())) {
            throw new IllegalStateException("Chi co the hoan tien cho don hang da thanh toan (PAID). Trang thai hien tai: " + order.getStatus());
        }

        try (Connection conn = com.bookstore.util.DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (OrderDetail detail : order.getDetails()) {
                    bookDAO.updateStock(conn, detail.getBookId(), detail.getQuantity());
                }
                orderDAO.updateStatus(conn, orderId, "REFUNDED");
                conn.commit();
                return order.getTotalAmount();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Doi tra san pham: tra lai oldQuantity cuon oldBookId, lay newQuantity cuon newBookId,
     * cap nhat lai chi tiet don hang va tong tien theo chenh lech gia.
     * Tra ve chenh lech tien: duong = khach can tra them, am = hoan lai cho khach.
     */
    public double exchangeProduct(int orderId, int oldBookId, int oldQuantity, int newBookId, int newQuantity) throws SQLException {
        if (oldQuantity <= 0 || newQuantity <= 0) {
            throw new IllegalArgumentException("So luong doi tra phai lon hon 0");
        }
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Khong tim thay don hang id=" + orderId);
        }
        if (!"PAID".equals(order.getStatus())) {
            throw new IllegalStateException("Chi co the doi tra san pham cho don hang da thanh toan (PAID). Trang thai hien tai: " + order.getStatus());
        }

        OrderDetail oldDetail = null;
        for (OrderDetail d : order.getDetails()) {
            if (d.getBookId() == oldBookId) {
                oldDetail = d;
                break;
            }
        }
        if (oldDetail == null) {
            throw new IllegalArgumentException("Don hang khong co san pham voi bookId=" + oldBookId);
        }
        if (oldDetail.getQuantity() < oldQuantity) {
            throw new IllegalArgumentException("So luong doi tra vuot qua so luong da mua (" + oldDetail.getQuantity() + ")");
        }

        Book newBook = bookDAO.findById(newBookId);
        if (newBook == null || !newBook.isAvailable()) {
            throw new IllegalStateException("San pham doi den khong kha dung");
        }
        if (newBook.getStockQuantity() < newQuantity) {
            throw new IllegalStateException("San pham doi den khong du ton kho");
        }

        double oldValue = oldDetail.getPrice() * oldQuantity;
        double newValue = newBook.getPrice() * newQuantity;
        double priceDiff = newValue - oldValue;

        try (Connection conn = com.bookstore.util.DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                bookDAO.updateStock(conn, oldBookId, oldQuantity);
                int remainingOld = oldDetail.getQuantity() - oldQuantity;
                if (remainingOld <= 0) {
                    orderDAO.deleteDetail(conn, orderId, oldBookId);
                } else {
                    orderDAO.updateDetailQuantity(conn, orderId, oldBookId, remainingOld);
                }

                bookDAO.updateStock(conn, newBookId, -newQuantity);
                OrderDetail existingNew = orderDAO.findDetail(conn, orderId, newBookId);
                if (existingNew != null) {
                    orderDAO.updateDetailQuantity(conn, orderId, newBookId, existingNew.getQuantity() + newQuantity);
                } else {
                    orderDAO.addDetail(conn, new OrderDetail(orderId, newBookId, newQuantity, newBook.getPrice()));
                }

                double newTotal = order.getTotalAmount() + priceDiff;
                orderDAO.updateTotal(conn, orderId, newTotal);
                conn.commit();
                return priceDiff;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /** Quan ly hoa don: lay chi tiet day du 1 don hang (dung cho xem/kiem tra hoa don). */
    public Order getInvoiceDetail(int orderId) throws SQLException {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Khong tim thay hoa don id=" + orderId);
        }
        return order;
    }
}
