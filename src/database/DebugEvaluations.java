package database;

import java.sql.*;

public class DebugEvaluations {
    public static void main(String[] args) {
        System.out.println("=== DEBUGGING EVALUATIONS ===\n");

        try (Connection conn = DatabaseConnection.getConnection()) {

             
            System.out.println("--- ALL SUBMISSIONS ---");
            PreparedStatement stmt1 = conn.prepareStatement(
                    "SELECT sub.submission_id, u.username, sub.title, sub.presentation_type " +
                            "FROM submissions sub JOIN users u ON sub.student_id = u.user_id");
            ResultSet rs1 = stmt1.executeQuery();
            while (rs1.next()) {
                System.out.println("  " + rs1.getString("username") +
                        " | " + rs1.getString("title") +
                        " | " + rs1.getString("presentation_type") +
                        " | ID: " + rs1.getString("submission_id"));
            }

             
            System.out.println("\n--- ALL EVALUATIONS ---");
            PreparedStatement stmt2 = conn.prepareStatement(
                    "SELECT e.evaluation_id, e.submission_id, e.evaluator_id, " +
                            "e.problem_clarity, e.methodology, e.results, e.presentation, e.overall_score, " +
                            "u.username as evaluator_name " +
                            "FROM evaluations e " +
                            "LEFT JOIN users u ON e.evaluator_id = u.user_id");
            ResultSet rs2 = stmt2.executeQuery();
            while (rs2.next()) {
                System.out.println("  Evaluator: " + rs2.getString("evaluator_name") +
                        " | Scores: " + rs2.getInt("problem_clarity") + "/" +
                        rs2.getInt("methodology") + "/" + rs2.getInt("results") + "/" +
                        rs2.getInt("presentation") +
                        " | Overall: " + rs2.getInt("overall_score") +
                        " | SubID: " + rs2.getString("submission_id"));
            }

             
            System.out.println("\n--- EVALUATION SUMMARY QUERY RESULT ---");
            String seminarId = null;
            PreparedStatement stmtSem = conn.prepareStatement("SELECT seminar_id FROM seminars LIMIT 1");
            ResultSet rsSem = stmtSem.executeQuery();
            if (rsSem.next()) {
                seminarId = rsSem.getString("seminar_id");
            }

            if (seminarId != null) {
                String sql = "SELECT sub.title, u.username as student_name, sub.presentation_type, " +
                        "e.overall_score, e.comments, eval.username as evaluator_name " +
                        "FROM submissions sub " +
                        "JOIN users u ON sub.student_id = u.user_id " +
                        "LEFT JOIN evaluations e ON e.submission_id = sub.submission_id " +
                        "LEFT JOIN users eval ON e.evaluator_id = eval.user_id " +
                        "WHERE sub.seminar_id = ?::uuid";
                PreparedStatement stmt3 = conn.prepareStatement(sql);
                stmt3.setString(1, seminarId);
                ResultSet rs3 = stmt3.executeQuery();
                while (rs3.next()) {
                    System.out.println("  Student: " + rs3.getString("student_name") +
                            " | Evaluator: " + rs3.getString("evaluator_name") +
                            " | Score: " + rs3.getInt("overall_score") +
                            " | Title: " + rs3.getString("title"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
