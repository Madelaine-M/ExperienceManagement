package service.implementation.suggestion.flight;

import model.ActionItem;
import model.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class FlightFeedbackStandardSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSugegstion1("Luxury In-Flight Amenity Set");
        actionItem.setSuggestion2("Priority Boarding on next flight");
    }
}
