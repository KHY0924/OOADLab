package controllers;

import models.Evaluation;
import database.AssignmentDAO;
import database.EvaluationDAO;
import models.Submission;

import java.util.List;

public class EvaluationController {

    private AssignmentDAO assignmentRepo;
    private EvaluationDAO evaluationRepo;

    public EvaluationController() {

        this.assignmentRepo = new AssignmentDAO();
        this.evaluationRepo = new EvaluationDAO();
    }

    public List<Submission> getAssignedSubmissions(String evaluatorId) {
        return assignmentRepo.getAssignmentsForEvaluator(evaluatorId);
    }

    public void submitEvaluation(String submissionId, String evaluatorId,
            int score1, int score2, int score3, int score4,
            String comments) {

        Evaluation eval = new Evaluation(submissionId, evaluatorId);

        eval.setRubricScores(score1, score2, score3, score4);
        eval.setComments(comments);

        evaluationRepo.saveEvaluation(eval);

        System.out.println("Controller: Evaluation submitted for " + submissionId);
    }

    public void viewEvaluation(String sessionID) {

    }

    public void generateAward(String submissionID) {
        highestOralMarks(submissionID);
        highestPosterMarks(submissionID);
        highestOverallMarks(submissionID);
    }
}
