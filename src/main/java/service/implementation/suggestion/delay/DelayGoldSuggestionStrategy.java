package service.implementation.suggestion.delay;

import model.ActionItem;
import model.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class DelayGoldSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSugegstion1("Superbowl Ticket");
        actionItem.setSuggestion2("Lounge access");
    }
}
