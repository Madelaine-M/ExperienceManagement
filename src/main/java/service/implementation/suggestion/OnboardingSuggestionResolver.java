package service.implementation.suggestion;

import model.domain.Incident;
import model.enums.IncidentType;
import service.implementation.suggestion.onboarding.OnboardingSuggestionStrategy;
import service.interfaces.suggestions.SuggestionResolver;
import service.interfaces.suggestions.SuggestionStrategy;

public class OnboardingSuggestionResolver implements SuggestionResolver {
    private final SuggestionStrategy strategy = new OnboardingSuggestionStrategy();

    @Override
    public boolean supports(Incident incident) {
        return incident.getType() == IncidentType.ONBOARDING;
    }

    @Override
    public SuggestionStrategy resolve(Incident incident) {
        return strategy;
    }
}
