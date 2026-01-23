package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import models.Session;
import models.Seminar;
import models.ScheduleItem;

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
                sessions.add(new Session(id, loc, new models.DateAndTime(date.toLocalDateTime().toLocalDate(),
                        date.toLocalDateTime().toLocalTime()), type));
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

    public void assignEvaluator(String evaluatorID, String studentID) throws SQLException {
        // Check if evaluator is already assigned
        String checkSql = "SELECT 1 FROM evaluator_assignments WHERE evaluator_id = ?::uuid";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setString(1, evaluatorID);
        ResultSet rs = checkStmt.executeQuery();
        if (rs.next()) {
            // Already assigned
            rs.close();
            checkStmt.close();
            throw new SQLException("Evaluator already assigned");
        }
        rs.close();
        checkStmt.close();

        // Find submission for student
        String subSql = "SELECT submission_id FROM submissions WHERE student_id = ?::uuid";
        PreparedStatement subStmt = conn.prepareStatement(subSql);
        subStmt.setString(1, studentID);
        ResultSet subRs = subStmt.executeQuery();
        if (subRs.next()) {
            String submissionId = subRs.getString("submission_id");
            // Assign
            String assignSql = "INSERT INTO evaluator_assignments (evaluator_id, submission_id) VALUES (?::uuid, ?::uuid)";
            PreparedStatement assignStmt = conn.prepareStatement(assignSql);
            assignStmt.setString(1, evaluatorID);
            assignStmt.setString(2, submissionId);
            assignStmt.executeUpdate();
            assignStmt.close();
        } else {
            subRs.close();
            subStmt.close();
            throw new SQLException("No submission found for student");
        }
        subRs.close();
        subStmt.close();
    }

    public List<ScheduleItem> getSessionSchedule() {
        List<ScheduleItem> schedule = new ArrayList<>();
        String sql = "SELECT s.session_id, s.session_date, s.location, ss.student_id, ea.evaluator_id " +
                     "FROM sessions s " +
                     "JOIN session_students ss ON s.session_id = ss.session_id " +
                     "JOIN submissions sub ON ss.student_id = sub.student_id " +
                     "JOIN evaluator_assignments ea ON sub.submission_id = ea.submission_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String sessionID = rs.getString("session_id");
                Timestamp sessionDate = rs.getTimestamp("session_date");
                LocalDate date = sessionDate.toLocalDateTime().toLocalDate();
                LocalTime time = sessionDate.toLocalDateTime().toLocalTime();
                String venue = rs.getString("location");
                String evaluatorID = rs.getString("evaluator_id");
                String studentID = rs.getString("student_id");
                schedule.add(new ScheduleItem(sessionID, date, time, venue, evaluatorID, studentID));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedule;
    }

}
