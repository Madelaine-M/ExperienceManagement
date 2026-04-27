package service.implementation.suggestion.delay;

import model.ActionItem;
import model.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class DelayVipSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSugegstion1("Gift a Rolex");
        actionItem.setSuggestion2("Superbowl Ticket");
    }
}
