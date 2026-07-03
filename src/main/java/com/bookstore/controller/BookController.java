package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import com.bookstore.view.ConsoleView;

import java.sql.SQLException;
import java.util.List;

public class BookController {
    private final BookService bookService = new BookService();
    private final ConsoleView view;

    public BookController(ConsoleView view) { this.view = view; }

    public void listAll() {
        int page = 1;
        final int pageSize = 5; // Show 5 books per page
        com.bookstore.model.Page<Book> bookPage;

        while (true) {
            try {
                bookPage = bookService.listPage(page, pageSize);
                if (bookPage.getContent().isEmpty()) {
                    view.print("Chua co sach nao");
                    return;
                }
                view.showBookList(bookPage);

                String choice = view.getPaginationInput();
                if ("N".equalsIgnoreCase(choice) && bookPage.hasNext()) {
                    page++;
                } else if ("P".equalsIgnoreCase(choice) && bookPage.hasPrevious()) {
                    page--;
                } else if ("E".equalsIgnoreCase(choice)) {
                    break;
                } else {
                    view.printError("Lua chon khong hop le.");
                }
            } catch (SQLException e) {
                view.printError("Loi he thong: " + e.getMessage());
                break;
            }
        }
    }

    public void search(com.bookstore.model.User user, CartController cartController) {
        String keyword = view.readLine("Tu khoa (ten/tac gia/the loai): ");
        try {
            List<Book> books = bookService.search(keyword);
            if (books.isEmpty()) {
                view.print("Khong tim thay sach phu hop");
                return;
            }
            books.forEach(b -> view.print(b.toString()));

            List<Book> recommendations = bookService.recommendBooks(keyword, 5);
            if (!recommendations.isEmpty()) {
                view.print("--- Goi y sach lien quan ---");
                recommendations.forEach(b -> view.print(b.toString()));
                String choice = view.readLine("Ban co muon them sach goi y vao gio hang? (Y/N): ");
                if ("Y".equalsIgnoreCase(choice)) {
                    int bookId = view.readInt("Nhap ID sach can them: ");
                    // Check if the selected book is in the recommendations
                    boolean found = recommendations.stream().anyMatch(b -> b.getBookId() == bookId);
                    if (found) {
                        int quantity = view.readInt("So luong: ");
                        cartController.addToCart(user, new com.bookstore.model.OrderDetail(0, bookId, quantity, 0));
                    } else {
                        view.printError("ID sach khong hop le.");
                    }
                }
            }
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void recommend() {
        String keyword = view.readLine("Nhap tu khoa de goi y sach: ");
        try {
            List<Book> recommendations = bookService.recommendBooks(keyword, 5);
            if (recommendations.isEmpty()) {
                view.print("Khong co sach goi y phu hop");
                return;
            }
            recommendations.forEach(b -> view.print(b.toString()));
        } catch (SQLException e) {
            view.printError(e.getMessage());
        }
    }

    public void addBook() {
        while (true) {
            view.print("--- FORM THEM SACH ---");
            String title = view.readLine("Ten sach: ");
            String author = view.readLine("Tac gia: ");
            String genre = view.readLine("The loai: ");
            double price = view.readDouble("Gia ban: ");
            double rentPrice = view.readDouble("Gia thue/ngay: ");
            int stock = view.readInt("So luong ton: ");
            try {
                Book book = bookService.addBook(title, author, genre, price, rentPrice, stock);
                view.print("Them sach thanh cong, bookId=" + book.getBookId());
                listAll();
                return;
            } catch (IllegalArgumentException e) {
                view.printError(e.getMessage());
            } catch (SQLException e) {
                view.printError("Loi he thong: " + e.getMessage());
                return;
            }
        }
    }

    public void editBook() {
        int id = view.readInt("Nhap bookId can sua: ");
        try {
            Book book = bookService.getById(id);
            if (book == null) { view.printError("Khong tim thay sach"); return; }
            while (true) {
                view.print("--- FORM SUA SACH ---");
                view.print("Hien tai: " + book);
                String title = view.readLine("Ten sach moi (Enter de giu nguyen): ");
                if (!title.isBlank()) book.setTitle(title);
                String author = view.readLine("Tac gia moi (Enter de giu nguyen): ");
                if (!author.isBlank()) book.setAuthor(author);
                String genre = view.readLine("The loai moi (Enter de giu nguyen): ");
                if (!genre.isBlank()) book.setGenre(genre);
                String priceStr = view.readLine("Gia moi (Enter de giu nguyen): ");
                String rentPriceStr = view.readLine("Gia thue/ngay moi (Enter de giu nguyen): ");
                String stockStr = view.readLine("So luong ton moi (Enter de giu nguyen): ");
                try {
                    if (!priceStr.isBlank()) book.setPrice(Double.parseDouble(priceStr));
                    if (!rentPriceStr.isBlank()) book.setRentPricePerDay(Double.parseDouble(rentPriceStr));
                    if (!stockStr.isBlank()) book.setStockQuantity(Integer.parseInt(stockStr));
                    bookService.updateBook(book);
                    view.print("Cap nhat thanh cong");
                    listAll();
                    return;
                } catch (NumberFormatException e) {
                    view.printError("Gia hoac so luong khong hop le");
                } catch (IllegalArgumentException e) {
                    view.printError(e.getMessage());
                }
            }
        } catch (IllegalArgumentException | SQLException e) {
            view.printError("Loi he thong: " + e.getMessage());
        }
    }

    public void hideBook() {
        int id = view.readInt("Nhap bookId can an: ");
        try {
            bookService.hideBook(id);
            view.print("Da an sach id=" + id);
            listAll();
        } catch (SQLException e) {
            view.printError("Loi he thong: " + e.getMessage());
        }
    }

    public void markFaulty() {
        int id = view.readInt("Nhap bookId bi loi: ");
        try {
            bookService.markFaulty(id, true);
            view.print("Da danh dau sach id=" + id + " la LOI");
        } catch (SQLException e) {
            view.printError("Loi he thong: " + e.getMessage());
        }
    }
}
