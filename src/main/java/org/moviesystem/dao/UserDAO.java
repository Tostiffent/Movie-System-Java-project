package org.moviesystem.dao;

import org.moviesystem.model.User;
import java.sql.*;

public class UserDAO {
    public static User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM accounts WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("aid"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("atype")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void initializeUsersTable() {
        String createTableSQL = 
            "CREATE TABLE IF NOT EXISTS accounts (" +
            "aid INT AUTO_INCREMENT PRIMARY KEY, " +
            "username VARCHAR(50) NOT NULL UNIQUE, " +
            "password VARCHAR(50) NOT NULL, " +
            "atype VARCHAR(10) NOT NULL)";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(createTableSQL);

            // Insert default admin and user accounts if they don't exist
            String insertAdminSQL = 
                "INSERT IGNORE INTO accounts (username, password, atype) " +
                "VALUES ('admin', 'admin123', 'admin')";
            String insertUserSQL = 
                "INSERT IGNORE INTO accounts (username, password, atype) " +
                "VALUES ('user', 'user123', 'user')";

            stmt.executeUpdate(insertAdminSQL);
            stmt.executeUpdate(insertUserSQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 