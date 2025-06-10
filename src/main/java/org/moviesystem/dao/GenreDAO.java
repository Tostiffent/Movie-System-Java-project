package org.moviesystem.dao;

import org.moviesystem.model.Genre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO {
    public static List<Genre> getAllGenres() {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT * FROM genres";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                genres.add(new Genre(
                    rs.getInt("genre_id"),
                    rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genres;
    }

    public static Genre addGenre(String name) {
        String sql = "INSERT INTO genres (name) VALUES (?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return new Genre(generatedKeys.getInt(1), name);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Genre getGenreById(int id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("name")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Genre> getGenresForMovie(int movieId) {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT g.* FROM genres g " +
                    "JOIN movie_genres mg ON g.genre_id = mg.genre_id " +
                    "WHERE mg.movie_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("name")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genres;
    }
} 