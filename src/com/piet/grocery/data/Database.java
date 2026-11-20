package com.piet.grocery.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public final class Database {
    private static final Properties SETTINGS = loadSettings();
    private static final String URL = SETTINGS.getProperty("db.url",
            "jdbc:mysql://localhost:3306/grocery_billing?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true");
    private static final String USER = SETTINGS.getProperty("db.user", "root");
    private static final String PASSWORD = SETTINGS.getProperty("db.password", "");

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeSchema() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS products ("
                    + "id INT PRIMARY KEY, name VARCHAR(150) NOT NULL, category VARCHAR(80) NOT NULL, "
                    + "quantity INT NOT NULL, unit_price DECIMAL(10,2) NOT NULL, "
                    + "gst_percent DECIMAL(5,2) NOT NULL, unit VARCHAR(30) NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS customers ("
                    + "id INT PRIMARY KEY, name VARCHAR(150) NOT NULL, phone VARCHAR(30) NOT NULL, "
                    + "address VARCHAR(255) NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS suppliers ("
                    + "id INT PRIMARY KEY, name VARCHAR(150) NOT NULL, phone VARCHAR(30) NOT NULL, "
                    + "city VARCHAR(100) NOT NULL, items_supplied VARCHAR(255) NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "id INT PRIMARY KEY, full_name VARCHAR(150) NOT NULL, username VARCHAR(80) NOT NULL UNIQUE, "
                    + "password VARCHAR(255) NOT NULL, role VARCHAR(30) NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS bills ("
                    + "id BIGINT PRIMARY KEY AUTO_INCREMENT, customer_name VARCHAR(150) NOT NULL, "
                    + "subtotal DECIMAL(10,2) NOT NULL, gst DECIMAL(10,2) NOT NULL, "
                    + "discount DECIMAL(10,2) NOT NULL, total DECIMAL(10,2) NOT NULL, "
                    + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS bill_items ("
                    + "id BIGINT PRIMARY KEY AUTO_INCREMENT, bill_id BIGINT NOT NULL, "
                    + "product_name VARCHAR(150) NOT NULL, quantity INT NOT NULL, "
                    + "unit_price DECIMAL(10,2) NOT NULL, gst_percent DECIMAL(5,2) NOT NULL, "
                    + "line_total DECIMAL(10,2) NOT NULL, "
                    + "CONSTRAINT fk_bill_items_bill FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE)");
        }
    }

    private static Properties loadSettings() {
        Properties properties = new Properties();
        Path settingsPath = Path.of("database.properties");
        if (Files.exists(settingsPath)) {
            try (InputStream input = Files.newInputStream(settingsPath)) {
                properties.load(input);
            } catch (IOException exception) {
                throw new ExceptionInInitializerError(exception);
            }
        }
        return properties;
    }
}