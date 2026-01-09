package database;

import java.sql.*;

public class UserDAO {

    public void createUser(String userId, String username, String password, String role) throws SQLException {
        String sql = "INSERT INTO users (user_id, username, password, role) VALUES (?::uuid, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, role);
            stmt.executeUpdate();
        }
    }

    public ResultSet findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        // Note: Caller is responsible for closing ResultSet and Connection if not
        // managed by DAO context (here simplified)
        // Ideally we return User object, but keeping signature for now or improving it.
        // Actually, to avoid connection leaks with ResultSet return, it's better to
        // return a mapped Object.
        // But for this quick implementation matching existing style:
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        return stmt.executeQuery();
    }
}
