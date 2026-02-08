package controllers;

import database.SubmissionDAO;
import java.sql.SQLException;

public class SubmissionController {
    private SubmissionDAO submissionDAO = new SubmissionDAO();

    public void registerForSeminar(String seminarId, String studentId, String title, String abstractText,
            String supervisor, String type) {
        try {
            submissionDAO.createSubmission(seminarId, studentId, title, abstractText, supervisor, type);
            System.out.println("Submission registered for " + studentId);
        } catch (SQLException e) {
            System.out.println("Error registering for seminar.");
        }
    }

    public void uploadPresentation(String filePath, String seminarId, String studentId) {
        try {
            if (filePath == null || !filePath.endsWith(".pdf")) {
                System.out.println("Invalid file format.");
                return;
            }
            submissionDAO.saveFilePath(studentId, filePath);
            System.out.println("File uploaded: " + filePath);
        } catch (SQLException e) {
            System.out.println("Error uploading presentation.");
        }
    }

    public void editSubmission(String studentId, String newTitle, String newAbstract) {
        try {
            submissionDAO.updateSubmission(studentId, newTitle, newAbstract);
            System.out.println("Submission updated.");
        } catch (SQLException e) {
            System.out.println("Error updating submission.");
        }
    }
}
