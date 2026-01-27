package database;

import models.Evaluation;
import java.sql.*;

public class EvaluationDAO {

    public void saveEvaluation(Evaluation evaluation) {
        String sql = "INSERT INTO evaluations (submission_id, evaluator_id, problem_clarity, methodology, results, presentation, overall_score, comments) "
                +
                "VALUES (?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, evaluation.getSubmissionId());
            stmt.setString(2, evaluation.getEvaluatorId());
            stmt.setInt(3, evaluation.getProblemClarity());
            stmt.setInt(4, evaluation.getMethodology());
            stmt.setInt(5, evaluation.getResults());
            stmt.setInt(6, evaluation.getPresentation());
            stmt.setInt(7, evaluation.getOverallScore());
            stmt.setString(8, evaluation.getComments());

            stmt.executeUpdate();
            System.out.println("Success: Evaluation saved to Database!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error: Failed to save evaluation to DB.");
        }
    }

    public boolean isEvaluated(String submissionId) {
        String sql = "SELECT 1 FROM evaluations WHERE submission_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, submissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}