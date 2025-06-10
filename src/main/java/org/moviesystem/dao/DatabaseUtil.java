package org.moviesystem.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/movies";
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
            
            // Create database
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS movies");
            stmt.close();

            if (conn != null) conn.close();
            conn = getConnection();
            stmt = conn.createStatement();

            // Create directors table
            String createDirectorsTable =
                "CREATE TABLE IF NOT EXISTS directors (" +
                "director_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL)";
            stmt.executeUpdate(createDirectorsTable);

            // Create languages table
            String createLanguagesTable =
                "CREATE TABLE IF NOT EXISTS languages (" +
                "language_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(50) NOT NULL)";
            stmt.executeUpdate(createLanguagesTable);

            // Create genres table
            String createGenresTable =
                "CREATE TABLE IF NOT EXISTS genres (" +
                "genre_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(50) NOT NULL)";
            stmt.executeUpdate(createGenresTable);

            // Create actors table
            String createActorsTable =
                "CREATE TABLE IF NOT EXISTS actors (" +
                "actor_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL)";
            stmt.executeUpdate(createActorsTable);

            // Create movies table with foreign keys
            String createMoviesTable =
                "CREATE TABLE IF NOT EXISTS movies (" +
                "movie_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "running_time_minutes INT, " +
                "rating DOUBLE, " +
                "release_date DATE, " +
                "plot TEXT, " +
                "reviews TEXT, " +
                "director_id INT, " +
                "language_id INT, " +
                "FOREIGN KEY (director_id) REFERENCES directors(director_id), " +
                "FOREIGN KEY (language_id) REFERENCES languages(language_id))";
            stmt.executeUpdate(createMoviesTable);

            // Create movie_actors junction table
            String createMovieActorsTable =
                "CREATE TABLE IF NOT EXISTS movie_actors (" +
                "movie_id INT, " +
                "actor_id INT, " +
                "PRIMARY KEY (movie_id, actor_id), " +
                "FOREIGN KEY (movie_id) REFERENCES movies(movie_id), " +
                "FOREIGN KEY (actor_id) REFERENCES actors(actor_id))";
            stmt.executeUpdate(createMovieActorsTable);

            // Create movie_genres junction table
            String createMovieGenresTable =
                "CREATE TABLE IF NOT EXISTS movie_genres (" +
                "movie_id INT, " +
                "genre_id INT, " +
                "PRIMARY KEY (movie_id, genre_id), " +
                "FOREIGN KEY (movie_id) REFERENCES movies(movie_id), " +
                "FOREIGN KEY (genre_id) REFERENCES genres(genre_id))";
            stmt.executeUpdate(createMovieGenresTable);

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
