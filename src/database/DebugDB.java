package database;

import java.sql.*;

public class DebugDB {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            System.out.println("--- USERS TABLE ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("user_id") +
                        ", User: [" + rs.getString("username") + "]" +
                        ", Pass: [" + rs.getString("password") + "]" +
                        ", Role: " + rs.getString("role"));
            }
            System.out.println("-------------------");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
