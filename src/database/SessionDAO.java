package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Session;
import models.Seminar;

public class SessionDAO {

    public List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM sessions";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("session_id");
                String loc = rs.getString("location");
                Timestamp date = rs.getTimestamp("session_date");
                String type = rs.getString("type");
                sessions.add(new Session(id, loc, new models.DateAndTime(date.toLocalDateTime().toLocalDate(), date.toLocalDateTime().toLocalTime()), type));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public void createSession(String sessionId, String location, Timestamp date, String type) throws SQLException {
        String sql = "INSERT INTO sessions (session_id, location, session_date, type) VALUES (?::uuid, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.setString(2, location);
            stmt.setTimestamp(3, date);
            stmt.setString(4, type);
            stmt.executeUpdate();
        }
    }

    // CREATE SEMINAR DATABASE 
    public void createSeminar(String seminarId, String location, Timestamp date) throws SQLException {
        String sql = "INSERT INTO Seminars (seminar_id, location, seminar_date) VALUES (?::uuid, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, seminarId);
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

    public void updateSession(String sessionID, String location, Timestamp date, String type) throws SQLException {
        String sql = "UPDATE session SET location = ?, session_date = ?, type = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, location);
            stmt.setTimestamp(2, date);
            stmt.setString(3, type);
            stmt.executeUpdate();
        }
    }

    
}
