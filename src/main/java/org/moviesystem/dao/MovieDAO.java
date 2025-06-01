package org.moviesystem.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.moviesystem.model.Actor;
import org.moviesystem.model.Director;
import org.moviesystem.model.Movie;
import org.moviesystem.model.Review;

public class MovieDAO {
    public static List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                int runningTime = rs.getInt("running_time_minutes");
                double rating = rs.getDouble("rating");
                Date releaseDate = rs.getDate("release_date");
                String actorsStr = rs.getString("actors");
                String directorStr = rs.getString("director");
                String plot = rs.getString("plot");
                String reviewsStr = rs.getString("reviews");

                List<Actor> actors = new ArrayList<>();
                if (actorsStr != null && !actorsStr.isEmpty()) {
                    for (String name : actorsStr.split(",")) {
                        actors.add(new Actor(name.trim()));
                    }
                }
                Director director = directorStr != null ? new Director(directorStr) : null;
                List<Review> reviews = new ArrayList<>();
                if (reviewsStr != null && !reviewsStr.isEmpty()) {
                    for (String text : reviewsStr.split(",")) {
                        reviews.add(new Review(text.trim()));
                    }
                }

                Movie movie = new Movie(id, title, runningTime, rating, releaseDate, actors, director, plot, reviews);
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public static boolean addMovie(Movie movie) {
        String sql = "INSERT INTO movies (title, running_time_minutes, rating, release_date, actors, director, plot, reviews) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, movie.getTitle());
            stmt.setInt(2, movie.getRunningTimeMinutes());
            stmt.setDouble(3, movie.getRating());
            if (movie.getReleaseDate() != null) {
                stmt.setDate(4, new java.sql.Date(movie.getReleaseDate().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setString(5, movie.getActors() != null ? movie.getActors().stream().map(Actor::getName).collect(Collectors.joining(", ")) : "");
            stmt.setString(6, movie.getDirector() != null ? movie.getDirector().getName() : "");
            stmt.setString(7, movie.getPlot());
            stmt.setString(8, movie.getReviews() != null ? movie.getReviews().stream().map(Review::getText).collect(Collectors.joining(", ")) : "");
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        movie.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateMovie(Movie movie) {
        String sql = "UPDATE movies SET title=?, running_time_minutes=?, rating=?, release_date=?, actors=?, director=?, plot=?, reviews=? WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, movie.getTitle());
            stmt.setInt(2, movie.getRunningTimeMinutes());
            stmt.setDouble(3, movie.getRating());
            if (movie.getReleaseDate() != null) {
                stmt.setDate(4, new java.sql.Date(movie.getReleaseDate().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setString(5, movie.getActors() != null ? movie.getActors().stream().map(Actor::getName).collect(Collectors.joining(", ")) : "");
            stmt.setString(6, movie.getDirector() != null ? movie.getDirector().getName() : "");
            stmt.setString(7, movie.getPlot());
            stmt.setString(8, movie.getReviews() != null ? movie.getReviews().stream().map(Review::getText).collect(Collectors.joining(", ")) : "");
            stmt.setInt(9, movie.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteMovie(int id) {
        String sql = "DELETE FROM movies WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
