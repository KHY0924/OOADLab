package controllers;

import java.util.List;
import models.EvaluationCriteria;
import models.PosterPresentation;
import models.PresentationBoard;
import services.PosterPresentationService;

public class PosterPresentationController {
    private PosterPresentationService service;

    public PosterPresentationController() {
        this.service = new PosterPresentationService();
    }

    public boolean createPresentation(int boardId, String submissionId, String title,
            String description, String sessionId, String status) {
        PosterPresentation presentation = new PosterPresentation(
                0, boardId, submissionId, title, description, sessionId, status);
        return service.addPresentation(presentation);
    }

    public PosterPresentation getPresentation(int presentationId) {
        return service.getPresentationById(presentationId);
    }

    public List<PosterPresentation> getBoardPresentations(int boardId) {
        return service.getPresentationsByBoard(boardId);
    }

    public List<PresentationBoard> getAllBoards() {
        return service.getAllBoards();
    }

    public boolean createBoard(String boardName, String location, int maxPresentations) {
        PresentationBoard board = new PresentationBoard(0, boardName, location, maxPresentations, 0, null, null);
        return service.createBoard(board);
    }

    public boolean addEvaluationCriteria(int presentationId, String criteriaName,
            String description, int maxScore, int weight) {
        EvaluationCriteria criteria = new EvaluationCriteria(
                0, presentationId, criteriaName, description, maxScore, weight);
        return service.addCriteria(criteria);
    }

    public List<EvaluationCriteria> getPresentationCriteria(int presentationId) {
        return service.getCriteriaForPresentation(presentationId);
    }

    public boolean assignPresentationToBoard(int presentationId, int boardId) {
        return service.assignPresentationToBoard(presentationId, boardId);
    }

    public boolean updatePresentation(int presentationId, String title, String description, String status) {
        PosterPresentation presentation = service.getPresentationById(presentationId);
        if (presentation != null) {
            presentation.setTitle(title);
            presentation.setDescription(description);
            presentation.setStatus(status);
            return service.updatePresentation(presentation);
        }
        return false;
    }

    public boolean deletePresentation(int presentationId) {
        return service.deletePresentation(presentationId);
    }

    public boolean deleteBoard(int boardId) {
        return service.deleteBoard(boardId);
    }

    public boolean updateEvaluationCriteria(int criteriaId, String criteriaName,
            String description, int maxScore, int weight) {
        EvaluationCriteria criteria = service.getCriteriaById(criteriaId);
        if (criteria != null) {
            criteria.setCriteriaName(criteriaName);
            criteria.setDescription(description);
            criteria.setMaxScore(maxScore);
            criteria.setWeight(weight);
            return service.updateCriteria(criteria);
        }
        return false;
    }
}