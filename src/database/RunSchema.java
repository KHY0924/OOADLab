package database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

public class RunSchema {
    public static void main(String[] args) {
        String schemaPath = "sql/schema.sql";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                BufferedReader br = new BufferedReader(new FileReader(schemaPath))) {

            System.out.println("Resetting Database...");
            String dropSql = "DROP TABLE IF EXISTS evaluations, evaluator_assignments, session_students, sessions, submissions, student_profiles, users CASCADE";
            stmt.execute(dropSql);
            System.out.println("Tables dropped.");

            System.out.println("Reading schema from: " + schemaPath);
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                // Ignore comments and empty lines for simpler parsing,
                // but usually reading the whole file string is fine if ; is handled or executed
                // as one block
                if (line.startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }
                sqlBuilder.append(line).append("\n");
            }

            String sql = sqlBuilder.toString();
            // Split by semicolon to execute multiple statements if needed,
            // OR execute as one block if DB supports it.
            // PostgreSQL supports executing multiple statements in one go often.

            System.out.println("Executing SQL...");
            stmt.execute(sql);
            System.out.println("Schema executed successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
