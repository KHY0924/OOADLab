package database;

import java.sql.Connection;
import java.sql.Statement;

public class FixPresentationBoards {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            System.out.println("Adding missing columns to presentation_boards...");

             
            stmt.execute(
                    "ALTER TABLE presentation_boards ADD COLUMN IF NOT EXISTS session_id UUID REFERENCES sessions(session_id) ON DELETE CASCADE");

             
            stmt.execute(
                    "ALTER TABLE presentation_boards ADD COLUMN IF NOT EXISTS presentation_type VARCHAR(50)");

            System.out.println("presentation_boards schema updated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
