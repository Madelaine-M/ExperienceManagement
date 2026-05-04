package model.domain;

import model.enums.ActionStatus;

public class ActionItem {
    private int id;
    private int incidentId;
    private String description;
    private String suggestion1;
    private String suggestion2;
    private ActionStatus status = ActionStatus.SUGGESTED;
    private int scoreImpact = 0; //raus
    private int expectedRec = 0;
    private int expectedRebooking = 0;


    public ActionItem() {
    }

    public ActionItem(int incidentId, String description) {
        this.incidentId = incidentId;
        this.description = description;
    }

    public ActionItem(int incidentId, String description, String suggestion1, String suggestion2,
                      ActionStatus status, int scoreImpact, int expectedRec,
                      int expectedRebooking) {
        this.incidentId = incidentId;
        this.description = description;
        this.suggestion1 = suggestion1;
        this.suggestion2 = suggestion2;
        this.status = status;
        this.scoreImpact = scoreImpact;
        this.expectedRec = expectedRec;
        this.expectedRebooking = expectedRebooking;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(int incidentId) {
        this.incidentId = incidentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActionStatus getStatus() {
        return status;
    }

    public void setStatus(ActionStatus status) {
        this.status = status;
    }

    public String getSuggestion1() {
        return suggestion1;
    }

    public void setSuggestion1(String suggestion1) {
        this.suggestion1 = suggestion1;
    }

    public String getSuggestion2() {
        return suggestion2;
    }

    public void setSuggestion2(String suggestion2) {
        this.suggestion2 = suggestion2;
    }

    public int getScoreImpact() {
        return scoreImpact;
    }

    public void setScoreImpact(int scoreImpact) {
        this.scoreImpact = scoreImpact;
    }

    public int getExpectedRec() {
        return expectedRec;
    }

    public void setExpectedRec(int expectedRec) {
        this.expectedRec = expectedRec;
    }

    public int getExpectedRebooking() {
        return expectedRebooking;
    }

    public void setExpectedRebooking(int expectedRebooking) {
        this.expectedRebooking = expectedRebooking;
    }
}
