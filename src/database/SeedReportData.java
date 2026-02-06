package database;

import java.sql.*;

public class SeedReportData {
    public static void main(String[] args) {
        String targetSeminarId = "5122296d-38b5-475e-abf4-c19562a3ab2f";

        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Seeding data for seminar: " + targetSeminarId);

            // Create 2 sessions for this seminar
            System.out.println("Creating sessions...");

            PreparedStatement insertSession = conn.prepareStatement(
                    "INSERT INTO sessions (seminar_id, location, session_date) VALUES (?::uuid, ?, ?) RETURNING session_id");

            insertSession.setString(1, targetSeminarId);
            insertSession.setString(2, "Main Hall A");
            insertSession.setTimestamp(3, new Timestamp(System.currentTimeMillis() + 86400000));
            ResultSet rs = insertSession.executeQuery();
            String sessionId1 = rs.next() ? rs.getString(1) : null;
            System.out.println("  Created Session 1: " + sessionId1);

            insertSession.setString(1, targetSeminarId);
            insertSession.setString(2, "Poster Area B");
            insertSession.setTimestamp(3, new Timestamp(System.currentTimeMillis() + 172800000));
            rs = insertSession.executeQuery();
            String sessionId2 = rs.next() ? rs.getString(1) : null;
            System.out.println("  Created Session 2: " + sessionId2);

            // Get evaluator and students
            String evaluatorId = null;
            rs = conn.createStatement().executeQuery("SELECT user_id FROM users WHERE role = 'evaluator' LIMIT 1");
            if (rs.next())
                evaluatorId = rs.getString(1);
            System.out.println("Evaluator: " + evaluatorId);

            // Get students with submissions for this seminar OR create sample submissions
            rs = conn.createStatement().executeQuery(
                    "SELECT s.student_id, s.title FROM submissions s WHERE s.seminar_id = '" + targetSeminarId + "'");

            int count = 0;
            PreparedStatement assignStudent = conn.prepareStatement(
                    "INSERT INTO session_students (session_id, student_id, evaluator_id) VALUES (?::uuid, ?::uuid, ?::uuid)");

            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String title = rs.getString("title");

                try {
                    assignStudent.setString(1, count % 2 == 0 ? sessionId1 : sessionId2);
                    assignStudent.setString(2, studentId);
                    assignStudent.setString(3, evaluatorId);
                    assignStudent.executeUpdate();
                    System.out.println("  Assigned: " + title);
                    count++;
                } catch (Exception e) {
                    System.out.println("  Skip (already assigned): " + title);
                }
            }

            if (count == 0) {
                System.out.println("No submissions found. Creating sample ones...");

                rs = conn.createStatement().executeQuery("SELECT user_id FROM users WHERE role = 'student' LIMIT 3");
                int i = 1;
                while (rs.next()) {
                    String studentId = rs.getString(1);

                    // Create submission
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO submissions (seminar_id, student_id, title, abstract_text, supervisor, presentation_type) "
                                    +
                                    "VALUES (?::uuid, ?::uuid, ?, ?, ?, ?)");
                    ps.setString(1, targetSeminarId);
                    ps.setString(2, studentId);
                    ps.setString(3, "Sample Project " + i);
                    ps.setString(4, "Abstract for sample project " + i);
                    ps.setString(5, "Dr. Supervisor");
                    ps.setString(6, i % 2 == 0 ? "Poster Presentation" : "Oral Presentation");
                    ps.executeUpdate();

                    // Assign to session
                    assignStudent.setString(1, i % 2 == 0 ? sessionId2 : sessionId1);
                    assignStudent.setString(2, studentId);
                    assignStudent.setString(3, evaluatorId);
                    assignStudent.executeUpdate();

                    System.out.println("  Created and assigned: Sample Project " + i);
                    i++;
                }
            }

            System.out.println("\nDone! Restart the app and try 'Seminar Schedule' again.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
