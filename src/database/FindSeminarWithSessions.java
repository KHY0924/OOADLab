package database;

import java.sql.*;

public class FindSeminarWithSessions {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("=== SEMINARS THAT HAVE SESSIONS ===");
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT DISTINCT sem.seminar_id, sem.semester, sem.year, sem.location " +
                            "FROM seminars sem " +
                            "JOIN sessions s ON sem.seminar_id = s.seminar_id");
            while (rs.next()) {
                System.out.println("Seminar ID: " + rs.getString("seminar_id"));
                System.out.println("  Semester " + rs.getInt("semester") + " " + rs.getInt("year") + " - "
                        + rs.getString("location"));
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
