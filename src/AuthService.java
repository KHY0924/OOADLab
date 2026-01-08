package backend;

import database.DatabaseConnection;
import java.sql.*;

public class AuthService {

    public Evaluator loginEvaluator(String username, String password) {
        // Use the users table from your friend's SQL
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = 'evaluator'";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // User found!
                String id = rs.getString("user_id");
                String name = rs.getString("username");
                return new Evaluator(id, name, username, password);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Login failed
    }
}