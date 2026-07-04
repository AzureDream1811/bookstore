USE bookstore;

-- 1. Thêm dữ liệu người dùng (1 Admin, 2 Customer, 1 Staff)
-- Admin password: Admin123
-- Customer password: Customer123
-- Customer password: Customer456
-- Staff password: Staff123
INSERT INTO user (full_name, email, password_hash, role, verify_code, otp_expires_at, verified)
VALUES ('Quản trị viên', 'admin@bookstore.com', '3b612c75a7b5048a435fb6ec81e52ff92d6d795a8b5a9c17070f6a63c97a53b2',
        'ADMIN', NULL, NULL, TRUE),
       ('Nguyễn Văn A', 'nva@gmail.com', 'e6f59d120b99238e3a81b7322136ac9be6f9e27c764f8daca738f68c16a62202', 'CUSTOMER',
        NULL, NULL, TRUE),
       ('Trần Thị B', 'ttb@gmail.com', '9cf47a89f39cd8001fea83b923369f82c1b8b2fe40fc123d14dc1e6b4d751f78', 'CUSTOMER',
        NULL, NULL, TRUE),
       ('Nhân viên bán hàng', 'staff@bookstore.com',
        '2f005e42a17da46ec51ba6f11d725e60788931a1dadd33d9cb85084fb32bb166',
        'STAFF', NULL, NULL, TRUE);

-- 2. Thêm dữ liệu thành viên cho Customer (user_id 2 và 3)
INSERT INTO member (member_id, membership_rank, points)
VALUES (2, 'SILVER', 150),
       (3, 'BRONZE', 50);

-- 3. Thêm dữ liệu sách
INSERT INTO book (title, author, genre, price, stock_quantity, is_faulty, status)
VALUES ('Clean Code', 'Robert C. Martin', 'IT', 300000, 50, FALSE, 'ACTIVE'),
       ('Head First Design Patterns', 'Eric Freeman', 'IT', 450000, 30, FALSE, 'ACTIVE'),
       ('Đắc Nhân Tâm', 'Dale Carnegie', 'Self-help', 100000, 100, FALSE, 'ACTIVE'),
       ('Nhà Giả Kim', 'Paulo Coelho', 'Novel', 85000, 120, FALSE, 'ACTIVE'),
       ('Dune', 'Frank Herbert', 'Sci-Fi', 250000, 20, FALSE, 'ACTIVE'),
       ('The Pragmatic Programmer', 'Andrew Hunt', 'IT', 350000, 40, FALSE, 'ACTIVE'),
       ('Code Complete', 'Steve McConnell', 'IT', 500000, 25, FALSE, 'ACTIVE'),
       ('Refactoring: Improving the Design of Existing Code', 'Martin Fowler', 'IT', 400000, 35, FALSE, 'ACTIVE'),
       ('The Mythical Man-Month', 'Frederick P. Brooks Jr.', 'IT', 320000, 30, FALSE, 'ACTIVE'),
       ('Thinking, Fast and Slow', 'Daniel Kahneman', 'Psychology', 280000, 60, FALSE, 'ACTIVE'),
       ('Sapiens: A Brief History of Humankind', 'Yuval Noah Harari', 'History', 320000, 80, FALSE, 'ACTIVE'),
       ('Homo Deus: A Brief History of Tomorrow', 'Yuval Noah Harari', 'History', 330000, 70, FALSE, 'ACTIVE'),
       ('21 Lessons for the 21st Century', 'Yuval Noah Harari', 'Philosophy', 300000, 90, FALSE, 'ACTIVE'),
       ('The Lord of the Rings', 'J.R.R. Tolkien', 'Fantasy', 550000, 40, FALSE, 'ACTIVE'),
       ('The Hobbit', 'J.R.R. Tolkien', 'Fantasy', 250000, 60, FALSE, 'ACTIVE'),
       ('A Song of Ice and Fire', 'George R.R. Martin', 'Fantasy', 1200000, 15, FALSE, 'ACTIVE'),
       ('1984', 'George Orwell', 'Dystopian', 150000, 100, FALSE, 'ACTIVE'),
       ('Brave New World', 'Aldous Huxley', 'Dystopian', 160000, 90, FALSE, 'ACTIVE'),
       ('Fahrenheit 451', 'Ray Bradbury', 'Dystopian', 140000, 80, FALSE, 'ACTIVE'),
       ('To Kill a Mockingbird', 'Harper Lee', 'Classic', 120000, 150, FALSE, 'ACTIVE'),
       ('The Great Gatsby', 'F. Scott Fitzgerald', 'Classic', 110000, 130, FALSE, 'ACTIVE'),
       ('One Hundred Years of Solitude', 'Gabriel Garcia Marquez', 'Magic Realism', 180000, 70, FALSE, 'ACTIVE'),
       ('The Catcher in the Rye', 'J.D. Salinger', 'Classic', 130000, 110, FALSE, 'ACTIVE'),
       ('The Grapes of Wrath', 'John Steinbeck', 'Classic', 170000, 80, FALSE, 'ACTIVE'),
       ('Don Quixote', 'Miguel de Cervantes', 'Classic', 220000, 50, FALSE, 'ACTIVE'),
       ('Moby Dick', 'Herman Melville', 'Classic', 190000, 60, FALSE, 'ACTIVE'),
       ('War and Peace', 'Leo Tolstoy', 'Classic', 350000, 40, FALSE, 'ACTIVE'),
       ('Ulysses', 'James Joyce', 'Modernist', 280000, 30, FALSE, 'ACTIVE'),
       ('The Odyssey', 'Homer', 'Epic', 150000, 90, FALSE, 'ACTIVE'),
       ('The Iliad', 'Homer', 'Epic', 150000, 85, FALSE, 'ACTIVE'),
       ('Crime and Punishment', 'Fyodor Dostoevsky', 'Psychological Fiction', 200000, 70, FALSE, 'ACTIVE'),
       ('The Brothers Karamazov', 'Fyodor Dostoevsky', 'Philosophical Fiction', 250000, 60, FALSE, 'ACTIVE'),
       ('The Idiot', 'Fyodor Dostoevsky', 'Philosophical Fiction', 220000, 55, FALSE, 'ACTIVE'),
       ('Demons', 'Fyodor Dostoevsky', 'Philosophical Fiction', 230000, 50, FALSE, 'ACTIVE'),
       ('Notes from Underground', 'Fyodor Dostoevsky', 'Philosophical Fiction', 120000, 80, FALSE, 'ACTIVE'),
       ('The Metamorphosis', 'Franz Kafka', 'Absurdist Fiction', 100000, 100, FALSE, 'ACTIVE');

