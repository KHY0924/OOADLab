package database;

import java.sql.*;

public class SubmissionDAO {

    public void createSubmission(String seminarId, String studentId, String title,
            String abstractText, String supervisor, String presentationType) throws SQLException {
        String sql = "INSERT INTO submissions (seminar_id, student_id, title, abstract_text, supervisor, presentation_type) VALUES (?, ?::uuid, ?, ?, ?, ?)";
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
}
