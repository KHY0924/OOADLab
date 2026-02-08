package models;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class Seminar {
    private String seminarID;
    private DateAndTime seminarDT;
    private String location;
    private int semester;
    private int year;
    private List<String> sessionIDs;

    public Seminar(String seminarID, String location, DateAndTime seminarDT, int semester, int year) {
        this.seminarID = seminarID;
        this.location = location;
        this.seminarDT = seminarDT;
        this.semester = semester;
        this.year = year;
        this.sessionIDs = new ArrayList<>();
    }

    public List<String> getSessionIDs() {
        return sessionIDs;
    }

    public void addSession(String sessionID) {
        this.sessionIDs.add(sessionID);
    }

    public String getSeminarId() {
        return seminarID;
    }

    public LocalDate getSeminarDate() {
        return seminarDT.date;
    }

    public LocalTime getSeminarTime() {
        return seminarDT.time;
    }

    public String getLocation() {
        return location;
    }

    public int getSemester() {
        return semester;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "Semester " + semester + " - " + year;
    }
}
