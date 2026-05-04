package model.domain;

import model.enums.IncidentStatus;
import model.enums.IncidentType;

import java.time.LocalDateTime;

public class OnboardingIncident extends Incident {
    public OnboardingIncident() {
    }

    public OnboardingIncident(int customerId, String description, int scoreImpact,
                              int revenueRisk, IncidentStatus status, Integer assignedAdvisorId,
                              LocalDateTime createdAt, Integer flightId, ActionItem suggestedAction) {
        super(customerId, description, scoreImpact, revenueRisk, status, assignedAdvisorId,
                createdAt, flightId, suggestedAction);
    }

    @Override
    public IncidentType getType() {
        return IncidentType.ONBOARDING;
    }
}
