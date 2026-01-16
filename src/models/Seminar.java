package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Seminar {
  private String seminarId;
  private DateAndTime seminarDT;
  private String location;

  public String getSeminarId() {
    return seminarId;
  }
  
  public LocalDate getSeminarDate() {
    return seminarDT.dateInput;
  }
  
  public LocalTime getSeminarTime() {
    return seminarDT.timeInput;
  }

  public String getLocation() {
    return location;
  }

}
