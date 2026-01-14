package models;

public class Evaluation {
    private String submissionId; // String for UUID
    private String evaluatorId; // String for UUID

    // Rubric Scores (Required by your Lab PDF)
    private int problemClarityScore;
    private int methodologyScore;
    private int resultsScore;
    private int presentationScore;

    private int totalScore;
    private String comments;

    public Evaluation(String id, String submissionId, String evaluatorId, int totalScore, String comments) {
        // Full constructor for retrieval/saving
        this.submissionId = submissionId;
        this.evaluatorId = evaluatorId;
        this.totalScore = totalScore;
        this.comments = comments;
    }

    public Evaluation(String submissionId, String evaluatorId) {
        this.submissionId = submissionId;
        this.evaluatorId = evaluatorId;
    }

    // Setters for the Rubric (Call this from your UI)
    public void setRubricScores(int problem, int method, int results, int presentation) {
        this.problemClarityScore = problem;
        this.methodologyScore = method;
        this.resultsScore = results;
        this.presentationScore = presentation;
        calculateTotal();
    }

    public void setProblemClarityScore(int score) {
        this.problemClarityScore = score;
        calculateTotal();
    }

    public void setMethodologyScore(int score) {
        this.methodologyScore = score;
        calculateTotal();
    }

    public void setResultsScore(int score) {
        this.resultsScore = score;
        calculateTotal();
    }

    public void setPresentationScore(int score) {
        this.presentationScore = score;
        calculateTotal();
    }

    private void calculateTotal() {
        this.totalScore = problemClarityScore + methodologyScore + resultsScore + presentationScore;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    // Getters
    public String getSubmissionId() {
        return submissionId;
    }

    public String getEvaluatorId() {
        return evaluatorId;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public String getComments() {
        return comments;
    }

    // New Getters for Rubrics
    public int getProblemClarityScore() {
        return problemClarityScore;
    }

    public int getMethodologyScore() {
        return methodologyScore;
    }

    public int getResultsScore() {
        return resultsScore;
    }

    public int getPresentationScore() {
        return presentationScore;
    }
}