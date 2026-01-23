package database;

import java.sql.*;

public class StudentProfileDAO {

    public void createProfile(String userId, String fullName, String email, String major) throws SQLException {
        String sql = "INSERT INTO student_profiles (user_id, full_name, email, major) VALUES (?::uuid, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, fullName);
            stmt.setString(3, email);
            stmt.setString(4, major);
            stmt.executeUpdate();
        }
    }

    public ResultSet findByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM student_profiles WHERE user_id = ?::uuid";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, userId);
        return stmt.executeQuery();
    }

    public void updateProfile(String userId, String fullName, String email, String major) throws SQLException {
        String sql = "UPDATE student_profiles SET full_name = ?, email = ?, major = ? WHERE user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, major);
            stmt.setString(4, userId);
            stmt.executeUpdate();
        }
    }
}
