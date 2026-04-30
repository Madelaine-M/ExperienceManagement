package service.implementation.suggestion.delay;

import model.domain.ActionItem;
import model.domain.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class DelayStandardSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSuggestion1("Lounge access");
        actionItem.setSuggestion2("Complimentary Lounge Access during next delay");
    }
}
