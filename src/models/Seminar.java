package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Seminar {
  private String seminarId;
  private LocalDateTime seminarDT;
  private String location;

  public String getSeminarId() {
    return seminarId;
  }
  
  public String getSeminarDate() {
    return seminarDT.date;
  }
  
  public LocalDateTime getSeminarTime() {
    return seminarDT.time;
  }

  public String getLocation() {
    return location;
  }

}
