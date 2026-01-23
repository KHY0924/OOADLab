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
        List<Evaluation> evaluations = evaluationRepo.getEvaluationsBySessionId(sessionID);
        System.out.println("Evaluations for Session: " + sessionID);
        System.out.println("-----------------------------------");
        for (Evaluation eval : evaluations) {
            System.out.println("Submission: " + eval.getSubmissionId());
            System.out.println("Evaluator: " + eval.getEvaluatorId());
            System.out.println("Total Score: " + eval.getTotalScore());
            System.out.println("Problem Clarity: " + eval.getProblemClarityScore());
            System.out.println("Methodology: " + eval.getMethodologyScore());
            System.out.println("Results: " + eval.getResultsScore());
            System.out.println("Presentation: " + eval.getPresentationScore());
            System.out.println("Comments: " + eval.getComments());
            System.out.println("Created At: " + eval.getCreatedAt());
            System.out.println("-----------------------------------");
        }
    }

    public void generateAward(String submissionID) {
        highestOralMarks();
        highestPosterMarks();
        highestOverallMarks();
    }

    private void highestOralMarks() {
        int maxScore = evaluationRepo.getHighestScoreForSessionType("oral");
        if (maxScore > 0) {
            String submissionId = evaluationRepo.getSubmissionWithHighestScoreForType("oral", maxScore);
            System.out.println("Highest Oral Marks: Submission " + submissionId + " with score " + maxScore);
        } else {
            System.out.println("No oral evaluations found.");
        }
    }

    private void highestPosterMarks() {
        int maxScore = evaluationRepo.getHighestScoreForSessionType("poster");
        if (maxScore > 0) {
            String submissionId = evaluationRepo.getSubmissionWithHighestScoreForType("poster", maxScore);
            System.out.println("Highest Poster Marks: Submission " + submissionId + " with score " + maxScore);
        } else {
            System.out.println("No poster evaluations found.");
        }
    }

    private void highestOverallMarks() {
        int maxScore = evaluationRepo.getHighestOverallScore();
        if (maxScore > 0) {
            String submissionId = evaluationRepo.getSubmissionWithHighestOverallScore(maxScore);
            System.out.println("Highest Overall Marks: Submission " + submissionId + " with score " + maxScore);
        } else {
            System.out.println("No evaluations found.");
        }
    }
}

