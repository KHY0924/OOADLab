package services;

import database.EvaluationCriteriaDAO;
import database.PosterPresentationDAO;
import database.PresentationBoardDAO;
import java.util.List;
import models.EvaluationCriteria;
import models.PosterPresentation;
import models.PresentationBoard;

public class PosterPresentationService {
    private PosterPresentationDAO presentationDAO;
    private PresentationBoardDAO boardDAO;
    private EvaluationCriteriaDAO criteriaDAO;

    public PosterPresentationService() {
        this.presentationDAO = new PosterPresentationDAO();
        this.boardDAO = new PresentationBoardDAO();
        this.criteriaDAO = new EvaluationCriteriaDAO();
    }

    public boolean addPresentation(PosterPresentation presentation) {
        if (presentationDAO.createPresentation(presentation)) {
            PresentationBoard board = boardDAO.getBoardById(presentation.getBoardId());
            if (board != null) {
                board.setCurrentPresentations(board.getCurrentPresentations() + 1);
                boardDAO.updateBoard(board);
            }
            return true;
        }
        return false;
    }

    public PosterPresentation getPresentationById(int presentationId) {
        return presentationDAO.getPresentationById(presentationId);
    }

    public List<PosterPresentation> getPresentationsByBoard(int boardId) {
        return presentationDAO.getPresentationsByBoardId(boardId);
    }

    public boolean isSubmissionAssigned(String submissionId) {
        return presentationDAO.isSubmissionAssigned(submissionId);
    }

    public boolean updatePresentation(PosterPresentation presentation) {
        return presentationDAO.updatePresentation(presentation);
    }

    public boolean deletePresentation(int presentationId) {
        return presentationDAO.deletePresentation(presentationId);
    }

    public boolean createBoard(PresentationBoard board) {
        return boardDAO.createBoard(board);
    }

    public PresentationBoard getBoardById(int boardId) {
        return boardDAO.getBoardById(boardId);
    }

    public List<PresentationBoard> getAllBoards() {
        return boardDAO.getAllBoards();
    }

    public boolean updateBoard(PresentationBoard board) {
        return boardDAO.updateBoard(board);
    }

    public boolean deleteBoard(int boardId) {
        return boardDAO.deleteBoard(boardId);
    }

    public boolean isBoardAssigned(int boardId) {
        return presentationDAO.getPresentationByBoardId(boardId) != null;
    }

    public String getStudentNameForBoard(int boardId) {
        return presentationDAO.getStudentNameForBoard(boardId);
    }

    public boolean addCriteria(EvaluationCriteria criteria) {
        return criteriaDAO.createCriteria(criteria);
    }

    public EvaluationCriteria getCriteriaById(int criteriaId) {
        return criteriaDAO.getCriteriaById(criteriaId);
    }

    public List<EvaluationCriteria> getCriteriaForPresentation(int presentationId) {
        return criteriaDAO.getCriteriaByPresentationId(presentationId);
    }

    public boolean updateCriteria(EvaluationCriteria criteria) {
        return criteriaDAO.updateCriteria(criteria);
    }

    public boolean deleteCriteria(int criteriaId) {
        return criteriaDAO.deleteCriteria(criteriaId);
    }

    public boolean assignPresentationToBoard(int presentationId, int boardId) {
        PosterPresentation presentation = presentationDAO.getPresentationById(presentationId);
        PresentationBoard board = boardDAO.getBoardById(boardId);

        if (presentation != null && board != null && !board.isFull()) {
            presentation.setBoardId(boardId);
            board.setCurrentPresentations(board.getCurrentPresentations() + 1);
            return presentationDAO.updatePresentation(presentation) && boardDAO.updateBoard(board);
        }
        return false;
    }
}