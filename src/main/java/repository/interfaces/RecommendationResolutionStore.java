package repository.interfaces;

import model.domain.CustomerNote;

public interface RecommendationResolutionStore {
    void completeRecommendationResolution(int incidentId, CustomerNote note);
}
