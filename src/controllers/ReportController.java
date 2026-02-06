package controllers;

import database.*;
import models.*;
import java.util.List;
import java.sql.*;

public class ReportController {

    public void generateSystemReport() {
        System.out.println("=== SYSTEM REPORT ===");
        generateUserReport();
        generateSubmissionReport();
        generateSessionReport();
        generateEvaluationReport();
        generateMaterialReport();
    }

    private void generateUserReport() {
        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.getAllUsers();
        System.out.println("--- Users ---");
        for (User user : users) {
            System.out.println(
                    "ID: " + user.getUserId() + ", Username: " + user.getUsername() + ", Role: " + user.getRole());
        }
        System.out.println();
    }

    private void generateSubmissionReport() {
        SubmissionDAO submissionDAO = new SubmissionDAO();
        List<Submission> submissions = submissionDAO.getAllSubmissionsList();
        System.out.println("--- Submissions ---");
        for (Submission sub : submissions) {
            System.out.println("ID: " + sub.getSubmissionId() + ", Student: " + sub.getStudentId() + ", Title: "
                    + sub.getTitle() + ", Seminar: " + sub.getSeminarId());
        }
        System.out.println();
    }

    private void generateSessionReport() {
        SessionDAO sessionDAO = new SessionDAO();
        List<Session> sessions = sessionDAO.getAllSessions();
        System.out.println("--- Sessions ---");
        for (Session sess : sessions) {
            System.out.println("ID: " + sess.getSessionID() + ", Location: " + sess.getLocation() + ", Type: "
                    + sess.getSessionType());
        }
        System.out.println();
    }

    private void generateEvaluationReport() {
        EvaluationDAO evaluationDAO = new EvaluationDAO();
        // Get all evaluations - need to add getAllEvaluations to EvaluationDAO
        // For now, print summary
        int totalEvaluations = getTotalEvaluations();
        System.out.println("--- Evaluations ---");
        System.out.println("Total Evaluations: " + totalEvaluations);
        System.out.println("(Detailed evaluations can be viewed per session)");
        System.out.println();
    }

    private void generateMaterialReport() {
        // Get all materials - need to query directly
        System.out.println("--- Materials ---");
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM materials")) {
            if (rs.next()) {
                System.out.println("Total Materials: " + rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    private int getTotalEvaluations() {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM evaluations")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
