import java.sql.*;
import database.DatabaseConnection;

public class ListSubmissions {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT s.*, u.username FROM submissions s JOIN users u ON s.student_id = u.user_id")) {
            System.out.println("--- Submissions in DB ---");
            while (rs.next()) {
                System.out.print("ID: " + rs.getString("submission_id"));
                System.out.print(" | Student: " + rs.getString("username"));
                System.out.print(" | Type: [" + rs.getString("presentation_type") + "]");
                System.out.println(" | Title: " + rs.getString("title"));
            }

            try (ResultSet rs2 = stmt.executeQuery("SELECT * FROM sessions")) {
                System.out.println("--- Sessions in DB ---");
                while (rs2.next()) {
                    System.out.println("ID: " + rs2.getString("session_id") +
                            " | Type: " + rs2.getString("session_type") +
                            " | Loc: " + rs2.getString("location"));
                }
            }
            System.out.println("--- End of List ---");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
