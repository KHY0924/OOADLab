package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import database.DatabaseConnection;
import models.Award;
import models.Report;
import models.Submission;
import models.Evaluation;
import services.AwardComputationService;

public class ReportTest {

    public static void main(String[] args) {
        System.out.println("Starting Reports & Summary Module Testing...\n");

        if (!testDatabaseConnection()) {
            System.out.println("[CRITICAL ERROR] Database connection failed. Aborting tests.\n");
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String seminarId = "SEM-2024-REPORTS";

            System.out.println("=== PHASE 1: STUDENT EVALUATION REPORT ===\n");
            testStudentEvaluationReport(conn, seminarId);

            System.out.println("\n=== PHASE 2: SEMINAR SUMMARY REPORT ===\n");
            testSeminarSummaryReport(conn, seminarId);

            System.out.println("\n=== PHASE 3: REPORT FILE EXPORT ===\n");
            testReportExport(conn, seminarId);

            System.out.println("\n--- Reports & Summary Module Testing Completed ---\n");

        } catch (SQLException e) {
            System.out.println("[DATABASE ERROR] " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("[CRITICAL ERROR] " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("\n[OK] Database connection closed.");
                } catch (SQLException e) {
                    System.out.println("[WARN] Error closing database connection: " + e.getMessage());
                }
            }
        }
    }

