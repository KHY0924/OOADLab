package models;

import java.time.LocalDateTime;

public class Submission {
    private String submissionId;
    private String seminarId;
    private String studentId;
    private String studentName;  
    private String title;
    private String abstractText;
    private String supervisor;
    private String presentationType;
    private String filePath;
    private LocalDateTime deadline;

    public Submission(String submissionId, String seminarId, String studentId, String title, String abstractText,
            String supervisor, String presentationType) {
        this.submissionId = submissionId;
        this.seminarId = seminarId;
        this.studentId = studentId;
        this.title = title;
        this.abstractText = abstractText;
        this.supervisor = supervisor;
        this.presentationType = presentationType;
        this.deadline = LocalDateTime.now().plusDays(2);
    }

    public Submission(String submissionId, String title) {
        this.submissionId = submissionId;
        this.title = title;
    }

    public static Submission findByStudentId(String studentId) {
        return null;
    }

    public boolean validateUpload(String filePath) {
        return filePath != null && (filePath.endsWith(".pdf") || filePath.endsWith(".ppt"));
    }

    public void saveFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean checkDeadline() {
        return LocalDateTime.now().isBefore(this.deadline);
    }

    public void saveUpdatedSubmission(String title, String abstractText) {
        this.title = title;
        this.abstractText = abstractText;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public String getId() {
        return submissionId;
    }

    public String getSeminarId() {
        return seminarId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getTitle() {
        return title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public String getPresentationType() {
        return presentationType;
    }

    public String getFilePath() {
        return filePath;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public String getDetails() {
        return "Title: " + title + ", Abstract: " + abstractText;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "submissionId='" + submissionId + '\'' +
                ", title='" + title + '\'' +
                ", studentName='" + studentName + '\'' +
                ", presentationType='" + presentationType + '\'' +
                '}';
    }
}
