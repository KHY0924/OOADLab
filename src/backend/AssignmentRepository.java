package backend;

import database.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class AssignmentRepository {

    public List<Submission> getAssignmentsForEvaluator(String evaluatorId) {
        List<Submission> list = new ArrayList<>();
        
        // Join tables to get assignments
        String sql = "SELECT s.* FROM submissions s " +
                     "JOIN evaluator_assignments ea ON s.submission_id = ea.submission_id " +
                     "WHERE ea.evaluator_id = ?::uuid"; 

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, evaluatorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String subId = rs.getString("submission_id");
                String title = rs.getString("title");
                list.add(new Submission(subId, title));
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}