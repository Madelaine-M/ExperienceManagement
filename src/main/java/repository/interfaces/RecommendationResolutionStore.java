package repository.interfaces;

import model.domain.CustomerNote;

public interface RecommendationResolutionStore {
    void completeRecommendationResolution(int incidentId, CustomerNote note);

    void saveRecommendationStep(int incidentId, CustomerNote note, boolean closeIncident);

    boolean hasRecommendationStep(int incidentId, int optionNumber);
}
