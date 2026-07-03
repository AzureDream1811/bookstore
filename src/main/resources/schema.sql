CREATE DATABASE IF NOT EXISTS bookstore;
USE bookstore;

CREATE TABLE IF NOT EXISTS user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER'
);

CREATE TABLE IF NOT EXISTS member (
    member_id INT PRIMARY KEY,
    membership_rank VARCHAR(20) DEFAULT 'BRONZE',
    points INT DEFAULT 0,
    FOREIGN KEY (member_id) REFERENCES user(user_id)
);

CREATE TABLE IF NOT EXISTS book (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100),
    genre VARCHAR(50),
    price DOUBLE NOT NULL,
    rent_price_per_day DOUBLE DEFAULT 0,
    stock_quantity INT NOT NULL DEFAULT 0,
    is_faulty BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS inventory_ticket (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(10) NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING'
);

CREATE TABLE IF NOT EXISTS inventory_detail (
    ticket_id INT NOT NULL,
    book_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DOUBLE NOT NULL,
    PRIMARY KEY (ticket_id, book_id),
    FOREIGN KEY (ticket_id) REFERENCES inventory_ticket(ticket_id),
    FOREIGN KEY (book_id) REFERENCES book(book_id)
);

CREATE TABLE IF NOT EXISTS combo (
    combo_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS combo_detail (
    combo_id INT NOT NULL,
    book_id INT NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (combo_id, book_id),
    FOREIGN KEY (combo_id) REFERENCES combo(combo_id),
    FOREIGN KEY (book_id) REFERENCES book(book_id)
);

CREATE TABLE IF NOT EXISTS voucher (
    voucher_code VARCHAR(30) PRIMARY KEY,
    discount_value DOUBLE NOT NULL,
    min_order_amount DOUBLE DEFAULT 0,
    expiry_date DATE NOT NULL,
    used BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DOUBLE DEFAULT 0,
    status VARCHAR(20) DEFAULT 'PENDING',
    voucher_code VARCHAR(30),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE IF NOT EXISTS order_detail (
    order_id INT NOT NULL,
    book_id INT NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    PRIMARY KEY (order_id, book_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (book_id) REFERENCES book(book_id)
);

CREATE TABLE IF NOT EXISTS rental (
    rental_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    rent_date DATE NOT NULL,
    days INT NOT NULL,
    status VARCHAR(20) DEFAULT 'RENTED',
    return_date DATE,
    late_fee DOUBLE DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (book_id) REFERENCES book(book_id)
);