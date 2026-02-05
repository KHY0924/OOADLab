package database;

import java.sql.*;

public class CheckSessionSeminar {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("=== ALL SESSIONS (showing seminar_id) ===");
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT session_id, seminar_id, session_date, location FROM sessions ORDER BY session_date LIMIT 10");
            while (rs.next()) {
                System.out.println("Session: " + rs.getString("session_id"));
                System.out.println("  Seminar ID: " + rs.getString("seminar_id"));
                System.out.println("  Date: " + rs.getTimestamp("session_date"));
                System.out.println("  Location: " + rs.getString("location"));
                System.out.println();
            }

            System.out.println("\n=== ALL SEMINARS ===");
            rs = conn.createStatement().executeQuery("SELECT * FROM seminars LIMIT 5");
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(md.getColumnName(i) + ": " + rs.getString(i) + " | ");
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
