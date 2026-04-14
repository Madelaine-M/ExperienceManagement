package model;

import model.enums.FeedbackCategory;
import model.enums.IncidentStatus;
import model.enums.IncidentType;
import java.time.LocalDateTime;

public class Incident {
    private int id;
    private int customerId;
    private IncidentType type;
    private FeedbackCategory feedbackType;  // noch in DB ergänzen
    private String description;
    private double priorityScore;
    private double scoreImpact;
    private int revenueRisk; //noch in DB einfügen
    private IncidentStatus status;
    private Integer assignedAdvisorId;
    private Integer sourceFeedbackItemId;
    private LocalDateTime createdAt;
    private int flightId;
    private ActionItem suggestedAction;

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

    public IncidentType getType() {
        return type;
    }

    public void setType(IncidentType type) {
        this.type = type;
    }

    public FeedbackCategory getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(FeedbackCategory feedbackType) {
        this.feedbackType = feedbackType;
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

    public Integer getSourceFeedbackItemId() {
        return sourceFeedbackItemId;
    }

    public void setSourceFeedbackItemId(Integer sourceFeedbackItemId) {
        this.sourceFeedbackItemId = sourceFeedbackItemId;
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

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public ActionItem getSuggestedAction() {
        return suggestedAction;
    }

    public void setSuggestedAction(ActionItem suggestedAction) {
        this.suggestedAction = suggestedAction;
    }

    public Incident() {}
}
