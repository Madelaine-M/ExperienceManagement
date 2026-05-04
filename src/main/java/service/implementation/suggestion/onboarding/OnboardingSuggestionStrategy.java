package service.implementation.suggestion.onboarding;

import model.domain.ActionItem;
import model.domain.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class OnboardingSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSuggestion1("Offer additional onboarding support and invite the customer to ask questions");
        actionItem.setSuggestion2("Ask the onboarding team to review the customer and provide targeted support");
    }
}