    private static boolean testDatabaseConnection() {
        System.out.println("Testing database connection...");
        try {
            Connection conn = DatabaseConnection.getConnection();
            boolean isValid = conn != null && !conn.isClosed();
            if (isValid) {
                System.out.println("   [SUCCESS] Database connection established.\n");
                conn.close();
                return true;
            } else {
                System.out.println("   [FAILURE] Database connection is not valid.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("   [FAILURE] " + e.getMessage());
            return false;
        }
    }

    private static void testStudentEvaluationReport(Connection conn, String seminarId) {
        System.out.println("1.1 Creating mock student with profile...");
        
        String studentId = createMockUserWithProfile(conn, "eval_student_" + System.currentTimeMillis(),
            "John Doe", "john.doe@university.edu", "Computer Science");
        if (studentId == null) {
            System.out.println("   [FAILURE] Could not create student user.");
            return;
        }
        System.out.println("   [OK] Student created: " + studentId);

        String studentName = getStudentNameFromProfile(conn, studentId);
        if (studentName == null) studentName = "John Doe";

        System.out.println("\n1.2 Creating mock evaluation scores...");
        Map<String, Integer> rubricScores = new HashMap<>();
        rubricScores.put("Problem Clarity", 85);
        rubricScores.put("Methodology", 90);
        rubricScores.put("Results", 88);
        rubricScores.put("Presentation", 92);
        
        double finalAverage = (85 + 90 + 88 + 92) / 4.0;
        System.out.println("   [OK] Rubric scores created. Average: " + String.format("%.2f", finalAverage));

        System.out.println("\n1.3 Creating evaluator comments...");
        List<String> comments = new ArrayList<>();
        comments.add("Excellent presentation with clear explanations.");
        comments.add("Good use of visuals and data representation.");
        comments.add("Strong conclusion and future work suggestions.");
        System.out.println("   [OK] " + comments.size() + " comments added.");

        System.out.println("\n1.4 Generating Student Evaluation Report...");
        Report.StudentEvaluationReport studentReport = new Report.StudentEvaluationReport(
                studentId,
                studentName,
                "SESSION-001",
                "Spring 2024 Seminar Session 1",
                rubricScores,
                finalAverage,
                comments
        );

        if (studentReport != null && studentReport.getContent() != null) {
            System.out.println("   [SUCCESS] Student Evaluation Report generated.");
            System.out.println("   - Report ID: " + studentReport.getReportId());
            System.out.println("   - Report Type: " + studentReport.getReportType());
            System.out.println("   - Student Name: " + studentReport.getStudentName());
            System.out.println("   - Final Average: " + String.format("%.2f", studentReport.getFinalAverage()));
            printReportPreview(studentReport.getContent());
        } else {
            System.out.println("   [FAILURE] Report generation failed.");
        }
    }

    private static void testSeminarSummaryReport(Connection conn, String seminarId) {
        System.out.println("2.1 Creating mock oral submissions and evaluations...");
        
        List<Submission> oralSubmissions = createMockOralSubmissionsWithProfiles(conn, seminarId);
        List<Submission> posterSubmissions = createMockPosterSubmissionsWithProfiles(conn, seminarId);
        List<Submission> allSubmissions = mergeLists(oralSubmissions, posterSubmissions);
        List<Evaluation> evaluations = createMockEvaluations(conn, allSubmissions);
        Map<String, Integer> peopleVotes = createMockPeopleVotes(allSubmissions);

        if (allSubmissions.isEmpty() || evaluations.isEmpty()) {
            System.out.println("   [FAILURE] Could not create mock data.");
            return;
        }
        System.out.println("   [OK] Created " + oralSubmissions.size() + " oral submissions.");
        System.out.println("   [OK] Created " + posterSubmissions.size() + " poster submissions.");
        System.out.println("   [OK] Created " + evaluations.size() + " evaluations.");

        System.out.println("\n2.2 Computing actual awards...");
        AwardComputationService awardService = new AwardComputationService();
        
        Award bestOral = awardService.computeBestOralPresentation(oralSubmissions, evaluations);
        Award bestPoster = awardService.computeBestPosterPresentation(posterSubmissions, evaluations);
        Award peoplesChoice = awardService.computePeoplesChoice(allSubmissions, peopleVotes);

        List<Award> awardWinners = new ArrayList<>();
        if (bestOral != null) {
            awardWinners.add(bestOral);
            System.out.println("   [OK] Best Oral Award: " + bestOral.getStudentName());
        }
        if (bestPoster != null) {
            awardWinners.add(bestPoster);
            System.out.println("   [OK] Best Poster Award: " + bestPoster.getStudentName());
        }
        if (peoplesChoice != null) {
            awardWinners.add(peoplesChoice);
            System.out.println("   [OK] People's Choice Award: " + peoplesChoice.getStudentName());
        }

        System.out.println("\n2.3 Calculating overall statistics...");
        int totalStudents = 25;
        int totalSessions = 4;
        double overallAverage = 89.75;
        System.out.println("   [OK] Total Students: " + totalStudents);
        System.out.println("   [OK] Total Sessions: " + totalSessions);
        System.out.println("   [OK] Overall Average: " + String.format("%.2f", overallAverage));

        System.out.println("\n2.4 Generating Seminar Summary Report...");
        Report.SeminarSummaryReport seminarReport = new Report.SeminarSummaryReport(
                seminarId,
                "Spring 2024 Research Seminar",
                totalStudents,
                totalSessions,
                overallAverage,
                awardWinners
        );

        if (seminarReport != null && seminarReport.getContent() != null) {
            System.out.println("   [SUCCESS] Seminar Summary Report generated.");
            System.out.println("   - Report ID: " + seminarReport.getReportId());
            System.out.println("   - Seminar Title: " + seminarReport.getSeminarTitle());
            System.out.println("   - Total Students: " + seminarReport.getTotalStudents());
            System.out.println("   - Total Sessions: " + seminarReport.getTotalSessions());
            System.out.println("   - Overall Average: " + String.format("%.2f", seminarReport.getOverallAverageScore()));
            printReportPreview(seminarReport.getContent());
        } else {
            System.out.println("   [FAILURE] Report generation failed.");
        }
    }

    private static void testReportExport(Connection conn, String seminarId) {
        System.out.println("3.1 Creating sample Student Evaluation Report for export...");
        
        String exportStudentId = createMockUserWithProfile(conn, "export_test_" + System.currentTimeMillis(),
            "Export Test Student", "export@university.edu", "Engineering");
        String exportStudentName = getStudentNameFromProfile(conn, exportStudentId);
        if (exportStudentName == null) exportStudentName = "Export Test Student";

        Map<String, Integer> rubricScores = new HashMap<>();
        rubricScores.put("Problem Clarity", 85);
        rubricScores.put("Methodology", 90);
        
        List<String> comments = new ArrayList<>();
        comments.add("Test comment for export.");
        
        Report.StudentEvaluationReport studentReport = new Report.StudentEvaluationReport(
                exportStudentId,
                exportStudentName,
                "SESSION-001",
                "Test Session",
                rubricScores,
                87.5,
                comments
        );

        System.out.println("   [OK] Student Evaluation Report created: " + studentReport.getReportId());

        System.out.println("\n3.2 Exporting Student Evaluation Report to file...");
        try {
            studentReport.exportToFile();
            System.out.println("   [SUCCESS] Report exported to logs/Report_STUDENT_EVALUATION_*.txt");
        } catch (Exception e) {
            System.out.println("   [FAILURE] Export failed: " + e.getMessage());
        }

        System.out.println("\n3.3 Creating and exporting Seminar Summary Report with computed awards...");
        List<Submission> oralSubmissions = createMockOralSubmissionsWithProfiles(conn, seminarId);
        List<Submission> posterSubmissions = createMockPosterSubmissionsWithProfiles(conn, seminarId);
        List<Submission> allSubmissions = mergeLists(oralSubmissions, posterSubmissions);
        List<Evaluation> evaluations = createMockEvaluations(conn, allSubmissions);
        Map<String, Integer> peopleVotes = createMockPeopleVotes(allSubmissions);

        AwardComputationService awardService = new AwardComputationService();
        Award bestOral = awardService.computeBestOralPresentation(oralSubmissions, evaluations);
        Award bestPoster = awardService.computeBestPosterPresentation(posterSubmissions, evaluations);
        Award peoplesChoice = awardService.computePeoplesChoice(allSubmissions, peopleVotes);

        List<Award> awards = new ArrayList<>();
        if (bestOral != null) awards.add(bestOral);
        if (bestPoster != null) awards.add(bestPoster);
        if (peoplesChoice != null) awards.add(peoplesChoice);

        Report.SeminarSummaryReport seminarReport = new Report.SeminarSummaryReport(
                seminarId,
                "Test Seminar",
                allSubmissions.size(),
                2,
                88.0,
                awards
        );

        try {
            seminarReport.exportToFile();
            System.out.println("   [SUCCESS] Report exported to logs/Report_SEMINAR_SUMMARY_*.txt");
        } catch (Exception e) {
            System.out.println("   [FAILURE] Export failed: " + e.getMessage());
        }

        System.out.println("\n3.4 Verifying all reports have content...");
        if (studentReport.getContent() != null && !studentReport.getContent().isEmpty()) {
            System.out.println("   [OK] Student Report has content (" + studentReport.getContent().length() + " chars)");
        }
        if (seminarReport.getContent() != null && !seminarReport.getContent().isEmpty()) {
            System.out.println("   [OK] Seminar Report has content (" + seminarReport.getContent().length() + " chars)");
        }
    }

    private static String createMockUser(Connection conn, String username) {
        try {
            String userId = UUID.randomUUID().toString();
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (user_id, username, password, role) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT DO NOTHING RETURNING user_id"
            );
            stmt.setObject(1, java.util.UUID.fromString(userId));
            stmt.setString(2, username);
            stmt.setString(3, "password123");
            stmt.setString(4, "student");
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userId = rs.getString("user_id");
            }
            stmt.close();
            return userId;
        } catch (SQLException e) {
            System.out.println("   [WARN] Error creating mock user: " + e.getMessage());
            return null;
        }
    }

