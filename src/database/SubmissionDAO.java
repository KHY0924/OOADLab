package database;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import models.Submission;

public class SubmissionDAO {

    public void createSubmission(String seminarId, String studentId, String title,
            String abstractText, String supervisor, String presentationType) throws SQLException {
        String sql = "INSERT INTO submissions (seminar_id, student_id, title, abstract_text, supervisor, presentation_type) VALUES (?::uuid, ?::uuid, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, seminarId);
            stmt.setString(2, studentId);
            stmt.setString(3, title);
            stmt.setString(4, abstractText);
            stmt.setString(5, supervisor);
            stmt.setString(6, presentationType);
            stmt.executeUpdate();
        }
        autoAssignToSession(seminarId, studentId, presentationType);
    }

    private void autoAssignToSession(String seminarId, String studentId, String presentationType) {
        String findSessionSql = "SELECT session_id FROM sessions WHERE seminar_id = ?::uuid AND session_type ILIKE ? LIMIT 1";
        String insertSql = "INSERT INTO session_students (session_id, student_id) VALUES (?::uuid, ?::uuid) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement findStmt = conn.prepareStatement(findSessionSql)) {
            String typePattern = "%" + presentationType.split(" ")[0] + "%";
            findStmt.setString(1, seminarId);
            findStmt.setString(2, typePattern);
            ResultSet rs = findStmt.executeQuery();
            if (rs.next()) {
                String sessionId = rs.getString("session_id");
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, sessionId);
                    insertStmt.setString(2, studentId);
                    insertStmt.executeUpdate();
                    System.out
                            .println("[SubmissionDAO] Auto-assigned student " + studentId + " to session " + sessionId);
                }
            } else {
                System.out.println("[SubmissionDAO] No matching session found for type: " + presentationType);
            }
        } catch (SQLException e) {
            System.err.println("[SubmissionDAO] Auto-assign failed: Database error.");
        }
    }

    public ResultSet findByStudentId(String studentId) throws SQLException {
        String sql = "SELECT * FROM submissions WHERE student_id = ?::uuid";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, studentId);
        return stmt.executeQuery();
    }

    public void updateSubmission(String studentId, String title, String abstractText) throws SQLException {
        String sql = "UPDATE submissions SET title = ?, abstract_text = ? WHERE student_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, abstractText);
            stmt.setString(3, studentId);
            stmt.executeUpdate();
        }
    }

    public void saveFilePath(String studentId, String filePath) throws SQLException {
        String sql = "UPDATE submissions SET file_path = ? WHERE student_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, filePath);
            stmt.setString(2, studentId);
            stmt.executeUpdate();
        }
    }

    public void deleteSubmission(String studentId) throws SQLException {
        String sql = "DELETE FROM submissions WHERE student_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.executeUpdate();
        }
    }

    public ResultSet getAllSubmissions() throws SQLException {
        String sql = "SELECT s.*, u.username as student_name FROM submissions s JOIN users u ON s.student_id = u.user_id";
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }

    public List<Submission> getAllSubmissionsList() {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT s.*, u.username as student_name FROM submissions s JOIN users u ON s.student_id = u.user_id";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String submissionId = rs.getString("submission_id");
                String seminarId = rs.getString("seminar_id");
                String studentId = rs.getString("student_id");
                String title = rs.getString("title");
                String abstractText = rs.getString("abstract_text");
                String supervisor = rs.getString("supervisor");
                String presentationType = rs.getString("presentation_type");
                String studentName = rs.getString("student_name");
                Submission sub = new Submission(submissionId, seminarId, studentId, title, abstractText, supervisor,
                        presentationType);
                sub.setStudentName(studentName);
                submissions.add(sub);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving submissions list.");
        }
        return submissions;
    }
}
