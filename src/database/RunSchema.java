package database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

public class RunSchema {
    public static void main(String[] args) {
        run();
    }

    public static void run() {
        String schemaPath = "sql/schema.sql";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                BufferedReader br = new BufferedReader(new FileReader(schemaPath))) {

            System.out.println("Resetting Database...");
            String dropSql = "DROP TABLE IF EXISTS evaluation_criteria, poster_presentations, presentation_boards, evaluations, evaluator_assignments, session_students, sessions, materials, submissions, student_profiles, Seminars, schedule CASCADE";
            stmt.execute(dropSql);
            System.out.println("Tables dropped.");

            System.out.println("Reading schema from: " + schemaPath);
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {

                if (line.startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }
                sqlBuilder.append(line).append("\n");
            }

            String sql = sqlBuilder.toString();
            String[] statements = sql.split(";");

            System.out.println("Executing SQL statements...");
            int count = 0;
            for (String s : statements) {
                String trimmed = s.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                    count++;
                }
            }
            System.out.println("Executed " + count + " SQL statements successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
