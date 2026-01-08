package backend;

import database.DatabaseConnection; // Import your friend's connection tool
import java.sql.*;

public class EvaluationRepository {

    public void saveEvaluation(Evaluation evaluation) {
        // This SQL matches the table 'evaluations' we discussed
        String sql = "INSERT INTO evaluations (submission_id, evaluator_id, score, comments) VALUES (?, ?, ?, ?)";
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            // Convert String IDs to UUID objects for PostgreSQL
            stmt.setObject(1, java.util.UUID.fromString(evaluation.getSubmissionId()));
            stmt.setObject(2, java.util.UUID.fromString(evaluation.getEvaluatorId()));
            stmt.setInt(3, evaluation.getTotalScore());
            stmt.setString(4, evaluation.getComments());
            
            stmt.executeUpdate();
            System.out.println("Success: Evaluation saved to Database!");
            
            stmt.close();
            // Do NOT close conn (DatabaseConnection handles that)
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error: Failed to save evaluation to DB.");
        }
    }
}