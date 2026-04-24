package model;

import model.enums.IncidentStatus;
import model.enums.IncidentType;

import java.time.LocalDateTime;

public class DelayIncident extends Incident {
    private Integer delayMinutes;

    public DelayIncident() {
    }

    public DelayIncident(int id, int customerId, String description, double priorityScore, double scoreImpact,
                         int revenueRisk, IncidentStatus status, Integer assignedAdvisorId, Integer delayMinutes,
                         LocalDateTime createdAt, Integer flightId, ActionItem suggestedAction) {
        super(id, customerId, description, priorityScore, scoreImpact, revenueRisk, status, assignedAdvisorId,
                createdAt, flightId, suggestedAction);
        this.delayMinutes = delayMinutes;
    }

    @Override
    public IncidentType getType() {
        return IncidentType.DELAY;
    }

    public Integer getDelayMinutes() {
        return delayMinutes;
    }

    public void setDelayMinutes(Integer delayMinutes) {
        this.delayMinutes = delayMinutes;
    }
}
