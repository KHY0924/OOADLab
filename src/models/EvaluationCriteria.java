package models;

public class EvaluationCriteria {
    private int criteriaId;
    private int presentationId;
    private String criteriaName;
    private String description;
    private int maxScore;
    private int weight;

    public EvaluationCriteria(int criteriaId, int presentationId, String criteriaName, String description, int maxScore, int weight) {
        this.criteriaId = criteriaId;
        this.presentationId = presentationId;
        this.criteriaName = criteriaName;
        this.description = description;
        this.maxScore = maxScore;
        this.weight = weight;
    }


    public int getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(int criteriaId) {
        this.criteriaId = criteriaId;
    }

    public int getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(int presentationId) {
        this.presentationId = presentationId;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public void setCriteriaName(String criteriaName) {
        this.criteriaName = criteriaName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}
