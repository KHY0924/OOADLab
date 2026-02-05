package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import models.Session;
import models.Seminar;
import models.ScheduleItem;

public class SessionDAO {

    public List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        // Query to fetch session and all its evaluators
        String sql = "SELECT s.*, u.username as evaluator_name, u.user_id as eval_id " +
                "FROM sessions s " +
                "LEFT JOIN session_evaluators se ON s.session_id = se.session_id " +
                "LEFT JOIN users u ON se.evaluator_id = u.user_id " +
                "ORDER BY s.session_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            Session currentSession = null;
            while (rs.next()) {
                String id = rs.getString("session_id");
                if (currentSession == null || !currentSession.getSessionID().equals(id)) {
                    String loc = rs.getString("location");
                    Timestamp date = rs.getTimestamp("session_date");
                    String type = rs.getString("session_type");
                    currentSession = new Session(id, loc, new models.DateAndTime(date.toLocalDateTime().toLocalDate(),
                            date.toLocalDateTime().toLocalTime()), type);
                    sessions.add(currentSession);
                }

                String evalId = rs.getString("eval_id");
                String evalName = rs.getString("evaluator_name");
                if (evalId != null) {
                    currentSession.addEvaluator(evalId, evalName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public List<Session> getUnassignedSessions() {
        List<Session> sessions = new ArrayList<>();
        // Now showing all sessions so coordinator can manage assignments even after
        // evaluator is set
        String sql = "SELECT s.* FROM sessions s";
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
        String sql = "SELECT s.*, u.username as evaluator_name, u.user_id as eval_id " +
                "FROM sessions s " +
                "LEFT JOIN session_evaluators se ON s.session_id = se.session_id " +
                "LEFT JOIN users u ON se.evaluator_id = u.user_id " +
                "WHERE seminar_id = ?::uuid " +
                "ORDER BY s.session_id";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, seminarId);
            ResultSet rs = stmt.executeQuery();
            Session currentSession = null;
            while (rs.next()) {
                String id = rs.getString("session_id");
                if (currentSession == null || !currentSession.getSessionID().equals(id)) {
                    String loc = rs.getString("location");
                    Timestamp date = rs.getTimestamp("session_date");
                    String type = rs.getString("session_type");
                    currentSession = new Session(id, loc, new models.DateAndTime(date.toLocalDateTime().toLocalDate(),
                            date.toLocalDateTime().toLocalTime()), type);
                    sessions.add(currentSession);
                }

                String evalId = rs.getString("eval_id");
                String evalName = rs.getString("evaluator_name");
                if (evalId != null) {
                    currentSession.addEvaluator(evalId, evalName);
                }
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

    public List<String[]> getEvaluatorsInSession(String sessionId) throws SQLException {
        List<String[]> evaluators = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username FROM session_evaluators se " +
                "JOIN users u ON se.evaluator_id = u.user_id " +
                "WHERE se.session_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                evaluators.add(new String[] { rs.getString("user_id"), rs.getString("username") });
            }
        }
        return evaluators;
    }

    public ResultSet getAllSeminars() throws SQLException {
        String sql = "SELECT seminar_id, location, seminar_date, semester, year FROM Seminars ORDER BY year DESC, semester DESC";
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }

    public List<Seminar> getSeminarsList() {
        List<Seminar> seminars = new ArrayList<>();
        try (ResultSet rs = getAllSeminars()) {
            while (rs.next()) {
                String id = rs.getString("seminar_id");
                String loc = rs.getString("location");
                Timestamp ts = rs.getTimestamp("seminar_date");
                int semester = rs.getInt("semester");
                int year = rs.getInt("year");

                models.DateAndTime dt = null;
                if (ts != null) {
                    LocalDateTime ldt = ts.toLocalDateTime();
                    dt = new models.DateAndTime(ldt.toLocalDate(), ldt.toLocalTime());
                }

                seminars.add(new Seminar(id, loc, dt, semester, year));
            }
            if (!rs.isClosed()) {
                rs.getStatement().getConnection().close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seminars;
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
        String sql = "INSERT INTO session_students (session_id, student_id) VALUES (?::uuid, ?::uuid) ON CONFLICT (session_id, student_id) DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.setString(2, studentId);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                // Already exists, just return or silently fail
            }
        }
    }

    public void removeStudentFromSession(String sessionId, String studentId) throws SQLException {
        String sql = "DELETE FROM session_students WHERE session_id = ?::uuid AND student_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.setString(2, studentId);
            stmt.executeUpdate();
        }
    }

    public void addEvaluatorToSession(String sessionId, String evaluatorId) throws SQLException {
        String sql = "INSERT INTO session_evaluators (session_id, evaluator_id) VALUES (?::uuid, ?::uuid) ON CONFLICT (session_id, evaluator_id) DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.setString(2, evaluatorId);
            stmt.executeUpdate();
        }
    }

