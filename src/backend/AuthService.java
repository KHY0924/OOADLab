import java.sql.*;

public class AuthService {
    // ASK GROUP for these details!
    private String url = "jdbc:postgresql://localhost:5432/your_db_name"; 
    private String dbUser = "postgres";
    private String dbPassword = "password";

    public Evaluator loginEvaluator(String username, String password) {
        // Query the database instead of a fake list
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = 'evaluator'";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // User found in DB! Create the Java object.
                String id = rs.getString("user_id");
                String name = rs.getString("username"); // or full_name from profile if available
                return new Evaluator(id, name, username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Login failed
    }
}
