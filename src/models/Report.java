package models;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Report {
    private String reportId;
    private String reportType;
    private LocalDateTime generatedDateTime;
    private String content;

    public Report(String reportType) {
        this.reportId = java.util.UUID.randomUUID().toString();
        this.reportType = reportType;
        this.generatedDateTime = LocalDateTime.now();
    }

    public static class StudentEvaluationReport extends Report {
        private String studentId;
        private String studentName;
        private String sessionId;
        private String sessionName;
        private Map<String, Integer> rubricScores;
        private double finalAverage;
        private List<String> evaluatorComments;
        public StudentEvaluationReport(String studentId, String studentName, String sessionId,
                                     String sessionName, Map<String, Integer> rubricScores,
                                     double finalAverage, List<String> evaluatorComments) {
            super("STUDENT_EVALUATION");
            this.studentId = studentId;
            this.studentName = studentName;
            this.sessionId = sessionId;
            this.sessionName = sessionName;
            this.rubricScores = rubricScores;
            this.finalAverage = finalAverage;
            this.evaluatorComments = evaluatorComments;
            generateContent();
        }
        private void generateContent() {
            StringBuilder sb = new StringBuilder();
            sb.append("========================================\n");
            sb.append("STUDENT EVALUATION REPORT\n");
            sb.append("========================================\n\n");
            sb.append("Report ID: ").append(getReportId()).append("\n");
            sb.append("Generated: ").append(getGeneratedDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
            sb.append("STUDENT INFORMATION\n");
            sb.append("-------------------\n");
            sb.append("Name: ").append(studentName).append("\n");
            sb.append("Student ID: ").append(studentId).append("\n");
            sb.append("Session: ").append(sessionName).append(" (").append(sessionId).append(")\n\n");
            sb.append("EVALUATION SCORES\n");
            sb.append("-------------------\n");
            if (rubricScores != null && !rubricScores.isEmpty()) {
                for (Map.Entry<String, Integer> entry : rubricScores.entrySet()) {
                    sb.append(String.format("%-25s: %d/100\n", entry.getKey(), entry.getValue()));
                }
            }
            sb.append("\n");
            sb.append(String.format("%-25s: %.2f/100\n", "Final Average", finalAverage));
            sb.append("\nEVALUATOR COMMENTS\n");
            sb.append("-------------------\n");
            if (evaluatorComments != null && !evaluatorComments.isEmpty()) {
                for (int i = 0; i < evaluatorComments.size(); i++) {
                    sb.append("Comment ").append(i + 1).append(": ").append(evaluatorComments.get(i)).append("\n");
                }
            } else {
                sb.append("No comments provided.\n");
            }
            sb.append("\n========================================\n");
            setContent(sb.toString());
        }
        public String getStudentName() {
            return studentName;
        }
        public String getSessionName() {
            return sessionName;
        }
        public double getFinalAverage() {
            return finalAverage;
        }
    }

    public static class SeminarSummaryReport extends Report {
        private String seminarId;
        private String seminarTitle;
        private int totalStudents;
        private int totalSessions;
        private double overallAverageScore;
        private List<Award> awardWinners;
        public SeminarSummaryReport(String seminarId, String seminarTitle, int totalStudents,
                                   int totalSessions, double overallAverageScore,
                                   List<Award> awardWinners) {
            super("SEMINAR_SUMMARY");
            this.seminarId = seminarId;
            this.seminarTitle = seminarTitle;
            this.totalStudents = totalStudents;
            this.totalSessions = totalSessions;
            this.overallAverageScore = overallAverageScore;
            this.awardWinners = awardWinners;
            generateContent();
        }
        private void generateContent() {
            StringBuilder sb = new StringBuilder();
            sb.append("========================================\n");
            sb.append("OVERALL SEMINAR SUMMARY REPORT\n");
            sb.append("========================================\n\n");
            sb.append("Report ID: ").append(getReportId()).append("\n");
            sb.append("Generated: ").append(getGeneratedDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
            sb.append("SEMINAR INFORMATION\n");
            sb.append("-------------------\n");
            sb.append("Seminar Title: ").append(seminarTitle).append("\n");
            sb.append("Seminar ID: ").append(seminarId).append("\n");
            sb.append("Total Students: ").append(totalStudents).append("\n");
            sb.append("Total Sessions: ").append(totalSessions).append("\n");
            sb.append(String.format("Overall Average Score: %.2f/100\n", overallAverageScore));
            sb.append("\nAWARD WINNERS\n");
            sb.append("-------------------\n");
            if (awardWinners != null && !awardWinners.isEmpty()) {
                for (Award award : awardWinners) {
                    sb.append(award.getAwardType()).append("\n");
                    sb.append("  Winner: ").append(award.getStudentName()).append("\n");
                    sb.append("  Submission: ").append(award.getSubmissionTitle()).append("\n\n");
                }
            } else {
                sb.append("No awards assigned.\n");
            }
            sb.append("\n========================================\n");
            setContent(sb.toString());
        }
        public String getSeminarTitle() {
            return seminarTitle;
        }
        public int getTotalStudents() {
            return totalStudents;
        }
        public int getTotalSessions() {
            return totalSessions;
        }
        public double getOverallAverageScore() {
            return overallAverageScore;
        }
    }

    public void exportToFile() throws IOException {
        DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fileName = "logs/Report_" + reportType + "_" + 
                         generatedDateTime.format(fileNameFormatter) + ".txt";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
        }
    }

    public String getReportId() {
        return reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public LocalDateTime getGeneratedDateTime() {
        return generatedDateTime;
    }

    public String getContent() {
        return content;
    }

    protected void setContent(String content) {
        this.content = content;
    }
}