    public void removeEvaluatorFromSession(String sessionId, String evaluatorId) throws SQLException {
        String sql = "DELETE FROM session_evaluators WHERE session_id = ?::uuid AND evaluator_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.setString(2, evaluatorId);
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

    public List<String> getStudentIdsInSession(String sessionId) throws SQLException {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT student_id FROM session_students WHERE session_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                ids.add(rs.getString("student_id"));
        }
        return ids;
    }

    public List<String> getEvaluatorIdsInSession(String sessionId) throws SQLException {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT evaluator_id FROM session_evaluators WHERE session_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                ids.add(rs.getString("evaluator_id"));
        }
        return ids;

    }

    public List<String[]> getAssignmentsOverview(String sessionId) throws SQLException {
        List<String[]> overview = new ArrayList<>();
        String sql = "SELECT " +
                "    sp.full_name as student_name, " +
                "    sub.title as submission_title, " +
                "    u_eval.username as evaluator_name, " +
                "    u_eval.user_id as eval_id, " +
                "    ss.student_id " +
                "FROM session_students ss " +
                "JOIN student_profiles sp ON ss.student_id = sp.user_id " +
                "JOIN submissions sub ON ss.student_id = sub.student_id " +
                "LEFT JOIN users u_eval ON ss.evaluator_id = u_eval.user_id " +
                "WHERE ss.session_id = ?::uuid " +
                "ORDER BY sp.full_name";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                overview.add(new String[] {
                        rs.getString("student_name"),
                        rs.getString("submission_title"),
                        rs.getString("evaluator_name") != null ? rs.getString("evaluator_name") : "Not Assigned",
                        rs.getString("eval_id"),
                        rs.getString("student_id")
                });
            }
        }
        return overview;
    }

    public void updateStudentEvaluator(String sessionId, String studentId, String evaluatorId) throws SQLException {
        String sql = "UPDATE session_students SET evaluator_id = ?::uuid WHERE session_id = ?::uuid AND student_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (evaluatorId == null || evaluatorId.isEmpty()) {
                stmt.setNull(1, Types.OTHER);
            } else {
                stmt.setString(1, evaluatorId);
            }
            stmt.setString(2, sessionId);
            stmt.setString(3, studentId);
            stmt.executeUpdate();
        }

        // Also sync with evaluator_assignments for the scoring system
        if (evaluatorId != null && !evaluatorId.isEmpty()) {
            String subSql = "SELECT submission_id FROM submissions WHERE student_id = ?::uuid";
            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement subStmt = conn.prepareStatement(subSql)) {
                subStmt.setString(1, studentId);
                ResultSet rs = subStmt.executeQuery();
                if (rs.next()) {
                    String submissionId = rs.getString("submission_id");
                    // Remove existing assignments for this student to ensure 1:1
                    String delSql = "DELETE FROM evaluator_assignments WHERE submission_id = ?::uuid";
                    try (PreparedStatement delStmt = conn.prepareStatement(delSql)) {
                        delStmt.setString(1, submissionId);
                        delStmt.executeUpdate();
                    }
                    // Add new assignment
                    String insSql = "INSERT INTO evaluator_assignments (evaluator_id, submission_id) VALUES (?::uuid, ?::uuid)";
                    try (PreparedStatement insStmt = conn.prepareStatement(insSql)) {
                        insStmt.setString(1, evaluatorId);
                        insStmt.setString(2, submissionId);
                        insStmt.executeUpdate();
                    }
                }
            }
        }
    }
}
