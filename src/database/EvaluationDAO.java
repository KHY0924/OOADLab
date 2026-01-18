package database;

import models.Evaluation;

import database.DatabaseConnection;
import java.sql.*;

public class EvaluationDAO {

    public void saveEvaluation(Evaluation evaluation) {

        String sql = "INSERT INTO evaluations (submission_id, evaluator_id, score, comments, problem_clarity, methodology, results, presentation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setObject(1, java.util.UUID.fromString(evaluation.getSubmissionId()));
            stmt.setObject(2, java.util.UUID.fromString(evaluation.getEvaluatorId()));
            stmt.setInt(3, evaluation.getTotalScore());
            stmt.setString(4, evaluation.getComments());

            stmt.setInt(5, evaluation.getProblemClarityScore());
            stmt.setInt(6, evaluation.getMethodologyScore());
            stmt.setInt(7, evaluation.getResultsScore());
            stmt.setInt(8, evaluation.getPresentationScore());

            stmt.executeUpdate();
            System.out.println("Success: Evaluation saved to Database!");

            stmt.close();

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