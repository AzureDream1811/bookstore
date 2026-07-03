package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.dao.ReportDAO;
import com.bookstore.model.Book;
import com.bookstore.model.ReportFilter;
import com.bookstore.model.RevenueReportData;
import com.bookstore.model.RevenueResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {
    private final OrderDAO orderDAO = new OrderDAO();
    private final BookDAO bookDAO = new BookDAO();
    private ReportDAO reportDAO = new ReportDAO();

    public double revenueBetween(String fromDate, String toDate) throws SQLException {
        return orderDAO.sumRevenue(fromDate, toDate);
    }

    /** [bookId, title, soldQuantity] */
    public List<Object[]> bestSellers(int limit) throws SQLException {
        return orderDAO.bestSellers(limit);
    }

    public List<Book> lowStockBooks(int threshold) throws SQLException {
        return bookDAO.findAll().stream()
                .filter(book -> book.getStockQuantity() <= threshold)
                .sorted(Comparator.comparingInt(Book::getStockQuantity)
                        .thenComparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public String bestSellersChart(int limit) throws SQLException {
        List<Object[]> items = bestSellers(limit);
        if (items.isEmpty()) {
            return "Khong co du lieu";
        }
        StringBuilder sb = new StringBuilder();
        int max = 0;
        for (Object[] item : items) {
            max = Math.max(max, ((Number) item[2]).intValue());
        }
        for (Object[] item : items) {
            int qty = ((Number) item[2]).intValue();
            int bars = max == 0 ? 0 : Math.max(1, qty * 20 / max);
            sb.append(item[1]).append(" | ")
                    .append("#".repeat(bars))
                    .append(" (").append(qty).append(")")
                    .append(System.lineSeparator());
        }
        return sb.toString();
    }

    public void exportRevenueCsv(String fromDate, String toDate, String filePath) throws SQLException, IOException {
        List<Object[]> sellers = bestSellers(10);
        StringBuilder builder = new StringBuilder();
        builder.append("from,to,revenue\n");
        builder.append(fromDate).append(',').append(toDate).append(',').append(revenueBetween(fromDate, toDate)).append('\n');
        builder.append("\nbookId,title,soldQuantity\n");
        for (Object[] item : sellers) {
            builder.append(item[0]).append(',')
                    .append(item[1]).append(',')
                    .append(item[2]).append('\n');
        }
        Files.writeString(Path.of(filePath), builder.toString(), StandardCharsets.UTF_8);
    }
    public RevenueReportData generateRevenueReport(ReportFilter filter) throws Exception {
        // Exception Flow 5.1: Kiểm tra dữ liệu đầu vào
        if (filter.getFromDate().isAfter(filter.getToDate())) {
            throw new IllegalArgumentException("Khoảng thời gian không hợp lệ (From date > To date)");
        }

        try {
            // DAO nay trả về danh sách theo từng ngày
            List<RevenueResult> dailyResults = reportDAO.getRevenueByFilter(filter);

            // Exception Flow 6.2: Không có dữ liệu
            if (dailyResults == null || dailyResults.isEmpty()) {
                throw new Exception("Không có dữ liệu phù hợp với điều kiện lọc.");
            }

            // Tính toán tổng số liệu từ danh sách theo ngày
            RevenueResult totalResult = new RevenueResult();
            double totalAmount = 0, totalDiscount = 0, totalShipping = 0, totalRefund = 0;

            for (RevenueResult day : dailyResults) {
                totalAmount += day.getTotalProductAmount();
                totalDiscount += day.getTotalDiscount();
                totalShipping += day.getTotalShippingFee();
                totalRefund += day.getTotalRefund();
            }

            totalResult.setTotalProductAmount(totalAmount);
            totalResult.setTotalDiscount(totalDiscount);
            totalResult.setTotalShippingFee(totalShipping);
            totalResult.setTotalRefund(totalRefund);
            totalResult.calculateNetRevenue();

            // Đề phòng trường hợp có dòng dữ liệu nhưng doanh thu bằng 0
            if (totalResult.getNetRevenue() == 0 && totalAmount == 0) {
                throw new Exception("Không có dữ liệu doanh thu phù hợp với điều kiện lọc.");
            }

            // Trả về đối tượng chứa cả danh sách ngày và tổng cộng
            return new RevenueReportData(dailyResults, totalResult);

        } catch (SQLException e) {
            // Exception Flow 2.1 & 6.1: Lỗi CSDL
            throw new Exception("Lỗi kết nối máy chủ hoặc truy vấn CSDL, vui lòng thử lại sau.", e);
        }
    }
}
