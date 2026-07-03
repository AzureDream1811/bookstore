package com.bookstore.util;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = buildUrl();
    private static final String USER = getConfig("MYSQL_USER", "root");
    private static final String PASSWORD = getConfig("MYSQL_PASSWORD", "root");

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String buildUrl() {
        String host = getConfig("MYSQL_HOST", "localhost");
        String port = getConfig("MYSQL_PORT", "3306");
        String database = getConfig("MYSQL_DATABASE", "bookstore");
        return "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private static String getConfig(String key, String defaultValue) {
        String value = dotenv.get(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public static void main(String[] args) {
        try (Connection ignored = getConnection()) {
            System.out.println("Connected to the database successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
        }
    }
}