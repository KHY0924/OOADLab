package database;

import java.sql.Connection;
import java.sql.Statement;

public class ClearTestData {
    public static void main(String[] args) {
        System.out.println("=== Clearing ALL tables except USERS ===\n");

         
        String[] tablesToClear = {
                "evaluation_criteria",
                "poster_presentations",
                "presentation_boards",
                "schedule",
                "evaluations",
                "evaluator_assignments",
                "session_evaluators",
                "session_students",
                "sessions",
                "materials",
                "submissions",
                "seminars",
                "student_profiles"
        };

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            for (String table : tablesToClear) {
                try {
                    stmt.executeUpdate("DELETE FROM " + table);
                    System.out.println("✓ Cleared: " + table);
                } catch (Exception e) {
                    System.out.println("✗ Error clearing " + table + ": " + e.getMessage());
                }
            }

             
            stmt.executeUpdate("ALTER SEQUENCE presentation_boards_board_id_seq RESTART WITH 1");
            stmt.executeUpdate("ALTER SEQUENCE poster_presentations_presentation_id_seq RESTART WITH 1");
            stmt.executeUpdate("ALTER SEQUENCE evaluation_criteria_criteria_id_seq RESTART WITH 1");
            System.out.println("\n✓ Reset auto-increment sequences");

            System.out.println("\n=== DONE! Only USERS table preserved. ===");

        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
