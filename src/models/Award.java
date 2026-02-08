package models;

import java.time.LocalDateTime;

public class Award {
    private String awardId;
    private String awardType;
    private String studentId;
    private String studentName;
    private String submissionTitle;
    private String seminarId;
    private LocalDateTime awardDate;
    private String remarks;

    public Award(String awardId, String awardType, String studentId, String studentName,
                 String submissionTitle, String seminarId) {
        this.awardId = awardId;
        this.awardType = awardType;
        this.studentId = studentId;
        this.studentName = studentName;
        this.submissionTitle = submissionTitle;
        this.seminarId = seminarId;
        this.awardDate = LocalDateTime.now();
    }

    public String getAwardId() {
        return awardId;
    }

    public String getAwardType() {
        return awardType;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getSubmissionTitle() {
        return submissionTitle;
    }

    public String getSeminarId() {
        return seminarId;
    }

    public LocalDateTime getAwardDate() {
        return awardDate;
    }

    public String getRemarks() {
        return remarks;
    }

     
    public void setAwardDate(LocalDateTime awardDate) {
        this.awardDate = awardDate;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Award{" +
                "awardId='" + awardId + '\'' +
                ", awardType='" + awardType + '\'' +
                ", studentName='" + studentName + '\'' +
                ", submissionTitle='" + submissionTitle + '\'' +
                '}';
    }
}