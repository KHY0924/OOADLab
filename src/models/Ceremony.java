package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

 
public class Ceremony {
    private String ceremonyId;
    private String seminarId;
    private String ceremonyName;
    private LocalDateTime scheduledDateTime;
    private String venue;
    private String ceremonyStatus;
    private List<Award> awards;
    private LocalDateTime createdDateTime;
    private String remarks;

    public Ceremony(String ceremonyId, String seminarId, String ceremonyName, 
                    LocalDateTime scheduledDateTime, String venue) {
        this.ceremonyId = ceremonyId;
        this.seminarId = seminarId;
        this.ceremonyName = ceremonyName;
        this.scheduledDateTime = scheduledDateTime;
        this.venue = venue;
        this.ceremonyStatus = "PLANNED";
        this.awards = new ArrayList<>();
        this.createdDateTime = LocalDateTime.now();
    }

    public boolean addAward(Award award) {
        if (award == null) {
            return false;
        }
        boolean awardTypeExists = awards.stream()
                .anyMatch(a -> a.getAwardType().equals(award.getAwardType()));
        if (!awardTypeExists) {
            awards.add(award);
            return true;
        }
        return false;
    }

    public boolean removeAward(String awardId) {
        return awards.removeIf(award -> award.getAwardId().equals(awardId));
    }

    public Award getAwardById(String awardId) {
        return awards.stream()
                .filter(award -> award.getAwardId().equals(awardId))
                .findFirst()
                .orElse(null);
    }

    public Award getAwardByType(String awardType) {
        return awards.stream()
                .filter(award -> award.getAwardType().equals(awardType))
                .findFirst()
                .orElse(null);
    }

    public List<Award> getAllAwards() {
        return new ArrayList<>(awards);
    }

    public int getAwardCount() {
        return awards.size();
    }

    public void startCeremony() {
        if ("PLANNED".equals(ceremonyStatus)) {
            this.ceremonyStatus = "IN_PROGRESS";
        }
    }

    public void completeCeremony() {
        if ("IN_PROGRESS".equals(ceremonyStatus)) {
            this.ceremonyStatus = "COMPLETED";
        }
    }

    public void cancelCeremony() {
        this.ceremonyStatus = "CANCELLED";
    }

    public boolean canAddAwards() {
        return "PLANNED".equals(ceremonyStatus);
    }

     
    public String getCeremonyId() {
        return ceremonyId;
    }

    public String getSeminarId() {
        return seminarId;
    }

    public String getCeremonyName() {
        return ceremonyName;
    }

    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }

    public String getVenue() {
        return venue;
    }

    public String getCeremonyStatus() {
        return ceremonyStatus;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        if ("PLANNED".equals(ceremonyStatus)) {
            this.scheduledDateTime = scheduledDateTime;
        }
    }

    public void setVenue(String venue) {
        if ("PLANNED".equals(ceremonyStatus)) {
            this.venue = venue;
        }
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override  
    public String toString() {
        return "Ceremony{" +
                "ceremonyId='" + ceremonyId + '\'' +
                ", ceremonyName='" + ceremonyName + '\'' +
                ", scheduledDateTime=" + scheduledDateTime +
                ", venue='" + venue + '\'' +
                ", status='" + ceremonyStatus + '\'' +
                ", awardCount=" + awards.size() +
                '}';
    }
}