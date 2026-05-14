package service.implementation.recommendation;

import model.domain.Incident;
import model.enums.IncidentType;
import repository.interfaces.RecommendationResolutionStore;

//AI used
public class RecommendationCompletionPolicy {
    public boolean closesIncidentAfterSend(Incident incident,
                                           int optionNumber,
                                           RecommendationResolutionStore recommendationResolutionStore) {
        if (incident.getType() != IncidentType.ONBOARDING) {
            return true;
        }
        return recommendationResolutionStore.hasRecommendationStep(incident.getId(), otherOptionNumber(optionNumber));
    }

    public String sendButtonText(RecommendationMailPolicy mailPolicy, boolean closesIncident) {
        if (!closesIncident && mailPolicy.isInternalTeamMail()) {
            return "Send Team Mail";
        }
        if (!closesIncident) {
            return "Send Customer Mail";
        }
        return mailPolicy.isInternalTeamMail() ? "Send Team Mail and Close Incident" : "Send and Close Incident";
    }

    private int otherOptionNumber(int optionNumber) {
        return optionNumber == 1 ? 2 : 1;
    }
}
