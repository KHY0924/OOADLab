package database;

import java.sql.*;

public class AddEval3 {
    public static void main(String[] args) {
        String sql = "INSERT INTO users (username, password, role) VALUES ('eval3', 'password123', 'evaluator') ON CONFLICT (username) DO NOTHING";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Created user: eval3 (evaluator)");
            } else {
                System.out.println("User eval3 already exists.");
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
