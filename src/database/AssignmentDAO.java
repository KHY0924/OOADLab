package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Submission;

public class AssignmentDAO {

    public List<Submission> getAssignmentsForEvaluator(String evaluatorId) {
        List<Submission> list = new ArrayList<>();
        String sql = "SELECT s.*, u.username as student_name FROM submissions s " +
        "JOIN evaluator_assignments ea ON s.submission_id = ea.submission_id " +
        "JOIN users u ON s.student_id = u.user_id " +
        "WHERE ea.evaluator_id = ?::uuid";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, evaluatorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String subId = rs.getString("submission_id");
                String title = rs.getString("title");
                String studentName = rs.getString("student_name");
                String type = rs.getString("presentation_type");
                String supervisor = rs.getString("supervisor");
                Submission sub = new Submission(subId, null, null, title, null, supervisor, type);
                sub.setStudentName(studentName);
                list.add(sub);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error retrieving assignments for evaluator.");
        }
        return list;
    }
}
