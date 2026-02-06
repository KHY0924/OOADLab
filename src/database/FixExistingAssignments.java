package database;

import java.sql.*;

/**
 * One-time script to auto-assign existing students to matching sessions
 */
public class FixExistingAssignments {
    public static void main(String[] args) {
        System.out.println("=== Auto-assigning existing students to matching sessions ===\n");

        String sql = "INSERT INTO session_students (session_id, student_id) " +
                "SELECT DISTINCT s.session_id, sub.student_id " +
                "FROM submissions sub " +
                "JOIN sessions s ON sub.seminar_id = s.seminar_id " +
                "    AND s.session_type ILIKE '%' || split_part(sub.presentation_type, ' ', 1) || '%' " +
                "WHERE NOT EXISTS ( " +
                "    SELECT 1 FROM session_students ss " +
                "    WHERE ss.session_id = s.session_id AND ss.student_id = sub.student_id " +
                ")";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int rows = stmt.executeUpdate();
            System.out.println("✓ Auto-assigned " + rows + " student(s) to matching sessions.");

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Done! Please regenerate the report. ===");
    }
}
