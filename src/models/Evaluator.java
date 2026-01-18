package models;

public class Evaluator extends User {
    private String evaluatorId;
    private boolean isAssigned;

    public Evaluator(String evaluatorId, String name, String email, String password) {
        super(evaluatorId, name, password, "Evaluator");
        this.evaluatorId = evaluatorId;
    }

    public String getEvaluatorId() {
        return evaluatorId;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }
}