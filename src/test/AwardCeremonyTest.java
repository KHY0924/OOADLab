package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import database.DatabaseConnection;
import models.Award;
import models.Ceremony;
import models.Evaluation;
import models.Submission;
import services.AwardComputationService;
import services.CeremonyService;

public class AwardCeremonyTest {

    public static void main(String[] args) {
        System.out.println("Starting Award & Ceremony Module Testing...\n");

         
        if (!testDatabaseConnection()) {
            System.out.println("[CRITICAL ERROR] Database connection failed. Aborting tests.\n");
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
             
            CeremonyService ceremonyService = new CeremonyService();
            AwardComputationService awardComputationService = new AwardComputationService();

            String seminarId = "SEM-2024";

            System.out.println("=== PHASE 1: CEREMONY CREATION ===\n");
            testCeremonyCreation(ceremonyService, seminarId);

            System.out.println("\n=== PHASE 2: AWARD COMPUTATION (ORAL PRESENTATION) ===\n");
            testBestOralComputation(conn, awardComputationService, seminarId);

            System.out.println("\n=== PHASE 3: AWARD COMPUTATION (POSTER PRESENTATION) ===\n");
            testBestPosterComputation(conn, awardComputationService, seminarId);

            System.out.println("\n=== PHASE 4: AWARD COMPUTATION (PEOPLE'S CHOICE) ===\n");
            testPeoplesChoiceComputation(conn, awardComputationService, seminarId);

            System.out.println("\n=== PHASE 5: AWARD ASSIGNMENT TO CEREMONY ===\n");
            testAwardAssignmentToCeremony(ceremonyService, awardComputationService, 
                                         conn, seminarId);

            System.out.println("\n=== PHASE 6: CEREMONY LIFECYCLE MANAGEMENT ===\n");
            testCeremonyLifecycle(ceremonyService, seminarId);

            System.out.println("\n=== PHASE 7: AWARD REMOVAL AND VALIDATION ===\n");
            testAwardRemovalAndValidation(ceremonyService, awardComputationService,
                                         conn, seminarId);

            System.out.println("\n--- Award & Ceremony Module Testing Completed ---\n");

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

     
    private static void testCeremonyCreation(CeremonyService ceremonyService, 
                                            String seminarId) {
        System.out.println("1.1 Creating new ceremony...");
        LocalDateTime ceremonyDateTime = LocalDateTime.of(2024, 12, 15, 14, 0);
        Ceremony ceremony = ceremonyService.createCeremony(
                seminarId,
                "End-of-Seminar Awards Ceremony",
                ceremonyDateTime,
                "Auditorium Hall A"
        );

        if (ceremony != null && ceremony.getCeremonyId() != null) {
            System.out.println("   [SUCCESS] Ceremony created. ID: " + ceremony.getCeremonyId());
            System.out.println("   - Name: " + ceremony.getCeremonyName());
            System.out.println("   - Status: " + ceremony.getCeremonyStatus());
            System.out.println("   - Venue: " + ceremony.getVenue());
        } else {
            System.out.println("   [FAILURE] Ceremony creation failed.");
            return;
        }

        System.out.println("\n1.2 Retrieving ceremony by ID...");
        Ceremony retrieved = ceremonyService.getCeremonyById(ceremony.getCeremonyId());
        if (retrieved != null && retrieved.getCeremonyId().equals(ceremony.getCeremonyId())) {
            System.out.println("   [SUCCESS] Ceremony retrieved correctly.");
        } else {
            System.out.println("   [FAILURE] Ceremony retrieval failed.");
        }

        System.out.println("\n1.3 Testing ceremony queries by seminar...");
        List<Ceremony> seminarCeremonies = ceremonyService.getCeremoniesBySeminar(seminarId);
        if (!seminarCeremonies.isEmpty()) {
            System.out.println("   [SUCCESS] Found " + seminarCeremonies.size() + " ceremony(ies) for seminar.");
        } else {
            System.out.println("   [FAILURE] No ceremonies found for seminar.");
        }

        System.out.println("\n1.4 Testing initial award count...");
        if (ceremony.getAwardCount() == 0) {
            System.out.println("   [SUCCESS] New ceremony has 0 awards (as expected).");
        } else {
            System.out.println("   [FAILURE] Award count should be 0, got " + ceremony.getAwardCount());
        }
    }

     
    private static void testBestOralComputation(Connection conn,
                                               AwardComputationService awardService,
                                               String seminarId) {
        System.out.println("2.1 Creating mock oral submissions and evaluations...");
        
        List<Submission> oralSubmissions = createMockOralSubmissions(conn, seminarId);
        List<Evaluation> evaluations = createMockEvaluations(conn, oralSubmissions);

        if (oralSubmissions.isEmpty() || evaluations.isEmpty()) {
            System.out.println("   [FAILURE] Could not create mock data.");
            return;
        }
        System.out.println("   [OK] Created " + oralSubmissions.size() + " oral submissions.");
        System.out.println("   [OK] Created " + evaluations.size() + " evaluations.");

        System.out.println("\n2.2 Computing Best Oral Presentation award...");
        Award bestOral = awardService.computeBestOralPresentation(oralSubmissions, evaluations);

        if (bestOral != null) {
            System.out.println("   [SUCCESS] Best Oral award computed.");
            System.out.println("   - Award ID: " + bestOral.getAwardId());
            System.out.println("   - Type: " + bestOral.getAwardType());
            System.out.println("   - Winner: " + bestOral.getStudentName());
            System.out.println("   - Submission: " + bestOral.getSubmissionTitle());
        } else {
            System.out.println("   [FAILURE] Best Oral award computation returned null.");
        }

        System.out.println("\n2.3 Validating award computation with no evaluations...");
        Award invalid = awardService.computeBestOralPresentation(oralSubmissions, new ArrayList<>());
        if (invalid == null) {
            System.out.println("   [SUCCESS] Correctly returns null with empty evaluations.");
        } else {
            System.out.println("   [FAILURE] Should return null with no evaluations.");
        }
    }

     
    private static void testBestPosterComputation(Connection conn,
                                                 AwardComputationService awardService,
                                                 String seminarId) {
        System.out.println("3.1 Creating mock poster submissions and evaluations...");
        
        List<Submission> posterSubmissions = createMockPosterSubmissions(conn, seminarId);
        List<Evaluation> evaluations = createMockEvaluations(conn, posterSubmissions);

        if (posterSubmissions.isEmpty() || evaluations.isEmpty()) {
            System.out.println("   [FAILURE] Could not create mock data.");
            return;
        }
        System.out.println("   [OK] Created " + posterSubmissions.size() + " poster submissions.");
        System.out.println("   [OK] Created " + evaluations.size() + " evaluations.");

        System.out.println("\n3.2 Computing Best Poster Presentation award...");
        Award bestPoster = awardService.computeBestPosterPresentation(posterSubmissions, evaluations);

        if (bestPoster != null) {
            System.out.println("   [SUCCESS] Best Poster award computed.");
            System.out.println("   - Award ID: " + bestPoster.getAwardId());
            System.out.println("   - Type: " + bestPoster.getAwardType());
            System.out.println("   - Winner: " + bestPoster.getStudentName());
            System.out.println("   - Submission: " + bestPoster.getSubmissionTitle());
        } else {
            System.out.println("   [FAILURE] Best Poster award computation returned null.");
        }
    }

     
    private static void testPeoplesChoiceComputation(Connection conn,
                                                    AwardComputationService awardService,
                                                    String seminarId) {
        System.out.println("4.1 Creating mock submissions and votes...");
        
        List<Submission> allSubmissions = new ArrayList<>();
        allSubmissions.addAll(createMockOralSubmissions(conn, seminarId));
        allSubmissions.addAll(createMockPosterSubmissions(conn, seminarId));

         
        Map<String, Integer> peopleVotes = new HashMap<>();
        if (!allSubmissions.isEmpty()) {
             
            peopleVotes.put(allSubmissions.get(0).getId(), 150);
            if (allSubmissions.size() > 1) {
                peopleVotes.put(allSubmissions.get(1).getId(), 100);
            }
            if (allSubmissions.size() > 2) {
                peopleVotes.put(allSubmissions.get(2).getId(), 75);
            }
        }

        if (allSubmissions.isEmpty() || peopleVotes.isEmpty()) {
            System.out.println("   [FAILURE] Could not create mock data.");
            return;
        }
        System.out.println("   [OK] Created " + allSubmissions.size() + " submissions for voting.");
        System.out.println("   [OK] Created " + peopleVotes.size() + " vote entries.");

        System.out.println("\n4.2 Computing People's Choice award...");
        Award peoplesChoice = awardService.computePeoplesChoice(allSubmissions, peopleVotes);

        if (peoplesChoice != null) {
            System.out.println("   [SUCCESS] People's Choice award computed.");
            System.out.println("   - Award ID: " + peoplesChoice.getAwardId());
            System.out.println("   - Type: " + peoplesChoice.getAwardType());
            System.out.println("   - Winner: " + peoplesChoice.getStudentName());
            System.out.println("   - Submission: " + peoplesChoice.getSubmissionTitle());
        } else {
            System.out.println("   [FAILURE] People's Choice award computation returned null.");
        }

        System.out.println("\n4.3 Testing People's Choice with empty votes...");
        Award invalid = awardService.computePeoplesChoice(allSubmissions, new HashMap<>());
        if (invalid == null) {
            System.out.println("   [SUCCESS] Correctly returns null with empty votes.");
        } else {
            System.out.println("   [FAILURE] Should return null with no votes.");
        }
    }

     
    private static void testAwardAssignmentToCeremony(CeremonyService ceremonyService,
                                                     AwardComputationService awardService,
                                                     Connection conn, String seminarId) {
        System.out.println("5.1 Creating ceremony for award assignment...");
        LocalDateTime ceremonyDateTime = LocalDateTime.of(2024, 12, 20, 15, 0);
        Ceremony ceremony = ceremonyService.createCeremony(
                seminarId,
                "Awards Assembly 2024",
                ceremonyDateTime,
                "Main Hall"
        );

        if (ceremony == null) {
            System.out.println("   [FAILURE] Could not create ceremony.");
            return;
        }
        System.out.println("   [SUCCESS] Ceremony created: " + ceremony.getCeremonyId());

        System.out.println("\n5.2 Preparing submissions and evaluations...");
        List<Submission> oralSubmissions = createMockOralSubmissions(conn, seminarId);
        List<Submission> posterSubmissions = createMockPosterSubmissions(conn, seminarId);
        List<Evaluation> evaluations = createMockEvaluations(conn, 
            mergeLists(oralSubmissions, posterSubmissions));
        Map<String, Integer> peopleVotes = createMockPeopleVotes(
            mergeLists(oralSubmissions, posterSubmissions));

        System.out.println("   [OK] Oral submissions: " + oralSubmissions.size());
        System.out.println("   [OK] Poster submissions: " + posterSubmissions.size());
        System.out.println("   [OK] Evaluations: " + evaluations.size());

        System.out.println("\n5.3 Assigning computed awards to ceremony...");
        boolean assigned = ceremonyService.assignAwardsToCeremony(
                ceremony,
                oralSubmissions,
                posterSubmissions,
                evaluations,
                peopleVotes
        );

        if (assigned && ceremony.getAwardCount() > 0) {
            System.out.println("   [SUCCESS] Awards assigned to ceremony.");
            System.out.println("   - Total awards in ceremony: " + ceremony.getAwardCount());
            
            for (Award award : ceremony.getAllAwards()) {
                System.out.println("   - " + award.getAwardType() + ": " + award.getStudentName());
            }
        } else {
            System.out.println("   [FAILURE] Award assignment failed or no awards added.");
        }

        System.out.println("\n5.4 Testing duplicate award prevention...");
        Award duplicateAward = new Award(
                "DUP_" + System.currentTimeMillis(),
                "Best Oral Presentation",
                "student123",
                "Test Student",
                "Duplicate Title",
                seminarId
        );
        
        boolean duplicateAdded = ceremony.addAward(duplicateAward);
        if (!duplicateAdded) {
            System.out.println("   [SUCCESS] Duplicate award type correctly rejected.");
        } else {
            System.out.println("   [FAILURE] Duplicate award should have been rejected.");
        }
    }

     
    private static void testCeremonyLifecycle(CeremonyService ceremonyService,
                                             String seminarId) {
        System.out.println("6.1 Creating ceremony for lifecycle testing...");
        Ceremony ceremony = ceremonyService.createCeremony(
                seminarId,
                "Lifecycle Test Ceremony",
                LocalDateTime.of(2024, 12, 25, 16, 0),
                "Test Venue"
        );

        if (ceremony == null) {
            System.out.println("   [FAILURE] Could not create ceremony.");
            return;
        }
        System.out.println("   [SUCCESS] Ceremony created with status: " + ceremony.getCeremonyStatus());

        System.out.println("\n6.2 Testing status transitions...");
        
         
        Award dummyAward = new Award(
                "AWD_DUMMY_" + System.currentTimeMillis(),
                "Best Oral Presentation",
                "stu_" + System.currentTimeMillis(),
                "Test Winner",
                "Test Submission",
                seminarId
        );
        ceremony.addAward(dummyAward);

        System.out.println("   [OK] Award added, attempting to start ceremony...");
        boolean started = ceremonyService.startCeremony(ceremony.getCeremonyId());
        if (started && "IN_PROGRESS".equals(ceremony.getCeremonyStatus())) {
            System.out.println("   [SUCCESS] Ceremony started. Status: " + ceremony.getCeremonyStatus());
        } else {
            System.out.println("   [FAILURE] Failed to start ceremony.");
        }

        System.out.println("\n6.3 Testing completion...");
        boolean completed = ceremonyService.completeCeremony(ceremony.getCeremonyId());
        if (completed && "COMPLETED".equals(ceremony.getCeremonyStatus())) {
            System.out.println("   [SUCCESS] Ceremony completed. Status: " + ceremony.getCeremonyStatus());
        } else {
            System.out.println("   [FAILURE] Failed to complete ceremony.");
        }

        System.out.println("\n6.4 Testing status-based queries...");
        List<Ceremony> completedCeremonies = ceremonyService.getCeremoniesByStatus("COMPLETED");
        if (completedCeremonies.stream()
                .anyMatch(c -> c.getCeremonyId().equals(ceremony.getCeremonyId()))) {
            System.out.println("   [SUCCESS] Completed ceremony found in status query.");
        } else {
            System.out.println("   [FAILURE] Ceremony not found in completed status query.");
        }
    }

     
    private static void testAwardRemovalAndValidation(CeremonyService ceremonyService,
                                                     AwardComputationService awardService,
                                                     Connection conn, String seminarId) {
        System.out.println("7.1 Creating ceremony with awards for removal testing...");
        Ceremony ceremony = ceremonyService.createCeremony(
                seminarId,
                "Removal Test Ceremony",
                LocalDateTime.of(2024, 12, 28, 17, 0),
                "Test Venue"
        );

        Award award1 = new Award(
                "AWD_TEST_1_" + System.currentTimeMillis(),
                "Best Oral Presentation",
                "stu_1",
                "Student One",
                "Submission One",
                seminarId
        );
        Award award2 = new Award(
                "AWD_TEST_2_" + System.currentTimeMillis(),
                "Best Poster Presentation",
                "stu_2",
                "Student Two",
                "Submission Two",
                seminarId
        );

        ceremony.addAward(award1);
        ceremony.addAward(award2);

        System.out.println("   [OK] Ceremony created with " + ceremony.getAwardCount() + " awards.");

        System.out.println("\n7.2 Testing award removal from PLANNED ceremony...");
        boolean removed = ceremonyService.removeAwardFromCeremony(
                ceremony.getCeremonyId(),
                award1.getAwardId()
        );

        if (removed && ceremony.getAwardCount() == 1) {
            System.out.println("   [SUCCESS] Award removed. Remaining awards: " + ceremony.getAwardCount());
        } else {
            System.out.println("   [FAILURE] Award removal failed.");
        }

        System.out.println("\n7.3 Testing removal from IN_PROGRESS ceremony...");
        Award award3 = new Award(
                "AWD_TEST_3_" + System.currentTimeMillis(),
                "People's Choice",
                "stu_3",
                "Student Three",
                "Submission Three",
                seminarId
        );
        ceremony.addAward(award3);
        ceremony.startCeremony();

        boolean removedAfterStart = ceremonyService.removeAwardFromCeremony(
                ceremony.getCeremonyId(),
                award2.getAwardId()
        );

        if (!removedAfterStart) {
            System.out.println("   [SUCCESS] Correctly prevented award removal from IN_PROGRESS ceremony.");
        } else {
            System.out.println("   [FAILURE] Should not allow removal from IN_PROGRESS ceremony.");
        }

        System.out.println("\n7.4 Testing ceremony validation...");
        boolean isValid = ceremonyService.validateCeremonySetup(ceremony.getCeremonyId());
        if (isValid) {
            System.out.println("   [SUCCESS] Ceremony validation passed.");
            System.out.println("   - Has awards: " + (ceremony.getAwardCount() > 0));
            System.out.println("   - Has scheduled date: " + (ceremony.getScheduledDateTime() != null));
            System.out.println("   - Has venue: " + (ceremony.getVenue() != null));
        } else {
            System.out.println("   [FAILURE] Ceremony validation failed.");
        }

        System.out.println("\n7.5 Testing award computation validation...");
        List<Submission> oralSubmissions = createMockOralSubmissions(conn, seminarId);
        List<Submission> posterSubmissions = createMockPosterSubmissions(conn, seminarId);
        List<Evaluation> evaluations = createMockEvaluations(conn,
            mergeLists(oralSubmissions, posterSubmissions));

        boolean computationValid = awardService.validateAwardComputation(
                oralSubmissions,
                posterSubmissions,
                evaluations
        );

        if (computationValid) {
            System.out.println("   [SUCCESS] Award computation validation passed.");
        } else {
            System.out.println("   [FAILURE] Award computation validation failed.");
        }
    }

     

     
    private static String createMockUser(Connection conn, String username) {
        try {
            String userId = UUID.randomUUID().toString();
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (user_id, username, password, role) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (username) DO UPDATE SET user_id = EXCLUDED.user_id RETURNING user_id"
            );
            stmt.setObject(1, UUID.fromString(userId));
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

    private static List<Submission> createMockOralSubmissions(Connection conn, 
                                                              String seminarId) {
        List<Submission> submissions = new ArrayList<>();
        try {
            for (int i = 1; i <= 3; i++) {
                 
                String username = "oral_student_" + i + "_" + System.currentTimeMillis();
                String studentId = createMockUser(conn, username);
                
                if (studentId == null) {
                    System.out.println("   [WARN] Failed to create user for oral submission " + i);
                    continue;
                }

                String submissionId = UUID.randomUUID().toString();
                String title = "Oral Presentation " + i;
                String abstractText = "Abstract for oral presentation " + i;

                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO submissions (submission_id, student_id, seminar_id, title, abstract_text, " +
                    "supervisor, presentation_type, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT DO NOTHING"
                );
                stmt.setObject(1, UUID.fromString(submissionId));
                stmt.setObject(2, UUID.fromString(studentId));
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
                submission.setStudentName("Oral Student " + i);
                submissions.add(submission);
            }
        } catch (SQLException e) {
            System.out.println("   [WARN] Error creating mock oral submissions: " + e.getMessage());
        }
        return submissions;
    }

    private static List<Submission> createMockPosterSubmissions(Connection conn,
                                                               String seminarId) {
        List<Submission> submissions = new ArrayList<>();
        try {
            for (int i = 1; i <= 3; i++) {
                 
                String username = "poster_student_" + i + "_" + System.currentTimeMillis();
                String studentId = createMockUser(conn, username);
                
                if (studentId == null) {
                    System.out.println("   [WARN] Failed to create user for poster submission " + i);
                    continue;
                }

                String submissionId = UUID.randomUUID().toString();
                String title = "Poster Presentation " + i;
                String abstractText = "Abstract for poster presentation " + i;

                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO submissions (submission_id, student_id, seminar_id, title, abstract_text, " +
                    "supervisor, presentation_type, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT DO NOTHING"
                );
                stmt.setObject(1, UUID.fromString(submissionId));
                stmt.setObject(2, UUID.fromString(studentId));
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
                submission.setStudentName("Poster Student " + i);
                submissions.add(submission);
            }
        } catch (SQLException e) {
            System.out.println("   [WARN] Error creating mock poster submissions: " + e.getMessage());
        }
        return submissions;
    }

    private static List<Evaluation> createMockEvaluations(Connection conn,
                                                         List<Submission> submissions) {
        List<Evaluation> evaluations = new ArrayList<>();
        try {
            for (Submission submission : submissions) {
                for (int evaluatorNum = 1; evaluatorNum <= 2; evaluatorNum++) {
                     
                    String evalUsername = "evaluator_" + submission.getId().substring(0, 8) + 
                                         "_" + evaluatorNum + "_" + System.currentTimeMillis();
                    String evaluatorId = createMockUser(conn, evalUsername);
                    
                    if (evaluatorId == null) {
                        continue;
                    }

                    String evaluationId = UUID.randomUUID().toString();
                    int score = 75 + (int)(Math.random() * 25);  

                    PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO evaluations (evaluation_id, submission_id, evaluator_id, " +
                        "originality, clarity, content, overall_score, comments) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING"
                    );
                    stmt.setObject(1, UUID.fromString(evaluationId));
                    stmt.setObject(2, UUID.fromString(submission.getId()));
                    stmt.setObject(3, UUID.fromString(evaluatorId));
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
}