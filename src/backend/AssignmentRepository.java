import java.sql.*;
import java.util.*;

public class AssignmentRepository {
    private String url = "jdbc:postgresql://localhost:5432/your_db_name"; 
    private String dbUser = "postgres";
    private String dbPassword = "password";

    public List<Submission> getAssignmentsForEvaluator(String evaluatorId) {
        List<Submission> list = new ArrayList<>();
        // Join the tables to find what this evaluator needs to grade
        String sql = "SELECT s.* FROM submissions s " +
                     "JOIN evaluator_assignments ea ON s.submission_id = ea.submission_id " +
                     "WHERE ea.evaluator_id = ?::uuid"; 
                     // '::uuid' casts the string to UUID for PostgreSQL

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, evaluatorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String subId = rs.getString("submission_id");
                String title = rs.getString("title");
                // Create submission object with data from DB
                Submission s = new Submission(subId, title);
                // (Optional) Populate other fields like abstract, etc.
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
