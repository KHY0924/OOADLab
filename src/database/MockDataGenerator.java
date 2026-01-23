package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class MockDataGenerator {

    public static void insertMockData() {
        String evaluatorId = UUID.randomUUID().toString();
        String studentId = UUID.randomUUID().toString();
        String submissionId = UUID.randomUUID().toString();

        // We need to fetch the IDs if they already exist to link them, but for
        // simplicity in this dev tool we just try insert
        // A better approach for a reusable script is checking existence.
        // For this specific request, let's use a robust approach calling creating if
        // not exists.

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Create Evaluator
            if (createUser(conn, evaluatorId, "harry", "password", "evaluator")) {
                System.out.println("Created evaluator 'harry'");
            } else {
                evaluatorId = getUserId(conn, "harry");
                System.out.println("Using existing evaluator 'harry': " + evaluatorId);
            }

            // 2. Create Student
            if (createUser(conn, studentId, "potter", "password", "student")) {
                System.out.println("Created student 'potter'");
            } else {
                studentId = getUserId(conn, "potter");
                System.out.println("Using existing student 'potter': " + studentId);
            }

            // 3. Create Submission
            createSubmission(conn, submissionId, studentId, "Defense Against the Dark Arts",
                    "A study on defensive spells.");

            // 4. Assign Evaluator
            assignEvaluator(conn, evaluatorId, submissionId);

            System.out.println("Mock data insertion complete.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean createUser(Connection conn, String id, String username, String password, String role)
            throws SQLException {
        String sql = "INSERT INTO users (user_id, username, password, role) VALUES (?::uuid, ?, ?, ?) ON CONFLICT (username) DO NOTHING";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, role);
            return stmt.executeUpdate() > 0;
        }
    }

    private static String getUserId(Connection conn, String username) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            var rs = stmt.executeQuery();
            if (rs.next())
                return rs.getString("user_id");
        }
        return null;
    }

    private static void createSubmission(Connection conn, String subId, String studentId, String title,
            String abstractText) throws SQLException {
        // Delete existing for this student to keep it clean for this test
        String delete = "DELETE FROM submissions WHERE student_id = ?::uuid";
        try (PreparedStatement stmt = conn.prepareStatement(delete)) {
            stmt.setString(1, studentId);
            stmt.executeUpdate();
        }

        String sql = "INSERT INTO submissions (submission_id, seminar_id, student_id, title, abstract_text, supervisor, presentation_type) VALUES (?::uuid, 'SEM-001', ?::uuid, ?, ?, 'Prof. Dumbledore', 'Oral')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subId);
            stmt.setString(2, studentId);
            stmt.setString(3, title);
            stmt.setString(4, abstractText);
            stmt.executeUpdate();
            System.out.println("Created submission for 'potter'");
        }
    }

    private static void assignEvaluator(Connection conn, String evaluatorId, String submissionId) throws SQLException {
        // Clear previous assignments for this sub
        String delete = "DELETE FROM evaluator_assignments WHERE submission_id = ?::uuid";
        try (PreparedStatement stmt = conn.prepareStatement(delete)) {
            stmt.setString(1, submissionId);
            stmt.executeUpdate();
        }

        String sql = "INSERT INTO evaluator_assignments (evaluator_id, submission_id) VALUES (?::uuid, ?::uuid)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, evaluatorId);
            stmt.setString(2, submissionId);
            stmt.executeUpdate();
            System.out.println("Assigned 'harry' to evaluate 'potter'");
        }
    }
}