    private static String createMockUserWithProfile(Connection conn, String username, 
                                                    String fullName, String email, String major) {
        try {
            String userId = UUID.randomUUID().toString();
            
            PreparedStatement userStmt = conn.prepareStatement(
                "INSERT INTO users (user_id, username, password, role) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT DO NOTHING RETURNING user_id"
            );
            userStmt.setObject(1, java.util.UUID.fromString(userId));
            userStmt.setString(2, username);
            userStmt.setString(3, "password123");
            userStmt.setString(4, "student");
            
            ResultSet rs = userStmt.executeQuery();
            if (rs.next()) {
                userId = rs.getString("user_id");
            }
            userStmt.close();

            if (userId == null || userId.isEmpty()) {
                System.out.println("   [WARN] Failed to get user ID after insertion.");
                return null;
            }

            PreparedStatement profileStmt = conn.prepareStatement(
                "INSERT INTO student_profiles (user_id, full_name, email, major) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT DO NOTHING"
            );
            profileStmt.setObject(1, java.util.UUID.fromString(userId));
            profileStmt.setString(2, fullName);
            profileStmt.setString(3, email);
            profileStmt.setString(4, major);
            profileStmt.executeUpdate();
            profileStmt.close();

            return userId;
        } catch (SQLException e) {
            System.out.println("   [WARN] Error creating mock user with profile: " + e.getMessage());
            return null;
        }
    }

