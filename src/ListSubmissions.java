import java.sql.*;
import java.io.*;
import database.DatabaseConnection;

public class ListSubmissions {
    public static void main(String[] args) {
        try (PrintStream out = new PrintStream(new FileOutputStream("db_debug.txt"));
                Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            out.println("--- Submissions in DB ---");
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT s.*, u.username FROM submissions s JOIN users u ON s.student_id = u.user_id")) {
                while (rs.next()) {
                    out.println("ID: " + rs.getString("submission_id") +
                            " | Student: " + rs.getString("username") +
                            " | Type: [" + rs.getString("presentation_type") + "]" +
                            " | Title: " + rs.getString("title"));
                }
            }

            out.println("\n--- Sessions in DB ---");
            try (ResultSet rs2 = stmt.executeQuery("SELECT * FROM sessions")) {
                while (rs2.next()) {
                    out.println("ID: " + rs2.getString("session_id") +
                            " | Type: " + rs2.getString("session_type") +
                            " | Loc: " + rs2.getString("location") +
                            " | EvalID: " + rs2.getString("evaluator_id"));
                }
            }

            out.println("\n--- Session Students Junction ---");
            try (ResultSet rs3 = stmt.executeQuery(
                    "SELECT ss.session_id, u.username FROM session_students ss JOIN users u ON ss.student_id = u.user_id")) {
                while (rs3.next()) {
                    out.println("SessionID: " + rs3.getString("session_id") +
                            " | Student: " + rs3.getString("username"));
                }
            }

            out.println("--- End of List ---");
            System.out.println("Debug info written to db_debug.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
