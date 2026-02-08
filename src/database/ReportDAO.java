package database;

import java.sql.*;

public class ReportDAO {

    public ResultSet getSeminarSchedule(String seminarId) throws SQLException {
        String sql = "SELECT COALESCE(s.session_id::text, 'UNASSIGNED') as session_id, " +
                "s.session_date, COALESCE(s.location, 'Not Scheduled') as location, " +
                "sub.title, u.username as student_name, sub.presentation_type, " +
                "eval.username as evaluator_name, pb.board_name " +
                "FROM submissions sub " +
                "JOIN users u ON sub.student_id = u.user_id " +
                "LEFT JOIN session_students ss ON sub.student_id = ss.student_id " +
                "LEFT JOIN sessions s ON ss.session_id = s.session_id AND s.seminar_id = sub.seminar_id " +
                "LEFT JOIN users eval ON ss.evaluator_id = eval.user_id " +
                "LEFT JOIN poster_presentations pp ON sub.submission_id = pp.submission_id " +
                "LEFT JOIN presentation_boards pb ON pp.board_id = pb.board_id " +
                "WHERE sub.seminar_id = ?::uuid " +
                "ORDER BY s.session_date ASC NULLS LAST, sub.title ASC";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, seminarId);
        return stmt.executeQuery();
    }

    public ResultSet getEvaluationSummary(String seminarId) throws SQLException {
        String sql = "SELECT sub.title, u.username as student_name, sub.presentation_type, " +
                "e.overall_score, e.comments, e.problem_clarity, e.methodology, e.results, e.presentation, " +
                "eval.username as evaluator_name, pb.board_name " +
                "FROM submissions sub " +
                "JOIN users u ON sub.student_id = u.user_id " +
                "LEFT JOIN evaluations e ON e.submission_id = sub.submission_id " +
                "LEFT JOIN users eval ON e.evaluator_id = eval.user_id " +
                "LEFT JOIN poster_presentations pp ON sub.submission_id = pp.submission_id " +
                "LEFT JOIN presentation_boards pb ON pp.board_id = pb.board_id " +
                "WHERE sub.seminar_id = ?::uuid";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, seminarId);
        return stmt.executeQuery();
    }

    public ResultSet getAwardWinners(String seminarId) throws SQLException {
        String sql = "SELECT * FROM (" +
                "  (SELECT 'Best Oral' as award_category, sub.title, u.username as student_name, e.overall_score " +
                "   FROM evaluations e " +
                "   JOIN submissions sub ON e.submission_id = sub.submission_id " +
                "   JOIN users u ON sub.student_id = u.user_id " +
                "   WHERE sub.seminar_id = ?::uuid AND (sub.presentation_type ILIKE '%Oral%') " +
                "   ORDER BY e.overall_score DESC LIMIT 1) " +
                "  UNION ALL " +
                "  (SELECT 'Best Poster' as award_category, sub.title, u.username as student_name, e.overall_score " +
                "   FROM evaluations e " +
                "   JOIN submissions sub ON e.submission_id = sub.submission_id " +
                "   JOIN users u ON sub.student_id = u.user_id " +
                "   WHERE sub.seminar_id = ?::uuid AND (sub.presentation_type ILIKE '%Poster%') " +
                "   ORDER BY e.overall_score DESC LIMIT 1) " +
                "  UNION ALL " +
                "  (SELECT 'People''s Choice' as award_category, sub.title, u.username as student_name, e.overall_score "
                +
                "   FROM evaluations e " +
                "   JOIN submissions sub ON e.submission_id = sub.submission_id " +
                "   JOIN users u ON sub.student_id = u.user_id " +
                "   WHERE sub.seminar_id = ?::uuid " +
                "   ORDER BY e.overall_score DESC OFFSET 2 LIMIT 1) " +  
                ") AS winners";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, seminarId);
        stmt.setString(2, seminarId);
        stmt.setString(3, seminarId);
        return stmt.executeQuery();
    }
}
