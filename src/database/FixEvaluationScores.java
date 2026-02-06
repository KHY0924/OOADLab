package database;

import java.sql.*;

/**
 * Fix existing evaluation scores to use the correct calculation:
 * overall_score = (problem_clarity + methodology + results + presentation) *
 * 2.5
 */
public class FixEvaluationScores {
    public static void main(String[] args) {
        System.out.println("=== Fixing Evaluation Overall Scores ===\n");

        String sql = "UPDATE evaluations SET overall_score = " +
                "(problem_clarity + methodology + results + presentation) * 2.5";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int rows = stmt.executeUpdate();
            System.out.println("Fixed " + rows + " evaluation(s).");

            // Show updated scores
            System.out.println("\n--- Updated Evaluations ---");
            PreparedStatement showStmt = conn.prepareStatement(
                    "SELECT e.problem_clarity, e.methodology, e.results, e.presentation, " +
                            "e.overall_score, u.username as evaluator " +
                            "FROM evaluations e LEFT JOIN users u ON e.evaluator_id = u.user_id");
            ResultSet rs = showStmt.executeQuery();
            while (rs.next()) {
                int sum = rs.getInt("problem_clarity") + rs.getInt("methodology") +
                        rs.getInt("results") + rs.getInt("presentation");
                System.out.println("Evaluator: " + rs.getString("evaluator") +
                        " | Scores: " + rs.getInt("problem_clarity") + "+" +
                        rs.getInt("methodology") + "+" + rs.getInt("results") + "+" +
                        rs.getInt("presentation") + " = " + sum +
                        " | Overall: " + rs.getInt("overall_score") + "/100");
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Done! ===");
    }
}
