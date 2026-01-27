package models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private String sessionID;
    private String sessionType;
    private String location;
    private DateAndTime sessionDT;
    private List<String> studentIDs;

    public Session(String sessionID, String location, DateAndTime sessionDT, String sessionType) {
        this.sessionID = sessionID;
        this.location = location;
        this.sessionDT = sessionDT;
        this.sessionType = sessionType;
        this.studentIDs = new ArrayList<>();
    }

    public void addStudent(String studentId) {
        this.studentIDs.add(studentId);
    }

    public static List<Session> findSessionsByStudent(String studentId) {
        return new ArrayList<>();
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getSessionType() {
        return sessionType;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getSessionDate() {
        return sessionDT.date;
    }

    public LocalTime getSessionTime() {
        return sessionDT.time;
    }

    public List<String> getStudentIDs() {
        return studentIDs;
    }

    @Override
    public String toString() {
        return "Session " + sessionID + " at " + location;
    }
    
}