-- 4. Thêm phiếu nhập kho và chi tiết nhập kho
INSERT INTO inventory_ticket (type, status)
VALUES ('IMPORT', 'COMPLETED'),
       ('IMPORT', 'COMPLETED'),
       ('IMPORT', 'COMPLETED'),
       ('IMPORT', 'COMPLETED');

INSERT INTO inventory_detail (ticket_id, book_id, quantity, unit_price)
VALUES (1, 1, 50, 200000),
       (1, 2, 30, 300000),
       (2, 3, 100, 50000),
       (2, 4, 120, 40000),
       (3, 5, 20, 150000),
       (3, 6, 40, 250000),
       (3, 7, 25, 400000),
       (3, 8, 35, 300000),
       (3, 9, 30, 220000),
       (4, 10, 60, 180000),
       (4, 11, 80, 220000),
       (4, 12, 70, 230000),
       (4, 13, 90, 200000),
       (4, 14, 40, 450000),
       (4, 15, 60, 150000);

-- 5. Thêm Combo và chi tiết Combo
INSERT INTO combo (name, price, start_date, end_date, status)
VALUES ('Combo Lập Trình Cơ Bản', 700000, '2025-01-01', '2025-12-31', 'ACTIVE');

INSERT INTO combo_detail (combo_id, book_id, quantity)
VALUES (1, 1, 1),
       (1, 2, 1);

-- 6. Thêm Voucher
INSERT INTO voucher (voucher_code, discount_value, min_order_amount, expiry_date, used)
VALUES ('GIAM50K', 50000, 200000, '2025-12-31', FALSE),
       ('GIAM100K', 100000, 500000, '2025-12-31', TRUE),
       ('HETHAN', 20000, 0, '2024-01-01', FALSE);

-- 7. Thêm dữ liệu Đơn hàng (orders) - Dùng để test Báo cáo doanh thu
-- Đơn 1: Hợp lệ (Completed), Tháng 10/2025. Doanh thu thuần = 300,000 - 50,000 + 15,000 - 0 = 265,000
INSERT INTO orders (user_id, total_product_amount, discount, shipping_fee, refund_amount, total_amount, status,
                    voucher_code, created_date)
VALUES (2, 300000, 50000, 15000, 0, 265000, 'Completed', 'GIAM50K', '2025-10-15 10:30:00');

-- Đơn 2: Hợp lệ (Paid), Tháng 10/2025. Doanh thu thuần = 100,000 - 0 + 20,000 - 0 = 120,000
INSERT INTO orders (user_id, total_product_amount, discount, shipping_fee, refund_amount, total_amount, status,
                    voucher_code, created_date)
VALUES (3, 100000, 0, 20000, 0, 120000, 'Paid', NULL, '2025-10-20 14:15:00');

-- Đơn 3: Hợp lệ (Delivered), Tháng 10/2025. Doanh thu thuần = 450,000 - 100,000 + 0 - 50,000 = 300,000 (Có refund một phần)
INSERT INTO orders (user_id, total_product_amount, discount, shipping_fee, refund_amount, total_amount, status,
                    voucher_code, created_date)
VALUES (2, 450000, 100000, 0, 50000, 350000, 'Delivered', 'GIAM100K', '2025-10-25 09:00:00');

-- Đơn 4: Không hợp lệ (Cancelled), Tháng 10/2025. Sẽ KHÔNG được cộng vào báo cáo.
INSERT INTO orders (user_id, total_product_amount, discount, shipping_fee, refund_amount, total_amount, status,
                    voucher_code, created_date)
VALUES (3, 85000, 0, 15000, 0, 100000, 'Cancelled', NULL, '2025-10-10 16:45:00');

-- Đơn 5: Hợp lệ (Completed), Nhưng ở tháng 9/2025. Dùng để test lọc khoảng thời gian.
INSERT INTO orders (user_id, total_product_amount, discount, shipping_fee, refund_amount, total_amount, status,
                    voucher_code, created_date)
VALUES (2, 250000, 0, 15000, 0, 265000, 'Completed', NULL, '2025-09-15 08:20:00');

-- 8. Thêm Chi tiết đơn hàng
INSERT INTO order_detail (order_id, book_id, quantity, price)
VALUES (1, 1, 1, 300000), -- Clean Code
       (2, 3, 1, 100000), -- Đắc Nhân Tâm
       (3, 2, 1, 450000), -- Head First Design Patterns
       (4, 4, 1, 85000),  -- Nhà Giả Kim
       (5, 5, 1, 250000); -- Dune
