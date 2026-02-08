package models;

public class PresentationBoard {
    private int boardId;
    private String boardName;
    private String location;
    private int maxPresentations;
    private int currentPresentations;
    private String sessionId;
    private String presentationType;

    public PresentationBoard(int boardId, String boardName, String location, int maxPresentations, int currentPresentations, String sessionId, String presentationType) {
        this.boardId = boardId;
        this.boardName = boardName;
        this.location = location;
        this.maxPresentations = maxPresentations;
        this.currentPresentations = currentPresentations;
        this.sessionId = sessionId;
        this.presentationType = presentationType;
    }

    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMaxPresentations() {
        return maxPresentations;
    }

    public void setMaxPresentations(int maxPresentations) {
        this.maxPresentations = maxPresentations;
    }

    public int getCurrentPresentations() {
        return currentPresentations;
    }

    public void setCurrentPresentations(int currentPresentations) {
        this.currentPresentations = currentPresentations;
    }

    public boolean isFull() {
        return currentPresentations >= maxPresentations;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPresentationType() {
        return presentationType;
    }

    public void setPresentationType(String presentationType) {
        this.presentationType = presentationType;
    }
}
