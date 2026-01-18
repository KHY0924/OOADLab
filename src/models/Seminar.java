package models;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class Seminar {
  private String seminarID;
  private DateAndTime seminarDT;
  private String location;
  private List<String> sessionIDs;

  public Seminar(String seminarID, String location, DateAndTime seminarDT) {
    this.seminarID = seminarID;
    this.location = location;
    this.seminarDT = seminarDT;
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

}
