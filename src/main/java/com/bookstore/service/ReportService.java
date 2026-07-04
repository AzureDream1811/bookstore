package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.dao.ReportDAO;
import com.bookstore.model.Book;
import com.bookstore.model.ReportFilter;
import com.bookstore.model.RevenueReportData;
import com.bookstore.model.RevenueResult;
import com.bookstore.model.BestSellerFilter;
import com.bookstore.model.BestSellerItem;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

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

    /**
     * [bookId, title, soldQuantity]
     */
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
        if (filter.getFromDate().isAfter(filter.getToDate())) {
            throw new IllegalArgumentException("Khoang thoi gian khng hop le");
        }

        try {
            List<RevenueResult> dailyResults = reportDAO.getRevenueByFilter(filter);
            if (dailyResults == null || dailyResults.isEmpty()) {
                throw new Exception("Khong co du lieu phu hop.");
            }
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
            if (totalResult.getNetRevenue() == 0 && totalAmount == 0) {
                throw new Exception("Khong co du lieu phu hop.");
            }
            return new RevenueReportData(dailyResults, totalResult);
        } catch (SQLException e) {
            throw new Exception("Lỗi kết nối máy chủ hoặc truy vấn CSDL, vui lòng thử lại sau.", e);
        }
    }

    public List<BestSellerItem> generateBestSellerReport(BestSellerFilter filter) throws Exception {
        LocalDate toDate = filter.getToDate() != null ? filter.getToDate() : LocalDate.now();
        LocalDate fromDate = filter.getFromDate() != null ? filter.getFromDate() : toDate.minusDays(30);
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("Khoang thoi gian khong hop le");
        }
        try {
            Map<Integer, BestSellerItem> merged = new HashMap<>();

            List<Object[]> soldRows = reportDAO.getSoldQuantityByBook(fromDate, toDate);
            for (Object[] row : soldRows) {
                int bookId = (int) row[0];
                int qty = (int) row[1];
                Book book = bookDAO.findById(bookId);
                String title = book != null ? book.getTitle() : "(Không rõ)";
                merged.put(bookId, new BestSellerItem(bookId, title, qty, 0));
            }

            List<Object[]> rentedRows = reportDAO.getRentedQuantityByBook(fromDate, toDate);
            for (Object[] row : rentedRows) {
                int bookId = (int) row[0];
                int qty = (int) row[1];
                BestSellerItem item = merged.get(bookId);
                if (item == null) {
                    Book book = bookDAO.findById(bookId);
                    String title = book != null ? book.getTitle() : "(Không rõ)";
                    merged.put(bookId, new BestSellerItem(bookId, title, 0, qty));
                } else {
                    item.setRentedQty(qty);
                }
            }

            if (merged.isEmpty()) {
                throw new Exception("Khong co du lieu phu hop.");
            }

            List<BestSellerItem> list = new ArrayList<>(merged.values());

            Comparator<BestSellerItem> comparator = switch (filter.getType()) {
                case SOLD -> Comparator.comparingInt(BestSellerItem::getSoldQty);
                case RENTED -> Comparator.comparingInt(BestSellerItem::getRentedQty);
                default -> Comparator.comparingInt(BestSellerItem::getTotalQty);
            };
            if (filter.getSortOrder() == BestSellerFilter.SortOrder.DESC) {
                comparator = comparator.reversed();
            }
            list.sort(comparator);

            return list.stream().limit(10).collect(Collectors.toList());

        } catch (SQLException e) {
            throw new Exception("Lỗi kết nối máy chủ hoặc truy vấn CSDL, vui lòng thử lại sau.", e);
        }
    }
}
