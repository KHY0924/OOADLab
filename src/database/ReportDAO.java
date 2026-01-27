package database;

import java.sql.*;

public class ReportDAO {

    public ResultSet getSeminarSchedule() throws SQLException {
        String sql = "SELECT s.session_date, s.location, sub.title, u.username as student_name " +
                "FROM sessions s " +
                "JOIN session_students ss ON s.session_id = ss.session_id " +
                "JOIN submissions sub ON ss.student_id = sub.student_id " +
                "JOIN users u ON sub.student_id = u.user_id " +
                "ORDER BY s.session_date ASC";
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }

    public ResultSet getEvaluationSummary() throws SQLException {
        // Correcting the query to match the evaluations table schema
        String sql = "SELECT sub.title, u.username as student_name, e.overall_score, e.comments " +
                "FROM evaluations e " +
                "JOIN submissions sub ON e.submission_id = sub.submission_id " +
                "JOIN users u ON sub.student_id = u.user_id";
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }

    public ResultSet getAwardWinners() throws SQLException {
        // Determines winners based on highest overall_score per presentation type
        // Including a subquery or union for People's Choice (using a random high score
        // if no voting exists)
        String sql = "SELECT presentation_type, title, student_name, overall_score FROM (" +
                "  SELECT sub.presentation_type, sub.title, u.username as student_name, e.overall_score " +
                "  FROM evaluations e " +
                "  JOIN submissions sub ON e.submission_id = sub.submission_id " +
                "  JOIN users u ON sub.student_id = u.user_id " +
                "  WHERE e.overall_score = (" +
                "    SELECT MAX(overall_score) FROM evaluations e2 " +
                "    JOIN submissions sub2 ON e2.submission_id = sub2.submission_id " +
                "    WHERE sub2.presentation_type = sub.presentation_type" +
                "  ) " +
                "  UNION ALL " +
                "  SELECT 'People''s Choice' as presentation_type, sub.title, u.username as student_name, e.overall_score "
                +
                "  FROM evaluations e " +
                "  JOIN submissions sub ON e.submission_id = sub.submission_id " +
                "  JOIN users u ON sub.student_id = u.user_id " +
                "  ORDER BY overall_score DESC LIMIT 1" +
                ") AS winners";
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }
}
