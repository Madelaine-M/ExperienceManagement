package service.implementation.suggestion.hotel;

import model.domain.ActionItem;
import model.domain.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class HotelVipSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSuggestion1("3 Day trip to Maldives after next flight");
        actionItem.setSuggestion2("Upgrade to presidential Suite for their next trip");
    }
}
