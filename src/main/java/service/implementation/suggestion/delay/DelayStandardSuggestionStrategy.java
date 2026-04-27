package service.implementation.suggestion.delay;

import model.ActionItem;
import model.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class DelayStandardSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSugegstion1("Lounge access");
        actionItem.setSuggestion2("Complimentary Lounge Access during next delay");
    }
}
