package models;

import java.util.UUID;
import java.sql.Timestamp;

public class Evaluation {
    private String evaluationId;
    private String submissionId;
    private String evaluatorId;
    private int problemClarity;
    private int methodology;
    private int results;
    private int presentation;
    private int overallScore;
    private String comments;
    private Timestamp createdAt;

    public Evaluation(String evaluationId, String submissionId, String evaluatorId, int overallScore, String comments, int problemClarity, int methodology, int results, int presentation, Timestamp createdAt) {
        this.evaluationId = evaluationId;
        this.submissionId = submissionId;
        this.evaluatorId = evaluatorId;
        this.problemClarity = problemClarity;
        this.methodology = methodology;
        this.results = results;
        this.presentation = presentation;
        this.overallScore = overallScore;
        this.comments = comments;
        this.createdAt = createdAt;
    }


    public Evaluation(String evaluationId, String submissionId, String evaluatorId, int problemClarity, int methodology, int results, int presentation, int overallScore, String comments) {
        this(evaluationId, submissionId, evaluatorId, overallScore, comments, problemClarity, methodology, results, presentation, new Timestamp(System.currentTimeMillis()));
    }

    public Evaluation(String submissionId, String evaluatorId) {
        this(UUID.randomUUID().toString(), submissionId, evaluatorId, 0, 0, 0, 0, 0, "");
    }

    public String getEvaluationId() {
        return evaluationId;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public String getEvaluatorId() {
        return evaluatorId;
    }

    public int getProblemClarity() {
        return problemClarity;
    }

    public int getMethodology() {
        return methodology;
    }

    public int getResults() {
        return results;
    }

    public int getPresentation() {
        return presentation;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public String getComments() {
        return comments;
    }

    public void setProblemClarity(int s) {
        this.problemClarity = s;
    }

    public void setMethodology(int s) {
        this.methodology = s;
    }

    public void setResults(int s) {
        this.results = s;
    }

    public void setPresentation(int s) {
        this.presentation = s;
    }

    public void setOverallScore(int s) {
        this.overallScore = s;
    }

    public void setComments(String c) {
        this.comments = c;
    }


    public void setRubricScores(int s1, int s2, int s3, int s4) {
        this.problemClarity = s1;
        this.methodology = s2;
        this.results = s3;
        this.presentation = s4;
        this.overallScore = (int) ((s1 + s2 + s3 + s4) * 2.5);
    }


    public int getScore() {
        return overallScore;
    }

    public int getTotalScore() {
        return overallScore;
    }

    public int getProblemClarityScore() {
        return problemClarity;
    }

    public int getMethodologyScore() {
        return methodology;
    }

    public int getResultsScore() {
        return results;
    }

    public int getPresentationScore() {
        return presentation;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
