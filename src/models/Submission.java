package models;

import java.time.LocalDateTime;

public class Submission {
    private String submissionId;
    private String seminarId;
    private String studentId;
    private String studentName; // Added for display purposes
    private String title;
    private String abstractText;
    private String supervisor;
    private String presentationType;
    private String filePath;
    private LocalDateTime deadline;

    public Submission(String sid, String semId, String stId, String titl, String abtxt,
            String sup,
            String pType) {
        this.sid = sid;
        this.semId = semId;
        this.stId = stId;
        this.titl = titl;
        this.abtxt = abtxt;
        this.sup = sup;
        this.pType = pType;
        this.dl = LocalDateTime.now().plusDays(2);
    }

    public Submission(String sid, String titl) {
        this.sid = sid;
        this.titl = titl;
    }

    public static Submission findByStudentId(String stId) {
        return null;
    }

    public boolean validateUpload(String fPath) {
        return fPath.endsWith(".pdf") || fPath.endsWith(".ppt");
    }

    public void saveFilePath(String fPath) {
        this.fPath = fPath;
    }

    public boolean checkDeadline() {
        return LocalDateTime.now().isBefore(this.dl);
    }

    public void saveUpdatedSubmission(String titl, String abtxt) {
        this.titl = titl;
        this.abtxt = abtxt;
    }

    public String getSubmissionId() {
        return sid;
    }

    public String getId() {
        return sid;
    }

    public String getSeminarId() {
        return semId;
    }

    public String getStudentId() {
        return stId;
    }

    public String getTitle() {
        return titl;
    }

    public String getAbstractText() {
        return abtxt;
    }

    public String getSupervisor() {
        return sup;
    }

    public String getPresentationType() {
        return pType;
    }

    public String getFilePath() {
        return fPath;
    }

    public LocalDateTime getDeadline() {
        return dl;
    }

    public String getDetails() {
        return "Title: " + titl + ", Abstract: " + abtxt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
