package model;

import model.enums.ActionStatus;

public class ActionItem {
    private int id;
    private int incidentId;
    private String description;
    private String sugegstion1; //noch in DB einfügen
    private String suggestion2; //noch in DB einfügen
    private ActionStatus status;
    private int priority;
    private double scoreImpact; //noch in DB einfügen
    private double expectedRec; //noch in DB einfügen
    private double expectedRebooking; //noch in DB einfügen


    public ActionItem() {
    }

    public ActionItem(int incidentId, String description, ActionStatus status, int priority) {
        this.incidentId = incidentId;
        this.description = description;
        this.status = status;
        this.priority = priority;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getSugegstion1() {
        return sugegstion1;
    }

    public void setSugegstion1(String sugegstion1) {
        this.sugegstion1 = sugegstion1;
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
