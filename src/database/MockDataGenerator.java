package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class MockDataGenerator {

    public static void insertMockData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
             
            String adminId = UUID.randomUUID().toString();
            if (createUser(conn, adminId, "admin", "password", "coordinator")) {
                System.out.println("Created coordinator 'admin'");
            } else {
                adminId = getUserId(conn, "admin");
            }

             
            String eval1Id = UUID.randomUUID().toString();
            String eval2Id = UUID.randomUUID().toString();
            createUser(conn, eval1Id, "eval1", "password", "evaluator");
            createUser(conn, eval2Id, "eval2", "password", "evaluator");
            eval1Id = getUserId(conn, "eval1");
            eval2Id = getUserId(conn, "eval2");

             
            String stud1Id = UUID.randomUUID().toString();
            String stud2Id = UUID.randomUUID().toString();
            String stud3Id = UUID.randomUUID().toString();
            createUser(conn, stud1Id, "stud1", "password", "student");
            createUser(conn, stud2Id, "stud2", "password", "student");
            createUser(conn, stud3Id, "stud3", "password", "student");
            String stud4Id = UUID.randomUUID().toString();
            String stud5Id = UUID.randomUUID().toString();
            createUser(conn, stud4Id, "stud4", "password", "student");
            createUser(conn, stud5Id, "stud5", "password", "student");
            stud1Id = getUserId(conn, "stud1");
            stud2Id = getUserId(conn, "stud2");
            stud3Id = getUserId(conn, "stud3");
            stud4Id = getUserId(conn, "stud4");
            stud5Id = getUserId(conn, "stud5");

             
            System.out.println("Base users maintained. Other mock data skipped for testing.");

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

    private static void createProfile(Connection conn, String userId, String name, String email, String major)
            throws SQLException {
        String sql = "INSERT INTO student_profiles (user_id, full_name, email, major) VALUES (?::uuid, ?, ?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setString(4, major);
            stmt.executeUpdate();
        }
    }

    private static void createSeminar(Connection conn, String id, String loc, Timestamp t, int semester, int year)
            throws SQLException {
        String sql = "INSERT INTO Seminars (seminar_id, location, seminar_date, semester, year) VALUES (?::uuid, ?, ?, ?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, loc);
            stmt.setTimestamp(3, t);
            stmt.setInt(4, semester);
            stmt.setInt(5, year);
            stmt.executeUpdate();
        }
    }

    private static void createSession(Connection conn, String id, String semId, String loc, Timestamp t, String type)
            throws SQLException {
        String sql = "INSERT INTO sessions (session_id, seminar_id, location, session_date, session_type) VALUES (?::uuid, ?::uuid, ?, ?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, semId);
            stmt.setString(3, loc);
            stmt.setTimestamp(4, t);
            stmt.setString(5, type);
            stmt.executeUpdate();
        }
    }

    private static void addStudentToSession(Connection conn, String sessionId, String studentId) throws SQLException {
        String sql = "INSERT INTO session_students (session_id, student_id) VALUES (?::uuid, ?::uuid) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.setString(2, studentId);
            stmt.executeUpdate();
        }
    }

    private static void createSubmission(Connection conn, String subId, String semId, String studId, String title,
            String abstractText, String supervisor, String type, String status) throws SQLException {
        String sql = "INSERT INTO submissions (submission_id, seminar_id, student_id, title, abstract_text, supervisor, presentation_type, status) VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subId);
            stmt.setString(2, semId);
            stmt.setString(3, studId);
            stmt.setString(4, title);
            stmt.setString(5, abstractText);
            stmt.setString(6, supervisor);
            stmt.setString(7, type);
            stmt.setString(8, status);
            stmt.executeUpdate();
        }
    }

    private static void assignEvaluator(Connection conn, String evalId, String subId) throws SQLException {
        String sql = "INSERT INTO evaluator_assignments (evaluator_id, submission_id) VALUES (?::uuid, ?::uuid) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, evalId);
            stmt.setString(2, subId);
            stmt.executeUpdate();
        }
    }

    private static void createEvaluation(Connection conn, String subId, String evalId, int overall, String comments,
            int pc, int meth, int res, int pres) throws SQLException {
        String sql = "INSERT INTO evaluations (submission_id, evaluator_id, overall_score, comments, problem_clarity, methodology, results, presentation) VALUES (?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subId);
            stmt.setString(2, evalId);
            stmt.setInt(3, overall);
            stmt.setString(4, comments);
            stmt.setInt(5, pc);
            stmt.setInt(6, meth);
            stmt.setInt(7, res);
            stmt.setInt(8, pres);
            stmt.executeUpdate();
        }
    }

    private static int createBoard(Connection conn, String name, String loc, int max, int curr) throws SQLException {
        String sql = "INSERT INTO presentation_boards (board_name, location, max_presentations, current_presentations) VALUES (?, ?, ?, ?) RETURNING board_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, loc);
            stmt.setInt(3, max);
            stmt.setInt(4, curr);
            var rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        }
        return 0;
    }

    private static int createPosterPresentation(Connection conn, int boardId, String subId, String title, String desc,
            String sessId, String status) throws SQLException {
        String sql = "INSERT INTO poster_presentations (board_id, submission_id, title, description, session_id, status) VALUES (?, ?::uuid, ?, ?, ?::uuid, ?) RETURNING presentation_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, boardId);
            stmt.setString(2, subId);
            stmt.setString(3, title);
            stmt.setString(4, desc);
            stmt.setString(5, sessId);
            stmt.setString(6, status);
            var rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        }
        return 0;
    }

    private static void createCriteria(Connection conn, int presId, String name, String desc, int max, int weight)
            throws SQLException {
        String sql = "INSERT INTO evaluation_criteria (presentation_id, criteria_name, description, max_score, weight) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, presId);
            stmt.setString(2, name);
            stmt.setString(3, desc);
            stmt.setInt(4, max);
            stmt.setInt(5, weight);
            stmt.executeUpdate();
        }
    }
}
