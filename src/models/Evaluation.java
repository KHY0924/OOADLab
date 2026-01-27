package models;

public class Evaluation {
    private String evaluationId;
    private String submissionId;
    private String evaluatorId;
    private int score;
    private String presentationType; 
    private String comments;

    public Evaluation(String evaluationId, String submissionId, String evaluatorId,
                     int score, String presentationType) {
        this.evaluationId = evaluationId;
        this.submissionId = submissionId;
        this.evaluatorId = evaluatorId;
        this.score = score;
        this.presentationType = presentationType;
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

    public int getScore() {
        return score;
    }

    public String getPresentationType() {
        return presentationType;
    }

    public String getComments() {
        return comments;
    }

    // Setters
    public void setScore(int score) {
        if (score >= 1 && score <= 10) {
            this.score = score;
        }
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setProblemClarityScore(int score) {
        setScore(score);
    }

    public void setMethodologyScore(int score) {
        setScore(score);
    }

    public void setResultsScore(int score) {
        setScore(score);
    }

    public void setPresentationScore(int score) {
        setScore(score);
    }
}