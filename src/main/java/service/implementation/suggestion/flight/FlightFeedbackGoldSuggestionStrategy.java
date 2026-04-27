package service.implementation.suggestion.flight;


import model.ActionItem;
import model.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class FlightFeedbackGoldSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSugegstion1("Cabin upgrade for the next flight with champagne reception");
        actionItem.setSuggestion2("Luxury In-Flight Amenity Set");
    }
}
