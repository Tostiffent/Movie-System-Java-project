package org.moviesystem.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/comedy_movies";
    private static final String USER = "root";
    private static final String PASSWORD = "rayyaan123";
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            conn.setAutoCommit(true);

            System.out.println("Database connection established successfully");
            return conn;

        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    public static void initializeDatabase() {
        Connection conn = null;
        try {
            String baseUrl = "jdbc:mysql://localhost:3306/";
            conn = DriverManager.getConnection(baseUrl, USER, PASSWORD);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS comedy_movies");
            stmt.close();

            if (conn != null) conn.close();
            conn = getConnection();

            String createTableSQL =
                "CREATE TABLE IF NOT EXISTS movies (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "running_time_minutes INT, " +
                "rating DOUBLE, " +
                "release_date DATE, " +
                "actors TEXT, " +
                "director VARCHAR(255), " +
                "plot TEXT, " +
                "reviews TEXT)";

            stmt = conn.createStatement();
            stmt.executeUpdate(createTableSQL);
            stmt.close();

            System.out.println("Database and tables initialized successfully");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
}
