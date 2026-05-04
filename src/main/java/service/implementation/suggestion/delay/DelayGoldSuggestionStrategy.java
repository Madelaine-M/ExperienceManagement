package service.implementation.suggestion.delay;

import model.domain.ActionItem;
import model.domain.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class DelayGoldSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSuggestion1("Superbowl Ticket");
        actionItem.setSuggestion2("Piece of a Space Rocket");
    }
}
