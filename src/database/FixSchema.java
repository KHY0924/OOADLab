package database;

import java.sql.Connection;
import java.sql.Statement;

public class FixSchema {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            System.out.println("Adding seminar_id to sessions...");
            stmt.execute(
                    "ALTER TABLE sessions ADD COLUMN IF NOT EXISTS seminar_id UUID REFERENCES Seminars(seminar_id) ON DELETE CASCADE");
            System.out.println("Schema fix applied successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
