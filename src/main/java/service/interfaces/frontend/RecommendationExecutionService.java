package service.interfaces.frontend;

import model.workflow.RecommendationEmailDraft;

public interface RecommendationExecutionService {

    RecommendationEmailDraft prepareDraft(int actionId, int optionNumber);

    void sendRecommendation(int actionId, int optionNumber, String subject, String emailBody);
}
