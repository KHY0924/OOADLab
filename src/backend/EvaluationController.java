import java.util.List;

public class EvaluationController {
    
    private AssignmentRepository assignmentRepo;
    private EvaluationRepository evaluationRepo;

    public EvaluationController() {
        // Initialize the repositories
        this.assignmentRepo = new AssignmentRepository();
        this.evaluationRepo = new EvaluationRepository();
    }

    // 1. Get list of students assigned to this evaluator
    public List<Submission> getAssignedSubmissions(String evaluatorId) {
        return assignmentRepo.getAssignmentsForEvaluator(evaluatorId);
    }

    // 2. Submit the grades
    public void submitEvaluation(String submissionId, String evaluatorId, 
                                 int score1, int score2, int score3, int score4, 
                                 String comments) {
        
        // Create the Evaluation object
        Evaluation eval = new Evaluation(submissionId, evaluatorId);
        
        // Set the specific rubric scores (Logic from PDF)
        eval.setRubricScores(score1, score2, score3, score4);
        eval.setComments(comments);
        
        // Save to Database (Repository will take the Total Score and save it)
        evaluationRepo.saveEvaluation(eval);
        
        System.out.println("Controller: Evaluation submitted for " + submissionId);
    }
}