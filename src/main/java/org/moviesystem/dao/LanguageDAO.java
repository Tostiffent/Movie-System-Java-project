package org.moviesystem.dao;

import org.moviesystem.model.Language;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LanguageDAO {
    public static List<Language> getAllLanguages() {
        List<Language> languages = new ArrayList<>();
        String sql = "SELECT * FROM languages";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                languages.add(new Language(
                    rs.getInt("language_id"),
                    rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return languages;
    }

    public static Language addLanguage(String name) {
        String sql = "INSERT INTO languages (name) VALUES (?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return new Language(generatedKeys.getInt(1), name);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Language getLanguageById(int id) {
        String sql = "SELECT * FROM languages WHERE language_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Language(
                        rs.getInt("language_id"),
                        rs.getString("name")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
} 