package test;

import database.DatabaseConnection;
import controllers.UserController;
import controllers.SubmissionController;
import controllers.EvaluationController;
import controllers.SessionController;
import models.Submission;
import database.UserDAO; // For verification helper if needed
import java.sql.*;
import java.util.UUID;
import java.util.List;

public class BackendTest {

    public static void main(String[] args) {
        System.out.println("Starting Full API Verification (Using Controllers)...");

        try {
            Connection conn = DatabaseConnection.getConnection();
            UserController userController = new UserController();
            SubmissionController subController = new SubmissionController();
            EvaluationController evalController = new EvaluationController();
            SessionController sessionController = new SessionController(); // Logic is void but we call it

            // Generate unique names to allow repeated runs
            long timestamp = System.currentTimeMillis();
            String studentUsername = "stud_" + timestamp;
            String evalUsername = "eval_" + timestamp;
            String studentId = null; // Will need to fetch this back from DB
            String evaluatorId = UUID.randomUUID().toString(); // We Manually create Evaluator for now as Controller is
                                                               // student-focused

            System.out.println("\n1. Testing User Registration (API)...");
            // API: Register Student
            userController.registerUser(studentUsername, "password123", "Test Student", "test@uni.edu", "CS");

            // Verification: Fetch ID
            PreparedStatement getStu = conn.prepareStatement("SELECT user_id FROM users WHERE username = ?");
            getStu.setString(1, studentUsername);
            ResultSet rsStu = getStu.executeQuery();
            if (rsStu.next()) {
                studentId = rsStu.getString("user_id");
                System.out.println("   [SUCCESS] Student registered via API. ID: " + studentId);
            } else {
                System.out.println("   [FAILURE] Student not found after registration API call.");
                return; // Stop test
            }

            // Setup: Create Evaluator (Manual, as registerUser API is student-only)
            try {
                PreparedStatement userStmt = conn.prepareStatement(
                        "INSERT INTO users (user_id, username, password, role) VALUES (?, ?, 'pass', 'evaluator')");
                userStmt.setObject(1, UUID.fromString(evaluatorId));
                userStmt.setString(2, evalUsername);
                userStmt.executeUpdate();
                System.out.println("   [OK] Evaluator created manually (API is student-only).");
            } catch (SQLException e) {
                System.out.println("   [WARN] Evaluator creation: " + e.getMessage());
            }

            System.out.println("\n2. Testing Submission APIs...");
            String seminarId = "SEM-2024";

            // API: Register for Seminar
            subController.registerForSeminar(seminarId, studentId, "Initial Title", "Initial Abstract",
                    "Dr. Supervisor", "Oral");

            // Verification: Fetch Submission ID
            String submissionId = null;
            // FIXED: Select * to ensure we can read all columns later
            PreparedStatement getSub = conn.prepareStatement("SELECT * FROM submissions WHERE student_id = ?::uuid");
            getSub.setString(1, studentId);
            ResultSet rsSub = getSub.executeQuery();
            if (rsSub.next()) {
                submissionId = rsSub.getString("submission_id");
                System.out.println("   [SUCCESS] Submission created via API. ID: " + submissionId);
            } else {
                System.out.println("   [FAILURE] Submission not found.");
                return;
            }

            // API: Upload Presentation
            subController.uploadPresentation("C:/slides/my_presentation.pdf", seminarId, studentId);

            // API: Edit Submission
            subController.editSubmission(studentId, "Updated Title", "Updated Abstract");

            // Verification of updates
            ResultSet rsCheck = getSub.executeQuery(); // Re-execute
            if (rsCheck.next()) {
                String path = rsCheck.getString("file_path");
                String title = rsCheck.getString("title");
                if ("C:/slides/my_presentation.pdf".equals(path) && "Updated Title".equals(title)) {
                    System.out.println("   [SUCCESS] Upload and Edit APIs worked (DB updated).");
                } else {
                    System.out.println("   [FAILURE] Update mismatch. Title: " + title + ", Path: " + path);
                }
            }

            System.out.println("\n3. Testing Session Info (API)...");
            sessionController.getSessionInformation(studentId); // Just verification it doesn't crash (void return)
            System.out.println("   [OK] Session API called without error.");

            System.out.println("\n4. Testing Assignment & Evaluation (API)...");

            // Setup: Assign Evaluator (Manual, Admin function)
            try {
                PreparedStatement assignStmt = conn.prepareStatement(
                        "INSERT INTO evaluator_assignments (evaluator_id, submission_id) VALUES (?, ?::uuid)");
                assignStmt.setObject(1, UUID.fromString(evaluatorId));
                assignStmt.setString(2, submissionId);
                assignStmt.executeUpdate();
                System.out.println("   [OK] Assignment created manually.");
            } catch (SQLException e) {
                // Ignore if already exists
            }

            // API: Get Assigned Submissions
            // Needs final string for lambda
            final String targetSubmissionId = submissionId;
            List<Submission> assigned = evalController.getAssignedSubmissions(evaluatorId);
            if (assigned.stream().anyMatch(s -> s.getSubmissionId().equals(targetSubmissionId))) {
                System.out.println("   [SUCCESS] EvaluationController found the assignment.");
            } else {
                System.out.println("   [FAILURE] Assignment not returned by API.");
            }

            // API: Submit Evaluation
            evalController.submitEvaluation(submissionId, evaluatorId, 5, 4, 3, 2, "Excellent work via Full API Test.");

            // Final Verification
            PreparedStatement verifyEval = conn
                    .prepareStatement("SELECT * FROM evaluations WHERE submission_id = ?::uuid");
            verifyEval.setString(1, submissionId);
            ResultSet rsEval = verifyEval.executeQuery();
            if (rsEval.next()) {
                int score = rsEval.getInt("score");
                if (score == 14) {
                    System.out.println("   [SUCCESS] Evaluation persisted with correct score (14).");
                } else {
                    System.out.println("   [FAILURE] Score mismatch: " + score);
                }
            } else {
                System.out.println("   [FAILURE] Evaluation not found in DB.");
            }

            System.out.println("\n--- Full System API Verification Completed ---");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
