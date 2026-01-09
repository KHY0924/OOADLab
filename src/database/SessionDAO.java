package database;

import java.sql.*;

public class SessionDAO {

    public void createSession(String sessionId, String location, Timestamp date) throws SQLException {
        String sql = "INSERT INTO sessions (session_id, location, session_date) VALUES (?::uuid, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.setString(2, location);
            stmt.setTimestamp(3, date);
            stmt.executeUpdate();
        }
    }

    public ResultSet findBySessionId(String sessionId) throws SQLException {
        String sql = "SELECT * FROM sessions WHERE session_id = ?::uuid";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, sessionId);
        return stmt.executeQuery();
    }

    public ResultSet findSessionsByStudent(String studentId) throws SQLException {
        String sql = "SELECT s.* FROM sessions s JOIN session_students ss ON s.session_id = ss.session_id WHERE ss.student_id = ?::uuid";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, studentId);
        return stmt.executeQuery();
    }

    public void addStudentToSession(String sessionId, String studentId) throws SQLException {
        String sql = "INSERT INTO session_students (session_id, student_id) VALUES (?::uuid, ?::uuid)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.setString(2, studentId);
            stmt.executeUpdate();
        }
    }

    public void deleteSession(String sessionId) throws SQLException {
        String sql = "DELETE FROM sessions WHERE session_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.executeUpdate();
        }
    }
}
