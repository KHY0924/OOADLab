package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private String sessionId;
    private String location;
    private LocalDateTime date;
    private List<String> studentIds;

    public Session(String sessionId, String location, LocalDateTime date) {
        this.sessionId = sessionId;
        this.location = location;
        this.date = date;
        this.studentIds = new ArrayList<>();
    }

    public void addStudent(String studentId) {
        this.studentIds.add(studentId);
    }

    public static List<Session> findSessionsByStudent(String studentId) {
        return new ArrayList<>();
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<String> getStudentIds() {
        return studentIds;
    }

    @Override
    public String toString() {
        return "Session " + sessionId + " at " + location;
    }
}
