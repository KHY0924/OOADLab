package database;

import java.sql.*;

public class ReportDAO {

    public ResultSet getSeminarSchedule(String seminarId) throws SQLException {
        String sql = "SELECT s.session_date, s.location, sub.title, u.username as student_name, sub.presentation_type "
                +
                "FROM sessions s " +
                "JOIN session_students ss ON s.session_id = ss.session_id " +
                "JOIN submissions sub ON ss.student_id = sub.student_id " +
                "JOIN users u ON sub.student_id = u.user_id " +
                "WHERE s.seminar_id = ?::uuid " +
                "ORDER BY s.session_date ASC";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, seminarId);
        return stmt.executeQuery();
    }

    public ResultSet getEvaluationSummary(String seminarId) throws SQLException {
        // Fetching individual scores and presentation type for more detail
        String sql = "SELECT sub.title, u.username as student_name, sub.presentation_type, " +
                "e.overall_score, e.comments, e.problem_clarity, e.methodology, e.results, e.presentation " +
                "FROM evaluations e " +
                "JOIN submissions sub ON e.submission_id = sub.submission_id " +
                "JOIN users u ON sub.student_id = u.user_id " +
                "WHERE sub.seminar_id = ?::uuid";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, seminarId);
        return stmt.executeQuery();
    }

    public ResultSet getAwardWinners(String seminarId) throws SQLException {
        String sql = "SELECT presentation_type, title, student_name, overall_score FROM (" +
                "  SELECT sub.presentation_type, sub.title, u.username as student_name, e.overall_score " +
                "  FROM evaluations e " +
                "  JOIN submissions sub ON e.submission_id = sub.submission_id " +
                "  JOIN users u ON sub.student_id = u.user_id " +
                "  WHERE sub.seminar_id = ?::uuid AND e.overall_score = (" +
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
                "  WHERE sub.seminar_id = ?::uuid " +
                "  ORDER BY overall_score DESC LIMIT 1" +
                ") AS winners";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, seminarId);
        stmt.setString(2, seminarId);
        return stmt.executeQuery();
    }
}
