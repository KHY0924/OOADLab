package database;

import java.sql.*;

/**
 * Clean up session assignments:
 * Students should only be in sessions matching their presentation type
 */
public class FixSessionAssignments {
    public static void main(String[] args) {
        System.out.println("=== Fixing Session Assignments ===\n");

        // Remove students from sessions where session_type doesn't match submission
        // presentation_type
        String sql = "DELETE FROM session_students ss " +
                "USING submissions sub, sessions s " +
                "WHERE ss.student_id = sub.student_id " +
                "AND ss.session_id = s.session_id " +
                "AND NOT (" +
                "  (sub.presentation_type ILIKE '%Oral%' AND s.session_type ILIKE '%Oral%') " +
                "  OR (sub.presentation_type ILIKE '%Poster%' AND s.session_type ILIKE '%Poster%') " +
                ")";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int rows = stmt.executeUpdate();
            System.out.println("Removed " + rows + " incorrect session assignment(s).");

            // Show remaining assignments
            System.out.println("\n--- Current Session Assignments ---");
            PreparedStatement showStmt = conn.prepareStatement(
                    "SELECT u.username, sub.presentation_type, s.session_type, s.location " +
                            "FROM session_students ss " +
                            "JOIN users u ON ss.student_id = u.user_id " +
                            "JOIN submissions sub ON ss.student_id = sub.student_id " +
                            "JOIN sessions s ON ss.session_id = s.session_id " +
                            "ORDER BY s.session_date, u.username");
            ResultSet rs = showStmt.executeQuery();
            while (rs.next()) {
                System.out.println("  " + rs.getString("username") +
                        " | Type: " + rs.getString("presentation_type") +
                        " | Session: " + rs.getString("session_type") +
                        " | Location: " + rs.getString("location"));
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Done! ===");
    }
}
