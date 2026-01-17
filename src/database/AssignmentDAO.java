package database;

import models.Submission;

import database.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class AssignmentDAO {

    public List<Submission> getAssignmentsForEvaluator(String evaluatorId) {
        List<Submission> list = new ArrayList<>();

        // Join tables to get assignments
        // Join tables to get assignments
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

                Submission sub = new Submission(subId, title);
                sub.setStudentName(studentName);
                list.add(sub);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}