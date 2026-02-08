package database;

import java.sql.*;

public class SeedEvaluations {
    public static void main(String[] args) {
        String targetSeminarId = "5122296d-38b5-475e-abf4-c19562a3ab2f";

        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Seeding evaluations for seminar: " + targetSeminarId);

             
            String evaluatorId = null;
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT user_id FROM users WHERE role = 'evaluator' LIMIT 1");
            if (rs.next())
                evaluatorId = rs.getString(1);
            System.out.println("Evaluator ID: " + evaluatorId);

             
            rs = conn.createStatement().executeQuery(
                    "SELECT submission_id, title FROM submissions WHERE seminar_id = '" + targetSeminarId + "'");

            PreparedStatement insertEval = conn.prepareStatement(
                    "INSERT INTO evaluations (submission_id, evaluator_id, problem_clarity, methodology, results, presentation, overall_score, comments) "
                            +
                            "VALUES (?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING");

            int count = 0;
            java.util.Random rand = new java.util.Random();

            while (rs.next()) {
                String submissionId = rs.getString("submission_id");
                String title = rs.getString("title");

                 
                int clarity = 15 + rand.nextInt(11);
                int methodology = 15 + rand.nextInt(11);
                int results = 15 + rand.nextInt(11);
                int presentation = 15 + rand.nextInt(11);
                int overall = clarity + methodology + results + presentation;

                insertEval.setString(1, submissionId);
                insertEval.setString(2, evaluatorId);
                insertEval.setInt(3, clarity);
                insertEval.setInt(4, methodology);
                insertEval.setInt(5, results);
                insertEval.setInt(6, presentation);
                insertEval.setInt(7, overall);
                insertEval.setString(8, "Good work on " + title + ". Keep improving methodology.");

                int rows = insertEval.executeUpdate();
                if (rows > 0) {
                    System.out.println("  Evaluated: " + title);
                    System.out.println("    Clarity: " + clarity + " | Methodology: " + methodology +
                            " | Results: " + results + " | Presentation: " + presentation);
                    System.out.println("    TOTAL: " + overall + "/100");
                    count++;
                }
            }

            System.out.println("\nDone! Added " + count + " evaluations.");
            System.out.println("Restart app and click '2. Evaluation Summary' again.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
