package model;

import model.enums.FeedbackCategory;
import model.enums.IncidentStatus;
import model.enums.IncidentType;

import java.time.LocalDateTime;

public class FeedbackIncident extends Incident {
    private Integer feedbackId;
    private FeedbackCategory feedbackType;
    private Integer sourceFeedbackItemId;

    public FeedbackIncident() {
    }

    public FeedbackIncident(int id, int customerId, String description, double priorityScore, double scoreImpact,
                            int revenueRisk, IncidentStatus status, Integer assignedAdvisorId,
                            Integer feedbackId, FeedbackCategory feedbackType, Integer sourceFeedbackItemId,
                            LocalDateTime createdAt, Integer flightId, ActionItem suggestedAction) {
        super(id, customerId, description, priorityScore, scoreImpact, revenueRisk, status, assignedAdvisorId,
                createdAt, flightId, suggestedAction);
        this.feedbackId = feedbackId;
        this.feedbackType = feedbackType;
        this.sourceFeedbackItemId = sourceFeedbackItemId;
    }

    @Override
    public IncidentType getType() {
        return IncidentType.FEEDBACK;
    }

    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }

    public FeedbackCategory getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(FeedbackCategory feedbackType) {
        this.feedbackType = feedbackType;
    }

    public Integer getSourceFeedbackItemId() {
        return sourceFeedbackItemId;
    }

    public void setSourceFeedbackItemId(Integer sourceFeedbackItemId) {
        this.sourceFeedbackItemId = sourceFeedbackItemId;
    }
}
