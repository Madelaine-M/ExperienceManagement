package service.implementation.suggestion.delay;

import model.domain.ActionItem;
import model.domain.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class DelayVipSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSuggestion1("Gift a Rolex");
        actionItem.setSuggestion2("Superbowl Ticket");
    }
}
