package service.implementation.suggestion.delay;

import model.domain.ActionItem;
import model.domain.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class DelayShortOperationalSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSuggestion1("Prepare a quick lunch for the customer before departure");
        actionItem.setSuggestion2("Instruct pick-up driver to take the detour route");
    }
}
