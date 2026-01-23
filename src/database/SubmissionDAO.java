package database;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import models.Submission;

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

    public List<Submission> getAllSubmissions() {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT * FROM submissions";
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
                submissions.add(new Submission(submissionId, seminarId, studentId, title, abstractText, supervisor, presentationType));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return submissions;
    }
}
