package test;

import database.DatabaseConnection;
import controllers.UserController;
import controllers.SubmissionController;
import controllers.EvaluationController;
import controllers.SessionController;
import models.Submission;

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
            SessionController sessionController = new SessionController();

            long timestamp = System.currentTimeMillis();
            String studentUsername = "stud_" + timestamp;
            String evalUsername = "eval_" + timestamp;
            String studentId = null;
            String evaluatorId = UUID.randomUUID().toString();

            System.out.println("\n1. Testing User Registration (API)...");

            userController.registerUser(studentUsername, "password123", "Test Student", "test@uni.edu", "CS");

            PreparedStatement getStu = conn.prepareStatement("SELECT user_id FROM users WHERE username = ?");
            getStu.setString(1, studentUsername);
            ResultSet rsStu = getStu.executeQuery();
            if (rsStu.next()) {
                studentId = rsStu.getString("user_id");
                System.out.println("   [SUCCESS] Student registered via API. ID: " + studentId);
            } else {
                System.out.println("   [FAILURE] Student not found after registration API call.");
                return;
            }

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

            subController.registerForSeminar(seminarId, studentId, "Initial Title", "Initial Abstract",
                    "Dr. Supervisor", "Oral");

            String submissionId = null;

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

            subController.uploadPresentation("C:/slides/my_presentation.pdf", seminarId, studentId);

            subController.editSubmission(studentId, "Updated Title", "Updated Abstract");

            ResultSet rsCheck = getSub.executeQuery();
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
            sessionController.getSessionInformation(studentId);
            System.out.println("   [OK] Session API called without error.");

            try {
                PreparedStatement assignStmt = conn.prepareStatement(
                        "INSERT INTO evaluator_assignments (evaluator_id, submission_id) VALUES (?, ?::uuid)");
                assignStmt.setObject(1, UUID.fromString(evaluatorId));
                assignStmt.setString(2, submissionId);
                assignStmt.executeUpdate();
                System.out.println("   [OK] Assignment created manually.");
            } catch (SQLException e) {

            }

            final String targetSubmissionId = submissionId;
            List<Submission> assigned = evalController.getAssignedSubmissions(evaluatorId);
            if (assigned.stream().anyMatch(s -> s.getSubmissionId().equals(targetSubmissionId))) {
                System.out.println("   [SUCCESS] EvaluationController found the assignment.");
            } else {
                System.out.println("   [FAILURE] Assignment not returned by API.");
            }

            evalController.submitEvaluation(submissionId, evaluatorId, 5, 4, 3, 2, "Excellent work via Full API Test.");

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
