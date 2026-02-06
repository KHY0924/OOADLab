package database;

import java.sql.*;

public class DebugStudentSessions {
    public static void main(String[] args) {
        System.out.println("=== DEBUGGING STUDENT SESSION ASSIGNMENTS ===\n");

        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1. All submissions
            System.out.println("--- ALL SUBMISSIONS ---");
            PreparedStatement stmt1 = conn.prepareStatement(
                    "SELECT sub.submission_id, u.username, sub.title, sub.seminar_id " +
                            "FROM submissions sub JOIN users u ON sub.student_id = u.user_id");
            ResultSet rs1 = stmt1.executeQuery();
            while (rs1.next()) {
                System.out.println("Student: " + rs1.getString("username") +
                        " | Title: " + rs1.getString("title") +
                        " | Seminar: " + rs1.getString("seminar_id"));
            }

            // 2. Session-Student assignments
            System.out.println("\n--- SESSION_STUDENTS TABLE ---");
            PreparedStatement stmt2 = conn.prepareStatement(
                    "SELECT ss.session_id, u.username, ss.evaluator_id " +
                            "FROM session_students ss JOIN users u ON ss.student_id = u.user_id");
            ResultSet rs2 = stmt2.executeQuery();
            boolean hasRows = false;
            while (rs2.next()) {
                hasRows = true;
                System.out.println("Session: " + rs2.getString("session_id") +
                        " | Student: " + rs2.getString("username") +
                        " | Evaluator ID: " + rs2.getString("evaluator_id"));
            }
            if (!hasRows) {
                System.out.println("(No students assigned to sessions!)");
            }

            // 3. Sessions
            System.out.println("\n--- SESSIONS ---");
            PreparedStatement stmt3 = conn.prepareStatement(
                    "SELECT session_id, location, session_type, seminar_id FROM sessions");
            ResultSet rs3 = stmt3.executeQuery();
            while (rs3.next()) {
                System.out.println("ID: " + rs3.getString("session_id") +
                        " | Location: " + rs3.getString("location") +
                        " | Type: " + rs3.getString("session_type"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
