package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class RepairSchema {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            System.out.println("Adding missing columns to presentation_boards...");
            try {
                stmt.execute(
                        "ALTER TABLE presentation_boards ADD COLUMN session_id UUID REFERENCES sessions(session_id) ON DELETE SET NULL");
                System.out.println("- Added session_id to presentation_boards");
            } catch (SQLException e) {
                System.out.println("- session_id may already exist or error: " + e.getMessage());
            }

            try {
                stmt.execute("ALTER TABLE presentation_boards ADD COLUMN presentation_type VARCHAR(50)");
                System.out.println("- Added presentation_type to presentation_boards");
            } catch (SQLException e) {
                System.out.println("- presentation_type may already exist or error: " + e.getMessage());
            }

            System.out.println("Adding missing columns to session_students...");
            try {
                stmt.execute(
                        "ALTER TABLE session_students ADD COLUMN evaluator_id UUID REFERENCES users(user_id) ON DELETE SET NULL");
                System.out.println("- Added evaluator_id to session_students");
            } catch (SQLException e) {
                System.out.println("- evaluator_id may already exist or error: " + e.getMessage());
            }

            System.out.println("Verifying critical tables and columns...");
            checkTable(stmt, "submissions");
            checkTable(stmt, "presentation_boards");
            checkTable(stmt, "poster_presentations");
            checkTable(stmt, "seminars");
            checkTable(stmt, "session_students");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkTable(Statement stmt, String table) {
        try {
            stmt.executeQuery("SELECT * FROM " + table + " LIMIT 1");
            System.out.println("Table '" + table + "' exists and is accessible.");
        } catch (SQLException e) {
            System.err.println("Table '" + table + "' error: " + e.getMessage());
        }
    }
}
