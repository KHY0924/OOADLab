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
        // Modified query to fetch evaluator name
        String sql = "SELECT s.*, " +
                " (SELECT u.username FROM users u WHERE u.user_id = s.evaluator_id) as evaluator_name " +
                "FROM sessions s";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("session_id");
                String loc = rs.getString("location");
                Timestamp date = rs.getTimestamp("session_date");
                String type = rs.getString("session_type");
                String evaluatorName = rs.getString("evaluator_name");

                Session session = new Session(id, loc, new models.DateAndTime(date.toLocalDateTime().toLocalDate(),
                        date.toLocalDateTime().toLocalTime()), type);
                session.setEvaluatorName(evaluatorName);
                session.setEvaluatorId(rs.getString("evaluator_id"));
                sessions.add(session);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public List<Session> getUnassignedSessions() {
        List<Session> sessions = new ArrayList<>();
        // Query to fetch only sessions where evaluator_id is NULL
        String sql = "SELECT s.* FROM sessions s WHERE s.evaluator_id IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("session_id");
                String loc = rs.getString("location");
                Timestamp date = rs.getTimestamp("session_date");
                String type = rs.getString("session_type");

                Session session = new Session(id, loc, new models.DateAndTime(date.toLocalDateTime().toLocalDate(),
                        date.toLocalDateTime().toLocalTime()), type);
                // evaluatorName and evaluatorId are null by definition of the query
                sessions.add(session);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public List<Session> getSessionsBySeminar(String seminarId) {
        List<Session> sessions = new ArrayList<>();
        // Modified query to fetch evaluator name
        String sql = "SELECT s.*, " +
                " (SELECT u.username FROM users u WHERE u.user_id = s.evaluator_id) as evaluator_name " +
                "FROM sessions s WHERE seminar_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, seminarId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("session_id");
                String loc = rs.getString("location");
                Timestamp date = rs.getTimestamp("session_date");
                String type = rs.getString("session_type");
                String evaluatorName = rs.getString("evaluator_name");

                Session session = new Session(id, loc, new models.DateAndTime(date.toLocalDateTime().toLocalDate(),
                        date.toLocalDateTime().toLocalTime()), type);
                session.setEvaluatorName(evaluatorName);
                session.setEvaluatorId(rs.getString("evaluator_id"));
                sessions.add(session);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public void createSession(String sessionId, String seminarId, String location, Timestamp date, String type)
            throws SQLException {
        String sql = "INSERT INTO sessions (session_id, seminar_id, location, session_date, session_type) VALUES (?::uuid, ?::uuid, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.setString(2, seminarId);
            stmt.setString(3, location);
            stmt.setTimestamp(4, date);
            stmt.setString(5, type);
            stmt.executeUpdate();
        }
    }

    public void createSeminar(String seminarId, String location, Timestamp date, int semester, int year)
            throws SQLException {
        String sql = "INSERT INTO Seminars (seminar_id, location, seminar_date, semester, year) VALUES (?::uuid, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, seminarId);
            stmt.setString(2, location);
            stmt.setTimestamp(3, date);
            stmt.setInt(4, semester);
            stmt.setInt(5, year);
            stmt.executeUpdate();
        }
    }

    public ResultSet getAllSeminars() throws SQLException {
        String sql = "SELECT seminar_id, location, seminar_date, semester, year FROM Seminars ORDER BY year DESC, semester DESC";
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
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
        String sql = "UPDATE sessions SET location = ?, session_date = ?, session_type = ? WHERE session_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, location);
            stmt.setTimestamp(2, date);
            stmt.setString(3, type);
            stmt.setString(4, sessionID);
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

    public void assignEvaluatorToSession(String sessionId, String evaluatorId) throws SQLException {
        // 1. Get all students in this session
        String getStudentsSql = "SELECT student_id FROM session_students WHERE session_id = ?::uuid";
        List<String> studentIds = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(getStudentsSql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                studentIds.add(rs.getString("student_id"));
            }
        }

        if (studentIds.isEmpty()) {
            throw new SQLException("No students found in this session.");
        }

        // 2. For each student, find their submission and assign the evaluator
        // We reuse the existing logic but handle it in a batch or loop
        int assignedCount = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String subSql = "SELECT submission_id FROM submissions WHERE student_id = ?::uuid";
            String assignSql = "INSERT INTO evaluator_assignments (evaluator_id, submission_id) VALUES (?::uuid, ?::uuid) ON CONFLICT DO NOTHING";
            // Note: simple schema might not have unique constraint properly set for ON
            // CONFLICT,
            // so we might need check-then-insert or just catch exception.
            // existing assignEvaluator throws if exists. let's try to be robust.

            PreparedStatement subStmt = conn.prepareStatement(subSql);
            PreparedStatement assignStmt = conn.prepareStatement(assignSql);

            for (String studentId : studentIds) {
                subStmt.setString(1, studentId);
                ResultSet subRs = subStmt.executeQuery();
                if (subRs.next()) {
                    String submissionId = subRs.getString("submission_id");

                    // Check if already assigned to THIS evaluator
                    String checkSql = "SELECT 1 FROM evaluator_assignments WHERE evaluator_id = ?::uuid AND submission_id = ?::uuid";
                    PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                    checkStmt.setString(1, evaluatorId);
                    checkStmt.setString(2, submissionId);
                    ResultSet checkRs = checkStmt.executeQuery();

                    if (!checkRs.next()) {
                        assignStmt.setString(1, evaluatorId);
                        assignStmt.setString(2, submissionId);
                        assignStmt.executeUpdate();
                        assignedCount++;
                    }
                    checkStmt.close();
                }
                subRs.close();
            }
            subStmt.close();
            assignStmt.close();

            // 3. Update the session table itself with the evaluator_id
            String updateSessionSql = "UPDATE sessions SET evaluator_id = ?::uuid WHERE session_id = ?::uuid";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSessionSql)) {
                updateStmt.setString(1, evaluatorId);
                updateStmt.setString(2, sessionId);
                updateStmt.executeUpdate();
            }
        }

        if (assignedCount == 0 && !studentIds.isEmpty()) {
            // Maybe they were all already assigned or no submissions found
            // Just return silently or log
        }
    }
}
