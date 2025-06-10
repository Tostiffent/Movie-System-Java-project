package org.moviesystem.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.moviesystem.model.*;

public class MovieDAO {
    public static List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT m.*, d.name as director_name, l.name as language_name " +
                    "FROM movies m " +
                    "LEFT JOIN directors d ON m.director_id = d.director_id " +
                    "LEFT JOIN languages l ON m.language_id = l.language_id";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int movieId = rs.getInt("movie_id");
                Movie movie = new Movie();
                movie.setMovieId(movieId);
                movie.setTitle(rs.getString("title"));
                movie.setRunningTimeMinutes(rs.getInt("running_time_minutes"));
                movie.setRating(rs.getDouble("rating"));
                movie.setReleaseDate(rs.getDate("release_date"));
                movie.setPlot(rs.getString("plot"));
                movie.setReviews(getReviewsForMovie(movieId));

                // Set director
                int directorId = rs.getInt("director_id");
                if (!rs.wasNull()) {
                    movie.setDirector(new Director(directorId, rs.getString("director_name")));
                }

                // Set language
                int languageId = rs.getInt("language_id");
                if (!rs.wasNull()) {
                    movie.setLanguage(new Language(languageId, rs.getString("language_name")));
                }

                // Set actors and genres
                movie.setActors(getActorsForMovie(movieId));
                movie.setGenres(GenreDAO.getGenresForMovie(movieId));

                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    private static List<Actor> getActorsForMovie(int movieId) {
        List<Actor> actors = new ArrayList<>();
        String sql = "SELECT a.* FROM actors a " +
                    "JOIN movie_actors ma ON a.actor_id = ma.actor_id " +
                    "WHERE ma.movie_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    actors.add(new Actor(
                        rs.getInt("actor_id"),
                        rs.getString("name")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actors;
    }

