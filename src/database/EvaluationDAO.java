package database;

import models.Evaluation;

import database.DatabaseConnection;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

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

    public List<Evaluation> getEvaluationsBySessionId(String sessionId) {
        List<Evaluation> evaluations = new ArrayList<>();
        String sql = "SELECT e.* FROM evaluations e " +
                     "JOIN submissions s ON e.submission_id = s.submission_id " +
                     "JOIN session_students ss ON s.student_id = ss.student_id " +
                     "WHERE ss.session_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String evaluationId = rs.getString("evaluation_id");
                    String submissionId = rs.getString("submission_id");
                    String evaluatorId = rs.getString("evaluator_id");
                    int score = rs.getInt("score");
                    String comments = rs.getString("comments");
                    int problemClarity = rs.getInt("problem_clarity");
                    int methodology = rs.getInt("methodology");
                    int results = rs.getInt("results");
                    int presentation = rs.getInt("presentation");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    evaluations.add(new Evaluation(evaluationId, submissionId, evaluatorId, score, comments,
                                                  problemClarity, methodology, results, presentation, createdAt));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evaluations;
    }

    public int getHighestScoreForSessionType(String type) {
        String sql = "SELECT MAX(e.score) FROM evaluations e " +
                     "JOIN submissions s ON e.submission_id = s.submission_id " +
                     "JOIN session_students ss ON s.student_id = ss.student_id " +
                     "JOIN sessions sess ON ss.session_id = sess.session_id " +
                     "WHERE sess.type = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getHighestOverallScore() {
        String sql = "SELECT MAX(score) FROM evaluations";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getSubmissionWithHighestScoreForType(String type, int score) {
        String sql = "SELECT e.submission_id FROM evaluations e " +
                     "JOIN submissions s ON e.submission_id = s.submission_id " +
                     "JOIN session_students ss ON s.student_id = ss.student_id " +
                     "JOIN sessions sess ON ss.session_id = sess.session_id " +
                     "WHERE sess.type = ? AND e.score = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            stmt.setInt(2, score);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSubmissionWithHighestOverallScore(int score) {
        String sql = "SELECT submission_id FROM evaluations WHERE score = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, score);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

