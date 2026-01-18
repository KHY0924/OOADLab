package models;

public class PosterPresentation {
    private int presentationId;
    private int boardId;
    private int submissionId;
    private String title;
    private String description;
    private int sessionId;
    private String status;

    public PosterPresentation(int presentationId, int boardId, int submissionId, 
                             String title, String description, int sessionId, String status) {
        this.presentationId = presentationId;
        this.boardId = boardId;
        this.submissionId = submissionId;
        this.title = title;
        this.description = description;
        this.sessionId = sessionId;
        this.status = status;
    }

    public int getPresentationId() { 
        return presentationId; 
    }

    public void setPresentationId(int presentationId) { 
        this.presentationId = presentationId; 
    }

    public int getBoardId() { 
        return boardId; 
    }

    public void setBoardId(int boardId) { 
        this.boardId = boardId; 
    }

    public int getSubmissionId() { 
        return submissionId; 
    }

    public void setSubmissionId(int submissionId) { 
        this.submissionId = submissionId; 
    }

    public String getTitle() { 
        return title; 
    }

    public void setTitle(String title) { 
        this.title = title; 
    }

    public String getDescription() { 
        return description; 
    }
    public void setDescription(String description) { 
        this.description = description; 
    }

    public int getSessionId() { 
        return sessionId; 
    }

    public void setSessionId(int sessionId) { 
        this.sessionId = sessionId; 
    }

    public String getStatus() { 
        return status; 
    }

    public void setStatus(String status) { 
        this.status = status; 
    }

}