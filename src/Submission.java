package backend;

public class Submission {
    private String submissionId; // Changed to String to match UUID
    private String title;
    // You can add more fields (abstract, supervisor) if you need them later

    public Submission(String submissionId, String title) {
        this.submissionId = submissionId;
        this.title = title;
    }

    public String getSubmissionId() { return submissionId; }
    public String getTitle() { return title; }
    
    // toString is useful for displaying in Swing Lists (comboboxes)
    @Override
    public String toString() {
        return title; 
    }
}