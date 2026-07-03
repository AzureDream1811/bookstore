package com.bookstore.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    static {
        loadEnv();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("[LOI] Khong tim thay Driver MySQL trong Classpath!");
            }
        }
    }

    private static void loadEnv() {
        java.io.File file = new java.io.File(".env");
        if (file.exists()) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    int eq = line.indexOf('=');
                    if (eq > 0) {
                        String key = line.substring(0, eq).trim();
                        String value = line.substring(eq + 1).trim();
                        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                            value = value.substring(1, value.length() - 1);
                        } else if (value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
                            value = value.substring(1, value.length() - 1);
                        }
                        System.setProperty(key, value);
                    }
                }
            } catch (java.io.IOException e) {
                // ignore
            }
        }
    }

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        // 1. Lấy thông tin động mỗi khi gọi hàm để tránh bị dính cache static
        String host = getConfig("MYSQL_HOST", "localhost");
        String port = getConfig("MYSQL_PORT", "3306");
        String database = getConfig("MYSQL_DATABASE", "bookstore");
        
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        
        String user = getConfig("MYSQL_USER", "root");
        
        String password = ""; 

        return DriverManager.getConnection(url, user, password);
    }

    private static String getConfig(String key, String fallback) {
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key);
        }
        return (value == null) ? fallback : value;
    }

    public static void main(String[] args) {
        try (Connection ignored = getConnection()) {
            System.out.println("Connected to the database successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
        }
    }
}