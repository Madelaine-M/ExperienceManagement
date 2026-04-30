package service.implementation.suggestion.flight;


import model.domain.ActionItem;
import model.domain.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class FlightFeedbackGoldSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSuggestion1("Cabin upgrade for the next flight with champagne reception");
        actionItem.setSuggestion2("Luxury In-Flight Amenity Set");
    }
}
