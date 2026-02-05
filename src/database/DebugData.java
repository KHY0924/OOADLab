package database;

import java.sql.*;

public class DebugData {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("=== SEMINARS ===");
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM seminars LIMIT 5");
            while (rs.next()) {
                System.out.println("  " + rs.getString(1) + " - " + rs.getString(2));
            }

            System.out.println("\n=== SESSIONS ===");
            rs = conn.createStatement()
                    .executeQuery("SELECT session_id, seminar_id, session_date, location FROM sessions LIMIT 10");
            while (rs.next()) {
                System.out.println("  " + rs.getString("session_id") + " | Seminar: " + rs.getString("seminar_id")
                        + " | " + rs.getTimestamp("session_date") + " | " + rs.getString("location"));
            }

            System.out.println("\n=== SUBMISSIONS ===");
            rs = conn.createStatement().executeQuery(
                    "SELECT s.submission_id, s.seminar_id, s.student_id, s.title, u.username FROM submissions s JOIN users u ON s.student_id = u.user_id LIMIT 10");
            while (rs.next()) {
                System.out.println("  " + rs.getString("submission_id") + " | Seminar: " + rs.getString("seminar_id")
                        + " | " + rs.getString("username") + " | " + rs.getString("title"));
            }

            System.out.println("\n=== SESSION_STUDENTS ===");
            rs = conn.createStatement().executeQuery("SELECT * FROM session_students LIMIT 10");
            while (rs.next()) {
                System.out.println("  Session: " + rs.getString("session_id") + " | Student: "
                        + rs.getString("student_id") + " | Evaluator: " + rs.getString("evaluator_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
