package database;

import java.sql.Connection;
import java.sql.Statement;

public class FixSchemaPhase2 {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            System.out.println("Applying Phase 2 schema updates...");

             
            stmt.execute(
                    "ALTER TABLE sessions ADD COLUMN IF NOT EXISTS seminar_id UUID REFERENCES Seminars(seminar_id) ON DELETE CASCADE");

             
            stmt.execute(
                    "ALTER TABLE session_students ADD COLUMN IF NOT EXISTS evaluator_id UUID REFERENCES users(user_id) ON DELETE SET NULL");

            System.out.println("Phase 2 schema updates applied successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
