package models;

import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleItem {
    private String sessionID;
    private LocalDate date;
    private LocalTime time;
    private String venue;
    private String evaluatorID;
    private String studentID;

    public ScheduleItem(String sessionID, LocalDate date, LocalTime time, String venue, String evaluatorID, String studentID) {
        this.sessionID = sessionID;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.evaluatorID = evaluatorID;
        this.studentID = studentID;
    }

    // Getters
    public String getSessionID() { return sessionID; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public String getVenue() { return venue; }
    public String getEvaluatorID() { return evaluatorID; }
    public String getStudentID() { return studentID; }

    @Override
    public String toString() {
        return "Session: " + sessionID + ", Date: " + date + ", Time: " + time + ", Venue: " + venue + ", Evaluator: " + evaluatorID + ", Student: " + studentID;
    }
}

