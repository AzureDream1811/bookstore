USE bookstore;

-- 1. Thêm dữ liệu người dùng (1 Admin, 2 Customer)
INSERT INTO user (full_name, email, password_hash, role)
VALUES ('Quản trị viên', 'admin@bookstore.com', 'hashed_pwd_admin', 'ADMIN'),
       ('Nguyễn Văn A', 'nva@gmail.com', 'hashed_pwd_123', 'CUSTOMER'),
       ('Trần Thị B', 'ttb@gmail.com', 'hashed_pwd_456', 'CUSTOMER');

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
       ('Dune', 'Frank Herbert', 'Sci-Fi', 250000, 20, FALSE, 'ACTIVE');

-- 4. Thêm phiếu nhập kho và chi tiết nhập kho
INSERT INTO inventory_ticket (type, status)
VALUES ('IMPORT', 'COMPLETED'),
       ('IMPORT', 'COMPLETED');

INSERT INTO inventory_detail (ticket_id, book_id, quantity, unit_price)
VALUES (1, 1, 50, 200000),
       (1, 2, 30, 300000),
       (2, 3, 100, 50000),
       (2, 4, 120, 40000);

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