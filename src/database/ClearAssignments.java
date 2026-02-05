package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class ClearAssignments {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            System.out.println("Starting data cleanup...");

            String[] tablesToClear = {
                    "evaluator_assignments",
                    "session_students",
                    "session_evaluators"
            };

            for (String table : tablesToClear) {
                try {
                    System.out.println("Deleting from " + table + "...");
                    stmt.execute("DELETE FROM " + table);
                } catch (SQLException e) {
                    System.err.println("Error clearing table " + table + ": " + e.getMessage());
                }
            }

            try {
                System.out.println("Resetting evaluator_id in sessions...");
                stmt.execute("UPDATE sessions SET evaluator_id = NULL");
            } catch (SQLException e) {
                System.err.println("Error resetting sessions: " + e.getMessage());
            }

            System.out.println("Cleanup attempt finished.");

        } catch (Exception e) {
            System.err.println("Fatal error during connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
