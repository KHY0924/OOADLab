package controllers;

import models.Submission;

public class SubmissionController {

    public void registerForSeminar(String seminarId, String studentId, String title, String abstractText,
            String supervisor, String type) {
        Submission submission = new Submission(seminarId, studentId, title, abstractText, supervisor, type);
    }

    public void uploadPresentation(String filePath, String seminarId, String studentId) {
        Submission submission = Submission.findByStudentId(studentId);
        if (submission == null) {
            return;
        }

        boolean isValid = submission.validateUpload(filePath);

        if (isValid) {
            submission.saveFilePath(filePath);
        }
    }

    public void editSubmission(String studentId, String newTitle, String newAbstract) {
        Submission submission = Submission.findByStudentId(studentId);

        if (submission == null) {
            return;
        }

        boolean isBeforeDeadline = submission.checkDeadline();

        if (isBeforeDeadline) {
            submission.saveUpdatedSubmission(newTitle, newAbstract);
        }
    }
}
