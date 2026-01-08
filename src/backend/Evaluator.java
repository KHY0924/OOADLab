public class Evaluator extends User {
    private String evaluatorId; // Changed from int to String for UUID
    private boolean isAssigned;

    public Evaluator(String evaluatorId, String name, String email, String password) {
        super(name, email, password, "Evaluator");
        this.evaluatorId = evaluatorId;
    }

    public String getEvaluatorId() { return evaluatorId; }
    public boolean isAssigned() { return isAssigned; }
    public void setAssigned(boolean assigned) { isAssigned = assigned; }
}