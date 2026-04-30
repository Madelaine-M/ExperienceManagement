package model.domain;

import model.enums.ActionStatus;

public class ActionItem {
    private int id;
    private int incidentId;
    private String description;
    private String suggestion1;
    private String suggestion2;
    private ActionStatus status = ActionStatus.SUGGESTED;
    private double scoreImpact = 0; //raus
    private double expectedRec = 0;
    private double expectedRebooking = 0;


    public ActionItem() {
    }

    public ActionItem(int id, int incidentId, String description) {
        this.id = id;
        this.incidentId = incidentId;
        this.description = description;
    }

    public ActionItem(int id, int incidentId, String description, String suggestion1, String suggestion2,
                      ActionStatus status, double scoreImpact, double expectedRec,
                      double expectedRebooking) {
        this.id = id;
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

    public double getScoreImpact() {
        return scoreImpact;
    }

    public void setScoreImpact(double scoreImpact) {
        this.scoreImpact = scoreImpact;
    }

    public double getExpectedRec() {
        return expectedRec;
    }

    public void setExpectedRec(double expectedRec) {
        this.expectedRec = expectedRec;
    }

    public double getExpectedRebooking() {
        return expectedRebooking;
    }

    public void setExpectedRebooking(double expectedRebooking) {
        this.expectedRebooking = expectedRebooking;
    }
}
