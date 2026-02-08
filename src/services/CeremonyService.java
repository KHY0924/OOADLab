package services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Award;
import models.Ceremony;
import models.Evaluation;
import models.Submission;

public class CeremonyService {
    private List<Ceremony> ceremonies;
    private AwardComputationService awardComputationService;

    public CeremonyService() {
        this.ceremonies = new ArrayList<>();
        this.awardComputationService = new AwardComputationService();
    }

    public Ceremony createCeremony(String seminarId, String ceremonyName, 
                                   LocalDateTime scheduledDateTime, String venue) {
        String ceremonyId = "CRM_" + System.currentTimeMillis();
        Ceremony ceremony = new Ceremony(ceremonyId, seminarId, ceremonyName, 
                                        scheduledDateTime, venue);
        ceremonies.add(ceremony);
        return ceremony;
    }

    public Ceremony getCeremonyById(String ceremonyId) {
        return ceremonies.stream()
                .filter(c -> c.getCeremonyId().equals(ceremonyId))
                .findFirst()
                .orElse(null);
    }

    public List<Ceremony> getCeremoniesBySeminar(String seminarId) {
        List<Ceremony> seminarCeremonies = new ArrayList<>();
        for (Ceremony ceremony : ceremonies) {
            if (ceremony.getSeminarId().equals(seminarId)) {
                seminarCeremonies.add(ceremony);
            }
        }
        return seminarCeremonies;
    }

    public List<Ceremony> getCeremoniesByStatus(String status) {
        List<Ceremony> statusCeremonies = new ArrayList<>();
        for (Ceremony ceremony : ceremonies) {
            if (ceremony.getCeremonyStatus().equals(status)) {
                statusCeremonies.add(ceremony);
            }
        }
        return statusCeremonies;
    }

    public boolean assignAwardsToCeremony(Ceremony ceremony,
                                          List<Submission> oralSubmissions,
                                          List<Submission> posterSubmissions,
                                          List<Evaluation> evaluations,
                                          Map<String, Integer> peopleVotes) {
        if (!ceremony.canAddAwards()) {
            return false;
        }
        Award bestOral = awardComputationService.computeBestOralPresentation(
                oralSubmissions, evaluations);
        Award bestPoster = awardComputationService.computeBestPosterPresentation(
                posterSubmissions, evaluations);
        Award peoplesChoice = awardComputationService.computePeoplesChoice(
                getMergedSubmissions(oralSubmissions, posterSubmissions), peopleVotes);
        boolean allAssigned = true;
        if (bestOral != null) {
            allAssigned &= ceremony.addAward(bestOral);
        }
        if (bestPoster != null) {
            allAssigned &= ceremony.addAward(bestPoster);
        }
        if (peoplesChoice != null) {
            allAssigned &= ceremony.addAward(peoplesChoice);
        }
        return allAssigned;
    }


    public boolean addAwardToCeremony(String ceremonyId, Award award) {
        Ceremony ceremony = getCeremonyById(ceremonyId);
        if (ceremony != null && ceremony.canAddAwards()) {
            return ceremony.addAward(award);
        }
        return false;
    }

    public boolean removeAwardFromCeremony(String ceremonyId, String awardId) {
        Ceremony ceremony = getCeremonyById(ceremonyId);
        if (ceremony != null && ceremony.canAddAwards()) {
            return ceremony.removeAward(awardId);
        }
        return false;
    }


    public boolean startCeremony(String ceremonyId) {
        Ceremony ceremony = getCeremonyById(ceremonyId);
        if (ceremony != null && ceremony.getAwardCount() > 0) {
            ceremony.startCeremony();
            return true;
        }
        return false;
    }


    public boolean completeCeremony(String ceremonyId) {
        Ceremony ceremony = getCeremonyById(ceremonyId);
        if (ceremony != null) {
            ceremony.completeCeremony();
            return true;
        }
        return false;
    }


    public boolean cancelCeremony(String ceremonyId) {
        Ceremony ceremony = getCeremonyById(ceremonyId);
        if (ceremony != null) {
            ceremony.cancelCeremony();
            return true;
        }
        return false;
    }

    public List<Ceremony> getAllCeremonies() {
        return new ArrayList<>(ceremonies);
    }

    private List<Submission> getMergedSubmissions(List<Submission> oralSubmissions,
                                                   List<Submission> posterSubmissions) {
        List<Submission> merged = new ArrayList<>();
        if (oralSubmissions != null) {
            merged.addAll(oralSubmissions);
        }
        if (posterSubmissions != null) {
            merged.addAll(posterSubmissions);
        }
        return merged;
    }

    public boolean validateCeremonySetup(String ceremonyId) {
        Ceremony ceremony = getCeremonyById(ceremonyId);
        return ceremony != null && 
               ceremony.getAwardCount() > 0 &&
               ceremony.getScheduledDateTime() != null &&
               ceremony.getVenue() != null;
    }
}