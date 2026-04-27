package service.implementation.suggestion.flight;


import model.ActionItem;
import model.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class FlightFeedbackVipSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSugegstion1("Exclusive Solo EVA in the next flight");
        actionItem.setSuggestion2("Cabin upgrade for the next flight with champagne reception");
    }
}
