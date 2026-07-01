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

    /** Tao don hang tu gio hang (danh sach OrderDetail chua orderId), tru ton kho, tra ve Order da luu. */
    public Order checkout(int userId, List<OrderDetail> cartItems, String voucherCode) throws SQLException {
        if (cartItems.isEmpty()) throw new IllegalArgumentException("Gio hang trong");
        double total = 0;
        for (OrderDetail item : cartItems) {
            Book book = bookDAO.findById(item.getBookId());
            if (book == null || !book.isAvailable() || book.getStockQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Sach id=" + item.getBookId() + " khong du de ban");
            }
            item.setPrice(book.getPrice());
            total += item.subTotal();
        }

        double discount = 0;
        if (voucherCode != null && !voucherCode.isBlank()) {
            discount = applyVoucher(voucherCode, total);
        }
        double finalAmount = total - discount;

        try (Connection conn = com.bookstore.util.DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Order order = new Order(0, userId, finalAmount, "PENDING", voucherCode);
                int orderId = orderDAO.createOrder(conn, order);
                order.setOrderId(orderId);
                for (OrderDetail item : cartItems) {
                    item.setOrderId(orderId);
                    orderDAO.addDetail(conn, item);
                    bookDAO.updateStock(conn, item.getBookId(), -item.getQuantity());
                }
                if (voucherCode != null && !voucherCode.isBlank()) {
                    voucherDAO.markUsed(conn, voucherCode);
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

    /** Kiem tra voucher hop le + dieu kien don toi thieu, tra ve so tien duoc giam. */
    public double applyVoucher(String code, double orderTotal) throws SQLException {
        Voucher voucher = voucherDAO.findByCode(code);
        if (voucher == null || !voucher.isValid()) {
            throw new IllegalArgumentException("Voucher khong hop le hoac da het han/da dung");
        }
        if (orderTotal < voucher.getMinOrderAmount()) {
            throw new IllegalStateException("Don hang chua du dieu kien ap dung voucher (toi thieu "
                    + voucher.getMinOrderAmount() + ")");
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
}
