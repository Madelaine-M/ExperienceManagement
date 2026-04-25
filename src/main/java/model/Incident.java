package model;

import model.enums.IncidentStatus;
import model.enums.IncidentType;

import java.time.LocalDateTime;

public abstract class Incident {
    private int id;
    private int customerId;
    private String description;
    private double priorityScore = 0;
    private double scoreImpact = 0;
    private int revenueRisk = 0;
    private IncidentStatus status = IncidentStatus.OPEN;
    private Integer assignedAdvisorId;
    private LocalDateTime createdAt;
    private Integer flightId;
    private ActionItem suggestedAction;

    protected Incident() {
    }

    protected Incident(int id, int customerId, String description, double priorityScore, double scoreImpact,
                       int revenueRisk, IncidentStatus status, Integer assignedAdvisorId, LocalDateTime createdAt,
                       Integer flightId, ActionItem suggestedAction) {
        this.id = id;
        this.customerId = customerId;
        this.description = description;
        this.priorityScore = priorityScore;
        this.scoreImpact = scoreImpact;
        this.revenueRisk = revenueRisk;
        this.status = status;
        this.assignedAdvisorId = assignedAdvisorId;
        this.createdAt = createdAt;
        this.flightId = flightId;
        this.suggestedAction = suggestedAction;
    }

    public abstract IncidentType getType();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(double priorityScore) {
        this.priorityScore = priorityScore;
    }

    public double getScoreImpact() {
        return scoreImpact;
    }

    public void setScoreImpact(double scoreImpact) {
        this.scoreImpact = scoreImpact;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public Integer getAssignedAdvisorId() {
        return assignedAdvisorId;
    }

    public void setAssignedAdvisorId(Integer assignedAdvisorId) {
        this.assignedAdvisorId = assignedAdvisorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getRevenueRisk() {
        return revenueRisk;
    }

    public void setRevenueRisk(int revenueRisk) {
        this.revenueRisk = revenueRisk;
    }

    public Integer getFlightId() {
        return flightId;
    }

    public void setFlightId(Integer flightId) {
        this.flightId = flightId;
    }

    public ActionItem getSuggestedAction() {
        return suggestedAction;
    }

    public void setSuggestedAction(ActionItem suggestedAction) {
        this.suggestedAction = suggestedAction;
    }
}
