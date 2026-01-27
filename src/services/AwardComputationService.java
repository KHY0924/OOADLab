package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Award;
import models.Evaluation;
import models.Submission;


public class AwardComputationService {
    private static final String BEST_ORAL = "Best Oral Presentation";
    private static final String BEST_POSTER = "Best Poster Presentation";
    private static final String PEOPLES_CHOICE = "People's Choice";
    private static final double MIN_SCORE_THRESHOLD = 0.0;


    public Award computeBestOralPresentation(List<Submission> oralSubmissions,
                                             List<Evaluation> evaluations) {
        if (!isValidInput(oralSubmissions, evaluations)) {
            return null;
        }

        Map<String, Double> submissionScores = calculateAverageScores(oralSubmissions, evaluations);
        
        String winningSubmissionId = submissionScores.entrySet().stream()
                .filter(entry -> entry.getValue() >= MIN_SCORE_THRESHOLD)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (winningSubmissionId == null) {
            return null;
        }

        Submission winningSubmission = findSubmissionById(oralSubmissions, winningSubmissionId);
        return winningSubmission != null ? createAward(BEST_ORAL, winningSubmission) : null;
    }


    public Award computeBestPosterPresentation(List<Submission> posterSubmissions,
                                               List<Evaluation> evaluations) {
        if (!isValidInput(posterSubmissions, evaluations)) {
            return null;
        }

        Map<String, Double> submissionScores = calculateAverageScores(posterSubmissions, evaluations);
        
        String winningSubmissionId = submissionScores.entrySet().stream()
                .filter(entry -> entry.getValue() >= MIN_SCORE_THRESHOLD)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (winningSubmissionId == null) {
            return null;
        }

        Submission winningSubmission = findSubmissionById(posterSubmissions, winningSubmissionId);
        return winningSubmission != null ? createAward(BEST_POSTER, winningSubmission) : null;
    }


    public Award computePeoplesChoice(List<Submission> allSubmissions,
                                      Map<String, Integer> peopleVotes) {
        if (allSubmissions == null || allSubmissions.isEmpty() || 
            peopleVotes == null || peopleVotes.isEmpty()) {
            return null;
        }

        String winningSubmissionId = peopleVotes.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (winningSubmissionId == null) {
            return null;
        }

        Submission winningSubmission = findSubmissionById(allSubmissions, winningSubmissionId);
        return winningSubmission != null ? createAward(PEOPLES_CHOICE, winningSubmission) : null;
    }


    private Map<String, Double> calculateAverageScores(List<Submission> submissions,
                                                       List<Evaluation> evaluations) {
        Map<String, List<Integer>> submissionEvaluations = new HashMap<>();

        for (Submission submission : submissions) {
            submissionEvaluations.put(submission.getId(), new ArrayList<>());
        }

        for (Evaluation evaluation : evaluations) {
            submissionEvaluations.computeIfPresent(evaluation.getSubmissionId(),
                    (key, scores) -> {
                        scores.add(evaluation.getScore());
                        return scores;
                    });
        }

        Map<String, Double> averageScores = new HashMap<>();
        for (Map.Entry<String, List<Integer>> entry : submissionEvaluations.entrySet()) {
            double average = entry.getValue().isEmpty() ? 0 :
                    entry.getValue().stream()
                            .mapToInt(Integer::intValue)
                            .average()
                            .orElse(0);
            averageScores.put(entry.getKey(), average);
        }

        return averageScores;
    }


    private Award createAward(String awardType, Submission submission) {
        String awardId = "AWD_" + System.currentTimeMillis();
        return new Award(awardId, awardType, submission.getStudentId(),
                submission.getStudentName(), submission.getTitle(), submission.getSeminarId());
    }


    private Submission findSubmissionById(List<Submission> submissions, String submissionId) {
        return submissions.stream()
                .filter(s -> s.getId().equals(submissionId))
                .findFirst()
                .orElse(null);
    }

    private boolean isValidInput(List<Submission> submissions, List<Evaluation> evaluations) {
        return submissions != null && !submissions.isEmpty() &&
               evaluations != null && !evaluations.isEmpty();
    }

    public boolean validateAwardComputation(List<Submission> oralSubmissions,
                                            List<Submission> posterSubmissions,
                                            List<Evaluation> evaluations) {
        return isValidInput(oralSubmissions, evaluations) &&
               isValidInput(posterSubmissions, evaluations);
    }
}