    private static List<Review> getReviewsForMovie(int movieId) {
        List<Review> reviews = new ArrayList<>();
        String reviewsStr = "";
        String sql = "SELECT reviews FROM movies WHERE movie_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reviewsStr = rs.getString("reviews");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (reviewsStr != null && !reviewsStr.isEmpty()) {
            for (String text : reviewsStr.split(",")) {
                reviews.add(new Review(text.trim()));
            }
        }
        return reviews;
    }

    public static boolean addMovie(Movie movie) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Insert or get director
            int directorId = getOrCreateDirector(conn, movie.getDirector());

            // Insert or get language
            int languageId = getOrCreateLanguage(conn, movie.getLanguage());

            // Insert movie
            String sql = "INSERT INTO movies (title, running_time_minutes, rating, release_date, " +
                        "plot, reviews, director_id, language_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, movie.getTitle());
                stmt.setInt(2, movie.getRunningTimeMinutes());
                stmt.setDouble(3, movie.getRating());
                stmt.setDate(4, movie.getReleaseDate() != null ? 
                    new java.sql.Date(movie.getReleaseDate().getTime()) : null);
                stmt.setString(5, movie.getPlot());
                stmt.setString(6, movie.getReviews() != null ? 
                    String.join(", ", movie.getReviews().stream()
                        .map(Review::getText).toList()) : "");
                stmt.setInt(7, directorId);
                stmt.setInt(8, languageId);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int movieId = generatedKeys.getInt(1);
                            movie.setMovieId(movieId);

                            // Add actors
                            addActorsToMovie(conn, movieId, movie.getActors());

                            // Add genres
                            addGenresToMovie(conn, movieId, movie.getGenres());

                            conn.commit();
                            return true;
                        }
                    }
                }
            }
            conn.rollback();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private static int getOrCreateDirector(Connection conn, Director director) throws SQLException {
        if (director == null || director.getName().isEmpty()) {
            return 0;
        }

        String sql = "SELECT director_id FROM directors WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, director.getName());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("director_id");
                }
            }
        }

        sql = "INSERT INTO directors (name) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, director.getName());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create director");
    }

    private static int getOrCreateLanguage(Connection conn, Language language) throws SQLException {
        if (language == null || language.getName().isEmpty()) {
            return 0;
        }

        String sql = "SELECT language_id FROM languages WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, language.getName());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("language_id");
                }
            }
        }

        sql = "INSERT INTO languages (name) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, language.getName());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create language");
    }

    private static void addActorsToMovie(Connection conn, int movieId, List<Actor> actors) throws SQLException {
        if (actors == null || actors.isEmpty()) {
            return;
        }

        // First ensure all actors exist
        String actorSql = "INSERT INTO actors (name) VALUES (?) ON DUPLICATE KEY UPDATE actor_id=LAST_INSERT_ID(actor_id)";
        String linkSql = "INSERT INTO movie_actors (movie_id, actor_id) VALUES (?, ?)";
        
        try (PreparedStatement actorStmt = conn.prepareStatement(actorSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement linkStmt = conn.prepareStatement(linkSql)) {
            
            for (Actor actor : actors) {
                actorStmt.setString(1, actor.getName());
                actorStmt.executeUpdate();
                
                try (ResultSet generatedKeys = actorStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int actorId = generatedKeys.getInt(1);
                        linkStmt.setInt(1, movieId);
                        linkStmt.setInt(2, actorId);
                        linkStmt.executeUpdate();
                    }
                }
            }
        }
    }

    private static void addGenresToMovie(Connection conn, int movieId, List<Genre> genres) throws SQLException {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        String genreSql = "INSERT INTO genres (name) VALUES (?) ON DUPLICATE KEY UPDATE genre_id=LAST_INSERT_ID(genre_id)";
        String linkSql = "INSERT INTO movie_genres (movie_id, genre_id) VALUES (?, ?)";
        
        try (PreparedStatement genreStmt = conn.prepareStatement(genreSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement linkStmt = conn.prepareStatement(linkSql)) {
            
            for (Genre genre : genres) {
                genreStmt.setString(1, genre.getName());
                genreStmt.executeUpdate();
                
                try (ResultSet generatedKeys = genreStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int genreId = generatedKeys.getInt(1);
                        linkStmt.setInt(1, movieId);
                        linkStmt.setInt(2, genreId);
                        linkStmt.executeUpdate();
                    }
                }
            }
        }
    }

    public static boolean updateMovie(Movie movie) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Update or create director and language
            int directorId = getOrCreateDirector(conn, movie.getDirector());
            int languageId = getOrCreateLanguage(conn, movie.getLanguage());

            // Update movie
            String sql = "UPDATE movies SET title=?, running_time_minutes=?, rating=?, " +
                        "release_date=?, plot=?, reviews=?, director_id=?, language_id=? " +
                        "WHERE movie_id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, movie.getTitle());
                stmt.setInt(2, movie.getRunningTimeMinutes());
                stmt.setDouble(3, movie.getRating());
                stmt.setDate(4, movie.getReleaseDate() != null ? 
                    new java.sql.Date(movie.getReleaseDate().getTime()) : null);
                stmt.setString(5, movie.getPlot());
                stmt.setString(6, movie.getReviews() != null ? 
                    String.join(", ", movie.getReviews().stream()
                        .map(Review::getText).toList()) : "");
                stmt.setInt(7, directorId);
                stmt.setInt(8, languageId);
                stmt.setInt(9, movie.getMovieId());

                if (stmt.executeUpdate() > 0) {
                    // Delete existing relationships
                    deleteMovieRelationships(conn, movie.getMovieId());

                    // Add new relationships
                    addActorsToMovie(conn, movie.getMovieId(), movie.getActors());
                    addGenresToMovie(conn, movie.getMovieId(), movie.getGenres());

                    conn.commit();
                    return true;
                }
            }
            conn.rollback();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private static void deleteMovieRelationships(Connection conn, int movieId) throws SQLException {
        // Delete actor relationships
        String sql = "DELETE FROM movie_actors WHERE movie_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            stmt.executeUpdate();
        }

        // Delete genre relationships
        sql = "DELETE FROM movie_genres WHERE movie_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            stmt.executeUpdate();
        }
    }

    public static boolean deleteMovie(int movieId) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Delete relationships first
            deleteMovieRelationships(conn, movieId);

            // Delete the movie
            String sql = "DELETE FROM movies WHERE movie_id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, movieId);
                if (stmt.executeUpdate() > 0) {
                    conn.commit();
                    return true;
                }
            }
            conn.rollback();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