    private static String getStudentNameFromProfile(Connection conn, String userId) {
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT full_name FROM student_profiles WHERE user_id = ?"
            );
            stmt.setObject(1, java.util.UUID.fromString(userId));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("full_name");
                stmt.close();
                return name;
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("   [WARN] Error retrieving student profile: " + e.getMessage());
        }
        return null;
    }

    private static List<Submission> createMockOralSubmissionsWithProfiles(Connection conn, String seminarId) {
        List<Submission> submissions = new ArrayList<>();
        String[] oralFirstNames = {"Emma", "Liam", "Olivia"};
        String[] oralLastNames = {"Wilson", "Anderson", "Taylor"};
        String[] majors = {"Computer Science", "Engineering", "Physics"};

        try {
            for (int i = 1; i <= 3; i++) {
                String fullName = oralFirstNames[i - 1] + " " + oralLastNames[i - 1];
                String email = (oralFirstNames[i - 1].toLowerCase() + "." + oralLastNames[i - 1].toLowerCase() 
                    + "@university.edu");
                String major = majors[i - 1];

                String username = "oral_student_" + i + "_" + System.currentTimeMillis();
                String studentId = createMockUserWithProfile(conn, username, fullName, email, major);
                
                if (studentId == null) continue;

                String submissionId = UUID.randomUUID().toString();
                String title = "Oral Presentation " + i;
                String abstractText = "Abstract for oral presentation " + i;

                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO submissions (submission_id, student_id, seminar_id, title, abstract_text, " +
                    "supervisor, presentation_type, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT DO NOTHING"
                );
                stmt.setObject(1, java.util.UUID.fromString(submissionId));
                stmt.setObject(2, java.util.UUID.fromString(studentId));
                stmt.setString(3, seminarId);
                stmt.setString(4, title);
                stmt.setString(5, abstractText);
                stmt.setString(6, "Dr. Supervisor " + i);
                stmt.setString(7, "Oral");
                stmt.setString(8, "SUBMITTED");
                stmt.executeUpdate();
                stmt.close();

                Submission submission = new Submission(submissionId, seminarId, studentId, 
                    title, abstractText, "Dr. Supervisor " + i, "Oral");
                submission.setStudentName(fullName);
                submissions.add(submission);
            }
        } catch (SQLException e) {
            System.out.println("   [WARN] Error creating mock oral submissions: " + e.getMessage());
        }
        return submissions;
    }

    private static List<Submission> createMockPosterSubmissionsWithProfiles(Connection conn, String seminarId) {
        List<Submission> submissions = new ArrayList<>();
        String[] posterFirstNames = {"Sophia", "Noah", "Ava"};
        String[] posterLastNames = {"Garcia", "Martinez", "Rodriguez"};
        String[] majors = {"Biology", "Chemistry", "Mathematics"};

        try {
            for (int i = 1; i <= 3; i++) {
                String fullName = posterFirstNames[i - 1] + " " + posterLastNames[i - 1];
                String email = (posterFirstNames[i - 1].toLowerCase() + "." + posterLastNames[i - 1].toLowerCase() 
                    + "@university.edu");
                String major = majors[i - 1];

                String username = "poster_student_" + i + "_" + System.currentTimeMillis();
                String studentId = createMockUserWithProfile(conn, username, fullName, email, major);
                
                if (studentId == null) continue;

                String submissionId = UUID.randomUUID().toString();
                String title = "Poster Presentation " + i;
                String abstractText = "Abstract for poster presentation " + i;

                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO submissions (submission_id, student_id, seminar_id, title, abstract_text, " +
                    "supervisor, presentation_type, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT DO NOTHING"
                );
                stmt.setObject(1, java.util.UUID.fromString(submissionId));
                stmt.setObject(2, java.util.UUID.fromString(studentId));
                stmt.setString(3, seminarId);
                stmt.setString(4, title);
                stmt.setString(5, abstractText);
                stmt.setString(6, "Dr. Supervisor " + i);
                stmt.setString(7, "Poster");
                stmt.setString(8, "SUBMITTED");
                stmt.executeUpdate();
                stmt.close();

                Submission submission = new Submission(submissionId, seminarId, studentId,
                    title, abstractText, "Dr. Supervisor " + i, "Poster");
                submission.setStudentName(fullName);
                submissions.add(submission);
            }
        } catch (SQLException e) {
            System.out.println("   [WARN] Error creating mock poster submissions: " + e.getMessage());
        }
        return submissions;
    }

    private static List<Evaluation> createMockEvaluations(Connection conn, List<Submission> submissions) {
        List<Evaluation> evaluations = new ArrayList<>();
        try {
            for (Submission submission : submissions) {
                for (int evaluatorNum = 1; evaluatorNum <= 2; evaluatorNum++) {
                    String evalUsername = "evaluator_" + submission.getId().substring(0, 8) + 
                                         "_" + evaluatorNum + "_" + System.currentTimeMillis();
                    String evaluatorId = createMockUserWithProfile(conn, evalUsername,
                        "Dr. Evaluator " + evaluatorNum, "evaluator" + evaluatorNum + "@university.edu", "Faculty");
                    
                    if (evaluatorId == null) continue;

                    String evaluationId = UUID.randomUUID().toString();
                    int score = 75 + (int)(Math.random() * 25);

                    PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO evaluations (evaluation_id, submission_id, evaluator_id, " +
                        "originality, clarity, content, overall_score, comments) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING"
                    );
                    stmt.setObject(1, java.util.UUID.fromString(evaluationId));
                    stmt.setObject(2, java.util.UUID.fromString(submission.getId()));
                    stmt.setObject(3, java.util.UUID.fromString(evaluatorId));
                    stmt.setInt(4, score - 10);
                    stmt.setInt(5, score - 5);
                    stmt.setInt(6, score);
                    stmt.setInt(7, score);
                    stmt.setString(8, "Mock evaluation for testing");
                    stmt.executeUpdate();
                    stmt.close();

                    Evaluation evaluation = new Evaluation(evaluationId, submission.getId(), 
                        evaluatorId, score, submission.getPresentationType());
                    evaluations.add(evaluation);
                }
            }
        } catch (SQLException e) {
            System.out.println("   [WARN] Error creating mock evaluations: " + e.getMessage());
        }
        return evaluations;
    }

    private static Map<String, Integer> createMockPeopleVotes(List<Submission> submissions) {
        Map<String, Integer> votes = new HashMap<>();
        if (!submissions.isEmpty()) {
            votes.put(submissions.get(0).getId(), 150);
            if (submissions.size() > 1) {
                votes.put(submissions.get(1).getId(), 100);
            }
            if (submissions.size() > 2) {
                votes.put(submissions.get(2).getId(), 75);
            }
        }
        return votes;
    }

    private static <T> List<T> mergeLists(List<T> list1, List<T> list2) {
        List<T> merged = new ArrayList<>();
        merged.addAll(list1);
        merged.addAll(list2);
        return merged;
    }

    private static void printReportPreview(String content) {
        System.out.println("\n--- REPORT PREVIEW ---");
        String[] lines = content.split("\n");
        for (int i = 0; i < Math.min(10, lines.length); i++) {
            System.out.println(lines[i]);
        }
        if (lines.length > 10) {
            System.out.println("...");
        }
        System.out.println("--- END PREVIEW ---\n");
    }
}