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
    private double priorityScore = 0;
    private double scoreImpact = 0;
    private int revenueRisk = 0; //noch in DB einfügen
    private IncidentStatus status = IncidentStatus.OPEN;
    private Integer assignedAdvisorId;
    private Integer sourceFeedbackItemId;
    private LocalDateTime createdAt;
    private int flightId;
    private ActionItem suggestedAction;

    public Incident() {}

    public Incident(int id, int customerId, IncidentType type, FeedbackCategory feedbackType, String description,
                    Integer sourceFeedbackItemId, LocalDateTime createdAt, int flightId) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.feedbackType = feedbackType;
        this.description = description;
        this.sourceFeedbackItemId = sourceFeedbackItemId;
        this.createdAt = createdAt;
        this.flightId = flightId;
    }

    public Incident(int id, int customerId, IncidentType type, FeedbackCategory feedbackType, String description,
                    double priorityScore, double scoreImpact, int revenueRisk, IncidentStatus status,
                    Integer assignedAdvisorId, Integer sourceFeedbackItemId, LocalDateTime createdAt, int flightId,
                    ActionItem suggestedAction) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.feedbackType = feedbackType;
        this.description = description;
        this.priorityScore = priorityScore;
        this.scoreImpact = scoreImpact;
        this.revenueRisk = revenueRisk;
        this.status = status;
        this.assignedAdvisorId = assignedAdvisorId;
        this.sourceFeedbackItemId = sourceFeedbackItemId;
        this.createdAt = createdAt;
        this.flightId = flightId;
        this.suggestedAction = suggestedAction;
    }

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
}
