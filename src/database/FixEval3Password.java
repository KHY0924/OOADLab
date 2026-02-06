package database;

import java.sql.*;

public class FixEval3Password {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check current passwords
            System.out.println("--- Current Users ---");
            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT username, password, role FROM users ORDER BY role, username");
            ResultSet rs = checkStmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("username") + " | " +
                        rs.getString("password") + " | " + rs.getString("role"));
            }

            // Update eval3 password to match eval1/eval2
            PreparedStatement getEval1 = conn.prepareStatement(
                    "SELECT password FROM users WHERE username = 'eval1'");
            ResultSet rsEval1 = getEval1.executeQuery();
            if (rsEval1.next()) {
                String correctPassword = rsEval1.getString("password");
                PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE users SET password = ? WHERE username = 'eval3'");
                updateStmt.setString(1, correctPassword);
                updateStmt.executeUpdate();
                System.out.println("\nUpdated eval3 password to: " + correctPassword);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
